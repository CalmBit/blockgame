package world.generators

import block.BlockRegistration
import block.TilePalette
import block.TileState
import org.spongepowered.noise.Noise
import org.spongepowered.noise.NoiseQuality
import world.Chunk
import world.World
import world.generators.decorators.DungeonDecorator
import world.generators.decorators.IDecorator
import world.generators.decorators.OreDecorator
import world.generators.decorators.TreeDecorator
import kotlin.random.Random


class DefaultGenerator : IGenerator {

    companion object {
        var _decorators : MutableList<IDecorator> = mutableListOf()
        val REPLACE_ONLY_STONE = {t: TileState -> t.block == BlockRegistration.STONE}
        val REPLACE_NOT_WATER = {t: TileState -> t.block != BlockRegistration.WATER && t.block != BlockRegistration.AIR}
        val STAY_ON_GRASS = {t: TileState -> t.block != BlockRegistration.GRASS}
        init {
            _decorators.add(TreeDecorator(TileState(BlockRegistration.LOG), TileState(BlockRegistration.LEAVES), 3, STAY_ON_GRASS))
            _decorators.add(TreeDecorator(TileState(BlockRegistration.BIRCH_LOG), TileState(BlockRegistration.BIRCH_LEAVES), 1, STAY_ON_GRASS))
            _decorators.add(OreDecorator(TileState(BlockRegistration.COAL_ORE), 12, 12, 78, 20, REPLACE_ONLY_STONE ))
            _decorators.add(OreDecorator(TileState(BlockRegistration.IRON_ORE), 8, 4, 64, 12, REPLACE_ONLY_STONE))
            _decorators.add(OreDecorator(TileState(BlockRegistration.GOLD_ORE), 4, 4, 32, 8, REPLACE_ONLY_STONE))
            _decorators.add(OreDecorator(TileState(BlockRegistration.DIAMOND_ORE), 6, 4, 16, 8, REPLACE_ONLY_STONE))
            _decorators.add(OreDecorator(TileState(BlockRegistration.AIR), 6, 4, 64, 64, REPLACE_ONLY_STONE))
            _decorators.add(DungeonDecorator(2, 16, 32))
        }
    }

    override fun generate(world: World, cX: Int, cZ: Int) {
        for (x in 0..15) {
            for (z in 0..15) {
                for (y in 0..127) {
                    var height = 64
                    var nX = world.adjustChunk(cX, x) / 16.0
                    var nZ = world.adjustChunk(cZ, z) / 16.0
                    var h = Noise.valueCoherentNoise3D(nX / 4, 0.0, nZ / 4, world.getSeed(), NoiseQuality.FAST)
                    h += 0.25 * Noise.valueCoherentNoise3D(nX / 2, 0.0, nZ / 2, world.getSeed(), NoiseQuality.FAST)
                    h += 0.125 * Noise.valueCoherentNoise3D(nX, 0.0, nZ, world.getSeed(), NoiseQuality.FAST)
                    h += 0.0625 * Noise.valueCoherentNoise3D(nX*2, 0.0, nZ*2, world.getSeed(), NoiseQuality.FAST)
                    h = Math.max(0.0, Math.min(1.0, h))
                    h = h-0.5
                    height += (h * 64.0).toInt()
                    height = Math.max(32, Math.min(height, 127))
                    var sandy = height < 70
                    var water = height < 64
                    var tile = TileState(BlockRegistration.AIR)
                    if (water) {
                        tile =
                            when (y) {
                                in 65..127 -> tile
                                in height..64 -> TileState(BlockRegistration.WATER)
                                in height-5 until height -> TileState(BlockRegistration.SAND)
                                else -> TileState(BlockRegistration.STONE)
                            }
                    } else {
                        tile =
                            when (y) {
                                in height + 1..127 -> tile
                                height -> if(sandy) TileState(BlockRegistration.SAND) else TileState(BlockRegistration.GRASS)
                                in height - 5 until height -> if(sandy) TileState(BlockRegistration.SAND) else TileState(
                                    BlockRegistration.DIRT)
                                else -> {
                                    TileState(BlockRegistration.STONE)
                                }
                            }
                    }
                    world.setTileAtAdjusted(cX, cZ, x, y, z, TilePalette.getTileRepresentation(tile))
                }
            }
        }

        for (x in 0..15) {
            for (z in 0..15) {
                world.setTileAtAdjusted(cX, cZ, x, 0, z, TilePalette.getTileRepresentation(TileState(BlockRegistration.BORDERSTONE)))
            }
        }
    }

    override fun decorate(world: World, cX: Int, cZ: Int) {
        world.random = Random(world.getSeed())
        world.random = Random(world.getSeed()
                + (world.random.nextInt(Int.MAX_VALUE) * cX)
                + (world.random.nextInt(Int.MAX_VALUE) * cZ) xor world.getSeed())
        for (d in _decorators) {
            d.decorate(world, cX, cZ)
        }
    }
}