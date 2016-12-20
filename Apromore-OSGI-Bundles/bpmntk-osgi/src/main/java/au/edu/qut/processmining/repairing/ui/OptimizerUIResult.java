package au.edu.qut.processmining.repairing.ui;

/**
 * Created by Adriano on 14/06/2016.
 */
public class OptimizerUIResult {
    private boolean recurrentActivities;
    private boolean optionalActivities;
    private boolean inclusiveChoice;
    private boolean setUnbalancedPaths;
    private boolean applyCleaning;

    public boolean isOptionalActivities() { return optionalActivities; }
    public void setOptionalActivities(boolean optionalActivities) { this.optionalActivities = optionalActivities; }

    public boolean isInclusiveChoice() {
        return inclusiveChoice;
    }
    public void setInclusiveChoice(boolean inclusiveChoice) { this.inclusiveChoice = inclusiveChoice; }

    public boolean isRecurrentActivities() { return recurrentActivities; }
    public void setRecurrentActivities(boolean recurrentActivities) { this.recurrentActivities = recurrentActivities; }

    public boolean isUnbalancedPaths() { return setUnbalancedPaths; }
    public void setUnbalancedPaths(boolean setUnbalancedPaths) { this.setUnbalancedPaths = setUnbalancedPaths; }

    public boolean isApplyCleaning() { return applyCleaning; }
    public void setApplyCleaning(boolean applyCleaning) { this.applyCleaning = applyCleaning; }
}
