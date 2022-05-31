package shaders;

import org.joml.Matrix4f;
import org.joml.Vector4f;

public class TextShader extends Shader {
    private int projectionLocation;
    private int fontTextureLocation;

    public TextShader() {
        super("text");
    }

    @Override
    protected void getAllUniformLocations() {
        projectionLocation = super.getUniformLocation("u_Projection");
        fontTextureLocation = super.getUniformLocation("u_FontTexture");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "a_Position");
        super.bindAttribute(1, "a_TexCoord");
        super.bindAttribute(2, "a_Color");
    }

    public void setProjection(Matrix4f mat) {
        super.setMatrix(projectionLocation, mat);
    }

    public void setFontTextureSlot(int slot) {
        super.setInt(fontTextureLocation, slot);
    }

}
