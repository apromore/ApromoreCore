package org.apromore.plugin.portal.logfilter.api;

import java.io.IOException;
import org.apromore.plugin.portal.PortalContext;

/**
 * This interface is used to register this plugin as an OSGi service for
 * other plugins to call to 
 * @author Bruce Nguyen
 *
 */
public interface LogFilterPlugin {
    public void execute(PortalContext portalContext, LogFilterInputParams inputParams,
            LogFilterResultListener resultListener) throws IOException;	
}
