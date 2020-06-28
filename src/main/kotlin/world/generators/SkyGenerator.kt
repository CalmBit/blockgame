package world.generators

import block.BlockRegistry
import block.TilePalette
import block.TileState
import org.spongepowered.noise.Noise
import org.spongepowered.noise.NoiseQuality
import world.World
import world.generators.decorators.IDecorator
import world.generators.decorators.OreDecorator
import world.generators.decorators.TreeDecorator


class SkyGenerator : IGenerator {

    companion object {
        var _decorators : MutableList<IDecorator> = mutableListOf()
        val REPLACE_ONLY_STONE = {t: TileState -> t.block == BlockRegistry.STONE}
        val REPLACE_NOT_WATER = {t: TileState -> t.block != BlockRegistry.WATER && t.block != BlockRegistry.AIR}
        val STAY_ON_GRASS = {t: TileState -> t.block != BlockRegistry.GRASS}
        init {
            _decorators.add(TreeDecorator(
                TileState(BlockRegistry.LOG),
                TileState(BlockRegistry.LEAVES), 4, STAY_ON_GRASS))
            _decorators.add(OreDecorator(TileState(BlockRegistry.COAL_ORE), 12, 12, 78, 20, REPLACE_ONLY_STONE ))
            _decorators.add(OreDecorator(TileState(BlockRegistry.IRON_ORE), 8, 4, 64, 12, REPLACE_ONLY_STONE))
            _decorators.add(OreDecorator(TileState(BlockRegistry.GOLD_ORE), 4, 4, 32, 8, REPLACE_ONLY_STONE))
            _decorators.add(OreDecorator(TileState(BlockRegistry.DIAMOND_ORE), 6, 4, 16, 8, REPLACE_ONLY_STONE))
        }
    }

    override fun generate(world: World, cX: Int, cZ: Int) {
        for (x in 0..15) {
            for (z in 0..15) {
                for (y in 0..127) {
                    var height = 64
                    var nX = ((cX * 16.0) + x) / 16.0
                    var nZ = ((cZ * 16.0) + z) / 16.0
                    var h = Noise.valueCoherentNoise3D(nX / 4, 0.0, nZ / 4, world.getSeed(), NoiseQuality.BEST)
                    h += 0.25 * Noise.valueCoherentNoise3D(nX / 2, 0.0, nZ / 2, world.getSeed(), NoiseQuality.BEST)
                    h += 0.125 * Noise.valueCoherentNoise3D(nX, 0.0, nZ, world.getSeed(), NoiseQuality.BEST)
                    h = Math.max(0.0, Math.min(1.0, h))
                    h = h-0.65
                    val hco = (h * 64.0).toInt()
                    height += hco
                    height = Math.max(32, Math.min(height, 127))
                    var air = height < 64
                    var tile = TileState(BlockRegistry.AIR)
                    if (air) {
                        tile = TileState(BlockRegistry.AIR)
                    } else {
                        tile =
                            when (y) {
                                in height + 1..127 -> tile
                                height -> TileState(BlockRegistry.GRASS)
                                in height - 5 until height -> TileState(BlockRegistry.DIRT)
                                in 0..(48 - (hco)) -> TileState(BlockRegistry.AIR)
                                else -> {
                                    TileState(BlockRegistry.STONE)
                                }
                            }
                    }
                    world.setTileAtAdjusted(cX, cZ, x, y, z, TilePalette.getTileRepresentation(tile))
                }
            }
        }
    }

    override fun decorate(world: World, cX: Int, cZ: Int) {
        for (d in _decorators) {
            d.decorate(world, cX, cZ)
        }
    }
}