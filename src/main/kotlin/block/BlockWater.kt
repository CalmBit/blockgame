package block

import registry.RegistryName

class BlockWater : Block(RegistryName("blockgame", "water")) {
    override fun isOpaque(): Boolean {
        return false
    }

    override fun renderLayer(): RenderType {
        return RenderType.TRANSLUCENT
    }
}