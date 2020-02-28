/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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

/**
 *
 */
package org.apromore.dao.model;

/**
 * The Clustering Summary.
 * @author <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
public class ClusteringSummary {

    protected int numClusters;
    protected int minClusterSize;
    protected int maxClusterSize;
    protected float minAvgFragmentSize;
    protected float maxAvgFragmentSize;
    protected double minBCR;
    protected double maxBCR;

    /**
     * Public Default Constructor.
     */
    public ClusteringSummary() { }

    /**
     * Public Default Constructor that sets up the Object.
     */
    public ClusteringSummary(Long numClusters, Integer minClusterSize, Integer maxClusterSize, Float minAvgFragSize, Float maxAvgFragSize,
            Double minBCR, Double maxBCR) {
        if (numClusters != null) {
            this.numClusters = numClusters.intValue();
        }
        this.minClusterSize = minClusterSize;
        this.maxClusterSize = maxClusterSize;
        this.minAvgFragmentSize = minAvgFragSize;
        this.maxAvgFragmentSize = maxAvgFragSize;
        this.minBCR = minBCR;
        this.maxBCR = maxBCR;
    }

    public float getMaxAvgFragmentSize() {
        return maxAvgFragmentSize;
    }

    public void setMaxAvgFragmentSize(float maxAvgFragmentSize) {
        this.maxAvgFragmentSize = maxAvgFragmentSize;
    }

    public int getNumClusters() {
        return numClusters;
    }

    public void setNumClusters(int numClusters) {
        this.numClusters = numClusters;
    }

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

    public float getMinAvgFragmentSize() {
        return minAvgFragmentSize;
    }

    public void setMinAvgFragmentSize(float minAvgFragmentSize) {
        this.minAvgFragmentSize = minAvgFragmentSize;
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
}
