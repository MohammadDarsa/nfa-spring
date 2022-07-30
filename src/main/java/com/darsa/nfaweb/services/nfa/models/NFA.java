package com.darsa.nfaweb.services.nfa.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class NFA {
    private List<Integer> states;
    private List<Transition> transitions;
    private int finalState;

    public NFA() {
        states = new ArrayList<>();
        transitions = new ArrayList<>();
        finalState = 0;
    }

    public NFA(int size) {
        this();
        setStateSize(size);
    }

    public NFA(int size, int finalState) {
        this(size);
        this.finalState = finalState;
    }

    public NFA(char c) {
        this(2, 1);
        this.transitions.add(new Transition(0, 1, c));
    }

    public void setStateSize(int size) {
        states.clear();
        for(int i=0; i<size; i++) {
            states.add(i);
        }
    }

    public void addTransition(int stateFrom, int stateTo, char transitionSymbol) {
        transitions.add(new Transition(stateFrom, stateTo, transitionSymbol));
    }

    /*
    * Here we'll start defining the 3 operations on the automaton
    * Kleene star which has the highest precedence
    * concat middle precedence
    * union lowest precedence
    * */

    public NFA star() {
        //creating the result automaton with final state
        NFA result = new NFA(states.size() + 2, states.size()+1);

        //copy list of transitions from initial to result
        result.transitions =  transitions.stream()
                .map(t -> new Transition(t.getStateFrom()+1, t.getStateTo()+1, t.getTransitionSymbol()))
                .collect(Collectors.toList());

        //adding transition between first state and final state
        result.transitions.add(new Transition(0, states.size()+1, 'E'));

        //adding the initial epsilon transition
        result.transitions.add(new Transition(0, 1, 'E'));

        //adding the tail epsilon transition
        result.transitions.add(new Transition(states.size(), states.size()+1, 'E'));

        //adding the transition between first initial state and last initial state
        result.transitions.add(new Transition(states.size(), 1, 'E'));

        return result;
    }

    public NFA concat(NFA nfa) {
        //creating a new automaton with size of both automaton - 1 cus there's a common state
        NFA result = new NFA(states.size() + nfa.states.size() - 1, states.size() + nfa.states.size() - 2);

        //adding initial automaton transitions
        result.transitions.addAll(transitions);

        //adding second automaton transitions
        List<Transition> nfaTransition = nfa.transitions.stream()
                .map(t -> new Transition(t.getStateFrom() + states.size()-1, t.getStateTo() + states.size()-1, t.getTransitionSymbol()))
                .collect(Collectors.toList());
        result.transitions.addAll(nfaTransition);

        return result;
    }

    public NFA union(NFA nfa) {
        //creating a new automaton with size of both automaton + 2
        NFA result = new NFA(states.size() + nfa.states.size() + 2, states.size() + nfa.states.size() + 1);

        //list of transitions of initial automaton
        List<Transition> initialNfaTransitions = transitions.stream()
                .map(t -> new Transition(t.getStateFrom() + 1, t.getStateTo() + 1, t.getTransitionSymbol()))
                .collect(Collectors.toList());
        //list of transitions of 2nd automaton
        List<Transition> operandNfaTransition = nfa.transitions.stream()
                .map(t -> new Transition(t.getStateFrom() + states.size()+1, t.getStateTo() + states.size()+1, t.getTransitionSymbol()))
                .collect(Collectors.toList());

        //adding the 4 initial and final transitions of both automaton to result automaton
        result.transitions.add(new Transition(0, 1, 'E'));
        result.transitions.add(new Transition(0, states.size()+1, 'E'));
        result.transitions.add(new Transition(states.size(), result.finalState, 'E'));
        result.transitions.add(new Transition(result.finalState-1, result.finalState, 'E'));

        //adding both lists of transitions to the final automaton
        result.transitions.addAll(initialNfaTransitions);
        result.transitions.addAll(operandNfaTransition);

        return result;
    }


    //outputs nfa as a mermaid flowchart
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for(Integer i : states) {
            if(i != finalState)
                result.append(i).append("((").append(i).append("))\n");
        }
        result.append(finalState).append("(((").append(finalState).append(")))\n\n");
        for(Transition transition : transitions) {
            result.append(transition.toString());
        }

        return result.toString();
    }
}
