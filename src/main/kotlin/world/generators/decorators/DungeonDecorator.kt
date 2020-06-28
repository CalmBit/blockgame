package world.generators.decorators

import block.BlockRegistry
import block.TilePalette
import block.TileState
import world.World
import kotlin.random.Random

class DungeonDecorator(val chances: Int, val minY: Int, val maxY: Int) : IDecorator {
    override fun decorate(world: World, cX: Int, cZ: Int) {
        world.random = Random(world.getSeed())
        world.random = Random(world.getSeed()
                + (world.random.nextInt(Int.MAX_VALUE) * cX)
                + (world.random.nextInt(Int.MAX_VALUE) * cZ) xor world.getSeed())
        for (c in 0 until chances) {
            var x = world.random.nextInt(8, 16)
            var y = world.random.nextInt(0, 128)
            var z = +world.random.nextInt(8, 16)

            if(y < minY || y > maxY) continue

            val r = world.random.nextInt(128)
            if (r > 4) {
                continue
            }

            if(world.getTileAtAdjusted(cX, cZ, x, y, z) == null) {
                world.getTileAtAdjusted(cX, cZ, x, y, z)
            }

            var len = if(world.random.nextBoolean()) 6 else 8
            var wid = if(world.random.nextBoolean()) 6 else 8

            for(i in 0..len) {
                for(j in 0 until 5) {
                    for(k in 0..wid) {
                        if(i==0||i==len||j==0||j==4||k==0||k==wid)
                            if(j==0)
                                world.setTileAtAdjusted(cX, cZ, x+i, y+j, z+k, TilePalette.getTileRepresentation(if (world.random.nextInt(4) <= 2) TileState(
                                    BlockRegistry.MOSS_COBBLE
                                ) else TileState(BlockRegistry.COBBLESTONE)
                                ))
                            else
                                world.setTileAtAdjusted(cX, cZ, x+i, y+j, z+k, TilePalette.getTileRepresentation(
                                    TileState(BlockRegistry.COBBLESTONE)
                                ))
                        else
                            world.setTileAtAdjusted(cX, cZ, x+i, y+j, z+k, TilePalette.getTileRepresentation(
                                TileState(BlockRegistry.AIR)
                            ))
                    }
                }
            }

            world.setTileAtAdjusted(cX, cZ, x+(len/2), y+1, z+(wid/2), TilePalette.getTileRepresentation(
                TileState(BlockRegistry.MONSTER_SPAWNER)
            ))
        }
    }
}