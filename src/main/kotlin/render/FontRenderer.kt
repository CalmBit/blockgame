package render

import gl.*
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL31.*
import org.lwjgl.system.MemoryStack
import java.io.File


object FontRenderer {
    var vao: Int
    var vbo: Int
    var font: Font
    var quads: Int = 0
    val verts = mutableListOf<Float>()
    var baked_verts = floatArrayOf()

    val fontHeight: Float
    val fontWidths: FloatArray

    var fontShader: ShaderProgram
    var fontProj: Int

    val SHADOW = Vector3f(0.45f, 0.45f, 0.45f)
    val WHITE = Vector3f(1.0f, 1.0f, 1.0f)

    init {
        vao = glGenVertexArrays()
        glBindVertexArray(vao)
        vbo = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        font = Font(File("font", "alphabet.png"))

        fontHeight = font.height.toFloat()
        fontWidths = FloatArray(font.fontTable.length) {
            font.getWidthOf(font.fontTable[it]).toFloat()
        }

        var textVert= VertexShader(File("shader", "text.vert"))
        var textFrag = FragmentShader(File("shader", "text.frag"))
        fontShader = ShaderProgram(textVert, textFrag)

        fontShader.use()

        fontProj = glGetUniformLocation(fontShader.program, "proj")

        var posAttrib = glGetAttribLocation(fontShader.program, "position")
        glEnableVertexAttribArray(posAttrib)
        glVertexAttribPointer(posAttrib, 2, GL_FLOAT, false, 7 * 4, 0L)

        var colorAttrib = glGetAttribLocation(fontShader.program, "color")
        glEnableVertexAttribArray(colorAttrib)
        glVertexAttribPointer(colorAttrib, 3, GL_FLOAT, false, 7 * 4, (2 * 4))

        var texAttrib = glGetAttribLocation(fontShader.program, "texcoord")
        glEnableVertexAttribArray(texAttrib)
        glVertexAttribPointer(texAttrib, 2, GL_FLOAT, false, 7 * 4, (5 * 4))
    }

    fun renderWithShadow(x: Float, y: Float, text: String, scale: Float, color: Vector3f = WHITE) {
        renderText(x+(1.0f),y+(1.0f),text,scale,SHADOW)
        renderText(x,y,text,scale,color)
    }

    fun renderText(x: Float, y: Float, text: String, scale: Float, color: Vector3f = WHITE) {
        glBindBuffer(GL_ARRAY_BUFFER, vbo)

        var cX = x
        var cY = y
        val height = fontHeight * scale

        for (c in text) {
            if (c == '\n') {
                cY += height + scale
                cX = x
                continue
            }

            val width = getFontWidth(c) * scale

            verts.add(cX)
            verts.add(cY)
            verts.add(color.x)
            verts.add(color.y)
            verts.add(color.z)
            verts.add(font.getUVOf(c, UVPosition.U1))
            verts.add(font.getUVOf(c, UVPosition.V1))

            verts.add(cX)
            verts.add(cY + height)
            verts.add(color.x)
            verts.add(color.y)
            verts.add(color.z)
            verts.add(font.getUVOf(c, UVPosition.U1))
            verts.add(font.getUVOf(c, UVPosition.V2))

            verts.add(cX + width)
            verts.add(cY + height)
            verts.add(color.x)
            verts.add(color.y)
            verts.add(color.z)
            verts.add(font.getUVOf(c, UVPosition.U2))
            verts.add(font.getUVOf(c, UVPosition.V2))

            verts.add(cX + width)
            verts.add(cY + height)
            verts.add(color.x)
            verts.add(color.y)
            verts.add(color.z)
            verts.add(font.getUVOf(c, UVPosition.U2))
            verts.add(font.getUVOf(c, UVPosition.V2))

            verts.add(cX + width)
            verts.add(cY)
            verts.add(color.x)
            verts.add(color.y)
            verts.add(color.z)
            verts.add(font.getUVOf(c, UVPosition.U2))
            verts.add(font.getUVOf(c, UVPosition.V1))

            verts.add(cX)
            verts.add(cY)
            verts.add(color.x)
            verts.add(color.y)
            verts.add(color.z)
            verts.add(font.getUVOf(c, UVPosition.U1))
            verts.add(font.getUVOf(c, UVPosition.V1))

            cX += width + scale
        }

        quads = verts.size / 7
    }

    fun renderWithShadowImmediate(proj: Matrix4f, x: Float, y: Float, text: String, scale: Float, color: Vector3f = WHITE) {
        renderWithShadow(x,y,text,scale,color)
        draw(proj)
    }

    fun getFontWidth(c: Char): Float {
        if(c in font.fontTable) {
            return fontWidths[font.fontTable.indexOf(c)]
        } else {
            return font.getWidthOf(c).toFloat()
        }
    }


    fun getStringWidth(s: String, scale: Float): Float {
        var width = 0.0f
        for(c in s) {
            width += (getFontWidth(c) * scale)
            width += scale
        }
        return width
    }

    fun bake_font() {
        if(verts.size > baked_verts.size){
            baked_verts = FloatArray(verts.size)
            System.out.println("resizing font")
        }
        for(i in 0 until verts.size) {
            baked_verts[i] = verts[i]
        }
    }

    fun clear_all() {
        for(i in baked_verts.indices) {
            baked_verts[i] = 0.0f
        }
        verts.clear()
    }

    fun draw(proj: Matrix4f) {
        if(verts.size == 0)
            return
        fontShader.use()
        font.use()
        glBindVertexArray(vao)
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        bake_font()
        glBufferData(GL_ARRAY_BUFFER, baked_verts, GL_DYNAMIC_DRAW)


        var stack: MemoryStack? = null
        try {
            stack = MemoryStack.stackPush()
            glUniformMatrix4fv(fontProj, false, proj.get(stack.mallocFloat(16)))
        } finally {
            stack?.pop()
        }
        glDrawArrays(GL_TRIANGLES, 0, quads)
        clear_all()
    }
}