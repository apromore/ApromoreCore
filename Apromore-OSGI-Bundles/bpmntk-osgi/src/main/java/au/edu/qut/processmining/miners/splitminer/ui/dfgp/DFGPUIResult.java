package au.edu.qut.processmining.miners.splitminer.ui.dfgp;

/**
 * Created by Adriano on 23/01/2017.
 */
public class DFGPUIResult {

    public static final double FREQUENCY_THRESHOLD = 0.33;
    public static final double PARALLELISMS_THRESHOLD = 0.10;

    private double percentileFrequencyThreshold;
    private double parallelismsThreshold;
    private FilterType filterType;

    public enum FilterType{STD, GUB, LPS, WTH}

    public DFGPUIResult() {
        percentileFrequencyThreshold = FREQUENCY_THRESHOLD;
        parallelismsThreshold = PARALLELISMS_THRESHOLD;
        filterType = FilterType.STD;
    }

    public FilterType getFilterType() { return filterType; }
    public void setFilterType(FilterType filterType) { this.filterType = filterType; }

    public double getPercentileFrequencyThreshold() {
        return percentileFrequencyThreshold;
    }
    public void setPercentileFrequencyThreshold(double percentileFrequencyThreshold) {
        this.percentileFrequencyThreshold = percentileFrequencyThreshold;
    }

    public double getParallelismsThreshold() {
        return parallelismsThreshold;
    }
    public void setParallelismsThreshold(double parallelismsThreshold) {
        this.parallelismsThreshold = parallelismsThreshold;
    }
}
