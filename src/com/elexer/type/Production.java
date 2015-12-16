package com.elexer.type;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lizhuoli on 15/12/16.
 */
public class Production {
    private Symbol left;
    private List<Symbol> right = new ArrayList<>();

    public Symbol getLeft() {
        return left;
    }

    public void setLeft(Symbol left) {
        this.left = left;
    }

    public List<Symbol> getRight() {
        return right;
    }

    public void addRight(Symbol right) {
        this.right.add(right);
    }
}
