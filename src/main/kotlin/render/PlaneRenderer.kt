package render

import gl.FragmentShader
import gl.ShaderProgram
import gl.VertexShader
import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL31.*
import org.lwjgl.system.MemoryStack

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

    val SKY_PLANE: Vector3f = Vector3f(0.000f, 0.749f, 1.000f)
    val VOID_PLANE: Vector3f = Vector3f(0.118f, 0.565f, 1.000f)

    init {
        vao = glGenVertexArrays()
        glBindVertexArray(vao)
        vbo = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        renderPlane(16.0f, SKY_PLANE)
        renderPlane(-16.0f, VOID_PLANE)

        var planeVert= VertexShader("shader/plane.vert")
        var planeFrag = FragmentShader("shader/plane.frag")
        planeShader = ShaderProgram(planeVert, planeFrag)

        planeShader.use()

        planeTrans = glGetUniformLocation(planeShader.program, "model")
        planeView = glGetUniformLocation(planeShader.program, "view")
        planeProj = glGetUniformLocation(planeShader.program, "proj")

        var posAttrib = glGetAttribLocation(planeShader.program, "position")
        glEnableVertexAttribArray(posAttrib)
        glVertexAttribPointer(posAttrib, 3, GL_FLOAT, false, 6*4, 0L)

        var colAttrib = glGetAttribLocation(planeShader.program, "color")
        glEnableVertexAttribArray(colAttrib)
        glVertexAttribPointer(colAttrib, 3, GL_FLOAT, false, 6*4, 3*4L)
    }

    fun renderPlane(y: Float, color: Vector3f) {
        verts.add(-size)
        verts.add(y)
        verts.add(-size)
        verts.add(color.x)
        verts.add(color.y)
        verts.add(color.z)

        verts.add(if(y < 0) -size else size)
        verts.add(y)
        verts.add(if(y < 0) size else -size)
        verts.add(color.x)
        verts.add(color.y)
        verts.add(color.z)

        verts.add(size)
        verts.add(y)
        verts.add(size)
        verts.add(color.x)
        verts.add(color.y)
        verts.add(color.z)

        verts.add(size)
        verts.add(y)
        verts.add(size)
        verts.add(color.x)
        verts.add(color.y)
        verts.add(color.z)

        verts.add(if(y < 0) size else -size)
        verts.add(y)
        verts.add(if(y < 0) -size else size)
        verts.add(color.x)
        verts.add(color.y)
        verts.add(color.z)

        verts.add(-size)
        verts.add(y)
        verts.add(-size)
        verts.add(color.x)
        verts.add(color.y)
        verts.add(color.z)

        quads = verts.size / 6

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
            glUniformMatrix4fv(
                planeTrans, false, Matrix4f()
                .translate(pos)
                .get(stack.mallocFloat(16)))
            glUniformMatrix4fv(planeView, false, view.get(stack.mallocFloat(16)))
            glUniformMatrix4fv(planeProj, false, proj.get(stack.mallocFloat(16)))
            glDrawArrays(GL_TRIANGLES, 0, quads)
        } finally {
            stack?.pop()
        }
        glDepthMask(true)
        glEnable(GL_TEXTURE)
    }
}