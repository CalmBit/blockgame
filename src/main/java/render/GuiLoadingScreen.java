package render;

import org.joml.Matrix4f;

public class GuiLoadingScreen extends GuiScreen {
    public int chunksLeft = 0;

    public void render(Matrix4f proj) {
        FontRenderer.FONT_RENDERER.renderWithShadowImmediate(proj, 128.0f, 128.0f, "Loading "+ chunksLeft + " chunks...", 1.0f);
    }
}
