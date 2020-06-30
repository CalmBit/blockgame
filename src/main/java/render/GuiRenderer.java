package render;

import gl.FragmentShader;
import gl.ShaderProgram;
import gl.Texture;
import gl.VertexShader;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL31;
import org.lwjgl.system.MemoryStack;
import util.FloatList;

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
        dvao = GL31.glGenVertexArrays();
        GL31.glBindVertexArray(dvao);
        dvbo = GL31.glGenBuffers();
        GL31.glBindBuffer(GL31.GL_ARRAY_BUFFER, dvbo);
        GL31. glBufferData(GL31.GL_ARRAY_BUFFER, doverlay, GL31.GL_STATIC_DRAW);

        VertexShader vert = new VertexShader(new File("shader", "doverlay.vert"));
        FragmentShader frag = new FragmentShader(new File("shader", "doverlay.frag"));

        doverlayShader = new ShaderProgram(vert, frag);
        doverlayShader.use();

        doverlayProj = GL31.glGetUniformLocation(doverlayShader.getProgram(), "proj");

        int posAttrib = GL31.glGetAttribLocation(doverlayShader.getProgram(), "position");
        GL31.glEnableVertexAttribArray(posAttrib);
        GL31.glVertexAttribPointer(posAttrib, 2, GL31.GL_FLOAT, false, 6*4, 0L);

        int colAttrib = GL31.glGetAttribLocation(doverlayShader.getProgram(), "color");
        GL31.glEnableVertexAttribArray(colAttrib);
        GL31.glVertexAttribPointer(colAttrib, 4, GL31.GL_FLOAT, false, 6*4, 2*4L);

        // Gui setup
        gvao = GL31.glGenVertexArrays();
        GL31.glBindVertexArray(gvao);
        gvbo = GL31.glGenBuffers();
        GL31.glBindBuffer(GL31.GL_ARRAY_BUFFER, gvbo);

        VertexShader gvert = new VertexShader(new File("shader", "gui.vert"));
        FragmentShader gfrag = new FragmentShader(new File("shader", "gui.frag"));

        guiShader = new ShaderProgram(gvert, gfrag);
        guiShader.use();

        guiProj = GL31.glGetUniformLocation(guiShader.getProgram(), "proj");

        int gposAttrib = GL31.glGetAttribLocation(guiShader.getProgram(), "position");
        GL31.glEnableVertexAttribArray(gposAttrib);
        GL31.glVertexAttribPointer(gposAttrib, 2, GL31.GL_FLOAT, false, 7 * 4, 0L);

        int gcolorAttrib = GL31.glGetAttribLocation(guiShader.getProgram(), "color");
        GL31.glEnableVertexAttribArray(gcolorAttrib);
        GL31.glVertexAttribPointer(gcolorAttrib, 3, GL31.GL_FLOAT, false, 7 * 4, (2 * 4));

        int gtexAttrib = GL31.glGetAttribLocation(guiShader.getProgram(), "texcoord");
        GL31.glEnableVertexAttribArray(gtexAttrib);
        GL31.glVertexAttribPointer(gtexAttrib, 2, GL31.GL_FLOAT, false, 7 * 4, (5 * 4));

        // Crosshair
        cvao = GL31.glGenVertexArrays();
        GL31.glBindVertexArray(cvao);
        cvbo = GL31.glGenBuffers();
        GL31.glBindBuffer(GL31.GL_ARRAY_BUFFER, cvbo);

        guiShader.use();

        GL31.glEnableVertexAttribArray(gposAttrib);
        GL31.glVertexAttribPointer(gposAttrib, 2, GL31.GL_FLOAT, false, 7 * 4, 0L);

        GL31.glEnableVertexAttribArray(gcolorAttrib);
        GL31.glVertexAttribPointer(gcolorAttrib, 3, GL31.GL_FLOAT, false, 7 * 4, (2 * 4));

        GL31.glEnableVertexAttribArray(gtexAttrib);
        GL31.glVertexAttribPointer(gtexAttrib, 2, GL31.GL_FLOAT, false, 7 * 4, (5 * 4));
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
            GL31.glGetIntegerv(GL31.GL_ARRAY_BUFFER_BINDING, buffer);
            GL31.glBindBuffer(GL31.GL_ARRAY_BUFFER, cvbo);
            GL31.glBufferData(GL31.GL_ARRAY_BUFFER, _crosshair, GL31.GL_STATIC_DRAW);
            GL31.glBindBuffer(GL31.GL_ARRAY_BUFFER, buffer.get());
        } finally {
            stack.pop();
        }
    }

    public static void renderCrosshair(Matrix4f proj) {
        guiShader.use();
        ctex.use();
        GL31.glBindVertexArray(cvao);
        GL31.glBindBuffer(GL31.GL_ARRAY_BUFFER, cvbo);
        GL31.glBlendFunc(GL31.GL_ONE_MINUS_DST_COLOR, GL31.GL_ONE_MINUS_SRC_COLOR);

        _stack = null;
        try {
            _stack = MemoryStack.stackPush();
            GL31.glUniformMatrix4fv(guiProj, false, proj.get(_stack.mallocFloat(16)));
        } finally {
            _stack.pop();
        }
        GL31.glDrawArrays(GL31.GL_TRIANGLES, 0, 6);
        GL31.glBlendFunc(GL31.GL_SRC_ALPHA, GL31.GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void renderDoverlay(Matrix4f proj) {
        doverlayShader.use();
        GL31.glDisable(GL31.GL_TEXTURE);
        GL31.glBindVertexArray(dvao);
        GL31.glBindBuffer(GL31.GL_ARRAY_BUFFER, dvbo);

        _stack = null;
        try {
            _stack = MemoryStack.stackPush();
            GL31.glUniformMatrix4fv(doverlayProj, false, proj.get(_stack.mallocFloat(16)));
        } finally {
            _stack.pop();
        }

        GL31.glDrawArrays(GL31.GL_TRIANGLES, 0, 6);
        GL31.glEnable(GL31.GL_TEXTURE);

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
}
