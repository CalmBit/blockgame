package blockgame.world.generators.decorator;

import blockgame.block.BlockRegistry;
import blockgame.block.TilePalette;
import blockgame.block.TileState;
import blockgame.world.World;

import java.util.function.Predicate;

public class OreDecorator implements IDecorator {
    private final TileState _ore;
    private final int _chances;
    private final int _minY;
    private final int _maxY;
    private final int _veinSize;
    private final Predicate<TileState> _replacePredicate;
    private final boolean _scaleFail;

    public OreDecorator(TileState ore, int chances, int minY, int maxY, int veinSize, Predicate<TileState> replacePredicate,
                        boolean scaleFail) {
        this._ore = ore;
        this._chances = chances;
        this._minY = minY;
        this._maxY = maxY;
        this._veinSize = veinSize;
        this._replacePredicate = replacePredicate;
        this._scaleFail = scaleFail;
    }

    public OreDecorator(TileState ore,  int chances, int minY, int maxY, int veinSize, Predicate<TileState> replacePredicate) {
        this(ore, chances, minY, maxY, veinSize, replacePredicate, true);
    }

    public void decorate(World world, int cX, int cZ) {
        for (int c = 0;c < _chances;c++) {
            int x = world.random.nextInt(8) + 8;
            int y = world.random.nextInt(128);
            int z = world.random.nextInt(8) + 8;

            if(y < _minY || y > _maxY)
                continue;

            if(world.getTileAtAdjusted(cX, cZ, x, y, z) == null) {
                world.getTileAtAdjusted(cX, cZ, x, y, z);
            }
            if (world.getTileAtAdjusted(cX, cZ, x, y, z).block != BlockRegistry.STONE)
                continue;

            world.setTileAtAdjusted(cX, cZ, x, y, z, TilePalette.getTileRepresentation(_ore));

            int count = 1;

            while (count < _veinSize) {

                if(_scaleFail && world.random.nextInt(_veinSize) < count) {
                    break;
                }

                int dX = world.random.nextInt(3) - 1;
                int dY = world.random.nextInt(3) - 1;
                int dZ = world.random.nextInt(3) - 1;

                if (y + dY < 0 || y + dY > 128  || y + dY < _minY || y + dY > _maxY) {
                    dY = 0;
                }

                if (!_replacePredicate.test(world.getTileAtAdjusted(cX, cZ, x + dX, y +dY, z + dZ))) {
                    dX = 0;
                    dY = 0;
                    dZ = 0;
                }


                world.setTileAtAdjusted(cX, cZ, x + dX, y + dY, z + dZ, TilePalette.getTileRepresentation(_ore));
                x += dX;
                y += dY;
                z += dZ;
                count++;
            }
        }
    }
}
