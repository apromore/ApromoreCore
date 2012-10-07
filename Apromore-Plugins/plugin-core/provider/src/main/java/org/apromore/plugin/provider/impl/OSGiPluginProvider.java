package org.apromore.plugin.provider.impl;

import java.util.Set;

import org.apromore.plugin.Plugin;
import org.springframework.stereotype.Service;

@Service("osgiPluginProviderImpl")
public class OSGiPluginProvider extends PluginProviderImpl {

    public Set<Plugin> getPluginSet() {
        return getInternalPluginSet();
    }

    public void setPluginSet(final Set<Plugin> pluginSet) {
        setInternalPluginList(pluginSet);
    }


}
