package assets;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Unpacker {
    public static Packer.PackedAssets unpack(String assetPath) {
        Packer.PackedAssets assets = new Packer.PackedAssets();
        try (FileInputStream stream = new FileInputStream(assetPath)) {
            String header = unpackString(stream, 4);
            if (!header.equals("aset"))
                throw new RuntimeException("File does not have asset file header");
            int assetCount = stream.read();

            for (int i = 0; i < assetCount; i++) {
                String assetHeader = unpackString(stream, 4);
                switch (assetHeader) {
                    case LoadedShader.HEADER -> assets.addShader(new LoadedShader(stream));
                    case LoadedTexture.HEADER -> assets.addTexture(new LoadedTexture(stream));
                    case LoadedModel.HEADER -> assets.addModel(new LoadedModel(stream));
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Failed to find asset file: " + assetPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not asset file: " + assetPath);
        }

        return assets;
    }

    public static String unpackString(FileInputStream stream, int len) throws IOException {
        byte[] bytes = stream.readNBytes(len);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static int unpackInteger(FileInputStream stream) throws IOException {
        int len = stream.read();
        String str = unpackString(stream, len);
        return Integer.parseInt(str);
    }

    public static float unpackFloat(FileInputStream stream) throws IOException {
        int len = stream.read();
        String str = unpackString(stream, len);
        return Float.parseFloat(str);
    }
}
