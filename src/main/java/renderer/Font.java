package renderer;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.system.MemoryStack;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.channels.FileChannel;
import java.util.Random;

import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.*;

public class Font {
    private ByteBuffer fontData;
    private STBTTFontinfo info;
    private int width, height;
    private STBTTPackedchar.Buffer chars;
    private ByteBuffer pixels;
    private Texture texture;

    public Font(String path) {
        width = 512;
        height = 512;
        pixels = BufferUtils.createByteBuffer(width * height);

        try (MemoryStack stack = stackPush()) {
            try (RandomAccessFile f = new RandomAccessFile(path, "r");
                 FileChannel channel = f.getChannel()) {
                long fileSize = channel.size();

                fontData = BufferUtils.createByteBuffer((int)fileSize);
                channel.read(fontData);
                fontData.flip();
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Failed to find font file " + path);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read font file " + path);
            }

            info = STBTTFontinfo.create();
            boolean res = stbtt_InitFont(info, fontData);
            if (!res)
                throw new RuntimeException("Failed to create font into for font " + path);

            STBTTPackContext context = STBTTPackContext.calloc(stack);
            res = stbtt_PackBegin(context, pixels, width, height, 0, 1);
            if (!res)
                throw new RuntimeException("Failed to begin char packing");

            chars = STBTTPackedchar.create(128-32);
            stbtt_PackFontRange(context, fontData, 0, STBTT_POINT_SIZE(50), 32, chars);

            stbtt_PackEnd(context);

            texture = new Texture(width, height, pixels, GL_RED);
        }
    }

    public Texture getTexture() {
        return texture;
    }

    public STBTTAlignedQuad getQuad(char c, MemoryStack stack, FloatBuffer x, FloatBuffer y) {
        STBTTAlignedQuad q = STBTTAlignedQuad.calloc(stack);
        stbtt_GetPackedQuad(chars, width, height, c - 32, x, y, q, true);
        return q;
    }
}
