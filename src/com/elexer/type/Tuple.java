package com.elexer.type;

/**
 * Created by lizhuoli on 15/12/18.
 */
public class Tuple<A, B> {
    public final A first;
    public final B second;

    public Tuple(A a, B b) {
        this.first = a;
        this.second = b;
    }
}