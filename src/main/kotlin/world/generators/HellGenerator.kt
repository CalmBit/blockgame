package world.generators

import block.BlockRegistry
import block.TilePalette
import block.TileState
import org.spongepowered.noise.Noise
import org.spongepowered.noise.NoiseQuality
import world.World
import world.generators.decorator.IDecorator
import world.generators.decorator.OreDecorator
import world.generators.decorator.TreeDecorator


class HellGenerator : IGenerator {

    companion object {
        var _decorators : MutableList<IDecorator> = mutableListOf()
        val REPLACE_ONLY_STONE = {t: TileState -> t.block == BlockRegistry.STONE}
        val REPLACE_NOT_WATER = {t: TileState -> t.block != BlockRegistry.WATER && t.block != BlockRegistry.AIR}
        val STAY_ON_DIRT = {t: TileState -> t.block != BlockRegistry.DIRT}
        init {
            _decorators.add(TreeDecorator(
                TileState(BlockRegistry.LOG),
                TileState(BlockRegistry.AIR), 4, STAY_ON_DIRT))
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
                    var height = 32
                    var nX = ((cX * 16.0) + x) / 16.0
                    var nZ = ((cZ * 16.0) + z) / 16.0
                    var h = 1.25 * Noise.valueCoherentNoise3D(nX / 4, 0.0, nZ / 4, world.getSeed(), NoiseQuality.BEST)
                    h += 0.25 * Noise.valueCoherentNoise3D(nX, 0.0, nZ, world.getSeed(), NoiseQuality.BEST)
                    h += 0.05 * Noise.valueCoherentNoise3D(nX*4, 0.0, nZ*4, world.getSeed(), NoiseQuality.BEST)
                    h = h-0.45
                    h = Math.pow(h, 2.0)
                    height += (h * 127.0).toInt()
                    height = Math.max(32, Math.min(height, 127))
                    var sandy = height < 54
                    var water = height < 48
                    var tile = TileState(BlockRegistry.AIR)
                    if (water) {
                        tile =
                            when (y) {
                                in 49..127 -> tile
                                in height..48 -> TileState(BlockRegistry.LAVA)
                                in height-5 until height -> TileState(BlockRegistry.LAVA_OBSIDIAN)
                                else -> TileState(BlockRegistry.STONE)
                            }
                    } else {
                        tile =
                            when (y) {
                                in height + 1..127 -> tile
                                height -> if(height in 48..49) TileState((BlockRegistry.LAVA_OBSIDIAN))
                                    else if(sandy) TileState(BlockRegistry.OBSIDIAN)
                                    else TileState(BlockRegistry.DIRT)
                                in height - 5 until height -> if(y in 48..49) TileState((BlockRegistry.LAVA_OBSIDIAN))
                                    else if(sandy) TileState(BlockRegistry.OBSIDIAN)
                                    else TileState(BlockRegistry.DIRT)
                                else -> {
                                    TileState(BlockRegistry.STONE)
                                }
                            }
                    }
                    world.setTileAtAdjusted(cX, cZ, x, y, z, TilePalette.getTileRepresentation(tile))
                }
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
        for (d in _decorators) {
            d.decorate(world, cX, cZ)
        }
    }
}