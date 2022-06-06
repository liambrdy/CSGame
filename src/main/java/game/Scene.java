package game;

import org.joml.Vector2f;
import org.joml.Vector4f;
import renderer.MasterRenderer;
import renderer.SpriteRenderer;
import renderer.SpriteSheet;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Scene {
    private Map<Vector2f, SpriteRenderer.SpriteEntry> tiles = new HashMap<>();
    private SpriteSheet sheet;

    private static Vector2f selectedTile;

    public Scene() {
        selectedTile = new Vector2f(0.0f, 0.0f);
        sheet = MasterRenderer.getCurrentSheet();
    }

    public void render() {
        for (SpriteRenderer.SpriteEntry e : tiles.values()) {
            MasterRenderer.drawSprite(e.getPosition(), e.getHeight(), e.getTexX(), e.getTexY());
        }
    }

    public static void drawEditor() {
        float pxEachX = 220.0f / 32.0f;
        float pxEachY = 220.0f / 32.0f;
        MasterRenderer.drawCoordinateSystem();
        MasterRenderer.drawTexture(new Vector4f(5, 5, 220.0f, 220.0f), MasterRenderer.getCurrentSheet().getSheet());
        MasterRenderer.drawSquare(new Vector4f(pxEachX * selectedTile.x + 5, pxEachY * selectedTile.y + 5, pxEachX, pxEachY), new Vector4f(0.8f, 0.8f, 0.2f, 1.0f));
    }

    public void setHeight(float x, float y, float h) {
        Vector2f s = new Vector2f(x, y);
        if (tiles.containsKey(s)) {
            tiles.get(s).setHeight(h);
        }
    }
}
