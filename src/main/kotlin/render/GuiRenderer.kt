package render

import gl.FragmentShader
import gl.ShaderProgram
import gl.VertexShader
import org.joml.Matrix4f
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL31.*
import org.lwjgl.system.MemoryStack
import sun.security.provider.certpath.Vertex
import java.io.File

object GuiRenderer {
    var dvao: Int = 0
    var dvbo: Int = 0

    var gvao: Int = 0
    var gvbo: Int = 0

    val doverlayShader: ShaderProgram
    var doverlayProj: Int = 0

    val guiShader: ShaderProgram
    var guiProj: Int = 0

    var screen: GuiScreen? = null

    var verts = mutableListOf<Float>()

    val doverlay = floatArrayOf(
        0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.5f,
        0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.5f,
        1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.5f,
        1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.5f,
        1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.5f,
        0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.5f
    )

    init {
        // Overlay setup
        dvao = glGenVertexArrays()
        glBindVertexArray(dvao)
        dvbo = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, dvbo)
        glBufferData(GL_ARRAY_BUFFER, doverlay, GL_STATIC_DRAW)

        val vert = VertexShader(File("shader", "doverlay.vert"))
        val frag = FragmentShader(File("shader", "doverlay.frag"))

        doverlayShader = ShaderProgram(vert, frag)
        doverlayShader.use()

        doverlayProj = glGetUniformLocation(doverlayShader.program, "proj")

        var posAttrib = glGetAttribLocation(doverlayShader.program, "position")
        glEnableVertexAttribArray(posAttrib)
        glVertexAttribPointer(posAttrib, 2, GL_FLOAT, false, 6*4, 0L)

        var colAttrib = glGetAttribLocation(doverlayShader.program, "color")
        glEnableVertexAttribArray(colAttrib)
        glVertexAttribPointer(colAttrib, 4, GL_FLOAT, false, 6*4, 2*4L)

        // Gui setup
        gvao = glGenVertexArrays()
        glBindVertexArray(gvao)
        gvbo = glGenBuffers()
        glBindBuffer(GL15.GL_ARRAY_BUFFER, gvbo)

        val gvert = VertexShader(File("shader", "gui.vert"))
        val gfrag = FragmentShader(File("shader", "gui.frag"))

        guiShader = ShaderProgram(gvert, gfrag)
        guiShader.use()

        guiProj = glGetUniformLocation(guiShader.program, "proj")

        var gposAttrib = glGetAttribLocation(guiShader.program, "position")
        glEnableVertexAttribArray(gposAttrib)
        glVertexAttribPointer(gposAttrib, 2, GL_FLOAT, false, 7 * 4, 0L)

        var gcolorAttrib = glGetAttribLocation(guiShader.program, "color")
        glEnableVertexAttribArray(gcolorAttrib)
        glVertexAttribPointer(gcolorAttrib, 3, GL_FLOAT, false, 7 * 4, (2 * 4))

        var gtexAttrib = glGetAttribLocation(guiShader.program, "texcoord")
        glEnableVertexAttribArray(gtexAttrib)
        glVertexAttribPointer(gtexAttrib, 2, GL_FLOAT, false, 7 * 4, (5 * 4))
    }

    fun updateScreenMouse(x: Float, y: Float) {
        if(this.screen != null) {
            this.screen!!.mouseMovement(x,y)
        }
    }



    fun renderDoverlay() {
        doverlayShader.use()
        glDisable(GL_TEXTURE)
        glBindVertexArray(dvao)
        glBindBuffer(GL_ARRAY_BUFFER, dvbo)

        var stack: MemoryStack? = null
        try {
            stack = MemoryStack.stackPush()
            var proj = Matrix4f()
                .ortho(0.0f, 1.0f, 1.0f, 0.0f, -1.0f, 10.0f)
            glUniformMatrix4fv(doverlayProj, false, proj.get(stack.mallocFloat(16)))
        } finally {
            stack?.pop()
        }

        glDrawArrays(GL_TRIANGLES, 0, 6)
        glEnable(GL_TEXTURE)
    }

    fun renderScreen(proj: Matrix4f) {
        this.screen!!.render(proj)
    }

    fun attachScreen(s: GuiScreen) {
        this.screen = s
    }

    fun clearScreen() {
        this.screen = null
    }
}