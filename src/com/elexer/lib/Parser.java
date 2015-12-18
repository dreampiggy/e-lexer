package com.elexer.lib;

import com.elexer.type.Symbol;

import java.util.List;

/**
 * Created by lizhuoli on 15/12/16.
 */
public interface Parser {
    public void init();
    public boolean parse(List<Symbol> tokens);
}
