package render

import org.joml.Matrix4f

class GuiLoadingScreen : GuiScreen() {
    var chunksLeft = 0
    override fun render(proj: Matrix4f) {
        Window.FONT_RENDERER.renderWithShadowImmediate(proj, 128.0f, 128.0f, "Loading "+ chunksLeft + " chunks...", 1.0f)
    }
}