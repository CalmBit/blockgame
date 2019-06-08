package registry

import kotlin.random.Random

class Registry<T: IRegistryEntry> {
    private var _dict: MutableMap<String, MutableMap<String, T>> = mutableMapOf()
    fun register(v: T): T {
        val reg = v.getRegistryName()
        if (!_dict.containsKey(reg.namespace)) {
            _dict[reg.namespace] = mutableMapOf()
        }
        if(_dict[reg.namespace]!!.containsKey(reg.name)) {
            throw DuplicateKeyException(this, reg)
        }
        _dict[reg.namespace]!![reg.name] = v
        return v
    }

    fun get(name: RegistryName): T? {
        return try {
            _dict[name.namespace]!![name.name]
        } catch (e: Exception) {
            null
        }
    }

    fun size(): Int {
        return _dict.values.fold(0, {s, e -> s + e.size})
    }

    fun iterateOverNamespace(namespace: String, op: (String, T)-> Unit) {
        try {
            _dict[namespace]!!.forEach { (s, t) -> op(s,t) }
        } catch (e: Exception) {
            Logger.logger.error("Unable to iterate over $namespace - nonexistent!")
        }
    }

    fun getRandom(namespace: String): T {
        var list = _dict[namespace]!!.entries
        return list.elementAt(Random.nextInt(list.size)).value
    }
}