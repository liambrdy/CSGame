package game;

import org.joml.Vector2f;
import org.joml.Vector4f;
import renderer.MasterRenderer;

public class Hero extends Entity {
    public Hero(Vector2f position, float maxHealth) {
        super(position, maxHealth);
    }

    @Override
    public void render(Scene scene) {
        Vector2f screen = MasterRenderer.getCurrentSheet().toScreen(position);
        screen.add(scene.getOffset());
        Vector2f pos = new Vector2f(screen.x + 32.0f/2 + 5.0f, screen.y + 32.0f/8 + 5.0f);
        MasterRenderer.drawRect(new Vector4f(pos.x, pos.y, 10.0f, 10.0f), new Vector4f(0.2f, 1.0f, 0.2f, 1.0f));
        MasterRenderer.drawText(health + "/" + maxHealth, new Vector2f(pos.x, pos.y - 3.0f), new Vector4f(0.8f, 0.2f, 0.2f, 1.0f));
    }
}
