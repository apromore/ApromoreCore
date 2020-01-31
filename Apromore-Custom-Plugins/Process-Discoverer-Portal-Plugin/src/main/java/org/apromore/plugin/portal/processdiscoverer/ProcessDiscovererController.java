/*
 * Copyright © 2019 The University of Melbourne.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.plugin.portal.processdiscoverer;

import static org.apromore.processdiscoverer.VisualizationAggregation.CASES;
import static org.apromore.processdiscoverer.VisualizationAggregation.MAX;
import static org.apromore.processdiscoverer.VisualizationAggregation.MEAN;
import static org.apromore.processdiscoverer.VisualizationAggregation.MEDIAN;
import static org.apromore.processdiscoverer.VisualizationAggregation.MIN;
import static org.apromore.processdiscoverer.VisualizationAggregation.MODE;
import static org.apromore.processdiscoverer.VisualizationAggregation.TOTAL;
import static org.apromore.processdiscoverer.VisualizationType.DURATION;
import static org.apromore.processdiscoverer.VisualizationType.FREQUENCY;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.UIManager;
import javax.xml.datatype.DatatypeFactory;

import org.apromore.logfilter.LogFilterService;
import org.apromore.logfilter.criteria.LogFilterCriterion;
import org.apromore.logfilter.criteria.factory.LogFilterCriterionFactory;
import org.apromore.logfilter.criteria.model.Action;
import org.apromore.logfilter.criteria.model.Containment;
import org.apromore.logfilter.criteria.model.Level;
import org.apromore.logman.stats.LogStatistics;
import org.apromore.model.LogSummaryType;
import org.apromore.model.SummaryType;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.loganimation.LogAnimationPluginInterface;
import org.apromore.plugin.portal.logfilter.generic.LogFilterContext;
import org.apromore.plugin.portal.logfilter.generic.LogFilterInputParams;
import org.apromore.plugin.portal.logfilter.generic.LogFilterOutputResult;
import org.apromore.plugin.portal.logfilter.generic.LogFilterPlugin;
import org.apromore.plugin.portal.logfilter.generic.LogFilterResultListener;
import org.apromore.plugin.portal.processdiscoverer.json.JSONBuilder;
import org.apromore.plugin.portal.processdiscoverer.util.StringValues;
import org.apromore.plugin.portal.processdiscoverer.util.TimeConverter;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.BaseController;
import org.apromore.portal.dialogController.dto.SignavioSession;
import org.apromore.processdiscoverer.AbstractionParams;
import org.apromore.processdiscoverer.ProcessDiscoverer;
import org.apromore.processdiscoverer.VisualizationAggregation;
import org.apromore.processdiscoverer.VisualizationType;
import org.apromore.processdiscoverer.dfg.ArcType;
import org.apromore.processdiscoverer.dfg.abstraction.AbstractAbstraction;
import org.apromore.processdiscoverer.dfg.abstraction.DFGAbstraction;
import org.apromore.processdiscoverer.dfg.abstraction.TraceAbstraction;
import org.apromore.processdiscoverer.dfg.vis.BPMNDiagramBuilder;
import org.apromore.processdiscoverer.logprocessors.ActivityClassifier;
import org.apromore.processdiscoverer.logprocessors.EventClassifier;
import org.apromore.processdiscoverer.logprocessors.LogUtils;
import org.apromore.processdiscoverer.qualitymeasures.AlignmentBasedFitness;
import org.apromore.service.CanoniserService;
import org.apromore.service.DomainService;
import org.apromore.service.EventLogService;
import org.apromore.service.ProcessService;
import org.apromore.service.bimp_annotation.BIMPAnnotationService;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectIntHashMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.json.JSONArray;
import org.processmining.contexts.uitopia.UIContext;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.plugins.bpmn.BpmnDefinitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobutton;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Slider;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import org.zkoss.zul.*;
import javax.swing.*;
import javax.xml.datatype.DatatypeFactory;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Pattern;

import static org.apromore.processdiscoverer.VisualizationAggregation.*;
import static org.apromore.processdiscoverer.VisualizationType.DURATION;
import static org.apromore.processdiscoverer.VisualizationType.FREQUENCY;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * Initialization: after the window has been loaded, the ZK client engine will send onLoaded event to the main window 
 * TODO: as this is open in a separate browser tab with a different ZK execution from that of the portal,
 * if the user signs out of the portal tab, the actions in this plugin calling to the portal session would fail
 * Similarly, this plugin stores the containing folder on the portal to a local variable. So if the user deletes or
 * move that folder in the portal, the related actions here would fail.   
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 * Modified by Simon Rabozi for SiMo
 * Modified by Bruce Nguyen
 */
public class ProcessDiscovererController extends BaseController implements LogFilterResultListener {
	public static final String DEFAULT_SELECTOR = LogStatistics.DEFAULT_CLASSIFIER_KEY;
	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessDiscovererController.class);
    private final DecimalFormat decimalFormat = new DecimalFormat("##############0.##");
    
    private final String STARTEVENT_REL_PATTERN = "|> =>";
    private final String STARTEVENT_NEW_REL_PATTERN = "[Start] =>";
    
    private final String ENDEVENT_REL_PATTERN = "=> []";
    private final String ENDEVENT_NEW_REL_PATTERN = "=> [End]";
    
    private final String XOR_FROM_PATTERN = "X =>";
    private final String XOR_TO_PATTERN = "=> X";
    
    private final String OR_FROM_PATTERN = "O =>";
    private final String OR_TO_PATTERN = "=> O";
    
    private final String AND_FROM_PATTERN = "+ =>";
    private final String AND_TO_PATTERN = "=> +";
    
    // These are the types of directly-follow relations to be visualized from logs 
    // Event logs usually contain only complete events or both start and complete events
    // Modify to suite different types of events
    private final ArcType[] arcTypes = {ArcType.START, ArcType.END, ArcType.SS, ArcType.SC, ArcType.CS, ArcType.CC}; 

    PortalContext portalContext;

    private Radio use_fixed;
    private Radio use_dynamic;
    private Checkbox gateways;
    private Checkbox secondary;
    private Checkbox inverted_nodes;
    private Checkbox inverted_arcs;

    private Intbox activitiesText;
    private Intbox arcsText;
    private Slider activities;
    private Slider arcs;
    private Slider parallelism;
    private Intbox parallelismText;
    private Row parallelismRow;

    private Combobutton selectorButton;
    private Menupopup selector;

    private Combobutton frequency;
    private Menuitem case_frequency;
    private Menuitem absolute_frequency;
    private Menuitem median_frequency;
    private Menuitem mean_frequency;
    private Menuitem mode_frequency;
    private Menuitem max_frequency;
    private Menuitem min_frequency;

    private Combobutton duration;
    private Menuitem total_duration;
    private Menuitem median_duration;
    private Menuitem mean_duration;
    private Menuitem max_duration;
    private Menuitem min_duration;
    
    private Combobutton layout;
    private Menuitem layout_hiera;
    //private Menuitem layout_dagre_LR;
    private Menuitem layout_dagre_TB;
    //private Menuitem layout_bf;

    private Button filter;
    private Button details;
    private Button cases;
    private Button fitness;
    private Button animate;
    private Button fitScreen;
    
    private Menuitem exportFilteredLog;
    
    private Label caseNumber;
    private Label uniquecaseNumber;
    private Label activityNumber;
    private Label eventNumber;
    private Label meanDuration;
    private Label medianDuration;
    private Label maxDuration;
    private Label minDuration;
    private Label logStartTime;
    private Label logEndTime;
    
    private Window cases_window = null;

    private int arcs_value = 10;
    private int parallelism_value = 40;
    private int activities_value = 100;

    private VisualizationType fixedType = FREQUENCY;
    private VisualizationAggregation fixedAggregation = TOTAL;

    private VisualizationType primaryType = FREQUENCY;
    private VisualizationAggregation primaryAggregation = TOTAL;
    private VisualizationType secondaryType = DURATION;
    private VisualizationAggregation secondaryAggregation = MEDIAN;

//    public boolean visualized = false;
    private String log_name = "";
    private XLog initial_log;
    private XLog filtered_log;
    private List<List<String>> filtered_log_cases; // list of cases in the filtered log (log after applying filter criteria)
    
    private BPMNDiagram diagram;
    private AbstractAbstraction currentAbstraction;
    private JSONArray jsonDiagram; // the corresponding JSON format of the diagram
    private LogSummaryType logSummary;

    private List<LogFilterCriterion> criteria;
    
    //key: type of attribute (see LogFilterTypeSelector), value: map (key: attribute value, value: frequency count)
    private LogStatistics local_stats = null; // for filtered log
    private LogStatistics global_stats = null; // always for the original log
    
    private long min = Long.MAX_VALUE; //the earliest timestamp of the log
    private long max = 0; //the latest timestamp of the log

    private String label = DEFAULT_SELECTOR; // the event attribute key used to label each task node, default "concept:name"
    
    private int selectedLayout = 0; //0: hierarchical, 1: dagre_LR, 2: dagre_TB, 3: breadth-first
    private boolean retainZoomPan = false;
    
    private CanoniserService canoniserService;
    private DomainService domainService;
    private ProcessService processService;
    private EventLogService eventLogService;
    private ProcessDiscoverer processDiscoverer;
    private LogAnimationPluginInterface logAnimationPluginInterface;
    private BIMPAnnotationService bimpAnnotationService;
    private LogFilterService logFilterService;
    private LogFilterCriterionFactory logFilterCriterionFactory;
    private LogFilterPlugin logFilterPlugin;
    
    private SummaryType selection = null;
    
    private int containingFolderId = 0;
    private String containingFolderName = "";
    
    public ProcessDiscovererController() throws Exception {
    	super();
    }
    
    
    public void onCreate() throws InterruptedException {

        // *******  profiling code start here ********
        long startTime = System.nanoTime();
        // *******  profiling code end here ********

        String id = Executions.getCurrent().getParameter("id");
        if (id == null) {
            throw new AssertionError("No id parameter in URL");
        }

        SignavioSession session = UserSessionManager.getEditSession(id);
        if (session == null) {
            throw new AssertionError("No edit session associated with id " + id);
        }

        canoniserService = (CanoniserService)beanFactory.getBean("canoniserService");
        domainService = (DomainService)beanFactory.getBean("domainService");
        processService = (ProcessService)beanFactory.getBean("processService");
        eventLogService = (EventLogService)beanFactory.getBean("eventLogService");
        logAnimationPluginInterface = (LogAnimationPluginInterface)beanFactory.getBean("logAnimationPlugin");
        bimpAnnotationService = (BIMPAnnotationService)beanFactory.getBean("bimpAnnotationService");
        logFilterService = (LogFilterService)beanFactory.getBean("logFilterService");
        logFilterCriterionFactory = (LogFilterCriterionFactory)beanFactory.getBean("logFilterCriterionFactory");
        logFilterPlugin = (LogFilterPlugin)beanFactory.getBean("logFilterPlugin");
        
        portalContext = (PortalContext)session.get("context");
        primaryType = (VisualizationType)session.get("visType");
        selection = (SummaryType)session.get("selection");
        containingFolderId = portalContext.getCurrentFolder() == null ? 0 : portalContext.getCurrentFolder().getId();
        containingFolderName = portalContext.getCurrentFolder() == null ? "Home" : portalContext.getCurrentFolder().getFolderName();
        
        primaryAggregation = (primaryType == FREQUENCY) ? VisualizationAggregation.CASES : VisualizationAggregation.MEAN;
        logSummary = (LogSummaryType) selection;
        log_name = logSummary.getName();
        Sessions.getCurrent().setAttribute("filtered_log_name", log_name); //to provide logName to Filter plugin

        initial_log = eventLogService.getXLog(logSummary.getId());
        if (initial_log != null) {
        	XLog initial_log_filtered = filterKeepingStartCompleteEvents(initial_log);
        	if (initial_log_filtered.isEmpty()) {
        		LogUtils.addCompleteLifecycle(initial_log);
        	}
        	else {
        		initial_log = initial_log_filtered;
        	}
        	
    		filtered_log = initial_log;
        	processDiscoverer = new ProcessDiscoverer();
	        generateGlobalStatistics(initial_log);
	        generateLocalStatistics(initial_log);
	        criteria = new ArrayList<>();
	        start();
        }
        else {
        	throw new AssertionError("Cannot obtain log file for log id = " + logSummary.getId());
        }

        // *******  profiling code start here ********
        long elapsedNanos = System.nanoTime() - startTime;
        LOGGER.info("Elapsed time: " + elapsedNanos / 1000000 + " ms");

//        System.gc();
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//        }
//        LOGGER.info("Memory Used: " + ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1024 / 1024 + " MB ");

        // *******  profiling code end here ********
    }

    protected MemoryUsage getMemoryUsage() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        return memoryMXBean.getHeapMemoryUsage();
    }

    private void start() {
        try {
            Window slidersWindow = (Window) this.getFellow("win");
            //slidersWindow = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), StringValues.b[16], null, null);
            slidersWindow.setTitle(logSummary != null ? logSummary.getName() : "");
            
            slidersWindow.addEventListener("onZIndex", new EventListener<Event>() {
            	public void onEvent(Event event) throws Exception {
                    if (cases_window != null && cases_window.inOverlapped()) {
                    	cases_window.setZindex(slidersWindow.getZIndex() + 1);
                    }
                }
            });
            
            this.use_fixed = (Radio) slidersWindow.getFellow(StringValues.b[20]);
            this.use_dynamic = (Radio) slidersWindow.getFellow(StringValues.b[21]);
            this.gateways = (Checkbox) slidersWindow.getFellow(StringValues.b[23]);
//            this.isShowingBPMN = gateways.isChecked();
            this.secondary = (Checkbox) slidersWindow.getFellow(StringValues.b[25]);
            this.inverted_nodes = (Checkbox) slidersWindow.getFellow(StringValues.b[26]);
            this.inverted_arcs = (Checkbox) slidersWindow.getFellow(StringValues.b[27]);

            this.activities = (Slider) slidersWindow.getFellow(StringValues.b[28]);
            this.arcs = (Slider) slidersWindow.getFellow(StringValues.b[29]);
            this.activitiesText = (Intbox) slidersWindow.getFellow(StringValues.b[31]);
            this.arcsText = (Intbox) slidersWindow.getFellow(StringValues.b[32]);
            
            this.parallelism = (Slider) slidersWindow.getFellow("parallelismSlider");
            this.parallelismText = (Intbox) slidersWindow.getFellow("parallelismText");
            this.parallelismRow = (Row) slidersWindow.getFellow("parallelismRow");

            this.caseNumber = (Label) slidersWindow.getFellow(StringValues.b[34]);
            this.uniquecaseNumber = (Label) slidersWindow.getFellow(StringValues.b[35]);
            this.activityNumber = (Label) slidersWindow.getFellow(StringValues.b[36]);
            this.eventNumber = (Label) slidersWindow.getFellow(StringValues.b[37]);
            this.meanDuration = (Label) slidersWindow.getFellow(StringValues.b[38]);
            this.medianDuration = (Label) slidersWindow.getFellow(StringValues.b[39]);
            this.maxDuration = (Label) slidersWindow.getFellow(StringValues.b[40]);
            this.minDuration = (Label) slidersWindow.getFellow(StringValues.b[41]);
            this.logStartTime = (Label) slidersWindow.getFellow("startTime");
            this.logEndTime = (Label) slidersWindow.getFellow("endTime");

            this.selector = (Menupopup) slidersWindow.getFellow(StringValues.b[42]);
            this.selectorButton = (Combobutton) slidersWindow.getFellow("selector");

            this.frequency = (Combobutton) slidersWindow.getFellow(StringValues.b[43]);
            this.absolute_frequency = (Menuitem) slidersWindow.getFellow(StringValues.b[44]);
            this.case_frequency = (Menuitem) slidersWindow.getFellow(StringValues.b[45]);
            this.median_frequency = (Menuitem) slidersWindow.getFellow(StringValues.b[46]);
            this.mean_frequency = (Menuitem) slidersWindow.getFellow(StringValues.b[47]);
            this.mode_frequency = (Menuitem) slidersWindow.getFellow(StringValues.b[48]);
            this.max_frequency = (Menuitem) slidersWindow.getFellow(StringValues.b[49]);
            this.min_frequency = (Menuitem) slidersWindow.getFellow(StringValues.b[50]);

            this.duration = (Combobutton) slidersWindow.getFellow(StringValues.b[51]);
            this.total_duration = (Menuitem) slidersWindow.getFellow(StringValues.b[52]);
            this.median_duration = (Menuitem) slidersWindow.getFellow(StringValues.b[53]);
            this.mean_duration = (Menuitem) slidersWindow.getFellow(StringValues.b[54]);
            this.max_duration = (Menuitem) slidersWindow.getFellow(StringValues.b[55]);
            this.min_duration = (Menuitem) slidersWindow.getFellow(StringValues.b[56]);
            
            this.layout = (Combobutton) slidersWindow.getFellow("layout");
            this.layout_hiera = (Menuitem) slidersWindow.getFellow("layout_hiera");
            //this.layout_dagre_LR = (Menuitem) slidersWindow.getFellow("layout_dagre_LR");
            this.layout_dagre_TB = (Menuitem) slidersWindow.getFellow("layout_dagre_TB");
            //this.layout_bf = (Menuitem) slidersWindow.getFellow("layout_bf");

            this.details = (Button) slidersWindow.getFellow(StringValues.b[63]);
            this.cases = (Button) slidersWindow.getFellow(StringValues.b[64]);
            this.fitness = (Button) slidersWindow.getFellow(StringValues.b[65]);
            this.filter = (Button) slidersWindow.getFellow(StringValues.b[66]);
            this.animate = (Button) slidersWindow.getFellow(StringValues.b[67]);
            this.fitScreen = (Button) slidersWindow.getFellow("fitScreen");

            this.exportFilteredLog = (Menuitem) slidersWindow.getFellow(StringValues.b[69]);

            Combobutton export = (Combobutton) slidersWindow.getFellow(StringValues.b[70]);
            Menuitem downloadPDF = (Menuitem) slidersWindow.getFellow(StringValues.b[71]);
            Menuitem downloadPNG = (Menuitem) slidersWindow.getFellow(StringValues.b[72]);
            Menuitem exportBPMN = (Menuitem) slidersWindow.getFellow(StringValues.b[73]);
            Menuitem exportBPMNAnnotatedForBIMP = (Menuitem) slidersWindow.getFellow("exportBPMNAnnotatedForBIMP");

            populateMetrics(filtered_log);
            
            for (String option : generateLabels(filtered_log)) {
                Menuitem item = new Menuitem(option);
                item.setCheckmark(true);
                item.setAutocheck(true);
                item.setChecked(option.equals(DEFAULT_SELECTOR) ? true : false);
                item.addEventListener("onClick", new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                    	for (Component comp: item.getParent().getChildren()) {
                    		if (comp != item) ((Menuitem)comp).setChecked(false);
                    	}
                    	
                        if (!item.getLabel().equals(getLabel())) {
                        	setLabel(item.getLabel());
                        	generateGlobalStatistics(initial_log);
                        	generateLocalStatistics(filtered_log);
                            populateMetrics(filtered_log);
                            visualizeMap();
                            filtered_log_cases = getCases(filtered_log);
                        }
                        
                    }
                });
                selector.appendChild(item);
            }
            
            selectorButton.addEventListener("onClick", new EventListener<Event>() {
            	public void onEvent(Event event) throws Exception {
            		for (Component comp: selector.getChildren()) {
                		Menuitem item = (Menuitem)comp;
                		if (item.getLabel().equals(DEFAULT_SELECTOR)) {
                			item.setChecked(true);
                			Events.postEvent(Events.ON_CLICK, item, null);
                			break;
                		}
                	}
            	}
            });
            
            EventListener<Event> secondaryListener = new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                	retainZoomPan = true;
                	visualizeMap();
                }
            };            

            EventListener<Event> radioListener = new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                	if (!gateways.isChecked()) {
                		//parallelism.setStyle("background-color:#D8D8D8; width:100%; height:25px;");
                		parallelismRow.setSclass("z-row-disabled");
                		parallelismText.setDisabled(true);
                		//parallelismLabel.setStyle("text-color:#D8D8D8");
                		parallelism_value = parallelism.getCurpos();
                	}
                	else {
                		//parallelism.setStyle("background-color:transparent; width:100%; height:25px;");
                		parallelismRow.setSclass("z-row");
                		parallelismText.setDisabled(false);
                	}
                	
                	if (use_dynamic.isChecked()) {
                		fixedType = primaryType;
                		fixedAggregation = primaryAggregation;
                	}
                	else {
                		fixedType = VisualizationType.FREQUENCY;
                		fixedAggregation = VisualizationAggregation.TOTAL;
                	}
                	
                    visualizeMap();
                }
            };
            this.use_fixed.addEventListener("onCheck", radioListener);
            this.use_dynamic.addEventListener("onCheck", radioListener);
            this.gateways.addEventListener("onCheck", radioListener);
            this.inverted_nodes.addEventListener("onCheck", radioListener);
            this.inverted_arcs.addEventListener("onCheck", radioListener);
            
            this.secondary.addEventListener("onCheck", secondaryListener);
            
            this.activities.addEventListener("onScroll", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    activitiesText.setValue(activities.getCurpos());
                    visualizeMap();
                }
            });
            this.arcs.addEventListener("onScroll", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    arcsText.setValue(arcs.getCurpos());
                    visualizeMap();
                }
            });
            
            this.parallelism.addEventListener("onScrolling", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    if (!gateways.isChecked()) parallelism.setCurpos(parallelism_value);
                }
            });
            
            this.parallelism.addEventListener("onScroll", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                	if (gateways.isChecked()) {
                		parallelismText.setValue(parallelism.getCurpos());
                		visualizeMap();
                	}
                	else {
                		parallelism.setCurpos(parallelism_value);
                	}
                }
            });

            EventListener<Event> actChangeListener = new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    int i = activitiesText.getValue();
                    if (i < 0) i = 0;
                    else if (i > 100) i = 100;
                    activitiesText.setValue(i);
                    activities.setCurpos(i);
                    visualizeMap();
                }
            };
            EventListener<Event> actChangingListener = new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    String s = ((InputEvent) event).getValue();
                    if (!s.isEmpty()) {
                        int i = Integer.parseInt(s);
                        if (i < 0) i = 0;
                        else if (i > 100) i = 100;
                        activitiesText.setValue(i);
                        activities.setCurpos(i);
                        visualizeMap();
                    }
                }
            };
            this.activitiesText.addEventListener(StringValues.b[77], actChangeListener);
            this.activitiesText.addEventListener(StringValues.b[78], actChangingListener);

            EventListener<Event> arcChangeListener = new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    int i = arcsText.getValue();
                    if (i < 0) i = 0;
                    else if (i > 100) i = 100;
                    arcsText.setValue(i);
                    arcs.setCurpos(i);
                    visualizeMap();
                }
            };
            EventListener<Event> arcChangingListener = new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    String s = ((InputEvent) event).getValue();
                    if (!s.isEmpty()) {
                        int i = Integer.parseInt(s);
                        if (i < 0) i = 0;
                        else if (i > 100) i = 100;
                        arcsText.setValue(i);
                        arcs.setCurpos(i);
                        visualizeMap();
                    }
                }
            };
            this.arcsText.addEventListener(StringValues.b[77], arcChangeListener);
            this.arcsText.addEventListener(StringValues.b[78], arcChangingListener);

            EventListener<Event> parallelismChangeListener = new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    int i = parallelismText.getValue();
                    if (i < 0) i = 0;
                    else if (i > 100) i = 100;
                    parallelismText.setValue(i);
                    parallelism.setCurpos(i);
                    visualizeMap();
                }
            };
            EventListener<Event> parallelismChangingListener = new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    String s = ((InputEvent) event).getValue();
                    if (!s.isEmpty()) {
                        int i = Integer.parseInt(s);
                        if (i < 0) i = 0;
                        else if (i > 100) i = 100;
                        parallelismText.setValue(i);
                        parallelism.setCurpos(i);
                        visualizeMap();
                    }
                }
            };
            this.parallelismText.addEventListener(StringValues.b[77], parallelismChangeListener);
            this.parallelismText.addEventListener(StringValues.b[78], parallelismChangingListener);

            EventListener<Event> frequencyListener = new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                	retainZoomPan = true;
                    visualizeFrequency();
                }
            };
            this.frequency.addEventListener("onClick", frequencyListener);
            this.absolute_frequency.addEventListener("onClick", frequencyListener);
            this.case_frequency.addEventListener("onClick", frequencyListener);
            this.median_frequency.addEventListener("onClick", frequencyListener);
            this.mean_frequency.addEventListener("onClick", frequencyListener);
            this.mode_frequency.addEventListener("onClick", frequencyListener);
            this.max_frequency.addEventListener("onClick", frequencyListener);
            this.min_frequency.addEventListener("onClick", frequencyListener);
            
            EventListener<Event> durationListener = new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                	retainZoomPan = true;
                    visualizeDuration();
                }
            };
            this.duration.addEventListener("onClick", durationListener);
            this.total_duration.addEventListener("onClick", durationListener);
            this.median_duration.addEventListener("onClick", durationListener);
            this.mean_duration.addEventListener("onClick", durationListener);
            this.max_duration.addEventListener("onClick", durationListener);
            this.min_duration.addEventListener("onClick", durationListener);
            
            EventListener<Event> layoutListener = new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    changeLayout();
                }
            };
            
            this.layout.addEventListener("onClick", layoutListener);
            this.layout_hiera.addEventListener("onClick", layoutListener);
            //this.layout_dagre_LR.addEventListener("onClick", layoutListener);
            this.layout_dagre_TB.addEventListener("onClick", layoutListener);
            //this.layout_bf.addEventListener("onClick", layoutListener);

            this.exportFilteredLog.addEventListener("onExport", new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                	ProcessDiscovererController.this.showInputDialog(
            			"Save filtered log",
						"Enter a log name (no more than 60 characters)", 
						logSummary.getName() + "_filtered", 
						"^[a-zA-Z0-9_\\(\\)\\-\\s]{1,60}$",
						"a-z, A-Z, 0-9, hyphen, underscore, and space. No more than 60 chars.",
						new EventListener<Event>() {
            				@Override
                        	public void onEvent(Event event) throws Exception {
            					if (event.getName().equals("onOK")) {
	            					String logName = (String)event.getData();
	            					activities_value = activities.getCurpos();
	        	                    arcs_value = arcs.getCurpos();
	        	                    //XLog filtered_log = processDiscoverer.getFilteredLog();
	        	                    saveLog(filtered_log, logName);
            					}
                        	}
	                	});
                	}
            });
                	

            this.details.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    //Window details_window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), StringValues.b[17], null, null);
                	Window details_window = (Window) Executions.createComponents("/zul/details.zul", null, null);
                    details_window.setTitle("Activities");
                    Listbox listbox = (Listbox) details_window.getFellow(StringValues.b[81]);
                    
                    Listheader pos = (Listheader) details_window.getFellow(StringValues.b[82]);
                    pos.setSortAscending(new NumberComparator(true, 0));
                    pos.setSortDescending(new NumberComparator(false, 0));
                    
                    Listheader detail_frequency = (Listheader) details_window.getFellow(StringValues.b[83]);
                    detail_frequency.setSortAscending(new NumberComparator(true, 2));
                    detail_frequency.setSortDescending(new NumberComparator(false, 2));
                    
                    Listheader detail_ratio = (Listheader) details_window.getFellow(StringValues.b[84]);
                    detail_ratio.setSortAscending(new NumberComparator(true, 2));
                    detail_ratio.setSortDescending(new NumberComparator(false, 2));
                    
                    Map<String, Integer> classifierValues = local_stats.getStatistics().get(getLabel());
                    int i = 1;
                    for (String key : classifierValues.keySet()) {
                        Listcell listcell0 = new Listcell(Integer.toString(i));
                        Listcell listcell1 = new Listcell(key);
                        Listcell listcell2 = new Listcell(classifierValues.get(key).toString());
                        Listcell listcell3 = new Listcell(decimalFormat.format(100 * ((double) classifierValues.get(key) / 
                        									NumberFormat.getNumberInstance(Locale.US).parse(eventNumber.getValue()).longValue())) + "%");
                        
                        Listitem listitem = new Listitem();
                        listitem.appendChild(listcell0);
                        listitem.appendChild(listcell1);
                        listitem.appendChild(listcell2);
                        listitem.appendChild(listcell3);
                        listbox.appendChild(listitem);
                        i++;
                    }

                    //listbox.setRows(5);

                    Button save = (Button) details_window.getFellow("save");
                    save.addEventListener("onClick", new EventListener<Event>() {
                        @Override
                        public void onEvent(Event event) throws Exception {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            Writer writer = new BufferedWriter(new OutputStreamWriter(baos));
                            CSVWriter csvWriter = new CSVWriter(writer);
                            csvWriter.writeNext(new String[] {"Activity", "Frequency", "Frequency %"});
                            for (String key : classifierValues.keySet()) {
                                csvWriter.writeNext(new String[] {key, classifierValues.get(key).toString(), decimalFormat.format(100 * ((double) classifierValues.get(key) / 
                                		NumberFormat.getNumberInstance(Locale.US).parse(eventNumber.getValue()).longValue())) + "%"});
                            }
                            csvWriter.flush();
                            csvWriter.close();
                            byte[] buffer = baos.toByteArray();
                            ByteArrayInputStream is = new ByteArrayInputStream(buffer);
                            AMedia amedia = new AMedia("Details.csv", "csv", "application/file", is);
                            Filedownload.save(amedia);
                        }
                    });

                    details_window.doModal();
                }
            });

            this.cases.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    //cases_window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), StringValues.b[18], null, null);
                    cases_window = (Window) Executions.createComponents("/zul/cases.zul", null, null);
                    cases_window.setTitle("Cases");
                    
                    cases_window.addEventListener("onClose", new EventListener<Event>() {
                    	public void onEvent(Event event) throws Exception {
                    		cases_window = null;
                    		display(jsonDiagram);
                        }
                    });
                    
                    Listbox listbox = (Listbox) cases_window.getFellow("casesList");
                    Listheader pos = (Listheader) cases_window.getFellow("pos");
                    pos.setSortAscending(new NumberComparator(true, 0));
                    pos.setSortDescending(new NumberComparator(false, 0));
                    
                    Listheader case_length = (Listheader) cases_window.getFellow("case_length");
                    case_length.setSortAscending(new NumberComparator(true, 2));
                    case_length.setSortDescending(new NumberComparator(false, 2));
                    
                    Listheader variant_value = (Listheader) cases_window.getFellow("variant_value");
                    variant_value.setSortAscending(new NumberComparator(true, 3));
                    variant_value.setSortDescending(new NumberComparator(false, 3));
                    
//                    Listheader variant_freq = (Listheader) cases_window.getFellow("variant_freq");
//                    variant_freq.setSortAscending(new NumberComparator(true, 4));
//                    variant_freq.setSortDescending(new NumberComparator(false, 4));

                    List<List<String>> info = filtered_log_cases;
                    int i = 1;
                    for (List<String> caseInfo : info) {
                        Listcell listcell0 = new Listcell(i+"");
                        Listcell listcell1 = new Listcell(caseInfo.get(0));
                        Listcell listcell2 = new Listcell(caseInfo.get(1));
                        Listcell listcell3 = new Listcell(caseInfo.get(2));
                        Listcell listcell4 = new Listcell(decimalFormat.format(Double.valueOf(caseInfo.get(3))) + "%");
                        
                        Listitem listitem = new Listitem();
                        listitem.appendChild(listcell0);
                        listitem.appendChild(listcell1);
                        listitem.appendChild(listcell2);
                        listitem.appendChild(listcell3);
                        listitem.appendChild(listcell4);

                        listbox.appendChild(listitem);
                        i++;
                    }

                    //listbox.setRows(5);

                    listbox.addEventListener("onSelect", new EventListener<Event>() {
                        @Override
                        public void onEvent(Event event) throws Exception {
                            try {
                                String traceID = ((Listcell) (listbox.getSelectedItem()).getChildren().get(1)).getLabel();
                            	AbstractionParams params = new AbstractionParams(getLabel(), 
																	1 - activities.getCurposInDouble() / 100, 
																	1 - arcs.getCurposInDouble() / 100, 
																	parallelism.getCurposInDouble() / 100, 
																	false, true, 
																	inverted_nodes.isChecked(), inverted_arcs.isChecked(),
																	false,
																	fixedType, fixedAggregation, 
																	VisualizationType.DURATION, VisualizationAggregation.CASES, 
																	VisualizationType.FREQUENCY, VisualizationAggregation.CASES,
																	new HashSet<>(Arrays.asList(arcTypes)), null);                                
                                JSONArray array = ProcessDiscovererController.this.generateTraceDFGJSON(traceID, params);

                                ProcessDiscovererController.this.displayTrace(array);
                            } catch(Exception e) {
                                Messagebox.show(e.getMessage());
                            }
                        }
                    });

                    Button save = (Button) cases_window.getFellow("save");
                    save.addEventListener("onClick", new EventListener<Event>() {
                        @Override
                        public void onEvent(Event event) throws Exception {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            Writer writer = new BufferedWriter(new OutputStreamWriter(baos));
                            CSVWriter csvWriter = new CSVWriter(writer);
                            List<List<String>> info = filtered_log_cases;
                            csvWriter.writeNext(new String[] {"Case ID", "Case Length", "Unique Case", "Percentage"});
                            for (List<String> caseInfo: info) {
                                csvWriter.writeNext(new String[] {caseInfo.get(0), caseInfo.get(1), caseInfo.get(2), 
                                									decimalFormat.format(Double.valueOf(caseInfo.get(3)))+"%"});
                            }
                            csvWriter.flush();
                            csvWriter.close();
                            byte[] buffer = baos.toByteArray();
                            ByteArrayInputStream is = new ByteArrayInputStream(buffer);
                            AMedia amedia = new AMedia("Cases.csv", "csv", "application/file", is);
                            Filedownload.save(amedia);
                        }
                    });
                    cases_window.doOverlapped();
                }
            });
            


            this.fitness.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    //Window details_window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), StringValues.b[19], null, null);
                	Window details_window = (Window) Executions.createComponents("/zul/fitness.zul", null, null);
                    details_window.setTitle("Fitness");
                    Listbox listbox = (Listbox) details_window.getFellow(StringValues.b[81]);
                    Listheader detail_frequency = (Listheader) details_window.getFellow(StringValues.b[83]);
                    detail_frequency.setSortAscending(new NumberComparator(true, 1));
                    detail_frequency.setSortDescending(new NumberComparator(false, 1));
                    Listheader detail_ratio = (Listheader) details_window.getFellow(StringValues.b[84]);
                    detail_ratio.setSortAscending(new NumberComparator(true, 1));
                    detail_ratio.setSortDescending(new NumberComparator(false, 1));
                    Listcell listcell1 = new Listcell(StringValues.b[88]);

                    BPMNDiagram bpmnDiagram = gateways.isChecked() ? diagram : BPMNDiagramBuilder.insertBPMNGateways(diagram);
                    EventClassifier eventClassifier = new ActivityClassifier(processDiscoverer.getAbstractionParams().getClassifier().getAttributes());
                    double fitness = AlignmentBasedFitness.measureFitness(bpmnDiagram, filtered_log, eventClassifier);
                    
                    Listcell listcell2 = new Listcell(decimalFormat.format(fitness));
                    Listcell listcell3 = new Listcell(decimalFormat.format(fitness * 100) + "%");
                    Listitem listitem = new Listitem();
                    listitem.appendChild(listcell1);
                    listitem.appendChild(listcell2);
                    listitem.appendChild(listcell3);
                    listbox.appendChild(listitem);
                    listbox.setRows(5);
                    details_window.doModal();
                }
            });

            this.filter.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
//                	logFilterPlugin.execute(portalContext, ProcessDiscovererController.this.getInitialLog(), 
//                						label, criteria, 
//                						global_stats, 
//                						logFilterService,
//                						logFilterCriterionFactory,
//                						ProcessDiscovererController.this);
                    logFilterPlugin.execute(new LogFilterContext(portalContext), 
                            new LogFilterInputParams(ProcessDiscovererController.this.getInitialLog(), label, criteria),
                            ProcessDiscovererController.this);
                }
            });
            
            this.animate.addEventListener("onAnimate", new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                	BPMNDiagram validDiagram = diagram;
                    String layout = event.getData().toString();
                    //Insert gateways to to make it a valid BPMN diagram, not a graph
                    if(!gateways.isChecked()) {
                    	validDiagram = BPMNDiagramBuilder.insertBPMNGateways(diagram);
                    }
                    
//                    for(BPMNEdge edge : validDiagram.getEdges()) {
//                        edge.setLabel("");
//                    }
                    // The log animation needs to identify the start and end events by names
                   // BPMNDiagramBuilder.updateStartEndEventLabels(validDiagram);

                    UIContext context = new UIContext();
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                    UIPluginContext uiPluginContext = context.getMainPluginContext();
                    BpmnDefinitions.BpmnDefinitionsBuilder definitionsBuilder = new BpmnDefinitions.BpmnDefinitionsBuilder(uiPluginContext, validDiagram);
                    BpmnDefinitions definitions = new BpmnDefinitions("definitions", definitionsBuilder);

                    StringBuilder sb = new StringBuilder();
                    sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                            "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"\n " +
                            "xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\"\n " +
                            "xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\"\n " +
                            "xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\"\n " +
                            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n " +
                            "targetNamespace=\"http://www.omg.org/bpmn20\"\n " +
                            "xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd\">");

                    sb.append(definitions.exportElements());
                    sb.append("</definitions>");
                    String model = sb.toString();

                    model.replaceAll("\n", "");

                    //XLog filtered = processDiscoverer.getFilteredLog();
                    logAnimationPluginInterface.execute(portalContext, model, layout, filtered_log, gateways.isChecked());
                }
            });

            this.animate.addEventListener("onNodeRemovedTrace", new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    activities_value = activities.getCurpos();
                    arcs_value = arcs.getCurpos();
                    parallelism_value = parallelism.getCurpos();

                    Set<String> manually_removed_activities = new HashSet<>();
                    String node = event.getData().toString();
                    manually_removed_activities.add(node);
//                    for (String name : classifierValues.keySet()) {
//                        if (name.equals(node) || name.replaceAll("'", "").equals(node)) {
//                            manually_removed_activities.add(name);
//                            break;
//                        }
//                    }

                    if (manually_removed_activities.size() > 0) {
                        addCriterion(logFilterCriterionFactory.getLogFilterCriterion(
                                Action.REMOVE,
                                Containment.CONTAIN_ANY,
                                Level.TRACE,
                                getLabel(),
                                getLabel(),
                                manually_removed_activities
                        ));
                    }
                }
            });

            this.animate.addEventListener("onNodeRetainedTrace", new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    activities_value = activities.getCurpos();
                    arcs_value = arcs.getCurpos();
                    parallelism_value = parallelism.getCurpos();

                    Set<String> manually_removed_activities = new HashSet<>();
                    String node = event.getData().toString();
                    manually_removed_activities.add(node);
//                    for (String name : classifierValues.keySet()) {
//                        if (name.equals(node) || name.replaceAll("'", "").equals(node)) {
//                            manually_removed_activities.add(name);
//                            break;
//                        }
//                    }

                    if (manually_removed_activities.size() > 0) {
                        addCriterion(logFilterCriterionFactory.getLogFilterCriterion(
                                Action.RETAIN,
                                Containment.CONTAIN_ANY,
                                Level.TRACE,
                                getLabel(),
                                getLabel(),
                                manually_removed_activities
                        ));
                    }
                }
            });

            this.animate.addEventListener("onNodeRemovedEvent", new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    activities_value = activities.getCurpos();
                    arcs_value = arcs.getCurpos();
                    parallelism_value = parallelism.getCurpos();

                    Set<String> manually_removed_activities = new HashSet<>();
                    String node = event.getData().toString();
                    manually_removed_activities.add(node);
//                    for (String name : classifierValues.keySet()) {
//                        if (name.equals(node) || name.replaceAll("'", "").equals(node)) {
//                            manually_removed_activities.add(name);
//                            break;
//                        }
//                    }

                    if (manually_removed_activities.size() > 0) {
                        addCriterion(logFilterCriterionFactory.getLogFilterCriterion(
                                Action.REMOVE,
                                Containment.CONTAIN_ANY,
                                Level.EVENT,
                                getLabel(),
                                getLabel(),
                                manually_removed_activities
                        ));
                    }
                }
            });

            this.animate.addEventListener("onNodeRetainedEvent", new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    activities_value = activities.getCurpos();
                    arcs_value = arcs.getCurpos();
                    parallelism_value = parallelism.getCurpos();

                    Set<String> manually_removed_activities = new HashSet<>();
                    String node = event.getData().toString();
                    manually_removed_activities.add(node);
//                    for (String name : classifierValues.keySet()) {
//                        if (name.equals(node) || name.replaceAll("'", "").equals(node)) {
//                            manually_removed_activities.add(name);
//                            break;
//                        }
//                    }

                    if (manually_removed_activities.size() > 0) {
                        addCriterion(logFilterCriterionFactory.getLogFilterCriterion(
                                Action.RETAIN,
                                Containment.CONTAIN_ANY,
                                Level.EVENT,
                                getLabel(),
                                getLabel(),
                                manually_removed_activities
                        ));
                    }
                }
            });

            this.animate.addEventListener("onEdgeRemoved", new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    activities_value = activities.getCurpos();
                    arcs_value = arcs.getCurpos();
                    parallelism_value = parallelism.getCurpos();

                    Set<String> manually_removed_arcs = new HashSet<>();
                    String edge = event.getData().toString();
                    
                    if (isGatewayEdge(edge)) {
                    	return;
                    }
                    else if (isStartOrEndEdge(edge)) {
                    	edge = convertStartOrEndEdge(edge);
                    }

                    manually_removed_arcs.add(edge);
//                    for (String name : local_stats.getStatistics().get(LogStatistics.DIRECTLY_FOLLOW_KEY).keySet()) {
//                        if (name.equals(edge) || name.replaceAll("'", "").equals(edge)) {
//                            manually_removed_arcs.add(name);
//                            break;
//                        }
//                    }

                    if (manually_removed_arcs.size() > 0) {
                        addCriterion(logFilterCriterionFactory.getLogFilterCriterion(
                                Action.REMOVE,
                                Containment.CONTAIN_ANY,
                                Level.TRACE,
                                getLabel(),
                                StringValues.b[94],
                                manually_removed_arcs
                        ));
                    }
                }
            });

            this.animate.addEventListener("onEdgeRetained", new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    activities_value = activities.getCurpos();
                    arcs_value = arcs.getCurpos();
                    parallelism_value = parallelism.getCurpos();

                    Set<String> manually_removed_arcs = new HashSet<>();
                    String edge = event.getData().toString();

                    if (isGatewayEdge(edge)) {
                    	return;
                    }
                    else if (isStartOrEndEdge(edge)) {
                    	edge = convertStartOrEndEdge(edge);
                    }
                    
                    manually_removed_arcs.add(edge);
//                    for (String name : local_stats.getStatistics().get(LogStatistics.DIRECTLY_FOLLOW_KEY).keySet()) {
//                        if (name.equals(edge) || name.replaceAll("'", "").equals(edge)) {
//                            manually_removed_arcs.add(name);
//                            break;
//                        }
//                    }

                    if (manually_removed_arcs.size() > 0) {
                        addCriterion(logFilterCriterionFactory.getLogFilterCriterion(
                                Action.RETAIN,
                                Containment.CONTAIN_ANY,
                                Level.TRACE,
                                getLabel(),
                                StringValues.b[94],
                                manually_removed_arcs
                        ));
                    }
                }
            });
            
            this.fitScreen.addEventListener("onClick", new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                	Clients.evalJavaScript("fitToWindow(" + selectedLayout + ");");
                }
            });
            
            exportBPMN.addEventListener("onClick", new ExportBPMNHandler(portalContext, this, false, false));
            exportBPMNAnnotatedForBIMP.addEventListener("onClick", new ExportBPMNHandler(portalContext, this, true, false));

            EventListener<Event> exportPDF = new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    String name = log_name;
                    int pos = 97;
                    if (primaryType == FREQUENCY) {
                        if (primaryAggregation == TOTAL) {
                            pos += 1;
                        } else if (primaryAggregation == CASES) {
                            pos += 2;
                        } else if (primaryAggregation == MEAN) {
                            pos += 3;
                        } else if (primaryAggregation == MEDIAN) {
                            pos += 4;
                        } else if (primaryAggregation == MODE) {
                            pos += 5;
                        } else if (primaryAggregation == MAX) {
                            pos += 6;
                        } else if (primaryAggregation == MIN) {
                            pos += 7;
                        }
                    } else if (primaryType == DURATION) {
                        if (primaryAggregation == TOTAL) {
                            pos += 8;
                        } else if (primaryAggregation == MEAN) {
                            pos += 9;
                        } else if (primaryAggregation == MEDIAN) {
                            pos += 10;
                        } else if (primaryAggregation == MAX) {
                            pos += 11;
                        } else if (primaryAggregation == MIN) {
                            pos += 12;
                        }
                    } else if (primaryType == DURATION) {
                        if (primaryAggregation == TOTAL) {
                            pos += 13;
                        } else if (primaryAggregation == MEAN) {
                            pos += 14;
                        } else if (primaryAggregation == MEDIAN) {
                            pos += 15;
                        } else if (primaryAggregation == MAX) {
                            pos += 16;
                        } else if (primaryAggregation == MIN) {
                            pos += 17;
                        }
                    }
                    name += StringValues.b[pos];
                    String command = String.format(StringValues.b[115], name);
                    Clients.evalJavaScript(command);
                }
            };

            export.addEventListener("onClick", exportPDF);
            downloadPDF.addEventListener("onClick", exportPDF);
            downloadPNG.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    String name = log_name;
                    int pos = 97;
                    if (primaryType == FREQUENCY) {
                        if (primaryAggregation == TOTAL) {
                            pos += 1;
                        } else if (primaryAggregation == CASES) {
                            pos += 2;
                        } else if (primaryAggregation == MEAN) {
                            pos += 3;
                        } else if (primaryAggregation == MEDIAN) {
                            pos += 4;
                        } else if (primaryAggregation == MODE) {
                            pos += 5;
                        } else if (primaryAggregation == MAX) {
                            pos += 6;
                        } else if (primaryAggregation == MIN) {
                            pos += 7;
                        }
                    } else if (primaryType == DURATION) {
                        if (primaryAggregation == TOTAL) {
                            pos += 8;
                        } else if (primaryAggregation == MEAN) {
                            pos += 9;
                        } else if (primaryAggregation == MEDIAN) {
                            pos += 10;
                        } else if (primaryAggregation == MAX) {
                            pos += 11;
                        } else if (primaryAggregation == MIN) {
                            pos += 12;
                        }
                    } else if (primaryType == DURATION) {
                        if (primaryAggregation == TOTAL) {
                            pos += 13;
                        } else if (primaryAggregation == MEAN) {
                            pos += 14;
                        } else if (primaryAggregation == MEDIAN) {
                            pos += 15;
                        } else if (primaryAggregation == MAX) {
                            pos += 16;
                        } else if (primaryAggregation == MIN) {
                            pos += 17;
                        }
                    }
                    name += StringValues.b[pos];
                    String command = String.format(StringValues.b[116], name);
                    Clients.evalJavaScript(command);
                }
            });
            
            EventListener<Event> windowListener = new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    int i = activitiesText.getValue();
                    if (i < 0) i = 0;
                    else if (i > 100) i = 100;
                    activitiesText.setValue(i);
                    activities.setCurpos(i);
                    i = arcsText.getValue();
                    if (i < 0) i = 0;
                    else if (i > 100) i = 100;
                    arcsText.setValue(i);
                    arcs.setCurpos(i);
                    i = parallelismText.getValue();
                    if (i < 0) i = 0;
                    else if (i > 100) i = 100;
                    parallelismText.setValue(i);
                    parallelism.setCurpos(i);
                    visualizeMap();
                    filtered_log_cases = getCases(filtered_log);
                }
            };            
            
            //parallelism.setStyle("background-color:#D8D8D8; width:100%; height:25px;");
            parallelismRow.setSclass("z-row-disabled");
    		parallelismText.setDisabled(true);
            slidersWindow.addEventListener("onLoaded", windowListener);
            slidersWindow.addEventListener("onOpen", windowListener);
            //slidersWindow.doOverlapped();
            //slidersWindow.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private boolean isGatewayEdge(String edge) {
    	return (edge.startsWith(AND_FROM_PATTERN) || edge.endsWith(AND_TO_PATTERN) ||
    			edge.startsWith(OR_FROM_PATTERN) || edge.endsWith(OR_TO_PATTERN) ||
    			edge.startsWith(XOR_FROM_PATTERN) || edge.endsWith(XOR_TO_PATTERN));
    }
    
    private boolean isStartOrEndEdge(String edge) {
    	return (edge.contains(STARTEVENT_REL_PATTERN) || edge.contains(ENDEVENT_REL_PATTERN));
    }
    
    private String convertStartOrEndEdge(String edge) {
    	if (edge.contains(STARTEVENT_REL_PATTERN)) {
    		return edge.replace(STARTEVENT_REL_PATTERN, STARTEVENT_NEW_REL_PATTERN);
    	}
    	else if (edge.contains(ENDEVENT_REL_PATTERN)) {
    		return edge.replace(ENDEVENT_REL_PATTERN, ENDEVENT_NEW_REL_PATTERN);
    	}
    	else {
    		return null;
    	}
    }

    // Return case list: 
    // 1st: traceID, 2nd: trace size, 3rd: index of the first unique trace having the same sequence.
    private List<List<String>> getCases(XLog log) {
        //XLog filtered_log = processDiscoverer.getFilteredLog();
    	List<List<String>> cases = new ArrayList<>();
        ObjectIntHashMap<String> variant = new ObjectIntHashMap<>(); //key: trace string, value: index to cases
        Map<Integer, Integer> countMap = new HashMap<>(); // key: index to unique case in cases, value: number of traces in the variant
        for(XTrace trace : log) {
//            int length = 0;
            StringBuilder traceBuilder = new StringBuilder();
            for (XEvent event : trace) {
//                String label = event.getAttributes().get(getLabel()).toString();
            	String label = processDiscoverer.getAbstractionParams().getClassifier().getClassIdentity(event);
//                if(event.getAttributes().get("lifecycle:transition").toString().toLowerCase().endsWith("complete")) length++;
                traceBuilder.append(label + ",");
            }
            String sequence = traceBuilder.toString();
            Integer variantIndex;
            if(variant.containsKey(sequence)) {
            	variantIndex = variant.get(sequence);
            	countMap.put(variantIndex, countMap.get(variantIndex)+1);
            }
            else {
            	variantIndex = variant.size() + 1;
                variant.put(sequence, variantIndex);
                countMap.put(variantIndex, 1);
            }
            cases.add(Arrays.asList(XConceptExtension.instance().extractName(trace), trace.size()+"", variantIndex+"", ""));
        }
        
        //Update frequency percentage
        for (List<String> oneCase : cases) {
        	oneCase.set(3, 1.0*100*countMap.get(Integer.valueOf(oneCase.get(2)))/log.size() + "");
        }
        
        return cases;
    }
    
    /**
     * Bruce added 21.05.2019
     * Display an input dialog
     * @param title: title of the dialog
     * @param message: the message regarding the input to enter
     * @param initialValue: initial value for the input
     * @param valuePattern: the expression pattern to check validity of the input
     * @param allowedValues: message about valid values allowed  
     * @returnValueHander: callback event listener, notified with onOK (containing return value as string) and onCancel event
     */
    public void showInputDialog(String title, String message, String initialValue, 
    							String valuePattern,
    							String allowedValues, 
    							EventListener<Event> returnValueHander) {
		Window win = (Window) Executions.createComponents("/zul/inputDialog.zul", null, null);
        Window dialog = (Window) win.getFellow("inputDialog");
        dialog.setTitle(title);
        Label labelMessage = (Label)dialog.getFellow("labelMessage"); 
        Textbox txtValue = (Textbox)dialog.getFellow("txtValue");
        Label labelError = (Label)dialog.getFellow("labelError"); 
        labelMessage.setValue(message);
        txtValue.setValue(initialValue);
        labelError.setValue("");
        
        dialog.doModal();
        
        ((Button)dialog.getFellow("btnCancel")).addEventListener("onClick", new EventListener<Event>() {
        	 @Override
             public void onEvent(Event event) throws Exception {
        		 dialog.detach();
        		 returnValueHander.onEvent( new Event("onCancel"));
        	 }
         });
         
         ((Button)dialog.getFellow("btnOK")).addEventListener("onClick", new EventListener<Event>() {
        	 @Override
             public void onEvent(Event event) throws Exception {
        		 if (txtValue.getValue().trim().isEmpty()) {
        			 labelError.setValue("Please enter a value!");
        		 }
        		 else if (!Pattern.matches(valuePattern, txtValue.getValue())) {
        			 labelError.setValue("The entered value is not valid! Allowed characters: " + allowedValues);
        		 }
        		 else {
        			 dialog.detach();
        			 returnValueHander.onEvent( new Event("onOK", null, txtValue.getValue()));
        		 }
        	 }
        });
    	
    }

   
    public ProcessDiscoverer getProcessDiscoverer() {
    	return this.processDiscoverer;
    }
    
    public DomainService getDomainService() {
    	return domainService;
    }
    
    public CanoniserService getCanoniserService() {
    	return canoniserService;
    }
    
    public ProcessService getProcessService() {
    	return processService;
    }
    
    public BIMPAnnotationService getBIMPAnnotationService() {
    	return bimpAnnotationService;
    }
    
    public LogFilterService getLogFilterService() {
    	return logFilterService;
    }
    
    public LogFilterCriterionFactory getLogFilterCriterionFactory() {
    	return logFilterCriterionFactory;
    }
    
    public XLog getInitialLog() {
    	return this.initial_log;
    }
    
    public String getLogName() {
    	return this.log_name;
    }
    
    public int getContainingFolderId() {
    	return this.containingFolderId;
    }
    
    public String getContainingFolderName() {
    	return this.containingFolderName;
    }
    
    public void setFilteredLog(XLog filtered_log) {
    	this.filtered_log = filtered_log;
    }
    
    public XLog getFilteredLog() {
    	return this.filtered_log;
    }
    
    public BPMNDiagram getBPMNDiagram() {
    	if(!gateways.isChecked()) {
        	return BPMNDiagramBuilder.insertBPMNGateways(diagram);
        }
    	else {
    		return this.diagram;
    	}
    }

    /**
     * Note that the filtered log has been checked to be non-empty
     * @param reset: true if the list of criteria is emptied
     * @throws InterruptedException
     */
    public void refreshCriteria(boolean reset) {
        populateMetrics(this.filtered_log);
        generateLocalStatistics(this.filtered_log);
        this.retainZoomPan = !reset;
        
        visualizeMap();
        
        if (!reset) {
        	Clients.evalJavaScript("centerToWindow(" + selectedLayout + ");");
        }
        
        this.filtered_log_cases = this.getCases(this.filtered_log);
    }

    private void addCriterion(LogFilterCriterion logFilterCriterion) throws InterruptedException {
        if (!criteria.contains(logFilterCriterion)) {
            criteria.add(logFilterCriterion);
            XLog filteredLog = logFilterService.filter(this.getInitialLog(), criteria);
        	if (filteredLog.isEmpty()) {
        		Messagebox.show("The log is empty after applying filter criteria! Please use different criteria.");
        		criteria.remove(logFilterCriterion);
        	}
        	else {
        		setFilteredLog(filteredLog);
        		refreshCriteria(false);
        	}
        }
    }

    private void populateMetrics(XLog log) {
        Set<String> uniqueTraces = new HashSet<>();
        Map<String, Integer> labels = new HashMap<>();
        Set<String> resources = new HashSet<>();
        List<Long> durations = new ArrayList<>(log.size());
        long logStart = Long.MAX_VALUE;
        long logEnd = Long.MIN_VALUE;

        int events = 0;

        Date start = null;
        Date end = null;

        XTimeExtension xte = XTimeExtension.instance();
        XOrganizationalExtension xor = XOrganizationalExtension.instance();

        for (XTrace trace : log) {
            StringBuilder traceBuilder = new StringBuilder();
            long traceStart = xte.extractTimestamp(trace.get(0)).getTime();
            long traceEnd = xte.extractTimestamp(trace.get(trace.size() - 1)).getTime();
            durations.add(traceEnd - traceStart);
            if (traceStart < logStart) logStart = traceStart;
            if (traceEnd > logEnd) logEnd = traceEnd;
            
            for (XEvent event : trace) {
                String label = event.getAttributes().get(getLabel()).toString();
                if (!labels.containsKey(label)) labels.put(label, labels.size());
                traceBuilder.append(labels.get(label) + ",");

                resources.add(xor.extractResource(event));
                Date d = xte.extractTimestamp(event);
                if (start == null || d.before(start)) start = d;
                if (end == null || d.after(end)) end = d;
                events++;
            }
            uniqueTraces.add(traceBuilder.toString());
        }

        Long[] dur = durations.toArray(new Long[durations.size()]);
        Arrays.sort(dur);

        double shortest = Double.MAX_VALUE;
        double longhest = 0;
        double median = (dur.length > 0) ? dur[dur.length / 2] : 0;
        double mean = 0;
        for (Long l : dur) {
            mean += l;
            if (shortest > l) shortest = l;
            if (longhest < l) longhest = l;
        }
        mean = mean / dur.length;

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        caseNumber.setValue(formatter.format(log.size()));
        uniquecaseNumber.setValue(formatter.format(uniqueTraces.size()));
        activityNumber.setValue(formatter.format(labels.size()));
        eventNumber.setValue(formatter.format(events));
        meanDuration.setValue(TimeConverter.convertMilliseconds(Double.toString(mean)));
        medianDuration.setValue(TimeConverter.convertMilliseconds(Double.toString(median)));
        maxDuration.setValue(TimeConverter.convertMilliseconds(Double.toString(longhest)));
        minDuration.setValue(TimeConverter.convertMilliseconds(Double.toString(shortest)));
        logStartTime.setValue(TimeConverter.formatDate(logStart));
        logEndTime.setValue(TimeConverter.formatDate(logEnd));
    }


    /**
     * Collect frequency statistics for all event attributes except the timestamp
     * options_frequency is updated
     * Key: attribute key
     * Value: map (key: attribute value, value: frequency count of the value)
     * @param log
     */
    private void generateGlobalStatistics(XLog log) {
        global_stats = new LogStatistics(log, this.getLabel());
    }
    
    private void generateLocalStatistics(XLog log) {
    	if (this.criteria == null || this.criteria.isEmpty()) {
    		local_stats = global_stats;
    	}
    	else {
        	local_stats = new LogStatistics(log, this.getLabel());	
    	}
    }

    private void visualizeFrequency() throws InterruptedException {
        primaryType = FREQUENCY;
        if (case_frequency.isChecked()) {
            primaryAggregation = CASES;
            case_frequency.setChecked(true);
        } else if (mean_frequency.isChecked()) {
            primaryAggregation = MEAN;
            mean_frequency.setChecked(true);
        } else if (median_frequency.isChecked()) {
            primaryAggregation = MEDIAN;
            median_frequency.setChecked(true);
        } else if (mode_frequency.isChecked()) {
            primaryAggregation = MODE;
            mode_frequency.setChecked(true);
        } else if (max_frequency.isChecked()) {
            primaryAggregation = MAX;
            max_frequency.setChecked(true);
        } else if (min_frequency.isChecked()) {
            primaryAggregation = MIN;
            min_frequency.setChecked(true);
        } else {
            primaryAggregation = TOTAL;
            absolute_frequency.setChecked(true);
        }

        secondaryType = DURATION;
        if (total_duration.isChecked()) {
            secondaryAggregation = TOTAL;
        } else if (mean_duration.isChecked()) {
            secondaryAggregation = MEAN;
        } else if (max_duration.isChecked()) {
            secondaryAggregation = MAX;
        } else if (min_duration.isChecked()) {
            secondaryAggregation = MIN;
        } else {
            secondaryAggregation = MEDIAN;
        }

        visualizeMap();
    }


    private void visualizeDuration() throws InterruptedException {
        primaryType = DURATION;
        if (total_duration.isChecked()) {
            primaryAggregation = TOTAL;
            total_duration.setChecked(true);
        } else if (mean_duration.isChecked()) {
            primaryAggregation = MEAN;
            mean_duration.setChecked(true);
        } else if (max_duration.isChecked()) {
            primaryAggregation = MAX;
            max_duration.setChecked(true);
        } else if (min_duration.isChecked()) {
            primaryAggregation = MIN;
            min_duration.setChecked(true);
        } else {
            primaryAggregation = MEDIAN;
            median_duration.setChecked(true);
        }

        secondaryType = FREQUENCY;
        if (case_frequency.isChecked()) {
            secondaryAggregation = CASES;
        } else if (mean_frequency.isChecked()) {
            secondaryAggregation = MEAN;
        } else if (median_frequency.isChecked()) {
            secondaryAggregation = MEDIAN;
        } else if (mode_frequency.isChecked()) {
            secondaryAggregation = MODE;
        } else if (max_frequency.isChecked()) {
            secondaryAggregation = MAX;
        } else if (min_frequency.isChecked()) {
            secondaryAggregation = MIN;;
        } else {
            secondaryAggregation = TOTAL;
        }
        visualizeMap();
    }
    
    private void changeLayout() throws InterruptedException {
    	if (layout_hiera.isChecked()) {
    		this.selectedLayout = 0;
    	}
//    	else if (this.layout_dagre_LR.isChecked()) {
//    		this.selectedLayout = 1;
//    	}
    	else if (this.layout_dagre_TB.isChecked()) {
    		this.selectedLayout = 2;
    	}    	
//    	else if (this.layout_bf.isChecked()) {
//    		this.selectedLayout = 3;
//    	}
    	
    	this.display(jsonDiagram);
    }
    
    public void setCriteria(List<LogFilterCriterion> newCriteria) {
    	this.criteria = newCriteria;
    }

    /*
     * This is the main processing method called by most of user events
     */
    public void visualizeMap() {
        try {
        	// Always create a non-empty DFGAbstraction first
        	// This DFGAbstraction will be used if BPMNAbstraction is visualized
        	boolean found = false;
        	DFGAbstraction dfgAbstraction = null;
            while (!found) {
                activities_value = activities.getCurpos();
                arcs_value = arcs.getCurpos();
                parallelism_value = parallelism.getCurpos();
                AbstractionParams params = new AbstractionParams(getLabel(), 
    																1 - activities.getCurposInDouble() / 100, 
    																1 - arcs.getCurposInDouble() / 100, 
    																parallelism.getCurposInDouble() / 100, 
    																true, true, 
    																inverted_nodes.isChecked(), inverted_arcs.isChecked(),
    																secondary.isChecked(),
    																fixedType, fixedAggregation, 
    																primaryType, primaryAggregation, 
    																secondaryType, secondaryAggregation,
    																new HashSet<>(Arrays.asList(arcTypes)), null);
                
                dfgAbstraction = processDiscoverer.generateDFGAbstraction(this.filtered_log, params);
                if (dfgAbstraction.getDiagram().getNodes().isEmpty() || dfgAbstraction.getDiagram().getEdges().isEmpty()) {
                	if (activities_value < arcs_value) {
                		activities.setCurpos(activities_value + 1);
                		activitiesText.setValue(activities_value + 1);
                	}
                	else {
	                    arcs.setCurpos(arcs_value + 1);
	                    arcsText.setValue(arcs_value + 1);
                	}
                } else {
                	found = true;
                }
            }

            //Actual operation with the new params
            AbstractionParams params = new AbstractionParams(getLabel(), 
					1 - activities.getCurposInDouble() / 100, 
					1 - arcs.getCurposInDouble() / 100, 
					parallelism.getCurposInDouble() / 100, 
					true, true, 
					inverted_nodes.isChecked(), inverted_arcs.isChecked(),
					secondary.isChecked(),
					fixedType, fixedAggregation, 
					primaryType, primaryAggregation, 
					secondaryType, secondaryAggregation,
					new HashSet<>(Arrays.asList(arcTypes)), 
					dfgAbstraction);
            
            if(gateways.isChecked()) {
            	currentAbstraction = processDiscoverer.generateBPMNAbstraction(this.filtered_log, params, dfgAbstraction);
            }else {
            	currentAbstraction = dfgAbstraction;
            }
            
            diagram = currentAbstraction.getDiagram();
	    	jsonDiagram = this.generateJSON(currentAbstraction, params);
	    	this.display(jsonDiagram);
            
        } catch(Exception e) {
            LOGGER.error("Unable to visualize map", e);
            Messagebox.show(e.getMessage() != null && !e.getMessage().trim().isEmpty() ? e.getMessage() : "Unexpected error has occurred! Check log files.");
        }
    }
    
    private JSONArray generateJSON(AbstractAbstraction dfgAbstraction, AbstractionParams params) throws Exception {
    	JSONBuilder jsonBuilder = new JSONBuilder(dfgAbstraction);
    	return jsonBuilder.generateJSONFromBPMN(false);
    }
    
    // This method does not affect the internal status of ProcessDiscoverer object
    public JSONArray generateTraceDFGJSON(String traceID, AbstractionParams params) throws Exception {
    	TraceAbstraction traceAbs = processDiscoverer.generateTraceAbstraction(traceID, params);
    	JSONBuilder jsonBuilder = new JSONBuilder(traceAbs);
    	return jsonBuilder.generateJSONFromBPMN(false);
    }
    
    /**
     * String values go from Java -> JSON -> Javascript, thus they must conform to three Java, JSON and Javascript rules 
     * in the same order. Special characters such as ', " and \ must be escaped according to these three rules.
     * In Java and Javascript, special characters must be escaped (i.e. adding "\")
     * In JSON:
     * 		- Double quotes (") and backslashes (\) must be escaped
     * 		- Single quotes (') may not be escaped 
     * JSONArray.toString strictly conforms to JSON syntax rules, i.e. it will escape special characters.
     * For example, a special character "\" appears in a string. 
     * 		- First it must be escaped in Java strings to be valid ("\\") 
     * 		- Next, JSONArray.toString will make it valid JSON strings, so it becomes "\\\\". 
     * 		- When it is parsed to JSON object in Javascript, the parser will remove escape chars, convert it back to "\\" 
     * 		- When this string is used at client side, it is understood as one backslash character ("\") 
     * @param jsonDiagram
     */
    private void display(JSONArray jsonDiagram) {
    	String jsonString = jsonDiagram.toString();
    	jsonString = jsonString.replaceAll("'", "\\\\\'"); // to make string conform to Javascript rules
    	
//    	int retainZoomPan = 1;
//    	if ((isShowingBPMN && !gateways.isChecked()) || 
//    			(!isShowingBPMN && gateways.isChecked()) || 
//    			selectorChanged || layoutChanged || displayFirstTime) {
//    		retainZoomPan = 0;
//        }
    	int retainZoomPan = this.retainZoomPan ? 1 : 0;
    	String javascript = "load('" + jsonString + "'," +  this.selectedLayout + "," + retainZoomPan + ");";
    	Clients.evalJavaScript(javascript);
    	this.retainZoomPan = false;
    	
//        isShowingBPMN = gateways.isChecked();
//        selectorChanged = false;
//        displayFirstTime = false;
//        layoutChanged = false;
    }
    
    private void displayTrace(JSONArray jsonDiagram) {
    	String jsonString = jsonDiagram.toString();
    	jsonString = jsonString.replaceAll("'", "\\\\\'"); // to make string conform to Javascript rules
        String javascript = "loadTrace('" + jsonString + "');";
        Clients.evalJavaScript(javascript);
    }

    private void saveLog(XLog filtered_log, String logName) {
        try {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            eventLogService.exportToStream(outputStream, filtered_log);

            //int folderId = portalContext.getCurrentFolder() == null ? 0 : portalContext.getCurrentFolder().getId();

            eventLogService.importLog(portalContext.getCurrentUser().getUsername(), containingFolderId,
                    logName, new ByteArrayInputStream(outputStream.toByteArray()), "xes.gz",
                    logSummary.getDomain(), DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()).toString(),
                    false);
            
            //Messagebox.show("A new log named '" + logName + "' has been saved in '" + portalContext.getCurrentFolder().getFolderName() + "' folder.");
            Messagebox.show("A new log named '" + logName + "' has been saved in the '" + this.containingFolderName + "' folder.", "Apromore", Messagebox.OK, Messagebox.NONE);

            portalContext.refreshContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Collect all attribute keys that are common to all events 
    // in the log, excluding "time:timestamp".
    // Attribute keys that are not present in all events are not used.
    private List<String> generateLabels(XLog log) {
        Set<String> set = new HashSet<>();
        for(XTrace trace : log) {
            for(XEvent event : trace) {
                for (XAttribute attribute : event.getAttributes().values()) {
                    String key = attribute.getKey();
                    if(key.equals("time:timestamp")) continue;
                    set.add(key);
                }
            }
        }
        for(XTrace trace : log) {
            for(XEvent event : trace) {
                Set<String> set1 = new HashSet<>();
                for (XAttribute attribute : event.getAttributes().values()) {
                    String key = attribute.getKey();
                    if(key.equals("time:timestamp")) continue;
                    set1.add(key);
                }
                for(String s : (new ArrayList<>(set))) {
                    if(!set1.contains(s)) {
                        set.remove(s);
                    }
                }
            }
        }
        List<String> list = new ArrayList<>(set);
        Collections.sort(list);
        return list;
    }

    private void setLabel(String label) {
        this.label = label;
    }

    private String getLabel() {
        return label;
    }

    /**
     * Add filter to select only start and complete events in traces
     * @param criteria
     * @return new list of criteria with new criterion added
     */
    private XLog filterKeepingStartCompleteEvents(XLog log) {
    	Set<String> lifecycle = new UnifiedSet<>();
    	lifecycle.add(LogUtils.START_CODE);
    	lifecycle.add(LogUtils.START_CODE.toUpperCase());
        lifecycle.add(LogUtils.COMPLETE_CODE);
        lifecycle.add(LogUtils.COMPLETE_CODE.toUpperCase());
    	LogFilterCriterion criterion = logFilterCriterionFactory.getLogFilterCriterion(
    			Action.RETAIN,
    			Containment.CONTAIN_ANY,
                Level.EVENT,
                LogUtils.LIFECYCLE_CODE.toString(),
                LogUtils.LIFECYCLE_CODE.toString(),
                lifecycle);
    	
    	List<LogFilterCriterion> filterCriteria = new ArrayList<>();
    	filterCriteria.add(criterion);
    	return logFilterService.filter(log, filterCriteria);
    }


	@Override
	public void onPluginExecutionFinished(LogFilterOutputResult outputParams) throws Exception {
		this.setFilteredLog(outputParams.getLog());
        this.setCriteria(outputParams.getFilterCriteria());
		this.refreshCriteria(outputParams.getFilterCriteria().isEmpty());
	}
}
