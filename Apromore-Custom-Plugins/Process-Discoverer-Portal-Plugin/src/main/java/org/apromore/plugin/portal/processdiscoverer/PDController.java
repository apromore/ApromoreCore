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
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpSession;

import org.apromore.logman.attribute.IndexableAttribute;
import org.apromore.logman.attribute.graph.MeasureType;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.PortalPlugin;
import org.apromore.plugin.portal.logfilter.generic.LogFilterPlugin;
import org.apromore.plugin.portal.processdiscoverer.actions.Action;
import org.apromore.plugin.portal.processdiscoverer.actions.ActionHistory;
import org.apromore.plugin.portal.processdiscoverer.actions.FilterAction;
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
import org.apromore.plugin.portal.processdiscoverer.data.OutputData;
import org.apromore.plugin.portal.processdiscoverer.data.UserOptionsData;
import org.apromore.plugin.portal.processdiscoverer.eventlisteners.AnimationController;
import org.apromore.plugin.portal.processdiscoverer.eventlisteners.BPMNExportController;
import org.apromore.plugin.portal.processdiscoverer.eventlisteners.LogExportController;
import org.apromore.plugin.portal.processdiscoverer.eventlisteners.LogFilterController;
import org.apromore.plugin.portal.processdiscoverer.impl.factory.PDFactory;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.BaseController;
import org.apromore.portal.dialogController.dto.ApromoreSession;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.model.LogSummaryType;
import org.apromore.portal.plugincontrol.PluginExecution;
import org.apromore.portal.plugincontrol.PluginExecutionManager;
import org.apromore.service.DomainService;
import org.apromore.service.EventLogService;
import org.apromore.service.ProcessService;
import org.apromore.service.loganimation.LogAnimationService2;
import org.json.JSONException;
import org.slf4j.Logger;
import org.zkoss.util.Locales;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;
import org.zkoss.web.Attributes;

/**
 * PDController is the top-level application object to manage PD plugin as a whole. It
 * <p>
 * Under PDController is different UI actions (under the actions package) and components (under components package).
 * PDController plays as a Mediator for these UI actions and components. It mediates the communication between these components to avoid direct communication between them.
 * <p>
 * Starting point of PDController: first, the window will be created which fires onCreate(), then ZK client engine sends an
 * onLoaded event to the main window triggering windowListener().
 * <p>
 * PDController has three <b>modes</b>: MODEL view, ANIMATION view and TRACE view. Initially it is in MODEL mode.
 * Each <b>action</b> will change PD to different modes. There are transition rules between modes and active state of UI controls
 * in each mode.
 * <p>
 * PDController provides an action management capability. Actions can be bundled into Action object which can be undo/redo to reduce
 * dependencies among various objects.
 * <ul>
 *  <li>UI Components such as ToolbarController and GraphVisController set up these actions (Invoker)
 *  <li>These actions have access to PDController and PDAnalyst (Receiver) to fulfill the request
 * </ul>
 * 
 */
public class PDController extends BaseController {

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(PDController.class);

    //////////////////// SUPPORT SERVICES ///////////////////////////////////

    private DomainService domainService;
    private ProcessService processService;
    private EventLogService eventLogService;
    private LogAnimationService2 logAnimationService;
    private LogFilterPlugin logFilterPlugin;
    private PDFactory pdFactory;
    
    //////////////////// THE MAIN PD Business Analyst //////////////////////////
    
    private PDAnalyst processAnalyst;
    private ActionHistory actionHistory = new ActionHistory();

    //////////////////// UI COMPONENTS ///////////////////////////////////

    private Window mainWindow;
    private final DecimalFormat decimalFormat = new DecimalFormat("##############0.##");
    private GraphVisController graphVisController;
    private CaseDetailsController caseDetailsController;
    private PerspectiveDetailsController perspectiveDetailsController;
    private ViewSettingsController viewSettingsController;
    private GraphSettingsController graphSettingsController;
    private LogStatsController logStatsController;
    private TimeStatsController timeStatsController;
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
        pageParams.put("pdLabels", getLabels());
        Executions.getCurrent().pushArg(pageParams);
    }

    public ResourceBundle getLabels() {
        return ResourceBundle.getBundle("metainfo.zk-label",
            (Locale) Sessions.getCurrent().getAttribute(Attributes.PREFERRED_LOCALE),
            PDController.class.getClassLoader());
    }

    public String getLabel(String key) {
        String label = getLabels().getString(key);
        if (label == null) {
            label = "";
        }
        return label;
    }

    public String getLabel(String key, String defaultVal) {
        String label = getLabels().getString(key);
        if (label == null) {
            label = defaultVal;
        }
        return label;
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
            Messagebox.show(getLabel("sessionTimeout_message"));
            return false;
        }

        if (!preparePortalSession(pluginSessionId)) {
            Messagebox.show(getLabel("sessionTimeout_message"));
            return false;
        }
        
        if (!prepareSystemServices()) {
            Messagebox.show(getLabel("servicesUnavailable_message"));
            return false;
        }
        
        return true;
    }

    public void onCreate() throws InterruptedException {
        try {
            if (!preparePluginSessionId()) {
                Messagebox.show(getLabel("sessionNotInitialized_message"));
                return;
            }

            if (!prepareCriticalServices()) {
                return;
            }

            // Set up Process Analyst
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
            processAnalyst = new PDAnalyst(contextData, configData, eventLogService);
            
            // Check data against the capacity of Process Analyst
            IndexableAttribute mainAttribute = processAnalyst.getAttribute(configData.getDefaultAttribute());
            if (mainAttribute == null) {
                Messagebox.show(
                    getLabel("missingActivity_message"),
                    getLabel("missingActivity_title"),
                    Messagebox.OK,
                    Messagebox.INFORMATION
                );
                return;
            }
            else if (mainAttribute.getValueSize() > configData.getMaxNumberOfUniqueValues()) {
                Messagebox.show(
                    MessageFormat.format(getLabel("tooManyActivities_message"), configData.getMaxNumberOfUniqueValues()),
                    getLabel("tooManyActivities_title"),
                    Messagebox.OK,
                    Messagebox.INFORMATION
                );
                return;
            }
            userOptions = UserOptionsData.DEFAULT(configData);
            
            // Set up UI components
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
            getDesktop().setAttribute("processAnalyst", processAnalyst);
            getDesktop().setAttribute("pluginSessionId", pluginSessionId);
        }
        catch (Exception ex) {
            Messagebox.show(getLabel("initError_message"));
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
            Messagebox.show(getLabel("initError_message"), getLabel("initError_title"), Messagebox.OK, Messagebox.ERROR);
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
            Messagebox.show(getLabel("initError_message"));
            LOGGER.error("An error occurred while initializing UI: " + ex.getMessage());
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
            Messagebox.show(getLabel("initEventHandlerError_message"));
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
            Messagebox.show(getLabel("failedShare_message"), getLabel("failedShare_title"), Messagebox.OK, Messagebox.ERROR);
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void openAnimation(Event e) {
        try {
            animationController.onEvent(e);
        }
        catch (Exception ex) {
            Messagebox.show(getLabel("failedOpenAnimation_message"));
            LOGGER.error(ex.getMessage(), ex);
        }
    }
    
    public void openLogExport(Event e) {
        try {
            logExportController.onEvent(e);
        }
        catch (Exception ex) {
            Messagebox.show(getLabel("failedExportLog_message"));
            LOGGER.error(ex.getMessage(), ex);
        }
    }
    
    public void openBPMNExport(Event e) {
        try {
            bpmnExportController.onEvent(e);
        }
        catch (Exception ex) {
            Messagebox.show(getLabel("failedExportBPMN_message"));
            LOGGER.error(ex.getMessage(), ex);
        }
    }
    
    public void openLogFilter(Event e) {
        try {
            logFilterController.onEvent(e);
        }
        catch (Exception ex) {
            Messagebox.show(getLabel("failedOpenFilter_message"));
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
            toolbarController.setDisabledFilterClear(this.getProcessAnalyst().isCurrentFilterCriteriaEmpty());
            toolbarController.updateUndoRedoButtons(actionHistory.canUndo(), actionHistory.canRedo());
        }
        catch (Exception ex) {
            Messagebox.show(getLabel("failedUpdateUI_message"));
            LOGGER.error("Errors occured while updating UI: " + ex.getMessage());
        }
    }

    /*
     * This is the main processing method calling to process-discoverer-logic
     */
    public void generateViz() {
        long timer1 = System.currentTimeMillis();
        
        if (this.mode != InteractiveMode.MODEL_MODE) return;

        if (processAnalyst.hasEmptyData()) {
            Messagebox.show(
                getLabel("failedViz_message"),
                getLabel("title_text"),
                Messagebox.OK,
                Messagebox.INFORMATION
            );
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
            Optional<OutputData> analysisResult = processAnalyst.discoverProcess(userOptions);
            if (analysisResult.isEmpty()) {
                Messagebox.show(
                    getLabel("failedMapProcess_message"),
                    getLabel("title_text"),
                    Messagebox.OK,
                    Messagebox.ERROR
                );
                return;
            }
            this.outputData = analysisResult.get();
            LOGGER.debug("PDAnalyst.discoverProcess: " + (System.currentTimeMillis() - timer1) + " ms.");
            
            graphVisController.displayDiagram(this.outputData.getVisualizedText());
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            LOGGER.debug("Sent json data to browser at " + formatter.format(new Date()));
            
            this.setInteractiveMode(InteractiveMode.MODEL_MODE);

        } catch (Exception e) {
            LOGGER.error("Unexpected error when discovering process.", e);
            Messagebox.show(
                getLabel("otherError_message"),
                getLabel("title_text"),
                Messagebox.OK,
                Messagebox.ERROR
            );
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
    private boolean setInteractiveMode(InteractiveMode newMode) {
        if (newMode == InteractiveMode.ANIMATION_MODE) {
            if (this.mode == InteractiveMode.TRACE_MODE) return false; //invalid move
            viewSettingsController.setDisabled(true);
            graphSettingsController.setDisabled(true);
            logStatsController.setDisabled(true);
            timeStatsController.setDisabled(true);
            toolbarController.setDisabled(true);
            toolbarController.setDisabledSearch(true);
            toolbarController.toogleAnimateBtn(true);
            caseDetailsController.setDisabled(true);
        }
        else if (newMode == InteractiveMode.MODEL_MODE) {
            viewSettingsController.setDisabled(false);
            graphSettingsController.setDisabled(false);
            logStatsController.setDisabled(false);
            timeStatsController.setDisabled(false);
            toolbarController.setDisabled(false);
            toolbarController.setDisabledSearch(false);
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
            toolbarController.setDisabledSearch(false);
            toolbarController.setDisabledAnimation(true);
            toolbarController.toogleAnimateBtn(false);
        }
        this.mode = newMode;
        return true;
    }
    
    public InteractiveMode getInteractiveMode() {
        return this.mode;
    }

    public String getPerspectiveName() {
        return viewSettingsController.getPerspectiveName();
    }

    public void setPerspective(String value, String label) throws Exception {
        if (this.mode != InteractiveMode.MODEL_MODE) return;
        if (!value.equals(userOptions.getMainAttributeKey())) {
            toolbarController.setDisabledAnimation(!value.equals(configData.getDefaultAttribute()));
            userOptions.setMainAttributeKey(value);
            processAnalyst.setMainAttribute(value);
            timeStatsController.updateUI(contextData);
            logStatsController.updateUI(contextData);
            logStatsController.updatePerspectiveHeading(label);
            generateViz();
        }
    }

    public ContextData getContextData() {
        return this.contextData;
    }

    public PDAnalyst getProcessAnalyst() {
        return this.processAnalyst;
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
    
    //////////////////////// ACTION MANAGEMENT /////////////////////////

    /** For real action that perform changes */
    public void executeAction(Action action) {
        if (action.execute()) {
            actionHistory.undoPush(action);
            // Redo actions are those actions that have been undoed.
            // When an action is redoed (re-executed), it assumes that the undo stack is the same as before it is pushed to undo
            // Thus, whenever a NEW action is pushed to the undo stack, all current redoable actions must be clear to ensure consistent state.
            actionHistory.clearRedo();
            if (action instanceof FilterAction) this.updateUI(false);
        }
    }
    
    /**
     * For actions that don't change anything but need to be bundled for undo/redo
     * Some actions fall into this category, such as do filtering via opening a LogFilter window
     * These actions can't be executed directly via executeAction() method, but they can be stored to support undo/redo
     */
    public void storeAction(Action action) {
        actionHistory.undoPush(action);
        if (action instanceof FilterAction) this.updateUI(false);
    }

    public void undoAction() {
        Action action = actionHistory.undoPop();
        if (action != null) {
            try {
                action.undo();
            } catch (Exception e) {
                // LOGGER.error("Error when undoing filter action. Error message: " + e.getMessage());
                Messagebox.show(getLabel("undoError_message"));
            }
            actionHistory.redoPush(action);
            if (action instanceof FilterAction) this.updateUI(false);
        }
    }

    // Re-execute
    public void redoAction() {
        Action action = actionHistory.redoPop();
        if (action != null) {
            if (action.execute()) {
                actionHistory.undoPush(action);
                if (action instanceof FilterAction) this.updateUI(false);
            }
        }
    }
}
