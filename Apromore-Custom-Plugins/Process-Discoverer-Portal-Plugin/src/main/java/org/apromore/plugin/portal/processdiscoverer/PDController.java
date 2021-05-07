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
import java.util.Objects;

import javax.servlet.http.HttpSession;

import org.apromore.logman.attribute.IndexableAttribute;
import org.apromore.logman.attribute.graph.MeasureAggregation;
import org.apromore.logman.attribute.graph.MeasureRelation;
import org.apromore.logman.attribute.graph.MeasureType;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalPlugin;
import org.apromore.plugin.portal.logfilter.generic.LogFilterPlugin;
import org.apromore.plugin.portal.processdiscoverer.actions.AnimationController;
import org.apromore.plugin.portal.processdiscoverer.actions.BPMNExportController;
import org.apromore.plugin.portal.processdiscoverer.actions.LogExportController;
import org.apromore.plugin.portal.processdiscoverer.actions.LogFilterController;
import org.apromore.plugin.portal.processdiscoverer.components.CaseDetailsController;
import org.apromore.plugin.portal.processdiscoverer.components.GraphSettingsController;
import org.apromore.plugin.portal.processdiscoverer.components.GraphVisController;
import org.apromore.plugin.portal.processdiscoverer.components.LogStatsController;
import org.apromore.plugin.portal.processdiscoverer.components.PerspectiveDetailsController;
import org.apromore.plugin.portal.processdiscoverer.components.TimeStatsController;
import org.apromore.plugin.portal.processdiscoverer.components.ToolbarController;
import org.apromore.plugin.portal.processdiscoverer.components.ViewSettingsController;
import org.apromore.plugin.portal.processdiscoverer.data.ConfigData;
import org.apromore.plugin.portal.processdiscoverer.data.ContextData;
import org.apromore.plugin.portal.processdiscoverer.data.LogData;
import org.apromore.plugin.portal.processdiscoverer.data.OutputData;
import org.apromore.plugin.portal.processdiscoverer.data.UserOptionsData;
import org.apromore.plugin.portal.processdiscoverer.impl.factory.PDFactory;
import org.apromore.plugin.portal.processdiscoverer.vis.ProcessVisualizer;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.BaseController;
import org.apromore.portal.dialogController.dto.ApromoreSession;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.model.LogSummaryType;
import org.apromore.portal.plugincontrol.PluginExecution;
import org.apromore.portal.plugincontrol.PluginExecutionManager;
import org.apromore.processdiscoverer.Abstraction;
import org.apromore.processdiscoverer.AbstractionParams;
import org.apromore.processdiscoverer.ProcessDiscoverer;
import org.apromore.service.DomainService;
import org.apromore.service.EventLogService;
import org.apromore.service.ProcessService;
import org.apromore.service.loganimation.LogAnimationService2;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

/**
 * PDController manages the main UI of PD. Other sub-controllers are either action (under the actions package)
 * or component controllers (under the components package). Each component controller manages one UI area such as
 * Abstraction settings, view settings, or log statistics.<br>
 * PDController is also the Mediator for the sub-controllers, representing them to communicate with
 * the outside as well as among them, avoiding direct communication between sub-controllers,
 * e.g. a UI change needs to communicate from a sub-controller to PDController which will coordinate
 * this change to other sub-controllers.
 * <p>
 * Data items in PD are grouped under three objects: <b>ContextData</b> contains contextual variables<br> relating to
 * the environment calling this plugin, <b>LogData</b> represents the log, <b>UserOptions</b><br> manages all user
 * variables set via UI controls, and <b>OutputData</b> represents the existing output data being displayed on the UI.
 * All controllers share common data which is provided via DataManager.
 * <p>
 * PD consumes memory resources in various ways (e.g. using third-party libraries), it implements SessionCleanup
 * interface to be called upon the Session is destroyed by ZK.
 * <p>
 * Starting point: first, the window will be created which fires onCreate(), then ZK client engine sends an
 * onLoaded event to the main window triggering windowListener().
 * <p>
 * PD has three <b>modes</b>: MODEL view, ANIMATION view and TRACE view. Initially it is in MODEL mode.
 * Each <b>action</b> will change PD to different modes. There are transition rules between modes
 * and active state of UI controls in each mode.
 * 
 */
public class PDController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PDController.class);

    ///////////////////// UI CONTROLS /////////////////////////////
    private Window mainWindow;

    //////////////////// SUPPORT SERVICES ///////////////////////////////////

    private DomainService domainService;
    private ProcessService processService;
    private EventLogService eventLogService;
    private LogAnimationService2 logAnimationService;
    private ProcessDiscoverer processDiscoverer;
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
    private AnimationController animationController;
    private LogExportController logExportController;
    private BPMNExportController bpmnExportController;
    private ToolbarController toolbarController;

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

    private int sourceLogId; // plugin maintain log ID for Filter; Filter remove value to avoid conflic from multiple plugins
    
    private InteractiveMode mode = InteractiveMode.MODEL_MODE; //initial mode
    private String pluginExecutionId;

    /////////////////////////////////////////////////////////////////////////

    public PDController() throws Exception {
        super();
        Map<String, Object> pageParams = new HashMap<>();
        pluginExecutionId = PluginExecutionManager.registerPluginExecution(new PluginExecution(this), Sessions.getCurrent());
        pageParams.put("pluginExecutionId", pluginExecutionId);
        Executions.getCurrent().pushArg(pageParams);
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
        logAnimationService = (LogAnimationService2) Sessions.getCurrent().getAttribute("logAnimationService");
        logFilterPlugin = (LogFilterPlugin) Sessions.getCurrent().getAttribute("logFilterPlugin"); //beanFactory.getBean("logFilterPlugin");

        if (domainService == null || processService == null || eventLogService == null || logFilterPlugin == null) {
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
            
            configData = ConfigData.DEFAULT;
            contextData = ContextData.valueOf(
                    logSummary.getDomain(),
                    portalContext.getCurrentUser().getUsername(),
                    logSummary.getId(),
                    logSummary.getName(),
                    portalContext.getCurrentFolder() == null ? 0 : portalContext.getCurrentFolder().getId(),
                    portalContext.getCurrentFolder() == null ? "Home" : portalContext.getCurrentFolder().getFolderName());
            userOptions = new UserOptionsData();
            userOptions.setPrimaryType(FREQUENCY);
            userOptions.setPrimaryAggregation(MeasureAggregation.CASES);
            userOptions.setSecondaryType(DURATION);
            userOptions.setSecondaryAggregation(MeasureAggregation.MEAN);
            userOptions.setFixedType(FREQUENCY);
            userOptions.setFixedAggregation(MeasureAggregation.CASES);
            userOptions.setInvertedNodesMode(false);
            userOptions.setInvertedArcsMode(false);

            // Prepare log data
            logData = pdFactory.createLogData(contextData, configData, eventLogService);
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
            logFilterController = pdFactory.createLogFilterController(this);
            animationController = pdFactory.createAnimationController(this);
            logExportController = pdFactory.createLogExportController(this);
            bpmnExportController = pdFactory.createBPMNExportController(this);
            toolbarController = pdFactory.createToolbarController(this);

            initialize();
            LOGGER.debug("Session ID = " + ((HttpSession)Sessions.getCurrent().getNativeSession()).getId());
            LOGGER.debug("Desktop ID = " + getDesktop().getId());
            
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

    // All data and controllers must be already available
    private void initialize() {
        try {
            initializeControls();
            timeStatsController.updateUI(contextData);
            viewSettingsController.updateUI(null);
            initializeEventListeners();
        } catch (Exception e) {
            LOGGER.error("Unable to initialize PD controller", e);
            Messagebox.show(e.getMessage(), "Process Discoverer", Messagebox.OK, Messagebox.ERROR);
        }
    }

    private void initializeControls() {
        try {
            mainWindow = (Window) this.getFellow("win");
            mainWindow.setTitle(contextData.getLogName());
            viewSettingsController.initializeControls(contextData);
            graphSettingsController.initializeControls(contextData);
            timeStatsController.initializeControls(contextData);
            logStatsController.initializeControls(contextData);
            graphVisController.initializeControls(contextData);
            toolbarController.initializeControls(contextData);
        }
        catch (Exception ex) {
            Messagebox.show("An error occurred while initializing UI: " + ex.getMessage());
        }
    }

    private void initializeEventListeners() {
        try {
            viewSettingsController.initializeEventListeners(contextData);
            graphSettingsController.initializeEventListeners(contextData);
            graphVisController.initializeEventListeners(contextData);
            toolbarController.initializeEventListeners(contextData);
            logStatsController.initializeEventListeners(contextData);
            timeStatsController.initializeEventListeners(contextData);

            EventListener<Event> windowListener = event -> {
                    graphSettingsController.ensureSliders();
                    generateViz();
            };
            mainWindow.addEventListener("onLoaded", windowListener);
            mainWindow.addEventListener("onOpen", windowListener);
            mainWindow.addEventListener("onZIndex", event -> {
                    putWindowAtTop(caseDetailsController.getWindow());
                    putWindowAtTop(perspectiveDetailsController.getWindow());
            });
            mainWindow.addEventListener("onCaseDetails", event -> caseDetailsController.onEvent(event));
            mainWindow.addEventListener("onPerspectiveDetails", event -> perspectiveDetailsController.onEvent(event));
            
        }
        catch (Exception ex) {
            Messagebox.show("Errors occured while initializing event handlers.");
            LOGGER.error("Errors occured while initializing event handlers.", ex);
        }

    }
    
    private void putWindowAtTop(Window window) {
        if (window != null && window.inOverlapped() && mainWindow != null) {
            window.setZindex(mainWindow.getZIndex() + 1);
        }
    }
    
    public void exportPDF() {
        graphVisController.exportPDF(viewSettingsController.getOutputName());
    }
    
    public void exportPNG() {
        graphVisController.exportPNG(viewSettingsController.getOutputName());
    }
    
    public void exportJSON() {
        graphVisController.exportJSON(viewSettingsController.getOutputName());
    }

    public void clearFilter() throws Exception {
        if (this.mode != InteractiveMode.MODEL_MODE) return;
        logFilterController.clearFilter();
    }
    
    public void changeLayout() throws Exception {
        if (this.mode != InteractiveMode.MODEL_MODE) return;
        graphVisController.changeLayout();
    }
    
    public void fitVisualizationToWindow() {
        graphVisController.fitToWindow();
    }
    
    public void openSharingWindow() {
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
            LOGGER.error(e.getMessage(), e);
        }
    }
    
    public void openAnimation(Event e) {
        try {
            animationController.onEvent(e);
        }
        catch (Exception ex) {
            Messagebox.show(ex.getMessage());
            LOGGER.error(ex.getMessage(), ex);
        }
    }
    
    public void openLogExport(Event e) {
        try {
            logExportController.onEvent(e);
        }
        catch (Exception ex) {
            Messagebox.show(ex.getMessage());
            LOGGER.error(ex.getMessage(), ex);
        }
    }
    
    public void openBPMNExport(Event e) {
        try {
            bpmnExportController.onEvent(e);
        }
        catch (Exception ex) {
            Messagebox.show(ex.getMessage());
            LOGGER.error(ex.getMessage(), ex);
        }
    }
    
    public void openLogFilter(Event e) {
        try {
            logFilterController.onEvent(e);
        }
        catch (Exception ex) {
            Messagebox.show(ex.getMessage());
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * Update UI
     * This is used if the underlying data is changed via filtering
     *
     * @param reset: true to reset the graph zoom and panning
     */
    public void updateUI(boolean reset) {
        if (this.mode != InteractiveMode.MODEL_MODE) return;
        try {
            logStatsController.updateUI(contextData);
            timeStatsController.updateUI(contextData);
            viewSettingsController.updateUI(contextData);
            userOptions.setRetainZoomPan(!reset);
            generateViz();
            if (!reset) graphVisController.centerToWindow();
            toolbarController.setDisabledFilterClear(this.getLogData().isCurrentFilterCriteriaEmpty());
        }
        catch (Exception ex) {
            Messagebox.show("Errors occured while updating UI: " + ex.getMessage());
        }
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
        
        if (this.mode != InteractiveMode.MODEL_MODE) return;

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
            LOGGER.debug("JsonBuilder.generateJSONFromBPMN: " + (System.currentTimeMillis() - timer1) + " ms.");
            outputData = pdFactory.createOutputData(currentAbstraction, visualizedText);
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            LOGGER.debug("Sent json data to browser at " + formatter.format(new Date()));

            graphVisController.displayDiagram(visualizedText);
            this.setInteractiveMode(InteractiveMode.MODEL_MODE);

        } catch (Exception e) {
            LOGGER.error("Unexpected error while generating visualization.", e);
            Messagebox.show(!e.getMessage().trim().isEmpty() ? e.getMessage() : "Unexpected error has occurred! Check log files.",
                    "Process Discoverer",
                    Messagebox.OK,
                    Messagebox.ERROR);
        }
    }
    
    public boolean isBPMNView() {
        return this.userOptions.getBPMNMode();
    }

    public void setBPMNView(boolean mode) {
        if (this.mode != InteractiveMode.MODEL_MODE) return;
        userOptions.setBPMNMode(mode);
        graphSettingsController.updateParallelism(mode);
        generateViz();
    }
    
    public void switchToAnimationView() {
        try {
            if (this.setInteractiveMode(InteractiveMode.ANIMATION_MODE)) {
                graphVisController.switchToAnimationView();
            }
        }
        catch (Exception ex) {
            Messagebox.show(ex.getMessage());
            LOGGER.error(ex.getMessage(), ex);
        }
    }
    
    public void switchToModelView() {
        if (this.setInteractiveMode(InteractiveMode.MODEL_MODE)) {
            graphVisController.switchToModelView();
        }
    }
    
    public void restoreModelView() {
        if (this.setInteractiveMode(InteractiveMode.MODEL_MODE)) {
            graphVisController.displayDiagram(outputData.getVisualizedText());
        }
    }
    
    public void showTrace(String traceVisualContent) {
        if (this.setInteractiveMode(InteractiveMode.TRACE_MODE)) {
            graphVisController.displayTraceDiagram(traceVisualContent);
        }
    }

    /**
     * Mode represents an overall state of PD. It is used to set relevant status for UI controls
     * without having to consider state of each control.
     * This method updates the UI in accordance with the current mode.
     * @param newMode
     * @return: true if the mode has been changed
     */
    public boolean setInteractiveMode(InteractiveMode newMode) {
        if (newMode == InteractiveMode.ANIMATION_MODE) {
            if (this.mode == InteractiveMode.TRACE_MODE) return false; //invalid move
            viewSettingsController.setDisabled(true);
            graphSettingsController.setDisabled(true);
            logStatsController.setDisabled(true);
            timeStatsController.setDisabled(true);
            toolbarController.setDisabled(true);
            toolbarController.toogleAnimateBtn(true);
            caseDetailsController.setDisabled(true);
        }
        else if (newMode == InteractiveMode.MODEL_MODE) {
            viewSettingsController.setDisabled(false);
            graphSettingsController.setDisabled(false);
            logStatsController.setDisabled(false);
            timeStatsController.setDisabled(false);
            toolbarController.setDisabled(false);
            toolbarController.setDisabledAnimation(false);
            toolbarController.toogleAnimateBtn(false);
            caseDetailsController.setDisabled(false);
        }
        else if (newMode == InteractiveMode.TRACE_MODE) {
            if (this.mode == InteractiveMode.ANIMATION_MODE) return false; //invalid move
            viewSettingsController.setDisabled(true);
            graphSettingsController.setDisabled(true);
            logStatsController.setDisabled(true);
            timeStatsController.setDisabled(true);
            toolbarController.setDisabled(true);
            toolbarController.setDisabledAnimation(true);
            toolbarController.toogleAnimateBtn(false);
        }
        this.mode = newMode;
        return true;
    }
    
    public InteractiveMode getInteractiveMode() {
        return this.mode;
    }

    public String getPerspective() {
        return viewSettingsController.getPerspectiveName();
    }

    public void setPerspective(String value, String label) throws Exception {
        if (this.mode != InteractiveMode.MODEL_MODE) return;
        if (!value.equals(userOptions.getMainAttributeKey())) {
            toolbarController.setDisabledAnimation(!value.equals(configData.getDefaultAttribute()));
            userOptions.setMainAttributeKey(value);
            logData.setMainAttribute(value);
            timeStatsController.updateUI(contextData);
            logStatsController.updateUI(contextData);
            logStatsController.updatePerspectiveHeading(label);
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

    public ProcessDiscoverer getProcessDiscoverer() {
        return this.processDiscoverer;
    }
    
    public ProcessVisualizer getProcessVisualizer() {
        return this.processVisualizer;
    }

    public EventLogService getEvenLogService() {
        return eventLogService;
    }

    public ProcessService getProcessService() {
        return processService;
    }

    public LogFilterPlugin getLogFilterPlugin() {
        return logFilterPlugin;
    }
    
    public LogAnimationService2 getLogAnimationService() {
        return logAnimationService;
    }

    public DecimalFormat getDecimalFormatter() {
        return this.decimalFormat;
    }

    public int getSourceLogId() {
        return sourceLogId;
    }
    
    public PortalContext getPortalContext() {
        return this.portalContext;
    }
    
    public void refreshPortal() {
        this.portalContext.refreshContent();
    }
    
    public String getPluginExecutionId() {
        return this.pluginExecutionId;
    }
    
    @Override
    public String processRequest(Map<String,String[]> parameterMap) {
        String  startFrameIndex = parameterMap.get("startFrameIndex")[0];
        String  chunkSize = parameterMap.get("chunkSize")[0];
        try {
            String chunkJSON = graphVisController.getAnimationMovie()
                                        .getChunkJSON(Integer.parseInt(startFrameIndex),
                                                        Integer.parseInt(chunkSize)).toString();
            return escapeQuotedJavascript(chunkJSON);
        }
        catch (NumberFormatException | JSONException e) {
            LOGGER.error(e.getMessage(), e);
            return "Error: " + e.getMessage();
        }
    }
    
    /**
     * @param json
     * @return the <var>json</var> escaped so that it can be quoted in Javascript.
     *     Specifically, it replaces apostrophes with \\u0027 and removes embedded newlines and leading and trailing whitespace.
     */
    private String escapeQuotedJavascript(String json) {
        return json.replace("\n", " ").replace("'", "\\u0027").trim();
    }
}
