package blockgame.render.gl.texture;

public class UVPair {
    public int u;
    public int v;

    public static final UVPair MISSING_UV = new UVPair(15, 15);

    public UVPair(int u, int v) {
        this.u = u;
        this.v = v;
    }
}
