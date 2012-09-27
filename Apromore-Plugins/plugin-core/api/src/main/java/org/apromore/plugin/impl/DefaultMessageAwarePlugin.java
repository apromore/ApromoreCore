package org.apromore.plugin.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apromore.plugin.MessageAwarePlugin;
import org.apromore.plugin.message.PluginMessage;
import org.apromore.plugin.message.SimplePluginMessage;

public abstract class DefaultMessageAwarePlugin extends DefaultPlugin implements MessageAwarePlugin {

    /**
     * List of messages
     */
    private Collection<PluginMessage> messageList;

    /* (non-Javadoc)
     * @see org.apromore.plugin.MessageAwarePlugin#getPluginMessages()
     */
    @Override
    public Collection<PluginMessage> getPluginMessages() {
        initMessageList();
        return Collections.unmodifiableCollection(messageList);
    }

    /**
     * Store a Messages to be retrieved by whoever is using this Plugin later on.
     *
     * @param messageFormat content of the Message used in String.format
     * @param args arguments of String.format
     */
    public void addPluginMessage(final String messageFormat, final Object... args) {
        addPluginMessage(String.format(messageFormat, args));
    }

    /**
     * Store a Messages to be retrieved by whoever is using this Plugin later on.
     *
     * @param message content of the Message
     */
    public void addPluginMessage(final String message) {
        addPluginMessage(new SimplePluginMessage(message));
    }

    /**
     * Store a Messages to be retrieved by whoever is using this Plugin later on.
     *
     * @param message an implementation of PluginMessage interface
     */
    public void addPluginMessage(final PluginMessage message) {
        initMessageList();
        messageList.add(message);
    }

    /**
     * Lazy initialization of message list
     */
    private void initMessageList() {
        if (messageList == null) {
            messageList = new ArrayList<>();
        }
    }

}
