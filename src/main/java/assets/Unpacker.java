package assets;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Unpacker {
    public static Packer.PackedAssets unpack(String assetPath) {
        Packer.PackedAssets assets = new Packer.PackedAssets();
        try (FileInputStream fileStream = new FileInputStream(assetPath)) {
            DataInputStream stream = new DataInputStream(fileStream);
            String header = unpackString(stream, 4);
            if (!header.equals(Packer.HEADER))
                throw new RuntimeException("File does not have asset file header");

            int assetCount = stream.readInt();
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
        } catch (EOFException ignored) {
        } catch (IOException e) {
            throw new RuntimeException("Could not asset file: " + assetPath);
        }

        return assets;
    }

    public static String unpackString(DataInputStream stream, int len) throws IOException {
        byte[] bytes = new byte[len];
        int count = stream.read(bytes);
        if (len != count)
            throw new RuntimeException("Failed to read from asset file");
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
