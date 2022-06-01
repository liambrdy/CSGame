package renderer;

import org.joml.Vector3f;
import shaders.StaticShader;

public class Material {
    public static final Vector3f DEFAULT_AMBIENT = new Vector3f(0.5f, 0.5f, 0.5f);
    public static final Vector3f DEFAULT_DIFFUSE = new Vector3f(1.0f, 1.0f, 1.0f);
    public static final Vector3f DEFAULT_SPECULAR = new Vector3f(0.5f, 0.5f, 0.5f);
    public static final float DEFAULT_SHININESS = 32.0f;
    private Vector3f ambient, diffuse, specular;
    private float shininess;
    private Texture texture;
    private String textureName;

    public Material(Vector3f am, Vector3f df, Vector3f sp, float sh, String texName) {
        ambient = am;
        diffuse = df;
        specular = sp;
        shininess = sh;
        textureName = texName;
    }

    public void setUniforms(StaticShader shader) {
        shader.setMaterial(ambient, diffuse, specular, shininess);
    }

    public Vector3f getAmbient() {
        return ambient;
    }

    public void setAmbient(Vector3f ambient) {
        this.ambient = ambient;
    }

    public Vector3f getDiffuse() {
        return diffuse;
    }

    public void setDiffuse(Vector3f diffuse) {
        this.diffuse = diffuse;
    }

    public Vector3f getSpecular() {
        return specular;
    }

    public void setSpecular(Vector3f specular) {
        this.specular = specular;
    }

    public float getShininess() {
        return shininess;
    }

    public void setShininess(float shininess) {
        this.shininess = shininess;
    }

    public String getTextureName() {
        return textureName;
    }
}
