package org.apromore.plugin.portal.generic;

import org.apromore.plugin.portal.PortalPlugin;

/**
 * This interface is used to register this plugin as an OSGi service for
 * other plugins to call to 
 * @author Bruce Nguyen
 *
 */
public interface GenericPlugin extends PortalPlugin {
    public void execute(PluginContext filterContext, PluginInputParams inputParams,
            PluginResultListener resultListener) throws Exception;	
}
