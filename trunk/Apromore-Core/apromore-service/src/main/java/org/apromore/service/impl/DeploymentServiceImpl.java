package org.apromore.service.impl;

import java.util.List;
import java.util.Set;

import org.apromore.plugin.PluginRequest;
import org.apromore.plugin.PluginResult;
import org.apromore.plugin.deployment.DeploymentPlugin;
import org.apromore.plugin.deployment.exception.DeploymentException;
import org.apromore.plugin.deployment.provider.DeploymentPluginProvider;
import org.apromore.plugin.exception.PluginException;
import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.plugin.impl.DefaultPluginRequest;
import org.apromore.plugin.message.PluginMessage;
import org.apromore.plugin.property.PropertyType;
import org.apromore.plugin.property.RequestPropertyType;
import org.apromore.service.DeploymentService;
import org.apromore.service.model.CanonisedProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Felix
 *
 */
@Service("DeploymentService")
public class DeploymentServiceImpl implements DeploymentService {

    @Autowired
    @Qualifier("DeploymentProvider")
    private DeploymentPluginProvider deploymentProvider;

    @Override
    public DeploymentPlugin findDeploymentPlugins(final String nativeType) throws PluginNotFoundException {
        return deploymentProvider.findByNativeType(nativeType);
    }

    /* (non-Javadoc)
     * @see org.apromore.service.DeploymentService#deployProcess(java.lang.String, org.apromore.service.model.CanonisedProcess, java.util.Set)
     */
    @Override
    public List<PluginMessage> deployProcess(final String nativeType, final CanonisedProcess canonisedProcess, final Set<RequestPropertyType<?>> deploymentProperties) throws DeploymentException {
        try {
            DeploymentPlugin deploymentPlugin = deploymentProvider.findByNativeType(nativeType);
            DefaultPluginRequest pluginRequest = new DefaultPluginRequest();
            pluginRequest.addRequestProperty(deploymentProperties);
            PluginResult deployResult;
            if (canonisedProcess.getAnt() != null) {
                deployResult = deploymentPlugin.deployProcess(canonisedProcess.getCpt(), canonisedProcess.getAnt(), pluginRequest);
            } else {
                deployResult = deploymentPlugin.deployProcess(canonisedProcess.getCpt(), pluginRequest);
            }
            return deployResult.getPluginMessage();
        } catch (PluginNotFoundException e) {
            throw new DeploymentException("Deployment Plugin not found", e);
        } catch (PluginException e) {
            throw new DeploymentException("Error applying properties for Deployment Plugin", e);
        }
    }

    /**
     * Mainly for Spring Unit Tests
     *
     * @param deploymentProvider
     */
    public void setDeploymentProvider(final DeploymentPluginProvider deploymentProvider) {
        this.deploymentProvider = deploymentProvider;
    }


}