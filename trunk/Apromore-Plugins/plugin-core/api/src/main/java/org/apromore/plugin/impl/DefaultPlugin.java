package org.apromore.plugin.impl;

import java.io.IOException;
import java.util.Properties;

import org.apromore.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of an Apromore Plugin. Unless you are sure about it you should always use this implementation as superclass for your Plugin.
 * It automatically reads reads meta data from a 'plugin.config' file. This file has to be in the 'ISO 8859-1' character encoding and follow all rules
 * of a Java Properties file.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public abstract class DefaultPlugin implements Plugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultPlugin.class);

    /**
     * Properties of the Plugin
     */
    private final Properties pluginConfiguration;

    /**
     * Creates a DefaultPlugin and reads meta data of the 'plugin.config' that has to be on the class path of the Plugin. This file has to be in the
     * 'ISO 8859-1' character encoding and follow all rules of a Java Properties file.
     */
    public DefaultPlugin() {
        super();
        pluginConfiguration = new Properties();
        try {
            getPluginConfiguration().load(this.getClass().getResourceAsStream("/plugin.config"));
        } catch (IOException e) {
            LOGGER.warn("Could not load plugin.config file for Plugin " + getClass().getSimpleName() + ". Default values will be used!", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.Plugin#getName()
     */
    @Override
    public String getName() {
        return getPluginConfiguration().getProperty("plugin.name", getClass().getSimpleName());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.Plugin#getVersion()
     */
    @Override
    public String getVersion() {
        return getPluginConfiguration().getProperty("plugin.version", "N/A");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.Plugin#getType()
     */
    @Override
    public String getType() {
        return getPluginConfiguration().getProperty("plugin.type", "N/A");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.Plugin#getDescription()
     */
    @Override
    public String getDescription() {
        return getPluginConfiguration().getProperty("plugin.description", "N/A");
    }

    /**
     * Returns the configuration option with the specified name. If no configuration option is found, then NULL is returned.
     *
     * @param name
     *            of the configuration option
     * @return
     */
    public String getConfigurationByName(final String name) {
        return pluginConfiguration.getProperty(name);
    }

    /**
     * Returns the configuration option with the specified name. If no configuration option is found, then the default value is returned.
     *
     * @param name
     *            of the configuration option
     * @param defaultValue
     *            if no configuration option is found
     * @return
     */
    public String getConfigurationByName(final String name, final String defaultValue) {
        return pluginConfiguration.getProperty(name, defaultValue);
    }

    private Properties getPluginConfiguration() {
        return pluginConfiguration;
    }

}
