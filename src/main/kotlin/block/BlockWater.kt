package block

import org.lwjgl.opengl.GL31.*
import registry.RegistryName

class BlockWater : Block(RegistryName("blockgame", "water")) {
    override fun isOpaque(): Boolean {
        return false
    }

    override fun renderLayer(): RenderType {
        return RenderType.TRANSLUCENT
    }

    override fun preRender() {
        glDisable(GL_CULL_FACE)
    }

    override fun postRender() {
        glEnable(GL_CULL_FACE)
    }
}