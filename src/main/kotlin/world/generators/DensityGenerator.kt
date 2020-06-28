package world.generators

import block.BlockRegistry
import block.TilePalette
import block.TileState
import org.spongepowered.noise.module.source.Perlin
import world.World
import kotlin.math.max
import kotlin.math.min

class DensityGenerator : IGenerator {
    override fun generate(world: World, cX: Int, cZ: Int) {
        var gen = Perlin()
        var formation = Perlin()
        gen.frequency = 0.25
        gen.persistence = 0.35
        gen.seed = world.getSeed()
        formation.seed = world.getSeed().xor(0xFACEBEEF.toInt())
        formation.frequency = 0.125
        for (x in 0..15) {
            for (z in 0..15) {
                var nX = world.adjustChunk(cX, x) / 16.0
                var nZ = world.adjustChunk(cZ, z) / 16.0
                var maxHeight = 64
                var h = gen.getValue(nX, 0.0, nZ)
                h -= 0.75
                h *= 2.5
                maxHeight += (h * 64.0).toInt()
                maxHeight = max(32, min(maxHeight, 127))
                var avg = 0.0
                for (y in 0..maxHeight) {
                    //var density = formation.getValue(nX/8.0, y.toDouble(), nZ/8.0)
                    //density += ((64 - maxHeight) / 32.0)
                    //density -= 0.75
                    //avg += density
                    var tile = TileState(BlockRegistry.AIR)
                    //if(density >= 0)  {
                        if(y == maxHeight) {
                            tile = TileState(BlockRegistry.GRASS)
                        } else if(y < maxHeight) {
                            tile = TileState(BlockRegistry.MOSS_COBBLE)
                        }
                    //}
                    world.setTileAtAdjusted(cX, cZ, x, y, z, TilePalette.getTileRepresentation(tile))
                }
                //System.out.println(avg)
                /*for (y in -1 downTo -5) {
                    world.setTileAtAdjusted(cX, cZ, x, maxHeight + y, z, TilePalette.getTileRepresentation(TileState(BlockRegistry.DIRT)))
                }*/
            }
        }

        for (x in 0..15) {
            for (z in 0..15) {
                world.setTileAtAdjusted(cX, cZ, x, 0, z, TilePalette.getTileRepresentation(
                    TileState(BlockRegistry.BORDERSTONE)
                ))
            }
        }
    }

    override fun decorate(world: World, cX: Int, cZ: Int) {
        return
    }
}