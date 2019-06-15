package block

import gl.UVPair

class TileState(var block: Block) {
    fun getUVForFace(face: EnumDirection): UVPair = block.getUVForFace(face)
    fun shouldRender(): Boolean = block.shouldRender()
    fun isOpaque(): Boolean = block.isOpaque()
    fun renderLayer(): RenderType = block.renderLayer()
    override fun equals(other: Any?): Boolean {
        if(other is TileState) {
            return this.block == other.block
        }
        return false
    }

    fun getEmittance(): Float = block.getEmittance()
}