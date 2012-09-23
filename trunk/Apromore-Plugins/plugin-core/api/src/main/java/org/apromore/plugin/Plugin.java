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
package org.apromore.plugin;

/**
 * Interface implemented by every Apromore plugin. Each plugin offers theses methods, therefore it is possible to handle all plugins in a
 * generic manner.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public interface Plugin {

	/**
	 * Plugin name should be short and unique together with the version. The symbolic name of the OSGi bundle should be used.
	 *
	 * @return name of the plugin
	 */
	String getName();

	/**
	 * Plugin version should be kept in sync with the OSGi bundle version.
	 *
	 * @return version of the plugin
	 */
	String getVersion();

	/**
	 * Plugin type can be used to group plugins that provide similar functionality.
	 *
	 * @return type of the plugin
	 */
	String getType();

	/**
	 * Plugin description can be used to inform users about the functionality of a plugin.
	 *
	 * @return description of the plugin
	 */
	String getDescription();
}
