package blockgame.render.gl.texture;

import blockgame.util.Logger;
import blockgame.util.registry.Registry;
import blockgame.util.registry.RegistryName;
import org.apache.logging.log4j.Level;

import java.io.File;

public class TextureManager {

    private static final Registry<Texture> REGISTRY = new Registry<>();

    private TextureManager() {

    }

    public static Texture getTexture(RegistryName name) {
        if(REGISTRY.get(name) != null) {
            return REGISTRY.get(name);
        }
        try {
            Texture t = new Texture(name, new File("texture", name.getName() + ".png"));
            REGISTRY.register(t);
            return t;
        } catch (Exception e) {
            Logger.LOG.error("Unable to load texture " + name + " - encountered '" + e.getClass() + "'");
            Logger.logStackTrace(Level.ERROR, e);
        }
        return null;
    }


}
