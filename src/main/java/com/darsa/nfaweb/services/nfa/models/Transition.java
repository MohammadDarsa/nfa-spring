package com.darsa.nfaweb.services.nfa.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Transition {
    private int stateFrom, stateTo;
    private char transitionSymbol;

    @Override
    public String toString() {
        String stateFromStr = Integer.toString(stateFrom);
        String stateToStr = Integer.toString(stateTo);
        return stateFromStr + "--" + transitionSymbol + "-->" + stateToStr + "\n";
    }
}
