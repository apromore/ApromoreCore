package org.apromore.plugin.property;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class BooleanPropertyUnitTest {

    @Test
    public void test() {
        BooleanProperty prop = new BooleanProperty("test", "test", false, false);
        assertFalse(prop.getValueAsBoolean());
        assertFalse((Boolean)prop.getValue());

        BooleanProperty prop2 = new BooleanProperty("test", "test", false);
        try {
            prop2.setValue("String");
            fail();
        } catch (IllegalArgumentException e) {
            // Expected
        }

        BooleanProperty prop3 = new BooleanProperty("test", "test", false);
        prop3.setValue(new Boolean(true));
        assertTrue(prop3.getValueAsBoolean());
    }

}
