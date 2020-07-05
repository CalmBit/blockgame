package blockgame.world.biome;

import java.util.ArrayList;
import java.util.List;

public class BiomeManager {
    private static final List<BiomeBase> BIOMES = new ArrayList<>();

    static {
        registerBiome(new BiomePlains());
    }

    private static void registerBiome(BiomeBase base) {
        base.setID(BIOMES.size());
        BIOMES.add(base);
    }
}
