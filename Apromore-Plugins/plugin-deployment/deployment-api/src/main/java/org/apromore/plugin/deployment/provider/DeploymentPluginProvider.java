/**
 * Copyright 2012, Felix Mannhardt
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.apromore.plugin.deployment.provider;

import java.util.Collection;

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
    Collection<DeploymentPlugin> listAll();

    /**
     * Finds a special Deployment Plugin by its Name
     *
     * @param name of the Deployment Plugin
     * @return DeploymentPlugin
     */
    DeploymentPlugin findByName(String name)  throws PluginNotFoundException;

	/**
	 *  Finds a special Deployment Plugin by its supported native type.
	 *
	 * @param nativeType of the process to be deployed
	 * @return DeploymentPlugin
	 * @throws PluginNotFoundException
	 */
	DeploymentPlugin findByNativeType(String nativeType) throws PluginNotFoundException;

}