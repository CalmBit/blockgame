package blockgame.gl;

import blockgame.Logger;
import blockgame.registry.Registry;
import blockgame.registry.RegistryName;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.IOException;

public class TextureManager {
    public static final TextureManager INSTANCE = new TextureManager();

    private Registry<Texture> _registry;

    private TextureManager() {
        _registry = new Registry<>();
    }

    public static Texture getTexture(RegistryName name) {
        if(INSTANCE._registry.get(name) != null) {
            return INSTANCE._registry.get(name);
        }
        try {
            Texture t = new Texture(name, new File("texture", name.getName() + ".png"));
            INSTANCE._registry.register(t);
            return t;
        } catch (Exception e) {
            Logger.LOG.error("Unable to load texture " + name + " - encountered '" + e.getClass() + "'");
            Logger.logStackTrace(Level.ERROR, e);
        }
        return null;
    }


}
