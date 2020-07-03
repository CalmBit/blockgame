package blockgame.world.generators;

import blockgame.block.BlockRegistry;
import blockgame.block.TilePalette;
import blockgame.block.TileState;
import blockgame.world.World;
import blockgame.world.generators.decorator.DungeonDecorator;
import blockgame.world.generators.decorator.IDecorator;
import blockgame.world.generators.decorator.OreDecorator;
import blockgame.world.generators.decorator.TreeDecorator;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.noise.module.Module;
import org.spongepowered.noise.module.modifier.Exponent;
import org.spongepowered.noise.module.source.Perlin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class DensityGenerator implements IGenerator {
    private static final List<IDecorator> _decorators = new ArrayList<>();
    private static final Predicate<TileState> REPLACE_ONLY_STONE = (TileState t) -> t.block == BlockRegistry.STONE;
    private static final Predicate<TileState> REPLACE_NOT_WATER = (TileState t) -> t.block != BlockRegistry.WATER && t.block != BlockRegistry.AIR;
    private static final Predicate<TileState> STAY_ON_GRASS = (TileState t) -> t.block == BlockRegistry.GRASS;

    private static final TileState AIR = new TileState(BlockRegistry.AIR);
    private static final TileState GRASS = new TileState(BlockRegistry.GRASS);
    private static final TileState STONE = new TileState(BlockRegistry.STONE);
    private static final TileState BASALT = new TileState(BlockRegistry.BASALT);
    private static final TileState SAND = new TileState(BlockRegistry.SAND);
    private static final TileState WATER = new TileState(BlockRegistry.WATER);
    private static final TileState DIRT = new TileState(BlockRegistry.DIRT);
    private static final TileState LAVA = new TileState(BlockRegistry.LAVA);
    private static final TileState BORDERSTONE = new TileState(BlockRegistry.BORDERSTONE);

    public static final Perlin gen;
    public static final Perlin formation;
    public static final Exponent exp;
    public static final Module act_gen;
    public static final Exponent exp2;
    public static final Module act_form;

    static {
        gen = new Perlin();
        gen.setOctaveCount(6);
        gen.setFrequency(0.45);
        gen.setPersistence(0.375);
        gen.setLacunarity(0.2);
        exp = new Exponent();
        exp.setSourceModule(0, gen);
        exp.setExponent(8);
        act_gen = exp;
        formation = new Perlin();
        formation.setFrequency(0.4);
        formation.setPersistence(0.515);
        formation.setLacunarity(0.49);
        exp2 = new Exponent();
        exp2.setSourceModule(0, formation);
        exp2.setExponent(24);
        act_form = exp2;
    }

    public DensityGenerator() {
        _decorators.add(new TreeDecorator(
                new TileState(BlockRegistry.LOG),
                new TileState(BlockRegistry.LEAVES), 3, STAY_ON_GRASS));
        _decorators.add(new TreeDecorator(
                new TileState(BlockRegistry.BIRCH_LOG),
                new TileState(BlockRegistry.BIRCH_LEAVES), 1, STAY_ON_GRASS));
        _decorators.add(new OreDecorator(new TileState(BlockRegistry.COAL_ORE), 24, 12, 78, 20, REPLACE_ONLY_STONE ));
        _decorators.add(new OreDecorator(new TileState(BlockRegistry.IRON_ORE), 16, 4, 64, 12, REPLACE_ONLY_STONE));
        _decorators.add(new OreDecorator(new TileState(BlockRegistry.GOLD_ORE), 8, 4, 32, 8, REPLACE_ONLY_STONE));
        _decorators.add(new OreDecorator(new TileState(BlockRegistry.DIAMOND_ORE), 12, 4, 16, 8, REPLACE_ONLY_STONE));
        _decorators.add(new DungeonDecorator(2, 16, 32));
    }

    @Override
    public void generate(@NotNull World world, int cX, int cZ) {
        gen.setSeed(world.getSeed());
        formation.setSeed(world.getSeed() ^ 0xFACEBEEF);
        for (int x = 0;x < 16;x++) {
            for (int z = 0;z < 16;z++) {
                double nX = world.adjustChunk(cX, x);
                double nZ = world.adjustChunk(cZ, z);
                int maxHeight = 64;
                double h = act_gen.getValue(nX / 16.0, 0.0, nZ / 16.0);
                maxHeight += (int)(h * 64.0);
                maxHeight = Integer.max(32, Integer.min(maxHeight, 127));
                for (int y = 0;y <= maxHeight;y++) {
                    double density = act_form.getValue(nX / 8.0, y / 4.0, nZ / 8.0);
                    density -= ((64 - y) / 256.0);
                    TileState tile = AIR;
                    if(density >= 0.15)  {
                        if(y == maxHeight) {
                            tile = GRASS;
                        } else if(y >= maxHeight - 5 && y <= maxHeight - 1) {
                            tile = DIRT;

                        } else if(y >= 12){
                            tile = STONE;
                        } else if(y >= 0 && y <= 12) {
                            tile = BASALT;
                        }
                    } else {
                        if(y >= 0 && y <= 10) {
                            tile = LAVA;
                        }
                    }
                    world.setTileAtAdjusted(cX, cZ, x, y, z, TilePalette.getTileRepresentation(tile));
                }
                /*for (y in -1 downTo -5) {
                    blockgame.world.setTileAtAdjusted(cX, cZ, x, maxHeight + y, z, TilePalette.getTileRepresentation(TileState(BlockRegistry.DIRT)))
                }*/
            }
        }

        for (int x = 0;x < 16;x++) {
            for (int z = 0;z < 16;z++) {
                world.setTileAtAdjusted(cX, cZ, x, 0, z, TilePalette.getTileRepresentation(BORDERSTONE));
            }
        }
    }

    @Override
    public void decorate(@NotNull World world, int cX, int cZ) {
        world.random = new Random(world.getSeed());
        world.random = new Random(world.getSeed()
                + (world.random.nextInt(Integer.MAX_VALUE) * cX)
                + (world.random.nextInt(Integer.MAX_VALUE) * cZ) ^ world.getSeed());
        for (IDecorator d : _decorators) {
            d.decorate(world, cX, cZ);
        }
    }
}
