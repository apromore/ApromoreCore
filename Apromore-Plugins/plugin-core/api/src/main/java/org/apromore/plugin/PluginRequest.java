package org.apromore.plugin;

import org.apromore.plugin.exception.PluginPropertyNotFoundException;
import org.apromore.plugin.property.PluginPropertyType;
import org.apromore.plugin.property.PropertyType;
import org.apromore.plugin.property.RequestPropertyType;

/**
 * Common Request interface for all Plugins. The basic version just contains the user supplied request properties {@link PropertyAwarePlugin} for the
 * current operation. Plugin APIs may extends this interface to provide advanced request parameter handling, when the built-in
 * {@link PluginPropertyType} and {@link RequestPropertyType} mechanism does not work.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public interface PluginRequest {

    /**
     * Get the current value for the given {@link PropertyType} in form of a {@link PropertyType}. Please not the returned {@link PropertyType} will
     * usually just holding the value, all other methods may just return NULL.
     *
     * @param pluginProperty that the Plugin defined
     * @return PropertyType holding the request value
     * @throws PluginPropertyNotFoundException if the property was not set and {link {@link PropertyType#isMandatory()} was true
     */
    <T> PropertyType<T> getRequestProperty(PropertyType<T> pluginProperty) throws PluginPropertyNotFoundException;

}
