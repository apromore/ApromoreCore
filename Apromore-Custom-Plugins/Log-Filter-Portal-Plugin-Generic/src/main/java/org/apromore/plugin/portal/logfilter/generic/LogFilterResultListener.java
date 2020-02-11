package org.apromore.plugin.portal.logfilter.generic;

import org.apromore.plugin.portal.generic.PluginOutputResult;
import org.apromore.plugin.portal.generic.PluginResultListener;

/**
 * @author Bruce Hoang Nguyen (30/08/2019)
 */
public interface LogFilterResultListener extends PluginResultListener {
	public void onPluginExecutionFinished(LogFilterOutputResult outputParams) throws Exception;
	
	// Modules which implement this interface won't need to implement this interface from PluginResultListener
	// if they don't want
	public default void onPluginExecutionFinished(PluginOutputResult outputParams) throws Exception {
	    if (outputParams instanceof LogFilterOutputResult) {
	        onPluginExecutionFinished((LogFilterOutputResult)outputParams);
	    }
	}
}
