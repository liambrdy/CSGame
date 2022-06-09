package game;

import core.Button;
import core.Input;
import core.Key;
import org.joml.Vector2f;
import org.joml.Vector4f;
import renderer.MasterRenderer;
import renderer.SpriteRenderer;
import renderer.SpriteSheet;

import javax.print.attribute.standard.SheetCollate;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Scene {
    private Map<Vector2f, SpriteRenderer.SpriteEntry> tiles = new HashMap<>();
    private Map<Vector2f, SpriteRenderer.SpriteEntry> decalTiles = new HashMap<>();
    private SpriteSheet sheet;
    private boolean coordinateSystemEnabled = true;
    private boolean editorEnabled = true;

    private Vector2f position;

    private Vector2f selectedTile;
    private Vector2f lastPlacedTile;
    private float currentHeight;

    private Vector2f homeTile, enemyTile;
    private boolean homeSet, enemySet;
    private String name;

    public Scene(String n) {
        name = n;
        selectedTile = new Vector2f(0.0f, 0.0f);
        sheet = MasterRenderer.getCurrentSheet();

        position = new Vector2f();
        homeTile = new Vector2f();
        enemyTile = new Vector2f();
        lastPlacedTile = new Vector2f();

        homeSet = false;
        enemySet = false;

        currentHeight = 0.0f;

        for (int y = 0; y < 40; y++) {
            for (int x = 0; x < 40; x++) {
                Vector2f pos = new Vector2f(x, y);
                tiles.put(pos, new SpriteRenderer.SpriteEntry(pos, 0.0f, 0.0f, 1.0f));
            }
        }
//        tiles.put(new Vector2f(10.0f, 10.0f), new SpriteRenderer.SpriteEntry(new Vector2f(10.0f, 10.0f), 0.0f, 0.0f, 1.0f));
    }

    public void render() {
        MasterRenderer.setSpriteOffset(position);

        for (SpriteRenderer.SpriteEntry e : tiles.values())
            MasterRenderer.drawSprite(new Vector2f(e.getPosition()), e.getHeight(), e.getTexX(), e.getTexY());

        for (SpriteRenderer.SpriteEntry e : decalTiles.values())
            MasterRenderer.drawSprite(new Vector2f(e.getPosition()), e.getHeight(), e.getTexX(), e.getTexY());

        if (homeSet)
            MasterRenderer.drawIsoTile(homeTile, new Vector4f(0.2f, 0.2f, 0.9f, 1.0f), position);
        if (enemySet)
            MasterRenderer.drawIsoTile(enemyTile, new Vector4f(0.7f, 0.7f, 0.1f, 1.0f), position);
    }

    public void drawEditor() {
        if (editorEnabled) {
            float pxEachX = 220.0f / sheet.getColumns();
            float pxEachY = 220.0f / sheet.getRows();
            if (coordinateSystemEnabled)
                MasterRenderer.drawCoordinateSystem(position);
            MasterRenderer.drawTexture(new Vector4f(5, 5, 220.0f, 220.0f), MasterRenderer.getCurrentSheet().getSheet());
            MasterRenderer.drawSquare(new Vector4f(pxEachX * selectedTile.x + 5, 220.0f - pxEachY - pxEachY * selectedTile.y + 5, pxEachX, pxEachY), new Vector4f(0.8f, 0.8f, 0.2f, 1.0f));

            boolean shifted = Input.isKeyDown(Key.Shift);

            if (Input.isButtonDown(Button.Button1)) {
                Vector2f tile = getTileCoordinate();
                if (tile.x >= 0 && tile.x <= 40 && tile.y >= 0 && tile.y <= 40) {
                    if (Input.isKeyDown(Key.Tab)) {
                        tiles.remove(tile);
                    } else if (!shifted) {
                        tiles.put(tile, new SpriteRenderer.SpriteEntry(new Vector2f((int) tile.x, (int) tile.y), 0.0f, selectedTile.x, selectedTile.y));
                        lastPlacedTile = tile;
                        currentHeight = 0.0f;
                    } else {
                        decalTiles.put(tile, new SpriteRenderer.SpriteEntry(new Vector2f((int) tile.x, (int) tile.y), sheet.getSpriteHeight() * sheet.getScale(), selectedTile.x, selectedTile.y));
                    }
                }
            }

            if (Input.isButtonClicked(Button.Button2)) {
                Vector2f tile = getTileCoordinate();
                if (tile.x >= 0 && tile.x <= 40 && tile.y >= 0 && tile.y <= 40 && tiles.containsKey(tile)) {
                    if (!shifted) {
                        homeTile = tile;
                        homeSet = true;
                    } else {
                        enemyTile = tile;
                        enemySet = true;
                    }
                }
            }

            if (Input.isButtonClicked(Button.Button3)) {
                Vector2f tile = getTileCoordinate();
                if (tile.x >= 0 && tile.x <= 40 && tile.y >= 0 && tile.y <= 40) {
                    if (!shifted && tiles.containsKey(tile))
                        tiles.remove(tile);
                    else if (shifted && decalTiles.containsKey(tile))
                        decalTiles.remove(tile);
                }
            }

            if (Input.scrolledThisFrame()) {
                currentHeight += Input.getScroll() * 1.5f;
                setHeight(lastPlacedTile.x, lastPlacedTile.y, currentHeight);
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
    }

    public void update() {
        if (Input.isKeyDown(Key.D))
            position.x -= 2.0f;
        else if (Input.isKeyDown(Key.A))
            position.x += 2.0f;
        if (Input.isKeyDown(Key.W))
            position.y += 2.0f;
        else if (Input.isKeyDown(Key.S))
            position.y -= 2.0f;

        if (Input.isKeyPressed(Key.Backspace))
            editorEnabled = !editorEnabled;
    }

    private Vector2f getTileCoordinate() {
        Vector2f pos = Input.getMousePos();
        pos.sub(position);
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

    public void write(String outputPath) {
        try (FileOutputStream fileStream = new FileOutputStream(outputPath)) {
            DataOutputStream stream = new DataOutputStream(fileStream);
            stream.writeBytes("SCEN");
            stream.writeInt(name.length());
            stream.writeBytes(name);
            stream.writeInt(tiles.size());
            stream.writeInt(decalTiles.size());

            for (SpriteRenderer.SpriteEntry e : tiles.values()) {
                Vector2f p = e.getPosition();
                stream.writeFloat(p.x);
                stream.writeFloat(p.y);
                stream.writeFloat(e.getHeight());
                stream.writeFloat(e.getTexX());
                stream.writeFloat(e.getTexY());
            }
//            SheetCollate nj
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Vector2f getOffset() {
        return position;
    }

    public boolean getEditorEnabled() {
        return editorEnabled;
    }

    public Vector2f getHomeTile() {
        return homeTile;
    }

    public Vector2f getEnemyTile() {
        return enemyTile;
    }
}
