package blockgame.block;

import blockgame.gl.UVPair;
import blockgame.registry.RegistryName;

public class BlockGrass extends Block {
    private static final UVPair TOP = new UVPair(2, 0);
    private static final UVPair BOT = new UVPair(0, 0);
    private static final UVPair SIDE = new UVPair(1, 0);

    public BlockGrass() {
        super(new RegistryName("blockgame", "grass"));
    }

    @Override
    public UVPair getUVForFace(EnumDirection face) {
        switch(face) {
            case UP:
                return TOP;
            case DOWN:
                return BOT;
            default:
                return SIDE;
        }
    }
}
