package org.apromore.plugin.portal.generic;

import org.apromore.plugin.portal.PortalContext;

public abstract class PluginContext {
    private PortalContext portalContext;
    public PluginContext(PortalContext portalContext) {
        this.portalContext = portalContext;
    }
    
    public PortalContext getPortalContext() {
        return this.portalContext;
    }
}
