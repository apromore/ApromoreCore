/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2013 Felix Mannhardt.
 * Copyright (C) 2014 - 2017 Queensland University of Technology.
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

package org.apromore.plugin.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apromore.plugin.PluginResultImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PluginResultImplUnitTest {

    private PluginResultImpl pluginResultImpl;

    @BeforeEach
    void setUp() throws Exception {
        pluginResultImpl = new PluginResultImpl();
        pluginResultImpl.addPluginMessage("test");
    }

    @Test
    void testGetPluginMessage() {
        assertNotNull(pluginResultImpl.getPluginMessage());
        assertEquals(1, pluginResultImpl.getPluginMessage().size());
    }

    @Test
    void testAddPluginMessageStringObjectArray() {
        pluginResultImpl.addPluginMessage("test {0}", new Integer(1));
        assertNotNull(pluginResultImpl.getPluginMessage());
        assertEquals(2, pluginResultImpl.getPluginMessage().size());
    }

    @Test
    void testAddPluginMessageString() {
        pluginResultImpl.addPluginMessage("test2");
        assertEquals(2, pluginResultImpl.getPluginMessage().size());
    }

}
