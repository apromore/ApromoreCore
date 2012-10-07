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
 * <p>
 * Defines a single Property of a Plugin. Contains both meta data (name, description, ..) and the value of the Property. Subclasses MUST provide a
 * valid {@see Object#equals(Object)} and {@see Object#hashCode()) implementation, see {@see DefaultProperty} for an example.
 *
 * <p>
 * Please not you should only use basic Java classes as properties, as properties often have to be converted to XML during Web Service invocation.
 * {@see: InputStream} and {@see: OutputStream} are OK to use!
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 * @param <T> type of Property
 */
public interface PropertyType<T> {

    /**
     * Identifier of the PropertyType. Two properties with the same identifier are "equal"!
     *
     * @return a unique identifier (across the Plugin) for this property
     */
    String getId();

    /**
     * @return the human readable name of this property (may be presented in UI)
     */
    String getName();

    /**
     * @return the class of the properties value
     */
    Class<T> getValueType();

    /**
     * @return if the property is required for the Plugin to work
     */
    boolean isMandatory();

    /**
     * @return the human readable description of this property (may be presented in UI)
     */
    String getDescription();

    /**
     * @return the actual value, which always will be of the specified value type
     */
    T getValue();

    /**
     * @param value
     */
    void setValue(T value);

    /**
     * @return false if the value is NULL, true otherwise
     */
    boolean hasValue();
}
