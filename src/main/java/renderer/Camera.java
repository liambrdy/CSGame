package renderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private Vector3f pos;
    private Vector3f forward;

    private Matrix4f view = new Matrix4f();

    public Camera() {
        this(new Vector3f(0.0f));
    }

    public Camera(Vector3f position) {
        forward = new Vector3f(0.0f, 0.0f, 1.0f);
        pos = position;
        view.lookAt(pos.x, pos.y, pos.z, pos.x + forward.x, pos.y + forward.y, pos.z + forward.z, 0.0f, 1.0f, 0.0f);
    }

    public Matrix4f getViewMatrix() {
        return view;
    }

    public void move() {
        if (Input.getKey(Key.A)) {

        }
    }
}
