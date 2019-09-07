package gl

import org.lwjgl.opengl.GL31.GL_VERTEX_SHADER
import java.io.File

class VertexShader(path: File) : Shader(GL_VERTEX_SHADER, path)