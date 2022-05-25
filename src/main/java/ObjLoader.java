import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;
import renderer.Mesh;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.*;

public class ObjLoader {
    public static Mesh[] loadObj(String path) {
        AIScene scene = aiImportFile(path, aiProcess_JoinIdenticalVertices | aiProcess_Triangulate | aiProcess_FixInfacingNormals | aiProcess_GenNormals);
        if (scene == null)
            throw new RuntimeException("Failed to load obj: " + path);

        int meshCount = scene.mNumMeshes();
        PointerBuffer aiMeshes = scene.mMeshes();
        Mesh[] meshes = new Mesh[meshCount];
        for (int i = 0; i < meshCount; i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            Mesh mesh = processMesh(aiMesh);
            meshes[i] = mesh;
        }

        return meshes;
    }

    private static Mesh processMesh(AIMesh aiMesh) {
        List<Float> vertices = new ArrayList<>();
        List<Float> textures = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        processVertices(aiMesh, vertices);
        processTexCoords(aiMesh, textures);
        processNormals(aiMesh, normals);
        processIndices(aiMesh, indices);

        Mesh mesh = Mesh.create(listToArray(vertices), listToArray(textures), listToArray(normals), listToIntArray(indices));

        return mesh;
    }

    private static void processVertices(AIMesh aiMesh, List<Float> vertices) {
        AIVector3D.Buffer aiVertices = aiMesh.mVertices();
        while (aiVertices.remaining() > 0) {
            AIVector3D aiVertex = aiVertices.get();
            vertices.add(aiVertex.x());
            vertices.add(aiVertex.y());
            vertices.add(aiVertex.z());
        }
    }

    private static void processTexCoords(AIMesh aiMesh, List<Float> coords) {
        if (aiMesh.mNumUVComponents().get(0) != 0) {
            AIVector3D.Buffer aiTextureCoords = aiMesh.mTextureCoords(0);
            for (int i = 0; i < aiMesh.mNumVertices(); i++) {
                AIVector3D uv = aiTextureCoords.get(i);
                coords.add(uv.x());
                coords.add(uv.y());
            }
        } else {
            coords.add(0.0f);
            coords.add(0.0f);
        }
    }

    private static void processNormals(AIMesh aiMesh, List<Float> normals) {
        AIVector3D.Buffer aiNormals = aiMesh.mNormals();
        while (aiNormals.remaining() > 0) {
            AIVector3D aiNormal = aiNormals.get();
            normals.add(aiNormal.x());
            normals.add(aiNormal.y());
            normals.add(aiNormal.z());
        }
    }

    private static void processIndices(AIMesh aiMesh, List<Integer> indices) {
        int faceCount = aiMesh.mNumFaces();
        AIFace.Buffer aiFaces = aiMesh.mFaces();
        while (aiFaces.remaining() > 0) {
            AIFace aiFace = aiFaces.get();
            if (aiFace.mNumIndices() != 3)
                throw new RuntimeException("Face does not have three indices");
            IntBuffer indexBuffer = aiFace.mIndices();
            indices.add(indexBuffer.get(0));
            indices.add(indexBuffer.get(1));
            indices.add(indexBuffer.get(2));
        }
    }

    private static float[] listToArray(List<Float> list) {
        float[] arr = new float[list.size()];
        for (int i = 0; i < list.size(); i++)
            arr[i] = list.get(i);
        return arr;
    }

    private static int[] listToIntArray(List<Integer> list) {
        int[] arr = new int[list.size()];
        for (int i = 0; i < list.size(); i++)
            arr[i] = list.get(i);
        return arr;
    }
}
