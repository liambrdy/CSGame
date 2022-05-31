package game;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import renderer.Model;

public class Entity {
    Model model;
    Matrix4f transform;
    Vector3f pos;

    public Entity(Model m, Vector3f p, float scale) {
        model = m;
        pos = p;
        transform = new Matrix4f().translate(p).scale(scale);
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public Matrix4f getTransform() {
        return transform;
    }

    public void setTransform(Matrix4f transform) {
        this.transform = transform;
    }

    public Vector3f getPos() {
        return pos;
    }

    public void setPos(Vector3f pos) {
        this.pos = pos;
    }
}
