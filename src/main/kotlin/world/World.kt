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
    val maxX = 8
    val maxZ = 8

    val generateChunkQueue: Queue<Chunk> = ArrayDeque<Chunk>((maxX*2)*(maxZ*2))
    val renderChunkQueue: Queue<RenderChunkBatch> = ArrayDeque<RenderChunkBatch>((maxX*2)*(maxZ*2))
    val bindChunkQueue: Queue<BindChunkBatch> = ArrayDeque<BindChunkBatch>((maxX*2)*(maxZ*2))

    init {
        for(x in -8 until maxX) {
            for(z in -8 until maxZ) {
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

    fun getTileAtAdjusted(cX: Int, cZ: Int, x: Int, y: Int, z: Int): TileState? {
        var tX = adjustChunk(cX, x)
        var tZ = adjustChunk(cZ, z)
        return getTileAt(tX, y, tZ)
    }

    fun getTileAt(x: Int, y: Int, z: Int): TileState? {
        var cPos = Pair(Math.floor(x / 16.0).toInt(),Math.floor(z / 16.0).toInt())

        if(_chunks.containsKey(cPos)) {
            var c = _chunks[cPos]
            return c?.getTileAt(if(x < 0) (x%16)+15 else (x%16),y,if(z < 0) (z%16)+15 else (z%16))
        }
        return null
    }

    fun getSeed(): Int {
        return _seed
    }

    private fun setTileAt(x: Int, y: Int, z: Int, tile: Int) {
        var cPos = Pair(Math.floor(x / 16.0).toInt(),Math.floor(z / 16.0).toInt())
        if(_chunks.containsKey(cPos)) {
            _chunks[cPos]?.setTileAt(if(x < 0) (x%16)+15 else (x%16),y,if(z < 0) (z%16)+15 else (z%16), tile)
        }
    }

    fun setTileAtAdjusted(cX: Int, cZ: Int, x: Int, y: Int, z: Int, tileRepresentation: Int) {
        var tX = adjustChunk(cX, x)
        var tZ = adjustChunk(cZ, z)
        setTileAt(tX, y, tZ, tileRepresentation)
    }

    fun getTopTilePos(x: Int, z: Int): Int {
        var cPos = Pair(Math.floor(x / 16.0).toInt(),Math.floor(z / 16.0).toInt())
        if(_chunks.containsKey(cPos)) {
            var y = 127
            while(_chunks[cPos]!!.getTileAt(if(x < 0) (x%16)+15 else (x%16),y,if(z < 0) (z%16)+15 else (z%16))!!.block == BlockRegistration.AIR)
            {
                y--
            }
            return y
        }
        throw Exception("chunk not found")
    }

    fun getTopTilePosAdjusted(cX: Int, cZ: Int, x: Int, z: Int): Int {
        var tX = adjustChunk(cX, x)
        var tZ = adjustChunk(cZ, z)
        return getTopTilePos(tX, tZ)
    }

    fun adjustChunk(c: Int, v: Int): Int {
        return if(c < 0) (((c*16)+1) + (15-v)) else (c*16) + v
    }

}