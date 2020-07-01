package blockgame.block;

import blockgame.registry.RegistryName;

public class BlockGlass extends Block {
    public BlockGlass() {
        super(new RegistryName("blockgame", "glass"));
    }

    @Override
    public boolean isOpaque() {
        return false;
    }
}
