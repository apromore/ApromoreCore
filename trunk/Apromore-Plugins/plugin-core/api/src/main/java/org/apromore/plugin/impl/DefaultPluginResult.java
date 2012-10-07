package org.apromore.plugin.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apromore.plugin.PluginResult;
import org.apromore.plugin.message.PluginMessage;
import org.apromore.plugin.message.SimplePluginMessage;

/**
 * Default implementation of the {@link PluginResult} interface providing management of {@link PluginMessage}.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class DefaultPluginResult implements PluginResult {

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
       pluginMessages.add(new SimplePluginMessage(MessageFormat.format(message, args)));
    }

    /**
     * Add a simple String message
     *
     * @param message
     */
    public void addPluginMessage(final String message) {
        initPluginMessages();
        pluginMessages.add(new SimplePluginMessage(message));
     }

    private void initPluginMessages() {
        if (pluginMessages == null) {
            pluginMessages = new ArrayList<PluginMessage>();
        }
    }

}
