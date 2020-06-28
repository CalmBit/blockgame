package block;

import gl.UVPair;
import registry.RegistryName;

public class BlockLog extends Block {
    private LogType _type;

    public BlockLog(LogType type) {
        super(new RegistryName("blockgame", type.getTypeName()+"_log"));
        _type = type;
    }

    @Override
    public UVPair getUVForFace(EnumDirection face) {
        switch(face) {
            case UP:
            case DOWN:
                return _type.getTop();
            default:
                return _type.getSide();
        }
    }
}
