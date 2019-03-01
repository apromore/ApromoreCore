package au.edu.qut.processmining.miners.splitminer.ui.dfgp;

import static au.edu.qut.processmining.miners.splitminer.ui.dfgp.DFGPUIResult.FilterType.WTH;

/**
 * Created by Adriano on 23/01/2017.
 */
public class DFGPUIResult {
    public enum FilterType{STD, NOF, FWG, WTH}

    public static final double FREQUENCY_THRESHOLD = 0.40;
    public static final double PARALLELISMS_THRESHOLD = 0.10;
    public static final FilterType STD_FILTER = WTH;
    public static final boolean PARALLELISMS_FIRST = false;

    private double percentileFrequencyThreshold;
    private double parallelismsThreshold;
    private FilterType filterType;
    private boolean parallelismsFirst;

    public DFGPUIResult() {
        percentileFrequencyThreshold = FREQUENCY_THRESHOLD;
        parallelismsThreshold = PARALLELISMS_THRESHOLD;
        filterType = STD_FILTER;
        parallelismsFirst = PARALLELISMS_FIRST;
    }

    public boolean isParallelismsFirst() { return parallelismsFirst; }
    public void setParallelismsFirst(boolean parallelismsFirst) { this.parallelismsFirst = parallelismsFirst; }

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
