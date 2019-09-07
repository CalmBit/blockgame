package world.biome

object BiomeManager {
    val biomes: MutableList<BiomeBase> = mutableListOf()

    init {
        biomes.add(BiomePlains())
    }

    fun addBiome(biome: BiomeBase) {
        biome.ID = biomes.count()
        biomes.add(biome)
    }
}