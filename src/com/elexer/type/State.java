package com.elexer.type;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by lizhuoli on 15/12/16.
 */
public class State {
    private boolean marked = false;
    private Set<Production> closure;

    public static State start;
    static {
        Set<Production> set = new HashSet<>();
        set.add(Production.preStart);
        start = new State(set);
    }

    public State(Set<Production> closure) {
        this.closure = closure;
    }

    public boolean isStart() {
        return this.closure.equals(start);
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
}
