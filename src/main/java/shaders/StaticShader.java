package shaders;

public class StaticShader extends Shader {
    public StaticShader() {
        super("shaders/static.vert", "shaders/static.frag");
    }

    @Override
    protected void getAllUniformLocations() {
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "a_Position");
        super.bindAttribute(1, "a_uvCoord");
    }
}
