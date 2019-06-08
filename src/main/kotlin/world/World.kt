package world

import block.TileState
import gl.ShaderProgram

class World {
    private var _chunks: MutableMap<Pair<Int, Int>, Chunk> = mutableMapOf()

    init {
        for(x in 0..16) {
            for(z in 0..16) {
                addChunk(Pair(x,z))
            }
        }
    }

    private fun addChunk(pos: Pair<Int, Int>) {
        _chunks[pos] = Chunk(pos.first, pos.second)
    }

    fun rebuildAllChunks(prog: ShaderProgram) {
        _chunks.forEach { (_, c) ->
            c.buildRenderData(this ,prog)
        }
    }

    fun draw(uniTrans: Int, timer: Float) {
        _chunks.forEach { (_, c) ->
            c.draw(uniTrans, timer)
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
}