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

    private Map<Symbol, Set<Symbol>> follow;
    private Map<Symbol, Set<Symbol>> first;
    private Map<Production, Set<Symbol>> select;
    private Set<Symbol> nullable;
    private Map<Symbol, Map<Symbol, List<Symbol>>> table; // row is non-terminal, column is terminal
    private List<Production> productionList;
    private Set<Symbol> nonTerminalSet;

    public LLParser(Grammar grammar) {
        this.grammar = grammar;
        this.productionList = grammar.getAllProductionList();
        this.nonTerminalSet = grammar.getProductions().keySet();
    }

    public boolean parse(List<Symbol> tokens) {
        Stack<Symbol> stack = new Stack<>();
        int i = 0;

        stack.push(Symbol.start);
        while (!stack.isEmpty()) {
            Symbol top = stack.peek();
            System.out.println("Currernt top: " + top.getValue() + " input: " + tokens.get(i).getValue());
            if (top.isTerminal()) {
                if (top.isEnd()) {
                    return false; // fail: not LL(1) grammar, meet # but stack not empty
                }
                if (stack.peek().equals(tokens.get(i++))) {
                    stack.pop();
                    if (i > tokens.size()) {
                        return false; // fail: no more input token
                    }
                } else {
                    return false; // fail: current token not equal for stack
                }
            } else {
                stack.pop();
                List<Symbol> symbolList = table.get(top).get(tokens.get(i));
                for (int j = symbolList.size()-1; j > -1; j--) {
                    if (symbolList.get(i).isEpsilon()) {
                        continue; // ignore the epsilon, which means no symbol
                    }
                    stack.push(symbolList.get(j)); // push reversely symbol in predict table
                }
            }
        }

        return true; // success
    }

    public void predictTable() {
        // init
        table = new HashMap<>();
        nonTerminalSet.forEach(s -> table.put(s, new HashMap<>()));

        for (Symbol row: nonTerminalSet) { // row for non-terminal
            for (Production P: grammar.getProductionList(row)) {
                List<Symbol> symbolList = P.getRight();
                for (Symbol column: select.get(P)) { // column for termiinal, include #
                    System.out.println("row: "+ row.getValue() + " column: "+column.getValue());
                    table.get(row).put(column, symbolList); // define # to terminal. so we add all
                }
            }
        }
    }

    /*
	while (set is changing) {
		for p in production: N -> T {
			if (T == epsilon) {
				NULLABLE(N) cup = {N}
			}
			if (T is T1T2...TN) {
				if (T1 & T2 & ... Tn are all NULLABLE) {
					NULLABLE(N) cup= {N}
				}
			}
		}
	}
     */
    public void nullableSet() {
        // init
        nullable = new HashSet<>();
        Set<Symbol> copy = new HashSet<>();

        do {
            copy.addAll(nullable);
            for(Production P: productionList) {
                Symbol N = P.getLeft();
                List<Symbol> symbolList = P.getRight();
                if (symbolList.size() == 1 && symbolList.get(0).isEpsilon()) {
                    nullable.add(N);
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
                        nullable.add(N);
                    }
                }
            }
        } while (!copy.equals(nullable));
    }

    /*
	while(set is changing)
	for p in (production like N -> T1T2...Tn) {
		for(i = 1;i <= n;i++) { //positive
			if(T[i] == a...) { //term
				FIRST(N) cup= {a};
				break;
			}
			else if(T[i] == M...) { //nonTerm
				FIRST(N) cup= FIRST(M);
				if (M is not in NULLABLE) {
					break;
				}
			}
		}
	}
     */
    public void firstSet() {
        // init
        first = new HashMap<>();
        nonTerminalSet.forEach(s -> first.put(s, new HashSet<>()));
        Map<Symbol, Set<Symbol>> copy = new HashMap<>();

        do {
            for(Map.Entry<Symbol,Set<Symbol>> entry : first.entrySet()) {
                copy.put(entry.getKey(), new HashSet<>(entry.getValue())); // attention, HashMap `putAll` function is not deep copy
            }
            for (Production P: productionList) {
                Symbol N = P.getLeft();
                List<Symbol> symbolList = P.getRight();
                for (int i = 0; i < symbolList.size(); i++) {
                    Symbol symbol  = symbolList.get(i);
                    if (symbol.isTerminal()) {
                        first.get(N).add(symbol); // add `a` to first(N)
                        break;
                    } else {
                        first.get(N).addAll(first.get(symbol)); // add first(M) to first(N)
                        if (!nullable.contains(symbol)) { // if A is not in nullable set, break
                            break;
                        }
                    }
                }
            }
        } while (!copy.equals(first));
    }

    /*
    follow(S) = #
	while(set is changing){
		for p in (production like N -> T1T2...Tn) {
			temp = FOLLOW(N) //copy to temp
			for (i = n;i >= 1;i--) { //reverse
				if (T[i] == a...) { //term
					temp = {a}; // which is first(a)
				} else if (T[i] == M...) { //nonTerm
					FOLLOW(M) cup= temp;
					if (M is not NULLABLE) {
						temp = FIRST(M)
					} else {
						temp cup= FIRST(M) except epsilon
					}
				}
			}
		}
	}
     */
    public void followSet() {
        // init
        follow = new HashMap<>();
        nonTerminalSet.forEach(s -> follow.put(s, new HashSet<>()));
        follow.get(Symbol.start).add(Symbol.end); // add # to follow(S)

        Map<Symbol, Set<Symbol>> copy = new HashMap<>();

        do {
            for(Map.Entry<Symbol,Set<Symbol>> entry : follow.entrySet()) {
                copy.put(entry.getKey(), new HashSet<>(entry.getValue())); // attention, HashMap `putAll` function is not deep copy
            }

            for (Production P: productionList) {
                Symbol N = P.getLeft();
                List<Symbol> symbolList = P.getRight();
                Set<Symbol> temp = new HashSet<>(follow.get(N)); // attention, use `new` to copy the set
                for (int i = symbolList.size()-1; i > -1; i--) {
                    Symbol symbol = symbolList.get(i);
                    if (symbol.isTerminal()) {
                        temp = new HashSet<>();
                        temp.add(symbol); // temp = first(a) (well, actually is {a})
                    } else {
                        follow.get(symbol).addAll(temp);
                        if (!nullable.contains(symbol)) {
                            temp = first.get(symbol);
                        } else {
                            Set<Symbol> tempSet = new HashSet<>(first.get(symbol));
                            tempSet.remove(Symbol.epsilon); // don't get epsilon
                            temp.addAll(tempSet);
                        }
                    }
                }
            }
        } while (!copy.equals(follow));
    }

    // use first and follow set to generate select set to build predict table
    public void selectSet() {
        // init
        select = new HashMap<>();
        productionList.forEach(p -> select.put(p, new HashSet<>())); // now key is a production

        for (Production P: productionList) {
            Symbol N = P.getLeft();
            List<Symbol> symbolList = P.getRight();
            for (int i = 0; i < symbolList.size(); i++) {
                Symbol symbol = symbolList.get(i);
                if (symbol.isTerminal()) { // N -> a
                    select.get(P).add(symbol); // just add and end
                    break;
                } else { // N -> M
                    select.get(P).addAll(first.get(symbol)); // add first(M) to select(N)
                    if (!nullable.contains(symbol)) {
                        break; // N can't product epsilon, so all is over. else need to judge next symbol
                    }
                }
            }
            select.get(P).addAll(follow.get(N)); // if all can get epsilon in N -> T1T2..Tn, then add follow set
        }
    }

    public void printNullable() {
        System.out.println("\nPrint Nullable set\n");
        nullable.forEach(s -> {
            System.out.println(s.getValue() + ", ");
        });
    }

    public void printFirst() {
        System.out.println("\nPrint First Set\n");
        first.forEach((k, v) -> {
            System.out.println("First(" + k.getValue() + "): ");
            v.forEach(s -> System.out.print(s.getValue() + ", "));
            System.out.println("");
        });
    }

    public void printFollow() {
        System.out.println("\nPrint Follow Set\n");
        follow.forEach((k, v) -> {
            System.out.println("Follow(" + k.getValue() + "): ");
            v.forEach(s -> System.out.print(s.getValue() + ", "));
            System.out.println("");
        });
    }

    public void printSelect() {
        System.out.println("\nPrint Select Set\n");
        select.forEach((k, v) -> {
            System.out.print("Select(left: " + k.getLeft().getValue() + ", right: ");
            k.getRight().forEach(s -> System.out.print(s.getValue()));
            System.out.println("): ");
            v.forEach(s -> System.out.print(s.getValue() + ", "));
            System.out.println("");
        });
    }
}