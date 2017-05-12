package au.edu.qut.processmining.miners.splitminer.ui.dfgp;

/**
 * Created by Adriano on 23/01/2017.
 */
public class DFGPUIResult {

    public static final double FREQUENCY_THRESHOLD = 1.0;
    public static final double PARALLELISMS_THRESHOLD = 0.10;

    private double frequencyThreshold;
    private double parallelismsThreshold;
    private FilterType filterType;

    public enum FilterType{STD, GUB, LPS, WTH}

    public DFGPUIResult() {
        frequencyThreshold = FREQUENCY_THRESHOLD;
        parallelismsThreshold = PARALLELISMS_THRESHOLD;
        filterType = FilterType.STD;
    }

    public FilterType getFilterType() { return filterType; }
    public void setFilterType(FilterType filterType) { this.filterType = filterType; }

    public double getFrequencyThreshold() {
        return frequencyThreshold;
    }
    public void setFrequencyThreshold(double frequencyThreshold) {
        this.frequencyThreshold = frequencyThreshold;
    }

    public double getParallelismsThreshold() {
        return parallelismsThreshold;
    }
    public void setParallelismsThreshold(double parallelismsThreshold) {
        this.parallelismsThreshold = parallelismsThreshold;
    }
}
