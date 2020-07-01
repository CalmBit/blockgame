package blockgame.world.generators;

import blockgame.world.World;

public interface IGenerator {
    void generate(World world, int cX, int cZ);
    void decorate(World world, int cX, int cZ);
}
