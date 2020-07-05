package blockgame.block;

import blockgame.render.gl.texture.UVPair;
import blockgame.render.world.RenderLayer;
import org.joml.Vector3f;

public class TileState {
    public final Block block;

    public TileState(Block block) {
        this.block = block;
    }

    public UVPair getUVForFace(Direction face) {
       return  block.getUVForFace(face);
    }

    public boolean shouldRender() {
        return block.shouldRender();
    }
    public boolean isOpaque() {
        return block.isOpaque();
    }

    public RenderLayer renderLayer() {
         return block.renderLayer();
    }

    public float getEmittance() {
        return block.getEmittance();
    }

    public Vector3f getTint(int cX, int cZ, int x, int y, int z) {
        return block.getTint(cX, cZ, x, y, z);
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof TileState) {
            return this.block == ((TileState)other).block;
        }
        return false;
    }
}
