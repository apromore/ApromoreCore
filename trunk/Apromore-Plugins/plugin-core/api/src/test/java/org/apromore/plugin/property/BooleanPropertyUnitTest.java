package org.apromore.plugin.property;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BooleanPropertyUnitTest {

    @Test
    public void test() {
        PluginPropertyType<Boolean> prop = new PluginPropertyType<Boolean>("t1", "test", "test", false, false);
        assertFalse(prop.getValue());
        assertFalse(prop.getValue());

        PluginPropertyType<Boolean> prop3 = new PluginPropertyType<Boolean>("t1", "test", Boolean.class, "test", false);
        prop3.setValue(new Boolean(true));
        assertTrue(prop3.getValue());
        assertEquals(Boolean.class, prop3.getValueType());
    }

}
