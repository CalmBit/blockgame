package blockgame.world.generators;

import blockgame.block.BlockRegistry;
import blockgame.block.TilePalette;
import blockgame.block.TileState;
import org.spongepowered.noise.Noise;
import org.spongepowered.noise.NoiseQuality;
import blockgame.world.World;
import blockgame.world.generators.decorator.DungeonDecorator;
import blockgame.world.generators.decorator.IDecorator;
import blockgame.world.generators.decorator.OreDecorator;
import blockgame.world.generators.decorator.TreeDecorator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class HellGenerator implements IGenerator {
        private static final List<IDecorator> _decorators = new ArrayList<>();
        private static final Predicate<TileState> REPLACE_ONLY_STONE = (TileState t) -> t.block == BlockRegistry.STONE;
        private static final Predicate<TileState> REPLACE_NOT_WATER = (TileState t) -> t.block != BlockRegistry.WATER && t.block != BlockRegistry.AIR;
        private static final Predicate<TileState> STAY_ON_DIRT = (TileState t) -> t.block == BlockRegistry.DIRT;
        private static final TileState AIR = new TileState(BlockRegistry.AIR);
        private static final TileState GRASS = new TileState(BlockRegistry.GRASS);
        private static final TileState STONE = new TileState(BlockRegistry.STONE);
        private static final TileState OBSIDIAN = new TileState(BlockRegistry.OBSIDIAN);
        private static final TileState LAVA_OBSIDIAN = new TileState(BlockRegistry.LAVA_OBSIDIAN);
        private static final TileState LAVA = new TileState(BlockRegistry.LAVA);
        private static final TileState DIRT = new TileState(BlockRegistry.DIRT);
        private static final TileState BORDERSTONE = new TileState(BlockRegistry.BORDERSTONE);

        public HellGenerator() {
            _decorators.add(new TreeDecorator(
                    new TileState(BlockRegistry.LOG),
                    new TileState(BlockRegistry.AIR), 4, STAY_ON_DIRT));
            _decorators.add(new OreDecorator(new TileState(BlockRegistry.COAL_ORE), 12, 12, 78, 20, REPLACE_ONLY_STONE ));
            _decorators.add(new OreDecorator(new TileState(BlockRegistry.IRON_ORE), 8, 4, 64, 12, REPLACE_ONLY_STONE));
            _decorators.add(new OreDecorator(new TileState(BlockRegistry.GOLD_ORE), 4, 4, 32, 8, REPLACE_ONLY_STONE));
            _decorators.add(new OreDecorator(new TileState(BlockRegistry.DIAMOND_ORE), 6, 4, 16, 8, REPLACE_ONLY_STONE));
            _decorators.add(new OreDecorator(new TileState(BlockRegistry.AIR), 6, 4, 64, 64, REPLACE_ONLY_STONE));
            _decorators.add(new DungeonDecorator(2, 16, 32));
        }

    public void generate(World world, int cX, int cZ) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 128; y++) {
                    int height = 32;
                    double nX = world.adjustChunk(cX, x) / 16.0;
                    double nZ = world.adjustChunk(cZ, z) / 16.0;
                    double h = 1.25 * Noise.valueCoherentNoise3D(nX / 4, 0.0, nZ / 4, world.getSeed(), NoiseQuality.FAST);
                    h += 0.25 * Noise.valueCoherentNoise3D(nX, 0.0, nZ, world.getSeed(), NoiseQuality.FAST);
                    h += 0.05 * Noise.valueCoherentNoise3D(nX*4, 0.0, nZ*4, world.getSeed(), NoiseQuality.FAST);
                    h = h-0.45;
                    h = Math.pow(h, 2.0);
                    height += (int)(h * 127.0);
                    height = Math.max(32, Math.min(height, 127));
                    boolean sandy = height < 54;
                    boolean water = height < 48;
                    TileState tile = AIR;
                    if (water) {
                        if(y >= 49 && y <= 127) {
                            tile = AIR;
                        } else if(y >= height && y <= 48) {
                            tile = LAVA;
                        } else if(y >= height-5 && y <= height-1) {
                            tile = LAVA_OBSIDIAN;
                        } else {
                            tile = STONE;
                        }
                    } else {
                        if(y >= height+1 && y <= 127) {
                            tile = AIR;
                        } else if(y >= height-5 && y <= height) {
                            if(y == 48 || y == 49) {
                                tile = LAVA_OBSIDIAN;
                            } else if(sandy)
                                tile = OBSIDIAN;
                            else
                                tile = DIRT;
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
        world.random = new Random(world.getSeed());
        world.random = new Random(world.getSeed()
                + (world.random.nextInt(Integer.MAX_VALUE) * cX)
                + (world.random.nextInt(Integer.MAX_VALUE) * cZ) ^ world.getSeed());
        for (IDecorator d : _decorators) {
            d.decorate(world, cX, cZ);
        }
    }
}
