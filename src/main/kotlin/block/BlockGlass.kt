package block

import registry.RegistryName

class BlockGlass(_name: RegistryName): Block(_name) {
    override fun isOpaque(): Boolean {
        return false
    }
}
