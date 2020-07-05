package blockgame.render.gui;

import blockgame.render.gl.texture.Texture;
import blockgame.render.gl.texture.TextureManager;
import blockgame.util.registry.RegistryName;
import blockgame.render.gl.font.FontRenderer;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;
import blockgame.util.container.FloatList;

public class GuiButton extends GuiWidget {
    private static Texture _buttonTex = null;
    private String _text;

    private Runnable _action;
    private FloatList _verts;
    private boolean _rollover = false;
    private boolean _active = true;
    private MemoryStack _stack = null;
    private float _labelWidth;

    public GuiButton(String text, Vector2f pos, Vector2f size, Runnable action) {
        super(pos, size);

        _text = text;
        _action = action;
        _labelWidth = FontRenderer.DEFAULT.getStringWidth(text, 1.0f);
        if(_buttonTex == null) {
            _buttonTex = TextureManager.getTexture(new RegistryName("blockgame", "button"));
        }
    }

    public GuiButton(String text, Vector2f pos, Runnable action) {
        this(text, pos, new Vector2f(256.0f, 32.0f), action);
    }

    @Override
    public void render(Matrix4f proj) {
        GuiRenderer.guiShader.use();
        GL33.glBindVertexArray(GuiRenderer.gvao);
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, GuiRenderer.gvbo);

        try {
            _stack = MemoryStack.stackPush();
            GL33.glUniformMatrix4fv(GuiRenderer.guiProj, false, proj.get(_stack.mallocFloat(16)));
        } finally {
            _stack.pop();
        }

        _buttonTex.use();

        recalculateTexcoords();

        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, _verts.getStore(), GL33.GL_DYNAMIC_DRAW);
        GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, 6);

        FontRenderer.DEFAULT.renderWithShadow(
                pos.x + ((size.x - _labelWidth)/2.0f),
                pos.y + ((size.y - FontRenderer.DEFAULT.fontHeight - 2.0f) /2.0f), _text, 1.0f,
                (!_active ? FontRenderer.INACTIVE : (_rollover ? FontRenderer.YELLOW : FontRenderer.WHITE)));
        // - 2.0f is our fudge factor for the fact the font isn't perfectly centered
    }

    @Override
    public void mouseClick(int button, int action) {
        if(!_active) return;
        if(_rollover && button == GLFW.GLFW_MOUSE_BUTTON_LEFT && action == GLFW.GLFW_RELEASE) {
            _action.run();
        }
    }

    @Override
    public void mouseMovement(float x, float y) {
        _rollover = (x >= bounds.minX && x <= bounds.maxX && y >= bounds.minY && y <= bounds.maxY);
    }

    public void recalculateTexcoords() {
        _verts.set(6, (!_active ? 0.25f : (_rollover ? 0.125f : 0.0f)));
        _verts.set(13, 0.125f + (!_active ? 0.25f : (_rollover ? 0.125f : 0.0f)));
        _verts.set(20, 0.125f + (!_active ? 0.25f : (_rollover ? 0.125f : 0.0f)));
        _verts.set(27, 0.125f + (!_active ? 0.25f : (_rollover ? 0.125f : 0.0f)));
        _verts.set(34, (!_active ? 0.25f : (_rollover ? 0.125f : 0.0f)));
        _verts.set(41, (!_active ? 0.25f : (_rollover ? 0.125f : 0.0f)));
    }

    @Override
    public void recalculateBounds() {
        super.recalculateBounds();

        if(_verts == null) {
            _verts = new FloatList(7*6);
        } else {
            _verts.clear();
        }
        _verts.append((float) bounds.minX);
        _verts.append((float) bounds.minY);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(0.0f);
        _verts.append(0.0f + (!_active ? 0.25f : (_rollover ? 0.125f : 0.0f)));

        _verts.append((float) bounds.minX);
        _verts.append((float) bounds.maxY);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(0.0f);
        _verts.append(0.125f + (!_active ? 0.25f : (_rollover ? 0.125f : 0.0f)));

        _verts.append((float) bounds.maxX);
        _verts.append((float) bounds.maxY);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(0.125f + (!_active ? 0.25f : (_rollover ? 0.125f : 0.0f)));

        _verts.append((float) bounds.maxX);
        _verts.append((float) bounds.maxY);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(0.125f + (!_active ? 0.25f : (_rollover ? 0.125f : 0.0f)));

        _verts.append((float) bounds.maxX);
        _verts.append((float) bounds.minY);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(0.0f + (!_active ? 0.25f : (_rollover ? 0.125f : 0.0f)));

        _verts.append((float) bounds.minX);
        _verts.append((float) bounds.minY);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(0.0f);
        _verts.append(0.0f + (!_active ? 0.25f : (_rollover ? 0.125f : 0.0f)));
    }
}
