package org.apromore.processdiscoverer.dfg;

import static org.apromore.processdiscoverer.logfilter.Containment.CONTAIN_ANY;
import static org.apromore.processdiscoverer.logfilter.Level.EVENT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apromore.plugin.portal.processdiscoverer.LogFilterCriterion;
import org.apromore.processdiscoverer.AbstractionParams;
import org.apromore.processdiscoverer.VisualizationAggregation;
import org.apromore.processdiscoverer.VisualizationType;
import org.apromore.processdiscoverer.dfg.abstraction.BPMNAbstraction;
import org.apromore.processdiscoverer.dfg.abstraction.DFGAbstraction;
import org.apromore.processdiscoverer.dfg.collectors.ArcInfoCollector;
import org.apromore.processdiscoverer.dfg.collectors.NodeInfoCollector;
import org.apromore.processdiscoverer.dfg.filters.ArcSelector;
import org.apromore.processdiscoverer.dfg.filters.NodeSelector;
import org.apromore.processdiscoverer.dfg.reachability.ReachabilityChecker;
import org.apromore.processdiscoverer.dfg.vis.BPMNDiagramBuilder;
import org.apromore.processdiscoverer.logfilter.Action;
import org.apromore.processdiscoverer.logfilter.LogFilter;
import org.apromore.processdiscoverer.logfilter.LogFilterCriterionFactory;
import org.apromore.processdiscoverer.logprocessors.LogUtils;
import org.apromore.processdiscoverer.logprocessors.SimplifiedLog;
import org.apromore.processdiscoverer.logprocessors.TimeLog;
import org.apromore.processdiscoverer.splitminer.DFGPWithLogThreshold;
import org.apromore.processdiscoverer.splitminer.ProcessDiscovererDFGP;
import org.apromore.processdiscoverer.splitminer.SimpleLogAdapter;
import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.model.XLog;
import org.eclipse.collections.impl.map.mutable.primitive.IntIntHashMap;
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectIntHashMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.set.mutable.primitive.IntHashSet;
import org.eclipse.collections.api.iterator.MutableIntIterator;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.list.primitive.LongList;
import org.eclipse.collections.api.set.primitive.IntSet;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;

import com.raffaeleconforti.foreignkeydiscovery.Pair;
import com.raffaeleconforti.splitminer.log.LogParser;
import com.raffaeleconforti.splitminer.log.SimpleLog;
import com.raffaeleconforti.splitminer.splitminer.SplitMiner;
import com.raffaeleconforti.splitminer.splitminer.dfgp.DirectlyFollowGraphPlus;
import com.raffaeleconforti.splitminer.splitminer.ui.miner.SplitMinerUIResult;

/**
 * LogDFG represents the directly-follows graph created from event logs.
 * Nodes are created from distinct event classifier values
 * Arcs are created from the directly-follows relation between events
 * SimplifiedLog and TimeLog are used instead of XLog to increase the performance in statistics
 * NodeInfoCollector and ArcInfoCollector are used to store start and complete events
 * separately, not combining them (similar to any other lifecycle transition values)
 * @author Bruce Nguyen
 *
 */
public class LogDFG {
	private ArcInfoCollector arcInfoCollector;
	private NodeInfoCollector nodeInfoCollector;
	
	private DFGAbstraction dfgAbstraction;
	private BPMNAbstraction bpmnAbstraction;
	
	SimplifiedLog simplifiedLog;
	TimeLog timeLog;
    
    public LogDFG(SimplifiedLog log, TimeLog timeLog) {
    	this.simplifiedLog = log;
    	this.timeLog = timeLog;
    	this.dfgAbstraction = null;
    	this.bpmnAbstraction = null;
    	
    	this.arcInfoCollector = new ArcInfoCollector(this);
        this.nodeInfoCollector = new NodeInfoCollector(this);
        
    	for(int t = 0; t < log.size(); t++) {
            IntList trace = log.get(t);
            LongList time_trace = timeLog.get(t);
            
            nodeInfoCollector.nextTrace();
            nodeInfoCollector.updateActivityFrequency(SimplifiedLog.START_INT, 1);
        	IntIntHashMap eventsCount = new IntIntHashMap();
        	ObjectIntHashMap<Arc> arcsCount = new ObjectIntHashMap<>();
        	
    		for(int i = 0; i < trace.size(); i++) {
    			eventsCount.addToValue(trace.get(i), 1);
    			if (i < trace.size()-1) {
    				Arc arc = new Arc(trace.get(i), trace.get(i + 1));
        			arcsCount.addToValue(arc, 1);
        			long arcDuration = time_trace.get(i + 1) - time_trace.get(i);
        			arcInfoCollector.updateArcDuration(arc, arcDuration);
        	        //arcInfoCollector.updateArcImpact(arc, arcDuration);
    			}
    		}
    		
        	for(int event : eventsCount.keySet().toArray()) {
                nodeInfoCollector.updateActivityFrequency(event, eventsCount.get(event));
            }
        	nodeInfoCollector.updateActivityFrequency(SimplifiedLog.END_INT, 1);
        	
        	//Long trace_duration = time_trace.get(time_trace.size() - 1) - time_trace.get(0);
        	for(Arc arc : arcsCount.keySet().toArray(new Arc[arcsCount.size()])) {
                arcInfoCollector.updateArcFrequency(arc, arcsCount.get(arc));
                //arcInfoCollector.consolidateArcImpact(arc, trace_duration);
            }
            arcInfoCollector.nextTrace();
    	}
    }
   
    public SimplifiedLog getSimplifiedLog() {
    	return this.simplifiedLog;
    }
    
    public TimeLog getTimeLog() {
    	return this.timeLog;
    }
    
    public IntSet getNodes() {
    	return this.nodeInfoCollector.getNodes();
    }
    
    public Set<Arc> getArcs() {
    	return this.arcInfoCollector.getArcs();
    }
    
    public Arc getArc(int source, int target) {
    	for (Arc arc : this.getArcs()) {
    		if (arc.getSource() == source && arc.getTarget() == target) {
    			return arc;
    		}
    	}
    	return null;
    }
    
    public ArcType getArcType(int source, int target) {
    	if (source == SimplifiedLog.START_INT) {
    		return ArcType.START;
    	}
    	else if (target == SimplifiedLog.END_INT) {
    		return ArcType.END;
    	}
    	else if (this.simplifiedLog.isStartEvent(source)) {
    		if (this.simplifiedLog.isStartEvent(target)) {
    			return ArcType.SS;
    		}
    		else {
    			return ArcType.SC;
    		}
    	}
    	else {
    		if (this.simplifiedLog.isStartEvent(target)) {
    			return ArcType.CS;
    		}
    		else {
    			return ArcType.CC;
    		}
    	}
    }
    
//    public ArcType getArcType(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge) {
//    	int source = simplifiedLog.getEventNumber(edge.getSource().getLabel());
//    	int target = simplifiedLog.getEventNumber(edge.getTarget().getLabel());
//    	return this.getArcType(source, target);
//    }
    
    public ArcInfoCollector getArcInfoCollector() {
    	return arcInfoCollector;
    }
    
    public NodeInfoCollector getNodeInfoCollector() {
    	return nodeInfoCollector;
    }
    
    public BPMNDiagram getDFG(AbstractionParams params) {
    	NodeSelector nodeSelector = new NodeSelector(nodeInfoCollector, 
    												params.getActivityLevel(), 
    												params.getFixedType(), 
    												params.getFixedAggregation(), 
    												params.invertedNodes());
    	
    	if (params.getCorrepondingDFG() != null) return params.getCorrepondingDFG().getDiagram();
    	
    	IntHashSet retained_activities = nodeSelector.selectActivities();
    	
    	ArcSelector arcSelector = new ArcSelector(this, params);
        Set<Arc> retained_arcs = arcSelector.selectArcs();
        
        //---------------------------------------
        // Check reachability on the retained nodes and arcs.
        // At the end, the selected set of nodes and arcs ensure that
        // all nodes are reachable from the source and to the sink
        //---------------------------------------
        boolean cycle = true;
        IntHashSet candidate_nodes = retained_activities;
        Set<Arc> candidate_arcs = retained_arcs;
        IntHashSet new_candidate_nodes;
        Set<Arc> new_candidate_arcs;
        ReachabilityChecker reachabilityChecker = new ReachabilityChecker();
        while (cycle) { //keep doing until all nodes can be reachable
            cycle = false;
            new_candidate_nodes = new IntHashSet();
            // Check each node to ensure the source can reach it and 
            // it can reach the sink
            for(int i : candidate_nodes.toArray()) {
                boolean input = false;
                boolean output = false;
                for (Arc arc : candidate_arcs) {
                    if (arc.getSource() == i && arc.getTarget() != i && candidate_nodes.contains(arc.getTarget()) && reachabilityChecker.reachable(i, candidate_arcs)) output = true;
                    if (arc.getTarget() == i && arc.getSource() != i && candidate_nodes.contains(arc.getSource()) && reachabilityChecker.reaching(i, candidate_arcs)) input = true;
                }
                if ((input && output) || (i == SimplifiedLog.START_INT && output) || (i == SimplifiedLog.END_INT && input)) {
                    new_candidate_nodes.add(i);
                }
            }
            if(new_candidate_nodes.size() < candidate_nodes.size()) {
                cycle = true;
            }
            candidate_nodes = new_candidate_nodes;

            // Update the candidate arcs to contain only the connected nodes
            new_candidate_arcs = new UnifiedSet<>();
            for(Arc arc : candidate_arcs) {
                if(candidate_nodes.contains(arc.getSource()) && candidate_nodes.contains(arc.getTarget())) {
                    new_candidate_arcs.add(arc);
                }
            }
            candidate_arcs = new_candidate_arcs;
        }
        
        //---------------------------------------
        // Create raw BPMNDiagram
        //---------------------------------------
        BPMNDiagramBuilder bpmnDiagramBuilder = new BPMNDiagramBuilder(arcInfoCollector);
        IntObjectHashMap<BPMNNode> map = new IntObjectHashMap<>();

        for(int i : candidate_nodes.toArray()) {
            BPMNNode node = bpmnDiagramBuilder.addNode(this.simplifiedLog.getEventFullName(i));
            map.put(i, node);
        }

        for(Arc arc : candidate_arcs) {
            BPMNNode source = map.get(arc.getSource());
            BPMNNode target = map.get(arc.getTarget());
//            bpmnDiagramBuilder.addArc(arc, source, target, params.getPrimaryType(), params.getPrimaryAggregation(), 
//            							params.getSecondaryType(), params.getSecondaryAggregation());
            bpmnDiagramBuilder.addFlow(source, target, "");
        }
        
        //---------------------------------------
        // Create collapsed diagram from the raw one
        //---------------------------------------
        BPMNDiagram collapsedDiagram = createCollapsedDiagram(bpmnDiagramBuilder.getBpmnDiagram(), params);
        BPMNDiagramBuilder.updateStartEndEventLabels(collapsedDiagram);

        return collapsedDiagram;
    }
    
    /**
     * Convert from a diagram with node labels ending with "+start" or "+complete", e.g. "A+start", "A+complete"
     * to a diagram with node labels only containing the event without "+start" and "+complete", e.g. "A".
     * TODO: the arc selection can be refined in case the log has both start and complete events, 
     * e.g. A_start and A_complete. So the arc A-->B can be refined based on 
     * start-to-start, start-to-finish, finish-to-start, or finish-to-finish relations.
     * Another to-do thing is the matching of A_start and A_complete could be refined to detect matching pairs better.
     * At the moment, a pair is matched if they are in a directly-follows relation.
     * @param bpmnDiagram
     * @return new BPMN diagram
     */
    private BPMNDiagram createCollapsedDiagram(BPMNDiagram bpmnDiagram, AbstractionParams params) {
    	BPMNDiagramBuilder bpmnDiagramBuilder = new BPMNDiagramBuilder(arcInfoCollector);
        Map<String, BPMNNode> nodes_map = new HashMap<>();
        
        // If nodes with name A_start or A_complete, only add
        // a new node with name A
        for(BPMNNode node : bpmnDiagram.getNodes()) {
            String collapsed_name = LogUtils.getCollapsedEvent(node.getLabel());
            if(!nodes_map.containsKey(collapsed_name)) {
                BPMNNode collapsed_node = bpmnDiagramBuilder.addNode(collapsed_name);
                nodes_map.put(collapsed_name, collapsed_node);
            }
        }

        Set<Pair<String, String>> edges = new HashSet<>();
        for(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : bpmnDiagram.getEdges()) {
            String source_name = edge.getSource().getLabel();
            String target_name = edge.getTarget().getLabel();

            String collapsed_source_name = LogUtils.getCollapsedEvent(source_name);
            String collapsed_target_name = LogUtils.getCollapsedEvent(target_name);

            BPMNNode source = nodes_map.get(collapsed_source_name);
            BPMNNode target = nodes_map.get(collapsed_target_name);

            // The rule below might not make sense for different types of logs
            // The arc B-->A_start, B_start-->A.start, B_complete-->A_start or B-->A are replaced with B-->A
            // The arc A_start-->B, A_start-->B_start, A_start-->B_complete or A-->B are replaced with A-->B.
            // Similarly for A_complete.
            // The arc A_complete-->A_start or A_complete-->A_complete are replaced with a loop A-->A
            // Arcs such as A_start-->A_start and A_start-->A_complete are ignored.
//            Pair<String, String> pair = new Pair<>(collapsed_source_name, collapsed_target_name);
//            if(!collapsed_source_name.equals(collapsed_target_name) || 
//            		simplifiedLog.isSingleTypeEvent(simplifiedLog.getEventNumber(source_name)) || 
//            		LogUtils.isCompleteEvent(source_name)) {
//                if(!edges.contains(pair)) {
//                	bpmnDiagramBuilder.addFlow(source, target, edge.getLabel());
//                    edges.add(pair);
//                }
//            }
            Pair<String, String> pair = new Pair<>(collapsed_source_name, collapsed_target_name);
            if(!edges.contains(pair)) {
            	if (this.isAcceptedArc(simplifiedLog.getEventNumber(edge.getSource().getLabel()), 
            						   simplifiedLog.getEventNumber(edge.getTarget().getLabel()),
            						   params)) {
            		bpmnDiagramBuilder.addFlow(source, target, "");
            		edges.add(pair);
            	}
            }
            
        }
        return bpmnDiagramBuilder.getBpmnDiagram();
    }
    
    public boolean isOneActivity(int source, int target) {
    	String source_collapsed_name = simplifiedLog.getEventCollapsedName(source);
    	String target_collapsed_name = simplifiedLog.getEventCollapsedName(target);
    	if (source_collapsed_name.equals(target_collapsed_name) &&
    			simplifiedLog.isStartEvent(source) && simplifiedLog.isCompleteEvent(target)) {
    		return true;
    	}
    	else {
    		return false;
    	}
    }
    
    public boolean isAcceptedArc(int source, int target, AbstractionParams params) {
    	return !isOneActivity(source, target) && 
    			params.getArcTypes().contains(this.getArcType(source, target));
    }
    
    public DFGAbstraction getDFGAbstraction(AbstractionParams params) throws Exception {
    	if (dfgAbstraction != null) {
    		AbstractionParams currentParams = dfgAbstraction.getAbstractionParams();
    		// The diagram is unchanged, only need to update weights
    		if (currentParams.getAttribute() == params.getAttribute() &&
    			
    			currentParams.getActivityLevel() == params.getActivityLevel() &&
    			currentParams.getArcLevel() == params.getArcLevel() &&
    			
    			currentParams.getFixedType() == params.getFixedType() &&
    			currentParams.getFixedAggregation() == params.getFixedAggregation() &&
    			
    			currentParams.invertedNodes() == params.invertedNodes() &&
    			currentParams.invertedArcs() == params.invertedArcs() &&
    			
    			currentParams.preserveConnectivity() == params.preserveConnectivity() &&
    					
    			currentParams.getArcTypes().equals(params.getArcTypes())) {
    			
    			dfgAbstraction.updateWeights(params);
    		}
    		else {
    			this.dfgAbstraction = new DFGAbstraction(this, params);
    		}
    	}
    	else {
    		this.dfgAbstraction = new DFGAbstraction(this, params);
    	}
    	
    	return this.dfgAbstraction;
    }
    
    /**
     * Create a frequency-based DFGAbstraction based an existing DFGAbstraction
     * Frequency-based DFGAbstraction is often needed for process discovery algorithms like 
     * SplitMiner. The returning DFGAbstraction has the same DFG as the 
     * input DFGAbstraction, but weights of acrs and nodes are frequency.
     * @param dfgAbs: an existing DFGAbstraction
     * @return: a frequency-based DFGAbstraction
     * @throws Exception
     */
    public DFGAbstraction getFrequencyBasedDFGAbstraction(DFGAbstraction dfgAbs) throws Exception {
    	if (dfgAbs == null) return null;
    	
    	AbstractionParams params = dfgAbs.getAbstractionParams();
    	AbstractionParams newParams = new AbstractionParams(params.getAttribute(), params.getActivityLevel(), 
    														params.getArcLevel(), 
    														params.getParallelismLevel(), 
    														true, true, false, false, false, 
    														VisualizationType.FREQUENCY, 
    														VisualizationAggregation.TOTAL, 
    														VisualizationType.FREQUENCY, 
    														VisualizationAggregation.TOTAL, 
    														VisualizationType.FREQUENCY, 
    														VisualizationAggregation.TOTAL,
    														params.getArcTypes(), null);
    	return new DFGAbstraction(dfgAbs, newParams);
    }
    
    /**
     * Create a BPMN abstraction of this LogDFG
     * @param params
     * @param dfgAbstraction: the corresponding DFGAbstraction with the same type of nodes/arcs and weights
     * @return
     * @throws Exception
     */
    public BPMNAbstraction getBPMNAbstraction(AbstractionParams params, DFGAbstraction dfgAbstraction) throws Exception {
    	if (bpmnAbstraction != null) {
    		AbstractionParams currentParams = bpmnAbstraction.getAbstractionParams();
    		// The diagram is unchanged, only need to update weights
    		if (currentParams.getAttribute() == params.getAttribute() &&
    			
    			currentParams.getActivityLevel() == params.getActivityLevel() &&
    			currentParams.getArcLevel() == params.getArcLevel() &&
    			currentParams.getParallelismLevel() == params.getParallelismLevel() &&
    			
    			currentParams.getFixedType() == params.getFixedType() &&
    			currentParams.getFixedAggregation() == params.getFixedAggregation() &&
    			
    			currentParams.invertedNodes() == params.invertedNodes() &&
    			currentParams.invertedArcs() == params.invertedArcs() &&
    			
    	    	currentParams.prioritizeParallelism() == params.prioritizeParallelism() && 
    	    	currentParams.preserveConnectivity() == params.preserveConnectivity() &&
    	    	
    			currentParams.getArcTypes().equals(params.getArcTypes())) {
    			
    			bpmnAbstraction.updateWeights(params);
    		}
    		else {
    			this.bpmnAbstraction = new BPMNAbstraction(this, params, dfgAbstraction);
    		}
    	}
    	else {
    		this.bpmnAbstraction = new BPMNAbstraction(this, params, dfgAbstraction);
    	}
    	
    	return this.bpmnAbstraction;
    }
    
    /**
     * Mine a BPMN model from an input DFGAbstraction. 
     * This DFGAbstraction must be frequency-based because it will be used
     * by SplitMiner.  
     * @param params
     * @param dfgAbs: DFGAbstraction
     * @return
     * @throws Exception
     */
    public BPMNDiagram getBPMN(AbstractionParams params, DFGAbstraction dfgAbs) throws Exception {
    	SimpleLog simpleLog = SimpleLogAdapter.getSimpleLog(dfgAbs.getLogDFG().getSimplifiedLog());
    	DirectlyFollowGraphPlus dfgp = new ProcessDiscovererDFGP(simpleLog, dfgAbs, 1.0, params.getParallelismLevel(), params.prioritizeParallelism());
    	SplitMiner splitMiner = new SplitMiner();
    	BPMNDiagram bpmnDiagram = splitMiner.mineBPMNModel(simpleLog, dfgp, SplitMinerUIResult.StructuringTime.NONE);
        BPMNDiagramBuilder.updateStartEndEventLabels(bpmnDiagram);
        return bpmnDiagram;
    }
    
    // Note that the arc and parallelism sliders are used in the DFG input to SplitMiner
    // Not by the ArcSelector as in the case of DFG.
    /**
     * Create a BPMN representation from the log
     * The created BPMN diagram should be closely aligned with the DFG representation
     * TODO: A technical issue is that SplitMiner also needs a log in addition to 
     * a DFG to discover a BPMN diagram. Therefore, a filtered log is recreated from the 
     * original log and the retained activities. The filtered log is then used as the input
     * to SplitMiner; however, this step creates differences between the DFG and the resulting BPMN.
     * Another issue is that the log is filtered to only keep complete events. This is because
     * this log is suitable for process discovery algorithms which only work with complete events.
     * A third issue is that another DFG called DFGPWithLogThreshold is created as input to SplitMiner. 
     * That's where the arc and parallelism slider values are used for filtering. However, this is a duplication of
     * LogDFG.getDFG() method.
     * @param params
     * @return BPMN diagram
     * @throws Exception
     */
    @Deprecated
    public BPMNDiagram getBPMN(AbstractionParams params, BPMNDiagram correspondingDFG) throws Exception {
    	
    	NodeSelector nodeSelector = new NodeSelector(
    										nodeInfoCollector, 
    										params.getActivityLevel(), 
											params.getFixedType(), 
											params.getFixedAggregation(), 
											params.invertedNodes());
		IntHashSet retained_activities = nodeSelector.selectActivities(); 
		
		//Make BPMN model contain equal or less nodes than the corresponding graph.
		if (correspondingDFG != null) {
			Set<String> nodeNames = new HashSet<>();
			for (BPMNNode node : correspondingDFG.getNodes()) {
				nodeNames.add(node.getLabel());
			}
			MutableIntIterator iterator = retained_activities.intIterator(); 
			while (iterator.hasNext()) {
				if (!nodeNames.contains(simplifiedLog.getEventCollapsedName(iterator.next()))) {
					iterator.remove();
				}
			}
		}
		
        //--------------------------------------------------
        // Filter log and reconstruct XLog for process discovery 
        //--------------------------------------------------
		SimplifiedLog filtered_simplified_log = this.simplifiedLog.filterActivities(retained_activities);
        XLog filtered_xlog = filtered_simplified_log.getXLog();
        
        //--------------------------------------------------
        // Detect if the log contains more than one lifecycle:transition
        // If so, filter the log to select only complete events
        // This is necessary because process model discovery algorithms usually only
        // work on one type of events for the same activity.
        //--------------------------------------------------
//        Set<String> transitions = new UnifiedSet<>(); // size > 1 if the log contains both start and end events
//        String complete = null;
//        String start = null;
//        for (XTrace trace : filtered) {
//            for (XEvent event : trace) {
//                String lifecycle = event.getAttributes().get(LogUtils.LIFECYCLE_CODE.toString()).toString();
//                if(lifecycle.toLowerCase().equals(LogUtils.COMPLETE_CODE.toString())) complete = lifecycle;
//                if(lifecycle.toLowerCase().equals(LogUtils.START_CODE.toString())) start = lifecycle;
//                transitions.add(lifecycle);
//                if (transitions.size() > 1 && complete != null && start != null) break;
//            }
//            if (transitions.size() > 1 && complete != null && start != null) break;
//        }
//        if (transitions.size() > 1) {
        XLog complete_event_xlog = null;
        if (filtered_simplified_log.containStartEvent()) {
            Set<String> lifecycle = new UnifiedSet<>();
            lifecycle.add(LogUtils.COMPLETE_CODE);
            lifecycle.add(LogUtils.COMPLETE_CODE.toUpperCase());
            LogFilterCriterion criterion = LogFilterCriterionFactory.getLogFilterCriterion(Action.RETAIN,
                    CONTAIN_ANY,
                    EVENT,
                    LogUtils.LIFECYCLE_CODE,
                    LogUtils.LIFECYCLE_CODE,
                    lifecycle);
            List<LogFilterCriterion> criteria = new ArrayList<>(1);
            criteria.add(criterion);
            complete_event_xlog = LogFilter.filter(filtered_xlog, criteria);
        }
        else {
        	complete_event_xlog = filtered_xlog;
        }
        
        SplitMiner splitMiner = new SplitMiner();
        SimpleLog simpleLog = LogParser.getSimpleLog(complete_event_xlog, new XEventAttributeClassifier(params.getAttribute(), params.getAttribute()));
        BPMNDiagram bpmnDiagram = splitMiner.mineBPMNModel(simpleLog, 
        				new DFGPWithLogThreshold(simpleLog, 
        										params.getArcLevel(), 
        										params.getParallelismLevel(), 
        										params.prioritizeParallelism(), 
        										params.preserveConnectivity(), 
        										params.invertedArcs()), 
        										SplitMinerUIResult.StructuringTime.NONE);
        
        BPMNDiagramBuilder.updateStartEndEventLabels(bpmnDiagram);
        
        return bpmnDiagram;
    }

}
