package org.apromore.plugin;

import org.apromore.plugin.exception.PluginPropertyNotFoundException;
import org.apromore.plugin.property.ParameterType;
import org.apromore.plugin.property.PluginParameterType;
import org.apromore.plugin.property.RequestParameterType;

/**
 * Common Request interface for all Plugins. The basic version just contains the user supplied request parameters {@link ParameterAwarePlugin} for the
 * current operation. Plugin APIs may extends this interface to provide advanced request parameter handling, when the built-in
 * {@link PluginParameterType} and {@link RequestParameterType} mechanism does not work.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public interface PluginRequest {

    /**
     * Get the current value for the given {@link ParameterType} in form of a {@link ParameterType}. Please not the returned {@link ParameterType} will
     * usually just holding the value, all other methods may just return NULL.
     *
     * @param pluginParameter which the {@link Plugin} defined
     * @return {@link ParameterType} holding the request value or the default {@link ParameterType} if request does not contain the parameter
     * @throws PluginPropertyNotFoundException if the property was not set and {link {@link ParameterType#isMandatory()} was true
     */
    <T> ParameterType<T> getRequestParameter(ParameterType<T> pluginParameter) throws PluginPropertyNotFoundException;

}
