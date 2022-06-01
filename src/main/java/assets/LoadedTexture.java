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
    private byte[] pixelArr;
    private String name;
    public static final String HEADER = "text";

    public LoadedTexture(File path) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer widthBuffer = stack.callocInt(1);
            IntBuffer heightBuffer = stack.callocInt(1);
            IntBuffer channelBuffer = stack.callocInt(1);

            name = path.getName();
            name = name.substring(0, name.lastIndexOf("."));

            pixels = stbi_load(path.toString(), widthBuffer, heightBuffer, channelBuffer, STBI_default);
            if (pixels == null)
                throw new RuntimeException("Failed to load texture: " + path);

            width = widthBuffer.get(0);
            height = heightBuffer.get(0);
            channels = channelBuffer.get(0);
        }
    }

    public LoadedTexture(DataInputStream stream) throws IOException {
        int nameLen = stream.readInt();
        name = Unpacker.unpackString(stream, nameLen);
        width = stream.readInt();
        height = stream.readInt();
        channels = stream.readInt();

        pixels = BufferUtils.createByteBuffer(width * height * channels);
        pixels.put(stream.readNBytes(width * height * channels));
        pixels.flip();
//        pixels = BufferUtils.createByteBuffer(width * height * channels);
//        for (int i = 0; i < width * height * channels; i++)
//            pixels.put(tmp.get(i));
    }

    public void write(DataOutputStream stream) throws IOException {
        stream.writeBytes(HEADER);
        stream.writeInt(name.length());
        stream.writeBytes(name);
        stream.writeInt(width);
        stream.writeInt(height);
        stream.writeInt(channels);

        byte[] bytes = new byte[pixels.remaining()];
        pixels.get(bytes, 0, bytes.length);
        stream.write(bytes);

//        stbi_image_free(pixels);
    }

    public String getName() {
        return name;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getChannels() {
        return channels;
    }

    public ByteBuffer getPixels() {
        return pixels;
    }

}
