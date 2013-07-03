package org.apromore.plugin.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apromore.plugin.PluginResultImpl;
import org.junit.Before;
import org.junit.Test;

public class PluginResultImplUnitTest {

    private PluginResultImpl pluginResultImpl;

    @Before
    public void setUp() throws Exception {
        pluginResultImpl = new PluginResultImpl();
        pluginResultImpl.addPluginMessage("test");
    }

    @Test
    public void testGetPluginMessage() {
        assertNotNull(pluginResultImpl.getPluginMessage());
        assertEquals(1, pluginResultImpl.getPluginMessage().size());
    }

    @Test
    public void testAddPluginMessageStringObjectArray() {
        pluginResultImpl.addPluginMessage("test {0}", new Integer(1));
        assertNotNull(pluginResultImpl.getPluginMessage());
        assertEquals(2, pluginResultImpl.getPluginMessage().size());
    }

    @Test
    public void testAddPluginMessageString() {
        pluginResultImpl.addPluginMessage("test2");
        assertEquals(2, pluginResultImpl.getPluginMessage().size());
    }

}
