package blockgame.render;

import blockgame.Logger;
import blockgame.gl.*;
import org.apache.logging.log4j.Level;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;
import blockgame.util.FloatList;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class FontRenderer {

    private final int vao;
    private final int vbo;
    private final int frameVao;
    private final int frameVbo;
    public Font font;
    private int _quadCount;
    private int _frameQuadCount;
    private FloatList _verts;
    private FloatList _framing;

    public float fontHeight;
    public float[] fontWidths;

    public ShaderProgram fontShader;
    public ShaderProgram fontFrameShader;
    public int fontProj;
    public int fontFrameProj;

    public static final Vector4f SHADOW = new Vector4f(0.45f, 0.45f, 0.45f, 1.0f);
    public static final Vector4f WHITE = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    public static final Vector4f YELLOW = new Vector4f(1.0f, 1.0f, 0.0f, 1.0f);
    public static final Vector4f INACTIVE = new Vector4f(0.75f, 0.75f, 0.75f, 1.0f);
    public static final Vector4f FRAME_COLOR = new Vector4f(0.5f, 0.5f, 0.5f, 0.5f);
    public char[] char_buffer;

    public static FontRenderer FONT_RENDERER = null;

    static {
        try {
            FONT_RENDERER = new FontRenderer();
        } catch (Exception e) {
            Logger.LOG.fatal("Unable to instantiate default font renderer - encountered '" + e.getClass().getName() + "'");
            Logger.logStackTrace(Level.FATAL, e);
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
        GL33.glVertexAttribPointer(posAttrib, 2, GL33.GL_FLOAT, false, 8 * 4, 0L);

        int colorAttrib = GL33.glGetAttribLocation(fontShader.getProgram(), "color");
        GL33.glEnableVertexAttribArray(colorAttrib);
        GL33.glVertexAttribPointer(colorAttrib, 4, GL33.GL_FLOAT, false, 8 * 4, (2 * 4));

        int texAttrib = GL33.glGetAttribLocation(fontShader.getProgram(), "texcoord");
        GL33.glEnableVertexAttribArray(texAttrib);
        GL33.glVertexAttribPointer(texAttrib, 2, GL33.GL_FLOAT, false, 8 * 4, (6 * 4));

        //

        frameVao = GL33.glGenVertexArrays();
        GL33.glBindVertexArray(frameVao);
        frameVbo = GL33.glGenBuffers();
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, frameVbo);

        VertexShader textFrameVert = new VertexShader(new File("shader", "text_frame.vert"));
        FragmentShader textFrameFrag = new FragmentShader(new File("shader", "text_frame.frag"));
        fontFrameShader = new ShaderProgram(textFrameVert, textFrameFrag);

        fontFrameShader.use();

        fontFrameProj = GL33.glGetUniformLocation(fontFrameShader.getProgram(), "proj");

        posAttrib = GL33.glGetAttribLocation(fontFrameShader.getProgram(), "position");
        GL33.glEnableVertexAttribArray(posAttrib);
        GL33.glVertexAttribPointer(posAttrib, 2, GL33.GL_FLOAT, false, 6 * 4, 0L);

        colorAttrib = GL33.glGetAttribLocation(fontFrameShader.getProgram(), "color");
        GL33.glEnableVertexAttribArray(colorAttrib);
        GL33.glVertexAttribPointer(colorAttrib, 4, GL33.GL_FLOAT, false, 6 * 4, (2 * 4));

        _verts = new FloatList();
        _framing = new FloatList();
    }

    public void renderWithShadow(float x, float y, String text, float scale, Vector4f color) {
        renderText(x+(1.0f),y+(1.0f),text,scale,SHADOW);
        renderText(x,y,text,scale,color);
    }

    public void renderWithShadow(float x, float y, String text, float scale) {
        renderWithShadow(x,y,text,scale,WHITE);
    }

    public void renderText(float x, float y, String text, float scale, Vector4f color) {
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

            _verts.append(cX);
            _verts.append(cY);
            _verts.append(color.x);
            _verts.append(color.y);
            _verts.append(color.z);
            _verts.append(color.w);
            _verts.append(font.getUVOf(c, UVPosition.U1));
            _verts.append(font.getUVOf(c, UVPosition.V1));

            _verts.append(cX);
            _verts.append(cY + height);
            _verts.append(color.x);
            _verts.append(color.y);
            _verts.append(color.z);
            _verts.append(color.w);
            _verts.append(font.getUVOf(c, UVPosition.U1));
            _verts.append(font.getUVOf(c, UVPosition.V2));

            _verts.append(cX + width);
            _verts.append(cY + height);
            _verts.append(color.x);
            _verts.append(color.y);
            _verts.append(color.z);
            _verts.append(color.w);
            _verts.append(font.getUVOf(c, UVPosition.U2));
            _verts.append(font.getUVOf(c, UVPosition.V2));

            _verts.append(cX + width);
            _verts.append(cY + height);
            _verts.append(color.x);
            _verts.append(color.y);
            _verts.append(color.z);
            _verts.append(color.w);
            _verts.append(font.getUVOf(c, UVPosition.U2));
            _verts.append(font.getUVOf(c, UVPosition.V2));

            _verts.append(cX + width);
            _verts.append(cY);
            _verts.append(color.x);
            _verts.append(color.y);
            _verts.append(color.z);
            _verts.append(color.w);
            _verts.append(font.getUVOf(c, UVPosition.U2));
            _verts.append(font.getUVOf(c, UVPosition.V1));

            _verts.append(cX);
            _verts.append(cY);
            _verts.append(color.x);
            _verts.append(color.y);
            _verts.append(color.z);
            _verts.append(color.w);
            _verts.append(font.getUVOf(c, UVPosition.U1));
            _verts.append(font.getUVOf(c, UVPosition.V1));

            cX += width + scale;
        }

        _quadCount = _verts.getLength() / 8;
    }

    public void renderWithShadowImmediate(Matrix4f proj, float x, float y, String text, float scale, Vector4f color) {
        renderWithShadow(x,y,text,scale,color);
        draw(proj);
    }

    public void renderWithShadowImmediate(Matrix4f proj, float x, float y, String text, float scale) {
        renderWithShadowImmediate(proj, x, y, text, scale, WHITE);
    }

    public void renderFrame(float x, float y, String text, float scale, Vector4f color) {
        float width = getStringWidth(text, scale) * scale;
        float height = fontHeight * scale;
        float fudge = scale;

        _framing.append(x - fudge);
        _framing.append(y - fudge);
        _framing.append(color.x);
        _framing.append(color.y);
        _framing.append(color.z);
        _framing.append(color.w);

        _framing.append(x - fudge);
        _framing.append(y + height + fudge);
        _framing.append(color.x);
        _framing.append(color.y);
        _framing.append(color.z);
        _framing.append(color.w);

        _framing.append(x + width + fudge);
        _framing.append(y + height + fudge);
        _framing.append(color.x);
        _framing.append(color.y);
        _framing.append(color.z);
        _framing.append(color.w);

        _framing.append(x + width + fudge);
        _framing.append(y + height + fudge);
        _framing.append(color.x);
        _framing.append(color.y);
        _framing.append(color.z);
        _framing.append(color.w);

        _framing.append(x + width + fudge);
        _framing.append(y - fudge);
        _framing.append(color.x);
        _framing.append(color.y);
        _framing.append(color.z);
        _framing.append(color.w);

        _framing.append(x - fudge);
        _framing.append(y - fudge);
        _framing.append(color.x);
        _framing.append(color.y);
        _framing.append(color.z);
        _framing.append(color.w);

        _frameQuadCount = _framing.getLength() / 6;
    }

    public void renderTextWithFrame(float x, float y, String text, float scale, Vector4f color, Vector4f frameColor) {
        renderText(x, y, text, scale, color);
        renderFrame(x,y,text,scale,frameColor);
    }
    public void renderTextWithFrame(float x, float y, String text, float scale) {
        renderTextWithFrame(x, y, text, scale, WHITE, FRAME_COLOR);
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
        if(_verts.getLength() == 0)
            return;

        if(_framing.getLength() != 0) {
            fontFrameShader.use();
            GL33.glDisable(GL33.GL_TEXTURE);
            GL33.glBindVertexArray(frameVao);
            GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, frameVbo);
            GL33.glBufferData(GL33.GL_ARRAY_BUFFER, _framing.getStore(), GL33.GL_DYNAMIC_DRAW);

            MemoryStack stack = null;
            try {
                stack = MemoryStack.stackPush();
                GL33.glUniformMatrix4fv(fontFrameProj, false, proj.get(stack.mallocFloat(16)));
            } finally {
                stack.pop();
            }
            GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, _frameQuadCount);
            GL33.glEnable(GL33.GL_TEXTURE);
            _framing.clear();
        }


        fontShader.use();
        font.use();
        GL33.glBindVertexArray(vao);
        GL33.glBindBuffer( GL33.GL_ARRAY_BUFFER, vbo);
        GL33.glBufferData( GL33.GL_ARRAY_BUFFER, _verts.getStore(),  GL33.GL_DYNAMIC_DRAW);

        MemoryStack stack = null;
        try {
            stack = MemoryStack.stackPush();
            GL33.glUniformMatrix4fv(fontProj, false, proj.get(stack.mallocFloat(16)));
        } finally {
            stack.pop();
        }
        GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, _quadCount);
        _verts.clear();


    }
}
