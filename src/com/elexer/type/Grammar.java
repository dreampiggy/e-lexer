package com.elexer.type;

import java.util.*;

/**
 * Created by lizhuoli on 15/12/16.
 */
public class Grammar {
    private static Map<String, List<Production>> productions = new HashMap<>();
    private static Grammar grammar;

    public Map<String, List<Production>> getProductions() {
        return productions;
    }

    public void addProductions(String key, Production value) {
        if (this.productions.get(key) != null) {
            this.productions.get(key).add(value);
        } else {
            List<Production> temp = new ArrayList<>();
            temp.add(value);
            this.productions.put(key, temp);
        }
    }

    public List<Production> getProductionList(Symbol symbol) {
        if (!symbol.isTerminal()) {
            return this.productions.get(symbol.getValue());
        }
        return null;
    }

    public Set<Symbol> firstSet(Symbol S) {
        Set<Symbol> set = new HashSet<>();
        if (S.isTerminal()) { // if S is terminal, add itself
            set.add(S);
            return set;
        }
        // if S -> ε, then add ε to first(S)
        // if S -> a, then add a to first(S)
        getProductionList(S).forEach(p -> { // all the right production for S
            List<Symbol> symbolList = p.getRight(); // all the symbol in a production
            for (Symbol symbol : symbolList) {
                if (symbol.isTerminal()) { // we define the epsilon to terminal so we can add together
                    set.add(symbol);
                } else { // if S -> ABC, use the official algorithm to do
                    int j = 0;
                    int k = symbolList.size();
                    while (j < k) {
                        Set<Symbol> tempSet = this.firstSet(symbolList.get(j));
                        if (!tempSet.contains(Symbol.epsilon)) {
                            break;
                        }
                        tempSet.remove(Symbol.epsilon);
                        set.addAll(tempSet);
                        j++;
                    }
                    if (j == k && this.firstSet(symbolList.get(k-1)).contains(Symbol.epsilon)) {
                        set.add(Symbol.epsilon);
                    }
                }
            }
        });
        return set;
    }

    public Set<Symbol> followSet(Symbol symbol) {
        Set<Symbol> set = new HashSet<>();
        if (symbol.isTerminal()) {

        }
        return set;
    }

    public static Grammar getGrammar() {
        return grammar;
    }

    public static void setGrammar(Grammar grammar) {
        Grammar.grammar = grammar;
    }
}
