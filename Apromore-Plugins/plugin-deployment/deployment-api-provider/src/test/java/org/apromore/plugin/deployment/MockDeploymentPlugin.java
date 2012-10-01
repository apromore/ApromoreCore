package org.apromore.plugin.deployment;

import java.util.List;
import java.util.Set;

import org.apromore.anf.AnnotationsType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.deployment.exception.DeploymentException;
import org.apromore.plugin.message.PluginMessage;
import org.apromore.plugin.property.PropertyType;

public class MockDeploymentPlugin implements DeploymentPlugin {

    @Override
    public List<PluginMessage> getPluginMessages() {
        return null;
    }

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
    public Set<PropertyType> getAvailableProperties() {
        return null;
    }

    @Override
    public Set<PropertyType> getMandatoryProperties() {
        return null;
    }

    @Override
    public String getNativeType() {
        return "YAWL 2.2";
    }

    @Override
    public void deployProcess(final CanonicalProcessType canonicalProcess) throws DeploymentException {

    }

    @Override
    public void deployProcess(final CanonicalProcessType canonicalProcess, final AnnotationsType annotation) throws DeploymentException {

    }

}
