package org.apromore.service.model;

import java.io.InputStream;
import java.util.List;

import org.apromore.plugin.message.PluginMessage;

public class DecanonisedProcess {

    private InputStream nativeFormat;
    private List<PluginMessage> messages;

    public InputStream getNativeFormat() {
        return nativeFormat;
    }

    public void setNativeFormat(final InputStream nativeFormat) {
        this.nativeFormat = nativeFormat;
    }

    public List<PluginMessage> getMessages() {
        return messages;
    }

    public void setMessages(final List<PluginMessage> messages) {
        this.messages = messages;
    }

}
