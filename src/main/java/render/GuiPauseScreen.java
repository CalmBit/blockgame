package render;

import org.joml.Vector2f;

public class GuiPauseScreen extends GuiScreen {
    public GuiPauseScreen() {
        appendChild(new GuiButton("Resume",
                new Vector2f((GuiRenderer.getScreenWidth() - 256.0f)/2.0f,
                        GuiRenderer.getScreenHeight() - 512.0f),
                new Vector2f(256.0f, 32.0f), () -> System.exit(0)));
        appendChild(new GuiButton("Quit",
                new Vector2f((GuiRenderer.getScreenWidth() - 256.0f)/2.0f,
                        GuiRenderer.getScreenHeight() - 128.0f),
                new Vector2f(256.0f, 32.0f), () -> System.exit(0)));
    }
}
