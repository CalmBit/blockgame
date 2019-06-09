package world.generators.decorators

import world.Chunk
import world.World

interface IDecorator {
    fun decorate(world: World, cX: Int, cZ: Int)
}