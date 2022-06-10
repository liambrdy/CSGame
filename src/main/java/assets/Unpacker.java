package assets;

import game.Scene;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Unpacker {
    public static Packer.PackedAssets unpack(String assetPath, boolean inJar) {
        Packer.PackedAssets assets = new Packer.PackedAssets();
        try {
            InputStream fileStream;
            if (inJar)
                fileStream = Unpacker.class.getResourceAsStream("/" + assetPath);
            else
                fileStream = new FileInputStream(assetPath);
            long begin = System.nanoTime();
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
                    case Scene.HEADER -> assets.addScene(new Scene(stream));
                }
            }

            long end = System.nanoTime();
            System.out.println("Unpacked " + assets.getModels().size() + " models, " + assets.getTextures().size() + " textures, and " + assets.getShaders().size() +
                    " shaders in " + ((end - begin) / 1000000000.0) + " seconds");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Failed to find asset file: " + assetPath);
        } catch (EOFException e) {
            throw new RuntimeException("Ran past end of asset file");
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
