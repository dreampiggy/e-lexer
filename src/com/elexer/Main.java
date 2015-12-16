package com.elexer;

import com.elexer.tool.Logger;
import com.elexer.tool.Scanner;
import com.elexer.type.Grammar;
import com.elexer.type.Production;
import com.elexer.type.Symbol;

import java.util.List;
import java.util.Map;


public class Main {

    public static void printGrammar(Grammar grammar) {
        Map<String, List<Production>> productions = grammar.getProductions();
        for (String key : productions.keySet()) {
            productions.get(key).forEach(p -> {
                System.out.println("left: " + p.getLeft().getValue());
                p.getRight().forEach(s -> {
                    System.out.println("right: " + s.getValue());
                });
                System.out.println("");
            });
        }
    }

    public static void main(String[] args) {
        Scanner scanner = Scanner.instance;
        Logger logger = Logger.instance;

        Production currentProduction = new Production();
        Grammar grammar = new Grammar();
        String tempLeft = "";
        String tempRight = "";
        char input;
        int state = 0;
        while (true) {
            if (scanner.hasNext()) {
                input = scanner.nextChar();
            } else {
                break;
            }

            logger.log("current input: " + input);

            switch (state) {
                case 0: //begin left production
                    if (input >= 'A' && input <= 'Z') {
                        state = 1;
                        tempLeft += input;
                        continue;
                    } else {
                        logger.parseError("left production except A-Z");
                    }
                case 1: //=> or {}
                    if (input == '=') {
                        state = 4;
                        continue;
                    } else if (input == '{') {
                        tempLeft += input;
                        state = 2;
                        continue;
                    } else {
                        logger.parseError("left production except '=' or '{'");
                    }
                case 2: //{} value is 0-9|*
                    if ((input >= '0' && input <= '9') || input == '*') {
                        state = 2;
                        tempLeft += input;
                        continue;
                    } else if (input == '}') {
                        tempLeft += input;
                        state = 3;
                        continue;
                    } else {
                        logger.parseError("left production except 0-9* in {}");
                    }
                case 3:
                    if (input == '=') {
                        state = 4;
                        continue;
                    } else {
                        logger.parseError("left production except =");
                    }
                case 4: //=>
                    if (input == '>') {
                        state = 5;
                        currentProduction.setLeft(new Symbol(tempLeft, false));
                        continue;
                    } else {
                        logger.parseError("left production except >");
                    }
                case 5: // right production
                    if ((input >= 'a' && input <= 'z') || (input >= '0' && input <= '9')
                            || (input == '(') || (input == ')')
                            || (input == '+') || (input == '-')
                            || (input == '*') || (input == '/')) { //a-z()+-*/
                        state = 6;
                        currentProduction.addRight(new Symbol(String.valueOf(input), true));
                        continue;
                    } else if(input >= 'A' && input <= 'Z') { //A-Z
                        if (scanner.peek() == '{') { // peek to check P{1} type
                            tempRight += input;
                            tempRight += scanner.nextChar(); // eat {
                            state = 7;
                        } else {
                            state = 6;
                            currentProduction.addRight(new Symbol(String.valueOf(input), false));
                        }
                        continue;
                    } else if (input == '\\') { //\e
                        state = 8;
                        continue;
                    } else {
                        logger.parseError("right production except terminal or non-terminal or epsilon");
                    }
                case 6:
                    if ((input >= 'a' && input <= 'z') || (input >= '0' && input <= '9')
                            || (input == '(') || (input == ')')
                            || (input == '+') || (input == '-')
                            || (input == '*') || (input == '/')) {	//a-zA-Z()+-*/
                        state = 6;
                        currentProduction.addRight(new Symbol(String.valueOf(input), true));
                        continue;
                    } else if(input >= 'A' && input <= 'Z') { //A-Z
                        if (scanner.peek() == '{') { // peek to check P{1} type
                            tempRight += input;
                            tempRight += scanner.nextChar(); // eat {
                            state = 7;
                        } else {
                            state = 6;
                            currentProduction.addRight(new Symbol(String.valueOf(input), false));
                        }
                        continue;
                    } else if (input == '\\') { //\e
                        state = 8;
                        continue;
                    } else if (input == '|') { //|
                        state = 6;
                        grammar.addProductions(tempLeft, currentProduction);
                        Production newProduction = new Production();
                        newProduction.setLeft(currentProduction.getLeft());
                        currentProduction = newProduction;
                        continue;
                    } else if (input == '\n') {
                        state = 0;
                        logger.log("success");
                        grammar.addProductions(tempLeft, currentProduction);
                        currentProduction = new Production();
                        tempLeft = "";
                        tempRight = "";
                        continue;
                    } else {
                        logger.parseError("right production except terminal or non-terminal or epsilon or | or \\n");
                    }
                case 7: // {} value is 0-9|*
                    if ((input >= '0' && input <= '9') || input == '*') {
                        state = 7;
                        tempRight += input;
                        continue;
                    } else if (input == '}') {
                        state = 6;
                        tempRight += input;
                        currentProduction.addRight(new Symbol(tempRight, false));
                        tempRight = "";
                        continue;
                    } else {
                        logger.parseError("right production except 0-9* in {}");
                    }
                case 8: // epsilon
                    if (input == 'e') {
                        state = 6;
                        currentProduction.addRight(new Symbol("ε", true));
                        continue;
                    } else {
                        logger.parseError("right production except e for epsilon");
                    }
                default:
                    logger.parseError("Fatal Error");
            }
        }
        printGrammar(grammar);
    }
}