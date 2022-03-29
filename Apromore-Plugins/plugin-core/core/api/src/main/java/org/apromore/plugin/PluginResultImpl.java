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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apromore.plugin.message.PluginMessage;
import org.apromore.plugin.message.PluginMessageImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of the {@link PluginResult} interface providing management of {@link PluginMessage}.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class PluginResultImpl implements PluginResult {

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginResultImpl.class);

    private List<PluginMessage> pluginMessages;

    /* (non-Javadoc)
     * @see org.apromore.plugin.PluginResult#getPluginMessage()
     */
    @Override
    public List<PluginMessage> getPluginMessage() {
        initPluginMessages();
        return Collections.unmodifiableList(pluginMessages);
    }

    /**
     * Add a Message using {@link MessageFormat#format(String, Object...)}
     *
     * @param message
     * @param args
     */
    public void addPluginMessage(final String message, final Object... args) {
       initPluginMessages();
       LOGGER.debug(MessageFormat.format(message, args));
       pluginMessages.add(new PluginMessageImpl(MessageFormat.format(message, args)));
    }

    /**
     * Add a simple String message
     *
     * @param message
     */
    public void addPluginMessage(final String message) {
        initPluginMessages();
        LOGGER.debug(message);
        pluginMessages.add(new PluginMessageImpl(message));
     }

    private void initPluginMessages() {
        if (pluginMessages == null) {
            pluginMessages = new ArrayList<PluginMessage>();
        }
    }

}
