package block

import registry.RegistryName

class BlockAir : Block(RegistryName("blockgame", "air")) {
    override fun shouldRender(): Boolean = false
}