package world.generators

import block.BlockRegistration
import block.TilePalette
import block.TileState
import org.spongepowered.noise.Noise
import org.spongepowered.noise.NoiseQuality
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
        formation.seed = world.getSeed()
        formation.frequency = 0.125
        for (x in 0..15) {
            for (z in 0..15) {
                var nX = world.adjustChunk(cX, x) / 16.0
                var nZ = world.adjustChunk(cZ, z) / 16.0
                var maxHeight = 64
                var h = gen.getValue(nX, 0.0, nZ)
                h -= 0.75
                h *= 4
                maxHeight += (h * 64.0).toInt()
                maxHeight = max(32, min(maxHeight, 127))
                for (y in 0..maxHeight) {
                    var density = formation.getValue(nX, y.toDouble(), nZ)
                    density -= 0.85
                    var tile = TileState(BlockRegistration.AIR)
                    if(density >= 0)  {
                        tile = TileState(BlockRegistration.STONE)
                    }
                    world.setTileAtAdjusted(cX, cZ, x, y, z, TilePalette.getTileRepresentation(tile))
                }
                world.setTileAtAdjusted(cX, cZ, x, maxHeight, z, TilePalette.getTileRepresentation(TileState(BlockRegistration.GRASS)))
                /*for (y in -1 downTo -5) {
                    world.setTileAtAdjusted(cX, cZ, x, maxHeight + y, z, TilePalette.getTileRepresentation(TileState(BlockRegistration.DIRT)))
                }*/
            }
        }

        for (x in 0..15) {
            for (z in 0..15) {
                world.setTileAtAdjusted(cX, cZ, x, 0, z, TilePalette.getTileRepresentation(TileState(BlockRegistration.BORDERSTONE)))
            }
        }
    }

    override fun decorate(world: World, cX: Int, cZ: Int) {
        return
    }
}