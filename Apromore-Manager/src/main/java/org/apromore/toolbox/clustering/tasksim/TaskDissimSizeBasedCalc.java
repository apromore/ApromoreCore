package org.apromore.toolbox.clustering.tasksim;

import org.apromore.toolbox.clustering.dissimilarity.DissimilarityCalc;
import org.apromore.toolbox.clustering.dissimilarity.model.SimpleGraph;

public class TaskDissimSizeBasedCalc implements DissimilarityCalc {

    private double maxDistance = 0.4;
    private double minSim = 0.6;
    private boolean includeGateways = false;

    public TaskDissimSizeBasedCalc(double maxDistace) {
        this.maxDistance = maxDistace;
        this.minSim = 1d - maxDistace;
    }

    public void setIncludeGateways(boolean includeGateways) {
        this.includeGateways = includeGateways;
    }


    @Override
    public String getName() {
        return "TaskDissimSizeBasedCalc";
    }

    @Override
    public double compute(SimpleGraph sg1, SimpleGraph sg2) {
        int n1 = sg1.getFunctions().size();
        n1 += sg1.getEvents().size();
        if (includeGateways) {
            n1 += sg1.getConnectors().size();
        }

        int n2 = sg2.getFunctions().size();
        n2 += sg2.getEvents().size();
        if (includeGateways) {
            n2 += sg2.getConnectors().size();
        }

        int m = Math.min(n1, n2);
        double sim = 2d * m / (n1 + n2);

        return 1d - sim;
    }

    @Override
    public boolean isAboveThreshold(double disim) {
        // TODO Auto-generated method stub
        return false;
    }

}
