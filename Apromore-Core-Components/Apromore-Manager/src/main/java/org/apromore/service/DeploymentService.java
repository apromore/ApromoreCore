/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.service;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.Canoniser;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.deployment.DeploymentPlugin;
import org.apromore.plugin.deployment.exception.DeploymentException;
import org.apromore.plugin.message.PluginMessage;
import org.apromore.plugin.property.RequestParameterType;

import java.util.List;
import java.util.Set;

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
