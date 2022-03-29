/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2013 Felix Mannhardt.
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Default implementation of an Apromore Plugin. Unless you are sure about it you should always use this implementation as superclass for your Plugin.
 * It automatically reads reads meta data from a '.config' file. This file has to be in the 'ISO 8859-1' character encoding and follow all rules of a
 * Java Properties file.
 *
 * <p>
 * The '.config' files has to be named according to the following rules and be placed in the '.' directory in the classpath of your plugin. The
 * configuration file always end with '.config' and is prefixed by the canonical name of the Plugin class. (as returned by
 * {@link Class#getCanonicalName}) For example:
 *
 * <p>
 * org.apromore.plugin.DefaultPlugin.config for the DefaultPlugin
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public abstract class DefaultPlugin implements Plugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultPlugin.class);

    private static final String DEFAULT_VALUE = "N/A";
    private static final String CONFIG_SUFFIX = ".config";

    protected static PluginResultImpl newPluginResult() {
        return new PluginResultImpl();
    }

    protected static PluginRequestImpl newPluginRequest() {
        return new PluginRequestImpl();
    }

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
            String pluginConfigName = getConfigFileName();
            if (this.getClass().getResource(pluginConfigName) != null) {
                try (InputStream configStream = this.getClass().getResourceAsStream(pluginConfigName)) {
                    getPluginConfiguration().load(configStream);
                    configStream.close();
                }
            }
        } catch (IOException e) {
            LOGGER.warn("Could not load config file with name " + getConfigFileName() + " for Plugin " + getClass().getName()
                    + ". Default values will be used!", e);
        }
    }

    private String getConfigFileName() {
        return "/" + this.getClass().getCanonicalName() + CONFIG_SUFFIX;
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
        return getPluginConfiguration().getProperty("plugin.version", DEFAULT_VALUE);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.Plugin#getType()
     */
    @Override
    public String getType() {
        return getPluginConfiguration().getProperty("plugin.type", DEFAULT_VALUE);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.Plugin#getDescription()
     */
    @Override
    public String getDescription() {
        return getPluginConfiguration().getProperty("plugin.description", DEFAULT_VALUE);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.Plugin#getAuthor()
     */
    @Override
    public String getAuthor() {
        return getPluginConfiguration().getProperty("plugin.author", DEFAULT_VALUE);
    }

    /* (non-Javadoc)
     * @see org.apromore.plugin.Plugin#getEMail()
     */
    @Override
    public String getEMail() {
        return getPluginConfiguration().getProperty("plugin.email", DEFAULT_VALUE);
    }

    /**
     * Returns the configuration option with the specified name. If no configuration option is found, then NULL is returned.
     *
     * @param name
     *            of the configuration option
     * @return value of the configuration
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
     * @return value of the configuration
     */
    public String getConfigurationByName(final String name, final String defaultValue) {
        return pluginConfiguration.getProperty(name, defaultValue);
    }

    private Properties getPluginConfiguration() {
        return pluginConfiguration;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
    	return Objects.hash(getName(),getVersion());
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        DefaultPlugin plugin = (DefaultPlugin) obj;
        // We just compare Name and Version, a Plugin is equals to another is Name and Version match
        return Objects.equals(getName(), plugin.getName()) && Objects.equals(getVersion(), plugin.getVersion());
        
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new StringBuffer().append(getClass()
        		.getSimpleName()).append("type="+getType())
        		.append("name="+getName())
        		.append("version="+getVersion()).toString();
    }

}
