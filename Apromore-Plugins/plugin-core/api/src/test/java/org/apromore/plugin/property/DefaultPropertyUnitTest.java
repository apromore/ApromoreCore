package org.apromore.plugin.property;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class DefaultPropertyUnitTest {

    private PluginPropertyType<String> defaultProperty;
    private PluginPropertyType<Integer> defaultProperty2;
    private PluginPropertyType<InputStream> defaultProperty3;

    @Before
    public void setUp() {
        defaultProperty = new PluginPropertyType<String>("t1", "test",String.class, "description", true);
        defaultProperty2 = new PluginPropertyType<Integer>("t2", "test2","description2", false, 2);
        defaultProperty3 = new PluginPropertyType<InputStream>("t3", "test3", InputStream.class, "description3", false);
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
        assertEquals(defaultProperty2.getValue(), new Integer(2));
        assertEquals(defaultProperty3.getValue(), null);
    }

    @Test
    public void testGetValueType() {
        assertEquals(String.class, defaultProperty.getValueType());
        assertEquals(Integer.class, defaultProperty2.getValueType());
        assertEquals(InputStream.class, defaultProperty3.getValueType());
    }

}
