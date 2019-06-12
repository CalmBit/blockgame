package world

import block.BlockRegistration
import block.RenderType
import block.TileState
import gl.ShaderProgram
import kotlinx.coroutines.GlobalScope
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

    val renderChunkQueue: Queue<Chunk> = ArrayDeque<Chunk>(maxX*maxZ)
    val bindChunkQueue: Queue<BindChunkBatch> = ArrayDeque<BindChunkBatch>(maxX*maxZ)

    init {
        for(x in 0 until maxX) {
            for(z in 0 until maxZ) {
                _chunks[Pair(x,z)] = Chunk(this, x, z)
                _chunks[Pair(x,z)]!!.generate(this)
                renderChunkQueue.offer(_chunks[Pair(x,z)]!!)
            }
        }

        var world = this
        GlobalScope.launch {
            while(renderChunkQueue.size != 0) {
                var c = renderChunkQueue.remove()
                for(l in RenderType.values) {
                    bindChunkQueue.offer(BindChunkBatch(c, c.buildRenderData(world, l), l))
                }
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