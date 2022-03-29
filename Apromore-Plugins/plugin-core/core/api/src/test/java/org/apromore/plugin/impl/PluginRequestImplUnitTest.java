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

import java.util.HashSet;

import org.apromore.plugin.PluginRequestImpl;
import org.apromore.plugin.exception.PluginPropertyNotFoundException;
import org.apromore.plugin.property.PluginParameterType;
import org.apromore.plugin.property.RequestParameterType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PluginRequestImplUnitTest {

    private PluginRequestImpl pluginRequestImpl;
    private RequestParameterType<String> request1;
    private RequestParameterType<String> request2;

    @BeforeEach
    void setUp() throws Exception {
        pluginRequestImpl = new PluginRequestImpl();
        request1 = new RequestParameterType<String>("test1", "foobar");
        pluginRequestImpl.addRequestProperty(request1);
        request2 = new RequestParameterType<String>("test2", "barfoo");
        pluginRequestImpl.addRequestProperty(request2);
    }

    @Test
    void testGetRequestProperty() throws PluginPropertyNotFoundException {
        PluginParameterType<String> pluginProp = new PluginParameterType<String>("test", "test", String.class, "test", false);
        assertEquals(pluginProp, pluginRequestImpl.getRequestParameter(pluginProp));
        assertEquals(request1, pluginRequestImpl.getRequestParameter(new PluginParameterType<String>("test1", "test", String.class, "test", false)));
        assertEquals(request2, pluginRequestImpl.getRequestParameter(new PluginParameterType<String>("test2", "test", String.class, "test", false)));
    }

    @Test
    void testAddRequestPropertyRequestPropertyTypeOfQ() {
        pluginRequestImpl.addRequestProperty(new RequestParameterType<Integer>("test3", new Integer(2)));
    }

    @Test
    void testAddRequestPropertySetOfRequestPropertyTypeOfQ() {
        pluginRequestImpl.addRequestProperty(new HashSet<RequestParameterType<?>>());
    }

}
