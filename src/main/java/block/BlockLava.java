package block;

import registry.RegistryName;

public class BlockLava extends Block {
    public BlockLava() {
        super(new RegistryName("blockgame", "lava"));
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    public float getEmittance() {
        return 1.0f;
    }
}
