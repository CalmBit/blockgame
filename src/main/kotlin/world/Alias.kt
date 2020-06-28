package world

import block.EnumRenderLayer
import util.FloatList

typealias RenderChunkBatch = Pair<Chunk, Boolean>
typealias BindChunkBatch = Triple<Chunk, MutableList<FloatList>, EnumRenderLayer>
