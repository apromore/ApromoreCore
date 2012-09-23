package org.apromore.plugin;

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
 * @author <a href="felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class AbstractPropertyAwarePluginTest {

    private AbstractPropertyAwarePlugin propertyAwarePlugin;
    private DefaultProperty mandatoryProperty;
    private DefaultProperty nonMandatoryProperty;

    @Before
    public void setUp() {
        this.propertyAwarePlugin = new AbstractPropertyAwarePlugin() {

            @Override
            public String getVersion() {
                return "";
            }

            @Override
            public String getType() {
                return "";
            }

            @Override
            public String getName() {
                return "";
            }

            @Override
            public String getDescription() {
                return "";
            }
        };
        mandatoryProperty = new DefaultProperty("test1", String.class, "test", true);
        nonMandatoryProperty = new DefaultProperty("test2", String.class, "test", false);
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
        assertTrue(this.propertyAwarePlugin.addProperty(new BooleanProperty("test", "test", true)));
        assertFalse(this.propertyAwarePlugin.addProperty(new BooleanProperty("test", "test", true)));
    }

}
