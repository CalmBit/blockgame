package blockgame.util.worker;

import blockgame.world.Chunk;
import blockgame.world.World;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class GeneratorPool {
    private static final ThreadPoolExecutor POOL;
    private static final int MAX_THREAD_COUNT = 12;

    static {
        POOL = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_THREAD_COUNT);
        POOL.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
    }

    private GeneratorPool() {

    }

    public static void enqueueChunkGen(World world, Chunk chunk) {
        POOL.execute(() -> {
            if(chunk.hasGenerated)
                return;
            chunk.generate(world);
                if(world.getChunk(world.calculateChunkPosition(chunk.cX-1, chunk.cZ)) != null && !world.getChunk(world.calculateChunkPosition(chunk.cX-1, chunk.cZ)).hasDecorated) {
                    DecoratorPool.enqueueChunkDecoration(world, world.getChunk(world.calculateChunkPosition(chunk.cX-1, chunk.cZ)));
                }
                if(world.getChunk(world.calculateChunkPosition(chunk.cX, chunk.cZ-1)) != null && !world.getChunk(world.calculateChunkPosition(chunk.cX, chunk.cZ-1)).hasDecorated) {
                    DecoratorPool.enqueueChunkDecoration(world, world.getChunk(world.calculateChunkPosition(chunk.cX, chunk.cZ - 1)));
                }
                if(world.getChunk(world.calculateChunkPosition(chunk.cX-1, chunk.cZ-1)) != null && !world.getChunk(world.calculateChunkPosition(chunk.cX-1, chunk.cZ-1)).hasDecorated) {
                    DecoratorPool.enqueueChunkDecoration(world, world.getChunk(world.calculateChunkPosition(chunk.cX - 1, chunk.cZ - 1)));
                }
            RenderPool.enqueueChunkRender(world, chunk, true);
        });
    }

    public static int queueSize() {
        return POOL.getQueue().size();
    }

    public static void shutdown() {
        POOL.shutdownNow();
    }

}
