package shaders;

import assets.AssetManager;
import assets.LoadedShader;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL21.*;

public abstract class Shader {
    private final int programID;

    private static final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    public Shader(String name) {
        LoadedShader s = AssetManager.getLoadedShader(name);
        String vertexSource = s.getShader(LoadedShader.LoadedShaderType.Vertex);
        String fragmentSource = s.getShader(LoadedShader.LoadedShaderType.Fragment);

        int vert = createShader(vertexSource, GL_VERTEX_SHADER);
        int frag = createShader(fragmentSource, GL_FRAGMENT_SHADER);

        programID = glCreateProgram();
        glAttachShader(programID, vert);
        glAttachShader(programID, frag);
        bindAttributes();
        glLinkProgram(programID);
        glValidateProgram(programID);

        if (glGetProgrami(programID, GL_LINK_STATUS) != GL_TRUE) {
            System.out.println(glGetProgramInfoLog(programID, 1024));
            System.out.println("Could not validate program");
            System.exit(-1);
        }

        glDetachShader(programID, vert);
        glDetachShader(programID, frag);
        glDeleteShader(vert);
        glDeleteShader(frag);

        getAllUniformLocations();
    }

    protected abstract void getAllUniformLocations();

    protected int getUniformLocation(String uniformName) {
        return glGetUniformLocation(programID, uniformName);
    }

    public void bind() {
        glUseProgram(programID);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        glDeleteProgram(programID);
    }

    protected abstract void bindAttributes();

    protected void bindAttribute(int attribute, String name) {
        glBindAttribLocation(programID, attribute, name);
    }

    protected void setFloat(int location, float val) {
        glUniform1f(location, val);
    }

    protected void setInt(int location, int val) {
        glUniform1i(location, val);
    }

    protected void setBoolean(int location, boolean bool) {
        if (bool)
            setInt(location, 1);
        else
            setInt(location, 0);
    }

    protected void setFloat2(int location, Vector2f vec) {
        glUniform2f(location, vec.x, vec.y);
    }

    protected void setFloat3(int location, Vector3f vec) {
        glUniform3f(location, vec.x, vec.y, vec.z);
    }

    protected void setFloat4(int location, Vector4f vec) {
        glUniform4f(location, vec.x, vec.y, vec.z, vec.w);
    }

    protected void setMatrix(int location, Matrix4f mat) {
        mat.get(matrixBuffer);
        glUniformMatrix4fv(location, false, matrixBuffer);
    }

    private static int createShader(String source, int type) {
        int id = glCreateShader(type);
        glShaderSource(id, source);
        glCompileShader(id);
        if (glGetShaderi(id, GL_COMPILE_STATUS) != GL_TRUE) {
            System.out.println(glGetShaderInfoLog(id, 1024));
            System.out.println("Could not compile shader");
            System.exit(-1);
        }

        return id;
    }
}
