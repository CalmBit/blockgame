package blockgame.util.worker;

import blockgame.world.Chunk;
import blockgame.world.World;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class DecoratorPool {
    private static final ThreadPoolExecutor POOL;
    private static final int MAX_THREAD_COUNT = 12;

    static {
        POOL = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_THREAD_COUNT);
        POOL.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
    }

    private DecoratorPool() {

    }

    public static void enqueueChunkDecoration(World world, Chunk chunk) {
        POOL.execute(() -> {
            if(!chunk.hasGenerated || chunk.isDecorating || chunk.hasDecorated)
                return;
            if (checkChunkGen(world, chunk.cX + 1, chunk.cZ)
                    && checkChunkGen(world, chunk.cX, chunk.cZ + 1)
                    && checkChunkGen(world, chunk.cX + 1, chunk.cZ + 1)) {
                chunk.decorate(world);
            }
        });
    }

    public static boolean checkChunkGen(World world, int x, int z) {
        return world.getChunk(world.calculateChunkPosition(x, z)) != null
                && world.getChunk(world.calculateChunkPosition(x, z)).hasGenerated;
    }

    public static int queueSize() {
        return POOL.getQueue().size();
    }

    public static void shutdown() {
        POOL.shutdownNow();
    }
}
