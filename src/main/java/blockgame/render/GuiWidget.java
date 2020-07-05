package blockgame.render;

import org.joml.Rectangled;
import org.joml.Vector2f;

public abstract class GuiWidget extends GuiBase {
    protected Vector2f pos;
    protected Vector2f size;
    protected Rectangled bounds;

    protected boolean resizesSelf = true;

    public GuiWidget(Vector2f pos, Vector2f size) {
        this.pos = pos;
        this.size = size;
        recalculateBounds();
    }

    public Rectangled getBounds()
    {
        return bounds;
    }

    public void setPos(Vector2f pos) {
        this.pos = pos;
        recalculateBounds();
    }

    public void setSize(Vector2f size) {
        this.size = size;
        recalculateBounds();
    }

    public void recalculateBounds() {
        this.bounds = new Rectangled(pos.x, pos.y, pos.x + size.x, pos.y + size.y);
    }
}
