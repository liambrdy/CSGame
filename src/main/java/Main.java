public class Main {
    public static void main(String[] args) {
        Window window = new Window(1280, 720, "Game");

        Input.Init(window.getHandle());

        while (!window.shouldClose()) {
            window.update();

            Input.Update();
        }
    }
}
