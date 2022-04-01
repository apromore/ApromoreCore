/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

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
