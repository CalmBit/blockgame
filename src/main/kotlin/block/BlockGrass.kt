package block

import gl.UVPair
import registry.RegistryName

class BlockGrass(_name: RegistryName) : Block(_name) {
    companion object {
        val TOP = UVPair(2, 0)
        val BOT = UVPair(0, 0)
        val SIDE = UVPair(1, 0)
    }

    override fun getUVForFace(face: EnumDirection): UVPair {
        return when (face) {
            EnumDirection.UP -> TOP
            EnumDirection.DOWN -> BOT
            else -> SIDE
        }
    }
}