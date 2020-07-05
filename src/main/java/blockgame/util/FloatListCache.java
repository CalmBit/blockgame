package blockgame.util;

import blockgame.Logger;

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
        private FloatList list = new FloatList();

        public void free() {
            list.clear();
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

        public float[] getStore() {
            if(free) {
                Logger.LOG.error("Tried to getStore when FloatList was free");
                return new float[]{};
            }
            return list.getStore();
        }
    }


    private static int _len = 0;
    private static int _capacity = 64;
    public static int _inUse = 0;
    private static Entry[] _cache = new Entry[_capacity];
    private static ReentrantLock lock = new ReentrantLock();

    public static Entry reserve() {
        lock.lock();
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
}
