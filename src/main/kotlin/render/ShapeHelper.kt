package render

import block.EnumDirection
import block.TileState
import world.World

object ShapeHelper {
    var uvSize = 0.0625f

    fun appendVerts(world: World, cX: Int, cZ: Int, rY: Int, x: Int, y: Int, z: Int, tileState: TileState, arr: MutableList<Float>) {
        val side = tileState.getUVForFace(EnumDirection.NORTH)
        val u = side.u
        val v = side.v

        val bot = tileState.getUVForFace(EnumDirection.DOWN)
        val bu = bot.u
        val bv = bot.v

        val top = tileState.getUVForFace(EnumDirection.UP)
        val tu = top.u
        val tv = top.v

        val vX = x.toFloat()
        val vY = y.toFloat()
        val vZ = z.toFloat()

        var verts: MutableList<Float> = mutableListOf()


        if(tileState.block.shouldRenderFace(world, cX, cZ, rY, x, y, z, EnumDirection.SOUTH)) {
            // South
            var sV = floatArrayOf(
            vX,       vY,       vZ,       1.0f, 1.0f, 1.0f, (u*uvSize)+uvSize, (v*uvSize)+uvSize,
            vX,       vY+1.0f,  vZ,       1.0f, 1.0f, 1.0f, (u*uvSize)+uvSize, (v*uvSize),
            vX+1.0f,  vY+1.0f,  vZ,       1.0f, 1.0f, 1.0f, (u*uvSize), (v*uvSize),
            vX+1.0f,  vY+1.0f,  vZ,       1.0f, 1.0f, 1.0f, (u*uvSize), (v*uvSize),
            vX+1.0f,  vY,       vZ,       1.0f, 1.0f, 1.0f, (u*uvSize), (v*uvSize)+uvSize,
            vX,       vY,       vZ,       1.0f, 1.0f, 1.0f, (u*uvSize)+uvSize, (v*uvSize)+uvSize)
            for(v in sV) {
                verts.add(v)
            }
        }

        if(tileState.block.shouldRenderFace(world, cX, cZ, rY, x, y, z, EnumDirection.NORTH)) {
            // North
            var nV = floatArrayOf(
            vX,       vY,       vZ+1.0f,  1.0f, 1.0f, 1.0f, (u*uvSize), (v*uvSize)+uvSize,
            vX+1.0f,  vY,       vZ+1.0f,  1.0f, 1.0f, 1.0f, (u*uvSize)+uvSize, (v*uvSize)+uvSize,
            vX+1.0f,  vY+1.0f,  vZ+1.0f,  1.0f, 1.0f, 1.0f, (u*uvSize)+uvSize, (v*uvSize),
            vX+1.0f,  vY+1.0f,  vZ+1.0f,  1.0f, 1.0f, 1.0f, (u*uvSize)+uvSize, (v*uvSize),
            vX,       vY+1.0f,  vZ+1.0f,  1.0f, 1.0f, 1.0f, (u*uvSize), (v*uvSize),
            vX,       vY,       vZ+1.0f,  1.0f, 1.0f, 1.0f, (u*uvSize), (v*uvSize)+uvSize)
            for(v in nV) {
                verts.add(v)
            }
        }

        if(tileState.block.shouldRenderFace(world, cX, cZ, rY, x, y, z, EnumDirection.EAST)) {
            // East
            var eV = floatArrayOf(
            vX,       vY+1.0f,  vZ+1.0f,  1.0f, 1.0f, 1.0f, (u*uvSize)+uvSize, (v*uvSize),
            vX,       vY+1.0f,  vZ,       1.0f, 1.0f, 1.0f, (u*uvSize), (v*uvSize),
            vX,       vY,       vZ,       1.0f, 1.0f, 1.0f, (u*uvSize), (v*uvSize)+uvSize,
            vX,       vY,       vZ,       1.0f, 1.0f, 1.0f, (u*uvSize), (v*uvSize)+uvSize,
            vX,       vY,       vZ+1.0f,  1.0f, 1.0f, 1.0f, (u*uvSize)+uvSize, (v*uvSize)+uvSize,
            vX,       vY+1.0f,  vZ+1.0f,  1.0f, 1.0f, 1.0f, (u*uvSize)+uvSize, (v*uvSize))
            for(v in eV) {
                verts.add(v)
            }
        }

        if(tileState.block.shouldRenderFace(world, cX, cZ, rY, x, y, z, EnumDirection.WEST)) {
            // West
            var wV = floatArrayOf(
            vX+1.0f,  vY+1.0f,  vZ+1.0f,  1.0f, 1.0f, 1.0f, (u*uvSize), (v*uvSize),
            vX+1.0f,  vY,       vZ+1.0f,  1.0f, 1.0f, 1.0f, (u*uvSize), (v*uvSize)+uvSize,
            vX+1.0f,  vY,       vZ,       1.0f, 1.0f, 1.0f, (u*uvSize)+uvSize, (v*uvSize)+uvSize,
            vX+1.0f,  vY,       vZ,       1.0f, 1.0f, 1.0f, (u*uvSize)+uvSize, (v*uvSize)+uvSize,
            vX+1.0f,  vY+1.0f,  vZ,       1.0f, 1.0f, 1.0f, (u*uvSize)+uvSize, (v*uvSize),
            vX+1.0f,  vY+1.0f,  vZ+1.0f,  1.0f, 1.0f, 1.0f, (u*uvSize), (v*uvSize))
            for(v in wV) {
                verts.add(v)
            }
        }

        if(tileState.block.shouldRenderFace(world, cX, cZ, rY, x, y, z, EnumDirection.DOWN)) {
            // Bottom
            var bV = floatArrayOf(
            vX,       vY,       vZ,       1.0f, 1.0f, 1.0f, (bu*uvSize)+uvSize, (bv*uvSize)+uvSize,
            vX+1.0f,  vY,       vZ,       1.0f, 1.0f, 1.0f, (bu*uvSize)+uvSize, (bv*uvSize),
            vX+1.0f,  vY,       vZ+1.0f,  1.0f, 1.0f, 1.0f, (bu*uvSize), (bv*uvSize),
            vX+1.0f,  vY,       vZ+1.0f,  1.0f, 1.0f, 1.0f, (bu*uvSize), (bv*uvSize),
            vX,       vY,       vZ+1.0f,  1.0f, 1.0f, 1.0f, (bu*uvSize), (bv*uvSize)+uvSize,
            vX,       vY,       vZ,       1.0f, 1.0f, 1.0f, (bu*uvSize)+uvSize, (bv*uvSize)+uvSize)
            for(v in bV) {
                verts.add(v)
            }
        }

        if(tileState.block.shouldRenderFace(world, cX, cZ, rY, x, y, z, EnumDirection.UP)) {
            // Top
            var tV = floatArrayOf(
            vX,       vY+1.0f,  vZ,       1.0f, 1.0f, 1.0f, (tu*uvSize), (tv*uvSize),
            vX,       vY+1.0f,  vZ+1.0f,  1.0f, 1.0f, 1.0f, (tu*uvSize)+uvSize, (tv*uvSize),
            vX+1.0f,  vY+1.0f,  vZ+1.0f,  1.0f, 1.0f, 1.0f, (tu*uvSize)+uvSize, (tv*uvSize)+uvSize,
            vX+1.0f,  vY+1.0f,  vZ+1.0f,  1.0f, 1.0f, 1.0f, (tu*uvSize)+uvSize, (tv*uvSize)+uvSize,
            vX+1.0f,  vY+1.0f,  vZ,       1.0f, 1.0f, 1.0f, (tu*uvSize), (tv*uvSize)+uvSize,
            vX,       vY+1.0f,  vZ,       1.0f, 1.0f, 1.0f, (tu*uvSize), (tv*uvSize))
            for(v in tV) {
                verts.add(v)
            }
        }


        for(i in 0 until verts.size) {
            arr.add(verts[i])
        }
    }
}

/* Original cube data, for posterity:
            // North
            -0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 1.0f, (u*uvSize)+uvSize, (v*uvSize)+uvSize,
            -0.5f, 0.5f, -0.5f, 1.0f, 1.0f, 1.0f, (u*uvSize)+uvSize, (v*uvSize),
            0.5f,  0.5f, -0.5f, 1.0f, 1.0f, 1.0f, (u*uvSize), (v*uvSize),
            0.5f,  0.5f, -0.5f, 1.0f, 1.0f, 1.0f, (u*uvSize), (v*uvSize),
            0.5f,  -0.5f, -0.5f, 1.0f, 1.0f, 1.0f, (u*uvSize), (v*uvSize)+uvSize,
            -0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 1.0f, (u*uvSize)+uvSize, (v*uvSize)+uvSize,

            // South
            -0.5f, -0.5f,  0.5f, 1.0f, 1.0f, 1.0f, (u*uvSize), (v*uvSize)+uvSize,
            0.5f, -0.5f,  0.5f, 1.0f, 1.0f, 1.0f, (u*uvSize)+uvSize, (v*uvSize)+uvSize,
            0.5f,  0.5f,  0.5f, 1.0f, 1.0f, 1.0f, (u*uvSize)+uvSize, (v*uvSize),
            0.5f,  0.5f,  0.5f, 1.0f, 1.0f, 1.0f, (u*uvSize)+uvSize, (v*uvSize),
            -0.5f,  0.5f,  0.5f, 1.0f, 1.0f, 1.0f, (u*uvSize), (v*uvSize),
            -0.5f, -0.5f,  0.5f, 1.0f, 1.0f, 1.0f, (u*uvSize), (v*uvSize)+uvSize,

            // East
            -0.5f,  0.5f,  0.5f, 1.0f, 1.0f, 1.0f, (u*uvSize)+uvSize, (v*uvSize),
            -0.5f,  0.5f, -0.5f, 1.0f, 1.0f, 1.0f, (u*uvSize), (v*uvSize),
            -0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 1.0f, (u*uvSize), (v*uvSize)+uvSize,
            -0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 1.0f, (u*uvSize), (v*uvSize)+uvSize,
            -0.5f, -0.5f,  0.5f, 1.0f, 1.0f, 1.0f, (u*uvSize)+uvSize, (v*uvSize)+uvSize,
            -0.5f,  0.5f,  0.5f, 1.0f, 1.0f, 1.0f, (u*uvSize)+uvSize, (v*uvSize),

            // West
            0.5f,  0.5f,  0.5f, 1.0f, 1.0f, 1.0f, (u*uvSize), (v*uvSize),
            0.5f, -0.5f, 0.5f, 1.0f, 1.0f, 1.0f, (u*uvSize), (v*uvSize)+uvSize,
            0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 1.0f, (u*uvSize)+uvSize, (v*uvSize)+uvSize,
            0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 1.0f, (u*uvSize)+uvSize, (v*uvSize)+uvSize,
            0.5f,  0.5f, -0.5f, 1.0f, 1.0f, 1.0f, (u*uvSize)+uvSize, (v*uvSize),
            0.5f,  0.5f,  0.5f, 1.0f, 1.0f, 1.0f, (u*uvSize), (v*uvSize),

           // Bottom
           -0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 1.0f, (bu*uvSize)+uvSize, (bv*uvSize)+uvSize,
            0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 1.0f, (bu*uvSize)+uvSize, (bv*uvSize),
            0.5f, -0.5f,  0.5f, 1.0f, 1.0f, 1.0f, (bu*uvSize), (bv*uvSize),
            0.5f, -0.5f,  0.5f, 1.0f, 1.0f, 1.0f, (bu*uvSize), (bv*uvSize),
            -0.5f, -0.5f,  0.5f, 1.0f, 1.0f, 1.0f, (bu*uvSize), (bv*uvSize)+uvSize,
            -0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 1.0f, (bu*uvSize)+uvSize, (bv*uvSize)+uvSize,


           // Top
            -0.5f,  0.5f, -0.5f, 1.0f, 1.0f, 1.0f, (tu*uvSize), (tv*uvSize),
            -0.5f,  0.5f, 0.5f, 1.0f, 1.0f, 1.0f, (tu*uvSize)+uvSize, (tv*uvSize),
            0.5f,  0.5f,  0.5f, 1.0f, 1.0f, 1.0f, (tu*uvSize)+uvSize, (tv*uvSize)+uvSize,
            0.5f,  0.5f,  0.5f, 1.0f, 1.0f, 1.0f, (tu*uvSize)+uvSize, (tv*uvSize)+uvSize,
            0.5f,  0.5f,  -0.5f, 1.0f, 1.0f, 1.0f, (tu*uvSize), (tv*uvSize)+uvSize,
            -0.5f,  0.5f, -0.5f, 1.0f, 1.0f, 1.0f, (tu*uvSize), (tv*uvSize)
        )
 */
