/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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
package org.apromore.plugin.provider;

import java.util.Set;

import org.apromore.plugin.Plugin;
import org.apromore.plugin.exception.PluginNotFoundException;

/**
 * Interfaces used by Apromore to retrieve all currently installed plugins. Implementations of this interface usually should use OSGi
 * services (Gemini Blueprint) to pick up availables plugins automatically. Though it would also be possible to provide non-OSGi based
 * implementations.
 *
 * @see "http://www.eclipse.org/gemini/blueprint/"
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public interface PluginProvider {

	/**
	 * @return read-only Set of all currently available plugins
	 */
	Set<Plugin> listAll();

	/**
	 * @param type
	 *            of the plugins
	 * @return read-only Set of all plugins with specified type
	 */
	Set<Plugin> listByType(String type);

	/**
	 * @param name
	 *            of the plugins, usually the symbolic bundle name
	 * @return read-only Set of all plugins with specified name
	 */
	Set<Plugin> listByName(String name);

	/**
	 * @param name
	 *            of the plugins, usually the symbolic bundle name
	 * @return plugin with specified name and highest version if multiple
	 * @throws PluginNotFoundException in case no matching Plugin is found
	 */
	Plugin findByName(String name) throws PluginNotFoundException;

	/**
	 * @param name
	 *            of the plugins, usually the symbolic bundle name
	 * @param version
	 *            of the plugins, usually the bundle version (MAJOR.MINOR.MICRO.QUALIFIER)
	 * @return plugin with specified name and version
	 * @throws PluginNotFoundException in case no matching Plugin is found
	 */
	Plugin findByNameAndVersion(String name, String version) throws PluginNotFoundException;

}
