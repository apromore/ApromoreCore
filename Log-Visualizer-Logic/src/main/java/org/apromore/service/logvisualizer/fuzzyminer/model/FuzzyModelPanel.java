package org.apromore.service.logvisualizer.fuzzyminer.model;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import org.apromore.service.logvisualizer.fuzzyminer.*;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.GraphSelectionModel;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.PluginExecutionResult;
import org.processmining.framework.plugin.PluginParameterBinding;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.events.Logger.MessageLevel;
import org.processmining.framework.util.Pair;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.fuzzymodel.metrics.MetricsRepository;
import org.processmining.models.graphbased.directed.fuzzymodel.util.FMColors;
import org.processmining.models.graphbased.directed.fuzzymodel.util.FMLogEvents;
import org.processmining.models.graphbased.directed.fuzzymodel.util.FuzzyMinerLog;
import org.processmining.plugins.log.logabstraction.LogRelations;

public class FuzzyModelPanel {
    private static final long serialVersionUID = 4221149364708440299L;
    private static final String FILE_SEPERATOR = File.separator;
    private static final String FILE_LOCATION_LOG_ATT_KEY = "fileLocation";
    private static final String PARENT_FILE_NAME_LOG_ATT_KEY = "parentFileName";

    private XLog log = null;
    private MutableFuzzyGraph graph;
    private PluginContext context;

    private ProMJGraph graphPanel;
    private ProMJGraph clustGraphPanel;
    private ProMJGraph patternGraphPanel;
    private ProMJGraph molecularGraphPanel;
    private HashSet<File> patternLogDirectorySet = new HashSet<File>();
    private boolean isPatternBasedTransformedLog = false;
    private Map<FMNode, ProMJGraph> clusterFrames;
    private Map<FMNode, ProMJGraph> molecularFrames;

    protected LogRelations molecularInnerRelations;

    public MutableFuzzyGraph getExportFuzzyGraphObjects() throws Exception {
        //add every cluster node and its corresponding graph
        MutableFuzzyGraph exportGraph = (MutableFuzzyGraph) graph.clone();

        // when getting the cluster's inner graph and the molecular activity's graph,
        // the color of the nodes in the original whole graph are changed
        for (FMClusterNode clusternode : exportGraph.getClusterNodes()) {
            MutableFuzzyGraph clusterGraph = (MutableFuzzyGraph) (clusternode.getClusterGraphPanel(log).getProMGraph());
            exportGraph.getAbstractionNodeGraphMap().put(clusternode, clusterGraph);
        }
        Vector<MutableFuzzyGraph> graphSet = new Vector<MutableFuzzyGraph>();
        graphSet.add(exportGraph);
		/*
		 * Iterator<MutableFuzzyGraph> graphIterator = graphSet.iterator();
		 * while(graphIterator.hasNext()) {
		 */
        for (int j = 0; j < graphSet.size(); j++) {
            MutableFuzzyGraph curGraph = graphSet.get(j);
            int size = curGraph.getNumberOfInitialNodes();
            for (int i = 0; i < size; i++) {
                FMNode node = curGraph.getPrimitiveNode(i);
                MutableFuzzyGraph innerGraph = null;
                innerGraph = getMolecularGraph(node);
                if (innerGraph != null) {
                    exportGraph.getAbstractionNodeGraphMap().put(node, innerGraph);
                    graphSet.add(innerGraph);
                }
            }
        }

        Set<FMNode> originalNodes = exportGraph.getNodes();
        //set the color of the nodes in the whole graph back
        for (FMNode node : originalNodes) {
            if (node instanceof FMClusterNode) {
                node.getAttributeMap().put(AttributeMap.FILLCOLOR, FMColors.getClusterBackgroundColor());
            } else if (exportGraph.getAbstractionNodeGraphMap().containsKey(node)) {
                //if the node is an abstract node
                node.getAttributeMap().put(AttributeMap.SQUAREBB, true);
                node.getAttributeMap().put(AttributeMap.FILLCOLOR, FMColors.getAbstractBackgroundColor());
            } else {
                node.getAttributeMap().put(AttributeMap.SQUAREBB, true);
                node.getAttributeMap().put(AttributeMap.FILLCOLOR, FMColors.getPrimitiveBackgroundColor());
            }
        }
        //when visulize the exportGraph, there is no unary metrics panel and no binary metrics panel
        exportGraph.setMetrics(null);
        return exportGraph;
    }

    public FuzzyModelPanel(PluginContext context, MetricsRepository metrics) {
        this(context, new MutableFuzzyGraph(metrics), metrics.getLogReader(), metrics.getNumberOfLogEvents());
    }


    public FuzzyModelPanel(PluginContext context, MutableFuzzyGraph graph, XLog log, int showNumberOfNodes) {
        this.log = log;
        this.graph = graph;
        this.context = context;
        redrawGraph(); //Set the Fuzzy Graph
        clusterFrames = new HashMap<FMNode, ProMJGraph>();
        molecularFrames = new HashMap<FMNode, ProMJGraph>();
    }

    public FuzzyGraph getGraph() {
        return graph;
    }

    public void redrawGraph() {
//		System.out.println("[FuzzyModelPanel] Graph size = " + graph.getNodes().size() + "," + graph.getClusterNodes().size() + "," + graph.getEdgeImpls().size());

        setColorOfAbstractActivityInGraph(graph);

        if (graphPanel == null) {
//			System.out.println("Creating new graph panel");
            graphPanel = ProMJGraphVisualizer.instance().visualizeGraph(graph);
//			System.out.println("Replacing graph panel");
        }
    }

    private void setColorOfAbstractActivityInGraph(MutableFuzzyGraph mGraph) {
        for (FMNode node : mGraph.getNodes()) {
            if (isMolecularNode(node)) {
//                node.getAttributeMap().put(AttributeMap.FILLCOLOR, Color.CYAN);
            }
        }
    }

    public boolean isMolecularNode(FMNode fmNode) {
        //	molecularGraphPanel = null;
        boolean haveFoundSubLog = false;
        File patternLogDirectory;
        if (isPatternBasedTransformedLog) {
            if (patternLogDirectorySet.isEmpty()) {
                setTransformedLogDirectory();
            }
            String legalPatternName = getLegalFileName(fmNode.getElementName());
            String legalPatternLogFileName = legalPatternName + ".xes.gz";
            Iterator<File> fileDirs = patternLogDirectorySet.iterator();
            while (fileDirs.hasNext() && (!haveFoundSubLog)) {
                patternLogDirectory = fileDirs.next();
                if (patternLogDirectory.exists()) {
                    File[] patternLogFiles = patternLogDirectory.listFiles();
                    for (File file : patternLogFiles) {
                        if (file.getName().equals(legalPatternLogFileName)) {
                            //transform the file into a XLog object
                            XesXmlGZIPParser parser = new XesXmlGZIPParser();
                            if (parser.canParse(file)) {

								/*
								 * Set the Fill Color of this Node to PINK
								 */
                                //fmNode.getAttributeMap().put(AttributeMap.FILLCOLOR, Color.PINK);
                                haveFoundSubLog = true;
                            }
                            break;
                        }
                    }
                }
            }
        }
        //graph.signalViews();
        return haveFoundSubLog;
    }

    /**
     * show the detail sub-process graph of the abstract/molecule activity
     *
     * @param fmNode
     * @return
     */
    public MutableFuzzyGraph getMolecularGraph(FMNode fmNode) {
        molecularGraphPanel = null;
        File patternLogDirectory;
        if (isPatternBasedTransformedLog) {
            if (patternLogDirectorySet.isEmpty()) {
                setTransformedLogDirectory();
            }
            String legalPatternName = getLegalFileName(fmNode.getElementName());
            String legalPatternLogFileName = legalPatternName + ".xes.gz";
            boolean haveFoundSubLog = false;
            Iterator<File> fileDirs = patternLogDirectorySet.iterator();
            while (fileDirs.hasNext() && (!haveFoundSubLog)) {
                patternLogDirectory = fileDirs.next();
                if (patternLogDirectory.exists()) {
                    File[] patternLogFiles = patternLogDirectory.listFiles();
                    for (File file : patternLogFiles) {
                        if (file.getName().equals(legalPatternLogFileName)) {
                            //transform the file into a XLog object
                            XesXmlGZIPParser parser = new XesXmlGZIPParser();
                            if (parser.canParse(file)) {
                                // ljf
								/*
								 * Test whether we already have a frame open for
								 * this node.
								 */
                                haveFoundSubLog = true;
                                if (!molecularFrames.containsKey(fmNode)) {
									/*
									 * No, we do not. Create a frame for this
									 * node.
									 */
                                    try {
                                        List<XLog> subLogs = parser.parse(file);
                                        XLog patternSubLog = subLogs.get(0);
                                        //create molecular graph
                                        molecularGraphPanel = getMolecularGraphPanel(context, patternSubLog);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
        //for export the inner graph of the abstract activity
        if (molecularGraphPanel != null) {
            MutableFuzzyGraph innerGraph = (MutableFuzzyGraph) (molecularGraphPanel.getProMGraph());
            return innerGraph;
        } else {
            return null;
        }
    }

    /**
     * show the detail sub-process graph of the abstract/molecule activity
     *
     * @param fmNode
     * @return
     */
    public MutableFuzzyGraph showMolecularGraph(FMNode fmNode) {
        molecularGraphPanel = null;
        File patternLogDirectory;
        if (isPatternBasedTransformedLog) {
            if (patternLogDirectorySet.isEmpty()) {
                setTransformedLogDirectory();
            }
            String legalPatternName = getLegalFileName(fmNode.getElementName());
            String legalPatternLogFileName = legalPatternName + ".xes.gz";
            boolean haveFoundSubLog = false;
            //		System.out.println("----the legal pattern log file name is : " + legalPatternLogFileName);
            Iterator<File> fileDirs = patternLogDirectorySet.iterator();
            while (fileDirs.hasNext() && (!haveFoundSubLog)) {
                patternLogDirectory = fileDirs.next();
                if (patternLogDirectory.exists()) {
                    File[] patternLogFiles = patternLogDirectory.listFiles();
                    for (File file : patternLogFiles) {
                        if (file.getName().equals(legalPatternLogFileName)) {
                            //transform the file into a XLog object
                            XesXmlGZIPParser parser = new XesXmlGZIPParser();
                            if (parser.canParse(file)) {
                                haveFoundSubLog = true;
                                // ljf
								/*
								 * Test whether we already have a frame open for
								 * this node.
								 */
                                if (!molecularFrames.containsKey(fmNode)) {
									/*
									 * No, we do not. Create a frame for this
									 * node.
									 */
                                    try {
                                        List<XLog> subLogs = parser.parse(file);
                                        XLog patternSubLog = subLogs.get(0);
                                        //create molecular graph
                                        molecularGraphPanel = getMolecularGraphPanel(context, patternSubLog);
                                        //set the color of the abstract activities in the molecular Graph
                                        setColorOfAbstractActivityInGraph((MutableFuzzyGraph) molecularGraphPanel
                                                .getProMGraph());

                                        molecularFrames.put(fmNode, molecularGraphPanel);
                                        installMolecularNodeGraphPanelListener();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
								/*
								 * Now there should be a frame for this node.
								 * Get it and make it visible.
								 */
                            }
                            break;
                        }
                    }
                }
            }
        }
        //for export the inner graph of the abstract activity
        if (molecularGraphPanel != null) {
            MutableFuzzyGraph innerGraph = (MutableFuzzyGraph) (molecularGraphPanel.getProMGraph());
            return innerGraph;
        } else {
            return null;
        }
    }

    /*
     * After pre-processing the log with patterns,there will be many molecular
     * node in the new log. The molecular node contains many atomic nodes, this
     * method is to get the inner graph inside the molecular node.
     */
    public ProMJGraph getMolecularGraphPanel(PluginContext context, XLog log) {
        ProMJGraph graphPanel = null;
        Set<FMNode> startNodes = new HashSet<FMNode>();
        Set<FMNode> endNodes = new HashSet<FMNode>();
        MutableFuzzyGraph molecularInnerGraph = new MutableFuzzyGraph(log);
        //molecularInnerGraph.getAttributeMap().put(AttributeMap.PREF_ORIENTATION, SwingConstants.NORTH);
        FMLogEvents logEvents = FuzzyMinerLog.getLogEvents(log);
        Set<XEvent> startEvents = FuzzyMinerLog.getStartEvents(log);
        Set<XEvent> endEvents = FuzzyMinerLog.getEndEvents(log);
        int eventsCount = logEvents.getEventsCount();
        Map<String, FMNode> eventClassIDFMNodeMap = new HashMap<String, FMNode>();
        //set the significance of the inner node in the molecular graph to be the significance of the molecular node
        //	Double nodeSignificance = node.getSignificance();
        Double nodeSignificance = 1.0;
        try {
            //create nodes
            for (int i = 0; i < eventsCount; i++) {
                XEvent evt = logEvents.get(i);
                String evtName = FuzzyMinerLog.getEventName(evt);
                String evtType = FuzzyMinerLog.getEventType(evt);
                String evtLabel = evtName + " " + evtType + " " + MutableFuzzyGraph.format(nodeSignificance);
                FMNode evtNode = new FMNode(molecularInnerGraph, i, evtLabel);
                if (FuzzyMinerLog.isEventAdded(evt, startEvents)) {
                    startNodes.add(evtNode);
                }
                if (FuzzyMinerLog.isEventAdded(evt, endEvents)) {
                    endNodes.add(evtNode);
                }
                molecularInnerGraph.addNode(evtNode, i);
                String eventClassID = evtName + "+" + evtType;
                eventClassIDFMNodeMap.put(eventClassID, evtNode);
            }
            //create arcs
            molecularInnerRelations = getLogRelations(context, log);

			/*
			 * Map<Pair<XEventClass,XEventClass>,Double> causalRelations =
			 * molecularInnerRelations.getCausalDependencies();
			 * for(Pair<XEventClass,XEventClass> evtPair:
			 * causalRelations.keySet()){ Double relationDegree =
			 * causalRelations.get(evtPair);
			 */
            Map<Pair<XEventClass, XEventClass>, Integer> directFollowRelations = molecularInnerRelations
                    .getDirectFollowsDependencies();
            for (Pair<XEventClass, XEventClass> evtPair : directFollowRelations.keySet()) {
                Integer relationDegree = directFollowRelations.get(evtPair);
                if (relationDegree > 0) {
                    XEventClass first = evtPair.getFirst();
                    XEventClass second = evtPair.getSecond();

                    String firstEvtId = first.getId();
                    String secondEvtId = second.getId();
                    if (eventClassIDFMNodeMap.containsKey(firstEvtId) && eventClassIDFMNodeMap.containsKey(firstEvtId)) {
                        FMNode firstNode, secondNode;
                        firstNode = eventClassIDFMNodeMap.get(firstEvtId);
                        secondNode = eventClassIDFMNodeMap.get(secondEvtId);
                        molecularInnerGraph.addEdge(firstNode, secondNode, 0.5, 0.5);
                    }
                }
            }
            //LJF add start
            //add start nodes and end nodes of the inner graph of this molecule node
            molecularInnerGraph.setStartNodes(startNodes);
            molecularInnerGraph.setEndNodes(endNodes);
            //LJF add end
            graphPanel = ProMJGraphVisualizer.instance().visualizeGraph(molecularInnerGraph);
            return graphPanel;

        } catch (CancellationException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return graphPanel;
    }

    /*
     * Install the listener to the cluster panel, When the molecular node is
     * clicked, the inner structure graph will be shown. Otherwise, the message
     * box is popped up.
     */
    public void installMolecularNodeGraphPanelListener() {
        GraphSelectionModel model = molecularGraphPanel.getSelectionModel();
        model.setSelectionMode(GraphSelectionModel.SINGLE_GRAPH_SELECTION);
        model.addGraphSelectionListener(new GraphSelectionListener() {
            public void valueChanged(GraphSelectionEvent evt) {
                for (Object cell : evt.getCells()) {
                    if (evt.isAddedCell(cell)) {
                        if (cell instanceof ProMGraphCell) {
                            ProMGraphCell node = (ProMGraphCell) cell;
                            FMNode fmNode = (FMNode) (node.getNode());
                            if ((!(fmNode instanceof FMClusterNode))) {
                                showMolecularGraph(fmNode);
                            }
                        }
                    }
                }
            }
        });
    }

    /*
     * If this log has been transformed , the method will set the directory of
     * the transformed log
     */
    private void setTransformedLogDirectory() {
        //get the directory that saved the pattern sub logs
        File patternLogDirectory;
        String logName = ((XAttributeLiteral) log.getAttributes().get("concept:name")).getValue();
        String logLocation = ((XAttributeLiteral) log.getAttributes().get(FILE_LOCATION_LOG_ATT_KEY)).getValue();
        //	System.out.println("$$$$$$$$$$ The location of the log is : " + logLocation +"$$$$$$$$$$$");
        //get the directory of the sublog in this level
        int delimit = logName.indexOf(".");
        logName = logName.substring(0, delimit);
        String patternLogDir = logLocation + logName + FILE_SEPERATOR;
        patternLogDirectory = new File(patternLogDir);
        patternLogDirectorySet.add(patternLogDirectory);
        //If there are several phase of transformation iterations, track back to find all the directories of sub logs in each level
        XAttributeLiteral parentFileAbsolutePath = ((XAttributeLiteral) log.getAttributes().get(
                PARENT_FILE_NAME_LOG_ATT_KEY));
        String prtFlAbsPathStr = parentFileAbsolutePath.getValue();
        if (prtFlAbsPathStr.contains(FILE_SEPERATOR)) {
            delimit = prtFlAbsPathStr.indexOf(".");
            String parentLogFileDir = prtFlAbsPathStr.substring(0, delimit) + FILE_SEPERATOR;
            patternLogDirectorySet.add(new File(parentLogFileDir));
        }
        XAttributeMap subAttrs = parentFileAbsolutePath.getAttributes();
        Iterator<XAttribute> ancesetorFiles = subAttrs.values().iterator();
        while (ancesetorFiles.hasNext()) {
            String ancesterLogFile = ((XAttributeLiteral) ancesetorFiles.next()).getValue();
            if (ancesterLogFile.contains(FILE_SEPERATOR)) {
                delimit = ancesterLogFile.indexOf(".");
                String ancestorLogFileDir = ancesterLogFile.substring(0, delimit) + FILE_SEPERATOR;
                patternLogDirectorySet.add(new File(ancestorLogFileDir));
            }
        }
    }

    private String getLegalFileName(String illegalFileName) {
        String[] illegalCharSetsInFileName = { "\\", "/", ":", "?", "\"", "<", ">", "|" };
        int numOfIllegalChars = illegalCharSetsInFileName.length;

        //	System.out.println("!!!The input file name is " + illegalFileName);
        for (int i = 0; i < numOfIllegalChars; i++) {

            illegalFileName = illegalFileName.replace(illegalCharSetsInFileName[i], " ");
        }

        illegalFileName = illegalFileName.replaceAll("-complete", "");
        String leagalFileName = illegalFileName;
        //			System.out.println("!!!!The out  file name is " + illegalFileName);
        return leagalFileName;
    }

    /*
     * get the causal relations in the specified log
     */
    public LogRelations getLogRelations(PluginContext context, XLog log) throws CancellationException,
            InterruptedException, ExecutionException {
        XLogInfo summary = XLogInfoFactory.createLogInfo(log);
        Collection<Pair<Integer, PluginParameterBinding>> plugins = context.getPluginManager().find(Plugin.class,
                LogRelations.class, context.getPluginContextType(), true, false, false, XLog.class, summary.getClass());

        if (plugins.isEmpty()) {
            context.log("No plugin found to create log relations, please specify relations manually",
                    MessageLevel.ERROR);
            return null;
        }
        // Let's just take the first available plugin for the job of constructing log abstractions
        Pair<Integer, PluginParameterBinding> plugin = plugins.iterator().next();

        // Now, the binding can be executed on the log and the summary
        // FIrst, we instantiate a new context for this plugin, which is a child context of the current context.
        PluginContext c2 = context.createChildContext("Log Relation Constructor");

        // Let's notify our lifecyclelisteners about the fact that we created a new context. this is
        // optional, but if this is not done, then the user interface doesn't show it (if there is a UI).
        context.getPluginLifeCycleEventListeners().firePluginCreated(c2);

        // At this point, we execute the binding to get the LogRelations. For this, we call the invoke method
        // on the PluginParameterBinding stored in the plugin variable. The return type is LogRelations.class and
        // as input we give the new context c2, the log and the summary. Note that the plugin might return mulitple
        // objects, hence we extract the object with number x, where x is stored as the first element of the plugin
        // variable.

        PluginExecutionResult pluginResult = plugin.getSecond().invoke(c2, log, summary);
        pluginResult.synchronize();
        LogRelations relations = pluginResult.<LogRelations>getResult(plugin.getFirst());
        return relations;
    }
}
