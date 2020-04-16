/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
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
 * #L%
 */
package org.apromore.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apromore.plugin.process.ProcessPlugin;

/**
 * Register new process plugins.
 */
public class ProcessPluginListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessPluginListener.class);

    // The following two methods are looked up using reflection by the Virgo container.
    // This is configured in the osgi:reference-listener element in context.xml

    /**
     * Register a new process plugin.
     *
     * @see http://www.eclipse.org/gemini/blueprint/documentation/reference/2.0.0.RELEASE/html/service-registry.html#service-registry:refs:dynamics
     */
    public void onProcessPluginBind(ProcessPlugin processPlugin, Map properties) {
        System.out.println("New process plugin bound (2): " + processPlugin + " with properties + " + properties);
        LOGGER.info("New process plugin bound: " + processPlugin + " with properties " + properties);
        processPlugin.bindToProcessService();
    }

    /**
     * Unregister a process plugin.
     *
     * @see http://www.eclipse.org/gemini/blueprint/documentation/reference/2.0.0.RELEASE/html/service-registry.html#service-registry:refs:dynamics
     */
    public void onProcessPluginUnbind(ProcessPlugin processPlugin, Map properties) {
        LOGGER.info("Process plugin unbound: " + processPlugin + " with properties " + properties);
        processPlugin.unbindFromProcessService();
    }
}
