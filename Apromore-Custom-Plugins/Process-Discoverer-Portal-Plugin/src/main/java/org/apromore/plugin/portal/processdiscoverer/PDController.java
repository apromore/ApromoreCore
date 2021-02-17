/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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

package org.apromore.plugin.portal.processdiscoverer;

import static org.apromore.logman.attribute.graph.MeasureType.DURATION;
import static org.apromore.logman.attribute.graph.MeasureType.FREQUENCY;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apromore.logman.attribute.IndexableAttribute;
import org.apromore.logman.attribute.graph.MeasureAggregation;
import org.apromore.logman.attribute.graph.MeasureRelation;
import org.apromore.logman.attribute.graph.MeasureType;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalPlugin;
import org.apromore.plugin.portal.loganimation.api.LogAnimationPluginInterface;
import org.apromore.plugin.portal.logfilter.generic.LogFilterPlugin;
import org.apromore.plugin.portal.processdiscoverer.controllers.CaseDetailsController;
import org.apromore.plugin.portal.processdiscoverer.controllers.GraphSettingsController;
import org.apromore.plugin.portal.processdiscoverer.controllers.GraphVisController;
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
import org.apromore.plugin.portal.processdiscoverer.impl.factory.PDFactory;
import org.apromore.plugin.portal.processdiscoverer.vis.ProcessVisualizer;
import org.apromore.portal.common.Constants;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.common.notification.Notification;
import org.apromore.portal.dialogController.BaseController;
import org.apromore.portal.dialogController.dto.ApromoreSession;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.model.LogSummaryType;
import org.apromore.processdiscoverer.Abstraction;
import org.apromore.processdiscoverer.AbstractionParams;
import org.apromore.processdiscoverer.ProcessDiscoverer;
import org.apromore.service.DomainService;
import org.apromore.service.EventLogService;
import org.apromore.service.ProcessService;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.json.JSONObject;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

/**
 * This class acts as the main controller for PD<br>
 * Other controllers under the controllers package are used to handle specific behaviors.<br>
 * All UI controls are contained in the main window (todo: no subclasses for panels on the window).<br>
 * To avoid managing too many individual variables, they are all grouped under data objects
 * <b>ContextData</b> contains contextual variables<br> relating to the environment calling this plugin,<br>
 * <b>LogData</b> represents the log data, <b>UserOptions</b><br> manages all user variables set via UI controls,<br>
 * and <b>OutputData</b> represents the existing output data being displayed on the UI.
 * <p>
 * Starting point: first, the window will be created which fires onCreate(),
 * then ZK client engine sends an onLoaded event to the main window, triggering windowListener().
 * TODO: as this is open in a separate browser tab with a different ZK execution from that of the portal,
 * if the user signs out of the portal tab, the actions in this plugin calling to the portal session would fail
 * 
 * In addition, as PD consumes substantial memory resources in various ways (e.g. using third-party libraries),
 * it implements SessionCleanup interface and registers with ZK to be called upon the Session is destroyed 
 * by ZK and the web server. For example, in its cleanup routine, data-intensive components such as PD Logic 
 * and Process Visualizer will be called to clean up themselves.
 */
public class PDController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PDController.class);

    ///////////////////// LOCAL CONSTANTS /////////////////////////////

    private final String LAYOUT_HIERARCHY = "layoutHierarchy";
    private final String LAYOUT_DAGRE_TB = "layoutDagreTopBottom";

    private final String FREQ_LABEL = "frequency";
    private final String DURATION_LABEL = "duration";

    private final String CASE_SECTION_ATTRIBUTE_COMBINATION = "CASE_SECTION_ATTRIBUTE_COMBINATION";
    private final String EVENT_ATTRIBUTE_DURATION = "EVENT_ATTRIBUTE_DURATION";
    private final String ATTRIBUTE_ARC_DURATION = "ATTRIBUTE_ARC_DURATION";

    ///////////////////// UI CONTROLS /////////////////////////////

    private Checkbox layoutHierarchy;
    private Checkbox layoutDagreTopBottom;

    private Button filter;
    private Button filterClear;
    private Button perspectiveDetails;
    private Button casesDetails;
    //private Button fitness;
    private Button animate;
    private Button fitScreen;
    private Button share;

    private Button exportFilteredLog;
    private Button downloadPDF;
    private Button downloadPNG;
    private Button exportBPMN;
    // private Button exportBPMNAnnotatedForBIMP;

    private Label logTitle;
    private Window mainWindow;

    private Label perspectiveSelected;

    //////////////////// SUPPORT SERVICES ///////////////////////////////////

    //private CanoniserService canoniserService;
    private DomainService domainService;
    private ProcessService processService;
    private EventLogService eventLogService;
    private ProcessDiscoverer processDiscoverer;
    private LogAnimationPluginInterface logAnimationPluginCE;
    private LogAnimationPluginInterface logAnimationPluginEE;
    private LogFilterPlugin logFilterPlugin;
    private PDFactory pdFactory;

    //////////////////// LOCAL UTILITIES ///////////////////////////////////

    private final DecimalFormat decimalFormat = new DecimalFormat("##############0.##");
    private GraphVisController graphVisController;
    private CaseDetailsController caseDetailsController;
    private PerspectiveDetailsController perspectiveDetailsController;
    private ViewSettingsController viewSettingsController;
    private GraphSettingsController graphSettingsController;
    private LogStatsController logStatsController;
    private TimeStatsController timeStatsController;
    private ProcessVisualizer processVisualizer;

    private LogFilterController logFilterController;

    //////////////////// DATA ///////////////////////////////////

    private String pluginSessionId; // the session ID of this plugin
    private ApromoreSession portalSession;
    private ConfigData configData;
    private ContextData contextData;
    private LogData logData;
    private UserOptionsData userOptions;
    private OutputData outputData;
    private LogSummaryType logSummary;
    private PortalContext portalContext;

    private String primaryTypeLabel;
    private String primaryAggregateCode;

    private int sourceLogId; // plugin maintain log ID for Filter; Filter remove value to avoid conflic from multiple plugins

    /////////////////////////////////////////////////////////////////////////

    public PDController() throws Exception {
        super();
    }
    
    //Note: this method is only valid inside onCreate() as it calls ZK current Execution
    private boolean preparePluginSessionId() {
        pluginSessionId = Executions.getCurrent().getParameter("id");
        if (pluginSessionId == null) return false;
        if (UserSessionManager.getEditSession(pluginSessionId) == null) return false;
        return true;
    }
    
    // True means the current portal session is not valid to go ahead any more
    // It could be the Apromore Portal session has timed out or user has logged off, or 
    // or something has made it crashed
    private boolean preparePortalSession(String pluginSessionId) {
        portalSession = UserSessionManager.getEditSession(pluginSessionId);
        if (portalSession == null) return false;
        portalContext = (PortalContext) portalSession.get("context");
        logSummary = (LogSummaryType) portalSession.get("selection");

        sourceLogId = logSummary.getId();

        if (portalContext == null || logSummary == null) return false;
        try {
            FolderType currentFolder = portalContext.getCurrentFolder();
            if (currentFolder == null) return false;
        }
        catch (Exception ex) {
            return false;
        }
        
        return true;
    } 
    
    // Check infrastructure services to be available. They can become unavailable
    // because of system crashes or modules crashed/undeployed
    private boolean prepareSystemServices() {
        //canoniserService = (CanoniserService) beanFactory.getBean("canoniserService");
        domainService = (DomainService) Sessions.getCurrent().getAttribute("domainService");
        processService = (ProcessService) Sessions.getCurrent().getAttribute("processService");
        eventLogService = (EventLogService) Sessions.getCurrent().getAttribute("eventLogService");
        logAnimationPluginCE = (LogAnimationPluginInterface) Sessions.getCurrent().getAttribute("logAnimationPluginCE"); 
        logAnimationPluginEE = (LogAnimationPluginInterface) Sessions.getCurrent().getAttribute("logAnimationPluginEE");
        logFilterPlugin = (LogFilterPlugin) Sessions.getCurrent().getAttribute("logFilterPlugin"); //beanFactory.getBean("logFilterPlugin");

        if (domainService == null || processService == null ||
                eventLogService == null || logAnimationPluginCE == null || logAnimationPluginEE == null ||
                logFilterPlugin == null) {
            return false;
        }
        return true;
    }
    
    // This is to check the availability of system services before executing a related action
    // E.g. before calling export log/model to the portal.
    public boolean prepareCriticalServices() {
        if (pluginSessionId == null) {
            Messagebox.show("You have logged off or your session has expired. Please log in again and reopen the file.");
            return false;
        }
        
        if (!preparePortalSession(pluginSessionId)) {
            Messagebox.show("You have logged off or your session has expired. Please log in again and reopen the file.");
            return false;
        }
        
        if (!prepareSystemServices()) {
            Messagebox.show("Key system services for PD are not available. Please contact your administrator.");
            return false;
        }
        
        return true;
    }
     
    public void onCreate() throws InterruptedException {
        try {
            if (!preparePluginSessionId()) {
                Messagebox.show("Process Discoverer session has not been initialized. Please open it again properly!");
                return;
            }
            
            if (!prepareCriticalServices()) {
                return;
            }
            
            // Prepare data
            ApromoreSession session = UserSessionManager.getEditSession(pluginSessionId);
            PortalContext portalContext = (PortalContext) session.get("context");
            LogSummaryType logSummary = (LogSummaryType) session.get("selection");
            pdFactory = (PDFactory) session.get("pdFactory");
            
            configData = pdFactory.createConfigData();
            contextData = pdFactory.createContextData(
                    portalContext,
                    logSummary.getDomain(),
                    logSummary.getId(),
                    logSummary.getName(),
                    portalContext.getCurrentFolder() == null ? 0 : portalContext.getCurrentFolder().getId(),
                    portalContext.getCurrentFolder() == null ? "Home" : portalContext.getCurrentFolder().getFolderName(),
                    configData);
            userOptions = pdFactory.createUserOptionsData();
            primaryTypeLabel = FREQ_LABEL;
            primaryAggregateCode = "case";
            userOptions.setPrimaryType(FREQUENCY);
            userOptions.setPrimaryAggregation(MeasureAggregation.CASES);
            userOptions.setSecondaryType(DURATION);
            userOptions.setSecondaryAggregation(MeasureAggregation.MEAN);
            userOptions.setFixedType(FREQUENCY);
            userOptions.setFixedAggregation(MeasureAggregation.CASES);
            userOptions.setInvertedNodesMode(false);
            userOptions.setInvertedArcsMode(false);
    
            // Prepare log data
            logData = pdFactory.createLogData(contextData, eventLogService);
            IndexableAttribute mainAttribute = logData.getAttribute(configData.getDefaultAttribute());
            if (mainAttribute == null) {
                Messagebox.show("We cannot display the process map due to missing activity (i.e. concept:name) attribute in the log.", 
                        "Process Discoverer", 
                        Messagebox.OK, 
                        Messagebox.INFORMATION);
                return;
            }
            else if (mainAttribute.getValueSize() > configData.getMaxNumberOfUniqueValues()) {
                Messagebox.show("We cannot display the process map due to a large number of activities in the log " +
                                " (more than " + configData.getMaxNumberOfUniqueValues() + ")", 
                                "Process Discoverer", 
                                Messagebox.OK, 
                                Messagebox.INFORMATION);
                return;
            }
            
            logData.setMainAttribute(configData.getDefaultAttribute());
            userOptions.setMainAttributeKey(configData.getDefaultAttribute());
            processDiscoverer = new ProcessDiscoverer(logData.getAttributeLog());
            processVisualizer = pdFactory.createProcessVisualizer(this);

            // Set up controllers
            graphVisController = pdFactory.createGraphVisController(this);
            caseDetailsController = pdFactory.createCaseDetailsController(this);
            perspectiveDetailsController = pdFactory.createPerspectiveDetailsController(this);
            viewSettingsController = pdFactory.createViewSettingsController(this);
            graphSettingsController = pdFactory.createGraphSettingsController(this);
            logStatsController = pdFactory.createLogStatsController(this);
            timeStatsController = pdFactory.createTimeStatsController(this);

            initialize();
            initializeDefaults();
            
            System.out.println("Session ID = " + ((HttpSession)Sessions.getCurrent().getNativeSession()).getId());
            System.out.println("Desktop ID = " + getDesktop().getId());
            
            // Finally, store objects to be cleaned up when the session timeouts 
            getDesktop().setAttribute("processDiscoverer", processDiscoverer);
            getDesktop().setAttribute("processVisualizer", processVisualizer);
            getDesktop().setAttribute("pluginSessionId", pluginSessionId);
        }
        catch (Exception ex) {
            Messagebox.show("Error occurred while initializing: " + ex.getMessage());
            LOGGER.error("Error occurred while initializing: " + ex.getMessage(), ex);
        }
    }

    // All data and controllers must be all already available
    private void initialize() {
        try {
            initializeControls();
            updateTimeStats();
            viewSettingsController.updateUI(null);
            initializeEventListeners();
        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show(e.getMessage(), "Process Discoverer", Messagebox.OK, Messagebox.ERROR);
        }
    }

    // Adjust UI when detecting special conditions
    private void initializeDefaults() {

    }

    private void initializeControls() {
        try {
            mainWindow = (Window) this.getFellow("win");
            Label logTitle = (Label) mainWindow.getFellow("logTitle");
            mainWindow.setTitle(contextData.getLogName());
            logTitle.setValue(contextData.getLogName());
    
            viewSettingsController.initializeControls(contextData);
            graphSettingsController.initializeControls(contextData);
            timeStatsController.initializeControls(contextData);
            logStatsController.initializeControls(contextData);
            graphVisController.initializeControls(contextData);
    
            Component compLogStats = mainWindow.query(".ap-pd-logstats");
    
            perspectiveSelected = (Label) compLogStats.getFellow("perspectiveSelected");
    
            // Main action buttons
            casesDetails = (Button) mainWindow.getFellow("caseDetails");
            perspectiveDetails = (Button) mainWindow.getFellow("perspectiveDetails");
            //fitness = (Button) mainWindow.getFellow("fitness");
            filter = (Button) mainWindow.getFellow("filter");
            filterClear = (Button) mainWindow.getFellow("filterClear");
            animate = (Button) mainWindow.getFellow("animate");
            fitScreen = (Button) mainWindow.getFellow("fitScreen");
            share = (Button) mainWindow.getFellow("share");
    
            exportFilteredLog = (Button) mainWindow.getFellow("exportUnfitted");
            // export = (Combobutton) mainWindow.getFellow(StringValues.b[70]);
            downloadPDF = (Button) mainWindow.getFellow("downloadPDF");
            downloadPNG = (Button) mainWindow.getFellow("downloadPNG");
            exportBPMN = (Button) mainWindow.getFellow("exportBPMN");
    
            // Layout
            layoutHierarchy = (Checkbox) mainWindow.getFellow(LAYOUT_HIERARCHY);
            layoutHierarchy.setChecked(userOptions.getLayoutHierarchy());
            layoutDagreTopBottom = (Checkbox) mainWindow.getFellow(LAYOUT_DAGRE_TB);
            layoutDagreTopBottom.setChecked(userOptions.getLayoutDagre());


        }
        catch (Exception ex) {
            Messagebox.show("An error occurred while initializing UI: " + ex.getMessage());
        }
    }

    private LogFilterController getFilterController() throws Exception {
        if (logFilterController == null) {
            logFilterController = pdFactory.createLogFilterController(this);
        }
        return logFilterController;
    }

    private void initializeEventListeners() {
        PDController me = this;
        try {
            viewSettingsController.initializeEventListeners(contextData);
            graphSettingsController.initializeEventListeners(contextData);
            graphVisController.initializeEventListeners(contextData);
    
            // Layout
            EventListener<Event> layoutListener = new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    String compId = ((Checkbox) event.getTarget()).getId();
                    setLayout(compId);
                }
            };
            layoutHierarchy.addEventListener("onClick", layoutListener);
            layoutDagreTopBottom.addEventListener("onClick", layoutListener);
    
            fitScreen.addEventListener("onClick", new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    graphVisController.fitToWindow();
                }
            });
    
            casesDetails.addEventListener("onApShow",
                new EventListener<Event>() {
                    @Override
                    public void onEvent(Event event) throws Exception {
                        caseDetailsController.onEvent(event);
                    }
                }
            );
            perspectiveDetails.addEventListener("onApShow",
                new EventListener<Event>() {
                    @Override
                    public void onEvent(Event event) throws Exception {
                        perspectiveDetailsController.onEvent(event);
                    }
                }
            );

            filterClear.addEventListener("onClick", new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    Messagebox.show(
                        "Are you sure you want to clear all filters?",
                        "Filter log",
                        Messagebox.OK | Messagebox.CANCEL,
                        Messagebox.QUESTION,
                        new org.zkoss.zk.ui.event.EventListener() {
                            @Override
                            public void onEvent(Event evt) {
                                if (evt.getName().equals("onOK")) {
                                    try {
                                        me.clearFilter();
                                    } catch (Exception e) {
                                        Messagebox.show("Unable to clear the filter", "Filter error", Messagebox.OK, Messagebox.ERROR);
                                    }
                                    /*
                                    LogFilterController logFilterController = me.getFilterController();
                                    logFilterController.subscribeFilterResult();

                                    EventQueue eqFilteredView = EventQueues.lookup("filter_view_ctrl", EventQueues.DESKTOP, true);
                                    if (eqFilteredView != null) {
                                        eqFilteredView.publish(new Event("ctrl", null, "removeall"));
                                    }
                                    */
                                }
                            }
                        }
                    );
                }
            });

            filter.addEventListener("onInvoke", new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    Clients.showBusy("Launch Filter Dialog ...");
                    Sessions.getCurrent().setAttribute("sourceLogId", sourceLogId);
                    String payload = event.getData().toString();
                    LogFilterController logFilterController = me.getFilterController();
                    logFilterController.onEvent(event);
                    EventQueue eqFilteredView = EventQueues.lookup("filter_view_ctrl", EventQueues.DESKTOP, true);
                    eqFilteredView.publish(new Event("ctrl", null, payload));
                    Clients.clearBusy();
                }
            });
            filter.addEventListener("onInvokeExt", new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    try {
                        JSONObject param = (JSONObject) event.getData();
                        String type = (String) param.get("type");
                        String data, source, target;
                        UnifiedMap<String, String> parameters = new UnifiedMap<>();
                        String mainAttribute = me.getUserOptions().getMainAttributeKey();
                        if (CASE_SECTION_ATTRIBUTE_COMBINATION.equals(type) || EVENT_ATTRIBUTE_DURATION.equals(type)) {
                            data = (String) param.get("data");
                            if (EVENT_ATTRIBUTE_DURATION.equals(type) && !me.logData.hasSufficientDurationVariant(mainAttribute, data)) {
                                Messagebox.show("Unable to filter on node duration as there's only one value.",
                                        "Filter error", Messagebox.OK, Messagebox.ERROR);
                                return;
                            }
                            parameters.put("filterType", type);
                            parameters.put("attributeKey", mainAttribute);
                            parameters.put("attributeValue", data);
                        } else if (ATTRIBUTE_ARC_DURATION.equals(type)) {
                            source = (String) param.get("source");
                            target = (String) param.get("target");
                            if (!me.logData.hasSufficientDurationVariant(mainAttribute, source, target)) {
                                Messagebox.show("Unable to filter on arc duration as there's only one value.",
                                        "Filter error", Messagebox.OK, Messagebox.ERROR);
                                return;
                            }
                            parameters.put("filterType", type);
                            parameters.put("attributeKey", mainAttribute);
                            parameters.put("indegreeValue", source);
                            parameters.put("outdegreeValue", target);
                        } else {
                            return;
                        }
                        Clients.showBusy("Launch Filter Dialog ...");
                        Sessions.getCurrent().setAttribute("sourceLogId", sourceLogId);
                        LogFilterController logFilterController = me.getFilterController();
                        logFilterController.onEvent(event);
                        EventQueue eqFilteredView = EventQueues.lookup("filter_view_ctrl", EventQueues.DESKTOP, true);
                        eqFilteredView.publish(new Event("ctrl", null, parameters));
                        Clients.clearBusy();
                    } catch (Exception e) {
                        LOGGER.error("Error occurred while launching quick filter: " + e.getMessage(), e);
                    }
                }
            });
            filter.addEventListener("onClick", this.getFilterController());
            animate.addEventListener("onClick", pdFactory.createAnimationController(this));
    
            exportFilteredLog.addEventListener("onExport", pdFactory.createLogExportController(this));
            exportBPMN.addEventListener("onClick", pdFactory.createBPMNExportController(this));
            downloadPDF.addEventListener("onClick", new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    graphVisController.exportPDF(getOutputName());
                }
            });
            downloadPNG.addEventListener("onClick", new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    graphVisController.exportPNG(getOutputName());
                }
            });

            share.addEventListener("onClick", event -> {
                PortalPlugin accessControlPlugin;

                try {
                    Map<String, PortalPlugin> portalPluginMap = portalContext.getPortalPluginMap();
                    Object selectedItem = logSummary;
                    accessControlPlugin = portalPluginMap.get("ACCESS_CONTROL_PLUGIN");
                    Map arg = new HashMap<>();
                    arg.put("withFolderTree", false);
                    arg.put("selectedItem", selectedItem);
                    arg.put("currentUser", UserSessionManager.getCurrentUser());
                    arg.put("autoInherit", true);
                    arg.put("showRelatedArtifacts", true);
                    accessControlPlugin.setSimpleParams(arg);
                    accessControlPlugin.execute(portalContext);
                } catch (Exception e) {
                    Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
                }
            });
    
            EventListener<Event> windowListener = new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    graphSettingsController.ensureSliders();
                    generateViz();
                }
            };
            mainWindow.addEventListener("onLoaded", windowListener);
            mainWindow.addEventListener("onOpen", windowListener);
            mainWindow.addEventListener("onZIndex", new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    Window cases_window = caseDetailsController.getWindow();
                    if (cases_window != null && cases_window.inOverlapped()) {
                        cases_window.setZindex(mainWindow.getZIndex() + 1);
                    }
                }
            });
        }
        catch (Exception ex) {
            Messagebox.show("Errors occured while initializing event handlers.");
        }

    }

    private void showSingleDurationFilterError() {
        Messagebox.show("Unable to filter on duration as there's only one value.",
                "Filter error", Messagebox.OK, Messagebox.ERROR);
    }

    public void clearFilter() throws Exception {
        this.getFilterController().clearFilter();
    }

    private void setLayout(String layout) throws Exception {
        switch (layout) {
            case LAYOUT_HIERARCHY:
                userOptions.setLayoutHierarchy(true);
                userOptions.setLayoutDagre(false);
                break;
            case LAYOUT_DAGRE_TB:
                userOptions.setLayoutHierarchy(false);
                userOptions.setLayoutDagre(true);
                break;
        }
        graphVisController.changeLayout();
    }


    private String getOutputName() {
        return contextData.getLogName() + " - " +
                Labels.getLabel("e.agg." + primaryAggregateCode + ".text") + " " + primaryTypeLabel;
    }

    /**
     * Update UI
     * This is used if the underlying data is changed via filtering
     *
     * @param reset: true to reset the graph zoom and panning
     */
    public void updateUI(boolean reset) {
        try {
            updateLogStats();
            updateTimeStats();
            viewSettingsController.updateUI(contextData);
            userOptions.setRetainZoomPan(!reset);
            generateViz();
            if (!reset) {
                graphVisController.centerToWindow();
            }
            boolean noFilter = this.getLogData().isCurrentFilterCriteriaEmpty();
            filterClear.setDisabled(noFilter);
        }
        catch (Exception ex) {
            Messagebox.show("Errors occured while updating UI: " + ex.getMessage());
        }
    }

    public boolean getBPMNMode() {
        return userOptions.getBPMNMode();
    }

    private void updateTimeStats() throws Exception {
        this.timeStatsController.updateUI(contextData);
    }

    public void updateLogStats() throws Exception {
        this.logStatsController.updateUI(contextData);
    }

    public void setOverlay(
            MeasureType primaryType,
            MeasureAggregation primaryAggregation,
            MeasureRelation primaryRelation,
            MeasureType secondaryType,
            MeasureAggregation secondaryAggregation,
            MeasureRelation secondaryRelation,
            String aggregateCode
    ) throws InterruptedException {

        userOptions.setPrimaryType(primaryType);
        userOptions.setPrimaryAggregation(primaryAggregation);
        userOptions.setPrimaryRelation(primaryRelation);
        
        userOptions.setSecondaryType(secondaryType);
        userOptions.setSecondaryAggregation(secondaryAggregation);
        userOptions.setSecondaryRelation(secondaryRelation);

        primaryAggregateCode = aggregateCode;
        if (primaryType == FREQUENCY) {
            primaryTypeLabel = FREQ_LABEL;
        } else { // (overlay == DURATION) assume DURATION
            primaryTypeLabel = DURATION_LABEL;
        }
        generateViz();
    }

    public AbstractionParams genAbstractionParamsSimple(
            boolean prioritizeParallelism, boolean preserve_connectivity, boolean secondary,
            MeasureType primaryType, MeasureAggregation primaryAggregation, MeasureRelation primaryRelation,
            MeasureType secondaryType, MeasureAggregation secondaryAggregation, MeasureRelation secondaryRelation
            ) {
        return new AbstractionParams(
                logData.getMainAttribute(),
                userOptions.getNodeFilterValue() / 100,
                userOptions.getArcFilterValue() / 100,
                userOptions.getParallelismFilterValue() / 100,
                prioritizeParallelism, preserve_connectivity,
                userOptions.getInvertedNodesMode(),
                userOptions.getInvertedArcsMode(),
                secondary,
                userOptions.getFixedType(),
                userOptions.getFixedAggregation(),
                userOptions.getFixedRelation(),
                primaryType,
                primaryAggregation,
                primaryRelation,
                secondaryType,
                secondaryAggregation,
                secondaryRelation,
                userOptions.getRelationReader(),
                null);
    }
    /*
     * This is the main processing method calling to process-discoverer-logic
     */
    public void generateViz() {
        long timer1 = System.currentTimeMillis();

        if (logData.getAttributeLog() == null) {
            Messagebox.show("Cannot create data for visualization. Please check the log!",
                    "Process Discoverer",
                    Messagebox.OK,
                    Messagebox.INFORMATION);
            return;
        } else if (logData.getAttributeLog().getTraces().size() == 0) {
            Messagebox.show("Data for visualization is empty. Please check the log!",
                    "Process Discoverer",
                    Messagebox.OK,
                    Messagebox.INFORMATION);
            return;
        }

        boolean isNormalOrdering = graphSettingsController.getNormalOrdering();
        MeasureType fixedType = userOptions.getFixedType();
        if (isNormalOrdering && fixedType == FREQUENCY || !isNormalOrdering && fixedType == DURATION) {
            userOptions.setInvertedNodesMode(false);
            userOptions.setInvertedArcsMode(false);
        } else {
            userOptions.setInvertedNodesMode(true);
            userOptions.setInvertedArcsMode(true);
        }

        try {
            AbstractionParams params = genAbstractionParamsSimple(
                true, true,
                userOptions.getIncludeSecondary(),
                userOptions.getPrimaryType(),
                userOptions.getPrimaryAggregation(),
                userOptions.getPrimaryRelation(),
                userOptions.getSecondaryType(),
                userOptions.getSecondaryAggregation(),
                userOptions.getSecondaryRelation()
            );

            // Find a DFG first
            Abstraction dfgAbstraction = processDiscoverer.generateDFGAbstraction(params);
            if (dfgAbstraction == null ||
                    dfgAbstraction.getDiagram() == null ||
                    dfgAbstraction.getDiagram().getNodes().isEmpty() ||
                    dfgAbstraction.getDiagram().getEdges().isEmpty()
            ) {
                Messagebox.show("Unexpected error: empty process map or failure is returned after this action!",
                        "Process Discoverer",
                        Messagebox.OK,
                        Messagebox.ERROR);
                return;
            }

            // Actual operation with the new params
            params.setCorrespondingDFG(dfgAbstraction);
            Abstraction currentAbstraction;
            if (userOptions.getBPMNMode()) {
                currentAbstraction = processDiscoverer.generateBPMNAbstraction(params, dfgAbstraction);
            } else {
                currentAbstraction = dfgAbstraction;
            }

            timer1 = System.currentTimeMillis();
            String visualizedText = processVisualizer.generateVisualizationText(currentAbstraction);
            System.out.println("JsonBuilder.generateJSONFromBPMN: " + (System.currentTimeMillis() - timer1) + " ms.");
            outputData = pdFactory.createOutputData(currentAbstraction, visualizedText);
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            System.out.println("Sent json data to browser at " + formatter.format(new Date()));

            graphVisController.displayDiagram(visualizedText);
            contextData.setFirstTimeLoadingFinished(true);

        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show(!e.getMessage().trim().isEmpty() ? e.getMessage() : "Unexpected error has occurred! Check log files.",
                    "Process Discoverer",
                    Messagebox.OK,
                    Messagebox.ERROR);
        }
    }

    public void setBPMNMode(boolean mode) {
        userOptions.setBPMNMode(mode);
        graphSettingsController.updateParallelism(mode);
        generateViz();
    }

    public String getPerspective() {
        return perspectiveSelected.getValue();
    }

    public void setPerspective(String value, String label) throws Exception {
        if (!value.equals(userOptions.getMainAttributeKey())) {
            perspectiveSelected.setValue(label);
            perspectiveSelected.setTooltiptext(label);
            animate.setDisabled(!value.equals(configData.getDefaultAttribute()));
            userOptions.setMainAttributeKey(value);
            logData.setMainAttribute(value);
            updateTimeStats();
            updateLogStats();
            generateViz();
        }
    }

    public ContextData getContextData() {
        return this.contextData;
    }

    public LogData getLogData() {
        return this.logData;
    }

    public UserOptionsData getUserOptions() {
        return this.userOptions;
    }

    public OutputData getOutputData() {
        return this.outputData;
    }

    public ConfigData getConfigData() {
        return this.configData;
    }

    public String getLogName() { return contextData.getLogName(); }

    public ProcessDiscoverer getProcessDiscoverer() {
        return this.processDiscoverer;
    }
    
    public ProcessVisualizer getProcessVisualizer() {
        return this.processVisualizer;
    }

    public DomainService getDomainService() {
        return domainService;
    }

    public EventLogService getEvenLogService() {
        return eventLogService;
    }

    public ProcessService getProcessService() {
        return processService;
    }
    
    public LogAnimationPluginInterface getLogAnimationPlugin() throws MissingLogAnimationPluginException {
        if (logAnimationPluginEE != null) {
            return logAnimationPluginEE;
        }
        else if (logAnimationPluginCE != null) {
            return logAnimationPluginCE;
        }
        else {
            throw new MissingLogAnimationPluginException("LogAnimation plugin was not available");
        }
    }

    public LogFilterPlugin getLogFilterPlugin() {
        return logFilterPlugin;
    }

    public DecimalFormat getDecimalFormatter() {
        return this.decimalFormat;
    }

    public int getSourceLogId() {
        return sourceLogId;
    }
}
