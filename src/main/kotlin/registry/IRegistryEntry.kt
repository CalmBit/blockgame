package registry

interface IRegistryEntry {
    fun setRegistryName(name: RegistryName)
    fun getRegistryName(): RegistryName
}