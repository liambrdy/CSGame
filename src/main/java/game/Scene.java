package game;

import core.Button;
import core.Input;
import core.Key;
import org.joml.Vector2f;
import org.joml.Vector4f;
import renderer.MasterRenderer;
import renderer.SpriteRenderer;
import renderer.SpriteSheet;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Scene {
    private Map<Vector2f, SpriteRenderer.SpriteEntry> tiles = new HashMap<>();
    private Map<Vector2f, SpriteRenderer.SpriteEntry> decalTiles = new HashMap<>();
    private SpriteSheet sheet;
    private boolean coordinateSystemEnabled = true;

    private static Vector2f selectedTile;

    public Scene() {
        selectedTile = new Vector2f(0.0f, 0.0f);
        sheet = MasterRenderer.getCurrentSheet();
    }

    public void render() {
        for (SpriteRenderer.SpriteEntry e : tiles.values())
            MasterRenderer.drawSprite(e.getPosition(), e.getHeight(), e.getTexX(), e.getTexY());

        for (SpriteRenderer.SpriteEntry e : decalTiles.values())
            MasterRenderer.drawSprite(e.getPosition(), e.getHeight(), e.getTexX(), e.getTexY());
    }

    public void drawEditor() {
        float pxEachX = 220.0f / sheet.getColumns();
        float pxEachY = 220.0f / sheet.getRows();
        if (coordinateSystemEnabled)
            MasterRenderer.drawCoordinateSystem();
        MasterRenderer.drawTexture(new Vector4f(5, 5, 220.0f, 220.0f), MasterRenderer.getCurrentSheet().getSheet());
        MasterRenderer.drawSquare(new Vector4f(pxEachX * selectedTile.x + 5, 220.0f - pxEachY - pxEachY * selectedTile.y + 5, pxEachX, pxEachY), new Vector4f(0.8f, 0.8f, 0.2f, 1.0f));

        if (Input.isButtonClicked(Button.Button1)) {
            Vector2f tile = getTileCoordinate();
            boolean shifted = Input.isKeyDown(Key.Shift);
            if (tile.x >= 0 && tile.x <= 40 && tile.y >= 0 && tile.y <= 40) {
                if (!shifted) {
                    tiles.put(tile, new SpriteRenderer.SpriteEntry(new Vector2f((int) tile.x, (int) tile.y), 0.0f, selectedTile.x, selectedTile.y));
                } else {
                    decalTiles.put(tile, new SpriteRenderer.SpriteEntry(new Vector2f((int) tile.x, (int) tile.y), sheet.getSpriteHeight() * sheet.getScale(), selectedTile.x, selectedTile.y));
                }
            }
        }

        if (Input.isKeyPressed(Key.Left) && selectedTile.x > 0) {
            selectedTile.x--;
        }
        if (Input.isKeyPressed(Key.Right) && selectedTile.x < sheet.getColumns() - 1) {
            selectedTile.x++;
        }
        if (Input.isKeyPressed(Key.Up) && selectedTile.y < sheet.getRows() - 1) {
            selectedTile.y++;
        }
        if (Input.isKeyPressed(Key.Down) && selectedTile.y > 0) {
            selectedTile.y--;
        }

        if (Input.isKeyPressed(Key.Space))
            coordinateSystemEnabled = !coordinateSystemEnabled;
    }

    private Vector2f getTileCoordinate() {
        Vector2f pos = Input.getMousePos();
//        pos.x -= MasterRenderer.getWidth() / 2.0f;
//        pos.x += 32.0f / 2.0f;
        pos =  MasterRenderer.getCurrentSheet().toTile(pos);
        pos.x = (float)Math.floor(pos.x);
        pos.y = (float)Math.floor(pos.y);
        return pos;
    }

    public void setHeight(float x, float y, float h) {
        Vector2f s = new Vector2f(x, y);
        if (tiles.containsKey(s)) {
            tiles.get(s).setHeight(h);
        }
    }
}
