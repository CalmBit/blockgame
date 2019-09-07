package gl

import org.lwjgl.opengl.GL31.*
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryStack
import java.io.File
import java.lang.RuntimeException
import java.nio.ByteBuffer

class Font(file: File) {
    var tex: Int = glGenTextures()
    var width: Int
    var height: Int
    var fontTable =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.,;:?!-_~#\"'&()[]"//{}^|`/\\@°+=*%€\$£¢<>©®ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØŒÙÚÛÜÝÞàáâãäåæçèéêëìíîïðñòóôõöøœùúûüýþßÿ¿¡"
    var uvTable: Array<Float> = Array(fontTable.length * 4) { 0.0f }

    var fontWidth = 8
    var vOffset: Float
    var uOffset: Float
    val u: Float

    init {
        glBindTexture(GL_TEXTURE_2D, tex)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        var stack: MemoryStack? = null
        var img: ByteBuffer? = null
        var fileName = File(javaClass.classLoader.getResource("").path, file.path).absolutePath
        try {
            stack = MemoryStack.stackPush()
            var w = stack.mallocInt(1)
            var h = stack.mallocInt(1)
            var chan = stack.mallocInt(1)
            img = STBImage.stbi_load(fileName, w, h, chan, 4)
                ?: throw RuntimeException("Unable to load " + file + "\n" + STBImage.stbi_failure_reason())
            width = w.get(0)
            height = h.get(0)
            vOffset = 1.0f/(height.toFloat()*8.0f)
            uOffset = 1.0f/(width.toFloat()*8.0f)
        } finally {
            stack?.pop()
        }
        u = 8.0f/(width.toFloat())
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
        var lWidth = 0
        if (img != null) {
            for(currentLetter in 0 until fontTable.length) {
                uvTable[currentLetter * 4] = (currentLetter.toFloat()*u) + uOffset
                uvTable[currentLetter * 4 + 1] = vOffset
                uvTable[currentLetter * 4 + 2] = (currentLetter.toFloat()*u) + u  - uOffset
                uvTable[currentLetter * 4 + 3] = 1.0f - vOffset
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
        if(fontTable.indexOf(c) == -1) {
            return 0.0f
        }
        return uvTable[(fontTable.indexOf(c)) * 4 + pos.ordinal]
    }

    fun getWidthOf(c: Char): Int {
        if (c == ' ') {
            return 4
        }
        return fontWidth
    }
}