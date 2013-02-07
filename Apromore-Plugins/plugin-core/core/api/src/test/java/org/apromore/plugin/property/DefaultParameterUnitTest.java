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
public class DefaultParameterUnitTest {

    private PluginParameterType<String> defaultProperty;
    private PluginParameterType<Integer> defaultProperty2;
    private PluginParameterType<InputStream> defaultProperty3;

    @Before
    public void setUp() {
        defaultProperty = new PluginParameterType<String>("t1", "test",String.class, "description", true);
        defaultProperty2 = new PluginParameterType<Integer>("t2", "test2","description2", false, 2);
        defaultProperty3 = new PluginParameterType<InputStream>("t3", "test3", InputStream.class, "description3", false);
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


    @Test
    public void testGetCategory() {
        assertEquals(ParameterType.DEFAULT_CATEGORY, defaultProperty.getCategory());
        assertEquals(ParameterType.DEFAULT_CATEGORY, defaultProperty2.getCategory());
        assertEquals(ParameterType.DEFAULT_CATEGORY, defaultProperty3.getCategory());
    }

}
