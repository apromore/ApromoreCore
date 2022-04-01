/*-
 * #%L
 * This file is part of "Apromore Core".
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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apromore.plugin.eventlog.EventLogPlugin;

/**
 * Register new event log plugins.
 */
public class EventLogPluginListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventLogPluginListener.class);

    // The following two methods are looked up using reflection by the Virgo container.
    // This is configured in the osgi:reference-listener element in context.xml

    /**
     * Register a new event log plugin.
     *
     * @see http://www.eclipse.org/gemini/blueprint/documentation/reference/2.0.0.RELEASE/html/service-registry.html#service-registry:refs:dynamics
     */
    public void onEventLogPluginBind(EventLogPlugin eventLogPlugin, Map properties) {
        LOGGER.info("New event log plugin bound: " + eventLogPlugin + " with properties " + properties);
        //eventLogPlugin.bindToEventLogService();
    }

    /**
     * Unregister an event log plugin.
     *
     * @see http://www.eclipse.org/gemini/blueprint/documentation/reference/2.0.0.RELEASE/html/service-registry.html#service-registry:refs:dynamics
     */
    public void onEventLogPluginUnbind(EventLogPlugin eventLogPlugin, Map properties) {
        LOGGER.info("Event log plugin unbound: " + eventLogPlugin + " with properties " + properties);
        //eventLogPlugin.unbindFromEventLogService();
    }
}
