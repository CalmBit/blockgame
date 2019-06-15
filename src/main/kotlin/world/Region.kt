package world

import block.BlockRegistration
import block.RenderType
import block.TilePalette
import block.TileState
import gl.ShaderProgram
import org.joml.Matrix4f
import org.lwjgl.opengl.GL31.*
import org.lwjgl.system.MemoryStack
import render.ShapeHelper
import kotlin.random.Random


class Region(val rX: Int, val rY: Int, val rZ: Int) {
    private var _region: Array<Int> = Array(16*16*16) { 0 }
    var vao: Array<Int> = Array(RenderType.values.size) {0}
    var vbo: Array<Int> = Array(RenderType.values.size) {0}
    var trans = Matrix4f()
    var vertSize: Array<Int> = Array(RenderType.values.size) {0}

    init {
        for(l in RenderType.values) {
            vao[l.ordinal] = glGenVertexArrays()
            glBindVertexArray(vao[l.ordinal])
            vbo[l.ordinal] = glGenBuffers()
            glBindBuffer(GL_ARRAY_BUFFER, vbo[l.ordinal])
        }

        trans = Matrix4f()
            .translate(rX*16.0f, rY*16.0f, rZ * 16.0f)
    }

    fun buildRenderData(world: World, cX: Int, cZ: Int, l: RenderType): MutableList<Float> {
        var verts: MutableList<Float> = mutableListOf()
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

    fun bindData(verts: MutableList<Float>, l: RenderType,  prog: ShaderProgram) {
        vertSize[l.ordinal] = verts.size
        glBindVertexArray(vao[l.ordinal])
        glBindBuffer(GL_ARRAY_BUFFER, vbo[l.ordinal])
        glBufferData(GL_ARRAY_BUFFER, verts.toFloatArray(), GL_STATIC_DRAW)
        setupAttribs(vao[l.ordinal], vbo[l.ordinal], prog)
    }


    fun getTileAt(x: Int, y: Int, z: Int): TileState? = TilePalette.getTileState(_region[(y*(16*16))+(z*16)+x])
    fun setTileAt(x: Int, y: Int, z: Int, tile: Int) {
        _region[(y*(16*16))+(z*16)+x] = tile
    }

    fun draw(l: RenderType, uniTrans: Int, timer: Float) {
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

        var posAttrib = glGetAttribLocation(prog.program, "position")
        glEnableVertexAttribArray(posAttrib)
        glVertexAttribPointer(posAttrib, 3, GL_FLOAT, false, 8*4, 0L)

        var colAttrib = glGetAttribLocation(prog.program, "color")
        glEnableVertexAttribArray(colAttrib)
        glVertexAttribPointer(colAttrib, 3, GL_FLOAT, false, 8*4, 3*4L)

        var texAttrib = glGetAttribLocation(prog.program, "texcoord")
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