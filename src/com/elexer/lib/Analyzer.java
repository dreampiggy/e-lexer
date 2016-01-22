package com.elexer.lib;

import com.elexer.type.Grammar;
import com.elexer.type.Production;
import com.elexer.type.State;
import com.elexer.type.Symbol;

import java.util.*;

/**
 * Created by lizhuoli on 15/12/18.
 */
public class Analyzer {
    private static Analyzer instance = null;
    private Grammar grammar;
    private Map<Symbol, Set<Symbol>> first;
    private Map<Symbol, Set<Symbol>> follow;
    private Map<Production, Set<Symbol>> select;
    private Set<Symbol> nullable;
    private Set<State> canonical;
    private List<Production> productionList;
    private Set<Symbol> terminalSet;
    private Set<Symbol> nonTerminalSet;

    public static Analyzer getInstance(Grammar grammar) {
        if (instance == null) {
            instance = new Analyzer(grammar);
        }
        return instance;
    }

    private Analyzer(Grammar grammar) {
        this.grammar = grammar;
        this.productionList = grammar.getAllProductionList();
        this.terminalSet = grammar.getTerminalSet();
        this.nonTerminalSet = grammar.getProductions().keySet();
    }

    public void analyze() {
        nullableSet();
        firstSet();
        followSet();
        selectSet();
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

        for (Symbol term: terminalSet) { // add all terminal symbol to first set
            Set<Symbol> temp = new HashSet<>();
            temp.add(term);
            first.put(term, temp);
        }
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
                if (symbol.isEpsilon()) {
                    select.get(P).addAll(follow.get(N)); // if all can get epsilon in N -> T1T2..Tn, then add follow set
                    break;
                }
                if (symbol.isTerminal()) { // N -> a except epsilon
                    select.get(P).add(symbol); // just add and end
                    break;
                } else { // N -> M
                    Set<Symbol> tempSet = first.get(symbol);
                    tempSet.remove(Symbol.epsilon);
                    select.get(P).addAll(tempSet); // add first(M) to select(N)
                    if (!nullable.contains(symbol)) {
                        break; // N can't product epsilon, so all is over. else need to judge next symbol
                    }
                }
            }
        }
    }

    // first set for a string, cup= all non-nullable first set
    public Set<Symbol> firstStringSet(List<Symbol> symbolList) {
        Set<Symbol> firstString = new HashSet<>();
        for (int i = 0; i < symbolList.size(); i++) {
            Symbol symbol = symbolList.get(i);
            Set<Symbol> firstSet = first.get(symbol);
            if (firstSet.contains(Symbol.epsilon)) { // if contains epsilon, stop
                break;
            }
            firstString.addAll(firstSet); // add first(s) to first_s(S...)
        }
        return firstString;
    }

    /*
	closure(s)
		while (s is still changing)
			for each item [A→β•Cδ,a]∈s
				for each production C→γ ∈ P
					for each b∈FIRST(δa)
						s ← s ∪ {[C→•γ,b]}
		return s
     */
    public Set<Production> genClosure(Set<Production> set) {
        Set<Production> copy = new HashSet<>();

        do {
            copy.addAll(set);
            for (Production P: copy) { // like [N -> \beta C \delta, a]
                List<Symbol> symbolList = P.getRight();
                int dot = P.getDot(); // dot position

                if (P.dotIsEnd()) continue; // there must be an C before dot meat end

                Symbol C = symbolList.get(dot);
                Symbol a = P.getPredict();
                if (symbolList.get(dot).isTerminal()) { // C must be non-terminal
                    continue;
                }
                List<Symbol> delta = new ArrayList<>();

                int start = dot + 1; // start after C
                if (start > symbolList.size() - 1) {
                    // delta is epsilon
                } else {
                    for (int i = start; i < symbolList.size(); i++) {
                        delta.add(symbolList.get(i));
                    }
                }

                for(Production production: grammar.getProductionList(C)) { // for all like C -> \gamma
                    List<Symbol> gamma = production.getRight();
                    if (!a.isEnd()) { // trick to calculate firstString without modify LL(1) first set
                        delta.add(a);
                    }
                    Set<Symbol> firstSet = firstStringSet(delta);
                    firstSet.add(a);
                    for (Symbol b : firstSet) { // predict symbol
                        Production temp = new Production(production.getLeft(), production.getRight(), b);
                        set.add(temp); // add [C -> . \gamma , b]
                    }
                }
            }
        } while(!copy.equals(set));

        return set;
    }

    /*
    goto(s, x)
        moved ← ∅
        for each item i ∈ s
            if the form of i is [α→β•xδ, a] then moved ← moved ∪ {[α→βx•δ, a]}
        return closure(moved)
     */
    public Set<Production> gotoClosure(Set<Production> set, Symbol symbol) {
        Set<Production> movedClosure = new HashSet<>();

        for (Production P: set) {
            List<Symbol> symbolList = P.getRight();
            int dot = P.getDot();
            if (!P.dotIsEnd() && symbol.equals(symbolList.get(dot))) { // like N -> \beta .x \delta(only x != epsilon)
                Production temp = new Production(P.getLeft(), P.getRight(), P.getPredict());
                temp.setDot(P.getDot() + 1);
                movedClosure.add(temp);
            }
        }

        if (movedClosure.isEmpty()) {
            return null;
        }

        return genClosure(movedClosure);
    }

    /*
    cc0 ← closure({[S′ →• S, eof]})
    CC ← {cc0}
    while (new sets are still being added to CC)
        for each unmarked set cci ∈ CC
            mark cci as processed
            for each x following a • in an item in cci
            temp ← goto(cci,x)
            if temp ∈/CC
                then CC ← CC∪{temp}
            record transition from cci to temp on x
     */
    public void canonicalSet() {
        // init
        canonical = new HashSet<>();
        Set<Production> startItem = new HashSet<>();
        startItem.add(Production.preStart);
        State start = new State(genClosure(startItem));
        State.setStart(start);
        canonical.add(start);

        Set<State> copy = new HashSet<>();

        do {
            copy.addAll(canonical);
            copy.add(new State(genClosure(startItem)));
            for (State state: copy) {
                if (state.isMarked()) {
                    continue;
                }
                state.mark();
                for (Production P: state.getClosure()) {
                    List<Symbol> symbolList = P.getRight();
                    int dot = P.getDot();
                    if (P.dotIsEnd()) continue;
                    Symbol symbol = symbolList.get(dot); // get x like N -> x. \beta, x != epsilon
                    if (symbol.isEpsilon()) continue;

                    State temp = new State(gotoClosure(state.getClosure(), symbol));
                    if (!canonical.contains(temp)) {
                        canonical.add(temp);
                    }
//                    recordTransition(set, temp, symbol); // we can get efficiency better if use record
                }
            }
        } while (!copy.equals(canonical));
    }

//    public void recordTransition(Set<Production> from, Set<Production> to, Symbol x) {
//        record from CC_i to temp on symbol x
//    }


    public Set<State> getCanonical() {
        return this.canonical;
    }

    public Set<Symbol> getNullableSet() {
        return nullable;
    }

    public Map<Symbol, Set<Symbol>> getFirstSet() {
        return first;
    }

    public Map<Symbol, Set<Symbol>> getFollowSet() {
        return follow;
    }

    public Map<Production, Set<Symbol>> getSelectSet() {
        return select;
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
            System.out.print("Select(" + k.getLeft().getValue() + " -> ");
            k.getRight().forEach(s -> System.out.print(s.getValue()));
            System.out.println("): ");
            v.forEach(s -> System.out.print(s.getValue() + ", "));
            System.out.println("");
        });
    }
}
