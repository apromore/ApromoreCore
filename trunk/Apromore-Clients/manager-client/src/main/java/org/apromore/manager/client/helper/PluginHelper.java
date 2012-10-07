package org.apromore.manager.client.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.activation.DataHandler;

import org.apromore.model.ObjectFactory;
import org.apromore.model.PluginInfo;
import org.apromore.model.PluginMessages;
import org.apromore.model.PluginProperties;
import org.apromore.model.PluginProperty;
import org.apromore.plugin.Plugin;
import org.apromore.plugin.message.PluginMessage;
import org.apromore.plugin.message.SimplePluginMessage;
import org.apromore.plugin.property.PropertyType;
import org.apromore.plugin.property.RequestPropertyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Helps converting the XML representation of Messages and Properties of {@see PluginResult} and {@see PluginRequest} forth and back.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class PluginHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginHelper.class);

    private static ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    private PluginHelper() {
        super();
    }

    public static Set<RequestPropertyType<?>> convertToRequestProperties(final PluginProperties xmlProperties) {
        Set<RequestPropertyType<?>> properties = new HashSet<RequestPropertyType<?>>();
        if (xmlProperties != null) {
            for (PluginProperty xmlProp : xmlProperties.getProperty()) {
                String clazz = xmlProp.getClazz();
                Class<?> propertyClass;
                try {
                    propertyClass = PropertyType.class.getClassLoader().loadClass(clazz);
                    RequestPropertyType<?> property = createPluginProperty(xmlProp, propertyClass);
                    properties.add(property);
                } catch (ClassNotFoundException e) {
                    LOGGER.error("Could not convert request property", e);
                }
            }
        }
        return properties;
    }

    private static RequestPropertyType<?> createPluginProperty(final PluginProperty xmlProp, final Class<?> propertyClass) {
        Object value = xmlProp.getValue();
        Object convertedValue = null;
        if (value instanceof DataHandler) {
            try {
                convertedValue = ((DataHandler) value).getInputStream();
            } catch (IOException e) {
                LOGGER.error("Could not convert InputStream property", e);
            }
        } else {
            convertedValue = value;
        }
        RequestPropertyType<?> property = new RequestPropertyType<Object>(xmlProp.getId(), convertedValue);
        return property;
    }

    public static PluginProperties convertFromPluginProperties(final Set<? extends PropertyType<?>> pluginProperties) {
        PluginProperties xmlProperties = OBJECT_FACTORY.createPluginProperties();
        for (PropertyType<?> p : pluginProperties) {
            PluginProperty xmlProp = OBJECT_FACTORY.createPluginProperty();
            xmlProp.setId(p.getId());
            xmlProp.setName(p.getName());
            xmlProp.setDescription(p.getDescription());
            xmlProp.setIsMandatory(p.isMandatory());
            xmlProp.setClazz(p.getValueType().getName());
            if (p.getValue() instanceof InputStream) {
                // Property will be rendered as FileInput
                xmlProp.setValue(null);
            } else {
                xmlProp.setValue(p.getValue());
            }
            xmlProperties.getProperty().add(xmlProp);
        }
        return xmlProperties;
    }

    public static PluginMessages convertFromPluginMessages(final List<? extends PluginMessage> messages) {
        PluginMessages xmlMessages = OBJECT_FACTORY.createPluginMessages();
        for (PluginMessage msg : messages) {
            org.apromore.model.PluginMessage xmlMessage = OBJECT_FACTORY.createPluginMessage();
            xmlMessage.setValue(msg.getMessage());
            xmlMessages.getMessage().add(xmlMessage);
        }
        return xmlMessages;
    }

    public static List<PluginMessage> convertToPluginMessages(final PluginMessages message) {
        List<PluginMessage> messageList = new ArrayList<PluginMessage>();
        if (message != null) {
            for (org.apromore.model.PluginMessage msg : message.getMessage()) {
                messageList.add(new SimplePluginMessage(msg.getValue()));
            }
        }
        return messageList;
    }

    public static PluginInfo convertPluginInfo(final Plugin plugin) {
        PluginInfo pluginInfo = new PluginInfo();
        pluginInfo.setName(plugin.getName());
        pluginInfo.setVersion(plugin.getVersion());
        pluginInfo.setDescription(plugin.getDescription());
        pluginInfo.setType(plugin.getType());
        pluginInfo.setAuthor(plugin.getAuthor());
        return pluginInfo;
    }

}
