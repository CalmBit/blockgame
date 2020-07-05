package blockgame;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

public class Logger {
    public static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger("blockgame");

    public static void logStackTrace(Level l, Exception e) {
        StringBuilder build = new StringBuilder();
        for(StackTraceElement el : e.getStackTrace()) {
            build.append(el.toString()).append("\n");
        }
        Logger.LOG.log(l, build.toString());
    }
}
