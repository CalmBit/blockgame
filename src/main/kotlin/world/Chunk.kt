package world

import block.RenderType
import block.TileState
import gl.ShaderProgram
import kotlinx.coroutines.runBlocking
import world.generators.DefaultGenerator
import world.generators.HellGenerator
import world.generators.SkyGenerator
import kotlin.random.Random

class Chunk(val world: World, val cX: Int, val cZ: Int) {
    private var _regions: Array<Region?> = Array(8) { null }
    var hasGenerated = false
    var hasDecorated = false


    fun getTileAt(x: Int, y: Int, z: Int): TileState? {
        if (x > 15 || x < 0 || z > 15 || z < 0 || y < 0 || y > 127) return null

        val r = y shr 4
        if (_regions.size >= r) {
            return _regions[r]!!.getTileAt(x, y and 15, z)
        }
        return null
    }

    init {
        for (i in 0 until _regions.size) {
            _regions[i] = Region(cX, i, cZ)
        }
    }

    fun generate(world: World) {
        world.worldType.gen.generate(world,  cX, cZ)
        hasGenerated = true
    }

    fun decorate(world: World) {
        world.worldType.gen.decorate(world, cX, cZ)
        hasDecorated = true
    }

    fun buildRenderData(world: World, l: RenderType): MutableList<MutableList<Float>> {
        val verts = mutableListOf<MutableList<Float>>()
        for (i in 0 until _regions.size) {
            val v = _regions[i]!!.buildRenderData(world, this.cX, this.cZ, l)
            verts.add(v)
        }
        return verts
    }

    fun draw(l: RenderType, uniTrans: Int, timer: Float) {
        for (i in 0 until _regions.size) {
            _regions[i]!!.draw(l, uniTrans, timer)
        }
    }

    fun setTileAt(x: Int, y: Int, z: Int, tile: Int) {
        if (x > 15 || x < 0 || z > 15 || z < 0 || y < 0 || y > 127) {
            return
        }
        val r = y shr 4
        if (_regions.size >= r) {
           _regions[r]!!.setTileAt(x, y and 15, z, tile)
        }
        if(this.hasGenerated) {
            val c = this
            runBlocking {
                world.renderChunkMutex.lock()
                world.renderChunkQueue.offer(RenderChunkBatch(c, false))
                world.renderChunkMutex.unlock()
            }
        }
    }

    fun bindRenderData(verts: MutableList<MutableList<Float>>, type: RenderType, prog: ShaderProgram) {
        for (i in 0 until _regions.size) {
            _regions[i]!!.bindData(verts[i], type, prog)
        }
    }

    fun tick(world: World) {
        for (i in 0 until _regions.size) {
            _regions[i]!!.tick(world)
        }
    }
}