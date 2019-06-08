package world

import block.BlockRegistration
import block.TileState
import gl.ShaderProgram
import kotlin.random.Random

class Chunk(val cX: Int, val cZ: Int) {
    private var _regions: Array<Region?> = Array(8) {null}

    fun getTileAt(x: Int, y: Int, z: Int): TileState? {
        if(x > 15 || x < 0 || z > 15 || z < 0 || y < 0 || y > 127) return null

        val r = y / 16
        if(_regions.size >= r) {
            return _regions[r]!!.getTileAt(x, y%16, z)
        }
        return null
    }

    init {
        for(i in 0 until _regions.size) {
            _regions[i] = Region(cX, i, cZ)
        }

        for(r in 0..7) {
            for (y in 0..15) {
                for (z in 0..15) {
                    for (x in 0..15) {
                        val tile =
                            when(r)  {
                            7 ->
                                when (y) {
                                    15 -> TileState(BlockRegistration.GRASS)
                                    in 10..14 -> TileState(BlockRegistration.DIRT)
                                    else -> {
                                        TileState(BlockRegistration.STONE)
                                    }
                                }
                            else -> TileState(BlockRegistration.STONE)
                        }

                        _regions[r]!!.setTileAt(x, y, z, tile)
                    }
                }
            }
        }

        for(r in 0..5) {
            for (chances in 0..15) {
                if(Random.nextInt(100) < 75) continue

                var tile: TileState = when(Random.nextInt(100)) {
                        in 0..75 -> TileState(BlockRegistration.COAL_ORE)
                        in 76..90 -> TileState(BlockRegistration.IRON_ORE)
                        else -> TileState(BlockRegistration.GOLD_ORE)
                }

                var cX = Random.nextInt(0,16)
                var cY = Random.nextInt(0,16)
                var cZ = Random.nextInt(0,16)

                _regions[r]!!.setTileAt(cX, cY, cZ, tile)

                var failed = false
                var count = 0


                while(count < 8) {
                    var dX = Random.nextInt(-1, 2)
                    var dY = Random.nextInt(-1, 2)
                    var dZ = Random.nextInt(-1, 2)

                    if(cX + dX < 0 || cX + dX > 15) {
                        failed = true
                        break
                    }

                    if(cY + dY < 0 || cY + dY > 15) {
                        failed = true
                        break
                    }

                    if(cZ + dZ < 0 || cZ + dZ > 15) {
                        failed = true
                        break
                    }

                    _regions[r]!!.setTileAt(cX, cY, cZ, tile)
                    cX += dX
                    cY += dY
                    cZ += dZ
                    count++
                }
            }
        }
    }

    fun buildRenderData(world: World, prog: ShaderProgram) {
        for(i in 0 until _regions.size) {
            _regions[i]!!.buildRenderData(world, this.cX, this.cZ, prog)
        }
    }

    fun draw(uniTrans: Int, timer: Float) {
        for(i in 0 until _regions.size) {
            _regions[i]!!.draw(uniTrans, timer)
        }
    }
}