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

package org.apromore.plugin.portal.processdiscoverer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import lombok.Getter;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.dao.model.User;
import org.apromore.logman.attribute.graph.MeasureType;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.PortalPlugin;
import org.apromore.plugin.portal.logfilter.generic.LogFilterPlugin;
import org.apromore.plugin.portal.processdiscoverer.actions.ActionManager;
import org.apromore.plugin.portal.processdiscoverer.components.CaseDetailsController;
import org.apromore.plugin.portal.processdiscoverer.components.CaseVariantDetailsController;
import org.apromore.plugin.portal.processdiscoverer.components.CostTableController;
import org.apromore.plugin.portal.processdiscoverer.components.GraphSettingsController;
import org.apromore.plugin.portal.processdiscoverer.components.GraphVisController;
import org.apromore.plugin.portal.processdiscoverer.components.LogStatsController;
import org.apromore.plugin.portal.processdiscoverer.components.PerspectiveDetailsController;
import org.apromore.plugin.portal.processdiscoverer.components.TimeStatsController;
import org.apromore.plugin.portal.processdiscoverer.components.ToolbarController;
import org.apromore.plugin.portal.processdiscoverer.components.ViewSettingsController;
import org.apromore.plugin.portal.processdiscoverer.data.ConfigData;
import org.apromore.plugin.portal.processdiscoverer.data.ContextData;
import org.apromore.plugin.portal.processdiscoverer.data.InvalidDataException;
import org.apromore.plugin.portal.processdiscoverer.data.OutputData;
import org.apromore.plugin.portal.processdiscoverer.data.UserOptionsData;
import org.apromore.plugin.portal.processdiscoverer.eventlisteners.AnimationController;
import org.apromore.plugin.portal.processdiscoverer.eventlisteners.BPMNExportController;
import org.apromore.plugin.portal.processdiscoverer.eventlisteners.LogExportController;
import org.apromore.plugin.portal.processdiscoverer.eventlisteners.LogFilterController;
import org.apromore.plugin.portal.processdiscoverer.impl.factory.PDFactory;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.BaseController;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.dialogController.dto.ApromoreSession;
import org.apromore.portal.menu.PluginCatalog;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.model.LogSummaryType;
import org.apromore.portal.model.PermissionType;
import org.apromore.portal.model.UserType;
import org.apromore.portal.plugincontrol.PluginExecution;
import org.apromore.portal.plugincontrol.PluginExecutionManager;
import org.apromore.portal.util.CostTable;
import org.apromore.service.AuthorizationService;
import org.apromore.service.ProcessService;
import org.apromore.service.SecurityService;
import org.apromore.service.loganimation.LogAnimationService2;
import org.apromore.util.AccessType;
import org.apromore.zk.event.CalendarEvents;
import org.apromore.zk.label.LabelSupplier;
import org.json.JSONException;
import org.slf4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Window;
import static org.apromore.logman.attribute.graph.MeasureType.DURATION;
import static org.apromore.logman.attribute.graph.MeasureType.FREQUENCY;


/**
 * PDController is the top-level application object to manage PD plugin as a
 * whole. It
 * <p>
 * Under PDController is different UI actions (under the actions package) and
 * components (under components package). PDController plays as a Mediator for
 * these UI actions and components. It mediates the communication between these
 * components to avoid direct communication between them.
 * <p>
 * Starting point of PDController: first, the window will be created which fires
 * onCreate(), then ZK client engine sends an onLoaded event to the main window
 * triggering windowListener().
 * <p>
 * PDController has three <b>modes</b>: MODEL view, ANIMATION view and TRACE
 * view. Initially it is in MODEL mode. Each <b>action</b> will change PD to
 * different modes. There are transition rules between modes and active state of
 * UI controls in each mode.
 * <p>
 * PDController provides an action management capability. Actions can be bundled
 * into Action object which can be undo/redo to reduce dependencies among
 * various objects.
 * <ul>
 * <li>UI Components such as ToolbarController and GraphVisController set up
 * these actions (Invoker)
 * <li>These actions have access to PDController and PDAnalyst (Receiver) to
 * fulfill the request
 * </ul>
 * 
 */

public class PDController extends BaseController implements Composer<Component>, LabelSupplier {

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(PDController.class);

    //////////////////// SUPPORT SERVICES ///////////////////////////////////

    @WireVariable("securityService")
    private SecurityService securityService;

    @WireVariable("authorizationService")
    private AuthorizationService authorizationService;

    @WireVariable
    private ProcessService processService;

    @WireVariable("logAnimationService2")
    private LogAnimationService2 logAnimationService;

    @WireVariable("logFilter")
    private LogFilterPlugin logFilterPlugin;

    //////////////////// THE MAIN PD Business Analyst //////////////////////////

    private PDAnalyst processAnalyst;
    private final ActionManager actionManager;

    //////////////////// UI COMPONENTS ///////////////////////////////////

    private Window mainWindow;
    private final DecimalFormat decimalFormat = new DecimalFormat("##############0.##");
    private GraphVisController graphVisController;
    private CaseDetailsController caseDetailsController;
    private CaseVariantDetailsController caseVariantDetailsController;
    private PerspectiveDetailsController perspectiveDetailsController;
    private ViewSettingsController viewSettingsController;
    private GraphSettingsController graphSettingsController;
    private LogStatsController logStatsController;
    private TimeStatsController timeStatsController;
    private LogFilterController logFilterController;
    private AnimationController animationController;
    private LogExportController logExportController;
    private BPMNExportController bpmnExportController;
    private CostTableController costTableController;
    private ToolbarController toolbarController;

    //////////////////// DATA ///////////////////////////////////

    private String pluginSessionId; // the session ID of this plugin

    @WireVariable
    private ConfigData configData;

    private ContextData contextData;
    private UserOptionsData userOptions;
    private OutputData outputData;
    private LogSummaryType logSummary;
    private PortalContext portalContext;

    @Getter
    private String sourceLogName;
    // plugin maintain log ID for Filter; Filter remove value to avoid
    // conflicts from multiple plugins
    private int sourceLogId;

    private InteractiveMode interactionMode = InteractiveMode.MODEL_MODE; // initial mode

    private Component pdComponent;
    private EventQueue<Event> sessionQueue;

    /////////////////////////////////////////////////////////////////////////

    public PDController() throws Exception {
        super();

        Map<String, Object> pageParams = new HashMap<>();
        String pluginExecutionId = PluginExecutionManager.registerPluginExecution(new PluginExecution(this),
                Sessions.getCurrent());
        pageParams.put("pluginExecutionId", pluginExecutionId);
        pageParams.put("pdLabels", getLabels());
        Executions.getCurrent().pushArg(pageParams);
        actionManager = new ActionManager(this);
    }

    @Override
    public String getBundleName() {
        return "pd";
    }

    // Note: this method is only valid inside onCreate() as it calls ZK current
    // Execution
    private boolean preparePluginSessionId() {
        pluginSessionId = Executions.getCurrent().getParameter("id");
        return !(pluginSessionId == null || UserSessionManager.getEditSession(pluginSessionId) == null);
    }

    // True means the current portal session is not valid to go ahead any more
    // It could be the Apromore Portal session has timed out or user has logged off,
    // or
    // or something has made it crashed
    private boolean preparePortalSession(String pluginSessionId) {
        ApromoreSession portalSession = UserSessionManager.getEditSession(pluginSessionId);
        if (portalSession == null) {
            return false;
        }
        portalContext = (PortalContext) portalSession.get("context");
        logSummary = (LogSummaryType) portalSession.get("selection");

        if (portalContext == null || logSummary == null) {
            return false;
        }

        sourceLogId = logSummary.getId();
        sourceLogName = logSummary.getName();

        try {
            FolderType currentFolder = portalContext.getCurrentFolder();
            if (currentFolder == null) {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }

        return true;
    }

    // This is to check the availability of system services before executing a
    // related action
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

        return true;
    }

    public void onCreate(Component comp) throws InterruptedException {
        try {
            init(comp);

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
            PDFactory pdFactory = (PDFactory) session.get("pdFactory");
            UserType currentUser = portalContext.getCurrentUser();

            User user = securityService.getUserById(currentUser.getId());
            AccessType currentUserAccessType = authorizationService.getLogAccessTypeByUser(logSummary.getId(), user);

            contextData = ContextData.valueOf(
                logSummary.getDomain(), currentUser.getUsername(),
                logSummary.getId(),
                logSummary.getName(),
                portalContext.getCurrentFolder() == null ? 0 : portalContext.getCurrentFolder().getId(),
                portalContext.getCurrentFolder() == null ? "Home"
                    : portalContext.getCurrentFolder().getFolderName(),
                ((MainController)portalContext.getMainController()).getConfig().isEnableCalendar()
                    && currentUser.hasAnyPermission(PermissionType.CALENDAR),
                currentUser.hasAnyPermission(PermissionType.MODEL_DISCOVER_EDIT));

            userOptions = UserOptionsData.DEFAULT(configData);
            String jsonString = getEventLogService().getCostTablesByLog(contextData.getLogId());
            ObjectMapper objectMapper = new ObjectMapper();
            //TODO: Set CostTable based on perspective types
            userOptions.setCostTable(!"".equals(jsonString) ? objectMapper.readValue(jsonString,
                    new TypeReference<List<CostTable>>(){}).iterator().next() :
                    CostTable.EMPTY);
            userOptions.setCalendarModel(getEventLogService().getCalendarFromLog(contextData.getLogId()));

            processAnalyst = new PDAnalyst(contextData, configData, getEventLogService());
            processAnalyst.loadAttributeData(configData.getDefaultAttribute(),
                userOptions.getCalendarModel(),
                userOptions.getCostTable());

            // On-load filtering if any filters are provided
            if (session.containsKey("logFilters")) {
                if (!processAnalyst.filter((List<LogFilterRule>)session.get("logFilters"))) {
                    Messagebox.show("The log is empty after applying log filter criteria. Stop opening Process Discoverer.");
                    return;
                } else {
                    LOGGER.info("Applied filter criteria to the log before opening Process Discoverer.");
                }
            }

            // Set up UI components
            graphVisController = pdFactory.createGraphVisController(this);
            caseDetailsController = pdFactory.createCaseDetailsController(this);
            caseVariantDetailsController = pdFactory.createCaseVariantDetailsController(this);
            perspectiveDetailsController = pdFactory.createPerspectiveDetailsController(this);
            viewSettingsController = pdFactory.createViewSettingsController(this);
            graphSettingsController = pdFactory.createGraphSettingsController(this);
            logStatsController = pdFactory.createLogStatsController(this);
            timeStatsController = pdFactory.createTimeStatsController(this);
            logFilterController = pdFactory.createLogFilterController(this);
            animationController = pdFactory.createAnimationController(this);
            logExportController = pdFactory.createLogExportController(this);
            bpmnExportController = pdFactory.createBPMNExportController(this);
            costTableController = pdFactory.createCostTableController(this);
            toolbarController = pdFactory.createToolbarController(this);
            costTableController.setViewOnly(
                AccessType.VIEWER.equals(currentUserAccessType) || AccessType.RESTRICTED.equals(currentUserAccessType)
            );

            initialize();
            LOGGER.debug("Session ID = " + ((HttpSession) Sessions.getCurrent().getNativeSession()).getId());
            LOGGER.debug("Desktop ID = " + comp.getDesktop().getId());

            // Finally, store objects to be cleaned up when the session timeouts
            comp.getDesktop().setAttribute("processAnalyst", processAnalyst);
            comp.getDesktop().setAttribute("pluginSessionId", pluginSessionId);
        } catch (InvalidDataException ex) {
            String errorMsg = "Missing log data, "
                + getLabel("missingActivity_title") + ", or "
                + getLabel("tooManyActivities_title");
            Messagebox.show(getLabel("initError_message") + ".\n Possible cause: " + errorMsg);
            LOGGER.error("Error occurred while initializing. Error message: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            Messagebox.show(getLabel("initError_message") + ".\n Error message: "
                + (ex.getMessage() == null ? "Internal errors occurred." : ex.getMessage()));
            LOGGER.error("Error occurred while initializing: " + ex.getMessage(), ex);
        }
    }

    // All data and controllers must be already available
    private void initialize() {
        try {
            initializeCalendar();
            initializeControls();
            timeStatsController.updateUI(contextData);
            viewSettingsController.updateUI(null);
            initializeEventListeners();
        } catch (Exception e) {
            LOGGER.error("Unable to initialize PD controller", e);
            Messagebox.show(getLabel("initError_message"), getLabel("initError_title"), Messagebox.OK,
                    Messagebox.ERROR);
        }
    }

    public void initializeCalendar() {
        sessionQueue = EventQueues.lookup(CalendarEvents.TOPIC, EventQueues.SESSION,true);
        sessionQueue.subscribe(event -> {
            String eventName = event.getName();
            String message = null;
            if (CalendarEvents.ON_CALENDAR_LINK.equals(eventName)) {
                int logId = (int) event.getData();
                if (logId == sourceLogId) {
                    message = Labels.getLabel("common_calendarUpdated_message");
                }
            } else if (CalendarEvents.ON_CALENDAR_UNLINK.equals(eventName)) {
                List<Integer> logIds = (List<Integer>) event.getData();
                if (logIds.contains(sourceLogId)) {
                    message = Labels.getLabel("common_calendarUnlinked_message");
                }
            } else if (CalendarEvents.ON_CALENDAR_REFRESH.equals(eventName)) {
                List<Integer> logIds = (List<Integer>) event.getData();
                if (logIds.contains(sourceLogId)) {
                    message = Labels.getLabel("common_calendarUpdated_message");
                }
            }
            if (message != null) {
                Messagebox.show(message,
                    new Messagebox.Button[] {Messagebox.Button.OK, Messagebox.Button.CANCEL},
                    (ClickEvent e) -> {
                        if (Messagebox.ON_OK.equals(e.getName())) {
                            Clients.evalJavaScript("window.location.reload()");
                        }
                    }
                );
            }
        });
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
        } catch (Exception ex) {
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
                putWindowAtTop(caseVariantDetailsController.getWindow());
                putWindowAtTop(perspectiveDetailsController.getWindow());
            });
            mainWindow.addEventListener("onCaseDetails", event -> caseDetailsController.onEvent(event));
            mainWindow.addEventListener("onCaseVariantDetails", event -> caseVariantDetailsController.onEvent(event));
            mainWindow.addEventListener("onPerspectiveDetails", event -> perspectiveDetailsController.onEvent(event));
        } catch (Exception ex) {
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
        if (this.interactionMode != InteractiveMode.MODEL_MODE) {
            return;
        }
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
            accessControlPlugin = portalPluginMap.get(PluginCatalog.PLUGIN_ACCESS_CONTROL);
            Map arg = new HashMap<>();
            arg.put("withFolderTree", false);
            arg.put("selectedItem", selectedItem);
            arg.put("currentUser", UserSessionManager.getCurrentUser());
            arg.put("autoInherit", true);
            arg.put("showRelatedArtifacts", true);
            accessControlPlugin.setSimpleParams(arg);
            accessControlPlugin.execute(portalContext);
        } catch (Exception e) {
            Messagebox.show(getLabel("failedShare_message"), getLabel("failedShare_title"), Messagebox.OK,
                    Messagebox.ERROR);
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void openCalendar() {
        ((MainController)portalContext.getMainController()).getBaseListboxController().launchCalendar(sourceLogName, sourceLogId);
    }

    public void openCost() {
        try {
            costTableController.onEvent(new Event("onCostTable"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void openAnimation(Event e) {
        try {
            animationController.onEvent(e);
        } catch (Exception ex) {
            Messagebox.show(getLabel("failedOpenAnimation_message"));
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    public void openLogExport(Event e) {
        try {
            logExportController.onEvent(e);
        } catch (Exception ex) {
            Messagebox.show(getLabel("failedExportLog_message"));
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    public void openBPMNExport(Event e) {
        try {
            bpmnExportController.onEvent(e);
        } catch (Exception ex) {
            Messagebox.show(getLabel("failedExportBPMN_message"));
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    public void openLogFilter(Event e) {
        try {
            logFilterController.onEvent(e);
        } catch (Exception ex) {
            Messagebox.show(getLabel("failedOpenFilter_message"));
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * Update UI This is used if the underlying data is changed via filtering
     *
     * @param reset: true to reset the graph zoom and panning
     */
    public void updateUI(boolean reset) {
        if (this.interactionMode != InteractiveMode.MODEL_MODE) {
            return;
        }
        try {
            logStatsController.updateUI(contextData);
            timeStatsController.updateUI(contextData);
            viewSettingsController.updateUI(contextData);
            userOptions.setRetainZoomPan(!reset);
            generateViz();
            if (!reset) {
                graphVisController.centerToWindow();
            }
            toolbarController.setDisabledFilterClear(this.getProcessAnalyst().isCurrentFilterCriteriaEmpty());
            toolbarController.updateUndoRedoButtons(actionManager.canUndo(), actionManager.canRedo());
        } catch (Exception ex) {
            Messagebox.show(getLabel("failedUpdateUI_message"));
            LOGGER.error("Errors occured while updating UI: " + ex.getMessage());
        }
    }

    /*
     * This is the main processing method calling to process-discoverer-logic
     */
    public void generateViz() {
        long timer1 = System.currentTimeMillis();

        if (this.interactionMode != InteractiveMode.MODEL_MODE) {
            return;
        }

        if (processAnalyst.hasEmptyData()) {
            Messagebox.show(getLabel("failedViz_message"), getLabel("title_text"), Messagebox.OK,
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
            Optional<OutputData> analysisResult = processAnalyst.discoverProcess(userOptions);
            if (analysisResult.isEmpty()) {
                Messagebox.show(getLabel("failedMapProcess_message"), getLabel("title_text"), Messagebox.OK,
                        Messagebox.ERROR);
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
            Messagebox.show(getLabel("otherError_message"), getLabel("title_text"), Messagebox.OK, Messagebox.ERROR);
        }
    }

    public boolean isBPMNView() {
        return this.userOptions.getBPMNMode();
    }

    public void setBPMNView(boolean isBPMNView) {
        if (this.interactionMode != InteractiveMode.MODEL_MODE) {
            return;
        }
        userOptions.setBPMNMode(isBPMNView);
        graphSettingsController.updateParallelism(isBPMNView);
        toolbarController.setDisabledModelExport(!isBPMNView);
        generateViz();
    }

    public void switchToAnimationView() {
        try {
            if (this.setInteractiveMode(InteractiveMode.ANIMATION_MODE)) {
                graphVisController.switchToAnimationView();
            }
        } catch (Exception ex) {
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

    public boolean disableGraphEditButtons() {
        return this.setInteractiveMode(InteractiveMode.TRACE_MODE);
    }

    /**
     * Mode represents an overall state of PD. It is used to set relevant status for
     * UI controls without having to consider state of each control. This method
     * updates the UI in accordance with the current mode.
     * 
     * @param newMode
     * @return: true if the mode has been changed
     */
    private boolean setInteractiveMode(InteractiveMode newMode) {
        if (newMode == InteractiveMode.ANIMATION_MODE) {
            if (this.interactionMode == InteractiveMode.TRACE_MODE) {
                return false; // invalid move
            }
            viewSettingsController.setDisabled(true);
            graphSettingsController.setDisabled(true);
            logStatsController.setDisabled(true);
            timeStatsController.setDisabled(true);
            toolbarController.setDisabled(true);
            toolbarController.setDisabledSearch(true);
            toolbarController.toogleAnimateBtn(true);
            caseDetailsController.setDisabled(true);
            caseVariantDetailsController.setDisabled(true);
        } else if (newMode == InteractiveMode.MODEL_MODE) {
            viewSettingsController.setDisabled(false);
            graphSettingsController.setDisabled(false);
            logStatsController.setDisabled(false);
            timeStatsController.setDisabled(false);
            toolbarController.setDisabled(false);
            toolbarController.setDisabledSearch(false);
            toolbarController.setDisabledAnimation(false);
            toolbarController.toogleAnimateBtn(false);
            caseDetailsController.setDisabled(false);
            caseVariantDetailsController.setDisabled(false);
        } else if (newMode == InteractiveMode.TRACE_MODE) {
            if (this.interactionMode == InteractiveMode.ANIMATION_MODE) {
                return false; // invalid move
            }
            viewSettingsController.setDisabled(true);
            graphSettingsController.setDisabled(true);
            logStatsController.setDisabled(true);
            timeStatsController.setDisabled(true);
            toolbarController.setDisabled(true);
            toolbarController.setDisabledSearch(false);
            toolbarController.setDisabledAnimation(true);
            toolbarController.toogleAnimateBtn(false);
        }
        this.interactionMode = newMode;
        return true;
    }

    public InteractiveMode getInteractiveMode() {
        return this.interactionMode;
    }

    public String getPerspectiveName() {
        return viewSettingsController.getPerspectiveName();
    }

    public void setPerspective(String value, String label) throws Exception {
        if (this.interactionMode != InteractiveMode.MODEL_MODE) {
            return;
        }
        if (!value.equals(userOptions.getMainAttributeKey())) {
            boolean disableVariantInspector = !"concept:name".equals(value);
            toolbarController.setDisabledAnimation(!value.equals(configData.getDefaultAttribute()));
            caseVariantDetailsController.setDisabledInspector(disableVariantInspector);
            userOptions.setMainAttributeKey(value);
            processAnalyst.loadAttributeData(value, userOptions.getCalendarModel(), userOptions.getCostTable());
            timeStatsController.updateUI(contextData);
            logStatsController.updateUI(contextData);
            logStatsController.updatePerspectiveHeading(label);
            logStatsController.updateVariantInspectorLink(disableVariantInspector);
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

    @Override
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

    public void refreshPortal() {
        this.portalContext.refreshContent();
    }

    @Override
    public String processRequest(Map<String, String[]> parameterMap) {
        String startFrameIndex = parameterMap.get("startFrameIndex")[0];
        String chunkSize = parameterMap.get("chunkSize")[0];
        try {
            String chunkJSON = graphVisController.getAnimationMovie()
                    .getChunkJSON(Integer.parseInt(startFrameIndex), Integer.parseInt(chunkSize)).toString();
            return escapeQuotedJavascript(chunkJSON);
        } catch (NumberFormatException | JSONException e) {
            LOGGER.error(e.getMessage(), e);
            return "Error: " + e.getMessage();
        }
    }

    /**
     * @param json
     * @return the <var>json</var> escaped so that it can be quoted in Javascript.
     *         Specifically, it replaces apostrophes with \\u0027 and removes
     *         embedded newlines and leading and trailing whitespace.
     */
    private String escapeQuotedJavascript(String json) {
        return json.replace("\n", " ").replace("'", "\\u0027").trim();
    }

    public ActionManager getActionManager() {
        return this.actionManager;
    }

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        this.pdComponent = comp;
        onCreate(comp);
    }

    @Override
    public Component query(String selector) {
        return this.pdComponent.query(selector);
    }

    @Override
    public Component getFellow(String compId) throws ComponentNotFoundException {
        return pdComponent.getFellow(compId);
    }
}
