package org.apromore.plugin.search.provider.impl;

import java.util.Collection;
import java.util.Set;

import javax.annotation.Resource;

import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.plugin.search.SearchByModelPlugin;
import org.apromore.plugin.search.provider.SearchByModelPluginProvider;
import org.springframework.stereotype.Component;

@Component
public class SearchByModelPluginProviderImpl implements SearchByModelPluginProvider {
    
    @Resource
    public Set<SearchByModelPlugin> searchByModelPluginSet;

    @Override
    public Collection<SearchByModelPlugin> listAll() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SearchByModelPlugin findByName() throws PluginNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SearchByModelPlugin findByNameAndVersion()
            throws PluginNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

}
