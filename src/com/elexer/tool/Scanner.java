package com.elexer.tool;

import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * Created by lizhuoli on 15/12/16.
 */
public class Scanner {
    public static final Scanner instance = new Scanner();
    private java.util.Scanner scanner = new java.util.Scanner(new BufferedInputStream(System.in));
    private String line;
    private int linePointer = 0;
    private int lineNumber = 0;

    private Scanner() {}

    public Scanner(InputStream in) {
        this.scanner = new java.util.Scanner(new BufferedInputStream(in));
    }

    public char nextChar() {
        if (line == null) {
            line = replaceSpace(scanner.nextLine());
        } else if (linePointer == line.length()) {
            line = null;
            linePointer = 0;
            lineNumber++;
            return '\n';
        }
        char output = line.charAt(linePointer);
        linePointer++;
        return output;
    }

    public boolean hasNext() {
        if (line != null && linePointer <= line.length()) {
            return true;
        }
        return scanner.hasNextLine();
    }

    public char peek() {
        if (linePointer < line.length()) {
            return line.charAt(linePointer);
        } else {
            return '\n';
        }
    }

    public int getLinePointer() {
        return linePointer;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String replaceSpace(String input) {
        return input.replace(" ", "");
    }
}
