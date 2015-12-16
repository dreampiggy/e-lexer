package com.elexer.type;

/**
 * Created by lizhuoli on 15/12/16.
 */
public class Symbol {
    private String value;
    private boolean isTerminal;

    public Symbol(String value, boolean isTerminal) {
        this.value = value;
        this.isTerminal = isTerminal;
    }

    public boolean isTerminal() {
        return this.isTerminal;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}