package org.apromore.plugin.metric.provider;

import java.util.Collection;

import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.plugin.metric.MetricPlugin;

public interface MetricPluginProvider {

    Collection<MetricPlugin> listAll();
    
    MetricPlugin findByName(String metricName) throws PluginNotFoundException;
    
    MetricPlugin findByNameAndVersion(String metricName, String version) throws PluginNotFoundException;

}
