package registry

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class RegistryTest {
    var registry = Registry<TRegistryEntry>()

    class TRegistryEntry(var reg: RegistryName) : IRegistryEntry {
        override fun getRegistryName(): RegistryName {
            return this.reg
        }

        override fun setRegistryName(name: RegistryName) {
            this.reg = name
        }

        override fun equals(other: Any?): Boolean = if (other is TRegistryEntry) {
                    reg == other.reg
                } else false

        override fun hashCode(): Int = reg.hashCode()
    }

    //region Registry.register()
    @Test
    fun `Registry should be empty on initialization`() {
        Assertions.assertEquals(0, registry.size())
    }

    @Test
    fun `Registering a single item`() {
        registry.register(TRegistryEntry(RegistryName("test", "testing")))
        Assertions.assertEquals(1, registry.size())
    }

    @Test
    fun `Registering multiple items in the same namespace`() {
        registry.register(TRegistryEntry(RegistryName("test", "testing")))
        registry.register(TRegistryEntry(RegistryName("test", "testing2")))
        Assertions.assertEquals(2, registry.size())
    }

    @Test
    fun `Registering multiple items in different namespaces`() {
        registry.register(TRegistryEntry(RegistryName("test", "testing")))
        registry.register(TRegistryEntry(RegistryName("test2", "testing")))
        Assertions.assertEquals(2, registry.size())
    }

    @Test
    fun `Registering a duplicate key should throw DuplicateKeyException`() {
        registry.register(TRegistryEntry(RegistryName("test", "testing")))
        Assertions.assertThrows(DuplicateKeyException::class.java) {
            registry.register(TRegistryEntry(RegistryName("test", "testing")))
        }
    }
    //endregion

    //region Registry.get()
    @Test
    fun `Getting a single value`() {
        val name = RegistryName("test", "testing")
        val test = TRegistryEntry(name)
        registry.register(test)
        Assertions.assertEquals(test, registry.get(name))
    }

    @Test
    fun `Getting multiple values in the same namespace`() {
        val name = RegistryName("test", "testing")
        val name2 = RegistryName("test", "testing2")
        val test = TRegistryEntry(name)
        val test2 = TRegistryEntry(name2)
        registry.register(test)
        registry.register(test2)
        Assertions.assertEquals(test, registry.get(name))
        Assertions.assertEquals(test2, registry.get(name2))
    }

    @Test
    fun `Getting multiple values in different namespaces`() {
        val name = RegistryName("test", "testing")
        val name2 = RegistryName("test2", "testing")
        val test = TRegistryEntry(name)
        val test2 = TRegistryEntry(name2)
        registry.register(test)
        registry.register(test2)
        Assertions.assertEquals(test, registry.get(name))
        Assertions.assertEquals(test2, registry.get(name2))
    }

    @Test
    fun `Getting a nonexistent value should return null`() {
        val name = RegistryName("test", "testing")
        val invalid = RegistryName("test2", "testing")
        val test = TRegistryEntry(name)
        registry.register(test)
        Assertions.assertEquals(null, registry.get(invalid))
    }
    //endregion
}