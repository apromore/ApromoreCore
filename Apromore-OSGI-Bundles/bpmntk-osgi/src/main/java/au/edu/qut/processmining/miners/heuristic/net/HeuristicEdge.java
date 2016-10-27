package au.edu.qut.processmining.miners.heuristic.net;

import au.edu.qut.processmining.log.graph.LogEdge;

/**
 * Created by Adriano on 24/10/2016.
 */
public class HeuristicEdge extends LogEdge {
    private int frequency;

    private double localDependencyScore;
    private double globalDependencyScore;

    public HeuristicEdge(HeuristicNode source, HeuristicNode target){
        super(source, target);
        frequency = 0;
        localDependencyScore = 0;
        globalDependencyScore = 0;
    }

    public HeuristicEdge(HeuristicNode source, HeuristicNode target, String label){
        super(source, target, label);
        frequency = 0;
        localDependencyScore = 0;
        globalDependencyScore = 0;
    }

    public HeuristicEdge(HeuristicNode source, HeuristicNode target, int frequency){
        super(source, target);
        this.frequency = frequency;
        localDependencyScore = 0;
        globalDependencyScore = 0;
    }

    public HeuristicEdge(HeuristicNode source, HeuristicNode target, String label, int frequency){
        super(source, target, label);
        this.frequency = frequency;
        localDependencyScore = 0;
        globalDependencyScore = 0;
    }

    public void increaseFrequency() { frequency++; }
    public void increaseFrequency(int amount) { frequency += amount; }

    public double getLocalDependencyScore() { return localDependencyScore; }
    public void setLocalDependencyScore(double localDependencyScore) { this.localDependencyScore = localDependencyScore; }

    public double getGlobalDependencyScore() { return globalDependencyScore; }
    public void setGlobalDependencyScore(double globalDependencyScore) { this.globalDependencyScore = globalDependencyScore; }

    public int getFrequency(){ return frequency; }
}
