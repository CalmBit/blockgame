package world

import block.EnumRenderLayer
import block.TilePalette
import block.TileState
import gl.ShaderProgram
import org.joml.Matrix4f
import org.lwjgl.opengl.GL31.*
import org.lwjgl.system.MemoryStack
import render.ShapeHelper
import util.FloatList
import util.IntList
import kotlin.random.Random


class Region(val rX: Int, val rY: Int, val rZ: Int) {
    private var _region: IntList = IntList(16*16*16)
    var vao: IntList = IntList(EnumRenderLayer.VALUES.size)
    var vbo: IntList = IntList(EnumRenderLayer.VALUES.size)
    var trans = Matrix4f()
    var vertSize: IntList = IntList(EnumRenderLayer.VALUES.size)

    init {
        for(l in EnumRenderLayer.VALUES) {
            vao[l.ordinal] = glGenVertexArrays()
            glBindVertexArray(vao[l.ordinal])
            vbo[l.ordinal] = glGenBuffers()
            glBindBuffer(GL_ARRAY_BUFFER, vbo[l.ordinal])
        }

        trans = Matrix4f()
            .translate(rX*16.0f, rY*16.0f, rZ * 16.0f)
    }

    fun buildRenderData(world: World, cX: Int, cZ: Int, l: EnumRenderLayer): FloatList {
        val verts = FloatList()
        for (y in 0..15) {
            for (z in 0..15) {
                for (x in 0..15) {
                    var t = getTileAt(x, y, z)
                    if (!t!!.shouldRender() || t.renderLayer() != l) continue
                    ShapeHelper.appendVerts(world, cX, cZ, rY, x, y, z, t, verts)
                }
            }
        }
        return verts
    }

    fun bindData(verts: FloatList, l: EnumRenderLayer, prog: ShaderProgram) {
        vertSize[l.ordinal] = verts.length
        glBindVertexArray(vao[l.ordinal])
        glBindBuffer(GL_ARRAY_BUFFER, vbo[l.ordinal])
        glBufferData(GL_ARRAY_BUFFER, verts.store, GL_STATIC_DRAW)
        setupAttribs(vao[l.ordinal], vbo[l.ordinal], prog)
    }


    fun getTileAt(x: Int, y: Int, z: Int): TileState? = TilePalette.getTileState(_region[(y*(16*16))+(z*16)+x])
    fun setTileAt(x: Int, y: Int, z: Int, tile: Int) {
        _region[(y*(16*16))+(z*16)+x] = tile
    }

    fun draw(l: EnumRenderLayer, uniTrans: Int, timer: Float) {
        var stack: MemoryStack? = null
        try {
            if(vertSize[l.ordinal] == 0) return
            stack = MemoryStack.stackPush()
            glBindVertexArray(vao[l.ordinal])
            glBindBuffer(GL_ARRAY_BUFFER, vbo[l.ordinal])
            glUniformMatrix4fv(uniTrans, false, trans.get(stack.mallocFloat(16)))
            glDrawArrays(GL_TRIANGLES, 0, vertSize[l.ordinal] / 8)
        } finally {
            stack?.pop()
        }
    }

    fun setupAttribs(vao: Int, vbo: Int, prog: ShaderProgram) {
        glBindVertexArray(vao)
        glBindBuffer(GL_ARRAY_BUFFER, vbo)

        prog.use()

        var posAttrib = glGetAttribLocation(prog.getProgram(), "position")
        glEnableVertexAttribArray(posAttrib)
        glVertexAttribPointer(posAttrib, 3, GL_FLOAT, false, 8*4, 0L)

        var colAttrib = glGetAttribLocation(prog.getProgram(), "color")
        glEnableVertexAttribArray(colAttrib)
        glVertexAttribPointer(colAttrib, 3, GL_FLOAT, false, 8*4, 3*4L)

        var texAttrib = glGetAttribLocation(prog.getProgram(), "texcoord")
        glEnableVertexAttribArray(texAttrib)
        glVertexAttribPointer(texAttrib, 2, GL_FLOAT, false, 8*4, 6*4L)
    }

    fun tick(world: World) {
        for(i in 0 until 3) {
            var x = Random.nextInt(0, 16)
            var y = Random.nextInt(0, 16)
            var z = Random.nextInt(0, 16)

            var t = getTileAt(x, y, z)
            t!!.block.tick(world, rX, rY, rZ, x, y, z)
        }
    }
}