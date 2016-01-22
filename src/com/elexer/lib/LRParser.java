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
        analyzer.canonicalSet();
        genLRTable();
    }

    public boolean parse(List<Symbol> tokens) {
        System.out.println("\nLR(1) Parser start\n");
        Stack<Tuple<Symbol, State>> stack = new Stack<>();
        int i = 0;
        stack.push(new Tuple<>(Symbol.end, State.getStart())); // push <#, start state)

        Symbol input = tokens.get(i);
        while (true) {
            Tuple<Symbol, State> top = stack.peek(); // peek the top
            Symbol symbol = top.first;
            State state = top.second;
            System.out.println("\nCurrernt top: State: " + top.first + "," + top.second +
                    " Input: " + input);
            Action action = actionTable.get(state).get(input); // action[state, input]
            if (action == null) {
                return false; // fail, no item in action table
            }
            System.out.println(action);
            if (action.isReduce()) { // reduce production
                Production production = action.getProduction(); // production like A->B
                Symbol left = production.getLeft();
                List<Symbol> right = production.getRight();

                for(int k = 0; k < right.size(); k++) {
                    stack.pop(); // pop all the reduced symbol
                }
                state = stack.peek().second; // reduce and get target state
                State gotoState = gotoTable.get(state).get(left);

                stack.push(new Tuple<>(left, gotoState)); // push <A, goto[state, A]>
            } else if (action.isShift()) { // shift next state
                State shiftState = action.getState();
                stack.push(new Tuple<>(input, shiftState)); // push <input, state>
                i++;
                if (i > tokens.size() - 1) {
                    return false; // fail, no more input
                }
                input  = tokens.get(i);
            } else if (action.isAccept()) {
                break; // success
            }
        }
        return true;
    }

    /*
    for each cci ∈ CC
        for each item I ∈ cci
            if I is [A→β •cγ,a] and goto(cci ,c) = ccj then Action[i,c] ← 'shift j'
            else if I is [A→β•,a] then Action[i,a] ← 'reduce A→β'
            else if I is [S′→S•,eof] then Action[i,eof] ← 'accept'
        for each n ∈ NT
            if goto(cci ,n) = ccj then
            Goto[i,n] ← j
     */
    public void genLRTable() {
        actionTable = new HashMap<>();
        gotoTable = new HashMap<>();
        Set<State> canonical = analyzer.getCanonical();

        for (State state: canonical) {
            actionTable.put(state, new HashMap<>()); // init for table(use map instead of two dimensional array)
            gotoTable.put(state, new HashMap<>());

            for (Production P: state.getClosure()) {
                Symbol left = P.getLeft();
                List<Symbol> symbolList = P.getRight();
                Symbol predict = P.getPredict();
                int dot = P.getDot();

                if (!P.dotIsEnd() && symbolList.get(dot).isTerminal()) { // . is not at the end
                    Symbol symbol = symbolList.get(dot);
                    State nextState = new State(analyzer.gotoClosure(state.getClosure(), symbol));
                    Action action = new Action(nextState);
                    actionTable.get(state).put(symbol, action); // Action[i,c] <- 'shift j'
                } else if (!symbolList.isEmpty() && !left.isPreStart()) { // . is at the end and has recognize sub string
                    Map<Symbol, Action> item = actionTable.get(state);
                    if (item != null) {
                        System.err.println("Shift reduce conflict at " + item.get(predict).getProduction() + "\nParser abort.");
                        System.exit(1);
                    }
                    Action action = new Action(P);
                    actionTable.get(state).put(predict, action); // Action[i,a] <- 'reduce A->β'
                } else if (predict.isEnd()) { // is [S' -> S.], #
                    actionTable.get(state).put(Symbol.end, Action.accept); // Action[i,eof] <- 'accept'
                }
            }
            for (Symbol nonTerminal: grammar.getProductions().keySet()) {
                Set<Production> closure = analyzer.gotoClosure(state.getClosure(), nonTerminal);
                if (closure == null) continue;
                State nextState = new State(closure);
                gotoTable.get(state).put(nonTerminal, nextState);
            }
        }
    }

    public void printTable() {
        System.out.println("\nAction table:");
        actionTable.forEach((state, item) -> {
            item.forEach((symbol, action) -> {
                if (action.getProduction() != null) {
                    System.out.println("[" + state + "," + symbol + "]: " +
                            (action.isShift() ? "shift " : "") +
                            (action.isReduce() ? "reduce " : "") +
                            (action.isAccept() ? "accept " : "") + action.getProduction());
                }
            });
        });

        System.out.println("\nGoto table:");
        gotoTable.forEach(((state, item) -> {
            item.forEach(((symbol, gotoState) -> {
                System.out.println("[" + state + "," + symbol + "]: " + gotoState);
            }));
        }));

        System.out.println();
    }

    public void log() {
        analyzer.printNullable();
        analyzer.printFirst();
        analyzer.printFollow();
        analyzer.printSelect();
        printTable();
    }
}
