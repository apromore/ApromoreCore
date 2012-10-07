package org.apromore.service.impl;

import java.util.Collections;
import java.util.Set;

import org.apromore.plugin.Plugin;
import org.apromore.plugin.PropertyAwarePlugin;
import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.plugin.property.PropertyType;
import org.apromore.plugin.provider.PluginProvider;
import org.apromore.service.PluginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
*
* Service for common Plugin operations, like list all installed Plugins, install a new Plugin, get Plugin meta data.
*
* @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
*
*/
@Service("PluginService")
public class PluginServiceImpl implements PluginService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginServiceImpl.class);

    @Autowired
    @Qualifier("PluginProvider")
    private PluginProvider canoniserProvider;

    /* (non-Javadoc)
     * @see org.apromore.service.PluginService#listAll()
     */
    @Override
    public Set<Plugin> listAll() {
        return canoniserProvider.listAll();
    }

    /* (non-Javadoc)
     * @see org.apromore.service.PluginService#listByType(java.lang.String)
     */
    @Override
    public Set<Plugin> listByType(final String type) {
        return canoniserProvider.listByType(type);
    }

    /* (non-Javadoc)
     * @see org.apromore.service.PluginService#listByName(java.lang.String)
     */
    @Override
    public Set<Plugin> listByName(final String name) {
        return canoniserProvider.listByName(name);
    }

    /* (non-Javadoc)
     * @see org.apromore.service.PluginService#findByName(java.lang.String)
     */
    @Override
    public Plugin findByName(final String name) throws PluginNotFoundException {
        return canoniserProvider.findByName(name);
    }

    /* (non-Javadoc)
     * @see org.apromore.service.PluginService#findByNameAndVersion(java.lang.String, java.lang.String)
     */
    @Override
    public Plugin findByNameAndVersion(final String name, final String version) throws PluginNotFoundException {
        return canoniserProvider.findByNameAndVersion(name, version);
    }

    /* (non-Javadoc)
     * @see org.apromore.service.PluginService#getOptionalProperties(java.lang.String, java.lang.String)
     */
    @Override
    public Set<PropertyType<?>> getOptionalProperties(final String name, final String version) throws PluginNotFoundException {
        Plugin plugin = canoniserProvider.findByNameAndVersion(name, version);
        if (plugin instanceof PropertyAwarePlugin) {
            return ((PropertyAwarePlugin) plugin).getOptionalProperties();
        } else {
            Set<PropertyType<?>> emptySet = Collections.emptySet();
            return Collections.unmodifiableSet(emptySet);
        }
    }

    /* (non-Javadoc)
     * @see org.apromore.service.PluginService#getMandatoryProperties(java.lang.String, java.lang.String)
     */
    @Override
    public Set<PropertyType<?>> getMandatoryProperties(final String name, final String version) throws PluginNotFoundException {
        Plugin plugin = canoniserProvider.findByNameAndVersion(name, version);
        if (plugin instanceof PropertyAwarePlugin) {
            return ((PropertyAwarePlugin) plugin).getMandatoryProperties();
        } else {
            Set<PropertyType<?>> emptySet = Collections.emptySet();
            return Collections.unmodifiableSet(emptySet);
        }
    }



}
