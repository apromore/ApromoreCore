/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 Felix Mannhardt.
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

package org.apromore.service.impl;

import org.apromore.plugin.ParameterAwarePlugin;
import org.apromore.plugin.Plugin;
import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.plugin.property.ParameterType;
import org.apromore.plugin.provider.PluginProvider;
import org.apromore.service.PluginService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import javax.inject.Inject;

/**
 * Service for common Plugin operations, like list all installed Plugins, install a new Plugin, get Plugin meta data.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 */
@Service
public class PluginServiceImpl implements PluginService {

    private PluginProvider provider;

    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     *
     * @param pluginProvider Plugin Provider.
     */
    @Inject
    public PluginServiceImpl(final @Qualifier("pluginProvider") PluginProvider pluginProvider) {
        provider = pluginProvider;
    }


    /* (non-Javadoc)
    * @see org.apromore.service.PluginService#listAll()
    */
    @Override
    public Set<Plugin> listAll() {
        return provider.listAll();
    }

    /* (non-Javadoc)
     * @see org.apromore.service.PluginService#listByType(java.lang.String)
     */
    @Override
    public Set<Plugin> listByType(final String type) {
        return provider.listByType(type);
    }

    /* (non-Javadoc)
     * @see org.apromore.service.PluginService#listByName(java.lang.String)
     */
    @Override
    public Set<Plugin> listByName(final String name) {
        return provider.listByName(name);
    }

    /* (non-Javadoc)
     * @see org.apromore.service.PluginService#findByName(java.lang.String)
     */
    @Override
    public Plugin findByName(final String name) throws PluginNotFoundException {
        return provider.findByName(name);
    }

    /* (non-Javadoc)
     * @see org.apromore.service.PluginService#findByNameAndVersion(java.lang.String, java.lang.String)
     */
    @Override
    public Plugin findByNameAndVersion(final String name, final String version) throws PluginNotFoundException {
        return provider.findByNameAndVersion(name, version);
    }

    /* (non-Javadoc)
     * @see org.apromore.service.PluginService#getOptionalProperties(java.lang.String, java.lang.String)
     */
    @Override
    public Set<ParameterType<?>> getOptionalProperties(final String name, final String version) throws PluginNotFoundException {
        Plugin plugin = provider.findByNameAndVersion(name, version);
        if (plugin instanceof ParameterAwarePlugin) {
            return ((ParameterAwarePlugin) plugin).getOptionalParameters();
        } else {
            Set<ParameterType<?>> emptySet = Collections.emptySet();
            return Collections.unmodifiableSet(emptySet);
        }
    }

    /* (non-Javadoc)
     * @see org.apromore.service.PluginService#getMandatoryProperties(java.lang.String, java.lang.String)
     */
    @Override
    public Set<ParameterType<?>> getMandatoryProperties(final String name, final String version) throws PluginNotFoundException {
        Plugin plugin = provider.findByNameAndVersion(name, version);
        if (plugin instanceof ParameterAwarePlugin) {
            return ((ParameterAwarePlugin) plugin).getMandatoryParameters();
        } else {
            Set<ParameterType<?>> emptySet = Collections.emptySet();
            return Collections.unmodifiableSet(emptySet);
        }
    }


}
