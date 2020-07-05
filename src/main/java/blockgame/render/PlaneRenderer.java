package blockgame.render;

import blockgame.render.gl.shader.FragmentShader;
import blockgame.render.gl.shader.ShaderProgram;
import blockgame.render.gl.shader.VertexShader;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;
import blockgame.util.container.FloatList;
import blockgame.world.WorldType;

import java.io.File;
import java.io.IOException;

public class PlaneRenderer {
    private static int _vao;
    private static int _vbo;
    private static FloatList _verts = new FloatList(32);
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
        _vao = GL33.glGenVertexArrays();
        GL33.glBindVertexArray(_vao);
        _vbo = GL33.glGenBuffers();
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, _vbo);

        renderPlane(16.0f);
        renderPlane(-16.0f);

        VertexShader planeVert= new VertexShader(new File("shader", "skyplane.vert"));
        FragmentShader planeFrag = new FragmentShader(new File("shader", "skyplane.frag"));
        _planeShader = new ShaderProgram(planeVert, planeFrag);

        _planeShader.use();

        _planeTrans = GL33.glGetUniformLocation(_planeShader.getProgram(), "model");
        _planeView = GL33.glGetUniformLocation(_planeShader.getProgram(), "view");
        _planeProj = GL33.glGetUniformLocation(_planeShader.getProgram(), "proj");
        _planeColor = GL33.glGetUniformLocation(_planeShader.getProgram(), "color");
        _planeFog = GL33.glGetUniformLocation(_planeShader.getProgram(), "fogColor");

        int posAttrib = GL33.glGetAttribLocation(_planeShader.getProgram(), "position");
        GL33.glEnableVertexAttribArray(posAttrib);
        GL33.glVertexAttribPointer(posAttrib, 3, GL33.GL_FLOAT, false, 3*4, 0L);
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

        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, _verts.getStore(), GL33.GL_DYNAMIC_DRAW);
    }

    public static void draw(Matrix4f view, Matrix4f proj, Vector3f pos, Double pitch, Double yaw) {
        _planeShader.use();
        GL33.glDisable(GL33.GL_TEXTURE);
        GL33.glDepthMask(false);
        _stack = null;
        try {
            _stack = MemoryStack.stackPush();
            GL33.glBindVertexArray(_vao);
            GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, _vbo);
            _planeTransMat = new Matrix4f()
                    .translate(pos);
            GL33.glUniformMatrix4fv(_planeTrans, false, _planeTransMat.get(_stack.mallocFloat(16)));
            GL33.glUniformMatrix4fv(_planeView, false, view.get(_stack.mallocFloat(16)));
            GL33.glUniformMatrix4fv(_planeProj, false, proj.get(_stack.mallocFloat(16)));
            GL33.glUniform3fv(_planeColor, SKY_PLANE.get(_stack.mallocFloat(3)));
            GL33.glUniform3fv(_planeFog, ATMOS_COLOR.get(_stack.mallocFloat(3)));
            GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, _quads/2);
            if(pos.y < 62) {
                GL33.glUniform3fv(_planeColor, DARK_PLANE.get(_stack.mallocFloat(3)));
            } else {
                GL33.glUniform3fv(_planeColor, VOID_PLANE.get(_stack.mallocFloat(3)));
            }
            GL33.glDrawArrays(GL33.GL_TRIANGLES, _quads/2, _quads);

        } finally {
            _stack.pop();
        }
        GL33.glDepthMask(true);
        GL33.glEnable(GL33.GL_TEXTURE);
    }
}
