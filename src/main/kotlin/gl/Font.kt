package gl

import org.lwjgl.opengl.GL31.*
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryStack
import java.lang.RuntimeException
import java.nio.ByteBuffer

class Font(file: String) {
    var tex: Int = glGenTextures()
    var width: Int
    var height: Int
    var fontTable =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.,;:?!-_~#\"'&()[]{}^|`/\\@°+=*%€\$£¢<>©®ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØŒÙÚÛÜÝÞàáâãäåæçèéêëìíîïðñòóôõöøœùúûüýþßÿ¿¡"
    var uvTable: Array<Float> = Array(fontTable.length * 4) { 0.0f }
    var currentU: Float = 0.0f
    var fontWidths = Array(fontTable.length) { 0 }

    init {
        glBindTexture(GL_TEXTURE_2D, tex)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        var stack: MemoryStack? = null
        var img: ByteBuffer? = null
        try {
            stack = MemoryStack.stackPush()
            var w = stack.mallocInt(1)
            var h = stack.mallocInt(1)
            var chan = stack.mallocInt(1)
            img = STBImage.stbi_load(javaClass.classLoader.getResource(file).path, w, h, chan, 4)
                ?: throw RuntimeException("Unable to load " + file + "\n" + STBImage.stbi_failure_reason())
            width = w.get(0)
            height = h.get(0)
        } finally {
            stack?.pop()
        }
        glTexImage2D(
            GL_TEXTURE_2D,
            0,
            GL_RGBA,
            width,
            height,
            0,
            GL_RGBA,
            GL_UNSIGNED_BYTE,
            img
        )

        var r: Byte = 0
        var g: Byte = 0
        var b: Byte = 0
        var a: Byte = 0
        val nil: Byte = 0x00
        var currentLetter = 0
        var lWidth = 0
        if (img != null) {
            for (x in 0 until width) {
                var empty = true
                for (y in 0 until height) {
                    a = img[(y * width * 4) + (x * 4) + 3]

                    if (a != nil) {
                        lWidth++
                        empty = false
                        break
                    }
                }
                if (empty) {
                    fontWidths[currentLetter] = lWidth
                    uvTable[currentLetter * 4] = currentU
                    uvTable[currentLetter * 4 + 1] = 0.0f
                    uvTable[currentLetter * 4 + 2] = currentU + (lWidth.toFloat() * (1.0f / width))
                    uvTable[currentLetter * 4 + 3] = 1.0f
                    currentU += (lWidth + 1).toFloat() * (1.0f / width)

                    lWidth = 0
                    currentLetter++
                }
            }
        }
        STBImage.stbi_image_free(img)
        glBindTexture(GL_TEXTURE_2D, 0)
    }

    fun use() {
        glBindTexture(GL_TEXTURE_2D, tex)
    }

    fun getUVOf(c: Char, pos: UVPosition): Float {
        if (c == ' ') {
            return 0.0f
        }
        return uvTable[(fontTable.indexOf(c)) * 4 + pos.ordinal]
    }

    fun getWidthOf(c: Char): Int {
        if (c == ' ') {
            return 4
        }
        return fontWidths[fontTable.indexOf(c)]
    }
}