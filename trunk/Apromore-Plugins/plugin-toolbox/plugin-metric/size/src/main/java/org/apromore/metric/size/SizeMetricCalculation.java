package org.apromore.metric.size;

import org.apromore.graph.canonical.Canonical;
import org.apromore.plugin.PluginRequest;
import org.apromore.plugin.metric.DefaultAbstractMetricProcessor;
import org.apromore.plugin.metric.result.MetricPluginResult;
import org.springframework.stereotype.Component;

/**
 * Size Metric Calculation.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Component("sizeMetricCalculation")
public class SizeMetricCalculation extends DefaultAbstractMetricProcessor {

    @Override
    public MetricPluginResult calculate(Canonical model, PluginRequest request) {
        MetricPluginResult result = new MetricPluginResult();
        result.setMetricResults((double) model.getNodes().size());
        return result;
    }
}
