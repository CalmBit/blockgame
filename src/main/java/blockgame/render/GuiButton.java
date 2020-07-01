package blockgame.render;

import blockgame.gl.Texture;
import org.joml.Matrix4f;
import org.joml.Rectangled;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;
import blockgame.util.FloatList;

import java.io.File;
import java.io.IOException;

public class GuiButton extends GuiBase {
    private static Texture _buttonTex = null;
    private String _text;
    private Vector2f _pos;
    private Vector2f _size;
    private Rectangled _bounds;
    private Runnable _action;
    private FloatList _verts;
    private boolean _rollover = false;
    private boolean _active = true;
    private MemoryStack _stack = null;
    private float _labelWidth;

    public GuiButton(String text, Vector2f pos, Vector2f size, Runnable action) {
        if(text.equals("Resume")) _active = false;
        _text = text;
        _pos = pos;
        _size = size;
        _bounds = new Rectangled(pos.x, pos.y, pos.x + size.x, pos.y + size.y);
        _action = action;
        _verts = new FloatList();
        _labelWidth = FontRenderer.FONT_RENDERER.getStringWidth(text, 1.0f);
        if(_buttonTex == null) {
            try {
                _buttonTex = new Texture(new File("texture", "button.png"));
            } catch(IOException e) {
                System.err.println(e.getMessage());
                System.exit(-1);
            }
        }
    }

    public Rectangled getBounds()
    {
        return _bounds;
    }



    public void mouseClick(int button, int action) {
        if(!_active) return;
        if(_rollover && button == GLFW.GLFW_MOUSE_BUTTON_LEFT && action == GLFW.GLFW_RELEASE) {
            _action.run();
        }
    }

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

        _verts.append((float) _bounds.minX);
        _verts.append((float) _bounds.minY);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(0.0f);
        _verts.append(0.0f + (!_active ? 0.25f : (_rollover ? 0.125f : 0.0f)));

        _verts.append((float) _bounds.minX);
        _verts.append((float) _bounds.maxY);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(0.0f);
        _verts.append(0.125f + (!_active ? 0.25f : (_rollover ? 0.125f : 0.0f)));

        _verts.append((float) _bounds.maxX);
        _verts.append((float) _bounds.maxY);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(0.125f + (!_active ? 0.25f : (_rollover ? 0.125f : 0.0f)));

        _verts.append((float) _bounds.maxX);
        _verts.append((float) _bounds.maxY);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(0.125f + (!_active ? 0.25f : (_rollover ? 0.125f : 0.0f)));

        _verts.append((float) _bounds.maxX);
        _verts.append((float) _bounds.minY);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(0.0f + (!_active ? 0.25f : (_rollover ? 0.125f : 0.0f)));

        _verts.append((float) _bounds.minX);
        _verts.append((float) _bounds.minY);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(0.0f);
        _verts.append(0.0f + (!_active ? 0.25f : (_rollover ? 0.125f : 0.0f)));

        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, _verts.getStore(), GL33.GL_DYNAMIC_DRAW);

        GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, 6);

        FontRenderer.FONT_RENDERER.renderWithShadowImmediate(proj,
                _pos.x + ((_size.x - _labelWidth)/2.0f),
                _pos.y + ((_size.y - FontRenderer.FONT_RENDERER.fontHeight - 2.0f) /2.0f), _text, 1.0f,
                (!_active ? FontRenderer.INACTIVE : (_rollover ? FontRenderer.YELLOW : FontRenderer.WHITE)));
        // - 2.0f is our fudge factor for the fact the font isn't perfectly centered
        _verts.clear();
    }

    @Override
    public void mouseMovement(float x, float y) {
        _rollover = (x >= _bounds.minX && x <= _bounds.maxX && y >= _bounds.minY && y <= _bounds.maxY);
    }
}
