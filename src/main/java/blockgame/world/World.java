package blockgame.world;

import blockgame.util.Logger;
import blockgame.block.BlockRegistry;
import blockgame.render.world.RenderLayer;
import blockgame.block.TileState;
import blockgame.util.container.LongMap;
import org.lwjgl.system.MemoryStack;
import blockgame.util.worker.GeneratorPool;

import java.util.Random;

public class World {
    private LongMap<Chunk> _chunks = new LongMap<>();
    private int _seed;
    public Random random;
    public Random tickRandom = new Random();
    public static final int MAX_X = 8;
    public static final int MAX_Z = 8;

    public WorldType worldType = WorldType.DEFAULT;

    public World() {
        Random r = new Random();
        _seed = r.nextInt();
        random = new Random(_seed);
        long cPos = 0;
        for (int x = -MAX_X; x < MAX_X; x++) {
            for (int z = -MAX_Z; z < MAX_Z; z++) {
                cPos = calculateChunkPosition(x, z);
                addChunk(cPos);
            }
        }

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int x = -MAX_X; x < MAX_X; x++) {
            for (int z = -MAX_Z; z < MAX_Z; z++) {
                cPos = calculateChunkPosition(x, z);
                GeneratorPool.enqueueChunkGen(this, _chunks.get(cPos));
            }
        }
    }

    public void draw(int uniTrans, float timer) {
        final MemoryStack[] stack = {null};

        _chunks.forEachValue((chunk) -> {
            stack[0] = MemoryStack.stackPush();
            chunk.draw(stack[0], RenderLayer.NORMAL, uniTrans, timer);
            stack[0].pop();
        });

        _chunks.forEachValue((chunk) -> {
            stack[0] = MemoryStack.stackPush();
            chunk.draw(stack[0], RenderLayer.TRANSLUCENT, uniTrans, timer);
            stack[0].pop();
        });
    }

    public TileState getTileAtAdjusted(int cX, int cZ, int x, int y, int z) {
        int tX = adjustChunk(cX, x);
        int tZ = adjustChunk(cZ, z);
        return getTileAt(tX, y, tZ);
    }

    public TileState getTileAt(int x, int y, int z) {
        long cPos = getChunkPositionFromBlockPosition(x, z);
        if(chunkExists(cPos)) {
            Chunk c = _chunks.get(cPos);
            return c.getTileAt(x & 15, y, z & 15);
        }
        return null;
    }

    public int getSeed() {
        return _seed;
    }

    private void setTileAt(int x, int y, int z, int tile) {
        long cPos = getChunkPositionFromBlockPosition(x, z);
        if (chunkExists(cPos)) {
            _chunks.get(cPos).setTileAt(x & 15, y, z & 15, tile);
        } else {
            Logger.LOG.error("Missing Chunk! X=" + x + " Y=" + y + " Z=" + z + " cpos=" + (cPos >> 32) + "," + ((int)cPos & 0xFFFFFFFFL));
        }
    }

    public void setTileAtAdjusted(int cX, int cZ, int x, int y, int z, int tile) {
        int tX = adjustChunk(cX, x);
        int tZ = adjustChunk(cZ, z);
        setTileAt(tX, y, tZ, tile);
    }

    public int getTopTilePos(int x, int z) {
        long cPos = getChunkPositionFromBlockPosition(x, z);
        if(chunkExists(cPos)) {
            int y = 127;
            while(_chunks.get(cPos).getTileAt(x & 15, y,z & 15).block == BlockRegistry.AIR)
            {
                y--;
            }
            return y;
        }
        Logger.LOG.error("Chunk not found on call to getTopTilePos!");
        return -1;
    }

    public int getTopTilePosAdjusted(int cX, int cZ, int x, int z) {
        int tX = adjustChunk(cX, x);
        int tZ = adjustChunk(cZ, z);
        return getTopTilePos(tX, tZ);
    }

    public int adjustChunk(int chunk, int position) {
        if(position > 15) {
            return (chunk << 4) + position;
        }
        return (chunk << 4) + position;
    }

    public boolean chunkExists(long cPos) {
        boolean r = _chunks.containsKey(cPos);
        return r;
    }

    public Chunk addChunk(long cPos) {
        Chunk c = new Chunk(this, (int)(cPos >> 32), (int)(cPos & 0xFFFFFFFFL));
        _chunks.put(cPos, c);
        c.isLoaded = true;

        return c;
    }

    public void tick() {
        _chunks.forEachValue((chunk) -> chunk.tick(this, tickRandom));
    }

    public int chunkCount() {
        return _chunks.size();
    }

    public Chunk getChunk(long cPos) {
        if(!chunkExists(cPos))
            return null;
        return _chunks.get(cPos);
    }

    public long calculateChunkPosition(int cX, int cZ) {
        return (((long)cX << 32) + (cZ & 0xFFFFFFFFL));
    }

    public long getChunkPositionFromBlockPosition(int x, int z) {
        return calculateChunkPosition(x >> 4, z >> 4);
    }
}
