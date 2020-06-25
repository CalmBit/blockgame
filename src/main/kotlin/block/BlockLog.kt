package block

import gl.UVPair
import registry.RegistryName

class BlockLog(val type: LogType) : Block(RegistryName("blockgame", "${type.type_name}_log")) {

    override fun getUVForFace(face: EnumDirection): UVPair {
        return when (face) {
            EnumDirection.UP, EnumDirection.DOWN -> type.top
            else -> type.side
        }
    }
}