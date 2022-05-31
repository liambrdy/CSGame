package renderer;

public interface IRenderer {
    void beginScene();
    void endScene();
    void onWindowResize(float width, float height);
}
