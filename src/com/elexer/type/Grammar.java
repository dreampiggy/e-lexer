package com.elexer.type;

import java.util.*;

/**
 * Created by lizhuoli on 15/12/16.
 */
public class Grammar {
    private Map<Symbol, List<Production>> productions = new HashMap<>();
    private List<Production> productionList = new ArrayList<>();
    private static Grammar grammar;

    public Map<Symbol, List<Production>> getProductions() {
        return productions;
    }

    public void addProduction(Symbol key, Production value) {
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
            return this.productions.get(symbol);
        }
        return null;
    }

    public List<Production> getAllProductionList() {
        this.productions.forEach((key, value) -> productionList.addAll(value));
        return this.productionList;
    }

    public static Grammar getGrammar() {
        return grammar;
    }

    public static void setGrammar(Grammar grammar) {
        Grammar.grammar = grammar;
    }
}
