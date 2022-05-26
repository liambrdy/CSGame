package shaders;

import org.joml.Matrix4f;

public class StaticShader extends Shader {
    private int projectionLocation;
    private int viewLocation;

    public StaticShader() {
        super("static");
    }

    @Override
    protected void getAllUniformLocations() {
        projectionLocation = super.getUniformLocation("u_Projection");
        viewLocation = super.getUniformLocation("u_View");
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
}
