package au.edu.qut.processmining.ui;

/**
 * Created by Adriano on 14/06/2016.
 */
public class MinerUIResult {
    private boolean recurrentTasks;
    private boolean optionalTasks;
    private boolean inclusiveChoice;
    private boolean setUnbalancedPaths;
    private boolean applyCleaning;

    public boolean isOptionalTasks() { return optionalTasks; }
    public void setOptionalTasks(boolean optionalTasks) { this.optionalTasks = optionalTasks; }

    public boolean isInclusiveChoice() {
        return inclusiveChoice;
    }
    public void setInclusiveChoice(boolean inclusiveChoice) { this.inclusiveChoice = inclusiveChoice; }

    public boolean isRecurrentTasks() { return recurrentTasks; }
    public void setRecurrentTasks(boolean recurrentTasks) { this.recurrentTasks = recurrentTasks; }

    public boolean isUnbalancedPaths() { return setUnbalancedPaths; }
    public void setUnbalancedPaths(boolean setUnbalancedPaths) { this.setUnbalancedPaths = setUnbalancedPaths; }

    public boolean isApplyCleaning() { return applyCleaning; }
    public void setApplyCleaning(boolean applyCleaning) { this.applyCleaning = applyCleaning; }
}
