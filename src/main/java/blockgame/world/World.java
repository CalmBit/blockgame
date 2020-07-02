package blockgame.world;

import blockgame.block.BlockRegistry;
import blockgame.block.EnumRenderLayer;
import blockgame.block.TileState;
import org.lwjgl.system.MemoryStack;
import blockgame.worker.GeneratorPool;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class World {
    private Map<Long, Chunk> _chunks = new HashMap<>();
    private int _seed = 0xDEADBEEF;
    public Random random = new Random(_seed);
    public Random tickRandom = new Random();
    public static final int MAX_X = 8;
    public static final int MAX_Z = 8;

    public WorldType worldType = WorldType.DEFAULT;

    public World() {
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

        _chunks.forEach((chunkPosition, chunk) -> {
            stack[0] = MemoryStack.stackPush();
            chunk.draw(stack[0], EnumRenderLayer.NORMAL, uniTrans, timer);
            stack[0].pop();
        });

        _chunks.forEach((chunkPosition, chunk) -> {
            stack[0] = MemoryStack.stackPush();
            chunk.draw(stack[0], EnumRenderLayer.TRANSLUCENT, uniTrans, timer);
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
            System.out.println("Missing Chunk! X=" + x + " Y=" + y + " Z=" + z + " cpos=" + (cPos >> 32) + "," + ((int)cPos & 0xFFFFFFFFL));
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
        System.err.println("Chunk not found...");
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
        _chunks.forEach((chunkPosition, chunk) -> chunk.tick(this, tickRandom));
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
        long xC = cX;
        long zC = cZ;
        long conv = xC << 32;
        long last = (conv + (cZ & 0xFFFFFFFFL));
        return last;
    }

    public long getChunkPositionFromBlockPosition(int x, int z) {
        return calculateChunkPosition(x >> 4, z >> 4);
    }
}
