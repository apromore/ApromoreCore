package org.apromore.plugin.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apromore.plugin.property.BooleanProperty;
import org.apromore.plugin.property.DefaultProperty;
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
    private DefaultProperty mandatoryProperty;
    private DefaultProperty nonMandatoryProperty;

    @Before
    public void setUp() {
        this.propertyAwarePlugin = new DefaultPropertyAwarePlugin() {
        };
        mandatoryProperty = new DefaultProperty("t1", "test1", String.class, "test", true);
        nonMandatoryProperty = new DefaultProperty("t2", "test2", String.class, "test", false);
        this.propertyAwarePlugin.addProperty(mandatoryProperty);
        this.propertyAwarePlugin.addProperty(nonMandatoryProperty);
    }

    @Test
    public void testAbstractPropertyAwarePlugin() {
        assertNotNull(this.propertyAwarePlugin);
    }

    @Test
    public void testGetMandatoryProperties() {
        assertEquals(1, this.propertyAwarePlugin.getMandatoryProperties().size());
        assertTrue(this.propertyAwarePlugin.getMandatoryProperties().contains(mandatoryProperty));
    }

    @Test
    public void testGetAvailableProperties() {
        assertEquals(2, this.propertyAwarePlugin.getAvailableProperties().size());
        assertTrue(this.propertyAwarePlugin.getAvailableProperties().contains(nonMandatoryProperty));
        assertTrue(this.propertyAwarePlugin.getAvailableProperties().contains(mandatoryProperty));
    }

    @Test
    public void testAddProperty() {
        assertFalse(this.propertyAwarePlugin.addProperty(null));
        assertTrue(this.propertyAwarePlugin.addProperty(new BooleanProperty("t1", "test", "test", true)));
        assertFalse(this.propertyAwarePlugin.addProperty(new BooleanProperty("t1", "test", "test", true)));
    }

}
