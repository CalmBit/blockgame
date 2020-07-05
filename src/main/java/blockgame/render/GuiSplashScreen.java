package blockgame.render;

import blockgame.gl.Texture;
import blockgame.gl.TextureManager;
import blockgame.registry.RegistryName;
import blockgame.util.FloatList;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;

public class GuiSplashScreen extends GuiScreen {
    private static final FloatList _verts = new FloatList();
    private static final Texture _splash = TextureManager.getTexture(new RegistryName("blockgame", "splashtest"));
    private MemoryStack _stack = null;

    public GuiSplashScreen() {
        _verts.append(0.0f);
        _verts.append(0.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(0.0f);
        _verts.append(0.0f);

        _verts.append(0.0f);
        _verts.append(GuiRenderer.getScreenHeight());
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(0.0f);
        _verts.append(1.0f);

        _verts.append(GuiRenderer.getScreenWidth());
        _verts.append(GuiRenderer.getScreenHeight());
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);

        _verts.append(GuiRenderer.getScreenWidth());
        _verts.append(GuiRenderer.getScreenHeight());
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);

        _verts.append(GuiRenderer.getScreenWidth());
        _verts.append(0.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(0.0f);

        _verts.append(0.0f);
        _verts.append(0.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(1.0f);
        _verts.append(0.0f);
        _verts.append(0.0f);
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

        _splash.use();

        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, _verts.getStore(), GL33.GL_DYNAMIC_DRAW);
        GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, 6);
    }


}
