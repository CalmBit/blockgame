package block

import gl.UVPair
import registry.RegistryName

class BlockLeaves(val type: LogType) : Block( RegistryName("blockgame", "${type.name}_leaves")) {
    override fun isOpaque(): Boolean {
        return false
    }

    override fun getUVForFace(face: EnumDirection): UVPair {
        return type.leaves
    }
}