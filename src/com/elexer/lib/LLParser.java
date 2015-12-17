package com.elexer.lib;

import com.elexer.type.Grammar;
import com.elexer.type.Production;
import com.elexer.type.Symbol;

import java.util.*;

/**
 * Created by lizhuoli on 15/12/17.
 */
public class LLParser {
    private Grammar grammar;

    private Map<Symbol, Set<Symbol>> follow = new HashMap<>();
    private Map<Symbol, Set<Symbol>> first = new HashMap<>();
    private Set<Symbol> nullable = new HashSet<>();

    public LLParser(Grammar grammar) {
        this.grammar = grammar;
    }

    public void nullableSet() {
        Set<Symbol> copy = new HashSet<>();
        do {
            copy.addAll(nullable);
            List<Production> productionList = grammar.getAllProductionList();
            for(Production p: productionList) {
                Symbol S = p.getLeft();
                List<Symbol> symbolList = p.getRight();
                if (symbolList.size() == 1 && symbolList.get(0).isEpsilon()) {
                    nullable.add(S);
                }
                boolean allNonterminalFlag = true;
                for (Symbol symbol: symbolList) {
                    if (symbol.isTerminal()) {
                        allNonterminalFlag = false;
                        break;
                    }
                }
                if (allNonterminalFlag) {
                    boolean allInNullableFlag = true;
                    for(Symbol symbol: symbolList) {
                        if (!nullable.contains(symbol)) {
                            allInNullableFlag = false;
                            break;
                        }
                    }
                    if (allInNullableFlag) {
                        nullable.add(S);
                    }
                }
            }
        } while (!copy.containsAll(nullable));
    }



    public Set<Symbol> firstSet(Symbol S) {
        // if S -> ε, then add ε to first(S)
        // if S -> a, then add a to first(S)
        Set<Symbol> set = new HashSet<>();
        if (S.isTerminal()) { // if S is terminal, add itself
            set.add(S);
            return set;
        }
        Grammar.getGrammar().getProductionList(S).forEach(p -> { // all the right production for S
            List<Symbol> symbolList = p.getRight(); // all the symbol in a production
            for (Symbol symbol : symbolList) {
                if (symbol.isTerminal()) { // we define the epsilon to terminal so we can add together
                    set.add(symbol);
                } else { // if S -> ABC, use the following algorithm to do
                    int j = 0;
                    int k = symbolList.size();
                    while (j < k) { // first calculate all the first(A) and cup equal(except epsilon) to first(S)
                        Set<Symbol> tempSet = firstSet(symbolList.get(j));
                        if (!tempSet.contains(Symbol.epsilon)) {
                            break;
                        }
                        tempSet.remove(Symbol.epsilon); // except epsilon
                        set.addAll(tempSet);
                        j++;
                    }
                    // if all the first(A) contains epsilon, then cup eual epsilon to first(S)
                    if (j == k && firstSet(symbolList.get(k-1)).contains(Symbol.epsilon)) {
                        set.add(Symbol.epsilon);
                    }
                }
            }
        });
        return set;
    }

    public void firstSet() {
        grammar.getProductions().keySet().forEach(S -> {
            first.put(S, firstSet(S));
        });
    }

    public void follow() {
        // if S -> aA, then add follow(S) except epsilon to follow(A)
        // if S -> aAb, b != epsilon, add first(b) except epsilon to follow(A)
        // if S -> aAb, b contains epsilon, add follow(S) to follow(A)
        // init follow set
        Grammar grammar = Grammar.getGrammar();
        Map<Symbol ,List<Production>> productions = grammar.getProductions();
        Set<Symbol> keySet = productions.keySet();

        keySet.forEach(S -> follow.put(S, new HashSet<>()));

        follow.get(Symbol.start).add(Symbol.end); // put the # into follow(Start)
    }


//    public Set<Symbol> followSet(Symbol S) {
//        // if S -> aA, then add follow(S) except epsilon to follow(A)
//        // if S -> aAb, b != epsilon, add first(b) except epsilon to follow(A)
//        // if S -> aAb, b contains epsilon, add follow(S) to follow(A)
//        Set<Symbol> set = new HashSet<>();
//        Grammar grammar = Grammar.getGrammar();
//        grammar.getAllProductionList().forEach(p -> {
//            if (p.getLeft().isStart()) {
//                set.add(Symbol.start); // first put the # into follow(Start)
//            }
//        });
//
//        for (Production production : grammar.getProductionList(S)) {
//            List<Symbol> symbolList = production.getRight();
//            int size = symbolList.size();
//            if (size < 2 || !symbolList.get(0).isTerminal() || symbolList.get(1).isTerminal()) {
//                return set; // should 2 symbol and look like aA*(capital for non-terminal)
//            }
//            if (size == 2) {
//                set.addAll(followSet(symbolList.get(1)));
//            }
//
//
//        }
//
//        return set;
//    }


    public void followSet() {
        // if S -> aA, then add follow(S) except epsilon to follow(A)
        // if S -> aAb, b != epsilon, add first(b) except epsilon to follow(A)
        // if S -> aAb, b contains epsilon, add follow(S) to follow(A)
        // init follow set
        Map<Symbol ,List<Production>> productions = grammar.getProductions();
        Set<Symbol> keySet = productions.keySet();

        keySet.forEach(S -> follow.put(S, new HashSet<>()));

        follow.get(Symbol.start).add(Symbol.end); // put the # into follow(Start)
        productions.forEach((key, value) -> {
            value.forEach(p -> {
                Symbol S = p.getLeft();
                List<Symbol> symbolList = p.getRight();
                int size = symbolList.size();

                if (size > 1 && symbolList.get(0).isTerminal() && !symbolList.get(1).isTerminal()) {
                    if (size == 2) {
                        follow.get(symbolList.get(1)).addAll(follow.get(S));
                    } else if (!symbolList.get(2).isEpsilon()) {
                        Set<Symbol> temp = first.get(symbolList.get(2));
                        temp.remove(Symbol.epsilon); // except epsilon
                        follow.get(symbolList.get(1)).addAll(temp);
                    } else if (first.get(symbolList.get(2)).contains(Symbol.epsilon)) {
                        follow.get(symbolList.get(1)).addAll(follow.get(S));
                    }
                }
            });
        });
    }

    public void printNullable() {
        System.out.println("\nPrint Nullable set\n");
        nullable.forEach(s -> {
            System.out.println(s.getValue() + ", ");
        });
    }

    public void printFirst() {
        System.out.println("\nPrint First set\n");
        first.forEach((k, v) -> {
            System.out.println("First(" + k.getValue() + "): ");
            v.forEach(s -> System.out.print(s.getValue() + ", "));
            System.out.println("");
        });
    }

    public void printFollow() {
        System.out.println("\nPrint Follow set\n");
        follow.forEach((k, v) -> {
            System.out.println("Follow(" + k.getValue() + "): ");
            v.forEach(s -> System.out.print(s.getValue() + ", "));
            System.out.println("");
        });
    }
}
