package blockgame.util.container;

import blockgame.util.Logger;

/**
 * IntList is a dynamically resizable array of integers, in lieu of using Objects to accomplish
 * the same thing in a comparable generic list.
 */
public class IntList {
    /**
     * The current maximum capacity of the list. This is the size that the
     * array is current allocated to, but doesn't represent the actual number
     * of entries to the list.
     */
    private int _capacity = 1024;

    /**
     * The current maximum entry in the list, from `0` to `_capacity`. This value
     * causes a resize if it is at or above `_capacity` at any point.
     */
    private int _length = 0;

    private int[] _store;

    public IntList() {
        _store = new int[_capacity];
    }

    public IntList(int size) {
        _capacity = size;
        _store = new int[size];
    }

    /**
     * Arbitrarily resize the underlying store with a new value for `_capacity`. If `truncate` is true,
     * values can be _smaller_ than `_capacity`, but will erase data in the process. Only `truncate`
     * if the entries at `capacity` and above can be erased!
     * @param capacity The new capacity for the underlying store
     * @param truncate Whether or not the new capacity can be lower than the current one,
     *                 erasing entries at or above `capacity`
     */
    public void resize(int capacity, boolean truncate) {
        if(!truncate && this._length > capacity) {
            Logger.LOG.error("Unable to resize FloatList - new capacity '" + capacity + "' is below current length '" + _length + "'!");
        }
        int[] n = new int[capacity];
        for(int i = 0;i < capacity;i++) {
            n[i] = _store[i];
            if(i == _capacity-1)
                break;
        }
        _store = n;
        _capacity = capacity;
        if(_length > _capacity) _length = _capacity;
    }

    public void resize(int capacity) {
        resize(capacity, false);
    }

    /**
     * Appends a new value to the list, increasing `_length` and possibly resizing the store if
     * equal to or greater than `_capacity`.
     * @param value The new value to append.
     */
    public void append(int value)  {
        if(_length + 1 > _capacity) {
            resize(_capacity * 2);
        }
        _store[_length++] = value;
    }

    /**
     * Appends a set of values to the list, increasing `_length` and possibly resizing the store
     * _multiple times_ while the new `_length` is under capacity.
     * @param values A variadic argument list of values to append to the list.
     */
    public void appendAll(int ...values) {
        while(_length + values.length > _capacity) {
            resize(_capacity * 2);
        }
        for(int v : values) {
            _store[_length++] = v;
        }
    }

    /**
     * Clears the entirety of the list, erasing all values and setting `_length`
     * to zero. Note that this doesn't resize the array - if an array has been resized, it
     * will stay that way until manually resized.
     */
    public void clear() {
        for(int i =0;i < _length;i++) {
            _store[i] = 0;
        }
        _length = 0;
    }

    public void set(int element, int value) {
        if(element > _capacity-1) {
            resize(element+1);
        }
        _store[element] = value;
    }

    public int get(int element) {
        if(element > _capacity-1) {
            return -1;
        }
        return _store[element];
    }

    public int[] getStore() {
        return _store;
    }


    public int getLength() {
        return _length;
    }

    public int getCapacity() {
        return _capacity;
    }

}
