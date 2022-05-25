package assets;

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;
import renderer.Mesh;

import java.io.*;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.*;
import static assets.Packer.*;

public class LoadedModel {
    public static final String HEADER = "modl";

    public class LoadedMesh {
        private List<Float> vertices = new ArrayList<>();
        private List<Float> texCoords = new ArrayList<>();
        private List<Float> normals = new ArrayList<>();
        private List<Integer> indices = new ArrayList<>();

        public LoadedMesh(List<Float> verts, List<Float> texs, List<Float> norms, List<Integer> idxs) {
            vertices = verts;
            texCoords = texs;
            normals = norms;
            indices = idxs;
        }

        public LoadedMesh(FileInputStream stream) throws IOException {
            int vertCount = Unpacker.unpackInteger(stream);
            int indexCount = Unpacker.unpackInteger(stream);

            for (int v = 0; v < vertCount; v++)
                vertices.add(Unpacker.unpackFloat(stream));

            for (int t = 0; t < vertCount; t++)
                texCoords.add(Unpacker.unpackFloat(stream));

            for (int n = 0; n < vertCount; n++)
                normals.add(Unpacker.unpackFloat(stream));

            for (int j = 0; j < indexCount; j++)
                indices.add(Unpacker.unpackInteger(stream));
        }
    }

    private List<LoadedMesh> meshes = new ArrayList<>();

    public LoadedModel(File path) {
        AIScene scene = aiImportFile(path.toString(), aiProcess_JoinIdenticalVertices | aiProcess_Triangulate | aiProcess_FixInfacingNormals | aiProcess_GenNormals);
        if (scene == null)
            throw new RuntimeException("Failed to load obj: " + path);

        int meshCount = scene.mNumMeshes();
        PointerBuffer aiMeshes = scene.mMeshes();
        for (int i = 0; i < meshCount; i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            meshes.add(processMesh(aiMesh));
        }
    }

    public LoadedModel(FileInputStream stream) throws IOException {
        int meshCount = stream.read();
        for (int i = 0; i < meshCount; i++) {
            meshes.add(new LoadedMesh(stream));
        }
    }

    public void write(FileOutputStream stream) throws IOException {
        writeString(stream, HEADER);
        stream.write(meshes.size());
        for (LoadedMesh mesh : meshes) {
            Packer.writeInteger(stream, mesh.vertices.size());
            Packer.writeInteger(stream, mesh.indices.size());
            for (Float v : mesh.vertices)
                Packer.writeFloat(stream, v);
            for (Float t : mesh.texCoords)
                Packer.writeFloat(stream, t);
            for (Float n : mesh.normals)
                Packer.writeFloat(stream, n);
            for (Integer i : mesh.indices)
                Packer.writeInteger(stream, i);
        }
    }

    private LoadedMesh processMesh(AIMesh aiMesh) {
        List<Float> vertices = new ArrayList<>();
        List<Float> textures = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        processVertices(aiMesh, vertices);
        processTexCoords(aiMesh, textures);
        processNormals(aiMesh, normals);
        processIndices(aiMesh, indices);

        return new LoadedMesh(vertices, textures, normals, indices);
    }

    private void processVertices(AIMesh aiMesh, List<Float> vertices) {
        AIVector3D.Buffer aiVertices = aiMesh.mVertices();
        while (aiVertices.remaining() > 0) {
            AIVector3D aiVertex = aiVertices.get();
            vertices.add(aiVertex.x());
            vertices.add(aiVertex.y());
            vertices.add(aiVertex.z());
        }
    }

    private void processTexCoords(AIMesh aiMesh, List<Float> coords) {
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

    private void processNormals(AIMesh aiMesh, List<Float> normals) {
        AIVector3D.Buffer aiNormals = aiMesh.mNormals();
        while (aiNormals.remaining() > 0) {
            AIVector3D aiNormal = aiNormals.get();
            normals.add(aiNormal.x());
            normals.add(aiNormal.y());
            normals.add(aiNormal.z());
        }
    }

    private void processIndices(AIMesh aiMesh, List<Integer> indices) {
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
}