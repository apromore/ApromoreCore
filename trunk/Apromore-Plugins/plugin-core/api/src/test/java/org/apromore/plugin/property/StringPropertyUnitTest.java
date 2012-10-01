package org.apromore.plugin.property;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class StringPropertyUnitTest {

    @Test
    public void test() {
        StringProperty prop = new StringProperty("t1", "test", "test", false, "test");
        assertEquals("test", prop.getValueAsString());
        assertEquals("test", prop.getValue());

        StringProperty prop2 = new StringProperty("t1", "test", "test", false);
        try {
            prop2.setValue(false);
            fail();
        } catch (IllegalArgumentException e) {
            // Expected
        }

        StringProperty prop3 = new StringProperty("t1", "test", "test", false);
        prop3.setValue("foobar");
        assertEquals("foobar", prop3.getValueAsString());
    }

}
