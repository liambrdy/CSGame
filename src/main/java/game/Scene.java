package game;

import org.joml.Vector2f;
import renderer.MasterRenderer;
import renderer.SpriteRenderer;
import renderer.SpriteSheet;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Scene {
    private Map<Vector2f, SpriteRenderer.SpriteEntry> tiles = new HashMap<>();
    private SpriteSheet sheet;

    public Scene() {
        sheet = MasterRenderer.getCurrentSheet();
    }

    public void render() {
        for (SpriteRenderer.SpriteEntry e : tiles.values()) {
            MasterRenderer.drawSprite(e.getPosition(), e.getHeight(), e.getTexX(), e.getTexY());
        }
    }

    public void setHeight(float x, float y, float h) {
        Vector2f s = new Vector2f(x, y);
        if (tiles.containsKey(s)) {
            tiles.get(s).setHeight(h);
        }
    }
}
