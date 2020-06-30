package worker;

import world.Chunk;
import world.World;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class GeneratorPool {
    private final ThreadPoolExecutor pool;
    private static final int MAX_THREAD_COUNT = 4;
    private static final int MAX_X = 8;
    private static final int MAX_Z = 8;

    public static final GeneratorPool INSTANCE = new GeneratorPool();

    private GeneratorPool() {
        pool = (ThreadPoolExecutor)Executors.newFixedThreadPool(4);
    }

    public static void enqueueChunkGen(World world, Chunk chunk) {
        INSTANCE.pool.execute(() -> {
            if(chunk.hasGenerated)
                return;
            chunk.generate(world);
                /*decorateChunkMutex.lock()
                if(world._chunks[ChunkPosition.getChunkPosition(c.cX-1, c.cZ)] != null && !world._chunks[ChunkPosition.getChunkPosition(c.cX-1, c.cZ)]!!.hasDecorated) {
                    decorateChunkQueue.offer(world._chunks[ChunkPosition.getChunkPosition(c.cX-1, c.cZ)])
                }
                if(world._chunks[ChunkPosition.getChunkPosition(c.cX, c.cZ-1)] != null && !world._chunks[ChunkPosition.getChunkPosition(c.cX, c.cZ-1)]!!.hasDecorated) {
                    decorateChunkQueue.offer(world._chunks[ChunkPosition.getChunkPosition(c.cX, c.cZ - 1)])
                }
                if(world._chunks[ChunkPosition.getChunkPosition(c.cX-1, c.cZ-1)] != null && !world._chunks[ChunkPosition.getChunkPosition(c.cX-1, c.cZ-1)]!!.hasDecorated) {
                    decorateChunkQueue.offer(world._chunks[ChunkPosition.getChunkPosition(c.cX - 1, c.cZ - 1)])
                }
                decorateChunkMutex.unlock()*/
            RenderPool.enqueueChunkRender(world, chunk, true);
        });
    }

    public static int queueSize() {
        return INSTANCE.pool.getQueue().size();
    }

}
