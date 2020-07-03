package blockgame.render;

import blockgame.worker.DecoratorPool;
import blockgame.worker.GeneratorPool;
import blockgame.worker.RenderPool;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.spongepowered.noise.module.Module;
import org.spongepowered.noise.module.source.Perlin;

public class GuiLoadingScreen extends GuiScreen {

    public GuiNoiseVisualizer v;
    public GuiNoiseVisualizer f;

    public GuiLoadingScreen(Module p, Module g) {
        v = new GuiNoiseVisualizer(new Vector2f(128.0f - 16.0f, 256.0f), p);
        f = new GuiNoiseVisualizer(new Vector2f(256.0f + 16.0f, 256.0f), g);

    }

    public void render(Matrix4f proj) {
        FontRenderer.FONT_RENDERER.renderWithShadowImmediate(proj, 128.0f, 128.0f, "Generating "+ GeneratorPool.queueSize() + " chunks...", 1.0f);
        FontRenderer.FONT_RENDERER.renderWithShadowImmediate(proj, 128.0f, 128.0f + (FontRenderer.FONT_RENDERER.fontHeight) + 2.0f, "Decorating "+ DecoratorPool.queueSize() + " chunks...", 1.0f);
        FontRenderer.FONT_RENDERER.renderWithShadowImmediate(proj, 128.0f, 128.0f + (FontRenderer.FONT_RENDERER.fontHeight * 2) + 4.0f, "Rendering "+ RenderPool.queueSize() + " chunks...", 1.0f);
        v.render(proj);
        f.render(proj);
        FontRenderer.FONT_RENDERER.renderWithShadowImmediate(proj, 128.0f - 16.0f, 256.0f - FontRenderer.FONT_RENDERER.fontHeight - 6.0f, "Heightmap", 1.0f);
        FontRenderer.FONT_RENDERER.renderWithShadowImmediate(proj, 256.0f + 16.0f, 256.0f - FontRenderer.FONT_RENDERER.fontHeight - 6.0f, "Density map", 1.0f);

    }
}
