package org.apromore.plugin.metric;

import org.apromore.graph.canonical.Canonical;
import org.apromore.plugin.ParameterAwarePlugin;
import org.apromore.plugin.PluginRequest;
import org.apromore.plugin.metric.result.MetricPluginResult;

public interface MetricPlugin extends ParameterAwarePlugin {

    MetricPluginResult calculate(Canonical model, PluginRequest request);

}
