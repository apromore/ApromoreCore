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
package org.apromore.canoniser.yawl.internal.impl;

import org.apromore.canoniser.yawl.internal.MessageManager;
import org.apromore.plugin.PluginResultImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Simple wrapper to pass messages on to the Plugin without needing to know about the Plugin
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 */
public class MessageManagerImpl implements MessageManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageManagerImpl.class);

    private final PluginResultImpl pluginResult;

    public MessageManagerImpl(final PluginResultImpl pluginResult) {
        this.pluginResult = pluginResult;
    }

    /* (non-Javadoc)
     * @see org.apromore.canoniser.yawl.internal.MessageManager#addMessage(java.lang.String)
     */
    @Override
    public void addMessage(final String message) {
        pluginResult.addPluginMessage(message);
        LOGGER.warn(message);
    }

    /* (non-Javadoc)
     * @see org.apromore.canoniser.yawl.internal.MessageManager#addMessage(java.lang.String, java.lang.Object[])
     */
    @Override
    public void addMessage(final String message, final Object... args) {
        pluginResult.addPluginMessage(message, args);
        LOGGER.warn(message, args);
    }

}
