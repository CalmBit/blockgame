package blockgame.util.container;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Java's default Map implementations rely on objects,
 * so if you want to use primitives because you actually place value on memory consumption then this is a better option.
 */
public class IntMap<V> {

    public class Entry<V> {

        private final int key;
        private V value;
        private boolean isVacated = false;

        public Entry(int key, V value) {
            this.key = key;
            this.value = value;
        }

        public V setValue(V value) {
            if (this.isVacated) {
                throw new RuntimeException("Unable to set the value of a vacated entry, this value should instead be replaced.");
            }
            V out = this.value;
            this.value = value;
            return out;
        }

        public boolean keyMatches(int key) {
            if (this.isVacated) {
                return false;
            }
            return this.key == key;
        }

        public boolean valueMatches(V value) {
            if (this.isVacated) {
                return false;
            }
            return this.value == value;
        }

        protected V vacate() {
            V out = this.value;
            this.isVacated = true;
            this.value = null;
            return out;
        }
    }

    private static final int DEFAULT_SIZE = 2 ^ 8;
    private Entry<V>[] data;

    private int nextIndex = 0;
    private int[] vacatedIndices = new int[0];

    public IntMap() {
        this.data = new Entry[DEFAULT_SIZE];
    }

    private int getNextIndex() {
        if (this.vacatedIndices.length != 0) {
            int out = this.vacatedIndices[0];
            this.trimVacatedIndices();
            return out;
        }

        return nextIndex;
    }

    private void trimVacatedIndices() {
        if (this.vacatedIndices.length > 0) {
            this.vacatedIndices = Arrays.copyOfRange(this.vacatedIndices, 1, this.vacatedIndices.length);
        }
    }

    /**
     * Doubles the size of the internal buffer, same as the standard HashMap implementation.
     */
    private void grow() {
        Entry<V>[] dataStorage = this.data;
        this.data = new Entry[this.data.length * 2];
        System.arraycopy(dataStorage, 0, this.data, 0, dataStorage.length);
    }


    /**
     * Searches the data buffer for an entry with the given key.
     *
     * @param key the key to search for.
     * @return the entry that is found, or null if nothing is found.
     */
    private Entry<V> searchForEntry(int key) {
        for (int i = 0; i < this.nextIndex; i++) {
            Entry<V> entry = this.data[i];
            if (entry.keyMatches(key)) {
                return entry;
            }
        }
        return null;
    }

    /**
     * Searches the data buffer for an entry with the given key.
     *
     * @param key the key to search for.
     * @return the index of the entry that was found, or -1 if nothing was found.
     */
    private int searchForEntryIndex(int key) {
        for (int i = 0; i < this.nextIndex; i++) {
            if (this.data[i].keyMatches(key)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Adds an entry to the map with the given key and value. If an entry already exists with the given key then the value will be updated.
     *
     * @param key   the key of the element to add.
     * @param value the value of the element to place.
     * @return the value of the entry that was present, or null if none was found.
     */
    public V put(int key, V value) {
        Entry<V> existingEntry = this.searchForEntry(key);
        if (existingEntry != null) {
            return existingEntry.setValue(value);
        }

        int nextIndex = this.getNextIndex();
        if (nextIndex >= this.data.length) {
            this.grow();
        }

        this.data[nextIndex] = new Entry<>(key, value);
        this.nextIndex++;
        return null;
    }

    /**
     * Gets the element associated with the given key in the map.
     *
     * @param key the key to search for.
     * @return the element matching the given key, or null if no element was found.
     */
    public V get(int key) {
        Entry<V> entry = this.searchForEntry(key);
        return entry == null ? null : entry.value;
    }

    /**
     * Checks if an entry with the given key exists in the map.
     *
     * @param key the key to search for.
     * @return true if a matching value was found, false otherwise.
     */
    public boolean containsKey(int key) {
        return this.searchForEntry(key) != null;
    }

    /**
     * Removes the element with the given key.
     *
     * @param key the key of the element to remove.
     * @return the value of the element that was removed, or null if no value existed.
     */
    public V remove(int key) {
        int entryIndex = this.searchForEntryIndex(key);

        if (entryIndex == -1) {
            return null;
        }

        Entry<V> datum = this.data[entryIndex];

        int[] vacatedIndicesStorage = new int[this.vacatedIndices.length + 1];
        System.arraycopy(this.vacatedIndices, 0, vacatedIndicesStorage, 1, this.vacatedIndices.length);
        vacatedIndicesStorage[0] = entryIndex;
        this.vacatedIndices = vacatedIndicesStorage;

        return datum.vacate();
    }

    /**
     * @return The size of the map.
     */
    public int size() {
        return this.nextIndex - this.vacatedIndices.length;
    }

    /**
     * Iterates over every non-vacated entry in the map and passes the values to the given consumer.
     *
     * @param action the consumer to pass each value into.
     */
    public void forEachValue(Consumer<V> action) {
        Objects.requireNonNull(action);
        for (int i = 0; i < this.nextIndex; i++) {
            Entry<V> entry = this.data[i];
            if (!entry.isVacated)
                action.accept(this.data[i].value);
        }
    }

}
