package blockgame.util;

public class IntList {
    private int _capacity = 1024;
    private int _length = 0;
    private int[] _store;

    public IntList() {
        _store = new int[_capacity];
    }

    public IntList(int size) {
        _capacity = size;
        _store = new int[size];
    }

    public int getLength() {
        return _length;
    }

    public int getCapacity() {
        return _capacity;
    }

    public void resize(int capacity, boolean truncate) {
        if(!truncate && this._length > capacity) {
            System.err.println("Unable to resize FloatList - new capacity '" + capacity + "' is below current length '" + _length + "'!");
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

    public void append(int value)  {
        if(_length + 1 > _capacity) {
            resize(_capacity * 2);
        }
        _store[_length++] = value;
    }

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
}
