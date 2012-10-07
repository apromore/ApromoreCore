package org.apromore.plugin.property;

import org.apromore.plugin.PluginRequest;

/**
 * Property used in a {@link PluginRequest}
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 * @param <T> type of Property
 */
public class RequestPropertyType<T> extends PluginPropertyType<T> {

    /**
     * Create a new RequestPropertyType just by ID and set a value.
     *
     * @param id
     *            of the property
     * @param value
     *            of the property
     */
    public RequestPropertyType(final String id, final T value) {
        super(id, null, null, null, value);
    }

}
