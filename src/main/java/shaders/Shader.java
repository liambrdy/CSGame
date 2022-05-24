package shaders;

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

    public Shader(String vertexFile, String fragmentFile) {
        int vert = loadShader("src/main/resources/" + vertexFile, GL_VERTEX_SHADER);
        int frag = loadShader("src/main/resources/" + fragmentFile, GL_FRAGMENT_SHADER);

        programID = glCreateProgram();
        glAttachShader(programID, vert);
        glAttachShader(programID, frag);
        bindAttributes();
        glLinkProgram(programID);
        glValidateProgram(programID);

        if (glGetProgrami(programID, GL_LINK_STATUS) != GL_FALSE) {
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

    private static int loadShader(String file, int type) {
        StringBuilder shaderSource = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("//\n");
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.err.println("File \"" + file + "\" not found!");
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            System.err.println("Could not read file: \"" + file + "\"!" );
            e.printStackTrace();
            System.exit(-1);
        }
        
        int id = glCreateShader(type);
        glShaderSource(id, shaderSource);
        glCompileShader(id);
        if (glGetShaderi(id, GL_COMPILE_STATUS) != GL_FALSE) {
            System.out.println(glGetShaderInfoLog(id, 1024));
            System.out.println("Could not compile shader " + file);
            System.exit(-1);
        }

        return id;
    }
}
