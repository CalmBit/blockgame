package block

import registry.RegistryName

class BlockLeaves(_name: RegistryName) : Block(_name) {
    override fun isOpaque(): Boolean {
        return false
    }
}