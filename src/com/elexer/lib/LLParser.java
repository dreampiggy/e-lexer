package com.elexer.lib;

import com.elexer.type.*;

import java.util.*;

/**
 * Created by lizhuoli on 15/12/17.
 */
public class LLParser implements Parser {
    private Grammar grammar;
    private Analyzer analyzer;
    private Map<Symbol, Map<Symbol, List<Symbol>>> table; // row is non-terminal, column is terminal

    public LLParser(Grammar grammar) {
        this.grammar = grammar;
        this.analyzer = Analyzer.getInstance(grammar);
    }

    public void init() {
        analyzer.analyze();
        genLLTable();
    }

    public boolean parse(List<Symbol> tokens) {
        System.out.println("\nLL(1) Parser start\n");
        Stack<Symbol> stack = new Stack<>();
        int i = 0;
        stack.push(Symbol.start);

        while (!stack.isEmpty() || i != tokens.size()-1) {
            if (stack.isEmpty()) {
                return false; // not a LL(1) grammar
            }
            Symbol top = stack.peek();
            System.out.println("Currernt top: " + top.getValue() + " input: " + tokens.get(i).getValue());
            if (top.isTerminal()) {
                if (stack.peek().equals(tokens.get(i++))) { // include top: # == input: #
                    stack.pop();
                } else {
                    return false; // fail: current token not equal for stack
                }
            } else {
                stack.pop();
                List<Symbol> symbolList = table.get(top).get(tokens.get(i));
                if (symbolList == null) {
                    return false; // not in the table
                }
                for (int j = symbolList.size()-1; j > -1; j--) {
                    if (symbolList.get(j).isEpsilon()) {
                        continue; // ignore the epsilon, which means no symbol
                    }
                    stack.push(symbolList.get(j)); // push reversely symbol in predict table
                }
            }
        }

        return true; // success
    }

    public void genLLTable() {
        // init
        table = new HashMap<>();
        Set<Symbol> nonTerminalSet = grammar.getProductions().keySet();
        nonTerminalSet.forEach(s -> table.put(s, new HashMap<>()));

        for (Symbol row: nonTerminalSet) { // row for non-terminal
            for (Production P: grammar.getProductionList(row)) {
                List<Symbol> symbolList = P.getRight();
                for (Symbol column: analyzer.getSelectSet().get(P)) { // column for terminal, include #
                    if (table.get(row).get(column) != null) {
                        System.err.println("Not a LL(1) grammar ! Parser abort.");
                        System.exit(1);
                    }
                    table.get(row).put(column, symbolList); // define # to terminal. so we add all
                }
            }
        }
    }
}