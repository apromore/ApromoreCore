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
