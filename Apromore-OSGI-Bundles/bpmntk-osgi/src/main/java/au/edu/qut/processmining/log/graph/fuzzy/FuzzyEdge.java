package au.edu.qut.processmining.log.graph.fuzzy;

import au.edu.qut.processmining.log.graph.EventEdge;

/**
 * Created by Adriano on 15/06/2016.
 */

public class FuzzyEdge extends EventEdge implements Comparable {
    private int frequency;

    public FuzzyEdge(FuzzyNode source, FuzzyNode target){
        super(source, target);
        frequency = 0;
    }

    public FuzzyEdge(FuzzyNode source, FuzzyNode target, String label){
        super(source, target, label);
        frequency = 0;
    }

    public FuzzyEdge(FuzzyNode source, FuzzyNode target, int frequency){
        super(source, target);
        this.frequency = frequency;
    }

    public FuzzyEdge(FuzzyNode source, FuzzyNode target, String label, int frequency){
        super(source, target, label);
        this.frequency = frequency;
    }

    public void increaseFrequency() { frequency++; }
    public void increaseFrequency(int amount) { frequency += amount; }

    public int getFrequency(){ return frequency; }

}
