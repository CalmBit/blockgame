package blockgame.render.gui;

import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public abstract class GuiScreen extends GuiBase {
    private final List<GuiBase> _children = new ArrayList<>();

    protected void appendChild(GuiBase c) {
        _children.add(c);
    }

    public void mouseMovement(float x, float y) {
        for (GuiBase c : _children) {
            c.mouseMovement(x,y);
        }
    }

    public void mouseClick(int button, int action) {
        for (GuiBase c : _children) {
            c.mouseClick(button, action);
        }
    }

    public void render(Matrix4f proj) {
        for (GuiBase c : _children) {
            c.render(proj);
        }
    }
}
