package world

import block.RenderType
import block.TileState
import gl.ShaderProgram
import world.generators.DefaultGenerator

class Chunk(world: World, val cX: Int, val cZ: Int) {
    private var _regions: Array<Region?> = Array(8) { null }
    companion object {
        private var _generator = DefaultGenerator()
    }

    fun getTileAt(x: Int, y: Int, z: Int): TileState? {
        if (x > 15 || x < 0 || z > 15 || z < 0 || y < 0 || y > 127) return null

        val r = y / 16
        if (_regions.size >= r) {
            return _regions[r]!!.getTileAt(x, y % 16, z)
        }
        return null
    }

    init {
        for (i in 0 until _regions.size) {
            _regions[i] = Region(cX, i, cZ)
        }
    }

    fun generate(world: World) {
        _generator.generate(world,  cX, cZ)
    }

    fun buildRenderData(world: World, prog: ShaderProgram) {
        for (i in 0 until _regions.size) {
            _regions[i]!!.buildRenderData(world, this.cX, this.cZ, prog)
        }
    }

    fun draw(l: RenderType, uniTrans: Int, timer: Float) {
        for (i in 0 until _regions.size) {
            _regions[i]!!.draw(l, uniTrans, timer)
        }
    }

    fun setTileAt(x: Int, y: Int, z: Int, tile: TileState) {
        if (x > 15 || x < 0 || z > 15 || z < 0 || y < 0 || y > 127) {
            return
        }
        val r = y / 16
        if (_regions.size >= r) {
           _regions[r]!!.setTileAt(x, y % 16, z, tile)
        }
    }
}