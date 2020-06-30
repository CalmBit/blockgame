package registry;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.BiConsumer;

public class Registry<T extends IRegistryEntry> {
    private HashMap<String, HashMap<String, T>> _dict = new HashMap<>();

    public T register(T v) throws DuplicateKeyException {
        RegistryName reg = v.getRegistryName();
        if (!_dict.containsKey(reg.getNamespace())) {
            _dict.put(reg.getNamespace(), new HashMap<>());
        }
        if(_dict.get(reg.getNamespace()).containsKey(reg.getName())) {
            throw new DuplicateKeyException(this, reg);
        }
        _dict.get(reg.getNamespace()).put(reg.getName(), v);
        return v;
    }

    public T get(RegistryName name) {
        if(_dict.containsKey(name.getNamespace()) && _dict.get(name.getNamespace()).containsKey(name.getName())) {
            return _dict.get(name.getNamespace()).get(name.getName());
        } else {
            return null;
        }
    }

    public int size() {
        int sz = 0;
        Collection<HashMap<String, T>> e = _dict.values();

        for(HashMap<String, T> i : e) {
            sz += i.size();
        }
        return sz;
    }

    public void forEach(String namespace, BiConsumer<String, T> op) {
        try {
            _dict.get(namespace).forEach(op);
        } catch (Exception e) {
            System.err.println("Unable to iterate over $namespace - nonexistent!");
        }
    }
}
