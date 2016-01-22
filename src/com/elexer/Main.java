package com.elexer;

import com.elexer.lib.LLParser;
import com.elexer.lib.LRParser;
import com.elexer.lib.Parser;
import com.elexer.tool.Config;
import com.elexer.tool.Logger;
import com.elexer.tool.Scanner;
import com.elexer.tool.Tool;
import com.elexer.type.Grammar;
import com.elexer.type.Production;
import com.elexer.type.Symbol;

import java.util.List;
import java.util.Map;


public class Main {

    public static void printGrammar(Grammar grammar) {
        Map<Symbol, List<Production>> productions = grammar.getProductions();
        for (Symbol key : productions.keySet()) {
            productions.get(key).forEach(p -> {
                System.out.println("left: " + p.getLeft().getValue());
                p.getRight().forEach(s -> {
                    System.out.println("right: " + s.getValue() + " type: " + s.isTerminal());
                });
                System.out.println("");
            });
        }
    }

    public static void main(String[] args) {

        System.out.println("Input the content-free grammar, you can use 'Enter' to separate lines.\n" +
                "Use :q to stop input.");

        Scanner scanner = Scanner.getInstance(Config.input);
        Logger logger = Logger.getInstance(Config.level);

        Production currentProduction = new Production();
        Grammar grammar = new Grammar();
        String tempLeft = "";
        String tempRight = "";
        char input;
        int state = 0;
        while (true) {
            if (scanner.hasNext()) {
                input = scanner.nextChar();
                if (input == ':' && scanner.nextChar() == 'q') {
                    break;
                }
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
                    if (input == '-') {
                        state = 4;
                        continue;
                    } else if (input == '{') {
                        tempLeft += input;
                        state = 2;
                        continue;
                    } else {
                        logger.parseError("left production except '-' or '{'");
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
                    if (input == '-') {
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
                        grammar.addProduction(new Symbol(tempLeft, false), currentProduction);
                        Production newProduction = new Production();
                        newProduction.setLeft(currentProduction.getLeft());
                        currentProduction = newProduction;
                        continue;
                    } else if (input == '\n') {
                        state = 0;
                        logger.log("success");
                        grammar.addProduction(new Symbol(tempLeft, false), currentProduction);
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
                        currentProduction.addRight(Symbol.epsilon);
                        continue;
                    } else {
                        logger.parseError("right production except e for epsilon");
                    }
                default:
                    logger.parseError("Fatal Error");
            }
        }
        Grammar.setGrammar(grammar);

        printGrammar(grammar);

        Parser parser = new LRParser(grammar);

        parser.init();

        parser.log();

        System.out.println("Now, input the token you want to recognize and press enter.\n" +
                "Use EOF to stop.");

        while (scanner.hasNextLine()) {
            String token = scanner.nextLine();
            boolean result = parser.parse(Tool.stringToSymbolList(token));
            logger.log(String.format("Parse result: %s", result));
        }
    }
}