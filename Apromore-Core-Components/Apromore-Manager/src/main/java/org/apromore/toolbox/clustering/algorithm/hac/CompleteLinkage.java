/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.toolbox.clustering.algorithm.hac;

import org.apromore.toolbox.clustering.containment.ContainmentRelation;
import org.apromore.toolbox.clustering.dissimilarity.DissimilarityMatrix;

public class CompleteLinkage extends HierarchicalAgglomerativeClustering {
    private double proximity = -1;

    public CompleteLinkage(ContainmentRelation crel, DissimilarityMatrix dmatrix) throws Exception {
        super(crel, dmatrix);
    }

    protected void resetProximityValue() {
        proximity = -1;
    }

    protected void updateProximityValue(double newValue) {
        proximity = Math.max(proximity, newValue);
    }

    protected boolean isItAValidProximityValue() {
        return proximity >= 0;
    }

    protected double getProximityValue() {
        return proximity;
    }

}
