package renderer;

import assets.AssetManager;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class SpriteSheet {
    private Texture sheet;
    private int rows, columns;
    private int spriteCount;
    private int spriteWidth, spriteHeight;

    public SpriteSheet(String textureName, int r, int c, int count) {
        sheet = new Texture(AssetManager.getLoadedTexture(textureName), true);
        rows = r;
        columns = c;
        spriteCount = count;

        spriteWidth = sheet.getWidth() / columns;
        spriteHeight = sheet.getHeight() / rows;
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
}
