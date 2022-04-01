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

package org.apromore.plugin.provider.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashSet;
import java.util.Set;

import org.apromore.plugin.Plugin;
import org.apromore.plugin.exception.PluginNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PluginProviderImplUnitTest {

    private PluginProviderImpl mockProvider;

    @BeforeEach
    void setUp() {
        mockProvider = new PluginProviderImpl();
        final Set<Plugin> pluginList = new HashSet<>();
        pluginList.add(new Plugin() {

            @Override
            public String getVersion() {
                return "0.1";
            }

            @Override
            public String getType() {
                return "test";
            }

            @Override
            public String getName() {
                return "test";
            }

            @Override
            public String getDescription() {
                return "description";
            }

            @Override
            public String getAuthor() {
                return "author";
            }

            @Override
            public String getEMail() {
                return "test@test.com";
            }

        });
        mockProvider.setPluginList(pluginList);
    }

    @Test
    void testListAll() throws PluginNotFoundException {
        assertNotNull(mockProvider.listAll());
        assertNotNull(mockProvider.findByName("test"));
        assertNotNull(mockProvider.findByNameAndVersion("test", "0.1"));
        try {
            mockProvider.findByName("test1");
            fail();
        } catch (PluginNotFoundException e) {

        }
        try {
            mockProvider.findByNameAndVersion("test", "0.2");
            fail();
        } catch (PluginNotFoundException e) {

        }
        assertTrue(mockProvider.listByType("test").size() == 1);
        assertTrue(mockProvider.listByName("test").size() == 1);
        assertTrue(mockProvider.listByName("test2").isEmpty());
    }

}
