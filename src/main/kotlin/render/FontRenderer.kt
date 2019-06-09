package render

import gl.Font
import gl.ShaderProgram
import gl.UVPosition
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL31.*


object FontRenderer {
    var vao: Int
    var vbo: Int
    var font: Font
    var quads: Int = 0
    val verts = mutableListOf<Float>()

    init {
        vao = glGenVertexArrays()
        glBindVertexArray(vao)
        vbo = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        font = Font("font/matchup.png")

        /*val cross = floatArrayOf(
            392.0f, 292.0f, font.uvTable[0], font.uvTable[1],
            392.0f, 308.0f, font.uvTable[0], font.uvTable[3],
            408.0f, 308.0f, font.uvTable[2], font.uvTable[3],
            408.0f, 308.0f, font.uvTable[2], font.uvTable[3],
            408.0f, 292.0f, font.uvTable[2], font.uvTable[0],
            392.0f, 292.0f, font.uvTable[0], font.uvTable[0]
        )*/

    }

    fun renderText(x: Float, y: Float, text: String, scale: Float) {
        var cX = x
        var cY = y
        for (c in text) {
            if (c == '\n') {
                cY += font.height.toFloat() * scale + scale
                cX = x
                continue
            }
            verts.add(cX)
            verts.add(cY)
            verts.add(font.getUVOf(c, UVPosition.U1))
            verts.add(font.getUVOf(c, UVPosition.V1))

            verts.add(cX)
            verts.add(cY + font.height.toFloat() * scale)
            verts.add(font.getUVOf(c, UVPosition.U1))
            verts.add(font.getUVOf(c, UVPosition.V2))

            verts.add(cX + font.getWidthOf(c) * scale)
            verts.add(cY + font.height.toFloat() * scale)
            verts.add(font.getUVOf(c, UVPosition.U2))
            verts.add(font.getUVOf(c, UVPosition.V2))

            verts.add(cX + font.getWidthOf(c) * scale)
            verts.add(cY + font.height.toFloat() * scale)
            verts.add(font.getUVOf(c, UVPosition.U2))
            verts.add(font.getUVOf(c, UVPosition.V2))

            verts.add(cX + font.getWidthOf(c) * scale)
            verts.add(cY)
            verts.add(font.getUVOf(c, UVPosition.U2))
            verts.add(font.getUVOf(c, UVPosition.V1))

            verts.add(cX)
            verts.add(cY)
            verts.add(font.getUVOf(c, UVPosition.U1))
            verts.add(font.getUVOf(c, UVPosition.V1))

            cX += font.getWidthOf(c) * scale + (scale)
        }

        quads = verts.size / 4

        glBufferData(GL_ARRAY_BUFFER, verts.toFloatArray(), GL_DYNAMIC_DRAW)
    }

    fun buildAttribs(prog: ShaderProgram) {
        glBindVertexArray(vao)
        glBindBuffer(GL_ARRAY_BUFFER, vbo)

        prog.use()

        var posAttrib = glGetAttribLocation(prog.program, "position")
        glEnableVertexAttribArray(posAttrib)
        glVertexAttribPointer(posAttrib, 2, GL_FLOAT, false, 4 * 4, 0L)

        var texAttrib = glGetAttribLocation(prog.program, "texcoord")
        glEnableVertexAttribArray(texAttrib)
        glVertexAttribPointer(texAttrib, 2, GL_FLOAT, false, 4 * 4, (2 * 4))
    }

    fun draw() {
        font.use()
        glBindVertexArray(vao)
        glDrawArrays(GL11.GL_TRIANGLES, 0, quads)
        verts.clear()
    }
}