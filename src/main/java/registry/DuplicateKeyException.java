package registry;

public class DuplicateKeyException extends Exception {
    public DuplicateKeyException(Registry<?> registry, RegistryName reg) {
        super("Duplicate key in "+registry.getClass().getName()+" - '"+reg+"'");
    }
}
