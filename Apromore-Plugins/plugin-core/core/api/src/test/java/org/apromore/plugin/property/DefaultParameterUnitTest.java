/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2013 Felix Mannhardt.
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
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

package org.apromore.plugin.property;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
class DefaultParameterUnitTest {

    private PluginParameterType<String> defaultProperty;
    private PluginParameterType<Integer> defaultProperty2;
    private PluginParameterType<InputStream> defaultProperty3;

    @BeforeEach
    void setUp() {
        defaultProperty = new PluginParameterType<String>("t1", "test",String.class, "description", true);
        defaultProperty2 = new PluginParameterType<Integer>("t2", "test2","description2", false, 2);
        defaultProperty3 = new PluginParameterType<InputStream>("t3", "test3", InputStream.class, "description3", false);
    }

    @Test
    void testGetName() {
        assertEquals("test", defaultProperty.getName());
        assertEquals("test2", defaultProperty2.getName());
        assertEquals("test3", defaultProperty3.getName());
    }

    @Test
    void testGetDescription() {
        assertEquals("description", defaultProperty.getDescription());
        assertEquals("description2", defaultProperty2.getDescription());
        assertEquals("description3", defaultProperty3.getDescription());
    }

    @Test
    void testIsMandatory() {
        assertTrue(defaultProperty.isMandatory());
        assertFalse(defaultProperty2.isMandatory());
        assertFalse(defaultProperty3.isMandatory());
    }

    @Test
    void testGetValue() {
        assertEquals(defaultProperty.getValue(), null);
        assertEquals(defaultProperty2.getValue(), new Integer(2));
        assertEquals(defaultProperty3.getValue(), null);
    }

    @Test
    void testGetValueType() {
        assertEquals(String.class, defaultProperty.getValueType());
        assertEquals(Integer.class, defaultProperty2.getValueType());
        assertEquals(InputStream.class, defaultProperty3.getValueType());
    }


    @Test
    void testGetCategory() {
        assertEquals(ParameterType.DEFAULT_CATEGORY, defaultProperty.getCategory());
        assertEquals(ParameterType.DEFAULT_CATEGORY, defaultProperty2.getCategory());
        assertEquals(ParameterType.DEFAULT_CATEGORY, defaultProperty3.getCategory());
    }

}
