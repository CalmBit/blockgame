package gl

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL31.*
import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.MemoryStack
import java.io.File
import java.net.URL
import java.nio.ByteBuffer

open class Texture(file: File) {
    var tex: Int = glGenTextures()
    var width: Int
    var height: Int

    init {
        glBindTexture(GL_TEXTURE_2D, tex)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        var stack: MemoryStack? = null
        var img: ByteBuffer? = null
        val stream = javaClass.classLoader.getResourceAsStream(file.path)!!
        val bytes = stream.readBytes()
        val buf = BufferUtils.createByteBuffer(bytes.size)
        buf.put(bytes)
        buf.flip()
        try {
            stack = MemoryStack.stackPush()
            var w = stack.mallocInt(1)
            var h = stack.mallocInt(1)
            var chan = stack.mallocInt(1)
            img = stbi_load_from_memory(buf, w, h, chan, 4)
                ?: throw RuntimeException("Unable to load " + file.path + "\n" + stbi_failure_reason())
            width = w.get(0)
            height = h.get(0)
        } finally {
            stack?.pop()
        }
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, img)
        stbi_image_free(img)
        glBindTexture(GL_TEXTURE_2D, 0)
    }

    fun use() {
        glBindTexture(GL_TEXTURE_2D, tex)
    }
}