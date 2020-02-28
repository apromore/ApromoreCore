/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.service.impl;

import org.apromore.anf.AnnotationsType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.PluginRequestImpl;
import org.apromore.plugin.PluginResult;
import org.apromore.plugin.deployment.DeploymentPlugin;
import org.apromore.plugin.deployment.exception.DeploymentException;
import org.apromore.plugin.deployment.provider.DeploymentPluginProvider;
import org.apromore.plugin.exception.PluginException;
import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.plugin.message.PluginMessage;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.service.DeploymentService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import javax.inject.Inject;

/**
 * Implementation of the Interface to access Deployment Plugins
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
@Service
public class DeploymentServiceImpl implements DeploymentService {

    private DeploymentPluginProvider pluginProvider;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param deploymentPluginProvider Annotation Repository.
     */
    @Inject
    public DeploymentServiceImpl(final @Qualifier("deploymentProvider") DeploymentPluginProvider deploymentPluginProvider) {
        pluginProvider = deploymentPluginProvider;
    }


    /*
     * (non-Javadoc)
     *
     * @see org.apromore.service.DeploymentService#listDeploymentPlugin(java.lang.String)
     */
    @Override
    public Set<DeploymentPlugin> listDeploymentPlugin(final String nativeType) {
        return pluginProvider.listByNativeType(nativeType);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.service.DeploymentService#deployProcess(java.lang.String, org.apromore.service.model.CanonisedProcess, java.util.Set)
     */
    @Override
    public List<PluginMessage> deployProcess(final String nativeType, final CanonicalProcessType cpf, final AnnotationsType anf,
            final Set<RequestParameterType<?>> deploymentProperties) throws DeploymentException {
        try {
            DeploymentPlugin deploymentPlugin = pluginProvider.findByNativeType(nativeType);
            return deployProcess(cpf, anf, deploymentProperties, deploymentPlugin);
        } catch (PluginNotFoundException e) {
            throw new DeploymentException("Deployment Plugin not found", e);
        } catch (PluginException e) {
            throw new DeploymentException("Error applying properties for Deployment Plugin", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.service.DeploymentService#deployProcess(java.lang.String, java.lang.String, java.lang.String,
     * org.apromore.service.model.CanonisedProcess, java.util.Set)
     */
    @Override
    public List<PluginMessage> deployProcess(final String nativeType, final String pluginName, final String pluginVersion,
            final CanonicalProcessType cpf, final AnnotationsType anf, final Set<RequestParameterType<?>> deploymentProperties)
            throws DeploymentException {
        try {
            DeploymentPlugin deploymentPlugin = pluginProvider.findByNativeTypeAndNameAndVersion(nativeType, pluginName, pluginVersion);
            return deployProcess(cpf, null, deploymentProperties, deploymentPlugin);
        } catch (PluginNotFoundException e) {
            throw new DeploymentException("Deployment Plugin not found", e);
        } catch (DeploymentException e) {
            throw new DeploymentException("Error deploying process", e);
        } catch (PluginException e) {
            throw new DeploymentException("Unkown error.", e);
        }
    }

    private static List<PluginMessage> deployProcess(final CanonicalProcessType cpf, final AnnotationsType anf,
            final Set<RequestParameterType<?>> deploymentProperties, final DeploymentPlugin deploymentPlugin) throws DeploymentException,
            PluginException {
        PluginRequestImpl pluginRequest = new PluginRequestImpl();
        pluginRequest.addRequestProperty(deploymentProperties);
        PluginResult deployResult;
        if (anf != null) {
            deployResult = deploymentPlugin.deployProcess(cpf, anf, pluginRequest);
        } else {
            deployResult = deploymentPlugin.deployProcess(cpf, pluginRequest);
        }
        return deployResult.getPluginMessage();
    }


}