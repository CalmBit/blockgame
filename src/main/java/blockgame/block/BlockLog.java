package blockgame.block;

import blockgame.render.gl.texture.UVPair;
import blockgame.util.registry.RegistryName;

public class BlockLog extends Block {
    private LogType _type;

    public BlockLog(LogType type) {
        super(new RegistryName("blockgame", type.getTypeName()+"_log"));
        _type = type;
    }

    @Override
    public UVPair getUVForFace(Direction face) {
        switch(face) {
            case UP:
            case DOWN:
                return _type.getTop();
            default:
                return _type.getSide();
        }
    }
}
