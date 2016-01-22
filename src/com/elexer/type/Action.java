package com.elexer.type;

/**
 * Created by lizhuoli on 15/12/18.
 */
public class Action {
    private enum TYPE {
        SHIFT, REDUCE, ACCEPT;
    }
    private TYPE type;
    private Production production;
    private State state;
    private Symbol symbol;

    public static Action accept = new Action(TYPE.ACCEPT, null, null);

    public Action(Production production) {
        this.type = TYPE.REDUCE;
        this.production = production;
    }

    public Action(State state) {
        this.type = TYPE.SHIFT;
        this.state = state;
    }

    public Action(TYPE type, Production production, State state) {
        this.type = TYPE.ACCEPT;
        this.production = production;
        this.state = state;
    }

    public boolean isShift() {
        return this.type == type.SHIFT;
    }

    public boolean isReduce() {
        return this.type == type.REDUCE;
    }

    public boolean isAccept() {
        return this.type == type.ACCEPT;

    }

    public Symbol getSymbol() {
        return symbol;
    }

    public Production getProduction() {
        return production;
    }

    public State getState() {
        return state;
    }

    @Override public String toString() {
        return "Type: " + type.toString() + " State: " + state + " Symbol: " + symbol;
    }
}
