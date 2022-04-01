/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2013 Felix Mannhardt.
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apromore.plugin.property.PluginParameterType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test functionality offered by AbstractPropertyAwarePlugin
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
class DefaultPropertyAwarePluginUnitTest {

    private DefaultParameterAwarePlugin propertyAwarePlugin;
    private PluginParameterType<String> mandatoryProperty;
    private PluginParameterType<String> nonMandatoryProperty;

    @BeforeEach
    void setUp() {
        this.propertyAwarePlugin = new DefaultParameterAwarePlugin() {
        };
        mandatoryProperty = new PluginParameterType<String>("t1", "test1", String.class, "test", true);
        nonMandatoryProperty = new PluginParameterType<String>("t2", "test2", String.class, "test", false);
        this.propertyAwarePlugin.registerParameter(mandatoryProperty);
        this.propertyAwarePlugin.registerParameter(nonMandatoryProperty);
    }

    @Test
    void testAbstractPropertyAwarePlugin() {
        assertNotNull(this.propertyAwarePlugin);
    }

    @Test
    void testGetMandatoryProperties() {
        assertEquals(1, this.propertyAwarePlugin.getMandatoryParameters().size());
        assertTrue(this.propertyAwarePlugin.getMandatoryParameters().contains(mandatoryProperty));
        assertFalse(this.propertyAwarePlugin.getMandatoryParameters().contains(nonMandatoryProperty));
    }

    @Test
    void testGetAvailableProperties() {
        assertEquals(2, this.propertyAwarePlugin.getAvailableParameters().size());
        assertTrue(this.propertyAwarePlugin.getAvailableParameters().contains(nonMandatoryProperty));
        assertTrue(this.propertyAwarePlugin.getAvailableParameters().contains(mandatoryProperty));
    }


    @Test
    void testGetOptionalProperties() {
        assertEquals(1, this.propertyAwarePlugin.getOptionalParameters().size());
        assertTrue(this.propertyAwarePlugin.getOptionalParameters().contains(nonMandatoryProperty));
        assertFalse(this.propertyAwarePlugin.getOptionalParameters().contains(mandatoryProperty));
    }

    @Test
    void testAddProperty() {
        assertFalse(this.propertyAwarePlugin.registerParameter(null));
        assertTrue(this.propertyAwarePlugin.registerParameter(new PluginParameterType<Boolean>("t3", "test", Boolean.class, "test", true)));
        assertFalse(this.propertyAwarePlugin.registerParameter(new PluginParameterType<Boolean>("t3", "test", Boolean.class, "test", true)));
    }

}
