package game;

import assets.Unpacker;
import core.Button;
import core.Input;
import core.Key;
import org.joml.Vector2f;
import org.joml.Vector3f;
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

    private Map<Vector3f, SpriteRenderer.SpriteEntry> tiles = new HashMap<>();
    private SpriteSheet sheet;
    private boolean coordinateSystemEnabled = true;

    private Vector2f position;

    private Vector2f selectedTile;
    private Vector2f lastPlacedTile;
    private float currentHeight;

    private Vector2f homeTile, enemyTile;
    private boolean homeSet, enemySet;
    private String name;

    public Scene(Scene other) {
        this.homeSet = other.homeSet;
        this.enemySet = other.enemySet;
        this.homeTile = other.homeTile;
        this.enemyTile = other.enemyTile;
        this.name = other.name;

        lastPlacedTile = new Vector2f();
        selectedTile = new Vector2f(0.0f, 0.0f);
        position = new Vector2f();

        tiles.putAll(other.tiles);

        sheet = MasterRenderer.getCurrentSheet();
    }

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
                Vector3f pos = new Vector3f(x, y, 0.0f);
                tiles.put(pos, new SpriteRenderer.SpriteEntry(new Vector2f(x, y), 0.0f, 0.0f, 1.0f));
            }
        }
//        tiles.put(new Vector2f(10.0f, 10.0f), new SpriteRenderer.SpriteEntry(new Vector2f(10.0f, 10.0f), 0.0f, 0.0f, 1.0f));
    }

    public Scene(DataInputStream stream) throws IOException {
        int nameLen = stream.readInt();
        name = Unpacker.unpackString(stream, nameLen);
        int tileCount = stream.readInt();

        for (int i = 0; i < tileCount; i++) {
            Vector3f pos = new Vector3f();
            float x = stream.readFloat();
            float y = stream.readFloat();
            pos.x = x;
            pos.y = y;
            pos.z = stream.readFloat();
            float height = stream.readFloat();
            float texX = stream.readFloat();
            float texY = stream.readFloat();
            tiles.put(pos, new SpriteRenderer.SpriteEntry(new Vector2f(x, y), height, texX, texY));
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

    public Scene(File path, boolean loadedHeader) {
        try (FileInputStream inputStream = new FileInputStream(path)) {
            DataInputStream stream = new DataInputStream(inputStream);

            if (!loadedHeader) {
                String h = Unpacker.unpackString(stream, 4);
                if (!h.equals(HEADER))
                    throw new RuntimeException("This asset is not a scene");
            }

            int nameLen = stream.readInt();
            name = Unpacker.unpackString(stream, nameLen);
            int tileCount = stream.readInt();

            for (int i = 0; i < tileCount; i++) {
                Vector3f pos = new Vector3f();
                float x = stream.readFloat();
                float y = stream.readFloat();
                pos.x = x;
                pos.y = y;
                pos.z = stream.readFloat();
                float height = stream.readFloat();
                float texX = stream.readFloat();
                float texY = stream.readFloat();
                tiles.put(pos, new SpriteRenderer.SpriteEntry(new Vector2f(x, y), height, texX, texY));
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

        for (Map.Entry<Vector3f, SpriteRenderer.SpriteEntry> e : tiles.entrySet()) {
            SpriteRenderer.SpriteEntry entry = e.getValue();
            MasterRenderer.drawSprite(entry.getPosition(), entry.getHeight() + e.getKey().z * sheet.getSpriteHeight() * sheet.getScale() / 2.0f, entry.getTexX(), entry.getTexY());
        }

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
            Vector2f t = getTileCoordinate();
            Vector3f tile = new Vector3f(t.x, t.y, 0.0f);
            if (tile.x >= 0 && tile.x <= 40 && tile.y >= 0 && tile.y <= 40) {
                SpriteRenderer.SpriteEntry lastTile = tiles.get(tile);
                if (Input.isKeyDown(Key.Tab)) {
                    if (tiles.containsKey(tile)) {
                        addEdit(new EditAction(EditActionType.TileRemove, t, new Vector2f(lastTile.getTexX(), lastTile.getTexY())));
                        tiles.remove(tile);
                    }
                } else if (!shifted && !tile.equals(lastPlacedTile)) {
                    if (tiles.containsKey(tile)) {
                        addEdit(new EditAction(EditActionType.TileReplace, t, new Vector2f(lastTile.getTexX(), lastTile.getTexY())));
                    } else {
                        addEdit(new EditAction(t));
                    }
                    tiles.put(new Vector3f(tile.x, tile.y, 0.0f), new SpriteRenderer.SpriteEntry(new Vector2f((int) tile.x, (int) tile.y), 0.0f, selectedTile.x, selectedTile.y));
                    lastPlacedTile = t;
                    currentHeight = 0.0f;
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
                    tiles.remove(new Vector3f(tile.x, tile.y, 0.0f));
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
                        tiles.put(new Vector3f(lastAction.location.x, lastAction.location.y, 0.0f), new SpriteRenderer.SpriteEntry(lastAction.location, lastAction.height, lastAction.lastSprite.x, lastAction.lastSprite.y));
                    case TileHeight -> {
                        setHeight(lastAction.location.x, lastAction.location.y, lastAction.height);
                        currentHeight = 0.0f;
                    }
                }
            }
        }

        if (Input.isKeyPressed(Key.F)) {
            for (int y = 0; y < 40; y++) {
                for (int x = 0; x < 40; x++) {
                    Vector2f pos = new Vector2f(x, y);
                    tiles.put(new Vector3f(pos.x, pos.y, 0.0f), new SpriteRenderer.SpriteEntry(pos, 0.0f, selectedTile.x, selectedTile.y));
                }
            }
        }

        if (Input.isKeyDown(Key.Control)) {
            if (Input.isKeyPressed(Key.Up)) {
                SpriteRenderer.SpriteEntry e = tiles.remove(new Vector3f(lastPlacedTile.x, lastPlacedTile.y, 0.0f));
                tiles.put(new Vector3f(lastPlacedTile.x, lastPlacedTile.y, 1.0f), e);
            }
        } else {
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

        for (Map.Entry<Vector3f, SpriteRenderer.SpriteEntry> e : tiles.entrySet()) {
            Vector3f p = e.getKey();
            stream.writeFloat(p.x);
            stream.writeFloat(p.y);
            stream.writeFloat(p.z);
            stream.writeFloat(e.getValue().getHeight());
            stream.writeFloat(e.getValue().getTexX());
            stream.writeFloat(e.getValue().getTexY());
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
    public void setName(String s) { name = s; }
}
