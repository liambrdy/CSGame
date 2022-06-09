package game;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import renderer.Model;

public abstract class Entity {
    Vector2f position;
    float health, maxHealth;
    int level, xp;

    public Entity(Vector2f position, float maxHealth) {
        this.position = position;
        this.health = maxHealth;
        this.maxHealth = maxHealth;
        this.level = 1;
        this.xp = 0;
    }

    public void move(Vector2f newPos) {
        position = newPos;
    }

    public abstract void render(Scene scene);
}
