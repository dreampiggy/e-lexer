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
    public static Symbol preStart = new Symbol("S{*}", false);

    public Symbol(String value, boolean isTerminal) {
        this.value = value;
        this.isTerminal = isTerminal;
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Symbol) {
            Symbol other = (Symbol)o;
            return other.value.equals(this.value) && other.isTerminal == this.isTerminal;
        }
        return false;
    }

    public boolean isTerminal() {
        return this.isTerminal;
    }

    public boolean isEpsilon() {
        return this.equals(epsilon);
    }

    public boolean isStart() {
        return this.equals(start);
    }

    public boolean isEnd() {
        return this.equals(end);
    }

    public boolean isPreStart() {
        return this.equals(preStart);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override public String toString() {
        return this.value;
    }
}