package org.apromore.plugin.search.provider;

import java.util.Collection;

import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.plugin.search.SearchByModelPlugin;

public interface SearchByModelPluginProvider {

    Collection<SearchByModelPlugin> listAll();
    
    SearchByModelPlugin findByName() throws PluginNotFoundException;
    
    SearchByModelPlugin findByNameAndVersion() throws PluginNotFoundException;

}
