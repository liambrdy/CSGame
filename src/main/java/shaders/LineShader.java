package shaders;

import org.joml.Matrix4f;

public class LineShader extends Shader {
    private int projectionLocation;

    public LineShader() {
        super("line");
    }

    @Override
    protected void getAllUniformLocations() {
        projectionLocation = super.getUniformLocation("u_Projection");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "a_V0");
        super.bindAttribute(1, "a_V1");
        super.bindAttribute(2, "a_C1");
        super.bindAttribute(3, "a_C2");
    }

    public void setProjection(Matrix4f proj) {
        super.setMatrix(projectionLocation, proj);
    }
}
