package renderer;

public class Mesh {
    private final int vao;
    private int count;

    public Mesh(int vao, int count) {
        this.vao = vao;
        this.count = count;
    }

    public int getVertexArray() {
        return vao;
    }

    public int getCount() {
        return count;
    }
}
