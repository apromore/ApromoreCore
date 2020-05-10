/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
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
package org.apromore.plugin.portal.processdiscoverer.impl.factory;

import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.apromore.plugin.portal.processdiscoverer.controllers.AnimationController;
import org.apromore.plugin.portal.processdiscoverer.controllers.BPMNExportController;
import org.apromore.plugin.portal.processdiscoverer.controllers.CaseDetailsController;
import org.apromore.plugin.portal.processdiscoverer.controllers.GraphSettingsController;
import org.apromore.plugin.portal.processdiscoverer.controllers.GraphVisController;
import org.apromore.plugin.portal.processdiscoverer.controllers.LogExportController;
import org.apromore.plugin.portal.processdiscoverer.controllers.LogFilterController;
import org.apromore.plugin.portal.processdiscoverer.controllers.LogStatsController;
import org.apromore.plugin.portal.processdiscoverer.controllers.PerspectiveDetailsController;
import org.apromore.plugin.portal.processdiscoverer.controllers.TimeStatsController;
import org.apromore.plugin.portal.processdiscoverer.controllers.ViewSettingsController;
import org.apromore.plugin.portal.processdiscoverer.data.ConfigData;
import org.apromore.plugin.portal.processdiscoverer.data.ContextData;
import org.apromore.plugin.portal.processdiscoverer.data.LogData;
import org.apromore.plugin.portal.processdiscoverer.data.OutputData;
import org.apromore.plugin.portal.processdiscoverer.data.UserOptionsData;
import org.apromore.plugin.portal.processdiscoverer.vis.ProcessVisualizer;
import org.apromore.processdiscoverer.Abstraction;
import org.apromore.service.EventLogService;

public interface PDFactory {
    ContextData createContextData(PortalContext portalContext, 
            String domain,
            int logId, String logName,
            int containingFolderId, String containingFolderName, 
            ConfigData configData)  throws Exception;
    LogData createLogData(ContextData contextData, EventLogService eventLogService)  throws Exception;
    ConfigData createConfigData()  throws Exception;
    UserOptionsData createUserOptionsData()  throws Exception;
    OutputData createOutputData(Abstraction currentAbstraction, String visualizedText)  throws Exception;
    
    GraphVisController createGraphVisController(PDController pdController) throws Exception;
    GraphSettingsController createGraphSettingsController(PDController pdController) throws Exception;
    ViewSettingsController createViewSettingsController(PDController pdController) throws Exception;
    LogStatsController createLogStatsController(PDController pdController) throws Exception;
    TimeStatsController createTimeStatsController(PDController pdController) throws Exception;
    CaseDetailsController createCaseDetailsController(PDController pdController) throws Exception;
    PerspectiveDetailsController createPerspectiveDetailsController(PDController pdController) throws Exception;
    LogFilterController createLogFilterController(PDController pdController) throws Exception;
    AnimationController createAnimationController(PDController pdController) throws Exception;
    BPMNExportController createBPMNExportController(PDController pdController) throws Exception;
    LogExportController createLogExportController(PDController pdController) throws Exception;
    
    ProcessVisualizer createProcessVisualizer(PDController pdController) throws Exception;
}
