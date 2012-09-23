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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apromore.plugin.property.PropertyType;


/**
 * Abstract default implementation of handling the properties.
 *
 * @author <a href="felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public abstract class AbstractPropertyAwarePlugin implements PropertyAwarePlugin {

	private final Set<PropertyType> availableProperties;

	public AbstractPropertyAwarePlugin() {
		super();
		availableProperties = new HashSet<PropertyType>(0);
	}

	/* (non-Javadoc)
	 * @see org.apromore.plugin.PropertyAwarePlugin#getMandatoryProperties()
	 */
	@Override
	public Set<PropertyType> getMandatoryProperties() {
		Set<PropertyType> mandatoryProperties = new HashSet<PropertyType>();
		for (PropertyType property: getAvailableProperties()) {
			if (property.isMandatory()) {
				mandatoryProperties.add(property);
			}
		}
		return Collections.unmodifiableSet(mandatoryProperties);
	}

	/* (non-Javadoc)
	 * @see org.apromore.plugin.PropertyAwarePlugin#getAvailableProperties()
	 */
	@Override
	public Set<PropertyType> getAvailableProperties() {
		return Collections.unmodifiableSet(availableProperties);
	}

	/**
	 * Add a property to our list of available properties.
	 *
	 * @param property to be added
	 * @return true if property was added, false if property was already available or NULL
	 */
	protected boolean addProperty(final PropertyType property) {
	    if (property != null) {
	        return this.availableProperties.add(property);
	    } else {
	        return false;
	    }
	}


}
