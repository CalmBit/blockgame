package world.generators

import world.World

interface IGenerator {
    fun generate(world: World, cX: Int, cZ: Int)
    fun decorate(world: World, cX: Int, cZ: Int)
}