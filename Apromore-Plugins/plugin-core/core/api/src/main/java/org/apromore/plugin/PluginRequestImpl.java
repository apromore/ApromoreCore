/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2013 Felix Mannhardt.
 * Copyright (C) 2014 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apromore.plugin.exception.PluginPropertyNotFoundException;
import org.apromore.plugin.property.ParameterType;
import org.apromore.plugin.property.RequestParameterType;

/**
 * Default implementation of the {@link PluginResult} interface providing management of {@link RequestParameterType} that are used by the consumer of a
 * Plugin.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class PluginRequestImpl implements PluginRequest {

    /**
     * Map of request properties by their ID
     */
    private Map<String, ParameterType<?>> requestProperties;

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.PluginRequest#getRequestProperty(org.apromore.plugin.property.PropertyType)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> ParameterType<T> getRequestParameter(final ParameterType<T> pluginProperty) throws PluginPropertyNotFoundException {
        initRequestProperties();
        ParameterType<?> propertyType = requestProperties.get(pluginProperty.getId());
        if (propertyType != null) {
            if (pluginProperty.getValueType().getClass().isInstance(propertyType.getValueType())) {
                return (ParameterType<T>) propertyType;
            } else {
                throw new IllegalArgumentException("Property types do not match " + pluginProperty.getValueType() + " and "
                        + propertyType.getValueType() + " for property with ID " + pluginProperty.getId());
            }
        } else {
            if (pluginProperty.isMandatory()) {
                throw new PluginPropertyNotFoundException("Mandatory property with ID " + pluginProperty.getId() + " not found!");
            } else {
                return pluginProperty;
            }
        }
    }

    /**
     * Add request property to current PluginRequest
     *
     * @param requestProperty
     */
    public void addRequestProperty(final RequestParameterType<?> requestProperty) {
        initRequestProperties();
        requestProperties.put(requestProperty.getId(), requestProperty);
    }

    /**
     * Adds all request properties to current PluginRequest
     *
     * @param requestProperties
     */
    public void addRequestProperty(final Set<RequestParameterType<?>> requestProperties) {
        for (RequestParameterType<?> requestProperty : requestProperties) {
            addRequestProperty(requestProperty);
        }
    }

    private void initRequestProperties() {
        if (requestProperties == null) {
            requestProperties = new HashMap<String, ParameterType<?>>();
        }
    }

}
