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
import org.apromore.plugin.PluginRequest;
import org.apromore.plugin.PluginResult;
import org.apromore.plugin.message.PluginMessage;
import org.apromore.plugin.message.PluginMessageImpl;
import org.apromore.plugin.property.PropertyType;
import org.apromore.plugin.property.RequestPropertyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Helps converting the XML representation of Messages and Properties of {@link PluginResult} and {@link PluginRequest} forth and back.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public final class PluginHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginHelper.class);

    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    private PluginHelper() {
        super();
    }

    /**
     * Converts the XML representation of Plugin properties to a Set of RequestPropertyType for usage in Plugins
     *
     * @param xmlProperties from web service
     * @return Set of RequestPropertyType
     */
    public static Set<RequestPropertyType<?>> convertToRequestProperties(final PluginProperties xmlProperties) {
        Set<RequestPropertyType<?>> properties = new HashSet<RequestPropertyType<?>>();
        if (xmlProperties != null) {
            for (PluginProperty xmlProp : xmlProperties.getProperty()) {
                String clazz = xmlProp.getClazz();
                Class<?> propertyClass;
                try {
                    propertyClass = PropertyType.class.getClassLoader().loadClass(clazz);
                    if (xmlProp.getValue() != null) {
                        properties.add(createPluginProperty(xmlProp, propertyClass));
                    } else {
                        LOGGER.warn("Request property {} is NULL", xmlProp.getName());
                    }
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
        return new RequestPropertyType<Object>(xmlProp.getId(), convertedValue);
    }

    /**
     * Converts the PropertyType of a Plugin to XML representation for transport through web service
     *
     * @param pluginProperties from Plugin
     * @return XML representation for use in web service
     */
    public static PluginProperties convertFromPluginProperties(final Set<? extends PropertyType<?>> pluginProperties) {
        PluginProperties xmlProperties = OBJECT_FACTORY.createPluginProperties();
        for (PropertyType<?> p : pluginProperties) {
            PluginProperty xmlProp = OBJECT_FACTORY.createPluginProperty();
            xmlProp.setId(p.getId());
            xmlProp.setName(p.getName());
            xmlProp.setDescription(p.getDescription());
            xmlProp.setIsMandatory(p.isMandatory());
            xmlProp.setClazz(p.getValueType().getCanonicalName());
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
                messageList.add(new PluginMessageImpl(msg.getValue()));
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
