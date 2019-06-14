package block

import gl.UVPair
import registry.IRegistryEntry
import registry.RegistryName
import world.World

open class Block(private var _name: RegistryName) : IRegistryEntry {
    var uv: UVPair? = null
    override fun getRegistryName() = _name

    override fun setRegistryName(name: RegistryName) {
        _name = name
    }

    open fun getUVForFace(face: EnumDirection): UVPair {
        return uv ?: UVPair.MISSING_UV
    }

    open fun setUV(uv: UVPair): Block {
        this.uv = uv
        return this
    }

    open fun isOpaque(): Boolean {
        return true
    }

    open fun shouldRenderFace(
        world: World,
        cX: Int,
        cZ: Int,
        rY: Int,
        x: Int,
        y: Int,
        z: Int,
        face: EnumDirection
    ): Boolean {
        var adj = face.getCovering(Triple(world.adjustChunk(cX, x),y+(16*rY),world.adjustChunk(cZ, z)))
        var other = world.getTileAt(adj.first, adj.second, adj.third)
        if(face == EnumDirection.UP && y+(16*rY) == 127)
            return true
        if(other != null) {
            return !other.shouldRender() || (!other.isOpaque() && this.isOpaque())
        }
        return false
    }

    open fun shouldRender(): Boolean = true

    open fun renderLayer(): RenderType {
        return RenderType.NORMAL
    }

    open fun preRender() {

    }

    open fun postRender() {

    }
}