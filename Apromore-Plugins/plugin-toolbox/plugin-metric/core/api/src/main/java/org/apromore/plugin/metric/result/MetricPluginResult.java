package org.apromore.plugin.metric.result;

import org.apromore.plugin.PluginResultImpl;

public class MetricPluginResult extends PluginResultImpl {

    private Double metricResults;

    /**
     * Returns the calculation Result.
     * @return the calc result.
     */
    public Double getMetricResults() {
        return metricResults;
    }

    /**
     * Sets the calculation Result.
     * @param calc the calc result.
     */
    public void setMetricResults(Double calc) {
        metricResults = calc;
    }

}
