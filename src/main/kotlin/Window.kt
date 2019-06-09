import gl.*
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
import render.FontRenderer
import world.World
import java.text.DecimalFormat
import java.time.Duration
import java.time.Instant

class Window {
    private var _window = NULL
    var uniTrans = 0
    var uniView = 0
    var uniProj = 0
    var textProj = 0
    var timer = 0.0f
    var frames = 0
    var fps = 0
    var lastFs: Instant = Instant.EPOCH
    var tex: Texture? = null
    var tex2: Texture? = null
    var ctex: Texture? = null
    var world: World? = null
    var prog: ShaderProgram? = null
    var textProg: ShaderProgram? = null

    var mlastX = 400.0
    var mlastY = 300.0

    var yaw = 0.0
    var pitch = 0.0

    var pos = Vector3f(16.0f, 84.0f, 16.0f)
    var front = Vector3f(0.0f, 0.0f, 32.0f)

    var view = Matrix4f()
    var proj = Matrix4f()

    var keyStates: Array<Boolean> = Array(GLFW_KEY_LAST+1) {false}

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

        glfwSetInputMode(_window, GLFW_CURSOR, GLFW_CURSOR_DISABLED)

        glfwSetCursorPosCallback(_window) {window :Long, xPos: Double, yPos: Double ->
            var xOffset = xPos - mlastX
            var yOffset = yPos - mlastY
            mlastX = xPos
            mlastY = yPos

            val sensitivity = 0.05
            xOffset *= sensitivity
            yOffset *= sensitivity

            yaw   += xOffset
            pitch -= yOffset

            if(pitch > 89.0)
                pitch =  89.0
            if(pitch < -89.0)
                pitch = -89.0

            var f = Vector3f()

            f.x = (Math.cos(Math.toRadians(pitch)) * Math.cos(Math.toRadians(yaw))).toFloat()
            f.y = (Math.sin(Math.toRadians(pitch))).toFloat()
            f.z = (Math.cos(Math.toRadians(pitch)) * Math.sin(Math.toRadians(yaw))).toFloat()
            front = f.normalize()
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
        glEnable(GL_BLEND)

        tex = Texture("texture/terrain.png")
        tex2 = Texture("texture/terrain2.png")
        ctex = Texture("texture/crosshair.png")

        tex!!.use()

        var vert = VertexShader("shader/test.vert")
        var frag = FragmentShader("shader/test.frag")
        prog = ShaderProgram(vert, frag)

        prog!!.use()

        uniTrans = glGetUniformLocation(prog!!.program, "model")
        uniView = glGetUniformLocation(prog!!.program, "view")
        uniProj = glGetUniformLocation(prog!!.program, "proj")

        var f = Instant.now()
        world = World()
        var a = Instant.now()
        Logger.logger.info("Gen: ${Duration.between(f, a)}")
        f = Instant.now()
        world!!.rebuildAllChunks(prog!!)
        a = Instant.now()
        Logger.logger.info("VBO: ${Duration.between(f, a)}")

        var textVert= VertexShader("shader/text.vert")
        var textFrag = FragmentShader("shader/text.frag")
        textProg = ShaderProgram(textVert, textFrag)

        textProg!!.use()

        textProj = glGetUniformLocation(textProg!!.program, "proj")

        FontRenderer.buildAttribs(textProg!!)
    }

    private fun loop() {

        if(lastFs == Instant.EPOCH) {
            lastFs = Instant.now()
        }
        glClearColor(0.529f, 0.808f, 0.980f, 1.0f)


        while (!glfwWindowShouldClose(_window)) {
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

            timer += 0.01f

            if(keyStates[GLFW_KEY_W]) pos.add(Vector3f(front.x * 0.5f, front.y * 0.5f, front.z * 0.5f))
            if(keyStates[GLFW_KEY_S]) pos.sub(Vector3f(front.x * 0.5f, front.y * 0.5f, front.z * 0.5f))
            if(keyStates[GLFW_KEY_A]) pos.sub(Vector3f(front.x, front.y, front.z)
                .cross(Vector3f(0.0f, 1.0f, 0.0f).div(2.0f)))
            if(keyStates[GLFW_KEY_D]) pos.add(Vector3f(front.x, front.y, front.z)
                .cross(Vector3f(0.0f, 1.0f, 0.0f).div(2.0f)))

            if(keyStates[GLFW_KEY_R]) {
                tex2!!.use()
            } else {
                tex!!.use()
            }

            FontRenderer.renderText(4.0f, 0.0f, "BlockGame v.06092019 (FPS: $fps)\nPosition: ${pos.toString(DecimalFormat("0.000"))}", 1.5f)
            FontRenderer.renderText(4.0f, 600.0f-(FontRenderer.font.height*1.5f), "BETA VERSION (donut steel)", 1.5f)

            prog!!.use()
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
            var stack: MemoryStack? = null
            try {
                stack = MemoryStack.stackPush()
                view = Matrix4f()
                    .lookAt(
                        pos,
                        Vector3f(pos.x + front.x, pos.y + front.y, pos.z + front.z),
                    Vector3f(0.0f,1.0f,0.0f))
                glUniformMatrix4fv(uniView, false, view.get(stack.mallocFloat(16)))
                proj = Matrix4f()
                    .perspective(Math.toRadians(90.0).toFloat(),800.0f/600.0f,1.0f,100.0f)
                glUniformMatrix4fv(uniProj, false, proj.get(stack.mallocFloat(16)))
            } finally {
                stack?.pop()
            }

            world!!.draw(uniTrans, timer)

            textProg!!.use()

            stack = null
            try {
                stack = MemoryStack.stackPush()
                proj = Matrix4f()
                    .ortho(0.0f, 800.0f, 600.0f, 0.0f, 0.0f, 10.0f)
                glUniformMatrix4fv(textProj, false, proj.get(stack.mallocFloat(16)))
            } finally {
                stack?.pop()
            }
            FontRenderer.draw()

            frames++
            if(Duration.between(lastFs, Instant.now()).seconds >= 1) {
                fps = frames
                frames = 0
                lastFs = Instant.now()
            }

            glfwSwapBuffers(_window)
            glfwPollEvents()
        }
    }


}