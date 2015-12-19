package com.elexer.type;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lizhuoli on 15/12/16.
 */
public class Production {
    private Symbol left;
    private List<Symbol> right = new ArrayList<>();
    private int dot = 0; // dot pointer for LR table, 0 means N -> .M, sizeOf(M) means N -> M.
    private Symbol predict; // use for LR(1) item predict symbol, must be terminal

    public static Production preStart; // [S' -> .S, #]

    static {
        List<Symbol> list = new ArrayList<>();
        list.add(Symbol.start);
        preStart = new Production(Symbol.preStart, list, Symbol.end);
    }
    public Production() {

    }

    public Production(Symbol left, List<Symbol> right) {
        this.left = left;
        this.right = right;
    }

    public Production(Symbol left, List<Symbol> right, Symbol predict) {
        this.left = left;
        this.right  = right;
        this.setPredict(predict);
    }

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

    private void setDot(int dot) {
        if(dot >= 0 && dot < this.right.size()) {
            this.dot = dot;
        } else {
            System.err.println("LR(1) dot symbol is out of range");
            System.exit(1);
        }
    }

    public int getDot() {
        return this.dot;
    }

    public boolean moveRight() {
        if (this.dot < this.getRight().size()+1) {
            this.dot++;
            return true;
        } else {
            return false;
        }
    }

    public boolean moveLeft() {
        if (this.dot > 0) {
            this.dot--;
            return true;
        } else {
            return false;
        }
    }

    public boolean dotIsEnd() {
        return this.dot == this.right.size();
    }

    public boolean dotIsStart() {
        return this.dot == 0;
    }

    public void setPredict(Symbol symbol) {
        if (symbol.isTerminal()) {
            this.predict = symbol;
        } else {
            System.err.println("LR(1) item predict symbol is not terminal");
            System.exit(1);
        }
    }

    public Symbol getPredict() {
        return this.predict;
    }
}
