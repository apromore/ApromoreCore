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
 * Property that holds a simple String value. Just a convenience class.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class StringProperty extends DefaultProperty {


    /**
     * Create a new StringProperty with given attributes without specifying a default value. The value will be initialised with NULL.
     *
     * @param id of the property
     * @param name of the property
     * @param description of the property
     * @param isMandatory true if Plugin requires this property to work properly
     */
    public StringProperty(final String id, final String name, final String description, final Boolean isMandatory) {
        super(id, name, String.class, description, isMandatory);
    }

    /**
     * Create a new StringProperty with given attributes without specifying a default value. The value will be initialised with NULL.
     *
     * @param name of the property
     * @param description of the property
     * @param isMandatory true if Plugin requires this property to work properly
     * @param defaultValue of the property
     */
    public StringProperty(final String id, final String name, final String description, final Boolean isMandatory, final Object defaultValue) {
        super(id, name, String.class, description, isMandatory, defaultValue);
    }


    /**
     * @return the Boolean value
     */
    public String getValueAsString() {
        return (String) getValue();
    }

}
