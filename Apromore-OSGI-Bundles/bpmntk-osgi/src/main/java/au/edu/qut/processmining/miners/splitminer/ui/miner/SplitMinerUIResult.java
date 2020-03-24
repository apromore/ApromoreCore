package au.edu.qut.processmining.miners.splitminer.ui.miner;

import au.edu.qut.processmining.miners.splitminer.ui.dfgp.DFGPUIResult;

/**
 * Created by Adriano on 29/02/2016.
 */
public class SplitMinerUIResult extends DFGPUIResult {

    public enum StructuringTime {NONE, POST, PRE}
    public static final StructuringTime STRUCT_POLICY = StructuringTime.NONE;
    private boolean replaceIORs;
    private boolean removeLoopActivities;

    private StructuringTime structuringTime;

    public SplitMinerUIResult() {
        structuringTime = STRUCT_POLICY;
        replaceIORs = true;
        removeLoopActivities = false;
    }

    public StructuringTime getStructuringTime() { return structuringTime; }
    public void setStructuringTime(StructuringTime structuringTime) { this.structuringTime = structuringTime; }

    public boolean isReplaceIORs() { return replaceIORs; }
    public void setReplaceIORs(boolean replaceIORs) { this.replaceIORs = replaceIORs; }

    public boolean isRemoveLoopActivities() { return removeLoopActivities; }
    public void setRemoveLoopActivities(boolean removeLoopActivities) { this.removeLoopActivities = removeLoopActivities; }

}
