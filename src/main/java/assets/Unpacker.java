package assets;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

public class Unpacker {
    public static Packer.PackedAssets unpack(String assetPath) {
        try (FileInputStream stream = new FileInputStream(assetPath)) {
            System.out.println(Arrays.toString(stream.readNBytes(4)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Failed to find asset file: " + assetPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not asset file: " + assetPath);
        }

        return new Packer.PackedAssets();
    }
}
