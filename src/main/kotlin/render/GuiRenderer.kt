package render

import gl.FragmentShader
import gl.ShaderProgram
import gl.VertexShader
import org.joml.Matrix4f
import org.lwjgl.opengl.GL31.*
import org.lwjgl.system.MemoryStack

object GuiRenderer {
    var vao: Int = 0
    var vbo: Int = 0

    val doverlayShader: ShaderProgram
    var doverlayProj: Int = 0

    val doverlay = floatArrayOf(
        0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.5f,
        0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.5f,
        1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.5f,
        1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.5f,
        1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.5f,
        0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.5f
    )

    init {
        vao = glGenVertexArrays()
        glBindVertexArray(vao)
        vbo = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glBufferData(GL_ARRAY_BUFFER, doverlay, GL_STATIC_DRAW)

        val vert = VertexShader("shader/doverlay.vert")
        val frag = FragmentShader("shader/doverlay.frag")

        doverlayShader = ShaderProgram(vert, frag)
        doverlayShader.use()

        doverlayProj = glGetUniformLocation(doverlayShader.program, "proj")

        var posAttrib = glGetAttribLocation(doverlayShader.program, "position")
        glEnableVertexAttribArray(posAttrib)
        glVertexAttribPointer(posAttrib, 2, GL_FLOAT, false, 6*4, 0L)

        var colAttrib = glGetAttribLocation(doverlayShader.program, "color")
        glEnableVertexAttribArray(colAttrib)
        glVertexAttribPointer(colAttrib, 4, GL_FLOAT, false, 6*4, 2*4L)
    }



    fun renderDoverlay() {
        doverlayShader.use()
        glDisable(GL_TEXTURE)
        glBindVertexArray(vao)
        glBindBuffer(GL_ARRAY_BUFFER, vbo)

        var stack: MemoryStack? = null
        try {
            stack = MemoryStack.stackPush()
            var proj = Matrix4f()
                .ortho(0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 10.0f)
            glUniformMatrix4fv(doverlayProj, false, proj.get(stack.mallocFloat(16)))
        } finally {
            stack?.pop()
        }

        glDrawArrays(GL_TRIANGLES, 0, 6)
        glEnable(GL_TEXTURE)
    }
}