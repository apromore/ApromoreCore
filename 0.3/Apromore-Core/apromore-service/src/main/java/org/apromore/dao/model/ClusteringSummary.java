/**
 *
 */
package org.apromore.dao.model;

/**
 * @author Chathura C. Ekanayake
 */
public class ClusteringSummary {

    protected int numClusters;
    protected int minClusterSize;
    protected int maxClusterSize;
    protected float minAvgFragmentSize;
    protected float maxAvgFragmentSize;
    protected double minBCR;
    protected double maxBCR;

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
