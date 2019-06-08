package block

import registry.Registry
import registry.RegistryName
import kotlin.random.Random

object BlockRegistration {
    var blockRegistry = Registry<Block>()

    val AIR = register(BlockAir())
    val DIRT = register(Block(RegistryName("blockgame", "dirt")).setUV(UVPair(0,0)))
    val GRASS = register(BlockGrass(RegistryName("blockgame", "grass")))
    var STONE = register(Block(RegistryName("blockgame", "stone")).setUV(UVPair(3, 0)))
    var GRAVEL = register(Block(RegistryName("blockgame", "gravel")).setUV(UVPair(4,0)))
    var SAND = register(Block(RegistryName("blockgame", "sand")).setUV(UVPair(5, 0)))
    var BRICK = register(Block(RegistryName("blockgame", "brick")).setUV(UVPair(6,0)))
    var COAL_ORE = register(BlockOre(RegistryName("blockgame", "coal_ore")).setUV(UVPair(7,0)))
    var IRON_ORE = register(BlockOre(RegistryName("blockgame", "iron_ore")).setUV(UVPair(8,0)))
    var GOLD_ORE = register(BlockOre(RegistryName("blockgame", "gold_ore")).setUV(UVPair(9,0)))
    var PLANKS = register(Block(RegistryName("blockgame", "planks")).setUV(UVPair(10, 0)))

    fun register(block: Block): Block = blockRegistry.register(block)
}