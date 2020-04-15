package org.apromore.plugin.portal.processdiscoverer.impl.factory;

import org.apromore.logman.ALog;
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
import org.apromore.plugin.portal.processdiscoverer.data.InvalidDataException;
import org.apromore.plugin.portal.processdiscoverer.data.LogData;
import org.apromore.plugin.portal.processdiscoverer.data.OutputData;
import org.apromore.plugin.portal.processdiscoverer.data.UserOptionsData;
import org.apromore.plugin.portal.processdiscoverer.impl.json.ProcessJSONVisualizer;
import org.apromore.plugin.portal.processdiscoverer.vis.ProcessVisualizer;
import org.apromore.processdiscoverer.Abstraction;
import org.apromore.service.EventLogService;
import org.deckfour.xes.model.XLog;

public class PDStandardFactory implements PDFactory {

    @Override
    public ContextData createContextData(PortalContext portalContext, 
            String domain,
            int logId, String logName,
            int containingFolderId, String containingFolderName, 
            ConfigData configData)  throws Exception {
        return new ContextData(portalContext, domain, logId, logName, containingFolderId, containingFolderName, configData);
    }

    @Override
    public LogData createLogData(ContextData contextData, EventLogService eventLogService)  throws Exception {
        XLog xlog = eventLogService.getXLog(contextData.getLogId());
        if (xlog == null) {
            throw new InvalidDataException("XLog data is null");
        }
        ALog aLog = new ALog(xlog);
        return new LogData(contextData.getConfigData(), aLog);
    }

    @Override
    public ConfigData createConfigData()  throws Exception {
        return new ConfigData();
    }

    @Override
    public UserOptionsData createUserOptionsData()  throws Exception {
        return new UserOptionsData();
    }

    @Override
    public OutputData createOutputData(Abstraction currentAbstraction, String visualizedText)  throws Exception{
        return new OutputData(currentAbstraction, visualizedText);
    }

    @Override
    public GraphVisController createGraphVisController(PDController pdController)  throws Exception {
        return new GraphVisController(pdController);
    }

    @Override
    public GraphSettingsController createGraphSettingsController(PDController pdController)  throws Exception {
        return new GraphSettingsController(pdController);
    }

    @Override
    public ViewSettingsController createViewSettingsController(PDController pdController) throws Exception {
        return new ViewSettingsController(pdController);
    }

    @Override
    public LogStatsController createLogStatsController(PDController pdController) throws Exception {
        return new LogStatsController(pdController);
    }

    @Override
    public TimeStatsController createTimeStatsController(PDController pdController) throws Exception {
        return new TimeStatsController(pdController);
    }

    @Override
    public CaseDetailsController createCaseDetailsController(PDController pdController) throws Exception {
        return new CaseDetailsController(pdController);
    }

    @Override
    public PerspectiveDetailsController createPerspectiveDetailsController(PDController pdController) throws Exception {
        return new PerspectiveDetailsController(pdController);
    }

    @Override
    public LogFilterController createLogFilterController(PDController pdController) throws Exception {
        return new LogFilterController(pdController);
    }

    @Override
    public AnimationController createAnimationController(PDController pdController) throws Exception {
        return new AnimationController(pdController);
    }

    @Override
    public BPMNExportController createBPMNExportController(PDController pdController) throws Exception {
        return new BPMNExportController(pdController, false);
    }

    @Override
    public LogExportController createLogExportController(PDController pdController) throws Exception {
        return new LogExportController(pdController);
    }

    @Override
    public ProcessVisualizer createProcessVisualizer(PDController pdController) throws Exception {
        return new ProcessJSONVisualizer(pdController);
    }

}
