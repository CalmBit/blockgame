package block;

import registry.RegistryName;

import static org.lwjgl.opengl.GL31.*;

public class BlockWater extends Block {
    public BlockWater() {
        super(new RegistryName("blockgame", "water"));
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    public EnumRenderLayer renderLayer() {
        return EnumRenderLayer.TRANSLUCENT;
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
