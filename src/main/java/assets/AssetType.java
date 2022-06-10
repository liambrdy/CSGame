package assets;

public enum AssetType {
    Unknown,
    Texture,
    Shader,
    Model,
    Scene;

    public static AssetType fromString(String ext) {
        return switch (ext) {
            case "png", "jpg", "jpeg" -> Texture;
            case "vert", "frag", "glsl" -> Shader;
            case "obj", "fbx" -> Model;
            case "scene" -> Scene;
            default -> Unknown;
        };
    }
}
