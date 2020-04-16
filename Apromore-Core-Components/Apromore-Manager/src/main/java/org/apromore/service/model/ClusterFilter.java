/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
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

/**
 *
 */
package org.apromore.service.model;

/**
 * <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
public class ClusterFilter {

    private int minClusterSize = 0;
    private int maxClusterSize = Integer.MAX_VALUE;

    private double minBCR = 0;
    private double maxBCR = Double.MAX_VALUE;

    private float minAverageFragmentSize = 0;
    private float maxAverageFragmentSize = Integer.MAX_VALUE;

    public int getMinClusterSize() {
        return minClusterSize;
    }

    public void setMinClusterSize(int minClusterSize) {
        this.minClusterSize = minClusterSize;
    }

    public int getMaxClusterSize() {
        return maxClusterSize;
    }

    public void setMaxClusterSize(int maxClusterSize) {
        this.maxClusterSize = maxClusterSize;
    }

    public double getMinBCR() {
        return minBCR;
    }

    public void setMinBCR(double minBCR) {
        this.minBCR = minBCR;
    }

    public double getMaxBCR() {
        return maxBCR;
    }

    public void setMaxBCR(double maxBCR) {
        this.maxBCR = maxBCR;
    }

    public float getMinAverageFragmentSize() {
        return minAverageFragmentSize;
    }

    public void setMinAverageFragmentSize(float minAverageFragmentSize) {
        this.minAverageFragmentSize = minAverageFragmentSize;
    }

    public float getMaxAverageFragmentSize() {
        return maxAverageFragmentSize;
    }

    public void setMaxAverageFragmentSize(float maxAverageFragmentSize) {
        this.maxAverageFragmentSize = maxAverageFragmentSize;
    }
}
