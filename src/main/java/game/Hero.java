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
        Vector2f o = scene.getOffset();
        float s = MasterRenderer.getCurrentSheet().getScale();
//        MasterRenderer.drawRect(new Vector4f(pos.x, pos.y, 10.0f, 10.0f), new Vector4f(0.2f, 1.0f, 0.2f, 1.0f));
        MasterRenderer.drawRectInTile(position, o, 5.0f, new Vector4f(0.2f, 1.0f, 0.2f, 1.0f));
        Vector2f mouse = scene.getTileCoordinate();
        if (mouse.equals(position))
            MasterRenderer.drawTextInTile(health + "/" + maxHealth, position, new Vector2f(o.x, o.y - 2.0f * s), new Vector4f(0.8f, 0.2f, 0.2f, 1.0f));
    }
}
