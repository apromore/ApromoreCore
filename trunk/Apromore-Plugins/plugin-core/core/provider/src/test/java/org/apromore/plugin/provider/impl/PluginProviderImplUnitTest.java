package org.apromore.plugin.provider.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.apromore.plugin.Plugin;
import org.apromore.plugin.exception.PluginNotFoundException;
import org.junit.Before;
import org.junit.Test;

public class PluginProviderImplUnitTest {

    private PluginProviderImpl mockProvider;

    @Before
    public void setUp() {
        mockProvider = new PluginProviderImpl();
        final Set<Plugin> pluginList = new HashSet<>();
        pluginList.add(new Plugin() {

            @Override
            public String getVersion() {
                return "0.1";
            }

            @Override
            public String getType() {
                return "test";
            }

            @Override
            public String getName() {
                return "test";
            }

            @Override
            public String getDescription() {
                return "description";
            }

            @Override
            public String getAuthor() {
                return "author";
            }

            @Override
            public String getEMail() {
                return "test@test.com";
            }

        });
        mockProvider.setPluginList(pluginList);
    }

    @Test
    public void testListAll() throws PluginNotFoundException {
        assertNotNull(mockProvider.listAll());
        assertNotNull(mockProvider.findByName("test"));
        assertNotNull(mockProvider.findByNameAndVersion("test", "0.1"));
        try {
            mockProvider.findByName("test1");
            fail();
        } catch (PluginNotFoundException e) {

        }
        try {
            mockProvider.findByNameAndVersion("test", "0.2");
            fail();
        } catch (PluginNotFoundException e) {

        }
        assertTrue(mockProvider.listByType("test").size() == 1);
        assertTrue(mockProvider.listByName("test").size() == 1);
        assertTrue(mockProvider.listByName("test2").isEmpty());
    }

}
