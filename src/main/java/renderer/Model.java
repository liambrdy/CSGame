package renderer;

import assets.AssetManager;
import assets.LoadedModel;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL31.*;

public class Model {
    Mesh[] meshes;
    Material[] materials;
    List<Integer> materialIdxs = new ArrayList<>();

    public Model(String name) {
        LoadedModel mod = AssetManager.getLoadedModel(name);
        List<LoadedModel.LoadedMesh> loadedMeshes = mod.getMeshes();
        List<Material> loadedMaterials = mod.getMaterials();
        materials = new Material[loadedMaterials.size()];
        for (int i = 0; i < loadedMaterials.size(); i++) {
            Material mat = loadedMaterials.get(i);
            materials[i] = mat;
        }

        meshes = new Mesh[materials.length];
        int meshIndex = 0;
        for (int matId = 0; matId < materials.length; matId++) {
            List<Integer> indices = new ArrayList<>();
            List<Float> positions = new ArrayList<>();
            List<Float> texs = new ArrayList<>();
            List<Float> norms = new ArrayList<>();

            int indexCount = 0;

            for (LoadedModel.LoadedMesh m : loadedMeshes) {
                if (m.getMaterialIndex() == matId) {
                    positions.addAll(m.getVertices());
                    texs.addAll(m.getTexCoords());
                    norms.addAll(m.getNormals());

                    if (indices.isEmpty()) {
                        indices.addAll(m.getIndices());
                        indexCount += m.getIndices().size();
                    }
                    else {
                        for (int i = 0; i < m.getIndices().size(); i++) {
                            indices.add(m.getIndices().get(i) + indexCount);
                        }
                        indexCount += m.getIndices().size();
                    }
//                    indices.addAll(m.getIndices());
                }
            }

            String txName = materials[matId].getTextureName();
            if (txName.equals("N/A"))
                meshes[meshIndex] = Mesh.create(toFloatArray(positions), toFloatArray(texs), toFloatArray(norms), toIntegerArray(indices));
            else
                meshes[meshIndex] = Mesh.create(toFloatArray(positions), toFloatArray(texs), toFloatArray(norms), toIntegerArray(indices), txName);
            meshIndex++;
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
    public Material getMaterial(int i) { return materials[i]; }
}
