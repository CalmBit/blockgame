package blockgame;

import blockgame.client.Camera;
import blockgame.client.GameState;
import blockgame.client.ViewProj;
import blockgame.gl.*;
import blockgame.registry.RegistryName;
import blockgame.render.*;
import blockgame.util.FloatListCache;
import blockgame.worker.BindChunkQueue;
import blockgame.worker.DecoratorPool;
import blockgame.worker.GeneratorPool;
import blockgame.worker.RenderPool;
import blockgame.world.World;
import blockgame.world.generators.DensityGenerator;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private long _window = 0;
    private int _uniTrans = 0;
    private int _uniView = 0;
    private int _uniProj = 0;
    private int _uniFog = 0;
    private int _uniTime = 0;
    private int _uniGamma = 0;
    private float _timer = 0.0f;
    private int _frames = 0;
    private int _ticks = 0;
    private int _fps = 0;
    private int _tps = 0;
    private float _renderDistance = 128.0f;
    private Instant _lastFs = Instant.EPOCH;
    private Instant _lastTick = Instant.EPOCH;
    private Texture _tex = null;
    private Texture _tex2 = null;

    private World _world = null;
    private ShaderProgram _prog = null;
    private float _wWidth = 800.0f;
    private float _wHeight = 600.0f;
    private int _fWidth = 800;
    private int _fHeight = 600;
    private boolean _texUse = true;

    private float _gamma = 1.0f;

    private boolean _focused = true;
    public static boolean refocusRequested = false;

    private Camera _camera = new Camera();

    private ViewProj _viewproj = new ViewProj(new Matrix4f(), new Matrix4f());
    private Matrix4f _proj = new Matrix4f();

    private Matrix4f _guiMat = new Matrix4f()
                                    .ortho(0.0f, _wWidth, _wHeight, 0.0f, -1.0f, 10.0f);

    private Vector3f atmocolor = new Vector3f(0.0f, 0.0f, 0.0f);

    private MemoryStack _stack = null;

    private static final SimpleDateFormat _SCREENSHOT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");

    private static final boolean[] keyStates = new boolean[GLFW.GLFW_KEY_LAST+1];

    private double _lastFrame = 0.0;

    private GameState _state = GameState.SPLASH;

    public void run() throws IOException {
        Logger.LOG.debug("[RUN]");

        init();
        loop();

        GeneratorPool.shutdown();
        DecoratorPool.shutdown();
        RenderPool.shutdown();

        Callbacks.glfwFreeCallbacks(_window);
        GLFW.glfwDestroyWindow(_window);

        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null);
    }

    private void init() throws IOException {
        Logger.LOG.debug("[INIT]");
        GLFWErrorCallback.createPrint(System.err);

        if (!GLFW.glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL33.GL_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);

        _window = GLFW.glfwCreateWindow(800, 600, "BlockGame", NULL, NULL);
        if (_window == NULL)
            throw new RuntimeException("Failed to create Window");

        GLFW.glfwSetKeyCallback(_window, (long window, int key, int scancode, int action, int mods) -> {
            if (_state != GameState.INGAME)
                return;
            if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE)
                if (!_focused) {
                    refocusWindow();
                } else {
                    _focused = false;
                }
            else if (key == GLFW.GLFW_KEY_R && action == GLFW.GLFW_RELEASE) {
                _texUse = !_texUse;
            } else if (key == GLFW.GLFW_KEY_G && action == GLFW.GLFW_RELEASE) {
                System.gc();
            } else if (key == GLFW.GLFW_KEY_F2 && action == GLFW.GLFW_RELEASE) {
                ByteBuffer b = BufferUtils.createByteBuffer(_fWidth * _fHeight * 3);
                ByteBuffer t = BufferUtils.createByteBuffer(_fWidth * _fHeight * 3);
                GL33.glReadPixels(0, 0, _fWidth, _fHeight, GL33.GL_RGB, GL33.GL_UNSIGNED_BYTE, b);
                // who woulda thought that images are hard
                for (int i =0;i < _fHeight;i++){
                    for (int j =0;j < _fWidth;j++){
                        t.put(((_fHeight - i - 1) * _fWidth * 3) + (j * 3), b.get((i * _fWidth * 3) + (j * 3)));
                        t.put(((_fHeight - i - 1) * _fWidth * 3) + (j * 3) + 1, b.get((i * _fWidth * 3) + (j * 3) + 1));
                        t.put(((_fHeight - i - 1) * _fWidth * 3) + (j * 3) + 2, b.get((i * _fWidth * 3) + (j * 3) + 2));
                    }
                }

                File screenshotFolder = new File("screenshots");
                if(!screenshotFolder.exists()) {
                    screenshotFolder.mkdir();
                }
                if(!screenshotFolder.isDirectory()) {
                    Logger.LOG.error("Can't save to screenshots directory - screenshots/ is not a directory!");
                } else {
                    String filename = "screenshots/"+_SCREENSHOT_DATE_FORMAT.format(new Date())+".png";

                    STBImageWrite.stbi_write_png(filename, _fWidth, _fHeight, 3, t, 0);
                    Logger.LOG.info("saved to '" + filename + "'");
                }
            } else {
                if (key > 0)
                    keyStates[key] = (action != GLFW.GLFW_RELEASE);
            }
        });

        GLFW.glfwSetWindowFocusCallback(_window, (long window, boolean focused) -> {
           if(_focused && !focused) {
               _focused = false;
           }
        });

        GLFW.glfwSetMouseButtonCallback(_window, (long window, int button, int action, int mods) -> {
            if (!_focused) {
                GuiRenderer.mouseClick(button, action);
            }
        });

        GLFW.glfwSetCursorPosCallback(_window, (long window, double x, double y) -> {
            if(_focused) {
                _camera.updateCameraRotation(x, y);
            } else {
                GuiRenderer.updateScreenMouse((float)x, (float)y);
            }
        });

        GLFW.glfwSetWindowSizeCallback(_window, (long window, int width, int height) -> {
            _wWidth = (float)width;
            _wHeight = (float)height;
            GuiRenderer.updateWindowSize(_wWidth, _wHeight);
        });

        GLFW.glfwSetFramebufferSizeCallback(_window, (long window, int width, int height) -> {
            _fWidth = width;
            _fHeight = height;
        });

        try {
            _stack = MemoryStack.stackPush();
            IntBuffer pWidth = _stack.mallocInt(1);
            IntBuffer pHeight = _stack.mallocInt(1);
            IntBuffer pFWidth = _stack.mallocInt(1);
            IntBuffer pFHeight = _stack.mallocInt(1);

            GLFW.glfwGetWindowSize(_window, pWidth, pHeight);
            GLFW.glfwGetFramebufferSize(_window, pFWidth, pFHeight);

            _wWidth = (float)pWidth.get(0);
            _wHeight = (float)pHeight.get(0);

            _fWidth = pFWidth.get(0);
            _fHeight = pFHeight.get(0);

            GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
            GLFW.glfwSetWindowPos(_window, (vidMode.width() - (int)_wWidth) / 2,
                    (vidMode.height() - (int)_wHeight) / 2);

        } finally {
            _stack.pop();
        }

        GLFW.glfwMakeContextCurrent(_window);
        //GLFW.glfwSwapInterval(1);
        GLFW.glfwShowWindow(_window);

        GL.createCapabilities();

        GL33.glEnable(GL33.GL_DEPTH_TEST);
        GL33.glEnable(GL33.GL_CULL_FACE);
        GL33.glEnable(GL33.GL_BLEND);

        try {
            GuiRenderer.init();
            PlaneRenderer.init();
            _tex = TextureManager.getTexture(new RegistryName("blockgame", "terrain"));
            _tex2 = TextureManager.getTexture(new RegistryName("blockgame", "terrain2"));
            GuiRenderer.ctex = TextureManager.getTexture(new RegistryName("blockgame", "crosshair"));

            _tex.use();

            VertexShader vert = new VertexShader(new File("shader", "world.vert"));
            FragmentShader frag = new FragmentShader(new File("shader", "world.frag"));
            _prog = new ShaderProgram(vert, frag);

            _prog.use();

            _uniTrans = GL33.glGetUniformLocation(_prog.getProgram(), "model");
            _uniView = GL33.glGetUniformLocation(_prog.getProgram(), "view");
            _uniProj = GL33.glGetUniformLocation(_prog.getProgram(), "proj");
            _uniFog = GL33.glGetUniformLocation(_prog.getProgram(), "fogColor");
            _uniTime = GL33.glGetUniformLocation(_prog.getProgram(), "time");
            _uniGamma = GL33.glGetUniformLocation(_prog.getProgram(), "gamma");

            GuiRenderer.updateWindowSize(_wWidth, _wHeight);


            GuiRenderer.attachScreen(new GuiSplashScreen());
        } catch (Exception e) {
            throw e;
        }
    }

    private void refocusWindow() {
        _focused = true;
        GuiRenderer.clearScreen();
        GLFW.glfwSetInputMode(_window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
        _camera.resetMouse();
    }

    private void transitionState(GameState state) {
        _state = state;
        switch(state) {
            case SPLASH:
                return;
            case LOADING:
                _world = new World();
                PlaneRenderer.setColors(_world.worldType);
                atmocolor = _world.worldType.atmoColor;
                GuiRenderer.attachScreen(new GuiLoadingScreen(DensityGenerator.act_gen, DensityGenerator.act_form));
                break;
            case INGAME:
                GuiRenderer.clearScreen();
                if(_focused) {
                    refocusWindow();
                } else {
                    GuiRenderer.attachScreen(new GuiPauseScreen());
                    GLFW.glfwSetInputMode(_window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
                }
                break;
        }
    }

    private void update() {
        switch(_state) {
            case SPLASH:
                _timer += 1f;
                if(_timer == 120f) {
                    _timer = 0.0f;
                    transitionState(GameState.LOADING);
                }
                break;
            case LOADING:
                if(GeneratorPool.queueSize() == 0 && RenderPool.queueSize() == 0) {
                    transitionState(GameState.INGAME);
                }
                break;
            case INGAME:
                _timer += 0.01f;

                double currentFrame = GLFW.glfwGetTime();
                double delta = currentFrame - _lastFrame;
                _lastFrame = currentFrame;

                if (_focused) {
                    _camera.updatePosition(delta, keyStates);
                }

                if(!_focused && !GuiRenderer.screenAttached()) {
                    GuiRenderer.attachScreen(new GuiPauseScreen());
                    GLFW.glfwSetInputMode(_window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
                }

                if(refocusRequested) {
                    if(!_focused) {
                        refocusWindow();
                    }
                    refocusRequested = false;
                }

                if (Duration.between(_lastTick, Instant.now()).toMillis() >= 50) {
                    _world.tick();
                    _lastTick = Instant.now();
                    _ticks++;
                }
                break;
        }

        BindChunkQueue.bindChunks(_prog);

        FloatListCache.watch();
    }

    public void renderWorld() {
        switch(_state) {
            case INGAME:
                if (!_texUse) {
                    _tex2.use();
                } else {
                    _tex.use();
                }

                GL33.glBlendFunc(GL33.GL_SRC_ALPHA, GL33.GL_ONE_MINUS_SRC_ALPHA);

                _viewproj = _camera.generateViewProj(_renderDistance);

                PlaneRenderer.draw(_viewproj.view, _viewproj.proj, _camera.getPos(), _camera.getPitch(), _camera.getYaw());

                _prog.use();

                try {
                    _stack = MemoryStack.stackPush();
                    GL33.glUniformMatrix4fv(_uniView, false, _viewproj.view.get(_stack.mallocFloat(16)));
                    GL33.glUniformMatrix4fv(_uniProj, false, _viewproj.proj.get(_stack.mallocFloat(16)));
                    GL33.glUniform3fv(_uniFog, atmocolor.get(_stack.mallocFloat(3)));
                    GL33.glUniform1f(_uniTime, _timer);
                    GL33.glUniform1f(_uniGamma, _gamma);
                } finally {
                    _stack.pop();
                }

                _world.draw(_uniTrans, _timer);
                break;
            default:
                break;
        }
    }

    public void renderGui(Runtime runtime) {
        GL33.glBlendFunc(GL33.GL_SRC_ALPHA, GL33.GL_ONE_MINUS_SRC_ALPHA);
        GL33.glDisable(GL33.GL_DEPTH_TEST);

        switch(_state) {
            case SPLASH:
            case LOADING:
                _proj = new Matrix4f().ortho(0.0f, _wWidth, _wHeight, 0.0f, -1.0f, 10.0f);
                FontRenderer.FONT_RENDERER.draw(_proj);
                GuiRenderer.renderScreen(_proj);
                FontRenderer.FONT_RENDERER.draw(_proj);
                break;
            case INGAME:
                GuiRenderer.renderCrosshair(_guiMat);

                FontRenderer.FONT_RENDERER.renderTextWithFrame(
                        4.0f,
                        2.0f,
                        "BlockGame pre-070320 (FPS: "+_fps+" / TPS: "+_tps+")",
                        1.0f
                );
                FontRenderer.FONT_RENDERER.renderTextWithFrame(
                        4.0f,
                        FontRenderer.FONT_RENDERER.font.getHeight() * 1.0f + 4.0f,
                        "Position: (X: "+_camera.getPos().x+" / Y: "+_camera.getPos().y+" / Z: "+_camera.getPos().z+") (Chunk: "+((int)_camera.getPos().x >> 4)+", "+((int)_camera.getPos().z >> 4)+")",
                        1.0f
                );
                FontRenderer.FONT_RENDERER.renderTextWithFrame(
                        4.0f,
                        FontRenderer.FONT_RENDERER.font.getHeight() * 2.0f + 6.0f,
                        "G: "+GeneratorPool.queueSize() + " / D: "+ DecoratorPool.queueSize() + " / R: "+ RenderPool.queueSize()+" / B: "+BindChunkQueue.queueSize(),
                        1.0f
                );
                FontRenderer.FONT_RENDERER.renderTextWithFrame(
                        4.0f,
                        FontRenderer.FONT_RENDERER.font.getHeight() * 3.0f + 8.0f,
                        "Memory: "+(runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)+"MB/"+runtime.totalMemory() / (1024 * 1024)+"MB",
                        1.0f
                );
                FontRenderer.FONT_RENDERER.renderTextWithFrame(
                        4.0f,
                        FontRenderer.FONT_RENDERER.font.getHeight() * 4.0f + 10.0f,
                        "Seed: "+_world.getSeed(),
                        1.0f
                );
                FontRenderer.FONT_RENDERER.renderTextWithFrame(
                        4.0f,
                        FontRenderer.FONT_RENDERER.font.getHeight() * 5.0f + 12.0f,
                        "Chunks Loaded: "+_world.chunkCount(),
                        1.0f
                );

                FontRenderer.FONT_RENDERER.draw(_guiMat);

                if (!_focused) {
                    GuiRenderer.renderScreen(_guiMat);
                }

                FontRenderer.FONT_RENDERER.draw(_guiMat);
                break;
        }
        GL33.glEnable(GL33.GL_DEPTH_TEST);
    }

    public void updateFps() {
        _frames++;
        if (Duration.between(_lastFs, Instant.now()).getSeconds() >= 1) {
            _fps = _frames;
            _tps = _ticks;
            _frames = 0;
            _ticks = 0;
            _lastFs = Instant.now();
        }
    }

    private void loop() {
        Runtime runtime = Runtime.getRuntime();

        if(_lastFs == Instant.EPOCH) {
            _lastFs = Instant.now();
        }

        while (!GLFW.glfwWindowShouldClose(_window)) {
            GL33.glClearColor(atmocolor.x, atmocolor.y, atmocolor.z, 1.0f);
            GL33.glClear(GL33.GL_COLOR_BUFFER_BIT | GL33.GL_DEPTH_BUFFER_BIT);

            update();
            renderWorld();
            renderGui(runtime);
            updateFps();

            /*int px = ((int)_camera.getPos().x >> 4);
            int pz = ((int)_camera.getPos().z >> 4);

            for (x in px - 4..px + 4) {
                for (z in pz - 4..pz + 4) {
                    if (!blockgame.world!!.chunkExists(ChunkPosition.getChunkPosition(x, z))) {
                        blockgame.world!!.lazyChunkQueue.offer(ChunkPosition.getChunkPosition(x, z))
                    }
                }
            }

            if (blockgame.world!!.lazyChunkQueue.size > 0) {
                while (blockgame.world!!.lazyChunkQueue.size > 0) {
                    var cPos = blockgame.world!!.lazyChunkQueue.remove()
                    val chunk = blockgame.world!!.addChunk(cPos)
                    blockgame.world!!.generateChunkQueue.offer(chunk)
                }
            }*/

            GLFW.glfwSwapBuffers(_window);
            GLFW.glfwPollEvents();
        }
    }
}
