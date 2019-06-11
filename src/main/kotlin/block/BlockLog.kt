package block

import gl.UVPair
import registry.RegistryName

class BlockLog(_name: RegistryName) : Block(_name) {
    companion object {
        var TOP: UVPair = UVPair(15, 0)
        var SIDE: UVPair = UVPair(14, 0)
    }

    override fun getUVForFace(face: EnumDirection): UVPair {
        return when (face) {
            EnumDirection.UP, EnumDirection.DOWN -> TOP
            else -> SIDE
        }
    }
}