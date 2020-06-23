package render

import org.joml.Matrix4f

abstract class GuiBase {
    abstract fun mouseMovement(x: Float, y: Float)
    abstract fun mouseClick(button: Int, action: Int)
    abstract fun render(proj: Matrix4f)
}