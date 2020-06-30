package render;

import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public abstract class GuiScreen extends GuiBase {
    private static final List<GuiBase> children = new ArrayList<>();

    protected void appendChild(GuiBase c) {
        children.add(c);
    }

    public void mouseMovement(float x, float y) {
        for (GuiBase c : children) {
            c.mouseMovement(x,y);
        }
    }

    public void mouseClick(int button, int action) {
        for (GuiBase c : children) {
            c.mouseClick(button, action);
        }
    }

    public void render(Matrix4f proj) {
        for (GuiBase c : children) {
            c.render(proj);
        }
    }
}
