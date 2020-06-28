package block;

import registry.RegistryName;
import world.World;

public class BlockOre extends Block {
    public BlockOre(RegistryName name) {
        super(name);
    }

    @Override
    public boolean shouldRenderFace(World world, int cX, int cZ, int rY, int x, int y, int z, EnumDirection face) {
        return super.shouldRenderFace(world, cX, cZ, rY, x, y, z, face);
    }
}
