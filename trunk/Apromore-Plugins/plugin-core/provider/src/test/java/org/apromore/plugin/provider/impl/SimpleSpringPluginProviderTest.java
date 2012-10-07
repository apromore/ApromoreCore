package org.apromore.plugin.provider.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apromore.plugin.exception.PluginNotFoundException;
import org.junit.Before;
import org.junit.Test;

public class SimpleSpringPluginProviderTest {

    private SimpleSpringPluginProvider pluginProvider;

    @Before
    public void setUp() {
        pluginProvider = new SimpleSpringPluginProvider();
    }

    @Test
    public void testListAll() {
        assertNotNull(pluginProvider.listAll());
        assertTrue(!pluginProvider.listAll().isEmpty());
    }

    @Test
    public void testListByType() {
        assertNotNull(pluginProvider.listByType(""));
        assertEquals(1, pluginProvider.listByType("org.apromore.plugin.provider.impl.test").size());
    }

    @Test
    public void testListByName() {
        assertNotNull(pluginProvider.listByName(""));
        assertEquals(1, pluginProvider.listByName("test").size());
    }

    @Test
    public void testFindByName() throws PluginNotFoundException {
        try {
            pluginProvider.findByName("");
            fail();
        } catch (PluginNotFoundException e) {
        }
        assertNotNull(pluginProvider.findByName("test"));
    }

    @Test
    public void testFindByNameAndVersion() throws PluginNotFoundException {
        try {
            pluginProvider.findByNameAndVersion("", "");
            fail();
        } catch (PluginNotFoundException e) {
        }
        assertNotNull(pluginProvider.findByNameAndVersion("test","1.0"));
    }

}
