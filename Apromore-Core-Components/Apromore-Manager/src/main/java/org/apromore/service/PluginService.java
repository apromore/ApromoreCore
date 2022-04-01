/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
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

package org.apromore.service;

import org.apromore.plugin.Plugin;
import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.plugin.property.ParameterType;

import java.util.Set;

/**
 *
 * Service for common Plugin operations, like list all installed Plugins, install a new Plugin, get Plugin meta data.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 *
 */
public interface PluginService {

    Set<Plugin> listAll();

    Set<Plugin> listByType(String type);

    Set<Plugin> listByName(String name);

    Plugin findByName(String name) throws PluginNotFoundException;

    Plugin findByNameAndVersion(String name, String version) throws PluginNotFoundException;

    Set<ParameterType<?>> getOptionalProperties(String name, String version) throws PluginNotFoundException;

    Set<ParameterType<?>> getMandatoryProperties(String name, String version) throws PluginNotFoundException;

}
