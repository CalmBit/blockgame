package render;

import gl.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL31;
import org.lwjgl.system.MemoryStack;
import util.FloatList;

import java.io.File;
import java.io.IOException;

public class FontRenderer {

    private final int vao;
    private final int vbo;
    public Font font;
    private int quads;
    private FloatList verts;

    public float fontHeight;
    public float[] fontWidths;

    public ShaderProgram fontShader;
    public int fontProj;

    public static final Vector3f SHADOW = new Vector3f(0.45f, 0.45f, 0.45f);
    public static final Vector3f WHITE = new Vector3f(1.0f, 1.0f, 1.0f);
    public static final Vector3f YELLOW = new Vector3f(1.0f, 1.0f, 0.0f);
    public static final Vector3f INACTIVE = new Vector3f(0.75f, 0.75f, 0.75f);
    public char[] char_buffer;

    public static FontRenderer FONT_RENDERER = null;

    static {
        try {
            FONT_RENDERER = new FontRenderer();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    public FontRenderer() throws IOException {
        vao = GL31.glGenVertexArrays();
        GL31.glBindVertexArray(vao);
        vbo = GL31.glGenBuffers();
        GL31.glBindBuffer( GL31.GL_ARRAY_BUFFER, vbo);
        font = new Font(new File("font", "alphabet.png"));

        fontHeight = (float)font.getHeight();
        fontWidths = new float[Font.FONT_TABLE.length()];

        for(int i = 0;i < Font.FONT_TABLE.length();i++) {
            fontWidths[i] = font.getWidthOf(Font.FONT_TABLE.charAt(i));
        }

        VertexShader textVert= new VertexShader(new File("shader", "text.vert"));
        FragmentShader textFrag = new FragmentShader(new File("shader", "text.frag"));
        fontShader = new ShaderProgram(textVert, textFrag);

        fontShader.use();

        fontProj = GL31.glGetUniformLocation(fontShader.getProgram(), "proj");

        int posAttrib = GL31.glGetAttribLocation(fontShader.getProgram(), "position");
        GL31.glEnableVertexAttribArray(posAttrib);
        GL31.glVertexAttribPointer(posAttrib, 2, GL31.GL_FLOAT, false, 7 * 4, 0L);

        int colorAttrib = GL31.glGetAttribLocation(fontShader.getProgram(), "color");
        GL31.glEnableVertexAttribArray(colorAttrib);
        GL31.glVertexAttribPointer(colorAttrib, 3, GL31.GL_FLOAT, false, 7 * 4, (2 * 4));

        int texAttrib = GL31.glGetAttribLocation(fontShader.getProgram(), "texcoord");
        GL31.glEnableVertexAttribArray(texAttrib);
        GL31.glVertexAttribPointer(texAttrib, 2, GL31.GL_FLOAT, false, 7 * 4, (5 * 4));

        verts = new FloatList();
    }

    public void renderWithShadow(float x, float y, String text, float scale, Vector3f color) {
        renderText(x+(1.0f),y+(1.0f),text,scale,SHADOW);
        renderText(x,y,text,scale,color);
    }

    public void renderWithShadow(float x, float y, String text, float scale) {
        renderWithShadow(x,y,text,scale,WHITE);
    }

    public void renderText(float x, float y, String text, float scale, Vector3f color) {
        GL31.glBindBuffer(GL31.GL_ARRAY_BUFFER, vbo);

        float cX = x;
        float cY = y;
        float height = fontHeight * scale;
        char_buffer = text.toCharArray();

        for (char c : char_buffer) {
            if (c == '\n') {
                cY += height + scale;
                cX = x;
                continue;
            }

            float width = getFontWidth(c) * scale;

            verts.append(cX);
            verts.append(cY);
            verts.append(color.x);
            verts.append(color.y);
            verts.append(color.z);
            verts.append(font.getUVOf(c, UVPosition.U1));
            verts.append(font.getUVOf(c, UVPosition.V1));

            verts.append(cX);
            verts.append(cY + height);
            verts.append(color.x);
            verts.append(color.y);
            verts.append(color.z);
            verts.append(font.getUVOf(c, UVPosition.U1));
            verts.append(font.getUVOf(c, UVPosition.V2));

            verts.append(cX + width);
            verts.append(cY + height);
            verts.append(color.x);
            verts.append(color.y);
            verts.append(color.z);
            verts.append(font.getUVOf(c, UVPosition.U2));
            verts.append(font.getUVOf(c, UVPosition.V2));

            verts.append(cX + width);
            verts.append(cY + height);
            verts.append(color.x);
            verts.append(color.y);
            verts.append(color.z);
            verts.append(font.getUVOf(c, UVPosition.U2));
            verts.append(font.getUVOf(c, UVPosition.V2));

            verts.append(cX + width);
            verts.append(cY);
            verts.append(color.x);
            verts.append(color.y);
            verts.append(color.z);
            verts.append(font.getUVOf(c, UVPosition.U2));
            verts.append(font.getUVOf(c, UVPosition.V1));

            verts.append(cX);
            verts.append(cY);
            verts.append(color.x);
            verts.append(color.y);
            verts.append(color.z);
            verts.append(font.getUVOf(c, UVPosition.U1));
            verts.append(font.getUVOf(c, UVPosition.V1));

            cX += width + scale;
        }

        quads = verts.getLength() / 7;
    }

    public void renderWithShadowImmediate(Matrix4f proj, float x, float y, String text, float scale, Vector3f color) {
        renderWithShadow(x,y,text,scale,color);
        draw(proj);
    }

    public void renderWithShadowImmediate(Matrix4f proj, float x, float y, String text, float scale) {
        renderWithShadowImmediate(proj, x, y, text, scale, WHITE);
    }

    public float getFontWidth(char c) {
        if(Font.FONT_TABLE.indexOf(c) != -1) {
            return fontWidths[Font.FONT_TABLE.indexOf(c)];
        } else {
            return font.getWidthOf(c);
        }
    }

    public float getStringWidth(String s, float scale) {
        float width = 0.0f;
        char_buffer = s.toCharArray();
        for(char c : char_buffer) {
            width += (getFontWidth(c) * scale);
            width += scale;
        }
        return width;
    }

    public void draw(Matrix4f proj) {
        if(verts.getLength() == 0)
            return;
        fontShader.use();
        font.use();
        GL31.glBindVertexArray(vao);
        GL31.glBindBuffer( GL31.GL_ARRAY_BUFFER, vbo);
        GL31.glBufferData( GL31.GL_ARRAY_BUFFER, verts.getStore(),  GL31.GL_DYNAMIC_DRAW);

        MemoryStack stack = null;
        try {
            stack = MemoryStack.stackPush();
            GL31.glUniformMatrix4fv(fontProj, false, proj.get(stack.mallocFloat(16)));
        } finally {
            stack.pop();
        }
        GL31.glDrawArrays(GL31.GL_TRIANGLES, 0, quads);
        verts.clear();
    }
}
