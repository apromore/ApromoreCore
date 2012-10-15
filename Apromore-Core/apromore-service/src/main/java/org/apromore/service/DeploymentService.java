package org.apromore.service;

import java.util.List;
import java.util.Set;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.Canoniser;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.deployment.DeploymentPlugin;
import org.apromore.plugin.deployment.exception.DeploymentException;
import org.apromore.plugin.message.PluginMessage;
import org.apromore.plugin.property.RequestParameterType;

/**
 * Interface to access Deployment Plugins
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public interface DeploymentService {

    /**
     * List all available Deployment Plugins for the specified native type
     *
     * @param nativeType
     *            to be deployed
     * @return installed deployment plugins for requested native type
     */
    Set<DeploymentPlugin> listDeploymentPlugin(String nativeType);

    /**
     * Deploys a process in canonical format to a process engine supporting the native type. It is up to the {@link DeploymentPlugin} to call any
     * {@link Canoniser} for deCanonisation of the process. Use {@link PluginService#getMandatoryProperties(String, String)} to find the required
     * properties for installed {@link DeploymentPlugin}'s.
     *
     * @param nativeType
     *            the process engine supports
     * @param cpf
     *            the process to deploy
     * @param anf
     *            annotation to use
     * @param deploymentProperties
     *            parameters like location of the process engine
     * @return list of generated messages during deployment
     * @throws DeploymentException
     *             in case of an error during deployment
     */
    List<PluginMessage> deployProcess(String nativeType, CanonicalProcessType cpf, AnnotationsType anf,
            Set<RequestParameterType<?>> deploymentProperties) throws DeploymentException;

    /**
     * Deploys a process in canonical format to a process engine supporting the native type. It is up to the {@link DeploymentPlugin} to call any
     * {@link Canoniser} for deCanonisation of the process. Use {@link PluginService#getMandatoryProperties(String, String)} to find the required
     * properties for installed {@link DeploymentPlugin}'s.
     *
     * @param nativeType
     *            the process engine supports
     * @param pluginName
     *            of the deployment plugin to be used
     * @param pluginVersion
     *            of the deployment plugin to used
     * @param cpf
     *            the process to deploy
     * @param anf
     *            annotation to use
     * @param deploymentProperties
     *            parameters like location of the process engine
     * @return ist of generated messages during deployment
     * @throws DeploymentException
     *             in case of an error during deployment
     */
    List<PluginMessage> deployProcess(String nativeType, String pluginName, String pluginVersion, CanonicalProcessType cpf, AnnotationsType anf,
            Set<RequestParameterType<?>> deploymentProperties) throws DeploymentException;

}
