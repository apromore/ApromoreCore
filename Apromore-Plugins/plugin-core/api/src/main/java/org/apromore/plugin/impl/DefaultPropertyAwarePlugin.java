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
package org.apromore.plugin.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apromore.plugin.PropertyAwarePlugin;
import org.apromore.plugin.property.PluginPropertyType;
import org.apromore.plugin.property.PropertyType;

/**
 * Default implementation of an Apromore Plugin that allows for runtime parameters. If your Plugin should be configurable by the User at runtime, then
 * you should inherit this class and register your parameters using {@see DefaultPropertyAwarePlugin#registerProperty(PropertyType)} in the Constructor.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public abstract class DefaultPropertyAwarePlugin extends DefaultPlugin implements PropertyAwarePlugin {

    /**
     * Stores all properties
     */
    private final Map<String, PropertyType<?>> availableProperties;

    /**
     * Creates a DefaultPropertyAwarePlugin
     */
    public DefaultPropertyAwarePlugin() {
        super();
        availableProperties = new HashMap<String, PropertyType<?>>();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.PropertyAwarePlugin#getMandatoryProperties()
     */
    @Override
    public Set<PropertyType<?>> getMandatoryProperties() {
        Set<PropertyType<?>> mandatoryProperties = new HashSet<PropertyType<?>>();
        for (PropertyType<?> property : getAvailableProperties()) {
            if (property.isMandatory()) {
                mandatoryProperties.add(property);
            }
        }
        return Collections.unmodifiableSet(mandatoryProperties);
    }


    /* (non-Javadoc)
     * @see org.apromore.plugin.PropertyAwarePlugin#getOptionalProperties()
     */
    @Override
    public Set<PropertyType<?>> getOptionalProperties() {
        Set<PropertyType<?>> optionalProperties = new HashSet<PropertyType<?>>();
        for (PropertyType<?> property : getAvailableProperties()) {
            if (!property.isMandatory()) {
                optionalProperties.add(property);
            }
        }
        return Collections.unmodifiableSet(optionalProperties);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.PropertyAwarePlugin#getAvailableProperties()
     */
    @Override
    public Set<PropertyType<?>> getAvailableProperties() {
        Set<PropertyType<?>> allProperties = new HashSet<PropertyType<?>>();
        for (Entry<String, PropertyType<?>> property : availableProperties.entrySet()) {
            allProperties.add(property.getValue());
        }
        return Collections.unmodifiableSet(allProperties);
    }



    /**
     * Adds a property to the list of available properties.
     *
     * @param property
     *            to be added
     * @return true if property was added, false if property was already available or NULL
     */
    protected boolean registerProperty(final PluginPropertyType<?> property) {
        if (property != null) {
            return this.availableProperties.put(property.getId(), property) == null;
        } else {
            return false;
        }
    }

}
