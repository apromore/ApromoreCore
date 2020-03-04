/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2013 Felix Mannhardt.
 * Copyright (C) 2018, 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.plugin.search.provider.impl;

import java.util.Collection;
import java.util.Set;

import javax.annotation.Resource;

import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.plugin.search.SearchByTextPlugin;
import org.apromore.plugin.search.provider.SearchByTextPluginProvider;
import org.springframework.stereotype.Component;

@Component
public class SearchByTextPluginProviderImpl implements SearchByTextPluginProvider {

    @Resource
    public Set<SearchByTextPlugin> searchByTextPluginSet;
    
    @Override
    public Collection<SearchByTextPlugin> listAll() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SearchByTextPlugin findByName() throws PluginNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SearchByTextPlugin findByNameAndVersion()
            throws PluginNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

}
