package org.apromore.plugin.impl;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;

import org.apromore.plugin.exception.PluginPropertyNotFoundException;
import org.apromore.plugin.property.PluginPropertyType;
import org.apromore.plugin.property.RequestPropertyType;
import org.junit.Before;
import org.junit.Test;

public class PluginRequestImplTest {

    private PluginRequestImpl pluginRequestImpl;
    private RequestPropertyType<String> request1;
    private RequestPropertyType<String> request2;

    @Before
    public void setUp() throws Exception {
        pluginRequestImpl = new PluginRequestImpl();
        request1 = new RequestPropertyType<String>("test1", "foobar");
        pluginRequestImpl.addRequestProperty(request1);
        request2 = new RequestPropertyType<String>("test2", "barfoo");
        pluginRequestImpl.addRequestProperty(request2);
    }

    @Test
    public void testGetRequestProperty() throws PluginPropertyNotFoundException {
        PluginPropertyType<String> pluginProp = new PluginPropertyType<String>("test", "test", String.class, "test", false);
        assertEquals(pluginProp, pluginRequestImpl.getRequestProperty(pluginProp));
        assertEquals(request1, pluginRequestImpl.getRequestProperty(new PluginPropertyType<String>("test1", "test", String.class, "test", false)));
        assertEquals(request2, pluginRequestImpl.getRequestProperty(new PluginPropertyType<String>("test2", "test", String.class, "test", false)));
    }

    @Test
    public void testAddRequestPropertyRequestPropertyTypeOfQ() {
        pluginRequestImpl.addRequestProperty(new RequestPropertyType<Integer>("test3", new Integer(2)));
    }

    @Test
    public void testAddRequestPropertySetOfRequestPropertyTypeOfQ() {
        pluginRequestImpl.addRequestProperty(new HashSet<RequestPropertyType<?>>());
    }

}
