/**
 *  Copyright 2012, Felix Mannhardt 
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.apromore.plugin.provider;

import java.util.Collection;

import org.apromore.plugin.Plugin;
import org.apromore.plugin.exception.PluginNotFoundException;

/**
 * Interfaces used by Apromore to retrieve all currently installed plugins. Implementations of this interface usually should use OSGi
 * services (Gemini Blueprint) to pick up availables plugins automatically. Though it would also be possible to provide non-OSGi based
 * implementations.
 * 
 * @see http://www.eclipse.org/gemini/blueprint/
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University oAS)
 * 
 */
public interface PluginProvider {

	/**
	 * @return read-only collection of all currently available plugins
	 */
	public Collection<Plugin> listAll();

	/**
	 * @param type
	 *            of the plugins
	 * @return read-only collection of all plugins with specified type
	 */
	public Collection<Plugin> listByType(String type);

	/**
	 * @param name
	 *            of the plugins, usually the symbolic bundle name
	 * @return read-only collection of all plugins with specified name
	 */
	public Collection<Plugin> listByName(String name);

	/**
	 * @param name
	 *            of the plugins, usually the symbolic bundle name
	 * @return plugin with specified name and highest version if multiple
	 * @throws PluginNotFoundException in case no matching Plugin is found
	 */
	public Plugin findByName(String name) throws PluginNotFoundException;

	/**
	 * @param name
	 *            of the plugins, usually the symbolic bundle name
	 * @param version
	 *            of the plugins, usually the bundle version (MAJOR.MINOR.MICRO.QUALIFIER)
	 * @return plugin with specified name and version
	 * @throws PluginNotFoundException in case no matching Plugin is found
	 */
	public Plugin findByNameAndVersion(String name, String version) throws PluginNotFoundException;

}
