package renderer;

import assets.AssetManager;
import assets.LoadedModel;
import org.joml.Matrix4f;
import shaders.Shader;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Model {
    Mesh[] meshes;
    Material[] materials;
    List<Integer> materialIdxs = new ArrayList<>();

    public Model(String name) {
        LoadedModel mod = AssetManager.getLoadedModel(name);
        List<LoadedModel.LoadedMesh> loadedMeshes = mod.getMeshes();
        List<Material> loadedMaterials = mod.getMaterials();
        meshes = new Mesh[loadedMeshes.size()];
        materials = new Material[loadedMaterials.size()];
        for (int i = 0; i < loadedMaterials.size(); i++) {
            Material mat = loadedMaterials.get(i);
            materials[i] = mat;
        }
        for (int i = 0; i < loadedMeshes.size(); i++) {
            LoadedModel.LoadedMesh m = loadedMeshes.get(i);
            meshes[i] = Mesh.create(toFloatArray(m.getVertices()), toFloatArray(m.getTexCoords()), toFloatArray(m.getNormals()), toIntegerArray(m.getIndices()));
            materialIdxs.add(m.getMaterialIndex());
        }
    }

    private float[] toFloatArray(List<Float> l) {
        float[] arr = new float[l.size()];
        for (int i = 0; i < l.size(); i++)
            arr[i] = l.get(i);
        return arr;
    }

    private int[] toIntegerArray(List<Integer> l) {
        int[] arr = new int[l.size()];
        for (int i = 0; i < l.size(); i++)
            arr[i] = l.get(i);
        return arr;
    }

    public Mesh[] getMeshes() { return meshes; }
    public Material getMaterial(int i) { return materials[materialIdxs.get(i)]; }
}
