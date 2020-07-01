package blockgame.world.generators.decorator;

import blockgame.block.BlockRegistry;
import blockgame.block.TilePalette;
import blockgame.block.TileState;
import blockgame.world.World;

import java.util.Random;

public class DungeonDecorator implements IDecorator {

    private final int _chances;
    private final int _minY;
    private final int _maxY;

    private static final TileState MOSS_COBBLE = new TileState(BlockRegistry.MOSS_COBBLE);
    private static final TileState COBBLESTONE = new TileState(BlockRegistry.COBBLESTONE);
    private static final TileState MONSTER_SPAWNER = new TileState(BlockRegistry.MONSTER_SPAWNER);
    private static final TileState AIR = new TileState(BlockRegistry.AIR);

    public DungeonDecorator(int chances, int minY, int maxY) {
        _chances = chances;
        _minY = minY;
        _maxY = maxY;
    }

    @Override
    public void decorate(World world, int cX, int cZ) {
        world.random = new Random(world.getSeed());
        world.random = new Random(world.getSeed()
                + (world.random.nextInt(Integer.MAX_VALUE) * cX)
                + (world.random.nextInt(Integer.MAX_VALUE) * cZ) ^ world.getSeed());
        for (int c =0; c < _chances;c++) {
            int x = world.random.nextInt(8) + 8;
            int y = world.random.nextInt(128);
            int z = world.random.nextInt(8) + 8;

            if(y < _minY || y > _maxY) continue;

            int r = world.random.nextInt(128);
            if (r > 4) {
                continue;
            }

            if(world.getTileAtAdjusted(cX, cZ, x, y, z) == null) {
                world.getTileAtAdjusted(cX, cZ, x, y, z);
            }

            int len = (world.random.nextBoolean()) ? 6 : 8;
            int wid = (world.random.nextBoolean()) ? 6 : 8;

            TileState tile = null;
            for(int i =0;i <= len;i++) {
                for(int j = 0;j < 5;j++) {
                    for(int k = 0;k <= wid;k++) {
                        if(i==0||i==len||j==0||j==4||k==0||k==wid) {
                            if(j==0) {
                                tile = (world.random.nextInt(4) <= 2) ? MOSS_COBBLE : COBBLESTONE;
                            } else {
                                tile = COBBLESTONE;
                            }
                        } else {
                            tile = AIR;
                        }
                        world.setTileAtAdjusted(cX, cZ, x+i, y+j, z+k, TilePalette.getTileRepresentation(tile));
                    }
                }
            }

            world.setTileAtAdjusted(cX, cZ, x+(len/2), y+1, z+(wid/2), TilePalette.getTileRepresentation(
                    MONSTER_SPAWNER
            ));
        }
    }
}
