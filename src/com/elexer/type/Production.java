package com.elexer.type;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lizhuoli on 15/12/16.
 */
public class Production {
    private Symbol left;
    private List<Symbol> right = new ArrayList<>();
    private int dot = 0; // dot pointer for LR table

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

    public int getDot() {
        return this.dot;
    }

    public void moveRight() {
        if (this.dot < this.getRight().size()-1) {
            this.dot++;
        }
    }

    public void moveLeft() {
        if (this.dot > 0) {
            this.dot--;
        }
    }
}
