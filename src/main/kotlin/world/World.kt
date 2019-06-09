package world

import block.BlockRegistration
import block.RenderType
import block.TileState
import gl.ShaderProgram
import kotlin.random.Random

class World {
    private var _chunks: MutableMap<Pair<Int, Int>, Chunk> = mutableMapOf()
    private var _seed: Int = Random.nextInt(Int.MAX_VALUE)
    var random = Random(_seed)

    init {
        for(x in 0..24) {
            for(z in 0..24) {
                addChunk(Pair(x,z))
            }
        }
    }

    private fun addChunk(pos: Pair<Int, Int>) {
        _chunks[pos] = Chunk(this, pos.first, pos.second)
        _chunks[pos]!!.generate(this)
    }

    fun rebuildAllChunks(prog: ShaderProgram) {
        _chunks.forEach { (_, c) ->
            c.buildRenderData(this ,prog)
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
            return c!!.getTileAt(Math.abs(x%16),y,Math.abs(z%16))
        }
        return null
    }

    fun getSeed(): Int {
        return _seed
    }

    fun setTileAt(x: Int, y: Int, z: Int, tile: TileState) {
        var cPos = Pair((x / 16) - (if (x < 0) 1 else 0),(z / 16) - (if (z < 0) 1 else 0))
        if(_chunks.containsKey(cPos)) {
            _chunks[cPos]!!.setTileAt(Math.abs(x%16),y,Math.abs(z%16), tile)
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