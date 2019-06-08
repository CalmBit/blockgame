package registry

data class RegistryName(val namespace: String, val name: String) {
    override fun toString(): String {
        return "$namespace:$name"
    }

    override fun equals(other: Any?): Boolean {
        return other.toString() == this.toString()
    }

    override fun hashCode(): Int {
        var result = namespace.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}