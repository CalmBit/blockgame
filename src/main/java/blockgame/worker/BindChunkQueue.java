package blockgame.worker;

import blockgame.block.EnumRenderLayer;
import blockgame.gl.ShaderProgram;
import blockgame.util.FloatList;
import blockgame.util.FloatListCache;
import blockgame.world.Chunk;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BindChunkQueue {
    public static class BindChunkQueueTask {
        public Chunk chunk;
        public List<FloatListCache.Entry> verts;
        public EnumRenderLayer layer;

        public BindChunkQueueTask(Chunk chunk, List<FloatListCache.Entry> verts, EnumRenderLayer layer) {
            this.chunk = chunk;
            this.verts = verts;
            this.layer = layer;
        }
    }

    private final Queue<BindChunkQueueTask> queue = new ArrayDeque<>();

    public static final BindChunkQueue INSTANCE = new BindChunkQueue();

    public static final int QUEUE_BATCH_SIZE = 48;

    public static final Lock BIND_LOCK = new ReentrantLock();

    public static void enqueueBind(Chunk chunk, List<FloatListCache.Entry> verts, EnumRenderLayer layer) {
        BIND_LOCK.lock();
        INSTANCE.queue.add(new BindChunkQueueTask(chunk, verts, layer));
        BIND_LOCK.unlock();
    }

    private static void bind(ShaderProgram program, BindChunkQueueTask currentTask) {
        currentTask.chunk.bindRenderData(currentTask.verts, currentTask.layer, program);
        currentTask.verts.forEach((entry -> entry.free()));
    }

    public static void bindChunks(ShaderProgram program) {
        if (INSTANCE.queue.size() > 0) {
            BindChunkQueueTask currentTask = null;
            if (INSTANCE.queue.size() < QUEUE_BATCH_SIZE) {
                while (INSTANCE.queue.size() > 0) {
                    currentTask = INSTANCE.queue.poll();
                    bind(program, currentTask);
                }
            } else {
                for (int i = 0;i < 48;i++) {
                    currentTask = INSTANCE.queue.poll();
                    bind(program, currentTask);
                }
            }
        }
    }

    public static int queueSize() {
        return INSTANCE.queue.size();
    }
}
