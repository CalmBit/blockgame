package blockgame.util.exception;

import blockgame.util.registry.Registry;
import blockgame.util.registry.RegistryName;

public class DuplicateKeyException extends Exception {
    public DuplicateKeyException(Registry<?> registry, RegistryName reg) {
        super("Duplicate key in "+registry.getClass().getName()+" - '"+reg+"'");
    }
}
