package blockgame.registry;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RegistryTest {
    Registry<TRegistryEntry> registry = new Registry<>();

    static class TRegistryEntry implements IRegistryEntry {

        private RegistryName _reg;

        public TRegistryEntry(RegistryName reg) {
            _reg = reg;
        }

        @Override
        public void setRegistryName(RegistryName reg) {
            _reg = reg;
        }

        @Override
        public RegistryName getRegistryName() {
            return _reg;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof TRegistryEntry) {
                TRegistryEntry other = (TRegistryEntry)obj;
                return other._reg == _reg;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return _reg.hashCode();
        }
    }

    //region Registry.register()
    @Test
    @DisplayName("Registry is empty on initialization")
    void registryEmptyOnInit() {
        Assertions.assertEquals(0, registry.size());
    }

    @Test
    @DisplayName("Registering a single item increments size")
    void registerSingleItem() throws DuplicateKeyException {
        registry.register(new TRegistryEntry(new RegistryName("test", "testing")));
        Assertions.assertEquals(1, registry.size());
    }

    @Test
    @DisplayName("Registering a multiple items in same namespace works correctly")
    void registerMultipleSameNamespace() throws DuplicateKeyException {
        registry.register(new TRegistryEntry(new RegistryName("test", "testing")));
        registry.register(new TRegistryEntry(new RegistryName("test", "testing2")));
        Assertions.assertEquals(2, registry.size());
    }

    @Test
    @DisplayName("Registering a multiple items in different namespaces works correctly")
    void registerMultipleDifferentNamespace() throws DuplicateKeyException {
        registry.register(new TRegistryEntry(new RegistryName("test", "testing")));
        registry.register(new TRegistryEntry(new RegistryName("test2", "testing")));
        Assertions.assertEquals(2, registry.size());
    }

    @Test
    @DisplayName("Registering a duplicate throws an exception")
    void registerDuplicateThrowsException() throws DuplicateKeyException {
        registry.register(new TRegistryEntry(new RegistryName("test", "testing")));
        Assertions.assertThrows(DuplicateKeyException.class, () -> registry.register(new TRegistryEntry(new RegistryName("test", "testing"))));
    }
    //endregion

    //region Registry.get()
    @Test
    @DisplayName("Registering and retrieving a single value works correctly")
    void getSingleValue() throws DuplicateKeyException {
        RegistryName name = new RegistryName("test", "testing");
        TRegistryEntry test = new TRegistryEntry(name);
        registry.register(test);
        Assertions.assertEquals(test, registry.get(name));
    }

    @Test
    @DisplayName("Registering and retrieving multiple values in the same namespace works correctly")
    void getMultipleSameNamespace() throws DuplicateKeyException {
        RegistryName name = new RegistryName("test", "testing");
        RegistryName name2 = new RegistryName("test", "testing2");
        TRegistryEntry test = new TRegistryEntry(name);
        TRegistryEntry test2 = new TRegistryEntry(name2);
        registry.register(test);
        registry.register(test2);
        Assertions.assertEquals(test, registry.get(name));
        Assertions.assertEquals(test2, registry.get(name2));
    }

    @Test
    @DisplayName("Registering and retrieving multiple values in different namespaces works correctly")
    void getMultipleDifferentNamespace() throws DuplicateKeyException {
        RegistryName name = new RegistryName("test", "testing");
        RegistryName name2 = new RegistryName("test2", "testing");
        TRegistryEntry test = new TRegistryEntry(name);
        TRegistryEntry test2 = new TRegistryEntry(name2);
        registry.register(test);
        registry.register(test2);
        Assertions.assertEquals(test, registry.get(name));
        Assertions.assertEquals(test2, registry.get(name2));
    }

    @Test
    @DisplayName("Retrieving a non-existant value returns nul")
    void gettingNullReturnsNull() throws DuplicateKeyException {
        RegistryName name = new RegistryName("test", "testing");
        RegistryName invalid = new RegistryName("test2", "testing");
        TRegistryEntry test = new TRegistryEntry(name);
        registry.register(test);
        Assertions.assertEquals(null, registry.get(invalid));
    }
    //endregion
}
