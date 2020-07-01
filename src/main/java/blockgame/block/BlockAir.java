package blockgame.block;

import blockgame.registry.RegistryName;

public class BlockAir extends Block {
    public BlockAir() {
        super(new RegistryName("blockgame", "air"));
    }

    @Override
    public boolean shouldRender() {
        return false;
    }
}
