package render;

import gl.FragmentShader;
import gl.ShaderProgram;
import gl.VertexShader;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL31;
import org.lwjgl.system.MemoryStack;
import util.FloatList;
import world.WorldType;

import java.io.File;
import java.io.IOException;

public class PlaneRenderer {
    private static int _vao;
    private static int _vbo;
    private static FloatList _verts = new FloatList();
    private static int _quads;

    private static final float SIZE = 64.0f;

    private static ShaderProgram _planeShader;
    private static int _planeTrans;
    private static int _planeView;
    private static int _planeProj;
    private static int _planeFog;
    private static int _planeColor;

    private static Vector3f ATMOS_COLOR = new Vector3f();
    private static Vector3f SKY_PLANE = new Vector3f(0.000f, 0.749f, 1.000f);
    private static Vector3f VOID_PLANE = new Vector3f(0.118f, 0.565f, 1.000f);
    private static Vector3f DARK_PLANE = new Vector3f();

    private static Matrix4f _planeTransMat = new Matrix4f();

    private static boolean hasInitialized = false;

    private static MemoryStack _stack = null;

    public static void init() throws IOException {
        if(hasInitialized) return;
        _vao = GL31.glGenVertexArrays();
        GL31.glBindVertexArray(_vao);
        _vbo = GL31.glGenBuffers();
        GL31.glBindBuffer(GL31.GL_ARRAY_BUFFER, _vbo);

        renderPlane(16.0f);
        renderPlane(-16.0f);

        VertexShader planeVert= new VertexShader(new File("shader", "skyplane.vert"));
        FragmentShader planeFrag = new FragmentShader(new File("shader", "skyplane.frag"));
        _planeShader = new ShaderProgram(planeVert, planeFrag);

        _planeShader.use();

        _planeTrans = GL31.glGetUniformLocation(_planeShader.getProgram(), "model");
        _planeView = GL31.glGetUniformLocation(_planeShader.getProgram(), "view");
        _planeProj = GL31.glGetUniformLocation(_planeShader.getProgram(), "proj");
        _planeColor = GL31.glGetUniformLocation(_planeShader.getProgram(), "color");
        _planeFog = GL31.glGetUniformLocation(_planeShader.getProgram(), "fogColor");

        int posAttrib = GL31.glGetAttribLocation(_planeShader.getProgram(), "position");
        GL31.glEnableVertexAttribArray(posAttrib);
        GL31.glVertexAttribPointer(posAttrib, 3, GL31.GL_FLOAT, false, 3*4, 0L);
    }

    public static void setColors(WorldType type) {
        ATMOS_COLOR = type.atmoColor;
        SKY_PLANE = type.skyColor;
        VOID_PLANE = type.voidColor;
    }

    private static void renderPlane(float y) {
        _verts.append(-SIZE);
        _verts.append(y);
        _verts.append(-SIZE);

        _verts.append(y < 0 ? -SIZE : SIZE);
        _verts.append(y);
        _verts.append(y < 0 ? SIZE : -SIZE);

        _verts.append(SIZE);
        _verts.append(y);
        _verts.append(SIZE);

        _verts.append(SIZE);
        _verts.append(y);
        _verts.append(SIZE);

        _verts.append(y < 0 ? SIZE : -SIZE);
        _verts.append(y);
        _verts.append(y < 0 ? -SIZE : SIZE);

        _verts.append(-SIZE);
        _verts.append(y);
        _verts.append(-SIZE);

        _quads = _verts.getLength() / 3;

        GL31.glBufferData(GL31.GL_ARRAY_BUFFER, _verts.getStore(), GL31.GL_DYNAMIC_DRAW);
    }

    public static void draw(Matrix4f view, Matrix4f proj, Vector3f pos, Double pitch, Double yaw) {
        _planeShader.use();
        GL31.glDisable(GL31.GL_TEXTURE);
        GL31.glDepthMask(false);
        _stack = null;
        try {
            _stack = MemoryStack.stackPush();
            GL31.glBindVertexArray(_vao);
            GL31.glBindBuffer(GL31.GL_ARRAY_BUFFER, _vbo);
            _planeTransMat = new Matrix4f()
                    .translate(pos);
            GL31.glUniformMatrix4fv(_planeTrans, false, _planeTransMat.get(_stack.mallocFloat(16)));
            GL31.glUniformMatrix4fv(_planeView, false, view.get(_stack.mallocFloat(16)));
            GL31.glUniformMatrix4fv(_planeProj, false, proj.get(_stack.mallocFloat(16)));
            GL31.glUniform3fv(_planeColor, SKY_PLANE.get(_stack.mallocFloat(3)));
            GL31.glUniform3fv(_planeFog, ATMOS_COLOR.get(_stack.mallocFloat(3)));
            GL31.glDrawArrays(GL31.GL_TRIANGLES, 0, _quads/2);
            if(pos.y < 62) {
                GL31.glUniform3fv(_planeColor, DARK_PLANE.get(_stack.mallocFloat(3)));
            } else {
                GL31.glUniform3fv(_planeColor, VOID_PLANE.get(_stack.mallocFloat(3)));
            }
            GL31.glDrawArrays(GL31.GL_TRIANGLES, _quads/2, _quads);

        } finally {
            _stack.pop();
        }
        GL31.glDepthMask(true);
        GL31.glEnable(GL31.GL_TEXTURE);
    }
}
