package blockgame.util.worker;

import blockgame.render.world.RenderLayer;
import blockgame.render.gl.shader.ShaderProgram;
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
        public RenderLayer layer;

        public BindChunkQueueTask(Chunk chunk, List<FloatListCache.Entry> verts, RenderLayer layer) {
            this.chunk = chunk;
            this.verts = verts;
            this.layer = layer;
        }
    }

    private static final Queue<BindChunkQueueTask> QUEUE = new ArrayDeque<>();
    public static final int QUEUE_BATCH_SIZE = 48;
    public static final Lock BIND_LOCK = new ReentrantLock();


    private BindChunkQueue() {

    }


    public static void enqueueBind(Chunk chunk, List<FloatListCache.Entry> verts, RenderLayer layer) {
        BIND_LOCK.lock();
        QUEUE.add(new BindChunkQueueTask(chunk, verts, layer));
        BIND_LOCK.unlock();
    }

    private static void bind(ShaderProgram program, BindChunkQueueTask currentTask) {
        currentTask.chunk.bindRenderData(currentTask.verts, currentTask.layer, program);
    }

    public static void bindChunks(ShaderProgram program) {
        if (QUEUE.size() > 0) {
            BindChunkQueueTask currentTask = null;
            if (QUEUE.size() < QUEUE_BATCH_SIZE) {
                while (QUEUE.size() > 0) {
                    currentTask = QUEUE.poll();
                    bind(program, currentTask);
                }
            } else {
                for (int i = 0;i < 48;i++) {
                    currentTask = QUEUE.poll();
                    bind(program, currentTask);
                }
            }
        }
    }

    public static int queueSize() {
        return QUEUE.size();
    }
}
