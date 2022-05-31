package renderer;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class Light {
    private Vector3f position;
    private Vector3f color;
    private Vector3f attenuation = new Vector3f(1, 0, 0);

    public Light(Vector3f pos, Vector3f col) {
        this.position = pos;
        this.color = col;
    }

    public Light(Vector3f pos, Vector3f col, Vector3f att) {
        this.position = pos;
        this.color = col;
        this.attenuation = att;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public Vector3f getAttenuation() {
        return attenuation;
    }

    public void setAttenuation(Vector3f attenuation) {
        this.attenuation = attenuation;
    }
}
