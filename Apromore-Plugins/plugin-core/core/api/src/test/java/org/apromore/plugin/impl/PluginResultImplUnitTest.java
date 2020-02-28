/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.plugin.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apromore.plugin.PluginResultImpl;
import org.junit.Before;
import org.junit.Test;

public class PluginResultImplUnitTest {

    private PluginResultImpl pluginResultImpl;

    @Before
    public void setUp() throws Exception {
        pluginResultImpl = new PluginResultImpl();
        pluginResultImpl.addPluginMessage("test");
    }

    @Test
    public void testGetPluginMessage() {
        assertNotNull(pluginResultImpl.getPluginMessage());
        assertEquals(1, pluginResultImpl.getPluginMessage().size());
    }

    @Test
    public void testAddPluginMessageStringObjectArray() {
        pluginResultImpl.addPluginMessage("test {0}", new Integer(1));
        assertNotNull(pluginResultImpl.getPluginMessage());
        assertEquals(2, pluginResultImpl.getPluginMessage().size());
    }

    @Test
    public void testAddPluginMessageString() {
        pluginResultImpl.addPluginMessage("test2");
        assertEquals(2, pluginResultImpl.getPluginMessage().size());
    }

}
