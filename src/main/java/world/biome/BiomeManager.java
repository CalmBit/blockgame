package world.biome;

import java.util.ArrayList;
import java.util.List;

public class BiomeManager {
    public static final BiomeManager INSTANCE = new BiomeManager();
    private static final List<BiomeBase> BIOMES = new ArrayList<>();

    private BiomeManager() {
        BIOMES.add(new BiomePlains());
    }

    public void registerBiome(BiomeBase base) {
        base.setID(BIOMES.size());
        BIOMES.add(base);
    }
}
