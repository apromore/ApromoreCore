package au.edu.qut.structuring.ui;

import au.edu.qut.structuring.core.StructuringCore;

/**
 * Created by Adriano on 29/02/2016.
 */
public class iBPStructUIResult {

    private StructuringCore.Policy policy;
    private int maxDepth;
    private int maxSol;
    private int maxChildren;
    private int maxStates;
    private int maxMinutes;
    private boolean timeBounded;
    private boolean forceStructuring;
    private boolean keepBisimulation;


    public boolean isForceStructuring() { return forceStructuring; }
    public void setForceStructuring(boolean forceStructuring) { this.forceStructuring = forceStructuring; }

    public boolean isKeepBisimulation() {
        return keepBisimulation;
    }
    public void setKeepBisimulation(boolean keepBisimulation) { this.keepBisimulation = keepBisimulation; }

    public boolean isTimeBounded() { return timeBounded; }
    public void setTimeBounded(boolean timeBounded) { this.timeBounded = timeBounded; }

    public StructuringCore.Policy getPolicy() {
        return policy;
    }
    public void setPolicy(StructuringCore.Policy policy) {
        this.policy = policy;
    }

    public int getMaxDepth() { return maxDepth; }
    public void setMaxDepth(int maxDepth) { this.maxDepth = maxDepth; }

    public int getMaxSol() { return maxSol; }
    public void setMaxSol(int maxSol) { this.maxSol = maxSol; }

    public int getMaxMinutes() { return maxMinutes; }
    public void setMaxMinutes(int maxMinutes) { this.maxMinutes = maxMinutes; }

    public int getMaxChildren() {
        return maxChildren;
    }
    public void setMaxChildren(int maxChildren) {
        this.maxChildren = maxChildren;
    }

    public int getMaxStates() {
        return maxStates;
    }
    public void setMaxStates(int maxStates) {
        this.maxStates = maxStates;
    }
}
