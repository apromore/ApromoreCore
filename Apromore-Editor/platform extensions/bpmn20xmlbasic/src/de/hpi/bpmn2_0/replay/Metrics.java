package de.hpi.bpmn2_0.replay;

public class Metrics {
    private int missingTokenCount = 0;
    private int consumedTokenCount = 0;
    private int remainingTokenCount = 0;
    private int producedTokenCount = 0;
    private double tracePercent = 0;
    
    public int getMissingTokenCount() {
        return this.missingTokenCount;
    }
    
    public void setMissingTokenCount(int missingToken) {
        this.missingTokenCount = missingToken;
    }
    
    public int getConsumedTokenCount() {
        return this.consumedTokenCount;
    }
    
    public void setConsumedTokenCount(int consumedToken) {
        this.consumedTokenCount = consumedToken;
    }    
    
    public int getRemainingTokenCount() {
        return this.remainingTokenCount;
    }
    
    public void setRemainingTokenCount(int remainingToken) {
        this.remainingTokenCount = remainingToken;
    }
    
    public int getProducedTokenCount() {
        return this.producedTokenCount;
    }
    
    public void setProducedTokenCount(int producedToken) {
        this.producedTokenCount = producedToken;
    }
    
    public double getTokenFitness() {
        if (consumedTokenCount > 0 && producedTokenCount > 0) {
            return 1.0*(1/2*(1-missingTokenCount/consumedTokenCount) + 1/2*(1-remainingTokenCount/producedTokenCount));
        }
        else {
            return 0;
        }
    }
    
    public double getTraceFitness() {
        return this.tracePercent;
    }
    
    public void setTraceFitness(double tracePercent) {
        this.tracePercent = tracePercent;
    }    
}