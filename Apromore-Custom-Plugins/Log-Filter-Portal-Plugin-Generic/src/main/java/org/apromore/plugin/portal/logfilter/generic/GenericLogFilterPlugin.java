package org.apromore.plugin.portal.logfilter.generic;

import org.apromore.plugin.portal.generic.DefaultGenericPlugin;
import org.apromore.plugin.portal.generic.PluginContext;
import org.apromore.plugin.portal.generic.PluginInputParams;
import org.apromore.plugin.portal.generic.PluginOutputResult;
import org.apromore.plugin.portal.generic.PluginResultListener;
import org.apromore.plugin.portal.generic.exception.WrongTargetPluginException;

/**
 * @author Bruce Hoang Nguyen (30/08/2019)
 */
public abstract class GenericLogFilterPlugin extends DefaultGenericPlugin 
                            implements LogFilterPlugin, LogFilterResultListener {

    //Implement GenericPlugin interface as LogFilerPlugin interface
    @Override
    public void execute(PluginContext filterContext, PluginInputParams inputParams,
            PluginResultListener resultListener) throws Exception {
        if (filterContext instanceof LogFilterContext &&
                inputParams instanceof LogFilterInputParams &&
                resultListener instanceof LogFilterResultListener) {
           this.execute((LogFilterContext)filterContext, (LogFilterInputParams)inputParams,
                   (LogFilterResultListener)resultListener);
        }
        else {
           throw new WrongTargetPluginException("Wrong plugin called!"); 
        }
    }
    
    //Implement PluginResultListener interface as LogFilerResultListener interface
    @Override
    public void onPluginExecutionFinished(PluginOutputResult outputParams) throws Exception {
        if (outputParams instanceof LogFilterOutputResult) {
            this.onPluginExecutionFinished((LogFilterOutputResult)outputParams);
        }
        else {
            throw new WrongTargetPluginException("Wrong plugin called!"); 
        }
    }

}
