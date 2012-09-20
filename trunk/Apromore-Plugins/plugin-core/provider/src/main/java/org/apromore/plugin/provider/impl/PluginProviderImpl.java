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
package org.apromore.plugin.provider.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apromore.plugin.Plugin;
import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.plugin.provider.PluginProvider;
import org.springframework.stereotype.Service;

/**
 * Default OSGi based implementation of the @link PluginProvider.
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University oAS)
 */
@Service
public class PluginProviderImpl implements PluginProvider {

	/**
	 * Will be injected by Eclipse Blueprint OSGi Framework at runtime
	 */
	@Resource
	private List<Plugin> pluginList;

	// Getter and Setter need to be public for DI

	public List<Plugin> getPluginList() {
		return pluginList;
	}

	public void setPluginList(final List<Plugin> pluginList) {
		this.pluginList = pluginList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apromore.plugin.provider.PluginProvider#listAll()
	 */
	@Override
	public Collection<Plugin> listAll() {
		return Collections.unmodifiableCollection(getPluginList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apromore.plugin.provider.PluginProvider#listByType(java.lang.String)
	 */
	@Override
	public Collection<Plugin> listByType(final String type) {
		return Collections.unmodifiableList(findAllPlugin(null, type, null));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apromore.plugin.provider.PluginProvider#listByName(java.lang.String)
	 */
	@Override
	public Collection<Plugin> listByName(final String name) {
		return Collections.unmodifiableList(findAllPlugin(name, null, null));
	}

	@Override
	public Plugin findByName(final String name) throws PluginNotFoundException {
		return findByNameAndVersion(name, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apromore.plugin.provider.PluginProvider#findByNameAndVersion(java .lang.String, java.lang.String)
	 */
	@Override
	public Plugin findByNameAndVersion(final String name, final String version) throws PluginNotFoundException {
		final List<Plugin> resultList = findAllPlugin(name, null, version);
		if (!resultList.isEmpty()) {
			// TODO decide which to take if there are more than 1 matching
			return resultList.get(0);
		}
		throw new PluginNotFoundException("Could not find plugin with name: " + ((name != null) ? name : "null") + " version: "
				+ ((version != null) ? version : "null"));
	}

	private List<Plugin> findAllPlugin(final String name, final String type, final String version) {

		final List<Plugin> resultList = new ArrayList<Plugin>();

		for (final Plugin c : getPluginList()) {
			if (compareNullable(type, c.getType()) && compareNullable(name, c.getName()) && compareNullable(version, c.getVersion())) {
				resultList.add(c);
			}
		}

		return resultList;
	}

	private boolean compareNullable(final String expectedType, final String actualType) {
		return expectedType == null ? true : expectedType.equals(actualType);
	}

}
