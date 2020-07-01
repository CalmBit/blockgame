package blockgame.worker;

import blockgame.util.ChunkPosition;
import blockgame.world.Chunk;
import blockgame.world.World;

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
                if(world.getChunk(ChunkPosition.getChunkPosition(chunk.cX-1, chunk.cZ)) != null && !world.getChunk(ChunkPosition.getChunkPosition(chunk.cX-1, chunk.cZ)).hasDecorated) {
                    DecoratorPool.enqueueChunkDecoration(world, world.getChunk(ChunkPosition.getChunkPosition(chunk.cX-1, chunk.cZ)));
                }
                if(world.getChunk(ChunkPosition.getChunkPosition(chunk.cX, chunk.cZ-1)) != null && !world.getChunk(ChunkPosition.getChunkPosition(chunk.cX, chunk.cZ-1)).hasDecorated) {
                    DecoratorPool.enqueueChunkDecoration(world, world.getChunk(ChunkPosition.getChunkPosition(chunk.cX, chunk.cZ - 1)));
                }
                if(world.getChunk(ChunkPosition.getChunkPosition(chunk.cX-1, chunk.cZ-1)) != null && !world.getChunk(ChunkPosition.getChunkPosition(chunk.cX-1, chunk.cZ-1)).hasDecorated) {
                    DecoratorPool.enqueueChunkDecoration(world, world.getChunk(ChunkPosition.getChunkPosition(chunk.cX - 1, chunk.cZ - 1)));
                }
            RenderPool.enqueueChunkRender(world, chunk, true);
        });
    }

    public static int queueSize() {
        return INSTANCE.pool.getQueue().size();
    }

}
