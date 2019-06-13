package world

import block.BlockRegistration
import block.RenderType
import block.TileState
import gl.ShaderProgram
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL
import java.util.*
import kotlin.random.Random

class World(val window: Long, prog: ShaderProgram) {
    private var _chunks: MutableMap<Pair<Int, Int>, Chunk> = mutableMapOf()
    private var _seed: Int = Random.nextInt(Int.MAX_VALUE)
    var random = Random(_seed)
    val maxX = 16
    val maxZ = 16

    val generateChunkQueue: Queue<Chunk> = ArrayDeque<Chunk>(maxX*maxZ)
    val renderChunkQueue: Queue<RenderChunkBatch> = ArrayDeque<RenderChunkBatch>(maxX*maxZ)
    val bindChunkQueue: Queue<BindChunkBatch> = ArrayDeque<BindChunkBatch>(maxX*maxZ)

    init {
        for(x in 0 until maxX) {
            for(z in 0 until maxZ) {
                _chunks[Pair(x,z)] = Chunk(this, x, z)
                generateChunkQueue.offer(_chunks[Pair(x,z)])
            }
        }

        var world = this

        GlobalScope.launch {
            while(true) {
                if (generateChunkQueue.size > 0) {
                    while (generateChunkQueue.size != 0) {
                        var c = generateChunkQueue.remove()
                        c.generate(world)
                        renderChunkQueue.offer(RenderChunkBatch(c, true))
                    }
                }
                delay(250L)
            }
        }

        GlobalScope.launch {
            while(true) {
                if (renderChunkQueue.size > 0) {
                    while (renderChunkQueue.size != 0) {
                        var (c,r) = renderChunkQueue.remove()
                        for (l in RenderType.values) {
                            bindChunkQueue.offer(BindChunkBatch(c, c.buildRenderData(world, l), l))
                        }
                        if(r) {
                            for(x in -1..1) {
                                for(z in -1..1) {
                                    if(x == 0 && z == 0) continue
                                    if(world._chunks.containsKey(Pair(c.cX+x, c.cZ+z))) {
                                        renderChunkQueue.offer(RenderChunkBatch(world._chunks[Pair(c.cX+x, c.cZ+z)]!!, false))
                                    }
                                }
                            }
                        }
                    }
                }
                delay(250L)
            }
        }
    }

    fun draw(uniTrans: Int, timer: Float) {
        _chunks.forEach { (_, c) ->
            c.draw(RenderType.NORMAL, uniTrans, timer)
        }
        _chunks.forEach { (_, c) ->
            c.draw(RenderType.TRANSLUCENT, uniTrans, timer)
        }
    }

    fun getTileAt(x: Int, y: Int, z: Int): TileState? {
        var cPos = Pair((x / 16) - (if (x < 0) 1 else 0),(z / 16) - (if (z < 0) 1 else 0))

        if(_chunks.containsKey(cPos)) {
            var c = _chunks[cPos]
            return c?.getTileAt(Math.abs(x%16),y,Math.abs(z%16))
        }
        return null
    }

    fun getSeed(): Int {
        return _seed
    }

    fun setTileAt(x: Int, y: Int, z: Int, tile: TileState) {
        var cPos = Pair((x / 16) - (if (x < 0) 1 else 0),(z / 16) - (if (z < 0) 1 else 0))
        if(_chunks.containsKey(cPos)) {
            _chunks[cPos]?.setTileAt(Math.abs(x%16),y,Math.abs(z%16), tile)
        }
    }

    fun getTopTilePos(x: Int, z: Int): Int {
        var cPos = Pair((x / 16) - (if (x < 0) 1 else 0),(z / 16) - (if (z < 0) 1 else 0))
        if(_chunks.containsKey(cPos)) {
            var y = 127
            while(_chunks[cPos]!!.getTileAt(Math.abs(x%16),y,Math.abs(z%16))!!.block == BlockRegistration.AIR)
            {
                y--
            }
            return y
        }
        throw Exception("chunk not found")
    }
}