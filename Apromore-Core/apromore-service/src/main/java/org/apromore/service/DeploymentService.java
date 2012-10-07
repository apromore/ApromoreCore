package org.apromore.service;

import java.util.List;
import java.util.Set;

import org.apromore.plugin.deployment.DeploymentPlugin;
import org.apromore.plugin.deployment.exception.DeploymentException;
import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.plugin.message.PluginMessage;
import org.apromore.plugin.property.PropertyType;
import org.apromore.plugin.property.RequestPropertyType;
import org.apromore.service.model.CanonisedProcess;

/**
 * Web Service Interface for all Deployment Plugins
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public interface DeploymentService {


    DeploymentPlugin findDeploymentPlugins(String nativeType) throws PluginNotFoundException;

    /**
     * Deploys a process in canonical format to a process engine supporting the native type. It is up to the {@see DeploymentPlugin} to call any
     * {@see: Canoniser} for deCanonisation of the process. Use {@see PluginService#getMandatoryProperties(String, String)} to find the required
     * properties for installed {@see DeploymentPlugin}'s.
     *
     * @param nativeType
     *            the process engine supports
     * @param canonisedProcess
     *            the process to deploy
     * @param deploymentProperties
     *            parameters like location of the process engine
     * @return list of generated messages during deployment
     * @throws DeploymentException
     */
    List<PluginMessage> deployProcess(String nativeType, CanonisedProcess canonisedProcess, Set<RequestPropertyType<?>> deploymentProperties)
            throws DeploymentException;

}
