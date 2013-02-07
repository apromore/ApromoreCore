package org.apromore.plugin.search.provider.impl;

import java.util.Collection;
import java.util.Set;

import javax.annotation.Resource;

import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.plugin.search.SearchByTextPlugin;
import org.apromore.plugin.search.provider.SearchByTextPluginProvider;
import org.springframework.stereotype.Component;

@Component
public class SearchByTextPluginProviderImpl implements SearchByTextPluginProvider {

    @Resource
    public Set<SearchByTextPlugin> searchByTextPluginSet;
    
    @Override
    public Collection<SearchByTextPlugin> listAll() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SearchByTextPlugin findByName() throws PluginNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SearchByTextPlugin findByNameAndVersion()
            throws PluginNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

}
