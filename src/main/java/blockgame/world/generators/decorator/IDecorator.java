package blockgame.world.generators.decorator;

import blockgame.world.World;

public interface IDecorator {
    void decorate(World world, int cX, int cZ);
}
