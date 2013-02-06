package org.apromore.plugin.deployment;

import java.util.Set;

import org.apromore.anf.AnnotationsType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.PluginRequest;
import org.apromore.plugin.PluginResult;
import org.apromore.plugin.deployment.exception.DeploymentException;
import org.apromore.plugin.property.ParameterType;

public class MockDeploymentPlugin implements DeploymentPlugin {

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String getType() {
        return "Deployment";
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
    public String getNativeType() {
        return "YAWL 2.2";
    }


    @Override
    public PluginResult deployProcess(final CanonicalProcessType canonicalProcess, final PluginRequest request) throws DeploymentException {
        return null;
    }

    @Override
    public PluginResult deployProcess(final CanonicalProcessType canonicalProcess, final AnnotationsType annotation, final PluginRequest request)
            throws DeploymentException {
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
        return "test@test.com";
    }



}
