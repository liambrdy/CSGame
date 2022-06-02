package renderer;

import assets.AssetManager;
import org.joml.Vector3f;
import shaders.StaticShader;

public class Material {
    public static final Vector3f DEFAULT_AMBIENT = new Vector3f(0.5f, 0.5f, 0.5f);
    public static final Vector3f DEFAULT_DIFFUSE = new Vector3f(1.0f, 1.0f, 1.0f);
    public static final Vector3f DEFAULT_SPECULAR = new Vector3f(0.5f, 0.5f, 0.5f);
    public static final float DEFAULT_SHININESS = 32.0f;
    private Vector3f ambient, diffuse, specular;
    private float shininess;
    private boolean hasNormalMap;
    private String diffuseTexName, normalTexName;
    private Texture diffuseTex, normalTex;

    public Material(Vector3f am, Vector3f df, Vector3f sp, float sh, String diffName, String normName) {
        ambient = am;
        diffuse = df;
        specular = sp;
        shininess = sh;
        diffuseTexName = diffName;
        normalTexName = normName;
        hasNormalMap = !normalTexName.equals("N/A");

        if (diffuseTexName.equals("N/A"))
            diffuseTex = AssetManager.getDefaultTexture();
        else
            diffuseTex = AssetManager.getTexture(diffuseTexName);

        if (hasNormalMap)
            normalTex = AssetManager.getTexture(normalTexName);
    }

    public Material(Vector3f am, Vector3f df, Vector3f sp, float sh, String diffName, String normName, boolean loading) {
        ambient = am;
        diffuse = df;
        specular = sp;
        shininess = sh;
        diffuseTexName = diffName;
        normalTexName = normName;
        hasNormalMap = !normalTexName.equals("N/A");

        if (!loading) {
            if (diffuseTexName.equals("N/A"))
                diffuseTex = AssetManager.getDefaultTexture();
            else
                diffuseTex = AssetManager.getTexture(diffuseTexName);

            if (hasNormalMap)
                normalTex = AssetManager.getTexture(normalTexName);
        }
    }

    public Material(Material other) {
        ambient = other.ambient;
        diffuse = other.diffuse;
        specular = other.specular;
        shininess = other.shininess;
        diffuseTexName = other.diffuseTexName;
        normalTexName = other.normalTexName;
        hasNormalMap = !normalTexName.equals("N/A");

        if (diffuseTexName.equals("N/A"))
            diffuseTex = AssetManager.getDefaultTexture();
        else
            diffuseTex = AssetManager.getTexture(diffuseTexName);

        if (hasNormalMap)
            normalTex = AssetManager.getTexture(normalTexName);
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

    public String getDiffuseTexName() {
        return diffuseTexName;
    }
    public String getNormalTexName() {
        return normalTexName;
    }

    public Texture getDiffuseTex() {
        return diffuseTex;
    }

    public Texture getNormalTex() {
        return normalTex;
    }

    public boolean getHasNormalMap() {
        return hasNormalMap;
    }
}
