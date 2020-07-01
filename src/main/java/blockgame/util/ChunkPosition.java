package blockgame.util;

import java.util.HashMap;

public class ChunkPosition {
    private static final HashMap<String, ChunkPosition> CHUNK_POS_MAP = new HashMap<>();
    private static final HashMap<Thread, String> STRING_BUFFER = new HashMap<>();

    public int x;
    public int z;

    private ChunkPosition(int x, int z) {
        this.x = x;
        this.z = z;
        CHUNK_POS_MAP.put(this.toString(), this);
    }

    @Override
    public String toString() {
        return x + "," + z;
    }

    public static ChunkPosition getChunkPosition(int x, int z) {
        STRING_BUFFER.put(Thread.currentThread(), x+","+z);
        String search = STRING_BUFFER.get(Thread.currentThread());
        if(CHUNK_POS_MAP.containsKey(search)) {
            return CHUNK_POS_MAP.get(search);
        } else {
            return new ChunkPosition(x, z);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ChunkPosition) {
            ChunkPosition other = (ChunkPosition)obj;
            return this.x == other.x && this.z == other.z;
        }
        return false;
    }
}
