package blockgame.block;

import blockgame.gl.UVPair;
import blockgame.registry.IRegistryEntry;
import blockgame.registry.RegistryName;
import blockgame.world.World;
import org.joml.Vector3f;

public class Block implements IRegistryEntry {
    private RegistryName _name;
    private UVPair _uv;

    public Block(RegistryName name) {
        _name = name;
    }

    public RegistryName getRegistryName() {
        return _name;
    }

    public void setRegistryName(RegistryName name) {
        _name = name;
    }

    public UVPair getUVForFace(EnumDirection face) {
        return _uv != null ? _uv : UVPair.MISSING_UV;
    }

    public Block setUV(UVPair pair) {
        this._uv = pair;
        return this;
    }

    public boolean isOpaque() {
        return true;
    }

    public boolean shouldRenderFace(World world, int cX, int cZ, int rY, int x, int y, int z, EnumDirection face) {
        TileState other = world.getTileAt(face.getX(world.adjustChunk(cX, x)), face.getY(y + (16 * rY)), face.getZ(world.adjustChunk(cZ, z)));
        if (face == EnumDirection.UP && y + (16 * rY) == 127)
            return true;
        if (other != null) {
            return !other.shouldRender() || (!other.isOpaque() && this.isOpaque());
        }
        return true;
    }

    public boolean shouldRender() {
        return true;
    }

    public EnumRenderLayer renderLayer() {
        return EnumRenderLayer.NORMAL;
    }

    public void preRender() {

    }

    public void postRender() {

    }

    public float getEmittance() {
        return 0.0f;
    }

    public boolean impartsTint() {
        return false;
    }

    public Vector3f getTint(int cX, int cZ, int x, int y, int z) {
        return null;
    }

    public void tick(World world, int rX, int rY, int rZ, int x, int y, int z) {

    }


}
