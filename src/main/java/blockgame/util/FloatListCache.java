package blockgame.util;

import blockgame.util.container.FloatList;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;

/**
 * We need a system to reserve FloatLists for the rendering and bind pools, without continually
 * eating memory by tossing away thousands of objects. FloatList cache allows us to selectively reserve and
 * free a set of pre-allocated FloatLists that can be "possessed" indefinitely.
 */
public class FloatListCache {

    public static class Entry {
        /**
         * When free is true, FloatListCache will be free to "reserve" this entry and mark it
         * as being in use. When false, it's known to be reserved, and the state should only be
         * modified by the owning thread.
         */
        private boolean free = true;
        private boolean dirty = false;
        private FloatList list = new FloatList();

        public void free() {
            list.clear();
            dirty = true;
            free = true;
            _inUse--;
        }

        public void addToList(float ...f) {
            if(free) {
                Logger.LOG.error("Tried to addToList when FloatList was free");
                return;
            }
            list.appendAll(f);
        }

        public FloatList getList() {
            if(free) {
                Logger.LOG.error("Tried to getList when FloatList was free");
                return null;
            }
            return list;
        }

        private FloatList forceGetList() {
            return list;
        }

        public float[] getStore() {
            if(free) {
                Logger.LOG.error("Tried to getStore when FloatList was free");
                return new float[]{};
            }
            return list.getStore();
        }
    }


    private static final int DEFAULT_CAPACITY = 64;
    private static final int CLEAR_WAIT_MILLIS = 500;
    private static int _len = 0;
    private static int _inUse = 0;
    private static int _capacity = DEFAULT_CAPACITY;

    private static Entry[] _cache = new Entry[_capacity];
    private static ReentrantLock lock = new ReentrantLock();
    private static Instant _lastReserve = Instant.EPOCH;


    public static Entry reserve() {
        lock.lock();
        _lastReserve = Instant.now();
        for(int i =0;i < _len;i++) {
            Entry l = _cache[i];
            if(l.free) {
                l.free = false;
                _inUse++;
                lock.unlock();
                return l;
            }
        }
        // If we've found no free entries, and we have no more to reserve, we'll have to
        // allocate more. In order to avoid doing this a lot under load, we'll allocate
        // twice the amount we currently have.
        if(_len >= _capacity) {
            Entry[] newCache = new Entry[_capacity * 2];
            _capacity *= 2;
            System.arraycopy(_cache, 0, newCache, 0, _len);
            _cache = newCache;
        }
        Entry en = new Entry();
        _cache[_len++] = en;
        en.free = false;
        _inUse++;
        lock.unlock();
        return en;
    }

    /**
     * FloatLists are re-allocated to match a power of two capacity that can fit all the required data. By
     * default, FloatLists don't do any internal mediation of their max capacity - preferably, a high
     * enough or low enough max capacity should be chosen on instantiation. Since the intended usage for
     * this cache (world rendering VBO data) is extremely variable, we can't pre-select a good maximum.
     *
     * There's no sense in re-allocating the buffers while they're still in use, as they'll inevitably
     * balloon just the same and we'll be stuck with frequent and costly re-allocations, which
     * we would like to avoid. The VBOs are updated according their _length_, not capacity, so no
     * garbage data is ever uploaded to the renderer.
     *
     * So, we just clean up the FloatLists every time the cache goes 500ms w/o an allocation.
     */
    public static void watch() {
        // The EPOCH here only serves as a tombstone for "don't bother updating because we've yet
        // to re-allocate since we last cleaned."
        if(_lastReserve != Instant.EPOCH && Duration.between(_lastReserve, Instant.now()).toMillis() >= CLEAR_WAIT_MILLIS) {
            lock.lock();
            for(Entry e : _cache) {
                // If we can't get the entry, it's in use, or it's not necessary to
                // clean it, don't bother.
                if(e == null || !e.free || !e.dirty)
                    continue;
                e.forceGetList().resize(FloatList.DEFAULT_CAPACITY, true);
                e.dirty = false;
            }
            lock.unlock();
            _lastReserve = Instant.EPOCH;
        }
    }
}
