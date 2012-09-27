package org.apromore.canoniser.yawl.internal.impl;

import org.apromore.canoniser.yawl.internal.MessageManager;
import org.apromore.plugin.impl.DefaultMessageAwarePlugin;


/**
 * Simple wrapper to pass messages on to the Plugin without needing to know about the Plugin
 *
 */
public class MessageManagerImpl implements MessageManager {

    private final DefaultMessageAwarePlugin messageAwarePlugin;

    public MessageManagerImpl(final DefaultMessageAwarePlugin messageAwarePlugin) {
        this.messageAwarePlugin = messageAwarePlugin;
    }

    /* (non-Javadoc)
     * @see org.apromore.canoniser.yawl.internal.MessageManager#addMessage(java.lang.String)
     */
    @Override
    public void addMessage(final String message) {
        messageAwarePlugin.addPluginMessage(message);
    }

    @Override
    public void addMessage(final String message, final Object... args) {
        messageAwarePlugin.addPluginMessage(message, args);
    }

}
