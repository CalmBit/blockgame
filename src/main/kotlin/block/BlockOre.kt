package block

import registry.RegistryName
import world.World

class BlockOre(_name: RegistryName) : Block(_name) {

    override fun shouldRenderFace(
        world: World,
        cX: Int,
        cZ: Int,
        rY: Int,
        x: Int,
        y: Int,
        z: Int,
        face: EnumDirection
    ): Boolean {
        return super.shouldRenderFace(world, cX, cZ, rY, x, y, z, face)
    }
}