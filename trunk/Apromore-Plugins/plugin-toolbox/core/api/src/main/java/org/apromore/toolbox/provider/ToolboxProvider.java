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
package org.apromore.toolbox.provider;

import java.util.Set;

import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.toolbox.Toolbox;

/**
 * Toolbox API used by Apromore, to access the Toolbox Plugins
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface ToolboxProvider {

    /**
     * List all available Tools.
     * @return Set of Toolbox
     */
    Set<Toolbox> listAll();

    /**
     * List all available Tools converting the specified tool name.
     * @param toolName for example "Hungarian Similarity Search" or "Greedy Similarity Search"
     * @return Set of Tools
     */
    Set<Toolbox> listByToolName(String toolName);

    /**
     * List all available Tool converting the specified native type with the exact name. Please note there could be multiple versions installed,
     * so a Collection will be returned.
     * @param toolName  toolName for example "Hungarian Similarity Search" or "Greedy Similarity Search"
     * @param className usually the full class name of the Tool
     * @return Set of Tools
     */
    Set<Toolbox> listByToolNameAndName(String toolName, String className);

    /**
     * Return the first Tool that is found with the given parameters.
     * @param toolName for example "Hungarian Similarity Search" or "Greedy Similarity Search"
     * @return Toolbox for given native type
     * @throws org.apromore.plugin.exception.PluginNotFoundException in case there is no Tool found
     */
    Toolbox findByToolName(String toolName) throws PluginNotFoundException;

    /**
     * Return the first Tool that is found with the given parameters.
     * @param toolName   for example "Hungarian Similarity Search" or "Greedy Similarity Search"
     * @param className  usually the full class name of the Tool
     * @return Toolbox for given native type and name
     * @throws org.apromore.plugin.exception.PluginNotFoundException in case there is no Tool found
     */
    Toolbox findByToolNameAndName(String toolName, String className) throws PluginNotFoundException;

    /**
     * Return the first Tool that is found with the given parameters.
     * @param toolName   for example "Hungarian Similarity Search" or "Greedy Similarity Search"
     * @param name       usually the full class name of the Canoniser
     * @param version    usually the bundle version (X.Y.Z-CLASSIFIER)
     * @return Toolbox for given native type and name and version
     * @throws org.apromore.plugin.exception.PluginNotFoundException in case there is no Tool found
     */
    Toolbox findByToolNameAndNameAndVersion(String toolName, String name, String version) throws PluginNotFoundException;

}
