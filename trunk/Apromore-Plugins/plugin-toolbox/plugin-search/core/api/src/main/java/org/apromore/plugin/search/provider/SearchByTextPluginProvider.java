package org.apromore.plugin.search.provider;

import java.util.Collection;

import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.plugin.search.SearchByTextPlugin;

public interface SearchByTextPluginProvider {

    Collection<SearchByTextPlugin> listAll();
    
    SearchByTextPlugin findByName() throws PluginNotFoundException;
    
    SearchByTextPlugin findByNameAndVersion() throws PluginNotFoundException;
    
}