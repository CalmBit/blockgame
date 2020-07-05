package blockgame.render;

import blockgame.worker.DecoratorPool;
import blockgame.worker.GeneratorPool;
import blockgame.worker.RenderPool;
import org.joml.Vector2f;
import org.spongepowered.noise.module.Module;

public class GuiLoadingScreen extends GuiScreen {

    public GuiNoiseVisualizer v;
    public GuiNoiseVisualizer f;

    public GuiLoadingScreen(Module p, Module g) {
        GuiGrid grid = new GuiGrid(2, 2);
        grid.addChildAt(new GuiNoiseVisualizer(new Vector2f(0,0), p), 0, 0);
        grid.addChildAt(new GuiNoiseVisualizer(new Vector2f(0,0), g), 1, 0);
        GuiGrid labelGrid = new GuiGrid(1, 3);
        labelGrid.addChildAt(new GuiLabel(new Vector2f(0, 0), "Generating some chunks...", guiLabel -> guiLabel.label = "Generating "+ GeneratorPool.queueSize() + " chunks..."), 0, 0);
        labelGrid.addChildAt(new GuiLabel(new Vector2f(0, 0), "Decorating some chunks...", guiLabel -> guiLabel.label = "Decorating "+ DecoratorPool.queueSize() + " chunks..."), 0, 1);
        labelGrid.addChildAt(new GuiLabel(new Vector2f(0, 0), "Rendering some chunks...", guiLabel -> guiLabel.label = "Rendering "+ RenderPool.queueSize() + " chunks..."), 0, 2);
        grid.addChildAt(labelGrid, 0, 1);
        appendChild(grid);
    }

    //public void render(Matrix4f proj) {
        //FontRenderer.FONT_RENDERER.renderWithShadowImmediate(proj, 128.0f, 128.0f, "Generating "+ GeneratorPool.queueSize() + " chunks...", 1.0f);
        //FontRenderer.FONT_RENDERER.renderWithShadowImmediate(proj, 128.0f, 128.0f + (FontRenderer.FONT_RENDERER.fontHeight) + 2.0f, "Decorating "+ DecoratorPool.queueSize() + " chunks...", 1.0f);
        //FontRenderer.FONT_RENDERER.renderWithShadowImmediate(proj, 128.0f, 128.0f + (FontRenderer.FONT_RENDERER.fontHeight * 2) + 4.0f, "Rendering "+ RenderPool.queueSize() + " chunks...", 1.0f);

        //FontRenderer.FONT_RENDERER.renderWithShadowImmediate(proj, 128.0f - 16.0f, 256.0f - FontRenderer.FONT_RENDERER.fontHeight - 6.0f, "Heightmap", 1.0f);
        //FontRenderer.FONT_RENDERER.renderWithShadowImmediate(proj, 256.0f + 16.0f, 256.0f - FontRenderer.FONT_RENDERER.fontHeight - 6.0f, "Density map", 1.0f);

    //}
}
