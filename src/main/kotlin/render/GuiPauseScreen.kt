package render

import org.joml.Vector2f

class GuiPauseScreen : GuiScreen() {

    init {
        appendChild(GuiButton(Vector2f(100.0f, 100.0f)))
    }
}