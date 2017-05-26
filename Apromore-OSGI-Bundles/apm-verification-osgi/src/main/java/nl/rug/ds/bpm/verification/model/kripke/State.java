package nl.rug.ds.bpm.verification.model.kripke;

import nl.rug.ds.bpm.verification.comparator.StateComparator;
import nl.rug.ds.bpm.verification.optimizer.stutterOptimizer.Block;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class State implements Comparable<State> {
    private static int stateID = 0;
    private String id;
    private String hash, APHash;
    private Set<String> atomicPropositions;
    private Set<State> nextStates, previousStates;
    //stutter variables
    private boolean flag = false;
    private Block block;

    public State(String marking, TreeSet<String> atomicPropositions) {
        this.id = "S" + stateID++;
        this.atomicPropositions = atomicPropositions;
        nextStates = new HashSet<>();
        previousStates = new HashSet<>();

        String M = marking + "=";
        String AP = "";
        Iterator<String> api = atomicPropositions.iterator();
        while (api.hasNext()) {
            AP = AP + api.next();
            if (api.hasNext()) AP = AP + " ";
        }
        M = M + AP;
        hash = M;
        APHash = AP;
    }
        
    public static void resetStateId(){
        State.stateID = 0;
    }

    public String getID() {
        return id;
    }

    @Override
    public int hashCode() { return hash.hashCode(); }

    public String hash() { return  hash; }
    public String APHash() { return APHash; }

    public Set<String> getAtomicPropositions() {
        return atomicPropositions;
    }

    public boolean addNext(State s) {
        return nextStates.add(s);
    }

    public boolean addNext(Set<State> s) {
        return nextStates.addAll(s);
    }

    public Set<State> getNextStates() {
        return nextStates;
    }

    public void setNextStates(Set<State> nextStates) { this.nextStates = nextStates; }

    public boolean addPrevious(State s) {
        return previousStates.add(s);
    }

    public boolean addPrevious(Set<State> s) {
        return previousStates.addAll(s);
    }

    public Set<State> getPreviousStates() {
        return previousStates;
    }

    public void setPreviousStates(Set<State> previousStates) { this.previousStates = previousStates; }

    @Override
    public String toString() {
        StringBuilder st = new StringBuilder(getID() + ": {" + hash + " = ");
        Iterator<String> api = getAtomicPropositions().iterator();
        while (api.hasNext()) {
            st.append(api.next());
            if (api.hasNext()) st.append(" ");
        }
        st.append("}");
        return st.toString();
    }

    @Override
    public int compareTo(State o) {
        if (this == o)
            return 0;
        if(o == null)
            return -1;
        if(this.getClass() != o.getClass())
            return -1;
        return hash.compareTo(o.hash());
    }

    @Override
    public boolean equals(Object arg0) {
        if(this == arg0)
            return true;
        if(arg0 == null)
            return false;
        if(this.getClass() != arg0.getClass())
            return false;
        return hash.equals(((State) arg0).hash());
    }

    public void removeAP(Set<String> APs) {
        atomicPropositions.removeAll(APs);

        String AP = "";
        Iterator<String> api = atomicPropositions.iterator();
        while (api.hasNext()) {
            AP = AP + api.next();
            if (api.hasNext()) AP = AP + " ";
        }
        APHash = AP;
    }

    public boolean APequals(State n) { return APHash.equals(n.APHash()); }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
    
    public boolean getFlag() {
        return flag;
    }
    
    public void setBlock(Block block) { this.block = block; }
    
    public void resetBlock() { block = null; }
    
    public Block getBlock() { return block; }
}
