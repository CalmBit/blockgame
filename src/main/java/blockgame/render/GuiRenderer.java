package blockgame.render;

import blockgame.gl.FragmentShader;
import blockgame.gl.ShaderProgram;
import blockgame.gl.Texture;
import blockgame.gl.VertexShader;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;
import blockgame.util.FloatList;

import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;

public class GuiRenderer {
    private static final GuiRenderer _INSTANCE = new GuiRenderer();

    private static MemoryStack _stack = null;

    public static int dvao;
    public static int dvbo;

    public static int gvao;
    public static int gvbo;

    public static int cvao;
    public static int cvbo;

    public static ShaderProgram doverlayShader;
    public static int doverlayProj;

    public static ShaderProgram guiShader;
    public static int guiProj;
    public static Texture ctex;

    private static GuiScreen _screen;

    private static FloatList _verts;

    private static float _wWidth;
    private static float _wHeight;

    public static boolean hasInitialized;

    private static final Matrix4f OVERLAY_MAT = new Matrix4f()
            .ortho(0.0f, 1.0f, 1.0f, 0.0f, -1.0f, 10.0f);

    private static final float[] doverlay = new float[] {
        0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.5f,
        0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.5f,
        1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.5f,
        1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.5f,
        1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.5f,
        0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.5f
    };

    private static float[] _crosshair = new float[]{};

    private GuiRenderer() {

    }

    public static void init() throws IOException {
        if(hasInitialized)
            return;
        // Overlay setup
        dvao = GL33.glGenVertexArrays();
        GL33.glBindVertexArray(dvao);
        dvbo = GL33.glGenBuffers();
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, dvbo);
        GL33. glBufferData(GL33.GL_ARRAY_BUFFER, doverlay, GL33.GL_STATIC_DRAW);

        VertexShader vert = new VertexShader(new File("shader", "doverlay.vert"));
        FragmentShader frag = new FragmentShader(new File("shader", "doverlay.frag"));

        doverlayShader = new ShaderProgram(vert, frag);
        doverlayShader.use();

        doverlayProj = GL33.glGetUniformLocation(doverlayShader.getProgram(), "proj");

        int posAttrib = GL33.glGetAttribLocation(doverlayShader.getProgram(), "position");
        GL33.glEnableVertexAttribArray(posAttrib);
        GL33.glVertexAttribPointer(posAttrib, 2, GL33.GL_FLOAT, false, 6*4, 0L);

        int colAttrib = GL33.glGetAttribLocation(doverlayShader.getProgram(), "color");
        GL33.glEnableVertexAttribArray(colAttrib);
        GL33.glVertexAttribPointer(colAttrib, 4, GL33.GL_FLOAT, false, 6*4, 2*4L);

        // Gui setup
        gvao = GL33.glGenVertexArrays();
        GL33.glBindVertexArray(gvao);
        gvbo = GL33.glGenBuffers();
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, gvbo);

        VertexShader gvert = new VertexShader(new File("shader", "gui.vert"));
        FragmentShader gfrag = new FragmentShader(new File("shader", "gui.frag"));

        guiShader = new ShaderProgram(gvert, gfrag);
        guiShader.use();

        guiProj = GL33.glGetUniformLocation(guiShader.getProgram(), "proj");

        int gposAttrib = GL33.glGetAttribLocation(guiShader.getProgram(), "position");
        GL33.glEnableVertexAttribArray(gposAttrib);
        GL33.glVertexAttribPointer(gposAttrib, 2, GL33.GL_FLOAT, false, 7 * 4, 0L);

        int gcolorAttrib = GL33.glGetAttribLocation(guiShader.getProgram(), "color");
        GL33.glEnableVertexAttribArray(gcolorAttrib);
        GL33.glVertexAttribPointer(gcolorAttrib, 3, GL33.GL_FLOAT, false, 7 * 4, (2 * 4));

        int gtexAttrib = GL33.glGetAttribLocation(guiShader.getProgram(), "texcoord");
        GL33.glEnableVertexAttribArray(gtexAttrib);
        GL33.glVertexAttribPointer(gtexAttrib, 2, GL33.GL_FLOAT, false, 7 * 4, (5 * 4));

        // Crosshair
        cvao = GL33.glGenVertexArrays();
        GL33.glBindVertexArray(cvao);
        cvbo = GL33.glGenBuffers();
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, cvbo);

        guiShader.use();

        GL33.glEnableVertexAttribArray(gposAttrib);
        GL33.glVertexAttribPointer(gposAttrib, 2, GL33.GL_FLOAT, false, 7 * 4, 0L);

        GL33.glEnableVertexAttribArray(gcolorAttrib);
        GL33.glVertexAttribPointer(gcolorAttrib, 3, GL33.GL_FLOAT, false, 7 * 4, (2 * 4));

        GL33.glEnableVertexAttribArray(gtexAttrib);
        GL33.glVertexAttribPointer(gtexAttrib, 2, GL33.GL_FLOAT, false, 7 * 4, (5 * 4));
        hasInitialized = true;
    }


    public static void updateScreenMouse(float x, float y) {
        if(_screen != null) {
            _screen.mouseMovement(x,y);
        }
    }

    public static void mouseClick(int button, int action) {
        if(_screen != null) {
            _screen.mouseClick(button, action);
        }
    }

    public static void updateWindowSize(float w, float h) {
        if(!hasInitialized)
            return;
        _wWidth = w;
        _wHeight = h;
        _crosshair = new float[]{
                ((_wWidth / 2.0f) - 8.0f), ((_wHeight / 2.0f) - 8.0f), 1.0f, 1.0f, 1.0f, 0.0f, 0.0f,
                ((_wWidth / 2.0f) - 8.0f), ((_wHeight / 2.0f) + 8.0f), 1.0f, 1.0f, 1.0f, 0.0f, 1.0f,
                ((_wWidth / 2.0f) + 8.0f), ((_wHeight / 2.0f) + 8.0f), 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
                ((_wWidth / 2.0f) + 8.0f), ((_wHeight / 2.0f) + 8.0f), 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
                ((_wWidth / 2.0f) + 8.0f), ((_wHeight / 2.0f) - 8.0f), 1.0f, 1.0f, 1.0f, 1.0f, 0.0f,
                ((_wWidth / 2.0f) - 8.0f), ((_wHeight / 2.0f) - 8.0f), 1.0f, 1.0f, 1.0f, 0.0f, 0.0f
        };
        MemoryStack stack = null;
        try {
            stack = MemoryStack.stackPush();
            IntBuffer buffer = stack.mallocInt(1);
            GL33.glGetIntegerv(GL33.GL_ARRAY_BUFFER_BINDING, buffer);
            GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, cvbo);
            GL33.glBufferData(GL33.GL_ARRAY_BUFFER, _crosshair, GL33.GL_STATIC_DRAW);
            GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, buffer.get());
        } finally {
            stack.pop();
        }
    }

    public static void renderCrosshair(Matrix4f proj) {
        guiShader.use();
        ctex.use();
        GL33.glBindVertexArray(cvao);
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, cvbo);
        GL33.glBlendFunc(GL33.GL_ONE_MINUS_DST_COLOR, GL33.GL_ONE_MINUS_SRC_COLOR);

        _stack = null;
        try {
            _stack = MemoryStack.stackPush();
            GL33.glUniformMatrix4fv(guiProj, false, proj.get(_stack.mallocFloat(16)));
        } finally {
            _stack.pop();
        }
        GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, 6);
        GL33.glBlendFunc(GL33.GL_SRC_ALPHA, GL33.GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void renderFadeBack() {
        doverlayShader.use();
        GL33.glDisable(GL33.GL_TEXTURE);
        GL33.glBindVertexArray(dvao);
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, dvbo);

        _stack = null;
        try {
            _stack = MemoryStack.stackPush();
            GL33.glUniformMatrix4fv(doverlayProj, false, OVERLAY_MAT.get(_stack.mallocFloat(16)));
        } finally {
            _stack.pop();
        }

        GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, 6);
        GL33.glEnable(GL33.GL_TEXTURE);

    }

    public static void renderScreen(Matrix4f proj) {
        _screen.render(proj);
    }

    public static void attachScreen(GuiScreen s) {
        _screen = s;
    }

    public static void clearScreen() {
        _screen = null;
    }

    public static float getScreenWidth() {
        return _wWidth;
    }

    public static float getScreenHeight() {
        return _wHeight;
    }

    public static boolean screenAttached() {
        return _screen != null;
    }
}
