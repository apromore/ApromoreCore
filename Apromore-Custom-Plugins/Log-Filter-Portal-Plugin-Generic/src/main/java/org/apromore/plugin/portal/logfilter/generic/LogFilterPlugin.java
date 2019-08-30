package org.apromore.plugin.portal.logfilter.generic;

import org.apromore.plugin.portal.generic.GenericPlugin;

/**
 * This interface is used to register this plugin as an OSGi service for
 * other plugins to call to 
 * @author Bruce Nguyen
 *
 */
public interface LogFilterPlugin extends GenericPlugin {
    public void execute(LogFilterContext filterContext, LogFilterInputParams inputParams,
            LogFilterResultListener resultListener) throws Exception;	
}
