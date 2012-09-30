package org.apromore.plugin.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apromore.plugin.MessageAwarePlugin;
import org.apromore.plugin.message.PluginMessage;
import org.apromore.plugin.message.SimplePluginMessage;

public abstract class DefaultMessageAwarePlugin extends DefaultPlugin implements MessageAwarePlugin {

    /**
     * List of messages
     */
    private List<PluginMessage> messageList;

    /* (non-Javadoc)
     * @see org.apromore.plugin.MessageAwarePlugin#getPluginMessages()
     */
    @Override
    public List<PluginMessage> getPluginMessages() {
        initMessageList();
        return Collections.unmodifiableList(messageList);
    }

    /**
     * Store a Messages to be retrieved by whoever is using this Plugin later on.
     *
     * @param messageFormat content of the Message used in {@see MessageFormat#format(String, Object...)}
     * @param args second argument of {@see MessageFormat#format(String, Object...)}
     */
    public void addPluginMessage(final String messageFormat, final Object... args) {
        addPluginMessage(MessageFormat.format(messageFormat, args));
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
            messageList = new ArrayList<PluginMessage>();
        }
    }

}
