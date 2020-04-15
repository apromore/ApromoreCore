/*
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2018-2020 The University of Melbourne.
 *
 * "Apromore Core" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore Core" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.plugin.portal.processdiscoverer;

import static org.apromore.logman.attribute.graph.MeasureType.DURATION;
import static org.apromore.logman.attribute.graph.MeasureType.FREQUENCY;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apromore.apmlog.APMLog;
import org.apromore.logman.ALog;
import org.apromore.logman.attribute.graph.MeasureAggregation;
import org.apromore.logman.attribute.graph.MeasureType;
import org.apromore.model.LogSummaryType;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.loganimation.LogAnimationPluginInterface;
import org.apromore.plugin.portal.logfilter.generic.LogFilterPlugin;
import org.apromore.plugin.portal.processdiscoverer.controllers.AnimationController;
import org.apromore.plugin.portal.processdiscoverer.controllers.BPMNExportController;
import org.apromore.plugin.portal.processdiscoverer.controllers.CaseDetailsController;
import org.apromore.plugin.portal.processdiscoverer.controllers.GraphSettingsController;
import org.apromore.plugin.portal.processdiscoverer.controllers.LogExportController;
import org.apromore.plugin.portal.processdiscoverer.controllers.LogStatsController;
import org.apromore.plugin.portal.processdiscoverer.controllers.PerspectiveDetailsController;
import org.apromore.plugin.portal.processdiscoverer.controllers.TimeStatsController;
import org.apromore.plugin.portal.processdiscoverer.controllers.ViewSettingsController;
import org.apromore.plugin.portal.processdiscoverer.controllers.VisualizationController;
import org.apromore.plugin.portal.processdiscoverer.data.ConfigData;
import org.apromore.plugin.portal.processdiscoverer.data.ContextData;
import org.apromore.plugin.portal.processdiscoverer.data.DataAwareController;
import org.apromore.plugin.portal.processdiscoverer.data.LogData;
import org.apromore.plugin.portal.processdiscoverer.data.NotFoundAttributeException;
import org.apromore.plugin.portal.processdiscoverer.data.OutputData;
import org.apromore.plugin.portal.processdiscoverer.data.UserOptions;
import org.apromore.plugin.portal.processdiscoverer.data.apmlog.LogDataWithAPMLog;
import org.apromore.plugin.portal.processdiscoverer.data.apmlog.LogFilterControllerWithAPMLog;
import org.apromore.plugin.portal.processdiscoverer.data.apmlog.LogStatsControllerWithAPMLog;
import org.apromore.plugin.portal.processdiscoverer.vis.json.ProcessJSONVisualizer;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.BaseController;
import org.apromore.portal.dialogController.dto.ApromoreSession;
import org.apromore.processdiscoverer.Abstraction;
import org.apromore.processdiscoverer.AbstractionParams;
import org.apromore.processdiscoverer.ProcessDiscoverer;
import org.apromore.service.CanoniserService;
import org.apromore.service.DomainService;
import org.apromore.service.EventLogService;
import org.apromore.service.ProcessService;
import org.deckfour.xes.model.XLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

/**
 * This class acts as the main controller for the main window.<br>
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
 */
public class PDController extends BaseController implements DataAwareController {

    ///////////////////// LOCAL CONSTANTS /////////////////////////////

    private final String LAYOUT_HIERARCHY = "layoutHierarchy";
    private final String LAYOUT_DAGRE_TB = "layoutDagreTopBottom";

    private final String FREQ_LABEL = "frequency";
    private final String DURATION_LABEL = "duration";


    ///////////////////// UI CONTROLS /////////////////////////////


    private Checkbox layoutHierarchy;
    private Checkbox layoutDagreTopBottom;


    private Button filter;
    private Button perspectiveDetails;
    private Button casesDetails;
    //private Button fitness;
    private Button animate;
    private Button fitScreen;

    private Button exportFilteredLog;
    private Button downloadPDF;
    private Button downloadPNG;
    private Button exportBPMN;
    // private Button exportBPMNAnnotatedForBIMP;


    private Window mainWindow;


    private Label perspectiveSelected;

    //////////////////// SUPPORT SERVICES ///////////////////////////////////

    private CanoniserService canoniserService;
    private DomainService domainService;
    private ProcessService processService;
    private EventLogService eventLogService;
    private ProcessDiscoverer processDiscoverer;
    //private LogAnimationPluginInterface logAnimationPluginInterface;
    private LogAnimationPluginInterface logAnimationPluginInterface;

    private LogFilterPlugin logFilterPlugin;

    //////////////////// LOCAL UTILITIES ///////////////////////////////////

    private static final Logger LOGGER = LoggerFactory.getLogger(PDController.class);
    private final DecimalFormat decimalFormat = new DecimalFormat("##############0.##");
    private VisualizationController visController;
    private CaseDetailsController caseDetailsController;

    private ViewSettingsController viewSettingsController;
    private GraphSettingsController graphSettingsController;
    private LogStatsController logStatsController;
    private TimeStatsController timeStatsController;

    private PerspectiveDetailsController perspectiveDetailsController;
    private ProcessJSONVisualizer processVisualizer = new ProcessJSONVisualizer();

    //////////////////// DATA ///////////////////////////////////

    private ConfigData configData;
    private ContextData contextData;
    private LogData logData;
    private UserOptions userOptions;
    private OutputData outputData;

    private String primaryTypeLabel;
    private String primaryAggregateCode;

    /////////////////////////////////////////////////////////////////////////

    public PDController() throws Exception {
        super();
    }

    public void onCreate() throws InterruptedException {
        String id = Executions.getCurrent().getParameter("id");
        if (id == null) {
            throw new AssertionError("No id parameter in URL");
        }

        ApromoreSession session = UserSessionManager.getEditSession(id);
        if (session == null) {
            throw new AssertionError("No edit session associated with id " + id);
        }

        // Prepare services
        canoniserService = (CanoniserService) beanFactory.getBean("canoniserService");
        domainService = (DomainService) beanFactory.getBean("domainService");
        processService = (ProcessService) beanFactory.getBean("processService");
        eventLogService = (EventLogService) beanFactory.getBean("eventLogService");
        logAnimationPluginInterface = (LogAnimationPluginInterface) beanFactory.getBean("logAnimationPlugin");
        logFilterPlugin = (LogFilterPlugin) beanFactory.getBean("logFilterPlugin");

        // Prepare config data
        configData = new ConfigData();
        PortalContext portalContext = (PortalContext) session.get("context");
        contextData = new ContextData(
                portalContext,
                portalContext.getCurrentFolder() == null ? 0 : portalContext.getCurrentFolder().getId(),
                portalContext.getCurrentFolder() == null ? "Home" : portalContext.getCurrentFolder().getFolderName(),
                (LogSummaryType) session.get("selection")
        );

        userOptions = new UserOptions();
        // userOptions.setUseDynamic(true); // TO CHECK default value
        // userOptions.setPrimaryType((MeasureType)session.get("visType"));
        // userOptions.setPrimaryAggregation((userOptions.getPrimaryType() == FREQUENCY) ? MeasureAggregation.CASES : MeasureAggregation.MEAN);
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
        XLog xlog = eventLogService.getXLog(contextData.getLogId());
        APMLog apmLog = eventLogService.getAggregatedLog(contextData.getLogId());

        if (xlog != null && apmLog != null) {
            long timer = System.currentTimeMillis();
            ALog aLog = new ALog(xlog);
            System.out.println("ALog.constructor: " + (System.currentTimeMillis() - timer) + " ms.");
            logData = new LogDataWithAPMLog(configData, aLog, apmLog);
            try {
                logData.setMainAttribute(configData.getDefaultAttribute());
            } catch (NotFoundAttributeException e) {
                Messagebox.show(e.getMessage(), "Process Discoverer", Messagebox.OK, Messagebox.ERROR);
            }
            userOptions.setMainAttributeKey(configData.getDefaultAttribute());
            processDiscoverer = new ProcessDiscoverer(logData.getAttributeLog());

            // Set up controllers
            visController = new VisualizationController(this);
            caseDetailsController = new CaseDetailsController(this);
            perspectiveDetailsController = new PerspectiveDetailsController(this);
            viewSettingsController = new ViewSettingsController(this);
            graphSettingsController = new GraphSettingsController(this);
            logStatsController = new LogStatsControllerWithAPMLog(this);
            timeStatsController = new TimeStatsController(this);

            initialize();
            initializeDefaults();
        } else {
            Messagebox.show("Cannot obtain logs from EventLog Service for log id = " + contextData.getLogId(), "Process Discoverer", Messagebox.OK, Messagebox.ERROR);
        }
    }

    // All data and controllers must be all already available
    private void initialize() {
        try {
            initializeControls();
            updateTimeStats();
            viewSettingsController.updatePerspectiveSelector();
            initializeEventListeners();
        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show(e.getMessage(), "Process Discoverer", Messagebox.OK, Messagebox.ERROR);
        }
    }

    // Adjust UI when detecting special conditions
    private void initializeDefaults() {
        // if (logData.getAttributeLog().getValues().size() > configData.getAttributeUniqueValuesToForceAdjust()) {
        //   userOptions.setNodeFilterValue(80);
        //   nodeSlider.setCurpos(userOptions.getNodeFilterValue());
        //   nodeInput.setValue((int)userOptions.getNodeFilterValue());
        // }
    }

    private void initializeControls() {
        mainWindow = (Window) this.getFellow("win");
        mainWindow.setTitle(contextData.getLogName());

        viewSettingsController.initializeControls();
        graphSettingsController.initializeControls();
        timeStatsController.initializeControls();
        logStatsController.initializeControls();
        visController.initializeControls();

        Component compLogStats = mainWindow.query(".ap-pd-logstats");

        perspectiveSelected = (Label) compLogStats.getFellow("perspectiveSelected");

        // Main action buttons
        casesDetails = (Button) mainWindow.getFellow("caseDetails");
        perspectiveDetails = (Button) mainWindow.getFellow("perspectiveDetails");
        //fitness = (Button) mainWindow.getFellow("fitness");
        filter = (Button) mainWindow.getFellow("filter");
        animate = (Button) mainWindow.getFellow("animate");
        fitScreen = (Button) mainWindow.getFellow("fitScreen");

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

    private void initializeEventListeners() {
        PDController self = this;

        viewSettingsController.initializeEventListeners();
        graphSettingsController.initializeEventListeners();
        visController.initializeEventListeners();

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
                visController.fitToWindow();
            }
        });

        casesDetails.addEventListener("onClick", caseDetailsController);
        perspectiveDetails.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                String perspectiveName = self.getPerspective();
                perspectiveDetailsController.launch(perspectiveName);
            }
        });

        filter.addEventListener("onClick", new LogFilterControllerWithAPMLog(this));
        // fitness.addEventListener("onClick", new FitnessCalculationController(this));
        //animate.addEventListener("onAnimate", new AnimationController(this));
        animate.addEventListener("onClick", new AnimationController(this));

        exportFilteredLog.addEventListener("onExport", new LogExportController(this));
        exportBPMN.addEventListener("onClick", new BPMNExportController(this, false, false));
        downloadPDF.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                visController.exportPDF(getOutputName());
            }
        });
        downloadPNG.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                visController.exportPNG(getOutputName());
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
        visController.changeLayout();
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
        updateLogStats();
        updateTimeStats();
        viewSettingsController.updatePerspectiveSelector();
        userOptions.setRetainZoomPan(!reset);
        generateViz();
        if (!reset) {
            visController.centerToWindow();
        }
    }

    public boolean getBPMNMode() {
        return userOptions.getBPMNMode();
    }

    private void updateTimeStats() {
        this.timeStatsController.updateValues();
    }

    public void updateLogStats() {
        this.logStatsController.updateValues();
    }

    public void setOverlay(
            MeasureType primaryType,
            MeasureAggregation primaryAggregation,
            MeasureType secondaryType,
            MeasureAggregation secondaryAggregation,
            String aggregateCode
    ) throws InterruptedException {

        userOptions.setPrimaryType(primaryType);
        userOptions.setPrimaryAggregation(primaryAggregation);
        userOptions.setSecondaryType(secondaryType);
        userOptions.setSecondaryAggregation(secondaryAggregation);

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
            MeasureType primaryType, MeasureAggregation primaryAggregation,
            MeasureType secondaryType, MeasureAggregation secondaryAggregation
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
                primaryType,
                primaryAggregation,
                secondaryType,
                secondaryAggregation,
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
                userOptions.getSecondaryType(),
                userOptions.getSecondaryAggregation()
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
            outputData = new OutputData(currentAbstraction, visualizedText);
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            System.out.println("Sent json data to browser at " + formatter.format(new Date()));

            visController.displayDiagram(visualizedText);
            contextData.setFirstTimeLoadingFinished(true);

        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show(!e.getMessage().trim().isEmpty() ? e.getMessage() : "Unexpected error has occurred! Check log files.",
                    "Process Discoverer",
                    Messagebox.OK,
                    Messagebox.ERROR);
        }
    }

//    public void applyCriterion(LogFilterRule logFilterCriterion) throws InterruptedException {
//        List<LogFilterRule> criteria = userOptions.getFilterCriteria();
//        if (!criteria.contains(logFilterCriterion)) {
//            List<LogFilterRule> newCriteria = new ArrayList<>(criteria);
//            newCriteria.add(logFilterCriterion);
//            try {
//                if (!this.filter(newCriteria, criteria)) {
//                    Messagebox.show("The log is empty after applying all filter criteria! Please use different criteria.",
//                            "Process Discoverer",
//                            Messagebox.OK,
//                            Messagebox.INFORMATION);
//                } else {
//                    userOptions.setFilterCriteria(newCriteria);
//                    this.updateUI(false);
//                }
//            } catch (InvalidLogBitMapException | InvalidALogStatusUpdateException | InvalidAttributeLogStatusUpdateException ex) {
//                Messagebox.show("Invalid log data status update from filtering!",
//                        "Process Discoverer",
//                        Messagebox.OK,
//                        Messagebox.ERROR);
//            }
//        }
//    }
//
//    private boolean filter(List<LogFilterRule> newCriteria, List<LogFilterRule> oldCriteria) throws InvalidLogBitMapException, InvalidALogStatusUpdateException, InvalidAttributeLogStatusUpdateException {
//        this.apmLogFilter.filter(newCriteria); // 2019-11-18
//        if (apmLogFilter.getPLog().getPTraceList().isEmpty()) { // Restore to the last state
//            apmLogFilter.filter(oldCriteria);
//            return false;
//        } else {
//            this.logData.updateLog(apmLogFilter.getPLog(), apmLogFilter.getApmLog());
//            return true;
//        }
//    }

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
            perspectiveSelected.setClientAttribute("title", label);
            animate.setDisabled(!value.equals(configData.getDefaultAttribute()));
            userOptions.setMainAttributeKey(value);
            logData.setMainAttribute(value);
            updateTimeStats();
            updateLogStats();
            generateViz();
        }
    }

    @Override
    public ContextData getContextData() {
        return this.contextData;
    }

    @Override
    public LogData getLogData() {
        return this.logData;
    }

    @Override
    public UserOptions getUserOptions() {
        return this.userOptions;
    }

    @Override
    public OutputData getOutputData() {
        return this.outputData;
    }

    @Override
    public ConfigData getConfigData() {
        return this.configData;
    }

    public String getLogName() { return contextData.getLogName(); }

    public ProcessDiscoverer getProcessDiscoverer() {
        return this.processDiscoverer;
    }

    public DomainService getDomainService() {
        return domainService;
    }

    public CanoniserService getCanoniserService() {
        return canoniserService;
    }

    public EventLogService getEvenLogService() {
        return eventLogService;
    }

    public ProcessService getProcessService() {
        return processService;
    }

    public LogAnimationPluginInterface getLogAnimationPlugin() {
        return this.logAnimationPluginInterface;
    }

    public LogFilterPlugin getLogFilterPlugin() {
        return logFilterPlugin;
    }

    public DecimalFormat getDecimalFormatter() {
        return this.decimalFormat;
    }
}