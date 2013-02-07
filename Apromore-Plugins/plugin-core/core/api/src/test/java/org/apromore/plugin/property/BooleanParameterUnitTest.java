package org.apromore.plugin.property;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BooleanParameterUnitTest {

    @Test
    public void test() {
        PluginParameterType<Boolean> prop = new PluginParameterType<Boolean>("t1", "test", "test", false, false);
        assertFalse(prop.getValue());
        assertFalse(prop.getValue());

        PluginParameterType<Boolean> prop3 = new PluginParameterType<Boolean>("t1", "test", Boolean.class, "test", false);
        prop3.setValue(new Boolean(true));
        assertTrue(prop3.getValue());
        assertEquals(Boolean.class, prop3.getValueType());
    }

}
