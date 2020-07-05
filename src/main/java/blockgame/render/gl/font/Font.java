package blockgame.render.gl.font;

import blockgame.render.gl.texture.UVPosition;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL33;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Font {
    private int _tex;
    private int _width;
    private int _height;
    public static final String FONT_TABLE =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.,;:?!-_~#\"'&()[]";//{}^|`/\\@°+=*%€\$£¢<>©®ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØŒÙÚÛÜÝÞàáâãäåæçèéêëìíîïðñòóôõöøœùúûüýþßÿ¿¡"
    private final float[] _uvTable = new float[FONT_TABLE.length() * 4];

    public static final int FONT_WIDTH = 8;
    public float vOffset;
    public float uOffset;
    public float u;

    public Font(File file) throws IOException {
        _tex = GL33.glGenTextures();
        GL33.glBindTexture(GL33.GL_TEXTURE_2D, _tex);
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_S, GL33.GL_CLAMP_TO_EDGE);
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_T, GL33.GL_CLAMP_TO_EDGE);
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_NEAREST);
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_NEAREST);
        MemoryStack stack = null;
        ByteBuffer img = null;
        InputStream stream = getClass().getClassLoader().getResourceAsStream(file.getPath());

        long len = stream.available();
        byte[] bytes = new byte[(int)len];

        int offset = 0;
        int total;
        while (offset < bytes.length
                && (total=stream.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += total;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }

        stream.close();

        ByteBuffer buffer = BufferUtils.createByteBuffer(bytes.length);
        buffer.put(bytes);
        buffer.flip();
        try {
            stack = MemoryStack.stackPush();
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer chan = stack.mallocInt(1);
            img = STBImage.stbi_load_from_memory(buffer, w, h, chan, 4);
            if (img == null) {
                throw new RuntimeException("Unable to load " + file.getPath() + "\n" + STBImage.stbi_failure_reason());
            }
            _width = w.get(0);
            _height = h.get(0);
            vOffset = 1.0f/((float)_height*8.0f);
            uOffset = 1.0f/((float)_width*8.0f);
        } finally {
            stack.pop();
        }
        u = 8.0f/((float)_width);
        GL33.glTexImage2D(
                GL33.GL_TEXTURE_2D,
                0,
                GL33.GL_RGBA,
                _width,
                _height,
                0,
                GL33.GL_RGBA,
                GL33.GL_UNSIGNED_BYTE,
                img
        );

        for(int currentLetter = 0; currentLetter < FONT_TABLE.length(); currentLetter++) {
            _uvTable[currentLetter * 4] = ((float)currentLetter*u) + uOffset;
            _uvTable[currentLetter * 4 + 1] = vOffset;
            _uvTable[currentLetter * 4 + 2] = ((float)currentLetter*u) + u  - uOffset;
            _uvTable[currentLetter * 4 + 3] = 1.0f - vOffset;
        }
        STBImage.stbi_image_free(img);
        GL33.glBindTexture(GL33.GL_TEXTURE_2D, 0);
    }

    public void use() {
        GL33.glBindTexture(GL33.GL_TEXTURE_2D, _tex);
    }

    public float getUVOf(char c, UVPosition pos) {
        if (c == ' ') {
            return 0.0f;
        }
        if(FONT_TABLE.indexOf(c) == -1) {
            return 0.0f;
        }
        return _uvTable[(FONT_TABLE.indexOf(c)) * 4 + pos.ordinal()];
    }

    public int getWidthOf(char c) {
        if (c == ' ') {
            return 4;
        }
        return FONT_WIDTH;
    }

    public int getHeight() {
        return _height;
    }
}
