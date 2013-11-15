package org.apromore.plugin.metric.provider.impl;

import org.apromore.graph.canonical.Canonical;
import org.apromore.plugin.PluginRequest;
import org.apromore.plugin.metric.MetricPlugin;
import org.apromore.plugin.property.ParameterType;

import java.util.Set;

public class TestMetricProcessor implements MetricPlugin {

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public String getVersion() {
        return "0.1";
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getAuthor() {
        return null;
    }


    @Override
    public MetricPluginResult calculate(Canonical model, PluginRequest request) {
        return null;
    }

    @Override
    public Set<ParameterType<?>> getAvailableParameters() {
        return null;
    }

    @Override
    public Set<ParameterType<?>> getMandatoryParameters() {
        return null;
    }

    @Override
    public Set<ParameterType<?>> getOptionalParameters() {
        return null;
    }

    @Override
    public String getEMail() {
        return "";
    }

}