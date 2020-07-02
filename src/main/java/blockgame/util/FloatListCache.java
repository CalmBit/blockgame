package blockgame.util;

import blockgame.Logger;

import java.util.concurrent.locks.ReentrantLock;

public class FloatListCache {

    public static class Entry {
        private boolean free = true;
        private FloatList list = new FloatList();

        public void free() {
            free = true;
            list.clear();
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
    private static Entry[] _cache = new Entry[_capacity];
    private static ReentrantLock lock = new ReentrantLock();


    public static Entry reserve() {
        lock.lock();
        for(int i =0;i < _len;i++) {
            Entry l = _cache[i];
            if(l.free) {
                l.free = false;
                lock.unlock();
                return l;
            }
        }
        if(_len >= _capacity) {
            Entry[] newCache = new Entry[_capacity * 2];
            _capacity *= 2;
            System.arraycopy(_cache, 0, newCache, 0, _len);
            _cache = newCache;
        }
        Entry en = new Entry();
        _cache[_len++] = en;
        en.free = false;
        lock.unlock();
        return en;
    }
}
