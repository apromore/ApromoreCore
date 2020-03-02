/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

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
