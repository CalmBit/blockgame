package gl

import org.lwjgl.opengl.GL31.GL_FRAGMENT_SHADER
import java.io.File

class FragmentShader(path: File) : Shader(GL_FRAGMENT_SHADER, path)