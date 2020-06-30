package world.generators;

import block.BlockRegistry;
import block.TilePalette;
import block.TileState;
import org.spongepowered.noise.Noise;
import org.spongepowered.noise.NoiseQuality;
import world.World;
import world.generators.IGenerator;
import world.generators.decorator.IDecorator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class DefaultGenerator implements IGenerator {
        private static final List<IDecorator> _decorators = new ArrayList<>();
        private static final Predicate<TileState> REPLACE_ONLY_STONE = (TileState t) -> t.block == BlockRegistry.STONE;
        private static final Predicate<TileState> REPLACE_NOT_WATER = (TileState t) -> t.block != BlockRegistry.WATER && t.block != BlockRegistry.AIR;
        private static final Predicate<TileState> STAY_ON_GRASS = (TileState t) -> t.block != BlockRegistry.GRASS;
        private static final TileState AIR = new TileState(BlockRegistry.AIR);
        private static final TileState GRASS = new TileState(BlockRegistry.GRASS);
        private static final TileState STONE = new TileState(BlockRegistry.STONE);
        private static final TileState SAND = new TileState(BlockRegistry.SAND);
        private static final TileState WATER = new TileState(BlockRegistry.WATER);
        private static final TileState DIRT = new TileState(BlockRegistry.DIRT);
        private static final TileState BORDERSTONE = new TileState(BlockRegistry.BORDERSTONE);

        public DefaultGenerator() {
            /*_decorators.add(TreeDecorator(
                    TileState(BlockRegistry.LOG),
                    TileState(BlockRegistry.LEAVES), 3, STAY_ON_GRASS))
            _decorators.add(TreeDecorator(
                    TileState(BlockRegistry.BIRCH_LOG),
                    TileState(BlockRegistry.BIRCH_LEAVES), 1, STAY_ON_GRASS))
            _decorators.add(OreDecorator(TileState(BlockRegistry.COAL_ORE), 12, 12, 78, 20, REPLACE_ONLY_STONE ))
            _decorators.add(OreDecorator(TileState(BlockRegistry.IRON_ORE), 8, 4, 64, 12, REPLACE_ONLY_STONE))
            _decorators.add(OreDecorator(TileState(BlockRegistry.GOLD_ORE), 4, 4, 32, 8, REPLACE_ONLY_STONE))
            _decorators.add(OreDecorator(TileState(BlockRegistry.DIAMOND_ORE), 6, 4, 16, 8, REPLACE_ONLY_STONE))
            _decorators.add(OreDecorator(TileState(BlockRegistry.AIR), 6, 4, 64, 64, REPLACE_ONLY_STONE))
            _decorators.add(DungeonDecorator(2, 16, 32))*/
        }

    public void generate(World world, int cX, int cZ) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 128; y++) {
                    int height = 64;
                    double nX = world.adjustChunk(cX, x) / 16.0;
                    double nZ = world.adjustChunk(cZ, z) / 16.0;
                    double h = Noise.valueCoherentNoise3D(nX / 4, 0.0, nZ / 4, world.getSeed(), NoiseQuality.FAST);
                    h += 0.25 * Noise.valueCoherentNoise3D(nX / 2, 0.0, nZ / 2, world.getSeed(), NoiseQuality.FAST);
                    h += 0.125 * Noise.valueCoherentNoise3D(nX, 0.0, nZ, world.getSeed(), NoiseQuality.FAST);
                    h += 0.0625 * Noise.valueCoherentNoise3D(nX*2, 0.0, nZ*2, world.getSeed(), NoiseQuality.FAST);
                    h = Math.max(0.0, Math.min(1.0, h));
                    h = h-0.5;
                    height += (int)(h * 64.0);
                    height = Math.max(32, Math.min(height, 127));
                    boolean sandy = height < 70;
                    boolean water = height < 64;
                    TileState tile = AIR;
                    if (water) {
                        if(y >= 65 && y <= 127) {
                            tile = AIR;
                        } else if(y >= height && y <= 64) {
                            tile = WATER;
                        } else if(y >= height-5 && y <= height-1) {
                            tile = SAND;
                        } else {
                            tile = STONE;
                        }
                    } else {
                        if(y >= height+1 && y <= 127) {
                            tile = AIR;
                        } else if(y == height) {
                            if(sandy) tile = SAND;
                            else tile = GRASS;
                        } else if(y >= height-5 && y <= height-1) {
                            if(sandy) tile = SAND ;
                            else tile = DIRT;
                        } else {
                            tile = STONE;
                        }
                    }
                    world.setTileAtAdjusted(cX, cZ, x, y, z, TilePalette.getTileRepresentation(tile));
                }
            }
        }

        for (int x = 0;x < 16;x++) {
            for (int z = 0;z < 16;z++) {
                world.setTileAtAdjusted(cX, cZ, x, 0, z, TilePalette.getTileRepresentation(
                        BORDERSTONE
                ));
            }
        }
    }

    @Override
    public void decorate(World world, int cX, int cZ) {
        /*world.random = new Random(world.getSeed())
        world.random = new Random(world.getSeed()
                + (world.random.nextInt(Int.MAX_VALUE) * cX)
                + (world.random.nextInt(Int.MAX_VALUE) * cZ) xor world.getSeed())
        for (d in _decorators) {
            d.decorate(world, cX, cZ)
        }*/
    }
}
