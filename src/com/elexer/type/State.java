package com.elexer.type;

/**
 * Created by lizhuoli on 15/12/16.
 */
public class State {
    public int number = -1; // state number
    public State[] edges;
    public boolean isAccept = false;

//    public int prediction;
//    public PredPrediction[] predicates;

    public static State start = new State(0, true);
    public static State end = new State(-1, true);

    public State() { }

    public State(int number) {
        this.number = number;
    }

    public State(int number, boolean isAccept) {
        this.number = number;
        this.isAccept = isAccept;
    }

    @Override
    public boolean equals(Object o) {
        return true;
//        if ( this==o ) return true;
//
//        if (!(o instanceof State)) {
//            return false;
//        }
//
//        State other = (State)o;
//        boolean sameSet = this.configs.equals(other.configs);
//        return sameSet;
    }

}
