package gl

import org.lwjgl.opengl.GL31.*

class ShaderProgram(vertexShader: VertexShader, fragmentShader: FragmentShader) {
    var program: Int = glCreateProgram()

    init {
        glAttachShader(program, vertexShader.shader)
        glAttachShader(program, fragmentShader.shader)

        glBindFragDataLocation(program, 0, "outColor")
        glLinkProgram(program)

        use()
    }

    fun use() {
        glUseProgram(program)
    }
}