package block

import registry.RegistryName

class BlockLava() : Block(RegistryName("blockgame", "lava")) {
    override fun isOpaque(): Boolean {
        return false
    }
}