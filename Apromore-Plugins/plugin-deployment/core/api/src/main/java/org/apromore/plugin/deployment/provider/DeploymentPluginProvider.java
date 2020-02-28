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
package org.apromore.plugin.deployment.provider;

import java.util.Set;

import org.apromore.plugin.deployment.DeploymentPlugin;
import org.apromore.plugin.exception.PluginNotFoundException;

/**
 * The Provider interface for your Plugins.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 *
 */
public interface DeploymentPluginProvider {

    /**
     * List all available Deployment Plugins
     *
     * @return Collection of DeploymentPlugin
     */
    Set<DeploymentPlugin> listAll();

    /**
     * List all available Deployment Plugins supporting native type
     *
     * @param nativeType
     *            of the process to be deployed
     * @return Collection of DeploymentPlugin
     */
    Set<DeploymentPlugin> listByNativeType(String nativeType);

    /**
     * Finds a special Deployment Plugin by its Name and Version
     *
     * @param nativeType
     *            of the process to be deployed
     * @param name
     *            of the Deployment Plugin
     * @param version
     *            of the Deployment Plugin
     * @return DeploymentPlugin
     */
    DeploymentPlugin findByNativeTypeAndNameAndVersion(String nativeType, String name, String version) throws PluginNotFoundException;

    /**
     * Returns the first available Deployment Plugin that supports the native type.
     *
     * @param nativeType
     *            of the process to be deployed
     * @return DeploymentPlugin that supports deploying native type
     * @throws PluginNotFoundException
     *             in case no Plugin is found
     */
    DeploymentPlugin findByNativeType(String nativeType) throws PluginNotFoundException;

}