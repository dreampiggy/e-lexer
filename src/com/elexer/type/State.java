package com.elexer.type;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by lizhuoli on 15/12/16.
 */
public class State {
    private boolean marked = false;
    private Set<Production> closure;
    private static State start;

    public State(Set<Production> closure) {
        this.closure = closure;
    }

    public static void setStart(State start) {
        State.start = start;
    }

    public static State getStart() {
        return State.start;
    }

    public boolean isMarked() {
        return marked;
    }

    public void mark() {
        this.marked = true;
    }

    public Set<Production> getClosure() {
        return closure;
    }

    public void setSet(Set<Production> closure) {
        this.closure = closure;
    }

    @Override public String toString() {
        return String.valueOf(this.closure);
    }

    @Override
    public int hashCode() {
        return this.closure.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof State) {
            return ((State) o).closure.equals(this.closure);
        }
        return false;
    }
}
