package assets;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static assets.Packer.writeString;

public class LoadedShader {
    public static final String HEADER = "shad";

    public enum LoadedShaderType {
        Unknown,
        Vertex,
        Fragment,
        MAX;

        public static LoadedShaderType fromString(String ext) {
            return switch (ext) {
                case "vert" -> Vertex;
                case "frag" -> Fragment;
                default -> Unknown;
            };
        }

        public static String toString(LoadedShaderType type) {
            return switch (type) {
                case Vertex -> "vert";
                case Fragment -> "frag";
                default -> throw new RuntimeException("Unknown shader type");
            };
        }

        public static String toString(int type) {
            return switch (type) {
                case 1 -> "vert";
                case 2 -> "frag";
                default -> throw new RuntimeException("Unknown shader type");
            };
        }
    }

    private Map<Integer, String> shaders = new HashMap<Integer, String>();

    public LoadedShader(File path) {
        StringBuilder sourceBuilder = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line;
            while ((line = reader.readLine()) != null) {
                sourceBuilder.append(line).append("\n");
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.err.println("File \"" + path + "\" not found!");
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            System.err.println("Could not read file: \"" + path + "\"!" );
            e.printStackTrace();
            System.exit(-1);
        }

        String del = "#type";
        int delLength = del.length();

        String source = sourceBuilder.toString();
        int pos = source.indexOf("#type");
        while (pos >= 0) {
            int eol = source.indexOf("\n", pos);
            if (eol < 0)
                throw new RuntimeException("Syntax error");
            int begin = pos + delLength + 1;
            String typeStr = source.substring(begin, eol);
            LoadedShaderType type = LoadedShaderType.fromString(typeStr);

            int nextLinePos = source.indexOf("\n", eol);
            if (nextLinePos < 0)
                throw new RuntimeException("Syntax error");
            pos = source.indexOf(del, nextLinePos);

            String shader = "";
            if (pos < 0) {
                shader = source.substring(nextLinePos);
            } else {
                shader = source.substring(nextLinePos, pos);
            }

            shaders.put(type.ordinal(), shader);
        }
    }

    public LoadedShader(DataInputStream stream) throws IOException {
        int shaderCount = stream.readInt();
        for (int i = 0; i < shaderCount; i++) {
            String typeStr = Unpacker.unpackString(stream, 4);
            int strLen = stream.readInt();
            String shader = Unpacker.unpackString(stream, strLen);
            shaders.put(LoadedShaderType.fromString(typeStr).ordinal(), shader);
        }
    }

    public void write(DataOutputStream stream) throws IOException {
        stream.writeBytes(HEADER);
        stream.writeInt(shaders.size());
        for (int i = LoadedShaderType.Vertex.ordinal(); i < LoadedShaderType.MAX.ordinal(); i++) {
            String shader = shaders.get(i);
            if (shader != null) {
                stream.writeBytes(LoadedShaderType.toString(i));
                stream.writeInt(shader.length());
                stream.writeBytes(shader);
            }
        }
    }
}
