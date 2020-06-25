package world.generators.decorators

import world.Chunk
import world.World
import java.util.*

interface IDecorator {
    fun decorate(world: World, cX: Int, cZ: Int)
}