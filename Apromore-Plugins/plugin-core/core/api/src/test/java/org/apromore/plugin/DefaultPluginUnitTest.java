/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2013 Felix Mannhardt.
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.apromore.plugin.DefaultPlugin;
import org.junit.Before;
import org.junit.Test;

public class DefaultPluginUnitTest {

    private final class DefaultPluginMock extends DefaultPlugin {
    }

    private DefaultPlugin plugin;

    @Before
    public void setUp() {
        plugin = new DefaultPluginMock();
    }

    @Test
    public void testDefaultPlugin() {
        assertNotNull(plugin);
    }

    @Test
    public void testGetName() {
        assertEquals("Test", plugin.getName());
    }

    @Test
    public void testGetVersion() {
        assertEquals("1.0", plugin.getVersion());
    }

    @Test
    public void testGetType() {
        assertEquals("GenericPlugin", plugin.getType());
    }

    @Test
    public void testGetDescription() {
        assertEquals("This is a nice Plugin", plugin.getDescription());
    }

    @Test
    public void testGetConfigurationByNameString() {
        assertEquals("FooBar", plugin.getConfigurationByName("myown.option"));
    }

    @Test
    public void testGetConfigurationByNameStringString() {
        assertEquals("test", plugin.getConfigurationByName("not.found", "test"));
    }

    @Test
    public void testGetAuthor() {
        assertEquals("Smith", plugin.getAuthor());
    }

    @Test
    public void testGetEMail() {
        assertEquals("smith@mail.com", plugin.getEMail());
    }

    @Test
    public void testEquals() {
        DefaultPluginMock plugin2 = new DefaultPluginMock();
        assertEquals(plugin, plugin2);
        assertEquals(plugin.hashCode(), plugin2.hashCode());
        DefaultPlugin plugin3 = new DefaultPlugin() {

            @Override
            public String getName() {
                return "___anothername";
            }

        };
        assertFalse(plugin3.equals(plugin));
        assertFalse(plugin3.hashCode() == plugin.hashCode());
    }

}
