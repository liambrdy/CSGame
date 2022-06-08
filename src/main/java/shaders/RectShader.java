package shaders;

import org.joml.Matrix4f;

public class RectShader extends Shader {
    private int projectionLocation;
    private int textureLocation;

    public RectShader() {
        super("rect.glsl");
    }

    @Override
    protected void getAllUniformLocations() {
        projectionLocation = super.getUniformLocation("u_Projection");
        textureLocation = super.getUniformLocation("u_SpriteSheet");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "a_V0");
        super.bindAttribute(1, "a_V1");
        super.bindAttribute(2, "a_V2");
        super.bindAttribute(3, "a_V3");
        super.bindAttribute(4, "a_C0");
        super.bindAttribute(5, "a_C1");
        super.bindAttribute(6, "a_C2");
        super.bindAttribute(7, "a_C3");
    }

    public void setProjection(Matrix4f m) {
        super.setMatrix(projectionLocation, m);
    }

    public void setTexture() {
        super.setInt(textureLocation, 0);
    }
}
