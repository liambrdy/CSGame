package renderer;

import assets.AssetManager;
import org.joml.Matrix2f;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class SpriteSheet {
    private Texture sheet;
    private int rows, columns;
    private int spriteCount;
    private int spriteWidth, spriteHeight;
    private float scale;
    private Matrix2f tileToScreen;
    private Matrix2f screenToTile;

    public SpriteSheet(String textureName, int r, int c, int count, float s) {
        sheet = new Texture(AssetManager.getLoadedTexture(textureName), true);
        rows = r;
        columns = c;
        spriteCount = count;
        scale = s;

        spriteWidth = sheet.getWidth() / columns;
        spriteHeight = sheet.getHeight() / rows;

        tileToScreen = new Matrix2f(0.5f * spriteWidth * scale, 0.25f * spriteHeight * scale, -0.5f * spriteWidth * scale, 0.25f * spriteHeight * scale);
        screenToTile = new Matrix2f(tileToScreen).invert();
    }

    public Vector2f toScreen(Vector2f tile) {
        Vector2f screen = new Vector2f(tile);
        screen.mul(tileToScreen);
        screen.x += MasterRenderer.getWidth() / 2.0f;
        screen.x -= spriteWidth * scale / 2.0f;

        return screen;
    }

    public Vector2f toTile(Vector2f screen) {
        Vector2f tile = new Vector2f(screen);
//        tile.x += spriteWidth * scale / 2.0f;
        tile.x -= MasterRenderer.getWidth() / 2.0f;
        tile.mul(screenToTile);
        return tile;
    }

    public Texture getSheet() {
        return sheet;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public int getSpriteCount() {
        return spriteCount;
    }

    public int getSpriteWidth() {
        return spriteWidth;
    }

    public int getSpriteHeight() {
        return spriteHeight;
    }

    public float getScale() {
        return scale;
    }
}
