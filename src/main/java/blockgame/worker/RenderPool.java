package blockgame.worker;

import blockgame.block.EnumRenderLayer;
import blockgame.util.ChunkPosition;
import blockgame.world.Chunk;
import blockgame.world.World;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class RenderPool {
    private final ThreadPoolExecutor pool;
    private static final int MAX_THREAD_COUNT = 4;

    public static final RenderPool INSTANCE = new RenderPool();

    private RenderPool() {
        pool = (ThreadPoolExecutor)Executors.newFixedThreadPool(4);
    }

    public static void enqueueChunkRender(World world, Chunk chunk, boolean cascade) {
        INSTANCE.pool.execute(() -> {
            if(!chunk.hasGenerated)
                return;
            for (EnumRenderLayer l : EnumRenderLayer.VALUES) {
                chunk.dirty = false;
                BindChunkQueue.enqueueBind(chunk, chunk.buildRenderData(world, l), l);
            }
            if(cascade) {
                Chunk c = null;
                for(int x = -1; x <= 1; x++) {
                    for(int z = -1; z <= 1; z++) {
                        if(x == z)
                            continue;
                        c = world.getChunk(ChunkPosition.getChunkPosition(chunk.cX+x, chunk.cZ+z));
                        if(c != null && !c.dirty) {
                            c.dirty = true;
                            RenderPool.enqueueChunkRender(world, c, false);
                        }
                    }
                }
            }
        });
    }
    public static int queueSize() {
        return INSTANCE.pool.getQueue().size();
    }
}
