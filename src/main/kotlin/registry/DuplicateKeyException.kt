package registry

class DuplicateKeyException(registry: Registry<*>, reg: RegistryName)
    : Exception("Duplicate key in ${registry.javaClass.name} - '$reg'") {
    override val message: String?
        get() = super.message
}
