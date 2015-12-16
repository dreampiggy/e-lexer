package com.elexer.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lizhuoli on 15/12/16.
 */
public class Grammar {
    private Map<String, List<Production>> productions = new HashMap<>();

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
}
