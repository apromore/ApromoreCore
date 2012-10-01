package org.apromore.canoniser.provider.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.apromore.plugin.exception.PluginNotFoundException;
import org.junit.Test;

public class SimpleSpringCanoniserProviderIntgTest {

    @Test
    public void testSimpleSpringCanoniserProvider() throws PluginNotFoundException {
        SimpleSpringCanoniserProvider canoniserProvider = new SimpleSpringCanoniserProvider();
        assertNotNull(canoniserProvider.getInternalCanoniserList());
        assertEquals(1, canoniserProvider.listAll().size());
        assertNotNull(canoniserProvider.findByNativeType("testType"));
        assertNotNull(canoniserProvider.findByNativeTypeAndName("testType", "test"));
        try {
            canoniserProvider.findByNativeType("invalidType");
            fail();
        } catch (PluginNotFoundException e) {
        }
    }

}
