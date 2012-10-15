package org.apromore.plugin.property;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StringParameterUnitTest {

    @Test
    public void test() {
        PluginParameterType<String> prop = new PluginParameterType<String>("t1", "test", "test", false, "test");
        assertEquals("test", prop.getValue());

        PluginParameterType<String> prop3 = new PluginParameterType<String>("t1", "test", String.class, "test", false);
        prop3.setValue("foobar");
        assertEquals("foobar", prop3.getValue());
    }

}
