package org.apromore.plugin.property;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StringPropertyUnitTest {

    @Test
    public void test() {
        PluginPropertyType<String> prop = new PluginPropertyType<String>("t1", "test", "test", false, "test");
        assertEquals("test", prop.getValue());

        PluginPropertyType<String> prop3 = new PluginPropertyType<String>("t1", "test", String.class, "test", false);
        prop3.setValue("foobar");
        assertEquals("foobar", prop3.getValue());
    }

}
