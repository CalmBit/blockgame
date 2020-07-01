package blockgame.render;

import org.joml.Matrix4f;

public abstract class GuiBase {
    abstract void mouseMovement(float x, float y);
    abstract void mouseClick(int button, int action);
    abstract void render(Matrix4f proj);
}
