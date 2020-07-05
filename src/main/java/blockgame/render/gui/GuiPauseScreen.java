package blockgame.render.gui;

import blockgame.client.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;

public class GuiPauseScreen extends GuiScreen {
    public GuiPauseScreen() {
        appendChild(new GuiButton("Resume",
                new Vector2f((GuiRenderer.getScreenWidth() - 256.0f)/2.0f,
                        GuiRenderer.getScreenHeight() - 512.0f), () -> Window.refocusRequested = true));
        appendChild(new GuiButton("Quit",
                new Vector2f((GuiRenderer.getScreenWidth() - 256.0f)/2.0f,
                        GuiRenderer.getScreenHeight() - 128.0f), () -> System.exit(0)));
    }

    @Override
    public void render(Matrix4f proj) {
        GuiRenderer.renderFadeBack();
        super.render(proj);
    }
}
