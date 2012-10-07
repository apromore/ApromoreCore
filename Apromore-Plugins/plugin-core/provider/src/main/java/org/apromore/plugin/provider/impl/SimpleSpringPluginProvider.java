package org.apromore.plugin.provider.impl;

import org.apromore.plugin.Plugin;
import org.apromore.plugin.provider.PluginProviderHelper;
import org.springframework.stereotype.Service;

@Service
public class SimpleSpringPluginProvider extends PluginProviderImpl {

    public SimpleSpringPluginProvider() {
        super();
        setInternalPluginList(PluginProviderHelper.findPluginsByClass(Plugin.class, "org.apromore"));
    }

}
