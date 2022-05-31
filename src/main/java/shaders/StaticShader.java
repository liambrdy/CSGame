package shaders;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import renderer.Light;

import java.util.List;
import java.util.Vector;

public class StaticShader extends Shader {
    private static final int MAX_LIGHTS = 4;

    private int projectionLocation;
    private int viewLocation;
    private int lightPosLocation;
    private int viewPosLocation;
    private int ambientColorLocation;
    private int diffuseColorLocation;
    private int specularColorLocation;
    private int shininessLocation;
    private int transformLocation;
    private int[] lightPosLocations;
    private int[] lightColorLocations;
    private int[] lightAttenuationLocations;

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
        transformLocation = super.getUniformLocation("u_Transform");

        lightPosLocations = new int[MAX_LIGHTS];
        lightColorLocations = new int[MAX_LIGHTS];
        lightAttenuationLocations = new int[MAX_LIGHTS];
        for (int i = 0; i < MAX_LIGHTS; i++) {
            lightPosLocations[i] = super.getUniformLocation("u_LightPositions[" + i + "]");
            lightColorLocations[i] = super.getUniformLocation("u_LightColors[" + i + "]");
            lightAttenuationLocations[i] = super.getUniformLocation("u_LightAttenuations[" + i + "]");
        }
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

    public void setTransform(Matrix4f transform) {
        super.setMatrix(transformLocation, transform);
    }

    public void setLights(List<Light> lights) {
        for (int i = 0; i < MAX_LIGHTS; i++) {
            if (i < lights.size()) {
                super.setFloat3(lightPosLocations[i], lights.get(i).getPosition());
                super.setFloat3(lightColorLocations[i], lights.get(i).getColor());
                super.setFloat3(lightAttenuationLocations[i], lights.get(i).getAttenuation());
            } else {
                super.setFloat3(lightPosLocations[i], new Vector3f(0.0f, 0.0f, 0.0f));
                super.setFloat3(lightColorLocations[i], new Vector3f(0.0f, 0.0f, 0.0f));
                super.setFloat3(lightAttenuationLocations[i], new Vector3f(1.0f, 0.0f, 0.0f));
            }
        }
    }
}
