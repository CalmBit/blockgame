package blockgame.worker;

import blockgame.block.EnumRenderLayer;
import blockgame.world.Chunk;
import blockgame.world.World;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class RenderPool {
    private final ThreadPoolExecutor pool;
    private static final int MAX_THREAD_COUNT = 12;

    public static final RenderPool INSTANCE = new RenderPool();

    private RenderPool() {
        pool = (ThreadPoolExecutor)Executors.newFixedThreadPool(MAX_THREAD_COUNT);
    }

    public static void enqueueChunkRender(World world, Chunk chunk, boolean cascade) {
        if(chunk.isRendering)
            return;
        chunk.isRendering = true;
        INSTANCE.pool.execute(() -> {
            if(!chunk.hasGenerated)
                return;
            for (EnumRenderLayer l : EnumRenderLayer.VALUES) {
                chunk.dirty = false;
                chunk.isRendering = false;
                BindChunkQueue.enqueueBind(chunk, chunk.buildRenderData(world, l), l);
            }
            if(cascade) {
                for(int x = -1; x <= 1; x++) {
                    for(int z = -1; z <= 1; z++) {
                        if(x == z)
                            continue;
                        Chunk c = world.getChunk(world.calculateChunkPosition(chunk.cX + x, chunk.cZ + z));
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
