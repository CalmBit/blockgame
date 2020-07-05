package blockgame;

import org.apache.logging.log4j.Level;

public class Blockgame {
    public static void main(String[] args) {
        try {
            Window w = new Window();
            w.run();
        } catch (Exception e) {
            Logger.LOG.fatal("Caught exception - " + e.getClass().getName());
            Logger.LOG.fatal("Message: " + e.getMessage());
            Logger.logStackTrace(Level.FATAL, e);
        }
    }
}
