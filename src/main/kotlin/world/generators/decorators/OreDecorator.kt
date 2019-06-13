package world.generators.decorators

import block.BlockRegistration
import block.TilePalette
import block.TileState
import world.World
import kotlin.random.Random

class OreDecorator(val ore: TileState, val chances: Int, val minY: Int, val maxY: Int, val veinSize: Int, val replacePredicate: (TileState) -> Boolean,
                   val scaleFail: Boolean = true) : IDecorator {
    override fun decorate(world: World, cX: Int, cZ: Int) {
        world.random = Random(world.getSeed())
        world.random = Random(world.getSeed()
                + (world.random.nextInt(Int.MAX_VALUE) * cX)
                + (world.random.nextInt(Int.MAX_VALUE) * cZ) xor world.getSeed())
        for (c in 0 until chances) {
            var x = world.random.nextInt(0, 16)
            var y = world.random.nextInt(0, 128)
            var z = +world.random.nextInt(0, 16)

            if(y < minY || y > maxY) continue

            if (world.getTileAt((cX*16)+x, y, (cZ*16)+z)!!.block != BlockRegistration.STONE) continue
            world.setTileAt((cX*16)+x, y, (cZ*16)+z, TilePalette.getTileRepresentation(ore))

            var count = 1


            while (count < veinSize) {

                if(scaleFail && world.random.nextInt(veinSize) < count) {
                    break
                }

                var dX = world.random.nextInt(-1, 2)
                var dY = world.random.nextInt(-1, 2)
                var dZ = world.random.nextInt(-1, 2)

                if (x + dX < 0 || x + dX > 15) {
                    dX = 0
                }

                if (y + dY < 0 || y + dY > 128  || y + dY < minY || y + dY > maxY) {
                    dY = 0
                }

                if (z + dZ < 0 || z + dZ > 15) {
                    dZ = 0
                }

                if (!replacePredicate(world.getTileAt((cX*16)+x + dX, y +dY, (cZ*16)+z + dZ)!!)) {
                    dX = 0
                    dY = 0
                    dZ = 0
                }


                world.setTileAt((cX*16)+x + dX, y + dY, (cZ*16)+z + dZ, TilePalette.getTileRepresentation(ore))
                x += dX
                y += dY
                z += dZ
                count++
            }
        }
    }
}