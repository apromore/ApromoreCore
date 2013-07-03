package org.apromore.plugin.impl;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;

import org.apromore.plugin.PluginRequestImpl;
import org.apromore.plugin.exception.PluginPropertyNotFoundException;
import org.apromore.plugin.property.PluginParameterType;
import org.apromore.plugin.property.RequestParameterType;
import org.junit.Before;
import org.junit.Test;

public class PluginRequestImplUnitTest {

    private PluginRequestImpl pluginRequestImpl;
    private RequestParameterType<String> request1;
    private RequestParameterType<String> request2;

    @Before
    public void setUp() throws Exception {
        pluginRequestImpl = new PluginRequestImpl();
        request1 = new RequestParameterType<String>("test1", "foobar");
        pluginRequestImpl.addRequestProperty(request1);
        request2 = new RequestParameterType<String>("test2", "barfoo");
        pluginRequestImpl.addRequestProperty(request2);
    }

    @Test
    public void testGetRequestProperty() throws PluginPropertyNotFoundException {
        PluginParameterType<String> pluginProp = new PluginParameterType<String>("test", "test", String.class, "test", false);
        assertEquals(pluginProp, pluginRequestImpl.getRequestParameter(pluginProp));
        assertEquals(request1, pluginRequestImpl.getRequestParameter(new PluginParameterType<String>("test1", "test", String.class, "test", false)));
        assertEquals(request2, pluginRequestImpl.getRequestParameter(new PluginParameterType<String>("test2", "test", String.class, "test", false)));
    }

    @Test
    public void testAddRequestPropertyRequestPropertyTypeOfQ() {
        pluginRequestImpl.addRequestProperty(new RequestParameterType<Integer>("test3", new Integer(2)));
    }

    @Test
    public void testAddRequestPropertySetOfRequestPropertyTypeOfQ() {
        pluginRequestImpl.addRequestProperty(new HashSet<RequestParameterType<?>>());
    }

}
