package util;

public class FloatList {
    private int _capacity = 1024;
    private int _length = 0;
    private float[] _store;

    public FloatList() {
        _store = new float[_capacity];
    }

    public FloatList(int size) {
        _capacity = size;
        _store = new float[size];
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
        float[] n = new float[capacity];
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

    public void append(float value)  {
        if(_length + 1 > _capacity) {
            resize(_capacity * 2);
        }
        _store[_length++] = value;
    }

    public void clear() {
        for(int i =0;i < _length;i++) {
            _store[i] = 0.0f;
        }
        _length = 0;
    }

    public float[] getStore() {
        return _store;
    }
}
