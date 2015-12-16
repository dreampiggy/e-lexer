package com.elexer.lib;

import com.elexer.tool.Scanner;
import com.elexer.type.Grammar;
import com.elexer.type.Symbol;

import java.util.Stack;

/**
 * Created by lizhuoli on 15/12/16.
 */
public class Parser {
    public void LLParse(Grammar grammar, Scanner scanner) {
        Stack<Symbol> stack = new Stack<>();

        int currentChoice = 0;

//        stack.push(grammar.getProductions().get("S")[0]);
//        while (!stack.empty()) {
//            char token = scanner.nextChar();
//            Symbol top = stack.pop();
//            if (top.isTerminal()) {
//                if (top.getValue().charAt(0) == token) {
//                    stack.pop();
//                    continue;
//                }
//            } else {
//                stack.pop();
//                if ()
//            }
//        }
//

    }
}
