package render

import gl.FragmentShader
import gl.ShaderProgram
import gl.VertexShader
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL31.*
import org.lwjgl.system.MemoryStack
import world.WorldType
import java.io.File

object PlaneRenderer {
    var vao: Int = 0
    var vbo: Int = 0
    val verts = mutableListOf<Float>()
    var quads: Int = 0

    val size = 64.0f

    val planeShader: ShaderProgram
    var planeTrans: Int
    var planeView: Int
    var planeProj: Int
    var planeFog: Int
    var planeColor: Int

    var ATMOS_COLOR: Vector3f = Vector3f()
    var SKY_PLANE: Vector3f = Vector3f(0.000f, 0.749f, 1.000f)
    var VOID_PLANE: Vector3f = Vector3f(0.118f, 0.565f, 1.000f)
    val DARK_PLANE: Vector3f = Vector3f()

    var planeTransMat = Matrix4f()

    init {
        vao = glGenVertexArrays()
        glBindVertexArray(vao)
        vbo = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vbo)

        renderPlane(16.0f)
        renderPlane(-16.0f)

        var planeVert= VertexShader(File("shader", "skyplane.vert"))
        var planeFrag = FragmentShader(File("shader", "skyplane.frag"))
        planeShader = ShaderProgram(planeVert, planeFrag)

        planeShader.use()

        planeTrans = glGetUniformLocation(planeShader.getProgram(), "model")
        planeView = glGetUniformLocation(planeShader.getProgram(), "view")
        planeProj = glGetUniformLocation(planeShader.getProgram(), "proj")
        planeColor = glGetUniformLocation(planeShader.getProgram(), "color")
        planeFog = glGetUniformLocation(planeShader.getProgram(), "fogColor")

        var posAttrib = glGetAttribLocation(planeShader.getProgram(), "position")
        glEnableVertexAttribArray(posAttrib)
        glVertexAttribPointer(posAttrib, 3, GL_FLOAT, false, 3*4, 0L)
    }

    fun setColors(type: WorldType) {
        ATMOS_COLOR = type.atmoColor
        SKY_PLANE = type.skyColor
        VOID_PLANE = type.voidColor
    }

    fun renderPlane(y: Float) {
        verts.add(-size)
        verts.add(y)
        verts.add(-size)

        verts.add(if(y < 0) -size else size)
        verts.add(y)
        verts.add(if(y < 0) size else -size)

        verts.add(size)
        verts.add(y)
        verts.add(size)

        verts.add(size)
        verts.add(y)
        verts.add(size)

        verts.add(if(y < 0) size else -size)
        verts.add(y)
        verts.add(if(y < 0) -size else size)

        verts.add(-size)
        verts.add(y)
        verts.add(-size)

        quads = verts.size / 3

        glBufferData(GL_ARRAY_BUFFER, verts.toFloatArray(), GL_DYNAMIC_DRAW)
    }

    fun draw(view: Matrix4f, proj: Matrix4f, pos: Vector3f, pitch: Double, yaw: Double) {
        planeShader.use()
        glDisable(GL_TEXTURE)
        glDepthMask(false)
        var stack: MemoryStack? = null
        try {
            stack = MemoryStack.stackPush()
            glBindVertexArray(vao)
            glBindBuffer(GL_ARRAY_BUFFER, vbo)
            planeTransMat = Matrix4f()
                .translate(pos)
            glUniformMatrix4fv(planeTrans, false, planeTransMat.get(stack.mallocFloat(16)))
            glUniformMatrix4fv(planeView, false, view.get(stack.mallocFloat(16)))
            glUniformMatrix4fv(planeProj, false, proj.get(stack.mallocFloat(16)))
            glUniform3fv(planeColor, SKY_PLANE.get(stack.mallocFloat(3)))
            glUniform3fv(planeFog, ATMOS_COLOR.get(stack.mallocFloat(3)))
            glDrawArrays(GL_TRIANGLES, 0, quads/2)
            if(pos.y < 62) {
                glUniform3fv(planeColor, DARK_PLANE.get(stack.mallocFloat(3)))
            } else {
                glUniform3fv(planeColor, VOID_PLANE.get(stack.mallocFloat(3)))
            }
            glDrawArrays(GL_TRIANGLES, quads/2, quads)

        } finally {
            stack?.pop()
        }
        glDepthMask(true)
        glEnable(GL_TEXTURE)
    }
}