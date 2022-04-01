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

import org.apromore.plugin.exception.PluginPropertyNotFoundException;
import org.apromore.plugin.property.ParameterType;
import org.apromore.plugin.property.PluginParameterType;
import org.apromore.plugin.property.RequestParameterType;

/**
 * Common Request interface for all Plugins. The basic version just contains the user supplied request parameters {@link ParameterAwarePlugin} for the
 * current operation. Plugin APIs may extends this interface to provide advanced request parameter handling, when the built-in
 * {@link PluginParameterType} and {@link RequestParameterType} mechanism does not work.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public interface PluginRequest {

    /**
     * Get the current value for the given {@link ParameterType} in form of a {@link ParameterType}. Please not the returned {@link ParameterType} will
     * usually just holding the value, all other methods may just return NULL.
     *
     * @param pluginParameter which the {@link Plugin} defined
     * @return {@link ParameterType} holding the request value or the default {@link ParameterType} if request does not contain the parameter
     * @throws PluginPropertyNotFoundException if the property was not set and {link {@link ParameterType#isMandatory()} was true
     */
    <T> ParameterType<T> getRequestParameter(ParameterType<T> pluginParameter) throws PluginPropertyNotFoundException;

}
