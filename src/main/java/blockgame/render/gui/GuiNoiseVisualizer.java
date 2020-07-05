package blockgame.render.gui;

import blockgame.render.gl.texture.Texture;
import blockgame.render.gl.texture.TextureManager;
import blockgame.util.registry.RegistryName;
import blockgame.util.container.FloatList;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;
import org.spongepowered.noise.module.Module;

import java.nio.ByteBuffer;

public class GuiNoiseVisualizer extends GuiWidget {

    private int _tex = 0;
    private FloatList _verts;
    private FloatList _frame;
    private MemoryStack _stack = null;

    public static Texture frame;

    public GuiNoiseVisualizer(Vector2f pos, Module perlin) {
        super(pos, new Vector2f(136.0f, 136.0f));
        if(frame == null) {
            frame = TextureManager.getTexture(new RegistryName("blockgame", "noise_frame"));
        }
        _tex = GL33.glGenTextures();
        GL33.glBindTexture(GL33.GL_TEXTURE_2D, _tex);
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_S, GL33.GL_REPEAT);
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_T, GL33.GL_REPEAT);
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_NEAREST);
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_NEAREST);

        ByteBuffer img = BufferUtils.createByteBuffer((int)(((size.x - 8.0f) * 4) * ((size.y - 8.0f) * 4) * 4));

        for(int x = 0;x < (size.x - 8)*4;x++) {
            for(int y = 0;y < (size.y - 8)*4;y++) {
                double value = Double.max(0.0, Double.min(1.0, perlin.getValue(x / 16.0f, 0.0, y / 16.0f)));
                img.put((byte)(255 * value));
                img.put((byte)(255 * value));
                img.put((byte)(255 * value));
                img.put((byte)(255));
            }
        }

        img.position(0);

        GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGBA, (int)(size.x-8)*4, (int)(size.y-8)*4, 0, GL33.GL_RGBA, GL33.GL_UNSIGNED_BYTE, img);
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

        //

        GL33.glBindTexture(GL33.GL_TEXTURE_2D, _tex);

        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, _verts.getStore(), GL33.GL_DYNAMIC_DRAW);
        GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, 6);
    }

    @Override
    public void recalculateBounds() {
        super.recalculateBounds();

        if(_frame == null) {
            _frame = new FloatList(7*6);
        } else {
            _frame.clear();
        }
        _frame.append((float) bounds.minX);
        _frame.append((float) bounds.minY);
        _frame.append(1.0f);
        _frame.append(1.0f);
        _frame.append(1.0f);
        _frame.append(0.0f);
        _frame.append(0.0f);

        _frame.append((float) bounds.minX);
        _frame.append((float) bounds.maxY);
        _frame.append(1.0f);
        _frame.append(1.0f);
        _frame.append(1.0f);
        _frame.append(0.0f);
        _frame.append(1.0f);

        _frame.append((float) bounds.maxX);
        _frame.append((float) bounds.maxY);
        _frame.append(1.0f);
        _frame.append(1.0f);
        _frame.append(1.0f);
        _frame.append(1.0f);
        _frame.append(1.0f);

        _frame.append((float) bounds.maxX);
        _frame.append((float) bounds.maxY);
        _frame.append(1.0f);
        _frame.append(1.0f);
        _frame.append(1.0f);
        _frame.append(1.0f);
        _frame.append(1.0f);

        _frame.append((float) bounds.maxX);
        _frame.append((float) bounds.minY);
        _frame.append(1.0f);
        _frame.append(1.0f);
        _frame.append(1.0f);
        _frame.append(1.0f);
        _frame.append(0.0f);

        _frame.append((float) bounds.minX);
        _frame.append((float) bounds.minY);
        _frame.append(1.0f);
        _frame.append(1.0f);
        _frame.append(1.0f);
        _frame.append(0.0f);
        _frame.append(0.0f);

        if(_verts == null) {
            _verts = new FloatList(7*6);
        } else {
            _verts.clear();
        }
        _verts.append((float) bounds.minX + 4.0f);
        _verts.append((float) bounds.minY + 4.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(0.0f);
        _verts.append(0.0f);

        _verts.append((float) bounds.minX + 4.0f);
        _verts.append((float) bounds.maxY - 4.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(0.0f);
        _verts.append(1.0f);

        _verts.append((float) bounds.maxX - 4.0f);
        _verts.append((float) bounds.maxY - 4.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);

        _verts.append((float) bounds.maxX - 4.0f);
        _verts.append((float) bounds.maxY - 4.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);

        _verts.append((float) bounds.maxX - 4.0f);
        _verts.append((float) bounds.minY + 4.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(0.0f);

        _verts.append((float) bounds.minX + 4.0f);
        _verts.append((float) bounds.minY + 4.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(0.0f);
        _verts.append(0.0f);
    }
}
