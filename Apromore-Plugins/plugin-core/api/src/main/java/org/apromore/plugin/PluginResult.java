package org.apromore.plugin;

import java.util.List;

import org.apromore.plugin.message.PluginMessage;

/**
 * Common result interface for all Plugins. The basic version just contains a way of reading the list of {@link PluginMessage}. Plugin APIs may extend
 * this interface to add their own results. Look at the Canoniser API for an example of extending this interface.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public interface PluginResult {

    /**
     * Get a list of messages that were added during the operation.
     *
     * @return List of {@link PluginMessage}
     */
    List<PluginMessage> getPluginMessage();

}
