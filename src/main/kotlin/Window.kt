import gl.FragmentShader
import gl.ShaderProgram
import gl.Texture
import gl.VertexShader
import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL31.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil.NULL
import world.World

class Window {
    private var _window = NULL
    var uniTrans = 0
    var uniView = 0
    var uniProj = 0
    var timer = 0.0f
    var tex: Texture? = null
    var tex2: Texture? = null
    var world: World? = null

    var keyStates: Array<Boolean> = Array(GLFW_KEY_LAST+1) {false}

    var cX = 0.0f
    var cY = 0.0f
    var cZ = 0.0f

    fun run() {
        Logger.logger.debug("[RUN]")

        init()
        loop()

        glfwFreeCallbacks(_window)
        glfwSetErrorCallback(null)?.free()
    }

    private fun init() {
        Logger.logger.debug("[INIT]")
        GLFWErrorCallback.createPrint(System.err)

        if (!glfwInit())
            throw IllegalStateException("Unable to initialize GLFW")

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE)
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)

        _window = glfwCreateWindow(800, 600, "Hello, world!", NULL, NULL)
        if (_window == NULL)
            throw RuntimeException("Failed to create Window")

        glfwSetKeyCallback(_window) { window: Long, key: Int, scancode: Int, action: Int, mods: Int ->
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true)
            else {
                keyStates[key] = (action != GLFW_RELEASE)
            }
        }



        var stack: MemoryStack? = null
        try {
            stack = MemoryStack.stackPush()
            val pWidth = stack.mallocInt(1)
            val pHeight = stack.mallocInt(1)
            glfwGetWindowSize(_window, pWidth, pHeight)
            val vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor())
            glfwSetWindowPos(_window, (vidMode!!.width() - pWidth.get(0)) / 2,
                (vidMode.height() - pHeight.get(0)) / 2)
        } finally {
            stack?.pop()
        }

        glfwMakeContextCurrent(_window)
        glfwSwapInterval(1)
        glfwShowWindow(_window)

        GL.createCapabilities()

        glEnable(GL_DEPTH_TEST)
        glEnable(GL_CULL_FACE)

        tex = Texture("texture/terrain.png")
        tex2 = Texture("texture/terrain2.png")

        tex!!.use()

        var vert = VertexShader("shader/test.vert")
        var frag = FragmentShader("shader/test.frag")
        var prog = ShaderProgram(vert, frag)

        prog.use()

        uniTrans = glGetUniformLocation(prog.program, "model")
        uniView = glGetUniformLocation(prog.program, "view")
        uniProj = glGetUniformLocation(prog.program, "proj")

        world = World()
        world!!.rebuildAllChunks(prog)
    }

    private fun loop() {

        glClearColor(0.529f, 0.808f, 0.980f, 1.0f)


        while (!glfwWindowShouldClose(_window)) {
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

            timer += 0.01f

            if(keyStates[GLFW_KEY_W]) cZ += 0.5f
            if(keyStates[GLFW_KEY_S]) cZ -= 0.5f
            if(keyStates[GLFW_KEY_A]) cX += 0.5f
            if(keyStates[GLFW_KEY_D]) cX -= 0.5f
            if(keyStates[GLFW_KEY_Q]) cY -= 0.5f
            if(keyStates[GLFW_KEY_E]) cY += 0.5f

            if(keyStates[GLFW_KEY_R]) {
                tex2!!.use()
            } else {
                tex!!.use()
            }


            var stack: MemoryStack? = null
            try {
                stack = MemoryStack.stackPush()
                var view = Matrix4f()
                    .lookAt(Vector3f(cX, cY, cZ),
                    Vector3f(cX,cY, cZ+32.0f),
                    Vector3f(0.0f,1.0f,0.0f))
                    .get(stack.mallocFloat(16))
                glUniformMatrix4fv(uniView, false, view)
                var proj = Matrix4f()
                    .perspective(Math.toRadians(90.0).toFloat(),800.0f/600.0f,1.0f,100.0f)
                    .get(stack.mallocFloat(16))
                glUniformMatrix4fv(uniProj, false, proj)
            } finally {
                stack?.pop()
            }

            world!!.draw(uniTrans, timer)
            glfwSwapBuffers(_window)
            glfwPollEvents()
        }
    }


}