package au.edu.qut.processmining.miners.heuristic.ui.miner;

import au.edu.qut.processmining.miners.heuristic.ui.net.HNMUIResult;

/**
 * Created by Adriano on 29/02/2016.
 */
public class HMPlusUIResult {

    public enum StructuringTime {NONE, POST, PRE}
    public static final StructuringTime STRUCT_POLICY = StructuringTime.NONE;
    private boolean replaceIORs;

    private HNMUIResult hnmParams;

    private StructuringTime structuringTime;

    public HMPlusUIResult() {
        hnmParams = new HNMUIResult();
        structuringTime = STRUCT_POLICY;
        replaceIORs = true;
    }

    public StructuringTime getStructuringTime() { return structuringTime; }
    public void setStructuringTime(StructuringTime structuringTime) { this.structuringTime = structuringTime; }

    public boolean isReplaceIORs() { return replaceIORs; }
    public void setReplaceIORs(boolean replaceIORs) { this.replaceIORs = replaceIORs; }

    public double getFrequencyThreshold() {
        return hnmParams.getFrequencyThreshold();
    }
    public void setFrequencyThreshold(double frequencyThreshold) {
        hnmParams.setFrequencyThreshold(frequencyThreshold);
    }

    public double getParallelismsThreshold() {
        return hnmParams.getParallelismsThreshold();
    }
    public void setParallelismsThreshold(double parallelismsThreshold) {
        hnmParams.setParallelismsThreshold(parallelismsThreshold);
    }
}
