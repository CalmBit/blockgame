package render

import org.joml.Matrix4f

abstract class GuiScreen : GuiBase() {
    val children: MutableList<GuiBase> = mutableListOf()

    protected fun appendChild(c: GuiBase) {
        children.add(c)
    }

    override fun mouseMovement(x: Float, y: Float) {
        for (c in children) {
            c.mouseMovement(x,y)
        }
    }

    override fun mouseClick(button: Int, action: Int) {
        for (c in children) {
            c.mouseClick(button, action)
        }
    }

    override fun render(proj: Matrix4f) {
        for (c in children) {
            c.render(proj)
        }
    }
}