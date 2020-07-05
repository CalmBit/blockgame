package blockgame.block;

import blockgame.util.registry.RegistryName;

public class BlockMonsterSpawner extends Block {
    public BlockMonsterSpawner() {
        super(new RegistryName("blockgame", "monster_spawner"));
    }

    @Override
    public boolean isOpaque() {
        return false;
    }
}
