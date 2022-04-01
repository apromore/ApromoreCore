/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2013 Felix Mannhardt.
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apromore.plugin.property.ParameterType;
import org.apromore.plugin.property.PluginParameterType;

/**
 * Default implementation of an Apromore Plugin that allows for runtime parameters. If your Plugin should be configurable by the User at runtime, then
 * you should inherit this class and register your parameters using {@link DefaultParameterAwarePlugin#registerParameter(PluginParameterType)} in the
 * Constructor.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public abstract class DefaultParameterAwarePlugin extends DefaultPlugin implements ParameterAwarePlugin {

    /**
     * Stores all properties
     */
    private final Map<String, ParameterType<?>> availableParameters;

    /**
     * Creates a DefaultParameterAwarePlugin
     */
    public DefaultParameterAwarePlugin() {
        super();
        availableParameters = new HashMap<>();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.PropertyAwarePlugin#getMandatoryProperties()
     */
    @Override
    public Set<ParameterType<?>> getMandatoryParameters() {
        Set<ParameterType<?>> mandatoryParams = new HashSet<>();
        for (ParameterType<?> param : getAvailableParameters()) {
            if (param.isMandatory()) {
                mandatoryParams.add(param);
            }
        }
        return Collections.unmodifiableSet(mandatoryParams);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.PropertyAwarePlugin#getOptionalProperties()
     */
    @Override
    public Set<ParameterType<?>> getOptionalParameters() {
        Set<ParameterType<?>> optionalParam = new HashSet<>();
        for (ParameterType<?> param : getAvailableParameters()) {
            if (!param.isMandatory()) {
                optionalParam.add(param);
            }
        }
        return Collections.unmodifiableSet(optionalParam);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.PropertyAwarePlugin#getAvailableProperties()
     */
    @Override
    public Set<ParameterType<?>> getAvailableParameters() {
        Set<ParameterType<?>> allParams = new HashSet<>();
        for (Entry<String, ParameterType<?>> param : availableParameters.entrySet()) {
            allParams.add(param.getValue());
        }
        return Collections.unmodifiableSet(allParams);
    }

    /**
     * Adds a property to the list of available properties.
     *
     * @param param
     *            to be added
     * @return true if parameter was added, false if parameter was already available or NULL
     */
    protected boolean registerParameter(final PluginParameterType<?> param) {
        return param != null && this.availableParameters.put(param.getId(), param) == null;
    }

}
