package world

import block.BlockRegistry
import block.EnumRenderLayer
import block.TileState
import gl.ShaderProgram
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import org.lwjgl.opengl.GL31
import java.util.*
import kotlin.random.Random

class World(val window: Long, prog: ShaderProgram) {
    private var _chunks: MutableMap<Pair<Int, Int>, Chunk> = mutableMapOf()
    private var _seed: Int = Random.nextInt(Int.MAX_VALUE)
    var random = Random(_seed)
    val maxX = 8
    val maxZ = 8

    val generateChunkQueue: Queue<Chunk> = ArrayDeque<Chunk>((maxX*2)*(maxZ*2))
    val decorateChunkMutex: Mutex = Mutex()
    val decorateChunkQueue: Queue<Chunk> = ArrayDeque<Chunk>((maxX*2)*(maxZ*2))
    val renderChunkMutex: Mutex = Mutex()
    val renderChunkQueue: Queue<RenderChunkBatch> = ArrayDeque<RenderChunkBatch>((maxX*2)*(maxZ*2))
    val bindChunkQueue: Queue<BindChunkBatch> = ArrayDeque<BindChunkBatch>((maxX*2)*(maxZ*2))
    val lazyChunkQueue: Queue<Pair<Int, Int>> = ArrayDeque<Pair<Int, Int>>((maxX*2)*(maxZ*2))

    val worldType: WorldType = WorldType.DEFAULT

    init {
        for(x in -maxX until maxX) {
            for(z in -maxX until maxZ) {
                addChunk(Pair(x,z))
                generateChunkQueue.offer(_chunks[Pair(x,z)])
            }
        }

        var world = this

        GlobalScope.launch {
            while(true) {
                if (generateChunkQueue.size > 0) {
                    while (generateChunkQueue.size != 0) {
                        var c = generateChunkQueue.remove()
                        if(c.hasGenerated) continue
                        while(!c.isLoaded) {

                        }
                        c.generate(world)
                        decorateChunkMutex.lock()
                        if(world._chunks[Pair(c.cX-1, c.cZ)] != null && !world._chunks[Pair(c.cX-1, c.cZ)]!!.hasDecorated) {
                            decorateChunkQueue.offer(world._chunks[Pair(c.cX-1, c.cZ)])
                        }
                        if(world._chunks[Pair(c.cX, c.cZ-1)] != null && !world._chunks[Pair(c.cX, c.cZ-1)]!!.hasDecorated) {
                            decorateChunkQueue.offer(world._chunks[Pair(c.cX, c.cZ - 1)])
                        }
                        if(world._chunks[Pair(c.cX-1, c.cZ-1)] != null && !world._chunks[Pair(c.cX-1, c.cZ-1)]!!.hasDecorated) {
                            decorateChunkQueue.offer(world._chunks[Pair(c.cX - 1, c.cZ - 1)])
                        }
                        decorateChunkMutex.unlock()
                        renderChunkMutex.lock()
                        renderChunkQueue.offer(RenderChunkBatch(c, true))
                        renderChunkMutex.unlock()
                    }
                }
                delay(50L)
            }
        }

        GlobalScope.launch {
            while(true) {
                if (decorateChunkQueue.size > 0) {
                    while (decorateChunkQueue.size != 0) {
                        decorateChunkMutex.lock()
                        var c: Chunk? = decorateChunkQueue.poll()
                        if(c == null) {
                            decorateChunkMutex.unlock()
                            continue
                        }
                        if(c.hasDecorated) {
                            decorateChunkMutex.unlock()
                            continue
                        }
                        try {
                            if (world._chunks[Pair(c.cX + 1, c.cZ)]!!.hasGenerated
                                && world._chunks[Pair(c.cX, c.cZ + 1)]!!.hasGenerated
                                && world._chunks[Pair(c.cX + 1, c.cZ + 1)]!!.hasGenerated
                            ) {
                                c.decorate(world)
                                renderChunkMutex.lock()
                                renderChunkQueue.offer(RenderChunkBatch(c, true))
                                renderChunkMutex.unlock()
                            } else {
                                decorateChunkQueue.offer(c)
                            }
                            decorateChunkMutex.unlock()
                        } catch(e: java.lang.Exception) {
                            decorateChunkMutex.unlock()
                        }
                    }
                }
                delay(50L)
            }
        }

        GlobalScope.launch {
            while(true) {
                if (renderChunkQueue.size > 0) {
                    var dead: MutableList<RenderChunkBatch> = mutableListOf()
                    while (renderChunkQueue.size != 0) {
                        renderChunkMutex.lock()
                        var (c,r) = renderChunkQueue.remove()
                        for(q in renderChunkQueue) {
                            if(q.first == c) {
                               r = r || q.second
                               dead.add(q)
                            }
                        }

                        for(v in dead) {
                            v.first.dirty = false
                            renderChunkQueue.remove(v)
                        }
                        dead.clear()
                        renderChunkMutex.unlock()


                        for (l in EnumRenderLayer.VALUES) {
                            bindChunkQueue.offer(BindChunkBatch(c, c.buildRenderData(world, l), l))
                        }
                        if(r) {
                            for(x in -1..1) {
                                for(z in -1..1) {
                                    if(x == 0 && z == 0) continue
                                    if(world._chunks.containsKey(Pair(c.cX+x, c.cZ+z)) && !c.dirty) {
                                        renderChunkMutex.lock()
                                        renderChunkQueue.offer(RenderChunkBatch(world._chunks[Pair(c.cX+x, c.cZ+z)]!!, false))
                                        renderChunkMutex.unlock()
                                    }
                                }
                            }
                        }
                    }
                }
                delay(50L)
            }
        }
    }

    fun draw(uniTrans: Int, timer: Float) {
        _chunks.forEach { (_, c) ->
            c.draw(EnumRenderLayer.NORMAL, uniTrans, timer)
        }
        _chunks.forEach { (_, c) ->
            c.draw(EnumRenderLayer.TRANSLUCENT, uniTrans, timer)
        }
    }

    fun getTileAtAdjusted(cX: Int, cZ: Int, x: Int, y: Int, z: Int): TileState? {
        var tX = adjustChunk(cX, x)
        var tZ = adjustChunk(cZ, z)
        return getTileAt(tX, y, tZ)
    }

    fun getTileAt(x: Int, y: Int, z: Int): TileState? {
        var cPos = Pair(x shr 4, z shr 4)

        if(_chunks.containsKey(cPos)) {
            var c = _chunks[cPos]
            return c?.getTileAt(x and 15, y, z and 15)
        }
        return null
    }

    fun getSeed(): Int {
        return _seed
    }

    private fun setTileAt(x: Int, y: Int, z: Int, tile: Int) {
        var cPos = Pair(x shr 4, z shr 4)
        if(_chunks.containsKey(cPos)) {
            _chunks[cPos]?.setTileAt(x and 15, y,z and 15, tile)
        } else {
            System.out.println("Missing Chunk! X="+ x + " Y=" +y + " Z=" + z + " cpos=" + cPos)
           // _chunks[cPos] = Chunk(this, cPos.first, cPos.second)
           // _chunks[cPos]?.setTileAt(x and 15, y,z and 15, tile)
          //  generateChunkQueue.offer(_chunks[cPos])
        }
    }

    fun setTileAtAdjusted(cX: Int, cZ: Int, x: Int, y: Int, z: Int, tileRepresentation: Int) {
        var tX = adjustChunk(cX, x)
        var tZ = adjustChunk(cZ, z)
        setTileAt(tX, y, tZ, tileRepresentation)
    }

    fun getTopTilePos(x: Int, z: Int): Int {
        var cPos = Pair(x shr 4, z shr 4)
        if(_chunks.containsKey(cPos)) {
            var y = 127
            while(_chunks[cPos]!!.getTileAt(x and 15, y,z and 15)!!.block == BlockRegistry.AIR)
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

    fun adjustChunk(chunk: Int, position: Int): Int {
        if(position > 15) {
            return (chunk shl 4) + position
        }
        return (chunk shl 4) + position
    }

    fun chunkExists(cPos: Pair<Int, Int>): Boolean {
        return _chunks.containsKey(cPos)
    }

    fun addChunk(cPos: Pair<Int, Int>): Chunk {
        val c = Chunk(this, cPos.first, cPos.second)
        c.isLoaded = true
        _chunks[cPos] = c
        return c
    }

    fun tick() {
        _chunks.forEach { (_, c) ->
            c.tick(this)
        }
    }

    fun chunkCount(): Int {
        return _chunks.size
    }
}