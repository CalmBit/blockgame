package render

import gl.Texture
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL31.*
import org.lwjgl.system.MemoryStack
import java.io.File

class GuiButton(val pos: Vector2f) : GuiBase() {

    companion object {
        val buttonTex: Texture
        init {
            buttonTex = Texture(File("texture", "button.png"))
        }
    }

    var rollover = false

    fun getBounds() : Pair<Vector2f, Vector2f> {
        return Pair(pos, Vector2f(pos.x + 128, pos.y + 32))
    }

    override fun mouseClick(button: Int, action: Int) {
    }

    override fun render(proj: Matrix4f) {
        GuiRenderer.guiShader.use()
        glBindVertexArray(GuiRenderer.gvao)
        glBindBuffer(GL_ARRAY_BUFFER, GuiRenderer.gvbo)

        var stack: MemoryStack? = null
        try {
            stack = MemoryStack.stackPush()
            glUniformMatrix4fv(GuiRenderer.guiProj, false, proj.get(stack.mallocFloat(16)))
        } finally {
            stack?.pop()
        }

        val verts = mutableListOf<Float>()

        buttonTex.use()

        verts.add(pos.x)
        verts.add(pos.y)
        verts.add(1.0f)
        verts.add(1.0f)
        verts.add(1.0f)
        verts.add(0.0f)
        verts.add(0.0f+ (if(rollover) 0.25f else 0.0f))

        verts.add(pos.x)
        verts.add(pos.y + 32.0f)
        verts.add(1.0f)
        verts.add(1.0f)
        verts.add(1.0f)
        verts.add(0.0f)
        verts.add(0.25f + (if(rollover) 0.25f else 0.0f))

        verts.add(pos.x + 128.0f)
        verts.add(pos.y + 32.0f)
        verts.add(1.0f)
        verts.add(1.0f)
        verts.add(1.0f)
        verts.add(1.0f)
        verts.add(0.25f + (if(rollover) 0.25f else 0.0f))

        verts.add(pos.x + 128.0f)
        verts.add(pos.y + 32.0f)
        verts.add(1.0f)
        verts.add(1.0f)
        verts.add(1.0f)
        verts.add(1.0f)
        verts.add(0.25f + (if(rollover) 0.25f else 0.0f))

        verts.add(pos.x + 128.0f)
        verts.add(pos.y)
        verts.add(1.0f)
        verts.add(1.0f)
        verts.add(1.0f)
        verts.add(1.0f)
        verts.add(0.0f + (if(rollover) 0.25f else 0.0f))

        verts.add(pos.x)
        verts.add(pos.y)
        verts.add(1.0f)
        verts.add(1.0f)
        verts.add(1.0f)
        verts.add(0.0f)
        verts.add(0.0f + (if(rollover) 0.25f else 0.0f))

        glBufferData(GL_ARRAY_BUFFER, verts.toFloatArray(), GL_DYNAMIC_DRAW)

        glDrawArrays(GL_TRIANGLES, 0, 6)

        FontRenderer.renderWithShadowImmediate(proj, pos.x + ((128 - (FontRenderer.getStringWidth("ur gay", 1.0f)))/2.0f), pos.y + 6.0f, "ur gay", 1.0f)
    }

    override fun mouseMovement(x: Float, y: Float) {
        this.rollover = x >= pos.x && x <= pos.x + 128 && y >= pos.y && y <= pos.y + 32
    }
}