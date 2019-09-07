import client.Camera
import gl.*
import kotlinx.coroutines.runBlocking
import org.joml.Matrix4f
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL31.*
import org.lwjgl.stb.STBImageWrite
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil.NULL
import render.FontRenderer
import render.GuiRenderer
import render.PlaneRenderer
import world.World
import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.util.*

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
    var fWidth = 800
    var fHeight = 600
    var texUse = true

    var focused = true

    var camera = Camera()

    var view = Matrix4f()
    var proj = Matrix4f()

    val filedf = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS")

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

        _window = glfwCreateWindow(800, 600, "BlockGame", NULL, NULL)
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
            else if(key == GLFW_KEY_F2 && action == GLFW_RELEASE) {
                var b = BufferUtils.createByteBuffer(fWidth*fHeight*3)
                var t = BufferUtils.createByteBuffer(fWidth*fHeight*3)
                glReadPixels(0,0,fWidth, fHeight,GL_RGB, GL_UNSIGNED_BYTE, b)
                // who woulda thought that images are hard
                for(i in 0 until fHeight) {
                    for(j in 0 until fWidth) {
                        t.put(((fHeight-i-1) * fWidth * 3)+(j*3), b.get((i*fWidth * 3)+(j*3)))
                        t.put(((fHeight-i-1) * fWidth * 3)+(j*3)+1, b.get((i*fWidth * 3)+(j*3)+1))
                        t.put(((fHeight-i-1) * fWidth * 3)+(j*3)+2, b.get((i*fWidth * 3)+(j*3)+2))
                    }
                }

                val filename = "${filedf.format(Date())}.png"

                STBImageWrite.stbi_write_png(filename, fWidth, fHeight, 3, t, 0)
                Logger.logger.info("saved filename")
            }
            else {
                if(key > 0)
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
                camera.updateCameraRotation(xPos, yPos)
            }
        }

        glfwSetWindowSizeCallback(_window) {window: Long, width: Int, height: Int ->
            wWidth = width.toFloat()
            wHeight = height.toFloat()
        }

        glfwSetFramebufferSizeCallback(_window) {window: Long, width: Int, height: Int ->
            fWidth = width
            fHeight = height
        }


        var stack: MemoryStack? = null
        try {
            stack = MemoryStack.stackPush()
            val pWidth = stack.mallocInt(1)
            val pHeight = stack.mallocInt(1)
            var pFWidth = stack.mallocInt(1)
            var pFHeight = stack.mallocInt(1)

            glfwGetWindowSize(_window, pWidth, pHeight)
            glfwGetFramebufferSize(_window, pFWidth, pFHeight)

            wWidth = pWidth.get(0).toFloat()
            wHeight = pHeight.get(0).toFloat()

            fWidth = pFWidth.get(0)
            fHeight = pFHeight.get(0)

            val vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor())
            glfwSetWindowPos(_window, (vidMode!!.width() - wWidth.toInt()) / 2,
                (vidMode.height() - wHeight.toInt()) / 2)

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

        tex = Texture(File("texture", "terrain.png"))
        tex2 = Texture(File("texture", "terrain2.png"))
        ctex = Texture(File("texture", "crosshair.png"))

        tex!!.use()

        var vert = VertexShader(File("shader", "test.vert"))
        var frag = FragmentShader(File("shader", "test.frag"))
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
                camera.updatePosition(keyStates)
            }

            if(!texUse) {
                tex2!!.use()
            } else {
                tex!!.use()
            }

            if(Duration.between(lastTick, Instant.now()).toMillis() >= 40) {
                world!!.tick()
                lastTick = Instant.now()
                ticks++
            }

            FontRenderer.renderWithShadow(4.0f, 2.0f, "BlockGame v.Alpha 06132019 (FPS: $fps / TPS: $tps)", 1.0f)
            FontRenderer.renderWithShadow(4.0f, FontRenderer.font.height * 1.0f + 2.0f, "Position: ${camera.pos.toString(DecimalFormat("0.000"))} (Chunk: ${camera.pos.x.toInt() shr 4}, ${camera.pos.z.toInt() shr 4})", 1.0f)
            FontRenderer.renderWithShadow(4.0f, FontRenderer.font.height * 2.0f + 2.0f, "G: ${world!!.generateChunkQueue.size} / D: ${world!!.decorateChunkQueue.size} / R: ${world!!.renderChunkQueue.size} / B: ${world!!.bindChunkQueue.size}", 1.0f)
            FontRenderer.renderWithShadow(4.0f, FontRenderer.font.height * 3.0f + 2.0f, "Memory: ${(runtime.totalMemory() - runtime.freeMemory())/(1024*1024)}MB/${runtime.totalMemory()/(1024*1024)}MB", 1.0f)
            FontRenderer.renderWithShadow(4.0f, FontRenderer.font.height * 4.0f + 2.0f, "Seed: ${world!!.getSeed()}", 1.0f)
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

            var (view, proj) = camera.generateViewProj(renderDistance)

            PlaneRenderer.draw(view, proj, camera.pos, camera.pitch, camera.yaw)

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

            if(!world!!.chunkExists(Pair(camera.pos.x.toInt() shr 4, camera.pos.z.toInt() shr 4))) {
                world!!.lazyChunkQueue.offer(Pair(camera.pos.x.toInt() shr 4, camera.pos.z.toInt() shr 4))
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