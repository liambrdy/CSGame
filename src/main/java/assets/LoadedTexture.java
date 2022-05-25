package assets;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static assets.Packer.writeString;

public class LoadedTexture {
    private int width, height, channels;
    private ByteBuffer pixels;

    public static final String HEADER = "text";

    public LoadedTexture(File path) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer widthBuffer = stack.callocInt(1);
            IntBuffer heightBuffer = stack.callocInt(1);
            IntBuffer channelBuffer = stack.callocInt(1);

            pixels = stbi_load(path.toString(), widthBuffer, heightBuffer, channelBuffer, STBI_default);
            if (pixels == null)
                throw new RuntimeException("Failed to load texture: " + path);

            width = widthBuffer.get(0);
            height = heightBuffer.get(0);
            channels = channelBuffer.get(0);
        }
    }

    public LoadedTexture(FileInputStream stream) throws IOException {
        width = stream.read();
        height = stream.read();
        channels = stream.read();

        pixels = BufferUtils.createByteBuffer(width * height);
        pixels.put(stream.readNBytes(width * height));
    }

    public void write(FileOutputStream stream) throws IOException {
        writeString(stream, HEADER);
        stream.write(width);
        stream.write(height);
        stream.write(channels);

        byte[] bytes = new byte[pixels.remaining()];
        pixels.get(bytes, 0, bytes.length);
        stream.write(bytes);

//        stbi_image_free(pixels);
    }
}
