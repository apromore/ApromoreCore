/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 Felix Mannhardt.
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

package org.apromore.manager.client.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;

import org.apromore.manager.client.PluginHelper;
import org.apromore.plugin.Plugin;
import org.apromore.plugin.message.PluginMessage;
import org.apromore.plugin.property.PluginParameterType;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.model.PluginMessages;
import org.apromore.portal.model.PluginParameter;
import org.apromore.portal.model.PluginParameters;
import org.junit.jupiter.api.Test;

class PluginHelperUnitTest {

    @Test
    void testConvertToRequestProperties() throws IOException {
        PluginParameters xmlProperties = new PluginParameters();

        PluginParameter prop1 = new PluginParameter();
        prop1.setClazz(Integer.class.getName());
        prop1.setId("test");
        prop1.setValue(new Integer(2));

        PluginParameter prop2 = new PluginParameter();
        prop2.setClazz(InputStream.class.getName());
        prop2.setId("test2");
        byte[] byteInput = new byte[20];

        prop2.setValue(new DataHandler(new ByteArrayDataSource(new ByteArrayInputStream(byteInput), "application/octet-stream")));
        xmlProperties.getParameter().add(prop1);
        xmlProperties.getParameter().add(prop2);

        Set<RequestParameterType<?>> requestProperties = PluginHelper.convertToRequestParameters(xmlProperties);
        assertNotNull(requestProperties);
        assertTrue(requestProperties.size() == 2);

        RequestParameterType<?> rProp1 = getPropertyById("test", requestProperties);
        RequestParameterType<?> rProp2 = getPropertyById("test2", requestProperties);

        assertNotNull(rProp1);
        assertEquals(new Integer(2), rProp1.getValue());
        assertNotNull(rProp2);
        assertTrue(rProp2.getValue() instanceof InputStream);
    }

    private static RequestParameterType<?> getPropertyById(final String id, final Set<RequestParameterType<?>> requestProperties) {
        for (RequestParameterType<?> prop: requestProperties) {
            if (id.equals(prop.getId())) {
                return prop;
            }
        }
        fail("Property not found "+id);
        return null;
    }

    @Test
    void testConvertToRequestProperty() {
        PluginParameter xmlProp = new PluginParameter();
        assertNull(PluginHelper.convertToRequestParameter(xmlProp));
        xmlProp.setClazz(String.class.getCanonicalName());
        assertNull(PluginHelper.convertToRequestParameter(xmlProp));

        PluginParameter xmlProp2 = new PluginParameter();
        xmlProp2.setId("test");
        assertNull(PluginHelper.convertToRequestParameter(xmlProp2));

        PluginParameter xmlProp3 = new PluginParameter();
        xmlProp3.setId("test");
        xmlProp3.setClazz(String.class.getCanonicalName());
        xmlProp3.setValue("test");
        assertNotNull(PluginHelper.convertToRequestParameter(xmlProp3));
    }

    @Test
    void testConvertFromPluginProperties() {
        HashSet<PluginParameterType<?>> hashSet = new HashSet<PluginParameterType<?>>();
        hashSet.add(new PluginParameterType<InputStream>("test", "testName", InputStream.class, "testDescr", true));
        hashSet.add(new PluginParameterType<String>("test2", "testName", String.class, "testDescr", false));
        PluginParameters pluginProperties = PluginHelper.convertFromPluginParameters(hashSet);
        assertNotNull(pluginProperties);

        assertEquals(2, pluginProperties.getParameter().size());

        PluginParameter prop1 = getPropertyById("test", pluginProperties.getParameter());
        PluginParameter prop2 = getPropertyById("test2", pluginProperties.getParameter());

        assertNotNull(prop1);
        assertEquals(InputStream.class.getName(), prop1.getClazz());
        assertEquals("testName", prop1.getName());
        assertEquals("testDescr", prop1.getDescription());
        assertTrue(prop1.isIsMandatory());
        assertNotNull(prop2);
        assertEquals("testName", prop2.getName());
        assertEquals("testDescr", prop2.getDescription());
        assertFalse(prop2.isIsMandatory());
        assertEquals(String.class.getName(), prop2.getClazz());
    }

    private static PluginParameter getPropertyById(final String id, final List<PluginParameter> propertyList) {
        for (PluginParameter prop: propertyList) {
            if (id.equals(prop.getId())) {
                return prop;
            }
        }
        fail("Property not found "+id);
        return null;
    }

    @Test
    void testConvertFromPluginMessages() {
        assertNotNull(PluginHelper.convertFromPluginMessages(new ArrayList<PluginMessage>()));
    }

    @Test
    void testConvertToPluginMessages() {
        assertNotNull(PluginHelper.convertToPluginMessages(new PluginMessages()));
    }

    @Test
    void testConvertPluginInfo() {
        assertNotNull(PluginHelper.convertPluginInfo(new Plugin() {

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

            @Override
            public String getAuthor() {
                return "";
            }

            @Override
            public String getEMail() {
                return "";
            }
        }));
    }

}
