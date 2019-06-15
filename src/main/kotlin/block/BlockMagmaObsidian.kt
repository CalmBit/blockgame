package block

import registry.RegistryName

class BlockMagmaObsidian : Block(RegistryName("blockgame", "magma_obsidian")) {
    override fun getEmittance(): Float {
        return 0.35f
    }
}