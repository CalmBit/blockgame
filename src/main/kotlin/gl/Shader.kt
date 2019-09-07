package gl

import org.lwjgl.opengl.GL31.*
import org.lwjgl.system.MemoryStack
import java.io.File
import java.lang.IllegalStateException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

open class Shader(type: Int, file: File) {
    var shader: Int = glCreateShader(type)
    var good = false

    init {
        var filePath = File(javaClass.classLoader.getResource("").path, file.path).toPath()
        val text = String(Files.readAllBytes(filePath), StandardCharsets.UTF_8)
        glShaderSource(shader, text)
        glCompileShader(shader)

        var stack: MemoryStack? = null
        try {
            stack = MemoryStack.stackPush()
            val buff = stack.mallocInt(1)
            glGetShaderiv(shader, GL_COMPILE_STATUS, buff)

            if (buff.get(0) != GL_TRUE)
                throw IllegalStateException(glGetShaderInfoLog(shader, 512))
            good = true
        } finally {
            stack?.pop()
        }
    }

}