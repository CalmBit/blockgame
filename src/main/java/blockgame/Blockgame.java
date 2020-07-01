package blockgame;

public class Blockgame {
    public static void main(String[] args) {
        try {
            Window w = new Window();
            w.run();
        } catch (Exception e) {
            Logger.LOG.fatal(e.getMessage());
            for(StackTraceElement el : e.getStackTrace()) {
                Logger.LOG.fatal(el.toString());
            }
        }
    }
}
