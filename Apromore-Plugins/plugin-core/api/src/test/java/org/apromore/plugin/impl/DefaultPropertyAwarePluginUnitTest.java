package org.apromore.plugin.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apromore.plugin.property.PluginPropertyType;
import org.junit.Before;
import org.junit.Test;

/**
 * Test functionality offered by AbstractPropertyAwarePlugin
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class DefaultPropertyAwarePluginUnitTest {

    private DefaultPropertyAwarePlugin propertyAwarePlugin;
    private PluginPropertyType<String> mandatoryProperty;
    private PluginPropertyType<String> nonMandatoryProperty;

    @Before
    public void setUp() {
        this.propertyAwarePlugin = new DefaultPropertyAwarePlugin() {
        };
        mandatoryProperty = new PluginPropertyType<String>("t1", "test1", String.class, "test", true);
        nonMandatoryProperty = new PluginPropertyType<String>("t2", "test2", String.class, "test", false);
        this.propertyAwarePlugin.registerProperty(mandatoryProperty);
        this.propertyAwarePlugin.registerProperty(nonMandatoryProperty);
    }

    @Test
    public void testAbstractPropertyAwarePlugin() {
        assertNotNull(this.propertyAwarePlugin);
    }

    @Test
    public void testGetMandatoryProperties() {
        assertEquals(1, this.propertyAwarePlugin.getMandatoryProperties().size());
        assertTrue(this.propertyAwarePlugin.getMandatoryProperties().contains(mandatoryProperty));
        assertFalse(this.propertyAwarePlugin.getMandatoryProperties().contains(nonMandatoryProperty));
    }

    @Test
    public void testGetAvailableProperties() {
        assertEquals(2, this.propertyAwarePlugin.getAvailableProperties().size());
        assertTrue(this.propertyAwarePlugin.getAvailableProperties().contains(nonMandatoryProperty));
        assertTrue(this.propertyAwarePlugin.getAvailableProperties().contains(mandatoryProperty));
    }


    @Test
    public void testGetOptionalProperties() {
        assertEquals(1, this.propertyAwarePlugin.getOptionalProperties().size());
        assertTrue(this.propertyAwarePlugin.getOptionalProperties().contains(nonMandatoryProperty));
        assertFalse(this.propertyAwarePlugin.getOptionalProperties().contains(mandatoryProperty));
    }

    @Test
    public void testAddProperty() {
        assertFalse(this.propertyAwarePlugin.registerProperty(null));
        assertTrue(this.propertyAwarePlugin.registerProperty(new PluginPropertyType<Boolean>("t3", "test", Boolean.class, "test", true)));
        assertFalse(this.propertyAwarePlugin.registerProperty(new PluginPropertyType<Boolean>("t3", "test", Boolean.class, "test", true)));
    }

}
