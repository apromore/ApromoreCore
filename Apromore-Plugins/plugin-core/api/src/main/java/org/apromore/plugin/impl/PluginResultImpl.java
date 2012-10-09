package org.apromore.plugin.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apromore.plugin.PluginResult;
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
