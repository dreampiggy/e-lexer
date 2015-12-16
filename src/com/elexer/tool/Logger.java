package com.elexer.tool;

/**
 * Created by lizhuoli on 15/12/16.
 */
public class Logger {
    public static final Logger instance = new Logger();
    private java.util.logging.Logger logger = java.util.logging.Logger.getLogger("elexer");
    private Scanner scanner = Scanner.instance;

    private Logger() {}

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
