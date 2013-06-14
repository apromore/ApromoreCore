package org.apromore.toolbox.clustering.dissimilarity;

/**
 * The Dissimilarity Matrix Interface.
 */
public interface DissimilarityMatrix {

    public static final Double GED_THRESHOLD = 0.45D;
    public static final Double LED_CUTOFF = 0.6D;
    public static final int LARGE_FRAGMENTS = 60;
    public static final int SMALL_FRAGMENTS = 6;

    /**
     * Computer the Dissimilarity matrix for the repo.
     */
    void computeDissimilarity();

    /**
     * Returns the Dissimilarity of two fragments.
     * @param index1 fragment one
     * @param index2 fragment two
     * @return the resulting dissimilarity value
     */
    Double getDissimilarity(Integer index1, Integer index2);

    /**
     * Add a Dissimilarity Calculator to help with the process.
     * @param calc a calculator that implements the DissimilarityCalc.
     */
    void addDissimCalc(DissimilarityCalc calc);

    /**
     * Add a GED Calculator to help with the process.
     * @param calc a calculator that implements the GEDMatrixCalc.
     */
    void addGedCalc(GEDMatrixCalc calc);

    /**
     * Sets the dissimilarity threshold. within this threshold we store the value. outside we disregard.
     * @param dissThreshold the threshold.
     */
    void setDissThreshold(double dissThreshold);

}
