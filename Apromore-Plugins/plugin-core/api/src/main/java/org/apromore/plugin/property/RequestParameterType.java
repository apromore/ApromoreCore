package org.apromore.plugin.property;

import org.apromore.plugin.PluginRequest;

/**
 * Parameter used in a {@link PluginRequest}
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 * @param <T> type of {@link ParameterType}
 */
public class RequestParameterType<T> extends PluginParameterType<T> {

    /**
     * Create a new {@link RequestParameterType} just by ID and set a value.
     *
     * @param id
     *            of the parameter
     * @param value
     *            of the parameter
     */
    public RequestParameterType(final String id, final T value) {
        super(id, null, null, null, value);
    }

}
