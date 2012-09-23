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

import java.util.Set;

import org.apromore.plugin.property.PropertyType;

/**
 * A PropertyAwarePlugin defines a Set of properties that can or must be provided by the caller for proper operation of the Plugin.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public interface PropertyAwarePlugin extends Plugin {

	/**
	 * Returns the Set of all available properties for this Plugin.
	 *
	 * @return Set of property names
	 */
	Set<PropertyType> getAvailableProperties();

	/**
	 * Returns the Set of all required properties for this Plugin.
	 *
	 * @return Set of required property names
	 */
	Set<PropertyType> getMandatoryProperties();

}
