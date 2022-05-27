package shaders;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Vector;

public class StaticShader extends Shader {
    private int projectionLocation;
    private int viewLocation;
    private int lightPosLocation;
    private int viewPosLocation;
    private int ambientColorLocation;
    private int diffuseColorLocation;
    private int specularColorLocation;
    private int shininessLocation;

    public StaticShader() {
        super("static");
    }

    @Override
    protected void getAllUniformLocations() {
        projectionLocation = super.getUniformLocation("u_Projection");
        viewLocation = super.getUniformLocation("u_View");
        lightPosLocation = super.getUniformLocation("u_LightPos");
        viewPosLocation = super.getUniformLocation("u_ViewPos");
        ambientColorLocation = super.getUniformLocation("u_Material.ambient");
        diffuseColorLocation = super.getUniformLocation("u_Material.diffuse");
        specularColorLocation = super.getUniformLocation("u_Material.specular");
        shininessLocation = super.getUniformLocation("u_Material.shininess");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "a_Position");
        super.bindAttribute(1, "a_uvCoord");
        super.bindAttribute(2, "a_Normal");
    }

    public void setProjection(Matrix4f proj) {
        super.setMatrix(projectionLocation, proj);
    }

    public void setView(Matrix4f view) {
        super.setMatrix(viewLocation, view);
    }
    public void setLightPos(Vector3f pos) {
        super.setFloat3(lightPosLocation, pos);
    }
    public void setViewPos(Vector3f pos) { super.setFloat3(viewPosLocation, pos); }
    public void setMaterial(Vector3f amb, Vector3f dif, Vector3f spec, float shi) {
        super.setFloat3(ambientColorLocation, amb);
        super.setFloat3(diffuseColorLocation, dif);
        super.setFloat3(specularColorLocation, spec);
        super.setFloat(shininessLocation, shi);
    }
}
