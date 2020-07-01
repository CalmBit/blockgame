package blockgame.world.generators;

import blockgame.block.BlockRegistry;
import blockgame.block.TilePalette;
import blockgame.block.TileState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.noise.module.source.Perlin;
import blockgame.world.World;

public class DensityGenerator implements IGenerator {
    private static final TileState AIR = new TileState(BlockRegistry.AIR);
    private static final TileState GRASS = new TileState(BlockRegistry.GRASS);
    private static final TileState STONE = new TileState(BlockRegistry.STONE);
    private static final TileState SAND = new TileState(BlockRegistry.SAND);
    private static final TileState WATER = new TileState(BlockRegistry.WATER);
    private static final TileState DIRT = new TileState(BlockRegistry.DIRT);
    private static final TileState BORDERSTONE = new TileState(BlockRegistry.BORDERSTONE);

    @Override
    public void generate(@NotNull World world, int cX, int cZ) {
        Perlin gen = new Perlin();
        Perlin formation = new Perlin();
        gen.setFrequency(0.25);
        gen.setPersistence(0.35);
        gen.setSeed(world.getSeed());
        formation.setSeed(world.getSeed() ^ 0xFACEBEEF);
        formation.setFrequency(0.125);
        for (int x = 0;x < 16;x++) {
            for (int z = 0;z < 16;z++) {
                double nX = world.adjustChunk(cX, x) / 16.0;
                double nZ = world.adjustChunk(cZ, z) / 16.0;
                int maxHeight = 64;
                double h = gen.getValue(nX, 0.0, nZ);
                h -= 0.75;
                h *= 2.5;
                maxHeight += (int)(h * 64.0);
                maxHeight = Integer.max(32, Integer.min(maxHeight, 127));
                double avg = 0.0;
                for (int y = 0;y <= maxHeight;y++) {
                    //var density = formation.getValue(nX/8.0, y.toDouble(), nZ/8.0)
                    //density += ((64 - maxHeight) / 32.0)
                    //density -= 0.75
                    //avg += density
                    TileState tile = AIR;
                    //if(density >= 0)  {
                    if(y == maxHeight) {
                        tile = GRASS;
                    } else {
                        tile = STONE;
                    }
                    //}
                    world.setTileAtAdjusted(cX, cZ, x, y, z, TilePalette.getTileRepresentation(tile));
                }
                //System.out.println(avg)
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

    }
}
