package block

import registry.RegistryName
import world.World

class BlockDirt(_name: RegistryName) : Block(_name) {
    override fun tick(world: World, rX: Int, rY: Int, rZ: Int, x: Int, y: Int, z: Int) {
        if(world.getTileAtAdjusted(rX, rZ, x, (rY shl 4) + y + 1, z)!!.block == BlockRegistration.AIR) {
            world.setTileAtAdjusted(rX, rZ, x, (rY shl 4) + y, z, TilePalette.getTileRepresentation(TileState(BlockRegistration.GRASS)))
        }
    }
}