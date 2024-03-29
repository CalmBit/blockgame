package blockgame.block;

import blockgame.render.gl.texture.UVPair;
import blockgame.render.world.RenderLayer;
import blockgame.util.registry.RegistryName;
import org.joml.Vector3f;

public class BlockLeaves extends Block {
    private LogType _type;
    private Vector3f _tint = new Vector3f(0.43f, 0.99f, 0.40f);

    public BlockLeaves(LogType type) {
        super(new RegistryName("blockgame", type.getTypeName()+"_leaves"));
        _type = type;
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    public RenderLayer renderLayer() {
        return RenderLayer.TRANSLUCENT;
    }

    @Override
    public UVPair getUVForFace(Direction face) {
        return _type.getLeaves();
    }

    public BlockLeaves setTint(Vector3f tint) {
        _tint = tint;
        return this;
    }

    @Override
    public boolean impartsTint() {
        return true;
    }

    @Override
    public Vector3f getTint(int cX, int cZ, int x, int y, int z) {
        return _tint;
    }
}
