package com.elexer.lib;

import com.elexer.type.*;

import java.util.*;

/**
 * Created by lizhuoli on 15/12/18.
 */
public class LRParser implements Parser {
    private Grammar grammar;
    private Analyzer analyzer;
    private Map<State, Map<Symbol, Action>> actionTable;
    private Map<State, Map<Symbol, State>> gotoTable;

    public LRParser(Grammar grammar) {
        this.grammar = grammar;
        this.analyzer = Analyzer.getInstance(grammar);
    }

    public void init() {
        analyzer.analyze();
        genActionTable();
        genGotoTable();
    }

    public boolean parse(List<Symbol> tokens) {
        System.out.println("\nLR(1) Parser start\n");
        Stack<Tuple<Symbol, State>> stack = new Stack<>();
        int i = 0;
        stack.push(new Tuple<>(Symbol.end, State.start)); // push <#, start state)

        Symbol input = tokens.get(i);
        while (true) {
            Tuple<Symbol, State> top = stack.peek(); // peek the top
            Symbol symbol = top.first;
            State state = top.second;

            Action action = actionTable.get(state).get(input); // action[state, input]
            if (action == null) {
                return false; // fail, no item in action table
            }
            else if (action.isReduce()) { // reduce production
                Production production = action.getProduction(); // production like A->B
                Symbol left = production.getLeft();
                List<Symbol> right = production.getRight();

                for(int k = 0; k < right.size(); k++) {
                    stack.pop(); // pop all the reduced symbol
                }
                State gotoState = gotoTable.get(state).get(left);
                stack.push(new Tuple<>(left, gotoState)); // push <A, goto[state, A]>

            } else if (action.isShift()) { // shift next state
                State shiftState = action.getState();
                stack.push(new Tuple<>(input, shiftState)); // push <input, state>
                i++;
                if (i == tokens.size()-1) {
                    return false; // fail, no more input
                }
                input  = tokens.get(i);

            } else if (action.isAccept()) {
                break; // success
            }
        }
        return true;
    }


    public void genGotoTable() {

    }

    public void genActionTable() {

    }

}
