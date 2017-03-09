package au.edu.qut.processmining.miners.yam.ui.miner;

import au.edu.qut.processmining.miners.yam.ui.dfgp.DFGPUIResult;

/**
 * Created by Adriano on 29/02/2016.
 */
public class YAMUIResult {

    public enum StructuringTime {NONE, POST, PRE}
    public static final StructuringTime STRUCT_POLICY = StructuringTime.NONE;
    private boolean replaceIORs;

    private DFGPUIResult dfgpParams;

    private StructuringTime structuringTime;

    public YAMUIResult() {
        dfgpParams = new DFGPUIResult();
        structuringTime = STRUCT_POLICY;
        replaceIORs = true;
    }

    public StructuringTime getStructuringTime() { return structuringTime; }
    public void setStructuringTime(StructuringTime structuringTime) { this.structuringTime = structuringTime; }

    public boolean isReplaceIORs() { return replaceIORs; }
    public void setReplaceIORs(boolean replaceIORs) { this.replaceIORs = replaceIORs; }

    public double getFrequencyThreshold() {
        return dfgpParams.getFrequencyThreshold();
    }
    public void setFrequencyThreshold(double frequencyThreshold) {
        dfgpParams.setFrequencyThreshold(frequencyThreshold);
    }

    public double getParallelismsThreshold() {
        return dfgpParams.getParallelismsThreshold();
    }
    public void setParallelismsThreshold(double parallelismsThreshold) {
        dfgpParams.setParallelismsThreshold(parallelismsThreshold);
    }
}
