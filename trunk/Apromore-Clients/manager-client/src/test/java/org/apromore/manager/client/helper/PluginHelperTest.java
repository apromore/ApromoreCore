package org.apromore.manager.client.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;

import org.apromore.model.PluginMessages;
import org.apromore.model.PluginProperties;
import org.apromore.model.PluginProperty;
import org.apromore.plugin.Plugin;
import org.apromore.plugin.message.PluginMessage;
import org.apromore.plugin.property.PluginPropertyType;
import org.apromore.plugin.property.RequestPropertyType;
import org.junit.Test;

public class PluginHelperTest {

    @Test
    public void testConvertToRequestProperties() throws IOException {
        PluginProperties xmlProperties = new PluginProperties();

        PluginProperty prop1 = new PluginProperty();
        prop1.setClazz(Integer.class.getName());
        prop1.setId("test");
        prop1.setValue(new Integer(2));

        PluginProperty prop2 = new PluginProperty();
        prop2.setClazz(InputStream.class.getName());
        prop2.setId("test2");
        byte[] byteInput = new byte[20];

        prop2.setValue(new DataHandler(new ByteArrayDataSource(new ByteArrayInputStream(byteInput), "application/octet-stream")));
        xmlProperties.getProperty().add(prop1);
        xmlProperties.getProperty().add(prop2);

        Set<RequestPropertyType<?>> requestProperties = PluginHelper.convertToRequestProperties(xmlProperties);
        assertNotNull(requestProperties);
        assertTrue(requestProperties.size() == 2);

        RequestPropertyType<?> rProp1 = getPropertyById("test", requestProperties);
        RequestPropertyType<?> rProp2 = getPropertyById("test2", requestProperties);

        assertNotNull(rProp1);
        assertEquals(new Integer(2), rProp1.getValue());
        assertNotNull(rProp2);
        assertTrue(rProp2.getValue() instanceof InputStream);
    }

    private static RequestPropertyType<?> getPropertyById(final String id, final Set<RequestPropertyType<?>> requestProperties) {
        for (RequestPropertyType<?> prop: requestProperties) {
            if (id.equals(prop.getId())) {
                return prop;
            }
        }
        fail("Property not found "+id);
        return null;
    }

    @Test
    public void testConvertFromPluginProperties() {
        HashSet<PluginPropertyType<?>> hashSet = new HashSet<PluginPropertyType<?>>();
        hashSet.add(new PluginPropertyType<InputStream>("test", "testName", InputStream.class, "testDescr", true));
        hashSet.add(new PluginPropertyType<String>("test2", "testName", String.class, "testDescr", false));
        PluginProperties pluginProperties = PluginHelper.convertFromPluginProperties(hashSet);
        assertNotNull(pluginProperties);

        assertEquals(2, pluginProperties.getProperty().size());

        PluginProperty prop1 = getPropertyById("test", pluginProperties.getProperty());
        PluginProperty prop2 = getPropertyById("test2", pluginProperties.getProperty());

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

    private static PluginProperty getPropertyById(final String id, final List<PluginProperty> propertyList) {
        for (PluginProperty prop: propertyList) {
            if (id.equals(prop.getId())) {
                return prop;
            }
        }
        fail("Property not found "+id);
        return null;
    }

    @Test
    public void testConvertFromPluginMessages() {
        assertNotNull(PluginHelper.convertFromPluginMessages(new ArrayList<PluginMessage>()));
    }

    @Test
    public void testConvertToPluginMessages() {
        assertNotNull(PluginHelper.convertToPluginMessages(new PluginMessages()));
    }

    @Test
    public void testConvertPluginInfo() {
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
        }));
    }

}
