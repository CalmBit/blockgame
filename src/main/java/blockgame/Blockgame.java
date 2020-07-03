package blockgame;

public class Blockgame {
    public static void main(String[] args) {
        try {
            Window w = new Window();
            w.run();
        } catch (Exception e) {
            Logger.LOG.fatal("Caught exception - " + e.getClass().getName());
            StringBuilder build = new StringBuilder();
            for(StackTraceElement el : e.getStackTrace()) {
                build.append(el.toString()).append("\n");
            }
            Logger.LOG.fatal(build.toString());
        }
    }
}
