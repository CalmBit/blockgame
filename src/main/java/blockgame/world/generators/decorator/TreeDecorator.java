package blockgame.world.generators.decorator;

import blockgame.block.BlockRegistry;
import blockgame.block.TilePalette;
import blockgame.block.TileState;
import blockgame.world.World;

import java.util.function.Predicate;

public class TreeDecorator implements IDecorator {
    private final TileState _log;
    private final TileState _leaves;
    private final int _chances;
    private final Predicate<TileState> _stayPredicate;

    public TreeDecorator(TileState log, TileState leaves, int chances, Predicate<TileState> stayPredicate){
        this._log = log;
        this._leaves = leaves;
        this._chances = chances;
        this._stayPredicate = stayPredicate;
    }

    public void decorate(World world, int cX, int cZ) {
        for (int c = 0;c < _chances;c++) {
            int x = world.random.nextInt(8) + 8;
            int z = world.random.nextInt(8) + 8;
            int y;

            try {
                y = world.getTopTilePosAdjusted(cX, cZ, x, z);
            } catch (Exception e) {
                continue;
            }

            // TODO: Change cX/cZ if the tree goes out of bounds
            if (!_stayPredicate.test(world.getTileAtAdjusted(cX, cZ, x, y, z)))
                continue;
            int height = world.random.nextInt(3) + 5;

            for (int i = 1;i <= height;i++) {
                world.setTileAtAdjusted(cX, cZ, x, y + i, z, TilePalette.getTileRepresentation(_log));
                if(i == height) {
                    for (int j = -1;j <= 1;j++) {
                        for (int k = -1; k <= 1; k++) {
                            if ((Math.abs(j) == 1 || Math.abs(k) == 1) && Math.abs(j) == Math.abs(k))
                                continue;
                            if (world.getTileAtAdjusted(cX, cZ, x + j, y + i, z + k).block != BlockRegistry.AIR)
                                continue;
                            world.setTileAtAdjusted(cX, cZ, x + j, y + i, z +k, TilePalette.getTileRepresentation(_leaves));
                        }
                    }
                }
                else if(i > height - 3) {
                    for (int j = -2;j <= 2;j++) {
                        for (int k = -2; k <= 2; k++) {
                            if ((Math.abs(j) == 2 || Math.abs(k) == 2) && Math.abs(j) == Math.abs(k))
                                continue;
                            if (world.getTileAtAdjusted(cX, cZ, x + j, y + i, z + k).block != BlockRegistry.AIR)
                                continue;
                            world.setTileAtAdjusted(cX, cZ, x + j, y + i, z +k, TilePalette.getTileRepresentation(_leaves));
                        }
                    }
                }
            }
            world.setTileAtAdjusted(cX, cZ, x, y + height + 1, z, TilePalette.getTileRepresentation(_leaves));
        }
    }
}
