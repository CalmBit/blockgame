package block

import registry.RegistryName

class BlockMonsterSpawner: Block(RegistryName("blockgame","monster_spawner")) {
    override fun isOpaque(): Boolean {
        return false
    }
}
