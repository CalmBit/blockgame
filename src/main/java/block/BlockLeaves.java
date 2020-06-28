package block;

import gl.UVPair;
import registry.RegistryName;

public class BlockLeaves extends Block {
    private LogType _type;

    public BlockLeaves(LogType type) {
        super(new RegistryName("blockgame", type.getTypeName()+"_leaves"));
        _type = type;
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    public EnumRenderLayer renderLayer() {
        return EnumRenderLayer.TRANSLUCENT;
    }

    @Override
    public UVPair getUVForFace(EnumDirection face) {
        return _type.getLeaves();
    }
}
