package world

import block.RenderType

typealias RenderChunkBatch = Pair<Chunk, Boolean>
typealias BindChunkBatch = Triple<Chunk, MutableList<MutableList<Float>>, RenderType>
