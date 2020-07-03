package blockgame.worker;

import blockgame.world.Chunk;
import blockgame.world.World;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class DecoratorPool {
    private final ThreadPoolExecutor pool;
    private static final int MAX_THREAD_COUNT = 12;
    private static final int MAX_X = 8;
    private static final int MAX_Z = 8;

    public static final DecoratorPool INSTANCE = new DecoratorPool();

    private DecoratorPool() {
        pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_THREAD_COUNT);
    }

    public static void enqueueChunkDecoration(World world, Chunk chunk) {
        INSTANCE.pool.execute(() -> {
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
        return INSTANCE.pool.getQueue().size();
    }
}
