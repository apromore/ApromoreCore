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

import au.com.bytecode.opencsv.CSVWriter;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.raffaeleconforti.context.FakePluginContext;
import com.raffaeleconforti.conversion.bpmn.BPMNToPetriNetConverter;

import nl.tue.astar.AStarException;
import org.apromore.model.LogSummaryType;
import org.apromore.model.SummaryType;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.loganimation.LogAnimationPluginInterface;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.BaseController;
import org.apromore.portal.dialogController.dto.SignavioSession;
import org.apromore.processdiscoverer.AbstractionParams;
import org.apromore.processdiscoverer.ProcessDiscoverer;
import org.apromore.processdiscoverer.VisualizationAggregation;
import org.apromore.processdiscoverer.VisualizationType;
import org.apromore.processdiscoverer.dfg.ArcType;
import org.apromore.processdiscoverer.dfg.vis.BPMNDiagramBuilder;
import org.apromore.processdiscoverer.logfilter.LogFilter;
import org.apromore.processdiscoverer.logfilter.LogFilterCriterionFactory;
import org.apromore.processdiscoverer.util.StringValues;
import org.apromore.processdiscoverer.util.TimeConverter;
import org.apromore.service.CanoniserService;
import org.apromore.service.DomainService;
import org.apromore.service.EventLogService;
import org.apromore.service.ProcessService;
import org.apromore.service.bimp_annotation.BIMPAnnotationService;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.*;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectIntHashMap;
import org.json.JSONArray;
import org.processmining.contexts.uitopia.UIContext;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.astar.petrinet.PetrinetReplayerWithILP;
import org.processmining.plugins.bpmn.BpmnDefinitions;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;
import javax.swing.*;
import javax.xml.datatype.DatatypeFactory;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Pattern;

import static org.apromore.processdiscoverer.VisualizationAggregation.*;
import static org.apromore.processdiscoverer.VisualizationType.DURATION;
import static org.apromore.processdiscoverer.VisualizationType.FREQUENCY;
import static org.apromore.processdiscoverer.logfilter.Action.REMOVE;
import static org.apromore.processdiscoverer.logfilter.Action.RETAIN;
import static org.apromore.processdiscoverer.logfilter.Containment.CONTAIN_ANY;
import static org.apromore.processdiscoverer.logfilter.Level.EVENT;
import static org.apromore.processdiscoverer.logfilter.Level.TRACE;

/**
 * Initialization: after the window has been loaded, the ZK client engine will send onLoaded event to the main window 
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 * Modified by Simon Rabozi for SiMo
 * Modified by Bruce Nguyen
 */
public class ProcessDiscovererController extends BaseController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessDiscovererController.class);
	
    private final DecimalFormat decimalFormat = new DecimalFormat("##############0.##");
    
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

    private Button filter;
    private Button details;
    private Button cases;
    private Button fitness;
    private Button animate;
    
    private Menuitem exportFilteredLog;
    
    private Label caseNumber;
    private Label uniquecaseNumber;
    private Label activityNumber;
    private Label eventNumber;
    private Label meanDuration;
    private Label medianDuration;
    private Label maxDuration;
    private Label minDuration;
    
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
    private XLog log;
    private Map<String, Integer[]> filtered_log_cases; // list of cases in the filtered log (log after applying filter criteria)
    
    private BPMNDiagram diagram;
    private JSONArray jsonDiagram; // the corresponding JSON format of the diagram
    private LogSummaryType logSummary;

    private List<LogFilterCriterion> criteria;
    
    //key: type of attribute (see LogFilterTypeSelector), value: map (key: attribute value, value: frequency count)
    private Map<String, Map<String, Integer>> options_frequency = new HashMap<>();
    private long min = Long.MAX_VALUE; //the earliest timestamp of the log
    private long max = 0; //the latest timestamp of the log

    private String label = "concept:name"; // the event attribute key used to label each task node, default "concept:name"
    private boolean selectorChanged = false;
    
    private boolean isShowingBPMN = false; //true if a BPMN model is being shown, not a graph
    
    private CanoniserService canoniserService;
    private DomainService domainService;
    private ProcessService processService;
    private EventLogService eventLogService;
    private ProcessDiscoverer processDiscoverer;
    private LogAnimationPluginInterface logAnimationPluginInterface;
    private BIMPAnnotationService bimpAnnotationService;
    
    private SummaryType selection = null;
    
    public ProcessDiscovererController() throws Exception {
    	super();
    }
    
    
    public void onCreate() throws InterruptedException {
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
        
        portalContext = (PortalContext)session.get("context");
        primaryType = (VisualizationType)session.get("visType");
        selection = (SummaryType)session.get("selection");
        
//        if (portalContext.getCurrentUser() == null) {
//            LOGGER.warn("Faking user session with admin(!)");
//            UserType user = new UserType();
//            user.setId("8");
//            user.setUsername("admin");
//            UserSessionManager.setCurrentUser(user);
//        }
        
        primaryAggregation = (primaryType == FREQUENCY) ? VisualizationAggregation.CASES : VisualizationAggregation.MEAN;
        logSummary = (LogSummaryType) selection;
        log_name = logSummary.getName();
        log = eventLogService.getXLog(logSummary.getId());
        if (log != null) {
        	processDiscoverer = new ProcessDiscoverer(log);
        	filtered_log_cases = this.getCases(log);
	        generateOptions(log);
	        criteria = new ArrayList<>();
	        start();
        }
        else {
        	throw new AssertionError("Cannot obtain log file for log id = " + logSummary.getId());
        }
    	
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
            this.isShowingBPMN = gateways.isChecked();
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

            this.selector = (Menupopup) slidersWindow.getFellow(StringValues.b[42]);

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

            this.details = (Button) slidersWindow.getFellow(StringValues.b[63]);
            this.cases = (Button) slidersWindow.getFellow(StringValues.b[64]);
            this.fitness = (Button) slidersWindow.getFellow(StringValues.b[65]);
            this.filter = (Button) slidersWindow.getFellow(StringValues.b[66]);
            this.animate = (Button) slidersWindow.getFellow(StringValues.b[67]);

            this.exportFilteredLog = (Menuitem) slidersWindow.getFellow(StringValues.b[69]);

            Combobutton export = (Combobutton) slidersWindow.getFellow(StringValues.b[70]);
            Menuitem downloadPDF = (Menuitem) slidersWindow.getFellow(StringValues.b[71]);
            Menuitem downloadPNG = (Menuitem) slidersWindow.getFellow(StringValues.b[72]);
            Menuitem exportBPMN = (Menuitem) slidersWindow.getFellow(StringValues.b[73]);
            Menuitem exportBPMNAnnotatedForBIMP = (Menuitem) slidersWindow.getFellow("exportBPMNAnnotatedForBIMP");

            populateMetrics(log);

            for (String option : generateLabels(log)) {
                Menuitem item = new Menuitem(option);
                item.addEventListener("onClick", new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                    	selectorChanged = true;
                        setLabel(item.getLabel());
                        options_frequency.clear();
                        generateOptions(log);
                        populateMetrics(log);
                        visualizeMap();
                    }
                });
                selector.appendChild(item);
            }

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
            this.secondary.addEventListener("onCheck", radioListener);
            this.inverted_nodes.addEventListener("onCheck", radioListener);
            this.inverted_arcs.addEventListener("onCheck", radioListener);
            
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
                    visualizeDuration();
                }
            };
            this.duration.addEventListener("onClick", durationListener);
            this.total_duration.addEventListener("onClick", durationListener);
            this.median_duration.addEventListener("onClick", durationListener);
            this.mean_duration.addEventListener("onClick", durationListener);
            this.max_duration.addEventListener("onClick", durationListener);
            this.min_duration.addEventListener("onClick", durationListener);

            this.exportFilteredLog.addEventListener("onExport", new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                	ProcessDiscovererController.this.showInputDialog(
            			"Input", 
						"Enter a log name (no more than 60 characters)", 
						logSummary.getName() + "_filtered", 
						"^[a-zA-Z0-9_\\-\\s]{1,60}$",
						"a-z, A-Z, 0-9, hyphen, underscore, and space. No more than 60 chars.",
						new EventListener<Event>() {
            				@Override
                        	public void onEvent(Event event) throws Exception {
            					if (event.getName().equals("onOK")) {
	            					String logName = (String)event.getData();
	            					activities_value = activities.getCurpos();
	        	                    arcs_value = arcs.getCurpos();
	        	                    XLog filtered_log = processDiscoverer.getFilteredLog();
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

                    int i = 1;
                    for (String key : options_frequency.get(getLabel()).keySet()) {
                        Listcell listcell0 = new Listcell(Integer.toString(i));
                        Listcell listcell1 = new Listcell(key);
                        Listcell listcell2 = new Listcell(options_frequency.get(getLabel()).get(key).toString());
                        Listcell listcell3 = new Listcell(decimalFormat.format(100 * ((double) options_frequency.get(getLabel()).get(key) / Double.parseDouble(eventNumber.getValue()))) + "%");
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
                            for (String key : options_frequency.get(getLabel()).keySet()) {
                                csvWriter.writeNext(new String[] {key, options_frequency.get(getLabel()).get(key).toString(), decimalFormat.format(100 * ((double) options_frequency.get(getLabel()).get(key) / Double.parseDouble(eventNumber.getValue()))) + "%"});
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
                    
                    Listbox listbox = (Listbox) cases_window.getFellow(StringValues.b[85]);
                    Listheader pos = (Listheader) cases_window.getFellow(StringValues.b[82]);
                    pos.setSortAscending(new NumberComparator(true, 0));
                    pos.setSortDescending(new NumberComparator(false, 0));
                    Listheader variant_value = (Listheader) cases_window.getFellow(StringValues.b[86]);
                    variant_value.setSortAscending(new NumberComparator(true, 2));
                    variant_value.setSortDescending(new NumberComparator(false, 2));
                    Listheader case_length = (Listheader) cases_window.getFellow(StringValues.b[87]);
                    case_length.setSortAscending(new NumberComparator(true, 2));
                    case_length.setSortDescending(new NumberComparator(false, 2));

                    Map<String, Integer[]> info = filtered_log_cases;
                    int i = 1;
                    for (String key : info.keySet()) {
                        Listcell listcell0 = new Listcell(Integer.toString(i));
                        Listcell listcell1 = new Listcell(key);
                        Listcell listcell2 = new Listcell(info.get(key)[1].toString());
                        Listcell listcell3 = new Listcell(info.get(key)[0].toString());
                        Listitem listitem = new Listitem();
                        listitem.appendChild(listcell0);
                        listitem.appendChild(listcell1);
                        listitem.appendChild(listcell2);
                        listitem.appendChild(listcell3);

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
																	new HashSet<>(Arrays.asList(arcTypes)));                                
                                JSONArray array = processDiscoverer.generateTraceDFGJSON(traceID, params);

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
                            Map<String, Integer[]> info = filtered_log_cases;
                            csvWriter.writeNext(new String[] {"Case ID", "Case Length", "Unique Case ID"});
                            for (String key : info.keySet()) {
                                csvWriter.writeNext(new String[] {key, info.get(key)[1].toString(), info.get(key)[0].toString()});
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
                    double fitness = measureFitness();
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
                    new FilterCriterionSelector(getLabel(), ProcessDiscovererController.this, criteria, options_frequency, min, max);
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
                    BPMNDiagramBuilder.updateStartEndEventLabels(validDiagram);

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

                    XLog filtered = processDiscoverer.getFilteredLog();
                    logAnimationPluginInterface.execute(portalContext, model, layout, filtered, gateways.isChecked());
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
                    for (String name : options_frequency.get(getLabel()).keySet()) {
                        if (name.equals(node) || name.replaceAll("'", "").equals(node)) {
                            manually_removed_activities.add(name);
                            break;
                        }
                    }

                    if (manually_removed_activities.size() > 0) {
                        addCriterion(LogFilterCriterionFactory.getLogFilterCriterion(
                                REMOVE,
                                CONTAIN_ANY,
                                TRACE,
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
                    for (String name : options_frequency.get(getLabel()).keySet()) {
                        if (name.equals(node) || name.replaceAll("'", "").equals(node)) {
                            manually_removed_activities.add(name);
                            break;
                        }
                    }

                    if (manually_removed_activities.size() > 0) {
                        addCriterion(LogFilterCriterionFactory.getLogFilterCriterion(
                                RETAIN,
                                CONTAIN_ANY,
                                TRACE,
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
                    for (String name : options_frequency.get(getLabel()).keySet()) {
                        if (name.equals(node) || name.replaceAll("'", "").equals(node)) {
                            manually_removed_activities.add(name);
                            break;
                        }
                    }

                    if (manually_removed_activities.size() > 0) {
                        addCriterion(LogFilterCriterionFactory.getLogFilterCriterion(
                                REMOVE,
                                CONTAIN_ANY,
                                EVENT,
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
                    for (String name : options_frequency.get(getLabel()).keySet()) {
                        if (name.equals(node) || name.replaceAll("'", "").equals(node)) {
                            manually_removed_activities.add(name);
                            break;
                        }
                    }

                    if (manually_removed_activities.size() > 0) {
                        addCriterion(LogFilterCriterionFactory.getLogFilterCriterion(
                                RETAIN,
                                CONTAIN_ANY,
                                EVENT,
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

                    for (String name : options_frequency.get(StringValues.b[94]).keySet()) {
                        if (name.equals(edge) || name.replaceAll("'", "").equals(edge)) {
                            manually_removed_arcs.add(name);
                            break;
                        }
                    }

                    if (manually_removed_arcs.size() > 0) {
                        addCriterion(LogFilterCriterionFactory.getLogFilterCriterion(
                                REMOVE,
                                CONTAIN_ANY,
                                TRACE,
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

                    for (String name : options_frequency.get(StringValues.b[94]).keySet()) {
                        if (name.equals(edge) || name.replaceAll("'", "").equals(edge)) {
                            manually_removed_arcs.add(name);
                            break;
                        }
                    }

                    if (manually_removed_arcs.size() > 0) {
                        addCriterion(LogFilterCriterionFactory.getLogFilterCriterion(
                                RETAIN,
                                CONTAIN_ANY,
                                TRACE,
                                getLabel(),
                                StringValues.b[94],
                                manually_removed_arcs
                        ));
                    }
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

    private Map<String, Integer[]> getCases(XLog filtered_log) {
        //XLog filtered_log = processDiscoverer.getFilteredLog();
        Map<String, Integer[]> cases = new HashMap<>(filtered_log.size());
        ObjectIntHashMap<String> variant = new ObjectIntHashMap<>();
        for(XTrace trace : filtered_log) {
            int length = 0;
            StringBuilder traceBuilder = new StringBuilder();
            for (XEvent event : trace) {
                String label = event.getAttributes().get(getLabel()).toString();
                if(event.getAttributes().get("lifecycle:transition").toString().toLowerCase().endsWith("complete")) length++;
                traceBuilder.append(label + ",");
            }
            String s = traceBuilder.toString();
            Integer i;
            if(variant.containsKey(s)) i = variant.get(s);
            else {
                i = variant.size() + 1;
                variant.put(s, i);
            }
            cases.put(XConceptExtension.instance().extractName(trace), new Integer[] {i, length});
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
    
    
    public XLog getOriginalLog() {
    	return this.log;
    }
    
    public String getLogName() {
    	return this.log_name;
    }
    
    public BPMNDiagram getBPMNDiagram() {
    	if(!gateways.isChecked()) {
        	return BPMNDiagramBuilder.insertBPMNGateways(diagram);
        }
    	else {
    		return this.diagram;
    	}
    }

    /*
     * Note that the filtered log may be empty
     * In that case the UI must be also properly empty, no errors thrown  
     */
    public void refreshCriteria(XLog filteredLog) throws InterruptedException {
    	this.filtered_log_cases = this.getCases(filteredLog);
        populateMetrics(filteredLog);
        generateOptions(filteredLog);
        visualizeMap();
    }
    
    private double measureFitness() {
        BPMNDiagram bpmnDiagram = diagram;
        if(!gateways.isChecked()) bpmnDiagram = BPMNDiagramBuilder.insertBPMNGateways(diagram);
        for (BPMNNode node : bpmnDiagram.getNodes()) {
            if(node instanceof org.processmining.models.graphbased.directed.bpmn.elements.Event) {
                org.processmining.models.graphbased.directed.bpmn.elements.Event event1 = (org.processmining.models.graphbased.directed.bpmn.elements.Event) node;
                if (event1.getEventType() == org.processmining.models.graphbased.directed.bpmn.elements.Event.EventType.START) {
                    event1.getAttributeMap().put("ProM_Vis_attr_label", "START");
                } else if (event1.getEventType() == org.processmining.models.graphbased.directed.bpmn.elements.Event.EventType.END) {
                    event1.getAttributeMap().put("ProM_Vis_attr_label", "END");
                } else {
                    event1.getAttributeMap().put("ProM_Vis_attr_label", "");
                }
            }
        }

        Object[] petrinet = BPMNToPetriNetConverter.convert(bpmnDiagram);

        PNRepResult result = computeAlignment(
                new FakePluginContext(),
                new XEventNameClassifier(),
                (Petrinet) petrinet[0],
                (Marking) petrinet[1],
                (Marking) petrinet[2],
                log);
        return getAlignmentValue(result);
    }

    private void addCriterion(LogFilterCriterion logFilterCriterion) throws InterruptedException {
        if (!criteria.contains(logFilterCriterion)) {
            criteria.add(logFilterCriterion);
            XLog filteredLog = LogFilter.filter(this.getOriginalLog(), criteria);
        	if (filteredLog.isEmpty()) {
        		Messagebox.show("The log is empty after applying filter criteria! Please use different criteria.");
        		criteria.remove(logFilterCriterion);
        	}
        	else {
        		refreshCriteria(filteredLog);
        	}
        }
    }

    private void populateMetrics(XLog log) {
        Set<String> uniqueTraces = new HashSet<>();
        Map<String, Integer> labels = new HashMap<>();
        Set<String> resources = new HashSet<>();
        List<Long> durations = new ArrayList<>(log.size());

        int events = 0;

        Date start = null;
        Date end = null;

        XTimeExtension xte = XTimeExtension.instance();
        XOrganizationalExtension xor = XOrganizationalExtension.instance();

        for (XTrace trace : log) {
            StringBuilder traceBuilder = new StringBuilder();
            durations.add(xte.extractTimestamp(trace.get(trace.size() - 1)).getTime() - xte.extractTimestamp(trace.get(0)).getTime());
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

        caseNumber.setValue(Integer.toString(log.size()));
        uniquecaseNumber.setValue(Integer.toString(uniqueTraces.size()));
        activityNumber.setValue(Integer.toString(labels.size()));
        eventNumber.setValue(Integer.toString(events));
        meanDuration.setValue(TimeConverter.convertMilliseconds(Double.toString(mean)));
        medianDuration.setValue(TimeConverter.convertMilliseconds(Double.toString(median)));
        maxDuration.setValue(TimeConverter.convertMilliseconds(Double.toString(longhest)));
        minDuration.setValue(TimeConverter.convertMilliseconds(Double.toString(shortest)));
    }

    // Collect different types of filter from the log
    // options_frequency contains these filter types
    // Key: filter type code
    // Value: map (key: filter type name, value: frequency count of the value)
    private void generateOptions(XLog log) {
        boolean firstTime = (options_frequency.keySet().size() == 0);
        Multimap<String, String> tmp_options = HashMultimap.create();
        Map<String, Map<String, Integer>> tmp_options_frequency = new HashMap<>();

        for (XTrace trace : log) {
            if (firstTime) {
                for (XEvent event : trace) {
                    for (XAttribute attribute : event.getAttributes().values()) {
                        String key = attribute.getKey();
                        if (!(key.equals("lifecycle:model") || key.equals("time:timestamp"))) {
                            tmp_options.put(key, attribute.toString());
                            if(tmp_options_frequency.get(key) == null) tmp_options_frequency.put(key, new HashMap<>());

                            Integer i = tmp_options_frequency.get(key).get(attribute.toString());
                            if (i == null) tmp_options_frequency.get(key).put(attribute.toString(), 1);
                            else tmp_options_frequency.get(key).put(attribute.toString(), i + 1);
                        }
                        if (key.equals("time:timestamp")) {
                            min = Math.min(min, ((XAttributeTimestamp) attribute).getValueMillis());
                            max = Math.max(max, ((XAttributeTimestamp) attribute).getValueMillis());
                        }
                    }
                }
            }

            for (int i = -1; i < trace.size(); i++) {
                String event1;
                if (i == -1) event1 = "|>";
                else event1 = trace.get(i).getAttributes().get(getLabel()).toString();

                for (int j = i + 1; j < trace.size() + 1; j++) {
                    String event2;
                    if (j == trace.size()) event2 = "[]";
                    else {
                        XAttribute attribute = trace.get(j).getAttributes().get(getLabel());
                        if (attribute != null) event2 = attribute.toString();
                        else event2 = "";
                    }

                    if(j == i + 1) {
                        String df = (event1 + " => " + event2);
                        tmp_options.put("direct:follow", df);
                        if (tmp_options_frequency.get("direct:follow") == null)
                            tmp_options_frequency.put("direct:follow", new HashMap<>());
                        Integer k = tmp_options_frequency.get("direct:follow").get(df);
                        if (k == null) tmp_options_frequency.get("direct:follow").put(df, 1);
                        else tmp_options_frequency.get("direct:follow").put(df, k + 1);
                    }
                    if(i != -1 && j != trace.size()) {
                        String ef = (event1 + " => " + event2);
                        tmp_options.put("eventually:follow", ef);
                        if (tmp_options_frequency.get("eventually:follow") == null)
                            tmp_options_frequency.put("eventually:follow", new HashMap<>());
                        Integer k = tmp_options_frequency.get("eventually:follow").get(ef);
                        if (k == null) tmp_options_frequency.get("eventually:follow").put(ef, 1);
                        else tmp_options_frequency.get("eventually:follow").put(ef, k + 1);
                    }
                }
            }
        }

        options_frequency.putAll(tmp_options_frequency);

        options_frequency.put("time:timestamp", new HashMap<>());
        options_frequency.put("time:duration", new HashMap<>());
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
    
    public void setCriteria(List<LogFilterCriterion> newCriteria) {
    	this.criteria = newCriteria;
    }

    /*
     * This is the main processing method called by most of user events
     */
    public void visualizeMap() {
        try {
        	//Search for the params that return a non-empty diagram
        	boolean found = false;
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
    																new HashSet<>(Arrays.asList(arcTypes)));
                
                BPMNDiagram diagram = processDiscoverer.generateDiagramFromLog(params, criteria);
                if (diagram.getNodes().isEmpty() || diagram.getEdges().isEmpty()) {
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
            if(gateways.isChecked()) {
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
																new HashSet<>(Arrays.asList(arcTypes)));
                Object[] o = processDiscoverer.generateBPMNJSON(params, criteria);
                jsonDiagram = (JSONArray) o[0];
                diagram = (BPMNDiagram) o[1];
            }else {
            	AbstractionParams params = new AbstractionParams(getLabel(), 
																1 - activities.getCurposInDouble() / 100, 
																1 - arcs.getCurposInDouble() / 100, 
																parallelism.getCurposInDouble() / 100, 
																false, true, 
																inverted_nodes.isChecked(), inverted_arcs.isChecked(),
																secondary.isChecked(),
																fixedType, fixedAggregation, 
																primaryType, primaryAggregation, 
																secondaryType, secondaryAggregation,
																new HashSet<>(Arrays.asList(arcTypes)));
                Object[] o = processDiscoverer.generateDFGJSON(params, criteria);
                jsonDiagram = (JSONArray) o[0];
                diagram = (BPMNDiagram) o[1];
            }

            this.display(jsonDiagram);
        } catch(Exception e) {
        	e.printStackTrace();
            Messagebox.show(!e.getMessage().trim().isEmpty() ? e.getMessage() : "Unexpected error has occurred! Check log files.");
        }
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
        String javascript = "load('" + jsonString + "');";
        if ((isShowingBPMN && !gateways.isChecked()) || (!isShowingBPMN && gateways.isChecked()) || selectorChanged) {
        	javascript += "fitToWindow();";
        }
        Clients.evalJavaScript(javascript);
        isShowingBPMN = gateways.isChecked();
        selectorChanged = false;
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

            int folderId = portalContext.getCurrentFolder() == null ? 0 : portalContext.getCurrentFolder().getId();

            eventLogService.importLog(portalContext.getCurrentUser().getUsername(), folderId,
                    logName, new ByteArrayInputStream(outputStream.toByteArray()), "xes.gz",
                    logSummary.getDomain(), DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()).toString(),
                    logSummary.isMakePublic());
            
            Messagebox.show("A new log named '" + logName + "' has been saved in '" + portalContext.getCurrentFolder().getFolderName() + "' folder.");

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

    private PNRepResult computeAlignment(PluginContext pluginContext, XEventClassifier xEventClassifier, Petrinet petrinet, Marking initialMarking, Marking finalMarking, XLog log) {
        pluginContext.addConnection(new InitialMarkingConnection(petrinet, initialMarking));
        pluginContext.addConnection(new FinalMarkingConnection(petrinet, finalMarking));

        PetrinetReplayerWithILP replayer = new PetrinetReplayerWithILP();

        XEventClass dummyEvClass = new XEventClass("DUMMY",99999);

        Map<Transition, Integer> transitions2costs = constructTTCMap(petrinet);
        Map<XEventClass, Integer> events2costs = constructETCMap(petrinet, xEventClassifier, log, dummyEvClass);

        CostBasedCompleteParam parameters = constructParameters(transitions2costs, events2costs, petrinet, initialMarking, finalMarking);
        TransEvClassMapping mapping = constructMapping(petrinet, xEventClassifier, log, dummyEvClass);

        try {
            return replayer.replayLog(pluginContext, petrinet, log, mapping, parameters);
        } catch (AStarException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return null;
    }


    private Map<Transition, Integer> constructTTCMap(Petrinet petrinet) {
        Map<Transition, Integer> transitions2costs = new UnifiedMap<>();

        for(Transition t : petrinet.getTransitions()) {
            if(t.isInvisible()) {
                transitions2costs.put(t, 0);
            }else {
                transitions2costs.put(t, 1);
            }
        }
        return transitions2costs;
    }

    private static Map<XEventClass, Integer> constructETCMap(Petrinet petrinet, XEventClassifier xEventClassifier, XLog log, XEventClass dummyEvClass) {
        Map<XEventClass,Integer> costMOT = new UnifiedMap<>();
        XLogInfo summary = XLogInfoFactory.createLogInfo(log, xEventClassifier);

        for (XEventClass evClass : summary.getEventClasses().getClasses()) {
            int value = 1;
            for(Transition t : petrinet.getTransitions()) {
                if(t.getLabel().equals(evClass.getId())) {
                    value = 1;
                    break;
                }
            }
            costMOT.put(evClass, value);
        }

        costMOT.put(dummyEvClass, 1);

        return costMOT;
    }

    private CostBasedCompleteParam constructParameters(Map<Transition, Integer> transitions2costs, Map<XEventClass, Integer> events2costs, Petrinet petrinet, Marking initialMarking, Marking finalMarking) {
        CostBasedCompleteParam parameters = new CostBasedCompleteParam(events2costs, transitions2costs);

        parameters.setInitialMarking(initialMarking);
        parameters.setFinalMarkings(finalMarking);
        parameters.setGUIMode(false);
        parameters.setCreateConn(false);
        ((CostBasedCompleteParam) parameters).setMaxNumOfStates(Integer.MAX_VALUE);

        return  parameters;
    }

    private static TransEvClassMapping constructMapping(Petrinet net, XEventClassifier xEventClassifier, XLog log, XEventClass dummyEvClass) {
        TransEvClassMapping mapping = new TransEvClassMapping(xEventClassifier, dummyEvClass);

        XLogInfo summary = XLogInfoFactory.createLogInfo(log, xEventClassifier);

        for (Transition t : net.getTransitions()) {
            boolean mapped = false;

            for (XEventClass evClass : summary.getEventClasses().getClasses()) {
                String id = evClass.getId();

                if (t.getLabel().equals(id)) {
                    mapping.put(t, evClass);
                    mapped = true;
                    break;
                }
            }

            if (!mapped) {
                mapping.put(t, dummyEvClass);
            }

        }

        return mapping;
    }

    private double getAlignmentValue(PNRepResult pnRepResult) {
        int unreliable = 0;
        if(pnRepResult == null) {
//            System.out.println("UNRELIABLE");
            return Double.NaN;
        }
        for(SyncReplayResult srp : pnRepResult) {
            if(!srp.isReliable()) {
                unreliable += srp.getTraceIndex().size();
            }
        }
        if(unreliable > pnRepResult.size() / 2) {
//            System.out.println("UNRELIABLE");
            return Double.NaN;
        }else {
            return (Double) pnRepResult.getInfo().get(PNRepResult.TRACEFITNESS);
        }
    }
}
