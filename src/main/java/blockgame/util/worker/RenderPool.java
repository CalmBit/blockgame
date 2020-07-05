package blockgame.util.worker;

import blockgame.render.world.RenderLayer;
import blockgame.world.Chunk;
import blockgame.world.World;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class RenderPool {
    private static final ThreadPoolExecutor POOL;
    private static final int MAX_THREAD_COUNT = 12;

    static {
        POOL = (ThreadPoolExecutor)Executors.newFixedThreadPool(MAX_THREAD_COUNT);
        POOL.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
    }

    private RenderPool() {

    }

    public static void enqueueChunkRender(World world, Chunk chunk, boolean cascade) {
        if(chunk.isRendering)
            return;
        chunk.isRendering = true;
        POOL.execute(() -> {
            if(!chunk.hasGenerated) {
                chunk.isRendering = false;
                return;
            }
            for (RenderLayer l : RenderLayer.VALUES) {
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
        return POOL.getQueue().size();
    }

    public static void shutdown() {
        POOL.shutdownNow();
    }
}
