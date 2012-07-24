/**
 *
 */
package org.apromore.service.model;

/**
 * @author Chathura C. Ekanayake
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
