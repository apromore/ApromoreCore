/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 Felix Mannhardt.
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
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
package org.apromore.plugin.deployment;

import org.apromore.anf.AnnotationsType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.ParameterAwarePlugin;
import org.apromore.plugin.PluginRequest;
import org.apromore.plugin.PluginResult;
import org.apromore.plugin.deployment.exception.DeploymentException;
import org.apromore.plugin.exception.PluginPropertyNotFoundException;

/**
 * Interface for Deployment Plugins, that support deploying processes to a process/work-flow engine.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 */
public interface DeploymentPlugin extends ParameterAwarePlugin {

    /**
     * Native type the Deployment Plugin uses
     *
     * @return native type the process will be deployed to
     */
    String getNativeType();

    /**
     * Deploys the process in canonical process format to a process/work-flow engine.
     *
     * @param canonicalProcess the process to be deployed
     * @throws DeploymentException in case of an error during deployment
     * @throws PluginPropertyNotFoundException
     *                             in case a mandatory property was not found
     */
    PluginResult deployProcess(CanonicalProcessType canonicalProcess, PluginRequest request) throws DeploymentException, PluginPropertyNotFoundException;

    /**
     * Deploys the process in canonical process format to a process/work-flow engine.
     *
     * @param canonicalProcess the process to be deployed
     * @param annotation       to be used during deployment
     * @throws DeploymentException in case of an error during deployment
     * @throws PluginPropertyNotFoundException
     *                             in case a mandatory property was not found
     */
    PluginResult deployProcess(CanonicalProcessType canonicalProcess, AnnotationsType annotation, PluginRequest request) throws DeploymentException, PluginPropertyNotFoundException;

}
