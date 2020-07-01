package blockgame.render;

import blockgame.gl.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;
import blockgame.util.FloatList;

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
        vao = GL33.glGenVertexArrays();
        GL33.glBindVertexArray(vao);
        vbo = GL33.glGenBuffers();
        GL33.glBindBuffer( GL33.GL_ARRAY_BUFFER, vbo);
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

        fontProj = GL33.glGetUniformLocation(fontShader.getProgram(), "proj");

        int posAttrib = GL33.glGetAttribLocation(fontShader.getProgram(), "position");
        GL33.glEnableVertexAttribArray(posAttrib);
        GL33.glVertexAttribPointer(posAttrib, 2, GL33.GL_FLOAT, false, 7 * 4, 0L);

        int colorAttrib = GL33.glGetAttribLocation(fontShader.getProgram(), "color");
        GL33.glEnableVertexAttribArray(colorAttrib);
        GL33.glVertexAttribPointer(colorAttrib, 3, GL33.GL_FLOAT, false, 7 * 4, (2 * 4));

        int texAttrib = GL33.glGetAttribLocation(fontShader.getProgram(), "texcoord");
        GL33.glEnableVertexAttribArray(texAttrib);
        GL33.glVertexAttribPointer(texAttrib, 2, GL33.GL_FLOAT, false, 7 * 4, (5 * 4));

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
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, vbo);

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
        GL33.glBindVertexArray(vao);
        GL33.glBindBuffer( GL33.GL_ARRAY_BUFFER, vbo);
        GL33.glBufferData( GL33.GL_ARRAY_BUFFER, verts.getStore(),  GL33.GL_DYNAMIC_DRAW);

        MemoryStack stack = null;
        try {
            stack = MemoryStack.stackPush();
            GL33.glUniformMatrix4fv(fontProj, false, proj.get(stack.mallocFloat(16)));
        } finally {
            stack.pop();
        }
        GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, quads);
        verts.clear();
    }
}
