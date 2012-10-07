package org.apromore.manager.client.helper;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashSet;

import org.apromore.model.PluginMessages;
import org.apromore.model.PluginProperties;
import org.apromore.plugin.Plugin;
import org.apromore.plugin.message.PluginMessage;
import org.apromore.plugin.property.PluginPropertyType;
import org.junit.Test;

public class PluginHelperTest {

    @Test
    public void testConvertToRequestProperties() {
        assertNotNull(PluginHelper.convertToRequestProperties(new PluginProperties()));
    }

    @Test
    public void testConvertFromPluginProperties() {
        HashSet<PluginPropertyType<Object>> hashSet = new HashSet<PluginPropertyType<Object>>();
        assertNotNull(PluginHelper.convertFromPluginProperties(hashSet));
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
