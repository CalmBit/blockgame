package block;

import registry.RegistryName;

public class BlockMagmaObsidian extends Block {
    public BlockMagmaObsidian() {
        super(new RegistryName("blockgame", "magma_obsidian"));
    }

    @Override
    public float getEmittance() {
        return 0.35f;
    }
}
