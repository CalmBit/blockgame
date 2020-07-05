package blockgame.block;

import blockgame.render.world.RenderLayer;
import blockgame.util.registry.RegistryName;

import static org.lwjgl.opengl.GL33.*;

public class BlockWater extends Block {
    public BlockWater() {
        super(new RegistryName("blockgame", "water"));
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    public RenderLayer renderLayer() {
        return RenderLayer.TRANSLUCENT;
    }

    @Override
    public void preRender() {
        glDisable(GL_CULL_FACE);
    }

    @Override
    public void postRender() {
        glEnable(GL_CULL_FACE);
    }
}
