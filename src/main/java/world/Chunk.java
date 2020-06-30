package world;

import block.EnumRenderLayer;
import block.TileState;
import gl.ShaderProgram;
import org.lwjgl.system.MemoryStack;
import util.FloatList;
import worker.RenderPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Chunk {
    private Region[] _regions = new Region[8];
    public boolean hasGenerated = false;
    public boolean hasDecorated = false;
    public boolean isLoaded = false;
    public boolean dirty = false;

    private World _world;
    public int cX;
    public int cZ;

    public Chunk(World world, int cX, int cZ) {
        _world = world;
        this.cX = cX;
        this.cZ = cZ;

        for (int i = 0;i < _regions.length;i++) {
            _regions[i] = new Region(cX, i, cZ);
        }
    }


    public TileState getTileAt(int x, int y, int z) {
        if (x > 15 || x < 0 || z > 15 || z < 0 || y < 0 || y > 127)
            return null;

        int r = y >> 4;
        if (_regions.length >= r) {
            return _regions[r].getTileAt(x, y & 15, z);
        }
        return null;
    }



    public void generate(World world) {
        world.worldType.gen.generate(world, cX, cZ);
        hasGenerated = true;
        dirty = true;
    }

    public void decorate(World world) {
        world.worldType.gen.decorate(world, cX, cZ);
        hasDecorated = true;
        dirty = true;
    }

    public List<FloatList> buildRenderData(World world, EnumRenderLayer l) {
        List<FloatList> verts = new ArrayList<>();
        for (Region region : _regions) {
            FloatList v = region.buildRenderData(world, cX, cZ, l);
            verts.add(v);
        }
        return verts;
    }

    public void draw(MemoryStack stack, EnumRenderLayer l, int uniTrans, float timer) {
        for (Region r : _regions) {
            r.draw(stack, l, uniTrans, timer);
        }
    }

    public void setTileAt(int x, int y, int z, int tile) {
        if (x > 15 || x < 0 || z > 15 || z < 0 || y < 0 || y > 127) {
            return;
        }
        int r = y >> 4;
        if (_regions.length >= r) {
            _regions[r].setTileAt(x, y & 15, z, tile);
        }
        if(this.hasGenerated && !this.dirty) {
            this.dirty = true;
            RenderPool.enqueueChunkRender(_world, this, false);
        }
    }

    public void bindRenderData(List<FloatList> verts, EnumRenderLayer l, ShaderProgram prog) {
        for(int i = 0;i < _regions.length;i++) {
            _regions[i].bindData(verts.get(i), l, prog);
        }
    }

    public void tick(World world, Random random) {
        for (Region r : _regions) {
            r.tick(world, random);
        }
    }
}
