package org.apromore.toolbox.provider.impl;

import java.util.Set;

import org.apromore.plugin.property.ParameterType;
import org.apromore.toolbox.Toolbox;

public class TestToolbox implements Toolbox {

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
    public String getEMail() {
        return null;
    }

    @Override
    public String getToolName() {
        return "testType";
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
}