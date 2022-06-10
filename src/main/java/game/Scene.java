package game;

import assets.Unpacker;
import core.Button;
import core.Input;
import core.Key;
import org.joml.Vector2f;
import org.joml.Vector4f;
import renderer.MasterRenderer;
import renderer.SpriteRenderer;
import renderer.SpriteSheet;

import javax.print.attribute.standard.SheetCollate;
import java.io.*;
import java.text.NumberFormat;
import java.util.*;

public class Scene {
    public enum EditActionType {
        TilePlace,
        TileReplace,
        TileRemove,
        TileHeight,
    };
    public class EditAction {
        EditActionType type;
        Vector2f location;
        Vector2f lastSprite;
        float height;

        public EditAction(EditActionType t, Vector2f loc, Vector2f last) {
            type = t;
            location = loc;
            lastSprite = last;
        }

        public EditAction(Vector2f loc, float h) {
            type = EditActionType.TileHeight;
            location = loc;
            height = h;
        }

        public EditAction(Vector2f loc) {
            type = EditActionType.TilePlace;
            location = loc;
        }
    };

    private static final int EDIT_STACK_SIZE = 100;
    public static final String HEADER = "scen";

    private Stack<EditAction> edits = new Stack<>();

    private Map<Vector2f, SpriteRenderer.SpriteEntry> tiles = new HashMap<>();
    private Map<Vector2f, SpriteRenderer.SpriteEntry> decalTiles = new HashMap<>();
    private SpriteSheet sheet;
    private boolean coordinateSystemEnabled = true;

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

    public Scene(DataInputStream stream) throws IOException {
        int nameLen = stream.readInt();
        name = Unpacker.unpackString(stream, nameLen);
        int tileCount = stream.readInt();
        int decalTileCount = stream.readInt();

        for (int i = 0; i < tileCount; i++) {
            Vector2f pos = new Vector2f();
            pos.x = stream.readFloat();
            pos.y = stream.readFloat();
            float height = stream.readFloat();
            float texX = stream.readFloat();
            float texY = stream.readFloat();
            tiles.put(pos, new SpriteRenderer.SpriteEntry(pos, height, texX, texY));
        }

        for (int i = 0; i < decalTileCount; i++) {
            Vector2f pos = new Vector2f();
            pos.x = stream.readFloat();
            pos.y = stream.readFloat();
            float height = stream.readFloat();
            float texX = stream.readFloat();
            float texY = stream.readFloat();
            decalTiles.put(pos, new SpriteRenderer.SpriteEntry(pos, height, texX, texY));
        }

        homeSet = stream.readBoolean();
        homeTile = new Vector2f();
        homeTile.x = stream.readFloat();
        homeTile.y = stream.readFloat();

        enemySet = stream.readBoolean();
        enemyTile = new Vector2f();
        enemyTile.x = stream.readFloat();
        enemyTile.y = stream.readFloat();
    }

    public Scene(File path) {
        try (FileInputStream inputStream = new FileInputStream(path)) {
            DataInputStream stream = new DataInputStream(inputStream);

            int nameLen = stream.readInt();
            name = Unpacker.unpackString(stream, nameLen);
            int tileCount = stream.readInt();
            int decalTileCount = stream.readInt();

            for (int i = 0; i < tileCount; i++) {
                Vector2f pos = new Vector2f();
                pos.x = stream.readFloat();
                pos.y = stream.readFloat();
                float height = stream.readFloat();
                float texX = stream.readFloat();
                float texY = stream.readFloat();
                tiles.put(pos, new SpriteRenderer.SpriteEntry(pos, height, texX, texY));
            }

            for (int i = 0; i < decalTileCount; i++) {
                Vector2f pos = new Vector2f();
                pos.x = stream.readFloat();
                pos.y = stream.readFloat();
                float height = stream.readFloat();
                float texX = stream.readFloat();
                float texY = stream.readFloat();
                decalTiles.put(pos, new SpriteRenderer.SpriteEntry(pos, height, texX, texY));
            }

            homeSet = stream.readBoolean();
            homeTile = new Vector2f();
            homeTile.x = stream.readFloat();
            homeTile.y = stream.readFloat();

            enemySet = stream.readBoolean();
            enemyTile = new Vector2f();
            enemyTile.x = stream.readFloat();
            enemyTile.y = stream.readFloat();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void render() {
        MasterRenderer.setSpriteOffset(position);

        for (SpriteRenderer.SpriteEntry e : tiles.values())
            MasterRenderer.drawSprite(new Vector2f(e.getPosition()), e.getHeight(), e.getTexX(), e.getTexY());

        for (SpriteRenderer.SpriteEntry e : decalTiles.values())
            MasterRenderer.drawSprite(new Vector2f(e.getPosition()), e.getHeight(), e.getTexX(), e.getTexY());

        if (homeSet)
            MasterRenderer.drawIsoTile(homeTile, new Vector4f(0.1f, 1.0f, 0.1f, 1.0f), position);
        if (enemySet)
            MasterRenderer.drawIsoTile(enemyTile, new Vector4f(1.0f, 0.2f, 0.1f, 1.0f), position);
    }

    public void drawEditor() {
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
                SpriteRenderer.SpriteEntry lastTile = tiles.get(tile);
                if (Input.isKeyDown(Key.Tab)) {
                    if (tiles.containsKey(tile)) {
                        addEdit(new EditAction(EditActionType.TileRemove, tile, new Vector2f(lastTile.getTexX(), lastTile.getTexY())));
                        tiles.remove(tile);
                    }
                } else if (!shifted && !tile.equals(lastPlacedTile)) {
                    if (tiles.containsKey(tile)) {
                        addEdit(new EditAction(EditActionType.TileReplace, tile, new Vector2f(lastTile.getTexX(), lastTile.getTexY())));
                    } else {
                        addEdit(new EditAction(tile));
                    }
                    tiles.put(tile, new SpriteRenderer.SpriteEntry(new Vector2f((int) tile.x, (int) tile.y), 0.0f, selectedTile.x, selectedTile.y));
                    lastPlacedTile = tile;
                    currentHeight = 0.0f;
                } else if (shifted && !tile.equals(lastPlacedTile)) {
                    decalTiles.put(tile, new SpriteRenderer.SpriteEntry(new Vector2f((int) tile.x, (int) tile.y), sheet.getSpriteHeight() * sheet.getScale() / 2.0f, selectedTile.x, selectedTile.y));
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
            if (shifted) {
                float s = MasterRenderer.getCurrentSheet().getScale() + Input.getScroll();
                s = Math.max(0.1f, Math.min(20.0f, s));
                MasterRenderer.getCurrentSheet().setScale(s);
            } else {
                currentHeight += Input.getScroll() * 1.5f;
                if (edits.isEmpty() || edits.peek().type != EditActionType.TileHeight) {
                    addEdit(new EditAction(lastPlacedTile, currentHeight));
                }
                setHeight(lastPlacedTile.x, lastPlacedTile.y, currentHeight);
            }
        }

        if (Input.isKeyPressed(Key.Z) && Input.isKeyDown(Key.Control)) {
            if (!edits.isEmpty()) {
                EditAction lastAction = edits.pop();
                switch (lastAction.type) {
                    case TilePlace -> {
                        if (tiles.containsKey(lastAction.location))
                            tiles.remove(lastAction.location);
                    }
                    case TileReplace, TileRemove ->
                        tiles.put(lastAction.location, new SpriteRenderer.SpriteEntry(lastAction.location, lastAction.height, lastAction.lastSprite.x, lastAction.lastSprite.y));
                    case TileHeight -> {
                        setHeight(lastAction.location.x, lastAction.location.y, lastAction.height);
                        currentHeight = 0.0f;
                    }
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

    private void addEdit(EditAction action) {
        while (edits.size() > EDIT_STACK_SIZE) {
            edits.remove(0);
        }

        edits.push(action);
    }

    public void update() {
        float s = MasterRenderer.getCurrentSheet().getScale();

        if (Input.isKeyDown(Key.D))
            position.x -= 4.0f;
        else if (Input.isKeyDown(Key.A))
            position.x += 4.0f;
        if (Input.isKeyDown(Key.W))
            position.y += 4.0f;
        else if (Input.isKeyDown(Key.S))
            position.y -= 4.0f;
    }

    public Vector2f getTileCoordinate() {
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

    public void write(File outputPath) {
        try (FileOutputStream fileStream = new FileOutputStream(outputPath)) {
            DataOutputStream stream = new DataOutputStream(fileStream);
            write(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(DataOutputStream stream) throws IOException {
        stream.writeBytes(HEADER);
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

        for (SpriteRenderer.SpriteEntry e : decalTiles.values()) {
            Vector2f p = e.getPosition();
            stream.writeFloat(p.x);
            stream.writeFloat(p.y);
            stream.writeFloat(e.getHeight());
            stream.writeFloat(e.getTexX());
            stream.writeFloat(e.getTexY());
        }

        stream.writeBoolean(homeSet);
        stream.writeFloat(homeTile.x);
        stream.writeFloat(homeTile.y);

        stream.writeBoolean(enemySet);
        stream.writeFloat(enemyTile.x);
        stream.writeFloat(enemyTile.y);
    }

    public Vector2f getOffset() {
        return position;
    }

    public Vector2f getHomeTile() {
        return homeTile;
    }

    public Vector2f getEnemyTile() {
        return enemyTile;
    }

    public String getName() {
        return name;
    }
}
