package org.apromore.plugin.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.apromore.plugin.message.PluginMessage;
import org.junit.Before;
import org.junit.Test;

public class DefaultMessageAwarePluginUnitTest {

    private DefaultMessageAwarePlugin defaultMessageAwarePlugin;

    @Before
    public void setUp() {
      this.defaultMessageAwarePlugin = new DefaultMessageAwarePlugin() {
      };
    }

    @Test
    public void testGetPluginMessagesByObject() {
        assertNotNull(defaultMessageAwarePlugin);
        defaultMessageAwarePlugin.addPluginMessage(new PluginMessage() {

            @Override
            public String getMessage() {
                return "Test";
            }
        });
        Collection<PluginMessage> pluginMessages = defaultMessageAwarePlugin.getPluginMessages();
        assertNotNull(pluginMessages);
        assertEquals(1, pluginMessages.size());
        assertEquals("Test", pluginMessages.iterator().next().getMessage());
    }

    @Test
    public void testAddPluginMessageByString() {
        assertNotNull(defaultMessageAwarePlugin);
        defaultMessageAwarePlugin.addPluginMessage("test");
        Collection<PluginMessage> pluginMessages = defaultMessageAwarePlugin.getPluginMessages();
        assertNotNull(pluginMessages);
        assertEquals(1, pluginMessages.size());
        assertEquals("test", pluginMessages.iterator().next().getMessage());
    }

    @Test
    public void testGetPluginMessages() {
        assertNotNull(defaultMessageAwarePlugin.getPluginMessages());
        assertTrue(defaultMessageAwarePlugin.getPluginMessages().isEmpty());
    }

}
