package world.generators.decorators

import block.BlockRegistration
import block.TileState
import world.World
import java.lang.Exception
import kotlin.random.Random

class TreeDecorator(val log: TileState, val leaves: TileState, val chances: Int) : IDecorator {
    override fun decorate(world: World, cX: Int, cZ: Int) {
        world.random = Random(world.getSeed())
        world.random = Random(
            world.getSeed()
                    + (world.random.nextInt(Int.MAX_VALUE) * cX)
                    + (world.random.nextInt(Int.MAX_VALUE) * cZ) xor world.getSeed().inv()
        )
        for (c in 0 until chances) {
            var x = world.random.nextInt(-8, 8)
            var z = +world.random.nextInt(-8, 8)
            var y = 0
            try {
                y = world.getTopTilePos((cX * 16) + x, (cZ * 16) + z)
            } catch (e: Exception) {
                continue
            }

            if (world.getTileAt((cX * 16) + x, y, (cZ * 16) + z)!!.block != BlockRegistration.GRASS) continue
            var height = world.random.nextInt(6, 9)
            for (i in 1..height) {
                world.setTileAt((cX * 16) + x, y + i, (cZ * 16) + z, log)
                when (i) {
                    in height-3 until height -> {
                        for (j in -2..2) {
                            for (k in -2..2) {
                                if((Math.abs(j) == 2 || Math.abs(k) == 2) && Math.abs(j) == Math.abs(k)) continue
                                if (world.getTileAt(
                                        (cX * 16) + x + j,
                                        y + i,
                                        (cZ * 16) + z + k
                                    )?.block != BlockRegistration.AIR
                                ) continue
                                world.setTileAt(
                                    (cX * 16) + x + j,
                                    y + i,
                                    (cZ * 16) + z + k, leaves
                                )
                            }
                        }
                    }
                    height -> {
                        for (j in -1..1) {
                            for (k in -1..1) {
                                if((Math.abs(j) == 1 || Math.abs(k) == 1) && Math.abs(j) == Math.abs(k)) continue
                                if (world.getTileAt(
                                        (cX * 16) + x + j,
                                        y + i,
                                        (cZ * 16) + z + k
                                    )?.block != BlockRegistration.AIR
                                ) continue
                                world.setTileAt(
                                    (cX * 16) + x + j,
                                    y + i,
                                    (cZ * 16) + z + k, leaves
                                )
                            }
                        }
                    }
                }
            }
            world.setTileAt((cX * 16) + x, y + height + 1, (cZ * 16) + z, leaves)


            /*var count = 1


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


                world.setTileAt((cX*16)+x + dX, y + dY, (cZ*16)+z + dZ, ore)
                x += dX
                y += dY
                z += dZ
                count++
            }*/
        }
    }
}