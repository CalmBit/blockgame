package block

class TileState(var block: Block) {
    fun getUVForFace(face: EnumDirection): UVPair = block.getUVForFace(face)
    fun shouldRender(): Boolean = block.shouldRender()
}