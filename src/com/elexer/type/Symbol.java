package com.elexer.type;

import java.util.Objects;

/**
 * Created by lizhuoli on 15/12/16.
 */
public class Symbol {
    private String value;
    private boolean isTerminal;
    public static Symbol epsilon = new Symbol("ε", true);
    public static Symbol start = new Symbol("S", false);
    public static Symbol end = new Symbol("#", true);

    public Symbol(String value, boolean isTerminal) {
        this.value = value;
        this.isTerminal = isTerminal;
    }

    @Override
    public int hashCode() {
        return this.value.hashCode() + (this.isTerminal() ? 1 : 0);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Symbol) {
            return ((Symbol) o).value.equals(this.value);
        }
        return false;
    }

    public boolean isTerminal() {
        return this.isTerminal;
    }

    public boolean isEpsilon() {
        return this.value.equals("ε");
    }

    public boolean isStart() {
        return this.value.equals("S");
    }

    public boolean isEnd() {
        return this.value.equals("#");
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}