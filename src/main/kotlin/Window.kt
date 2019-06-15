import gl.*
import kotlinx.coroutines.runBlocking
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
import render.GuiRenderer
import render.PlaneRenderer
import world.World
import java.text.DecimalFormat
import java.time.Duration
import java.time.Instant

class Window {
    private var _window = NULL
    var uniTrans = 0
    var uniView = 0
    var uniProj = 0
    var uniFog = 0
    var timer = 0.0f
    var frames = 0
    var ticks = 0
    var fps = 0
    var tps = 0
    var renderDistance = 128.0f
    var lastFs: Instant = Instant.EPOCH
    var lastTick: Instant = Instant.EPOCH
    var tex: Texture? = null
    var tex2: Texture? = null
    var ctex: Texture? = null
    var world: World? = null
    var prog: ShaderProgram? = null
    var wWidth = 800.0f
    var wHeight = 600.0f
    var texUse = true

    var focused = true

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
        runBlocking {
            loop()
        }

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
                if(!focused)
                    glfwSetWindowShouldClose(window, true)
                else {
                    focused = false
                    glfwSetInputMode(_window, GLFW_CURSOR, GLFW_CURSOR_NORMAL)
                }
            else if(key == GLFW_KEY_R && action == GLFW_RELEASE) {
                texUse = !texUse
            }
            else {
                keyStates[key] = (action != GLFW_RELEASE)
            }
        }

        glfwSetInputMode(_window, GLFW_CURSOR, GLFW_CURSOR_DISABLED)

        glfwSetMouseButtonCallback(_window) {window: Long, button: Int, action: Int, mods: Int ->
            if(!focused && button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
                focused = true
                glfwSetInputMode(_window, GLFW_CURSOR, GLFW_CURSOR_DISABLED)
            }
        }

        glfwSetCursorPosCallback(_window) {window :Long, xPos: Double, yPos: Double ->
            if(focused) {
                var xOffset = xPos - mlastX
                var yOffset = yPos - mlastY
                mlastX = xPos
                mlastY = yPos

                val sensitivity = 0.05
                xOffset *= sensitivity
                yOffset *= sensitivity

                yaw += xOffset
                pitch -= yOffset

                if (pitch > 89.0)
                    pitch = 89.0
                if (pitch < -89.0)
                    pitch = -89.0

                var f = Vector3f()

                f.x = (Math.cos(Math.toRadians(pitch)) * Math.cos(Math.toRadians(yaw))).toFloat()
                f.y = (Math.sin(Math.toRadians(pitch))).toFloat()
                f.z = (Math.cos(Math.toRadians(pitch)) * Math.sin(Math.toRadians(yaw))).toFloat()
                front = f.normalize()
            }
        }

        glfwSetWindowSizeCallback(_window) {window: Long, width: Int, height: Int ->
            wWidth = width.toFloat()
            wHeight = height.toFloat()
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
        uniFog = glGetUniformLocation(prog!!.program, "fogColor")

        world = World(_window, prog!!)

        PlaneRenderer.setColors(world!!.worldType)
    }

    private suspend fun loop() {

        val runtime = Runtime.getRuntime()

        if(lastFs == Instant.EPOCH) {
            lastFs = Instant.now()
        }
        val atmocolor = world!!.worldType.atmoColor
        glClearColor(atmocolor.x, atmocolor.y, atmocolor.z, 1.0f)


        while (!glfwWindowShouldClose(_window)) {
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

            timer += 0.01f

            if(focused) {
                if (keyStates[GLFW_KEY_W]) pos.add(Vector3f(front.x * 0.5f, front.y * 0.5f, front.z * 0.5f))
                if (keyStates[GLFW_KEY_S]) pos.sub(Vector3f(front.x * 0.5f, front.y * 0.5f, front.z * 0.5f))
                if (keyStates[GLFW_KEY_A]) pos.sub(
                    Vector3f(front.x, front.y, front.z)
                        .cross(Vector3f(0.0f, 1.0f, 0.0f).div(2.0f))
                )
                if (keyStates[GLFW_KEY_D]) pos.add(
                    Vector3f(front.x, front.y, front.z)
                        .cross(Vector3f(0.0f, 1.0f, 0.0f).div(2.0f))
                )
            }

            if(!texUse) {
                tex2!!.use()
            } else {
                tex!!.use()
            }

            if(Duration.between(lastTick, Instant.now()).toMillis() >= 50) {
                world!!.tick()
                lastTick = Instant.now()
                ticks++
            }

            FontRenderer.renderWithShadow(4.0f, 0.0f, "BlockGame v.Alpha 06132019 (FPS: $fps / TPS: $tps)", 1.5f)
            FontRenderer.renderWithShadow(4.0f, FontRenderer.font.height * 1.5f + 1.5f, "Position: ${pos.toString(DecimalFormat("0.000"))} (Chunk: ${pos.x.toInt() shr 4}, ${pos.z.toInt() shr 4})", 1.5f)
            FontRenderer.renderWithShadow(4.0f, FontRenderer.font.height * 3.0f + 1.5f, "G: ${world!!.generateChunkQueue.size} / D: ${world!!.decorateChunkQueue.size} / R: ${world!!.renderChunkQueue.size} / B: ${world!!.bindChunkQueue.size}", 1.5f)
            FontRenderer.renderWithShadow(4.0f, FontRenderer.font.height * (4.5f) + 1.5f, "Memory: ${(runtime.totalMemory() - runtime.freeMemory())/(1024*1024)}MB/${runtime.totalMemory()/(1024*1024)}MB", 1.5f)

            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

            view = Matrix4f()
                .lookAt(
                    pos,
                    Vector3f(pos.x + front.x, pos.y + front.y, pos.z + front.z),
                    Vector3f(0.0f,1.0f,0.0f))
            proj = Matrix4f()
                .perspective(Math.toRadians(90.0).toFloat(),800.0f/600.0f,1.0f,renderDistance)

            PlaneRenderer.draw(view, proj ,pos, pitch, yaw)

            prog!!.use()

            var stack: MemoryStack? = null
            try {
                stack = MemoryStack.stackPush()
                glUniformMatrix4fv(uniView, false, view.get(stack.mallocFloat(16)))
                glUniformMatrix4fv(uniProj, false, proj.get(stack.mallocFloat(16)))
                glUniform3fv(uniFog, atmocolor.get(stack.mallocFloat(3)))
            } finally {
                stack?.pop()
            }

            world!!.draw(uniTrans, timer)

            proj = Matrix4f()
                .ortho(0.0f, wWidth, wHeight, 0.0f, -1.0f, 10.0f)

            FontRenderer.draw(proj)

            if(!focused) {
                GuiRenderer.renderDoverlay()
            }

            fun bindChunk(batch: world.BindChunkBatch) {
                batch.first.bindRenderData(batch.second, batch.third, prog!!)
            }

            if(world!!.bindChunkQueue.size > 0) {
                if(world!!.bindChunkQueue.size < 24) {
                    while(world!!.bindChunkQueue.size > 0) {
                        bindChunk(world!!.bindChunkQueue.remove())
                    }
                } else {
                    for(i in 0 until 24) {
                        bindChunk(world!!.bindChunkQueue.remove())
                    }
                }
            }

            if(!world!!.chunkExists(Pair(pos.x.toInt() shr 4, pos.z.toInt() shr 4))) {
                world!!.lazyChunkQueue.offer(Pair(pos.x.toInt() shr 4, pos.z.toInt() shr 4))
            }

            if(world!!.lazyChunkQueue.size > 0) {
                while(world!!.lazyChunkQueue.size > 0) {
                    var cPos = world!!.lazyChunkQueue.remove()
                    world!!.generateChunkQueue.offer(world!!.addChunk(cPos))
                }
            }

            frames++
            if(Duration.between(lastFs, Instant.now()).seconds >= 1) {
                fps = frames
                tps = ticks
                frames = 0
                ticks = 0
                lastFs = Instant.now()
            }

            glfwSwapBuffers(_window)
            glfwPollEvents()
        }
    }


}