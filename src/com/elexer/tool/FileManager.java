package com.elexer.tool;

import java.util.logging.Level;

/**
 * Created by lizhuoli on 15/12/18.
 */
public class FileManager {
    private static FileManager instance = null;
    private String rootPath = System.getProperty("user.dir"); // root path for output
    public static FileManager getInstance(String rootPath) {
        if (instance == null) {
            instance = new FileManager(rootPath);
        }
        return instance;
    }

    public FileManager(String rootPath) {
        this.rootPath = rootPath;
    }

    public void append(String text) {

    }

    public void flush() {

    }

    public void writeToFile(String fileName) {

    }
}
