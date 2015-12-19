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
        genLRTable();
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


    public void genLRTable() {
        Set<State> canonical = analyzer.getCanonical();

        for (State state: canonical) {
            for (Production P: state.getClosure()) {
                Symbol left = P.getLeft();
                List<Symbol> symbolList = P.getRight();
                Symbol predict = P.getPredict();
                int dot = P.getDot();

                if (dot < symbolList.size()) { // . is not at the end
                    Symbol symbol = symbolList.get(dot);
                    State nextState = new State(analyzer.gotoClosure(state.getClosure(), symbol));
                    HashMap<Symbol, Action> temp = new HashMap<>();
                    Action action = new Action(nextState);
                    temp.put(symbol, action); // shift j
                    actionTable.put(state, temp);
                } else if (P.dotIsEnd()) { // . is at the end
                    Action action = new Action(P);
                    HashMap<Symbol, Action> temp = new HashMap<>();
                    temp.put(predict, action);
                    actionTable.put(state, temp); // reduce A->B
                } else if (left.isPreStart()) { // is S' -> S
                    HashMap<Symbol, Action> temp = new HashMap<>();
                    temp.put(Symbol.end, Action.accept);
                    actionTable.put(state, temp); // accept -> Action[i, eof]
                }
            }
            for (Symbol nonTerminal: grammar.getProductions().keySet()) {
                State nextState = new State(analyzer.gotoClosure(state.getClosure(), nonTerminal));
                HashMap<Symbol, State> temp = new HashMap<>();
                temp.put(nonTerminal, nextState);
                gotoTable.put(state, temp);
            }
        }
    }
}
