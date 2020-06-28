package block;

import registry.RegistryName;

public class BlockAir extends Block {
    public BlockAir() {
        super(new RegistryName("blockgame", "air"));
    }

    @Override
    public boolean shouldRender() {
        return false;
    }
}
