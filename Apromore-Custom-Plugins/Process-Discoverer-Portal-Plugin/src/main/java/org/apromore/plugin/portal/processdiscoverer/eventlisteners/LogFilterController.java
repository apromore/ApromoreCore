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

package org.apromore.plugin.portal.processdiscoverer.eventlisteners;

import org.apromore.plugin.portal.logfilter.generic.LogFilterOutputResult;
import org.apromore.plugin.portal.logfilter.generic.LogFilterResultListener;
import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.apromore.plugin.portal.processdiscoverer.components.AbstractController;
import org.zkoss.zk.ui.event.Event;

/**
 * This class allows to filter the log via the LogFilter window UI.
 * Note that this is a different way of filtering logs by mouse/keyboard shortcuts
 * which is managed directly by ProcessDiscovererController
 * 
 * @author Bruce Nguyen
 *
 */
public class LogFilterController extends AbstractController implements LogFilterResultListener {
    public LogFilterController(PDController controller) {
        super(controller);
    }

    @Override
    // Open LogFilter window
    public void onEvent(Event event) throws Exception {
        throw new Exception("This class has been replaced with LogFilterControllerWithAPMLog");
    }

    @Override
    public void onPluginExecutionFinished(LogFilterOutputResult outputParams) throws Exception {
        throw new Exception("This class has been replaced with LogFilterControllerWithAPMLog");
    }

    public void subscribeFilterResult() {}

    public void clearFilter() throws Exception {}
}
