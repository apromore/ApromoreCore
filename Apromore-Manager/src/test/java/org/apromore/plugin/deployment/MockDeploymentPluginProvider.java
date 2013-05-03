package org.apromore.plugin.deployment;

import java.util.Set;

import org.apromore.anf.AnnotationsType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.PluginRequest;
import org.apromore.plugin.PluginResult;
import org.apromore.plugin.deployment.exception.DeploymentException;
import org.apromore.plugin.exception.PluginPropertyNotFoundException;
import org.apromore.plugin.property.ParameterType;

/**
 * Mock used to trick the tests that this class from the osgi context actually does exist.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class MockDeploymentPluginProvider implements DeploymentPlugin {

    @Override
    public String getNativeType() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PluginResult deployProcess(CanonicalProcessType canonicalProcess, PluginRequest request) throws DeploymentException, PluginPropertyNotFoundException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PluginResult deployProcess(CanonicalProcessType canonicalProcess, AnnotationsType annotation, PluginRequest request) throws DeploymentException, PluginPropertyNotFoundException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Set<ParameterType<?>> getAvailableParameters() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Set<ParameterType<?>> getMandatoryParameters() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Set<ParameterType<?>> getOptionalParameters() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getVersion() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getType() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getDescription() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getAuthor() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getEMail() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
