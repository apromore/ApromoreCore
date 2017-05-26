package nl.rug.ds.bpm.verification.model.kripke;

import nl.rug.ds.bpm.verification.comparator.StateComparator;
import nl.rug.ds.bpm.verification.comparator.StringComparator;

import java.util.*;
import java.util.stream.Collectors;

public class Kripke {
    private TreeSet<String> atomicPropositions;
    private TreeSet<State> states;
    private TreeSet<State> initial;

    public Kripke() {
        atomicPropositions = new TreeSet<String>(new StringComparator());
        states = new TreeSet<State>(new StateComparator());
        initial = new TreeSet<State>(new StateComparator());
    }

    public boolean addInitial(State s) {
        addState(s);
        return initial.add(s);
    }

    public boolean addState(State s) {
        boolean known = states.add(s);
        if (known)
            atomicPropositions.addAll(s.getAtomicPropositions());
        return known;
    }

    public State addNext(State current, State next) {
        State known = states.ceiling(next);
        if(!next.equals(known)) {
            known = next;
            addState(next);
        }
        current.addNext(known);
        known.addPrevious(current);

        return known;
    }

    public TreeSet<String> getAtomicPropositions() {
        return atomicPropositions;
    }

    public TreeSet<State> getStates() {
        return states;
    }

    public TreeSet<State> getInitial() {
        return initial;
    }

    public Set<State> getSinkStates() {
        return states.stream().filter(s -> s.getNextStates().isEmpty()).collect(Collectors.toSet());
    }
    
    public int getStateCount() {
        return states.size();
    }

    public int getRelationCount() {
        int relCount = 0;
        for (State s : states)
            relCount += s.getNextStates().size();

        return relCount;
    }

    @Override
    public String toString() {
        String ret = "";
        StringBuilder ap = new StringBuilder("Atomic Propositions: {");
        Iterator<String> k = atomicPropositions.iterator();
        while (k.hasNext()) {
            ap.append(k.next());
            if (k.hasNext()) ap.append(", ");
        }
        ap.append("}\n\n");

        StringBuilder st = new StringBuilder("States:\n");
        for (State s : states)
            st.append(s.toString() + "\n");

        StringBuilder rel = new StringBuilder("\nRelations:\n");
        double relCount = 0;
        for (State s : states) {
            relCount += s.getNextStates().size();
            if (!s.getNextStates().isEmpty()) {
                rel.append(s.toString() + " -> ");
                for (State t : s.getNextStates())
                    rel.append(t.toString() + " ");
                rel.append("\n");
            }
        }

        StringBuilder count = new StringBuilder("\nNumber of states: " + states.size() + "\n");
        count.append("Number of relations: " + getRelationCount() + "\n");
        count.append("Number of AP: " + atomicPropositions.size() + "\n");

        ret = ap.toString() + st.toString() + rel.toString() + count.toString() + "\n";

        return ret;
    }
    
    public String stats() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("|S| = " + states.size() + ", ");
        sb.append("|R| = " + states.stream().map(State::getNextStates).mapToInt(Set::size).sum() +", ");
        sb.append("|AP| = " + atomicPropositions.size());
        
        return sb.toString();
    }
}
