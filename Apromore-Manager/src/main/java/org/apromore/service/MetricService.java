/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of “Apromore”.
 *
 * “Apromore” is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * “Apromore” is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.service;

import org.apromore.exception.ExceptionDao;
import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.plugin.metric.MetricPlugin;

import javax.xml.bind.JAXBException;
import java.util.Collection;
import java.util.Set;

/**
 * Service for the Metric calculations for process Models.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface MetricService {

    /**
     * List all available Metric plugins.
     * @return Set of plugins
     */
    Collection<MetricPlugin> listAll();

    /**
     * Finds first metric plugin using the criteria defined.
     * @param pluginName the plugin name of the metric calculation
     * @return The found plugin with that name.
     * @throws org.apromore.plugin.exception.PluginNotFoundException
     */
    MetricPlugin findByName(String pluginName) throws PluginNotFoundException;

    /**
     * Finds first metric plugin using the criteria defined.
     * @param pluginName the plugin name of the metric calculation
     * @param versionNumber the version of the plugin we want. could be more than one version.
     * @return The found plugin with that name and version
     * @throws org.apromore.plugin.exception.PluginNotFoundException
     */
    MetricPlugin findByNameAndVersion(String pluginName, String versionNumber) throws PluginNotFoundException;


}
