package com.elexer.tool;

import com.elexer.type.Symbol;

import java.util.*;

/**
 * Created by lizhuoli on 15/12/17.
 */
public class Tool {
    public static List<Symbol> stringToSymbolList(String input) {
        List<Symbol> list = new ArrayList<>();
        input = input.replace(" ", "");
        char[] arr = input.toCharArray();
        for (char a: arr) {
            list.add(new Symbol(String.valueOf(a), true)); // all input tokens are terminal
        }
        list.add(Symbol.end); // add #
        return list;
    }
}
