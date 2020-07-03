package blockgame.render;

import blockgame.gl.Texture;
import blockgame.util.FloatList;
import org.joml.Matrix4f;
import org.joml.Rectangled;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL33;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.spongepowered.noise.module.Module;
import org.spongepowered.noise.module.source.Perlin;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class GuiNoiseVisualizer extends GuiBase {

    private int _tex = 0;
    private Vector2f _pos;
    private Vector2f _size;
    private Rectangled _bounds;
    private FloatList _verts = new FloatList();
    private FloatList _frame = new FloatList();
    private MemoryStack _stack = null;

    public static Texture frame;

    public GuiNoiseVisualizer(Vector2f pos, Module perlin) {
        if(frame == null) {
            try {
                frame = new Texture(new File("texture", "noise_frame.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        _tex = GL33.glGenTextures();
        GL33.glBindTexture(GL33.GL_TEXTURE_2D, _tex);
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_S, GL33.GL_REPEAT);
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_T, GL33.GL_REPEAT);
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_NEAREST);
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_NEAREST);

        _pos = pos;
        _size = new Vector2f(512.0f, 512.0f);
        _bounds = new Rectangled(pos.x, pos.y, pos.x + (_size.x / 4.0f), pos.y + (_size.y / 4.0f));

        precacheVerts();

        ByteBuffer img = BufferUtils.createByteBuffer((int)(_size.x * _size.y * 4));

        for(int x = 0;x < _size.x;x++) {
            for(int y = 0;y < _size.y;y++) {
                double value = Double.max(0.0, Double.min(1.0, perlin.getValue(x / 16.0f, 0.0, y / 16.0f)));
                img.put((byte)(255 * value));
                img.put((byte)(255 * value));
                img.put((byte)(255 * value));
                img.put((byte)(255));
            }
        }

        img.position(0);

        GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGBA, (int)_size.x, (int)_size.y, 0, GL33.GL_RGBA, GL33.GL_UNSIGNED_BYTE, img);
        GL33.glBindTexture(GL33.GL_TEXTURE_2D, 0);
    }

    @Override
    void mouseMovement(float x, float y) {

    }

    @Override
    void mouseClick(int button, int action) {

    }

    @Override
    void render(Matrix4f proj) {
        GuiRenderer.guiShader.use();
        GL33.glBindVertexArray(GuiRenderer.gvao);
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, GuiRenderer.gvbo);

        try {
            _stack = MemoryStack.stackPush();
            GL33.glUniformMatrix4fv(GuiRenderer.guiProj, false, proj.get(_stack.mallocFloat(16)));
        } finally {
            _stack.pop();
        }

        frame.use();

        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, _frame.getStore(), GL33.GL_DYNAMIC_DRAW);
        GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, 6);


        GL33.glBindTexture(GL33.GL_TEXTURE_2D, _tex);

        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, _verts.getStore(), GL33.GL_DYNAMIC_DRAW);
        GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, 6);
    }

    public void precacheVerts() {
        _frame.append((float) _bounds.minX - 4);
        _frame.append((float) _bounds.minY - 4);
        _frame.append(1.0f);
        _frame.append(1.0f);
        _frame.append(1.0f);
        _frame.append(0.0f);
        _frame.append(0.0f);

        _frame.append((float) _bounds.minX - 4);
        _frame.append((float) _bounds.maxY + 4);
        _frame.append(1.0f);
        _frame.append(1.0f);
        _frame.append(1.0f);
        _frame.append(0.0f);
        _frame.append(1.0f);

        _frame.append((float) _bounds.maxX + 4);
        _frame.append((float) _bounds.maxY + 4);
        _frame.append(1.0f);
        _frame.append(1.0f);
        _frame.append(1.0f);
        _frame.append(1.0f);
        _frame.append(1.0f);

        _frame.append((float) _bounds.maxX + 4);
        _frame.append((float) _bounds.maxY + 4);
        _frame.append(1.0f);
        _frame.append(1.0f);
        _frame.append(1.0f);
        _frame.append(1.0f);
        _frame.append(1.0f);

        _frame.append((float) _bounds.maxX + 4);
        _frame.append((float) _bounds.minY - 4);
        _frame.append(1.0f);
        _frame.append(1.0f);
        _frame.append(1.0f);
        _frame.append(1.0f);
        _frame.append(0.0f);

        _frame.append((float) _bounds.minX - 4);
        _frame.append((float) _bounds.minY - 4);
        _frame.append(1.0f);
        _frame.append(1.0f);
        _frame.append(1.0f);
        _frame.append(0.0f);
        _frame.append(0.0f);

        _verts.append((float) _bounds.minX);
        _verts.append((float) _bounds.minY);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(0.0f);
        _verts.append(0.0f);

        _verts.append((float) _bounds.minX);
        _verts.append((float) _bounds.maxY);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(0.0f);
        _verts.append(1.0f);

        _verts.append((float) _bounds.maxX);
        _verts.append((float) _bounds.maxY);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);

        _verts.append((float) _bounds.maxX);
        _verts.append((float) _bounds.maxY);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);

        _verts.append((float) _bounds.maxX);
        _verts.append((float) _bounds.minY);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(0.0f);

        _verts.append((float) _bounds.minX);
        _verts.append((float) _bounds.minY);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(0.0f);
        _verts.append(0.0f);
    }
}
