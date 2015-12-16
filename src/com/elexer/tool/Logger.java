package com.elexer.tool;

import java.util.logging.Level;

/**
 * Created by lizhuoli on 15/12/16.
 */
public class Logger {
    private static Logger instance = null;
    public static Logger getInstance(Level level) {
        if (instance == null) {
            instance = new Logger(level);
        }
        return instance;
    }
    private java.util.logging.Logger logger;
    private Scanner scanner = Scanner.getInstance(Config.input);

    private Logger(Level level) {
        logger = java.util.logging.Logger.getLogger("elexer");
        logger.setLevel(level);
    }

    public void log(String msg) {
        logger.info(msg);
    }

    public void parseError(String msg) {
        logger.severe("parsing \"" + msg + "\"\n at line: " + scanner.getLineNumber()
                + "\n at char" + scanner.getLinePointer());
        System.exit(1);
    }

    public void parseWarning(String msg) {
        logger.warning("parsing \"" + msg + "\"n at line " + scanner.getLineNumber()
                + "\n at char" + scanner.getLinePointer());
    }
}
