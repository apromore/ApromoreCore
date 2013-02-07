package org.apromore.plugin.property;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class CategoryParameterUnitTest {


    @Test
    public void test() {
        PluginParameterType<String> param = new PluginParameterType<String>("test", "test", String.class, "test", true, "categoryA");
        assertNotNull(param);
        assertEquals("categoryA", param.getCategory());

        PluginParameterType<String> param2 = new PluginParameterType<String>("test", "test", "test", true, "categoryA", "value");
        assertNotNull(param2);
        assertEquals("categoryA", param2.getCategory());
    }

}
