package org.apromore.service;

import java.util.Set;

import org.apromore.plugin.Plugin;
import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.plugin.property.PropertyType;

/**
 *
 * Service for common Plugin operations, like list all installed Plugins, install a new Plugin, get Plugin meta data.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 *
 */
public interface PluginService {

    Set<Plugin> listAll();

    Set<Plugin> listByType(String type);

    Set<Plugin> listByName(String name);

    Plugin findByName(String name) throws PluginNotFoundException;

    Plugin findByNameAndVersion(String name, String version) throws PluginNotFoundException;

    Set<PropertyType<?>> getOptionalProperties(String name, String version) throws PluginNotFoundException;

    Set<PropertyType<?>> getMandatoryProperties(String name, String version) throws PluginNotFoundException;

}
