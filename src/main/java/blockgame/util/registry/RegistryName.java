package blockgame.util.registry;

public class RegistryName {
    private String _namespace;
    private String _name;

    public RegistryName(String namespace, String name) {
        _namespace = namespace;
        _name = name;
    }

    @Override
    public String toString() {
        return _namespace+":"+_name;
    }

    @Override
    public boolean equals(Object obj) {
        return toString().equals(obj.toString());
    }

    public int hashCode() {
        int result = _namespace.hashCode();
        result = 31 * result + _name.hashCode();
        return result;
    }

    public String getNamespace() {
        return _namespace;
    }

    public String getName() {
        return _name;
    }
}
