package org.apromore.plugin.property;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class DefaultPropertyUnitTest {

    private DefaultProperty defaultProperty;
    private DefaultProperty defaultProperty2;
    private DefaultProperty defaultProperty3;

    @Before
    public void setUp() {
        defaultProperty = new DefaultProperty("t1", "test", String.class, "description", true);
        defaultProperty2 = new DefaultProperty("t2", "test2", Integer.class, "description2", false, 2);
        defaultProperty3 = new DefaultProperty("t3", "test3", InputStream.class, "description3", false);
    }

    @Test
    public void testCreateDefaultProperty() {
        assertNotNull(defaultProperty);
        assertNotNull(defaultProperty2);
        assertNotNull(defaultProperty3);
        try {
            new DefaultProperty("t3", "test3", String.class, "description3", false, new Integer(1));
            fail();
        } catch (IllegalArgumentException e) {
            //Expected
        }
    }

    @Test
    public void testGetName() {
        assertEquals("test", defaultProperty.getName());
        assertEquals("test2", defaultProperty2.getName());
        assertEquals("test3", defaultProperty3.getName());
    }

    @Test
    public void testGetDescription() {
        assertEquals("description", defaultProperty.getDescription());
        assertEquals("description2", defaultProperty2.getDescription());
        assertEquals("description3", defaultProperty3.getDescription());
    }

    @Test
    public void testIsMandatory() {
        assertTrue(defaultProperty.isMandatory());
        assertFalse(defaultProperty2.isMandatory());
        assertFalse(defaultProperty3.isMandatory());
    }

    @Test
    public void testGetValue() {
        assertEquals(defaultProperty.getValue(), null);
        assertEquals(defaultProperty2.getValue(), 2);
        assertEquals(defaultProperty3.getValue(), null);
    }

    @Test
    public void testSetValue() {
        defaultProperty.setValue("Test");
        try {
            defaultProperty.setValue(2);
            fail();
        } catch (IllegalArgumentException e) {
            // Expected
        }
        assertEquals("Test", defaultProperty.getValue());
    }

    @Test
    public void testGetValueType() {
        assertEquals(String.class, defaultProperty.getValueType());
        assertEquals(Integer.class, defaultProperty2.getValueType());
        assertEquals(InputStream.class, defaultProperty3.getValueType());
    }

}
