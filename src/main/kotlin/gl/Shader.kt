package gl

import org.lwjgl.opengl.GL31.*
import org.lwjgl.system.MemoryStack
import java.lang.IllegalStateException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

open class Shader(type: Int, path: String) {
    var shader: Int = glCreateShader(type)
    var good = false

    init {
        var file = javaClass.classLoader.getResource(path)
        var truePath = Paths.get(file.toURI())
        val text = String(Files.readAllBytes(truePath), StandardCharsets.UTF_8)
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