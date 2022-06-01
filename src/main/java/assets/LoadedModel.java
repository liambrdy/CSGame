package assets;

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import renderer.Material;
import renderer.Mesh;

import java.io.*;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static org.lwjgl.assimp.Assimp.*;
import static assets.Packer.*;

public class LoadedModel {
    public static final String HEADER = "modl";

    public class LoadedMesh {
        private List<Float> vertices = new ArrayList<>();
        private List<Float> texCoords = new ArrayList<>();
        private List<Float> normals = new ArrayList<>();
        private List<Integer> indices = new ArrayList<>();
        private int materialIndex;

        public LoadedMesh(List<Float> verts, List<Float> texs, List<Float> norms, List<Integer> idxs, int matIdx) {
            vertices = verts;
            texCoords = texs;
            normals = norms;
            indices = idxs;
            materialIndex = matIdx;
        }

        public LoadedMesh(DataInputStream stream) throws IOException {
            materialIndex = stream.readInt();
            int vertCount = stream.readInt();
            int indexCount = stream.readInt();

            int vertexCount = vertCount / 3;

            for (int v = 0; v < vertexCount * 3; v++) {
                vertices.add(stream.readFloat());
            }

            for (int t = 0; t < vertexCount * 2; t++) {
                texCoords.add(stream.readFloat());
            }

            for (int n = 0; n < vertexCount * 3; n++) {
                normals.add(stream.readFloat());
            }

            for (int j = 0; j < indexCount; j++) {
                indices.add(stream.readInt());
            }
        }

        public List<Float> getVertices() {
            return vertices;
        }

        public List<Float> getTexCoords() {
            return texCoords;
        }

        public List<Float> getNormals() {
            return normals;
        }

        public List<Integer> getIndices() {
            return indices;
        }

        public int getMaterialIndex() {
            return materialIndex;
        }
    }

    private List<LoadedMesh> meshes = new ArrayList<>();
    private List<Material> materials = new ArrayList<>();
    private String name;

    public LoadedModel(File path) {
        name = path.getName();
        name = name.substring(0, name.lastIndexOf("."));

        AIScene scene = aiImportFile(path.toString(), aiProcess_JoinIdenticalVertices | aiProcess_Triangulate | aiProcess_FixInfacingNormals | aiProcess_GenNormals | aiProcess_GenUVCoords);
        if (scene == null)
            throw new RuntimeException("Failed to load obj: " + path);

        int materialCount = scene.mNumMaterials();
        PointerBuffer aiMaterials = scene.mMaterials();
        for (int i = 0; i < materialCount; i++) {
            AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i));
            processMaterial(aiMaterial);
        }

        int meshCount = scene.mNumMeshes();
        PointerBuffer aiMeshes = scene.mMeshes();
        for (int i = 0; i < meshCount; i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            meshes.add(processMesh(aiMesh));
        }
    }

    public LoadedModel(DataInputStream stream) throws IOException {
        long begin = System.nanoTime();
        int nameLen = stream.readInt();
        name = Unpacker.unpackString(stream, nameLen);
        int materialCount = stream.readInt();
        for (int i = 0; i < materialCount; i++) {
            Vector3f am = readVector3f(stream);
            Vector3f df = readVector3f(stream);
            Vector3f sp = readVector3f(stream);
            float sh = stream.readFloat();
            int texLen = stream.readInt();
            String tex = Unpacker.unpackString(stream, texLen);
            materials.add(new Material(am, df, sp, sh, tex));
        }
        int meshCount = stream.readInt();
        for (int i = 0; i < meshCount; i++) {
            meshes.add(new LoadedMesh(stream));
        }
        long end = System.nanoTime();
        System.out.println("Took " + ((end - begin) / 1000000000.0) + " secs to load mesh " + name);
    }

    public void write(DataOutputStream stream) throws IOException {
        stream.writeBytes(HEADER);
        stream.writeInt(name.length());
        stream.writeBytes(name);
        stream.writeInt(materials.size());
        for (Material mat : materials) {
            writeVector3f(stream, mat.getAmbient());
            writeVector3f(stream, mat.getDiffuse());
            writeVector3f(stream, mat.getSpecular());
            stream.writeFloat(mat.getShininess());
            stream.writeInt(mat.getTextureName().length());
            stream.writeBytes(mat.getTextureName());
        }
        stream.writeInt(meshes.size());
        for (LoadedMesh mesh : meshes) {
            stream.writeInt(mesh.materialIndex);
            stream.writeInt(mesh.vertices.size());
            stream.writeInt(mesh.indices.size());

            for (Float v : mesh.vertices)
                stream.writeFloat(v);
            for (Float t : mesh.texCoords)
                stream.writeFloat(t);
            for (Float n : mesh.normals)
                stream.writeFloat(n);
            for (Integer i : mesh.indices)
                stream.writeInt(i);
        }
    }

    private void processMaterial(AIMaterial aiMaterial) {
        AIColor4D color = AIColor4D.create();

        AIString path = AIString.calloc();
        int result = Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, path, (IntBuffer) null, null, null, null, null, null);
        String name = "N/A";
        if (result == 0) {
            String texPath = path.dataString();
            name = texPath.substring(0, texPath.lastIndexOf("."));
        }

        Vector3f ambient = Material.DEFAULT_AMBIENT;
        result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_AMBIENT, aiTextureType_NONE, 0, color);
        if (result == 0)
            ambient = new Vector3f(color.r(), color.g(), color.b());

        Vector3f diffuse = Material.DEFAULT_DIFFUSE;
        result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, color);
        if (result == 0)
            diffuse = new Vector3f(color.r(), color.g(), color.b());

        Vector3f specular = Material.DEFAULT_SPECULAR;
        result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_SPECULAR, aiTextureType_NONE, 0, color);
        if (result == 0)
            specular = new Vector3f(color.r(), color.g(), color.b());

        float shininess = Material.DEFAULT_SHININESS;
        result = aiGetMaterialColor(aiMaterial, AI_MATKEY_SHININESS, aiTextureType_NONE, 0, color);
        if (result == 0)
            shininess = color.r();

        materials.add(new Material(ambient, diffuse, specular, shininess, name));
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

        if (textures.size() == 0) {
            int numElements = (vertices.size() / 3) * 2;
            for (int i = 0; i < numElements; i++)
                textures.add(0.0f);
        }

        int materialIdx = aiMesh.mMaterialIndex();

        return new LoadedMesh(vertices, textures, normals, indices, materialIdx);
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
        AIVector3D.Buffer textCoords = aiMesh.mTextureCoords(0);
        int numTextCoords = textCoords != null ? textCoords.remaining() : 0;
        for (int i = 0; i < numTextCoords; i++) {
            AIVector3D textCoord = textCoords.get();
            coords.add(textCoord.x());
            coords.add(1 - textCoord.y());
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

    private void writeVector3f(DataOutputStream stream, Vector3f v) throws IOException {
        stream.writeFloat(v.x);
        stream.writeFloat(v.y);
        stream.writeFloat(v.z);
    }

    private Vector3f readVector3f(DataInputStream stream) throws IOException {
        float x = stream.readFloat();
        float y = stream.readFloat();
        float z = stream.readFloat();
        return new Vector3f(x, y, z);
    }

    public String getName() { return name; }
    public List<LoadedMesh> getMeshes() { return meshes; }
    public List<Material> getMaterials() { return materials; }
}
