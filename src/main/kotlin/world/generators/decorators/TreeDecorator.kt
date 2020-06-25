package world.generators.decorators

import block.BlockRegistration
import block.TilePalette
import block.TileState
import world.World
import java.lang.Exception
import kotlin.random.Random

class TreeDecorator(val log: TileState, val leaves: TileState, val chances: Int, val stayPredicate: (TileState) -> Boolean) : IDecorator {
    override fun decorate(world: World, cX: Int, cZ: Int) {
        for (c in 0 until chances) {
            var x = world.random.nextInt(8, 16)
            var z = world.random.nextInt(8, 16)
            var y = 0
            try {
                y = world.getTopTilePosAdjusted(cX, cZ, x, z)
            } catch (e: Exception) {
                continue
            }

            // TODO: Change cX/cZ if the tree goes out of bounds
            if (stayPredicate(world.getTileAtAdjusted(cX, cZ, x, y, z)!!)) continue
            var height = world.random.nextInt(6, 9)
            for (i in 1..height) {
                world.setTileAtAdjusted(cX, cZ, x, y + i, z, TilePalette.getTileRepresentation(log))
                when (i) {
                    in height-3 until height -> {
                        for (j in -2..2) {
                            for (k in -2..2) {
                                if((Math.abs(j) == 2 || Math.abs(k) == 2) && Math.abs(j) == Math.abs(k)) continue
                                if (world.getTileAtAdjusted(cX, cZ,
                                        x + j,
                                        y + i,
                                        z + k
                                    )?.block != BlockRegistration.AIR
                                ) continue
                                world.setTileAtAdjusted(cX, cZ,
                                    x + j,
                                    y + i,
                                    z + k, TilePalette.getTileRepresentation(leaves)
                                )
                            }
                        }
                    }
                    height -> {
                        for (j in -1..1) {
                            for (k in -1..1) {
                                if((Math.abs(j) == 1 || Math.abs(k) == 1) && Math.abs(j) == Math.abs(k)) continue
                                if (world.getTileAtAdjusted(cX, cZ,
                                        x + j,
                                        y + i,
                                        z + k
                                    )?.block != BlockRegistration.AIR
                                ) continue
                                world.setTileAtAdjusted(cX, cZ,
                                    x + j,
                                    y + i,
                                    z + k, TilePalette.getTileRepresentation(leaves)
                                )
                            }
                        }
                    }
                }
            }
            world.setTileAtAdjusted(cX, cZ, x, y + height + 1, z, TilePalette.getTileRepresentation(leaves))


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