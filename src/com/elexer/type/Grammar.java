package com.elexer.type;

import java.util.*;

/**
 * Created by lizhuoli on 15/12/16.
 */
public class Grammar {
    private Map<Symbol, List<Production>> productions = new HashMap<>(); // key are non-terminal symbol
    private List<Production> productionList = new ArrayList<>(); // all production list
    private Set<Symbol> terminalSet = new HashSet<>(); // all terminal symbol set

    private static Grammar grammar;

    public Map<Symbol, List<Production>> getProductions() {
        return productions;
    }

    public List<Production> getAllProductionList() {
        return productionList;
    }

    public void addProduction(Symbol key, Production value) {
        this.productionList.add(value);
        if (key.isTerminal()) {
            terminalSet.add(key);
        }
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

    public Set<Symbol> getTerminalSet() {
        return terminalSet;
    }

    public static Grammar getGrammar() {
        return grammar;
    }

    public static void setGrammar(Grammar grammar) {
        Grammar.grammar = grammar;
    }
}
