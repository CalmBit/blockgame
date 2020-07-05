package blockgame.render;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.util.function.Consumer;

public class GuiLabel extends GuiWidget {

    protected String label;
    protected Consumer<GuiLabel> textCalculation;


    public GuiLabel(Vector2f pos, String label, @Nullable Consumer<GuiLabel> textCalculation) {
        super(pos, new Vector2f(0,0));
        this.label = label;
        this.textCalculation = textCalculation;
    }

    public GuiLabel(Vector2f pos, String label) {
        this(pos, label, null);
    }

    @Override
    void mouseMovement(float x, float y) {

    }

    @Override
    void mouseClick(int button, int action) {

    }

    @Override
    void render(Matrix4f proj) {
        if(textCalculation != null) {
            textCalculation.accept(this);
        }
        FontRenderer.FONT_RENDERER.renderWithShadow(pos.x, pos.y, label, 1.0f, FontRenderer.WHITE);
    }
}
