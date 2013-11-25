package org.apromore.service;

import org.apromore.exception.ExceptionDao;
import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.plugin.metric.MetricPlugin;

import javax.xml.bind.JAXBException;
import java.util.Collection;
import java.util.Set;

/**
 * Service for the Metric calculations for process Models.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface MetricService {

    /**
     * List all available Metric plugins.
     * @return Set of plugins
     */
    Collection<MetricPlugin> listAll();

    /**
     * Finds first metric plugin using the criteria defined.
     * @param pluginName the plugin name of the metric calculation
     * @return The found plugin with that name.
     * @throws org.apromore.plugin.exception.PluginNotFoundException
     */
    MetricPlugin findByName(String pluginName) throws PluginNotFoundException;

    /**
     * Finds first metric plugin using the criteria defined.
     * @param pluginName the plugin name of the metric calculation
     * @param versionNumber the version of the plugin we want. could be more than one version.
     * @return The found plugin with that name and version
     * @throws org.apromore.plugin.exception.PluginNotFoundException
     */
    MetricPlugin findByNameAndVersion(String pluginName, String versionNumber) throws PluginNotFoundException;


}
