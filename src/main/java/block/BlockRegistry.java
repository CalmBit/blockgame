package block;

import gl.UVPair;
import registry.Registry;
import registry.RegistryName;

public class BlockRegistry {
    private static final Registry<Block> _REGISTRY = new Registry<>();

    public static final Block AIR = register(new BlockAir());
    public static final Block DIRT = register(new BlockDirt().setUV(new UVPair(0, 0)));
    public static final Block GRASS = register(new BlockGrass());
    public static final Block STONE = register(new Block(new RegistryName("blockgame", "stone")).setUV(new UVPair(3, 0)));
    public static final Block GRAVEL = register(new Block(new RegistryName("blockgame", "gravel")).setUV(new UVPair(4, 0)));
    public static final Block SAND = register(new Block(new RegistryName("blockgame", "sand")).setUV(new UVPair(5, 0)));
    public static final Block BRICK = register(new Block(new RegistryName("blockgame", "brick")).setUV(new UVPair(6, 0)));
    public static final Block COAL_ORE = register(new BlockOre(new RegistryName("blockgame", "coal_ore")).setUV(new UVPair(7, 0)));
    public static final Block IRON_ORE = register(new BlockOre(new RegistryName("blockgame", "iron_ore")).setUV(new UVPair(8, 0)));
    public static final Block GOLD_ORE = register(new BlockOre(new RegistryName("blockgame", "gold_ore")).setUV(new UVPair(9, 0)));
    public static final Block PLANKS = register(new Block(new RegistryName("blockgame", "planks")).setUV(new UVPair(10, 0)));
    public static final Block WATER = register(new BlockWater().setUV(new UVPair(11, 0)));
    public static final Block DIAMOND_ORE = register(new BlockOre(new RegistryName("blockgame", "diamond_ore")).setUV(new UVPair(12, 0)));
    public static final Block LAVA = register(new BlockLava().setUV(new UVPair(13, 0)));
    public static final Block LOG = register(new BlockLog(LogType.OAK));
    public static final Block LEAVES = register(new BlockLeaves(LogType.OAK));
    public static final Block GLASS = register(new BlockGlass().setUV(new UVPair(1,1)));
    public static final Block BORDERSTONE = register(new Block(new RegistryName("blockgame", "borderstone")).setUV(new UVPair(2,1)));
    public static final Block COBBLESTONE = register(new Block(new RegistryName("blockgame", "cobblestone")).setUV(new UVPair(3, 1)));
    public static final Block MOSS_COBBLE = register(new Block(new RegistryName("blockgame", "mossy_cobblestone")).setUV(new UVPair(4, 1)));
    public static final Block OBSIDIAN = register(new Block(new RegistryName("blockgame", "obsidian")).setUV(new UVPair(8, 1)));
    public static final Block LAVA_OBSIDIAN = register(new BlockMagmaObsidian().setUV(new UVPair(9, 1)));
    public static final Block MONSTER_SPAWNER = register(new BlockMonsterSpawner().setUV(new UVPair(10, 1)));

    public static final Block BIRCH_LOG = register(new BlockLog(LogType.BIRCH));
    public static final Block BIRCH_LEAVES = register(new BlockLeaves(LogType.BIRCH));

    public static final Block UVTEST = register(new Block(new RegistryName("blockgame", "uvtest")).setUV(new UVPair(14, 15)));


    private static Block register(Block block) {
        return _REGISTRY.register(block);
    }

}
