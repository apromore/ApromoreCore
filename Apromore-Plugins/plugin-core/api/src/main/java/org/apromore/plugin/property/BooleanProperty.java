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
package org.apromore.plugin.property;

/**
 * Property that holds a simple Boolean value. Just a convenience class.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class BooleanProperty extends DefaultProperty {

	/**
	 * Create a new BooleanProperty with given attributes and specify a default value.
	 *
	 * @param name
	 * @param description
	 * @param isMandatory
	 * @param defaultValue
	 */
	public BooleanProperty(final String name, final String description, final Boolean isMandatory, final Object defaultValue) {
		super(name, Boolean.class, description, isMandatory, defaultValue);
	}

	/**
	 * Create a new BooleanProperty with given attributes without specifying a default value. The value will be initialised with NULL.
	 *
	 * @param name
	 * @param description
	 * @param isMandatory
	 */
	public BooleanProperty(final String name, final String description, final Boolean isMandatory) {
		super(name, Boolean.class, description, isMandatory);
	}

	/**
	 * @return the Boolean value
	 */
	public Boolean getValueAsBoolean() {
		return (Boolean) getValue();
	}


}
