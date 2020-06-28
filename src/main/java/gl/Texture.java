package gl;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL31;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Texture {
    private final int _tex;
    private final int _width;
    private final int _height;

    public Texture(File file) throws IOException {
        _tex = GL31.glGenTextures();
        GL31.glBindTexture(GL31.GL_TEXTURE_2D, _tex);
        GL31.glTexParameteri(GL31.GL_TEXTURE_2D, GL31.GL_TEXTURE_WRAP_S, GL31.GL_REPEAT);
        GL31.glTexParameteri(GL31.GL_TEXTURE_2D, GL31.GL_TEXTURE_WRAP_T, GL31.GL_REPEAT);
        GL31.glTexParameteri(GL31.GL_TEXTURE_2D, GL31.GL_TEXTURE_MIN_FILTER, GL31.GL_NEAREST);
        GL31.glTexParameteri(GL31.GL_TEXTURE_2D, GL31.GL_TEXTURE_MAG_FILTER, GL31.GL_NEAREST);
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
        ByteBuffer buf = BufferUtils.createByteBuffer(bytes.length);
        buf.put(bytes);
        buf.flip();
        try {
            stack = MemoryStack.stackPush();
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer chan = stack.mallocInt(1);
            img = STBImage.stbi_load_from_memory(buf, w, h, chan, 4);
            if(img == null)
                throw new RuntimeException("Unable to load " + file.getPath() + "\n" + STBImage.stbi_failure_reason());
            _width = w.get(0);
            _height = h.get(0);
        } finally {
            stack.pop();
        }
        GL31.glTexImage2D(GL31.GL_TEXTURE_2D, 0, GL31.GL_RGBA, _width, _height, 0, GL31.GL_RGBA, GL31.GL_UNSIGNED_BYTE, img);
        STBImage.stbi_image_free(img);
        GL31.glBindTexture(GL31.GL_TEXTURE_2D, 0);
    }

    public void use() {
        GL31.glBindTexture(GL31.GL_TEXTURE_2D, _tex);
    }
}
