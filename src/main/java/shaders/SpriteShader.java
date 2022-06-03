package shaders;

import org.joml.Matrix4f;
import renderer.SpriteSheet;

public class SpriteShader extends Shader {
    private int projectionLocation;
    private int spriteSheetLocation;
    private int spriteSheetColsLocation;
    private int spriteSheetRowsLocation;
    private int spriteSheetCountLocation;

    public SpriteShader() {
        super("sprite");
    }

    @Override
    protected void getAllUniformLocations() {
        projectionLocation = super.getUniformLocation("u_Projection");
        spriteSheetLocation = super.getUniformLocation("u_spriteSheet.sheet");
        spriteSheetColsLocation = super.getUniformLocation("u_spriteSheet.columns");
        spriteSheetRowsLocation = super.getUniformLocation("u_spriteSheet.rows");
        spriteSheetCountLocation = super.getUniformLocation("u_spriteSheet.count");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "a_V0");
        super.bindAttribute(1, "a_V1");
        super.bindAttribute(2, "a_V2");
        super.bindAttribute(3, "a_V3");
        super.bindAttribute(4, "a_SpriteIndex");
    }

    public void setSpriteSheet(SpriteSheet sheet) {
        super.setInt(spriteSheetLocation, 0);
        super.setFloat(spriteSheetColsLocation, sheet.getColumns());
        super.setFloat(spriteSheetRowsLocation, sheet.getRows());
        super.setInt(spriteSheetCountLocation, sheet.getSpriteCount());
    }

    public void setProjection(Matrix4f mat) {
        super.setMatrix(projectionLocation, mat);
    }
}
