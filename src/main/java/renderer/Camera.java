package renderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private Vector3f pos;

    private Matrix4f view = new Matrix4f();

    public Camera() {
        this(new Vector3f(0.0f));
    }

    public Camera(Vector3f position) {
        pos = position;
        view.lookAt(pos.x, pos.y, pos.z, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
    }

    public Matrix4f getViewMatrix() {
        return view;
    }
}
