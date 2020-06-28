package block;

import gl.UVPair;

public class TileState {
    public final Block block;

    public TileState(Block block) {
        this.block = block;
    }

    public UVPair getUVForFace(EnumDirection face) {
       return  block.getUVForFace(face);
    }

    public boolean shouldRender() {
        return block.shouldRender();
    }
    public boolean isOpaque() {
        return block.isOpaque();
    }

    public EnumRenderLayer renderLayer() {
         return block.renderLayer();
    }

    public float getEmittance() {
        return block.getEmittance();
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof TileState) {
            return this.block == ((TileState)other).block;
        }
        return false;
    }
}
