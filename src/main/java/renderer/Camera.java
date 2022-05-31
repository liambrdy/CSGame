package renderer;

import core.Input;
import core.Key;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private Vector3f pos;
    private Vector3f forward, up, right;

    private Matrix4f view = new Matrix4f();

    float yaw, pitch;

    public Camera() {
        this(new Vector3f(0.0f));
    }

    public Camera(Vector3f position) {
        yaw = 0.0f;
        pitch = 0.0f;
        forward = new Vector3f(0.0f, 0.0f, 1.0f);
        up = new Vector3f(0.0f, 1.0f, 0.0f);
        right = new Vector3f(1.0f, 0.0f, 0.0f);
        pos = position;
        updateCameraVectors();
        updateViewMatrix();
    }

    public Matrix4f getViewMatrix() {
        return view;
    }

    public void move() {
        boolean moved = Input.isKeyDown(Key.A) || Input.isKeyDown(Key.D) || Input.isKeyDown(Key.W) || Input.isKeyDown(Key.S);
        if (Input.isKeyDown(Key.A))
            pos.sub(right);
        else if (Input.isKeyDown(Key.D))
            pos.add(right);
        if (Input.isKeyDown(Key.W))
            pos.add(forward);
        else if (Input.isKeyDown(Key.S))
            pos.sub(forward);

        boolean rotated = Input.isKeyDown(Key.Up) || Input.isKeyDown(Key.Down) || Input.isKeyDown(Key.Left) || Input.isKeyDown(Key.Right);
        if (Input.isKeyDown(Key.Down))
            pitch -= 1;
        else if (Input.isKeyDown(Key.Up))
            pitch += 1;
        if (Input.isKeyDown(Key.Left))
            yaw -= 1;
        else if (Input.isKeyDown(Key.Right))
            yaw += 1;

        if (rotated)
            updateCameraVectors();

        if (moved)
            updateViewMatrix();
    }

    private void updateViewMatrix() {
        view.setLookAt(pos, new Vector3f(pos).add(forward), up);
    }

    private void updateCameraVectors() {
        Vector3f f = new Vector3f();
        f.x = (float)Math.cos(Math.toRadians(yaw)) * (float)Math.cos(Math.toRadians(pitch));
        f.y = (float)Math.sin(Math.toRadians(pitch));
        f.z = (float)Math.sin(Math.toRadians(yaw)) * (float)Math.cos(Math.toRadians(pitch));
        forward.set(f.normalize());

        right.set(f.cross(up).normalize());
        up.set(new Vector3f(right).cross(forward).normalize());

        updateViewMatrix();
    }

    public Vector3f getPos() { return pos; }
}
