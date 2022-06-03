package renderer;

import assets.LoadedTexture;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.opengl.GL33.*;

public class Texture {
    private int textureID;
    private int width, height;
    private int format, channels;

    public Texture(String path) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer widthBuffer = stack.callocInt(1);
            IntBuffer heightBuffer = stack.callocInt(1);
            IntBuffer channelBuffer = stack.callocInt(1);

            String realPath = "src/main/resources/" + path;

            stbi_set_flip_vertically_on_load(true);
            ByteBuffer pixels = stbi_load(realPath, widthBuffer, heightBuffer, channelBuffer, STBI_default);
            if (pixels == null) {
                throw new RuntimeException("Failed to load image " + path);
            }

            width = widthBuffer.get(0);
            height = heightBuffer.get(0);

            channels = channelBuffer.get(0);
            if (channels == 4) format = GL_RGBA;
            else if (channels == 3) format = GL_RGB;
            else throw new RuntimeException("Unknown image format");

            IntBuffer textureBuffer = stack.callocInt(1);
            glGenTextures(textureBuffer);
            textureID = textureBuffer.get(0);
            glBindTexture(GL_TEXTURE_2D, textureID);
            glTexImage2D(GL_TEXTURE_2D, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, pixels);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            stbi_image_free(pixels);
        }
    }

    public Texture(int width, int height, ByteBuffer pixels, int format) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer texBuffer = stack.callocInt(1);
            glGenTextures(texBuffer);
            textureID = texBuffer.get(0);
            glBindTexture(GL_TEXTURE_2D, textureID);
            glTexImage2D(GL_TEXTURE_2D, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, pixels);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        }
    }

    public Texture(LoadedTexture texture) {
        width = texture.getWidth();
        height = texture.getHeight();
        channels = texture.getChannels();
        format = channels == 4 ? GL_RGBA : GL_RGB;

        try (MemoryStack stack = stackPush()) {
            IntBuffer texBuffer = stack.callocInt(1);
            glGenTextures(texBuffer);
            textureID = texBuffer.get(0);
            glBindTexture(GL_TEXTURE_2D, textureID);

            glTexImage2D(GL_TEXTURE_2D, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, texture.getPixels());

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        }
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, textureID);
    }

    public void bind(int slot) {
        glActiveTexture(GL_TEXTURE0 + slot);
        glBindTexture(GL_TEXTURE_2D, textureID);
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
}
