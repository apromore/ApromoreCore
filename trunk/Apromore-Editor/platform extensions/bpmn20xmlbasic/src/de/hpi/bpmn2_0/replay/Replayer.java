/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hpi.bpmn2_0.replay;

import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.activity.Activity;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import de.hpi.bpmn2_0.model.event.EndEvent;
import de.hpi.bpmn2_0.backtracking2.Backtracking;
import de.hpi.bpmn2_0.backtracking2.Node;
import de.hpi.bpmn2_0.backtracking2.State;
import de.hpi.bpmn2_0.backtracking2.StateElementStatus;
import de.vogella.algorithms.dijkstra.model.Vertex;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections4.iterators.PermutationIterator;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeImpl;
import org.deckfour.xes.model.impl.XEventImpl;
import org.deckfour.xes.model.impl.XTraceImpl;
import org.joda.time.DateTime;
import org.processmining.plugins.signaturediscovery.encoding.EncodeTraces;
import org.processmining.plugins.signaturediscovery.encoding.EncodingNotFoundException;
import servlet.BPMNAnimationServlet;

/**
 *
 * @author Administrator
 */
public class Replayer {
    private Definitions bpmnDefinition = null;
    private ReplayParams params;
    private float fitness;
    private double logStartTime;
    private double logEndTime;
    private BPMNDiagramHelper helper;
    private boolean isValidProcess = true;
    private String processCheckMessage = "";
    private String[] colors = {"blue","orange","red","green","margenta"};
    private int curColorIndex = -1; //index of the current color used
    
    //Contains number of token increased/decreased carried on every edge
    //Used for OR-join enabledness check
    private Map<SequenceFlow,Integer> edgeToTokenChangeMap = new HashMap();     
    
    //EncodeTraces encodedTraces; // mapping from traceId to trace encoded char string
    Map<String, Node> traceBacktrackingNodeMap = new HashMap(); // mapping from trace string to leaf node of backtracking
    
    private static final Logger LOGGER = Logger.getLogger(Replayer.class.getCanonicalName());
    
    public Replayer(Definitions bpmnDefinition, ReplayParams params) {
        this.bpmnDefinition = bpmnDefinition;
        this.params = params;
        this.isValidProcess = true;
        try {
            helper = new BPMNDiagramHelper();
            helper.checkModel(bpmnDefinition);
            ORJoinEnactmentManager.init(helper);
        } catch (Exception ex) {
            isValidProcess = false;
            processCheckMessage = ex.getMessage();
            Logger.getLogger(Replayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean isValidProcess() {
        return isValidProcess;
    }
    
    public String getProcessCheckingMsg() {
        return processCheckMessage;
    }
    
    public AnimationLog replay(XLog log) {
        AnimationLog animationLog = new AnimationLog();
        animationLog.setColor(this.getLogColor());
        ReplayTrace replayTrace;
        
        //--------------------------------------------
        // traceEncode contains mapping from traceId to char string representation
        //--------------------------------------------
        /*
        try {
            encodedTraces = new EncodeTraces(log);
        } catch (EncodingNotFoundException ex) {
            Logger.getLogger(Replayer.class.getName()).log(Level.SEVERE, null, ex);
            LOGGER.severe(ex.getMessage());
            return animationLog;
        }
        */
        
        //-------------------------------------------
        // Set the initial and very far log start/end date as marker only
        //-------------------------------------------
        Calendar cal = Calendar.getInstance();
        cal.set(2020, 1, 1);
        DateTime logStartDate = new DateTime(cal.getTime());
        cal.set(1920, 1, 1);
        DateTime logEndDate = new DateTime(cal.getTime());
        
        //-------------------------------------------
        // Replay every trace in the log
        //-------------------------------------------
        for (XTrace trace : log) {
            replayTrace = this.replayWithBacktracking(trace);
            if (!replayTrace.isEmpty()) {
                replayTrace.calcTiming();
                if (logStartDate.isAfter(replayTrace.getStartDate())) {
                    logStartDate = replayTrace.getStartDate();
                }
                if (logEndDate.isBefore(replayTrace.getEndDate())) {
                    logEndDate = replayTrace.getEndDate();
                }
                animationLog.getTraceMap().put(trace, replayTrace);
            }
        }
        
        if (!animationLog.isEmpty()) {
            animationLog.setStartDate(new DateTime(logStartDate));
            animationLog.setEndDate(new DateTime(logEndDate));
            animationLog.setName(log.getAttributes().get("concept:name").toString());
        }
        
        return animationLog;
    }
    
    /*
    * Preprocess the trace, including
    * - remove all events with name not found in the process model
    * - add start and end event since trace only contains activities
    * - calculate timing for start and end event from the first and last trace activity
    */
    private XTrace preProcess(XTrace trace) {
        XTrace processTrace = new XTraceImpl(trace.getAttributes());
        
        //Remove unfound event name in process model
        for (XEvent event : trace) {
            if (helper.getActivityNames().contains(event.getAttributes().get("concept:name").toString())) {
                processTrace.insertOrdered(event);
            }
        }
        
        //Create start event
        //Timing 4 seconds prior to the first activity of trace
        //Name is taken from the start event of process mdoel
        XEvent event = (XEvent)trace.get(0).clone();        
        DateTime timestamp = new DateTime(LogUtility.getTimestamp(processTrace.get(0))); 
        LogUtility.setConceptName(event, helper.getStartEvent().getName());
        LogUtility.setTimestamp(event, timestamp.minusSeconds(4).toDate());
        processTrace.insertOrdered(event);
        
        //Create end event
        //Timing 60 seconds after to the last activity of trace
        //Name is taken from the end event of process mdoel
        event = (XEvent)trace.get(0).clone();        
        timestamp = new DateTime(LogUtility.getTimestamp(processTrace.get(processTrace.size()-1))); 
        LogUtility.setConceptName(event, helper.getEndEvent().getName());
        LogUtility.setTimestamp(event, timestamp.plusSeconds(60).toDate());        
        processTrace.insertOrdered(event);
        
        return processTrace;
    }
    
    
    /*
     * Replay a trace using replay algorithm from http://dl.acm.org/citation.cfm?id=2555061
     */
    public ReplayTrace replayNaive(XTrace oriTrace) {
        int unableReplayCount = 0;
        
        this.edgeToTokenChangeMap.clear(); //prepare for a new replay
        for (SequenceFlow flow : helper.getAllSequenceFlows()) {
            edgeToTokenChangeMap.put(flow, 0);
        }
        
        XTrace2 trace = new XTrace2((preProcess(oriTrace)));
        
        //Marking of the process: represent the state of the process
        //The main algorithm idea is the process state at a point in time is represented
        //by set of active elements (activity or gateway) (i.e. each has a token).
        //At every marking (state), the algorithm selects a next element which could reach 
        //the next activity of the trace (based on a next function). 
        //This element is one of elements belong to the current marking.
        //Once an element is selected, the algorithm fires that element means taking token out of that
        //element and put one token in each of its target elements. If the element also matches the
        //current activity of the trace, that means one more activity would be replayed successfully at firing. 
        //If the selected element is not an activity, the firing would only move the process
        //to the next marking (state) closer to the activity to be matched, and so on.
        
        //Element: is a node active (with token)
        Collection<FlowNode> markings = helper.getSet(helper.getStartEvent());
        
        
        //Contains the trace status when a node is matched against a trace.
        //This is to deal with loop in the process which may cause unnecessary visits 
        //(or infinite loop) since the loop sequence can select the visited node again into 
        //next list. So the next function would check this variable against the current trace status
        //It would only select the node if the current trace status is different from the saved trace status
        //key: node that has been visited to check against the trace
        //value: the trace 
        Map<FlowNode,XTrace2> lastVisitedStates = new HashMap();

        
        //Nexts contains all targets of the current element
        Collection<FlowNode> nexts = new HashSet();
        
        //Contains set of active source nodes (with a token) of a join
        //key: the join node 
        //value: set of source nodes of the join node (key)
        //The meaning of sync[node]: is used to check if all source nodes have 
        //tokens or not. If so, the join node (key) will be fireable and thus can be
        //added into the current marking of the process
        Map<FlowNode, Collection<FlowNode>> sync = new HashMap();
        //Map<FlowNode, Set<FlowNode>> syncOR = new HashMap();

        //restSize = -1: perfect replay, end of trace and end of model at the same time
        //restSize = 0: complete replay, end of trace but not reach end event of model
        //restSize > 0: incomplete replay, end of model reached but not end of trace, 
        //restSize is the number of activities unreplayed in the trace
        int restSize = trace.size();     
        
        Collection<FlowNode> bigSet = helper.getSet(helper.getStartEvent());
        bigSet.addAll(helper.getAllMerges());
        bigSet.addAll(helper.getAllForks());
        bigSet.addAll(helper.getAllJoins());
        bigSet.addAll(helper.getAllORSplits());
        bigSet.addAll(helper.getAllORJoins());
        bigSet.addAll(helper.getActivities());
        
        FlowNode nextToCurrent = null;
        FlowNode previous = null; //previous node before the current node
        
        ORJoinEnactmentManager.init(helper);
        
        //Contain the bpmn trace as result of a log trace replay
        //Note that bpmnTrace tracks a current node (TraceNode) like the cur variable for FlowNode
        //The distinguished feature of bpmnTrace is it is a flatten form of the BPMN model (FlowNodes).
        //Loops in BPMN model will be represented with the repeated path added to the current node
        //However, in case of fork, the structure, the fork branches must remain the same as the model, i.e. 
        //they are connected to the fork node in parallel. If there is a loop of fork, then it is replicated
        //and added to bpmnTrace at the node it starts repeating.
        ReplayTrace bpmnTrace = new ReplayTrace(new TraceNode(helper.getStartEvent()),trace);
        bpmnTrace.getMetrics().setMissingTokenCount(oriTrace.size() - trace.size() + 2); //for removed events
        unableReplayCount += (oriTrace.size() - trace.size() + 2);
        
        FlowNode cur = helper.getStartEvent(); //start from start event
        
        
        //Traverse both the log trace and the model at the same time
        //Log trace is traversed sequentially one event after another
        //The next node to traverse on the model is selected from the model markings
        //which are able to reach the next event in the trace
        //At every node on the model, select and add target nodes into the markings (with conditions)
        //The nodes in ReplayTrace object grow with the model markings. 
        //At the end of the process, the ReplayTrace must run pruning process to remove
        //those unnecessary nodes and branches
        //ORJoinEnactmentManager keeps the updated state of all OR-Join gateways in the model
        while (cur != null) {
            markings.remove(cur);
            lastVisitedStates.put(cur, trace);
            nexts.clear();
            bpmnTrace.setReplayed(cur);

            //----------------------------------------------
            // FIRE the current node, produce and put tokens in target nodes             
            // Different tokens produced for Decision (XOR-split), OR-split and other nodes
            // Below is for other nodes (NOT XOR-Split, OR-Split)
            //----------------------------------------------
            if (bigSet.contains(cur)) { 
                nexts.addAll(helper.getTargets(cur)); // FIRE!!! meaning put next nodes into nexts set
                this.updateTokenChangeForEdges(cur, previous, helper.getTargets(cur));
                bpmnTrace.getMetrics().setProducedTokenCount(bpmnTrace.getMetrics().getProducedTokenCount() + helper.getTargets(cur).size());
                
                if (helper.getAllJoins().contains(cur)) { // reset in case there is repetition
                    sync.put(cur, new HashSet());
                }
                else if (helper.getActivities().contains(cur) || cur == helper.getStartEvent() || cur == helper.getEndEvent()) { 
                    if (trace.getFirstActName().equals(cur.getName())) { //match activity with the event
                        bpmnTrace.setMatchedActivity(cur);
                        bpmnTrace.setNodeTime(cur, LogUtility.getTimestamp(trace.get(0)));

                        if (trace.size()>=1) {
                            trace = trace.dropFirst();
                            restSize = trace.size();
                        }
                        else {
                            break; // Complete the trace
                        }
                    } else {
                        bpmnTrace.setVirtual(cur); //mark Virtual for animation
                    }
                }
            }
            //-----------------------------------------------
            // In case of XOR-Split, select a branch node with shortest path to the next event
            //-----------------------------------------------
            else if (helper.getAllDecisions().contains(cur)) { 
                FlowNode branchNode = this.nextShortest(helper.getTargets(cur), trace);
                if (branchNode != null) { // stop if cannot find
                    nexts = helper.getSet(branchNode);
                    this.updateTokenChangeForEdges(cur, previous, helper.getSet(branchNode));
                    bpmnTrace.getMetrics().setProducedTokenCount(bpmnTrace.getMetrics().getProducedTokenCount() + 1);
                }
            }
            //-----------------------------------------------
            // In case of OR splits, only put a token on a branch if it can reach
            // one event within "n" events in the trace. "n" is the number of
            // branches of the split.
            //-----------------------------------------------
            else if (helper.getAllORSplits().contains(cur)) {
                for (FlowNode branchNode : helper.getTargets(cur)) {
                    for (XEvent event : trace.getSubTrace(helper.getTargets(cur).size()).getTrace()) {
//                        if (this.next(helper.getSet(branchNode), LogUtility.getConceptName(event)) != null) {
//                            //There's an event within this gateway proximity that can be reached from this branch node
//                            nexts = helper.getSet(branchNode);
//                            this.updateTokenChangeForEdges(cur, previous, helper.getSet(branchNode));
//                            bpmnTrace.getMetrics().setProducedTokenCount(bpmnTrace.getMetrics().getProducedTokenCount() + 1);
//                        }
                    }
                }
            }

            //----------------------------------------------
            //Update marking after the firing
            //Special processing for a join node (AND/OR) since it has to wait for 
            //all branch nodes to be active before it can be active
            //Also, grow the current replayed trace (graph) with new nodes added to the markings
            //----------------------------------------------
            for (FlowNode nextNode : nexts) {
                if (helper.getAllJoins().contains(nextNode) || helper.getAllORJoins().contains(nextNode)) {
                    sync.put(nextNode, helper.addToSet(sync.get(nextNode), cur));
                    // Only add join node to markings if its all branch sources have been fired
                    if (sync.get(nextNode).containsAll(helper.getSources(nextNode))) {
                        markings = helper.addToSet(markings, nextNode);
                        bpmnTrace.add(helper.getSources(nextNode),new TraceNode(nextNode)); //Attach new node to the trace
                        bpmnTrace.getMetrics().setConsumedTokenCount(bpmnTrace.getMetrics().getConsumedTokenCount() + helper.getSources(nextNode).size());
                    }                    
                }
                else { 
                    markings = helper.addToSet(markings, nextNode);    
                    bpmnTrace.add(cur, new TraceNode(nextNode)); //Attach new node to the trace
                    bpmnTrace.getMetrics().setConsumedTokenCount(bpmnTrace.getMetrics().getConsumedTokenCount() + 1);
                }
            }
            
            //----------------------------------------------
            // Check enabledness of OR-Join and include in the markings if it is enabled
            // Note: if OR-Join is not enabled then we must remove it out of 
            // the current marking so that its state can be evaluated again
            //----------------------------------------------
            for (FlowNode orJoin : helper.getAllORJoins()) {
                if (ORJoinEnactmentManager.isEnabled(orJoin)) {
                    markings = helper.addToSet(markings, orJoin);
                } else {
                    if (markings.contains(orJoin)) {
                        markings.remove(orJoin);
                    }
                }
            }
            
            
            //----------------------------------------------
            // Find a suitable node in current markings for next move with the next trace event
            // If the current process state cannot reach the next trace event
            // -> skip the activity and try next one until finding a node that can reach the event
            //----------------------------------------------
            nextToCurrent = null;
            while ((nextToCurrent == null) && (trace.size() > 0)) {
                nextToCurrent = this.nextShortest(markings, trace);
                if (nextToCurrent == null) {
                    trace = trace.dropFirst();
                    bpmnTrace.getMetrics().setMissingTokenCount(bpmnTrace.getMetrics().getMissingTokenCount() + 1);
                    unableReplayCount++;
                }
            }
            previous = cur;
            cur = nextToCurrent;            
        }
        //end of while
        
        bpmnTrace.getMetrics().setRemainingTokenCount(markings.size());
        bpmnTrace.getMetrics().setTraceFitness(1.0*(1 - unableReplayCount/oriTrace.size()));
        
        bpmnTrace.pruneOrphanNodes();

        return bpmnTrace;
    } 
    
    /*
    * Replay using backtracking algorithm
    * Return replay trace or empty if backtracking returns no result
    * Noted side effect: the input trace can be preprocessed (activities not in the model are removed)
    * Assume: encodedTraces has been created for the log.
    */
    public ReplayTrace replayWithBacktracking(XTrace trace) {
        Node leafNode; //contains the found backtracking leaf node
        
        //---------------------------------------------------
        // Use clustering. If the same trace has been replayed
        // before, then no need another replay but just reuse the 
        // replay result done for previous trace.
        //---------------------------------------------------
        String traceString = EncodeTraces.getEncodeTraces().getCharStream(LogUtility.getConceptName(trace));
        if (traceBacktrackingNodeMap.containsKey(traceString)) {
            leafNode = traceBacktrackingNodeMap.get(traceString);
        }
        else { // has to replay a new trace
            //--------------------------------------------
            //Remove unfound event name in process model
            //--------------------------------------------
            for (XEvent event : trace) {
                if (!helper.getActivityNames().contains(LogUtility.getConceptName(event))) {
                    trace.remove(event);
                }
            }

            //--------------------------------------------
            // Replay trace with backtracking algorithm
            //--------------------------------------------
            Backtracking backtrack = new Backtracking(this.params, trace, helper);
            leafNode = backtrack.explore();    
            if (leafNode != null && !traceBacktrackingNodeMap.containsKey(traceString)) {
                traceBacktrackingNodeMap.put(traceString, leafNode); //traceString: the original trace string.
            }
        }
        
        Stack<Node> stack = new Stack<>();
        Node node = leafNode;
        while (node != null) {
            stack.push(node);
            node = node.getParent();
        }
        
        //---------------------------------------------
        // Create replay trace from the backtracking result
        //---------------------------------------------
        ReplayTrace replayTrace = new ReplayTrace(new XTrace2(trace));
        if (!stack.isEmpty()) {
            
            //--------------------------------------------
            // Set up start event node for replay trace
            //--------------------------------------------
            FlowNode modelNode = helper.getStartEvent();
            TraceNode traceNode = new TraceNode(modelNode);
            traceNode.setStart((new DateTime(LogUtility.getTimestamp(trace.get(0)))).minusSeconds(20));
            replayTrace.setStart(traceNode);
            
            //---------------------------------------------
            // Traverse backtracking result from the beginning, note that
            // it must always start with the start event node (so start event node has been
            // added to replay trace above to be matched with). At every node, connect all targets into
            // the replay trace, set proper node properties so that those redundant nodes
            // would be pruned later on
            //---------------------------------------------
            Map<FlowNode,Collection<FlowNode>> joinSourcesMap = new HashMap();
            while (!stack.empty()) {
                node = (Node)stack.pop();
                if (node.getState().getElementStatus() != StateElementStatus.EVENT_SKIPPED) {
                    modelNode = node.getState().getElement();
                    
                    //----------------------------------------------------
                    // Connect the current joining node to its all source nodes
                    // which have been collected before
                    //----------------------------------------------------
                    if (helper.getAllJoins().contains(modelNode) || helper.getAllORJoins().contains(modelNode)) {
                        replayTrace.add(joinSourcesMap.get(modelNode), new TraceNode(modelNode));
                        joinSourcesMap.get(modelNode).clear();
                    }
                    
                    //----------------------------------------------------
                    // Mark the current node: the order it is replayed,
                    // it is activity matched with timestamp or skipped activity
                    //----------------------------------------------------
                    traceNode = replayTrace.getMarkingsMap().get(modelNode);
                    if (traceNode != null) {
                        replayTrace.setReplayed(modelNode);
                        if (traceNode.isActivity()) {
                            if (node.getState().getElementStatus() == StateElementStatus.ACTIVITY_MATCHED) {
                                traceNode.setIsMatched(true);
                                traceNode.setStart(new DateTime(LogUtility.getTimestamp(trace.get(node.getState().getTraceIndex()-1))));
                            } else {
                                traceNode.setVirtual(true);
                            }
                        }
                    }
                    
                    //-----------------------------------------------------
                    // Connect all targets of current node to it (pruning is done later)
                    // For joining: collect its possible source nodes and will connect 
                    // them all to joining node when it is encountered above.
                    //-----------------------------------------------------
                    for (FlowNode target : helper.getTargets(modelNode)) {
                        if (helper.getAllJoins().contains(target) || helper.getAllORJoins().contains(target)) {
                            joinSourcesMap.put(target, helper.addToSet(joinSourcesMap.get(target), modelNode));
                        }
                        else {
                            replayTrace.add(modelNode, new TraceNode(target));
                        }
                    }
                }
            }
            
            //----------------------------------------------
            // Set timing for end event if it is connected to the replay trace
            //----------------------------------------------
            TraceNode endNode = replayTrace.getMarkingsMap().get(helper.getEndEvent());
            if (endNode != null) {
                endNode.setStart((new DateTime(LogUtility.getTimestamp(trace.get(trace.size()-1)))).plusSeconds(20));
            }
            
            //-----------------------------------------
            // Prune those nodes not on the backtracking result
            //-----------------------------------------
            replayTrace.pruneOrphanNodes();
        }
        
        return replayTrace;
    }
       
    /*
    * When current node is fired
    *   - Each of its active incoming sequences flow will reduce one token
    *   - Each of its outgoing sequences will have one new token
    */
    private void updateTokenChangeForEdges(FlowNode current, FlowNode previous, Collection<FlowNode> targets) {
        int currentCount;
        
        //Incoming sequence flows
        if (helper.getAllMerges().contains(current)) {  //XOR join: remove one token on the active incoming flow
            for (SequenceFlow flow : current.getIncomingSequenceFlows()) {
                if (previous == (FlowNode)flow.getSourceRef()) {
                    currentCount = this.edgeToTokenChangeMap.get(flow);
                    currentCount--;
                    this.edgeToTokenChangeMap.put(flow, currentCount);
                }
            }
        } 
        else if (helper.getAllJoins().contains(current)) { //AND join: remove one token on every incoming flow
            for (SequenceFlow flow : current.getIncomingSequenceFlows()) {
                currentCount = this.edgeToTokenChangeMap.get(flow);
                currentCount--;
                this.edgeToTokenChangeMap.put(flow, currentCount);
            }
        }
        else if (helper.getAllORJoins().contains(current)) { //OR join: remove one token on every active incoming flow (has token)
            for (SequenceFlow flow : current.getIncomingSequenceFlows()) {
                currentCount = this.edgeToTokenChangeMap.get(flow);
                if (currentCount > 0) {
                    currentCount--;
                    this.edgeToTokenChangeMap.put(flow, currentCount);
                }
            }
        }
        else { // Activity node, XOR split, AND split, OR split: remove one token on the only incoming sequence flow
            for (SequenceFlow flow : current.getIncomingSequenceFlows()) {
                currentCount = this.edgeToTokenChangeMap.get(flow);
                currentCount--;
                this.edgeToTokenChangeMap.put(flow, currentCount);
            }
        }
        
        
        //Outgoing sequence flows
        //XOR split: add one token on the selected outgoing flow
        //AND or OR split: add one token on every outgoing flow
        //Others: add one token on the only outgoing flow
        for (SequenceFlow flow : current.getOutgoingSequenceFlows()) {
            if (targets.contains((FlowNode)flow.getTargetRef())) {
                currentCount = this.edgeToTokenChangeMap.get(flow);
                currentCount++;
                this.edgeToTokenChangeMap.put(flow, currentCount);
            }
        }            
        
        //Call to update all OR-join state
        ORJoinEnactmentManager.update(edgeToTokenChangeMap);
    }
    
    //Select the next node in nodeSet to replay against trace
    //The node either matches with the trace first event
    //or its next activities contain the trace first event
    private FlowNode next(Set<FlowNode> nodeSet, XTrace2 trace, Map<FlowNode,XTrace2> lastVisitedStates) {
        //Find activity in markings first
        for (FlowNode node : nodeSet) {
            if (node instanceof Activity) {
                if (node.getName().equals(trace.getFirstActName())) {
                    return node;
                }
            }
        }

        //Then look to reachable activities
        for (FlowNode node : nodeSet) {
            if (!(node instanceof Activity)) {
                if ((lastVisitedStates.get(node)==null) || 
                        !trace.equals(lastVisitedStates.get(node))) {
                    for (FlowNode nextAct : helper.getNextActivities(node)) {
                        if (nextAct.getName().equals(trace.getFirstActName())) {
                            return node;
                        }
                    }
                }
            }
        }

        return null; //return null after trying all above.
    }

    /*
    * Select the next node which can reach the current trace activity via shortest path
    * nodeSet contains next nodes to be considered
    * Use Dijikstra to check the shortest path (path with least number of activities)
    */
    private FlowNode nextShortest(Collection<FlowNode> nodeSet, XTrace2 trace) {
        FlowNode selectedNode = null;
        ArrayList<Vertex> path; 
        int shortest = Integer.MAX_VALUE;
        int count;
        FlowNode target = helper.getNodeFromEvent(trace.getFirstActName());
        for (FlowNode node : nodeSet) {
            if (node == target) {
                selectedNode = node;
                break;
            }
            else {
                helper.getDijkstraAlgo().execute(helper.getDijikstraVertex(node));
                path = helper.getDijkstraAlgo().getPath(helper.getDijikstraVertex(target));
                if (path != null) {
                    count = helper.countActivities(path);
                    if (count < shortest) {
                        shortest = count;
                        selectedNode = node;
                    }
                }
            }
        }
        return selectedNode;
    }
    
   /* NOT FINISHED YET
    * Start from nodeSet as a starting state (markings), search for a process path which can best align 
    * with the eventSequence. Stop when reaching the end of the process (cannot proceed) or the number
    * of activities in the path equal the length of the eventSequence
    * Return: a map.
    * Key: list of FlowNode as the best selected path
    * Value: integer "1" represent match with 
    */
    public void findPath(Collection<FlowNode> markings, XTrace2 trace, int curIndex, 
                         List<FlowNode> previous, int previousMark, 
                         List<FlowNode> best, int bestMark) {
        
        if (previous.size() >= trace.size()) {
            //assign previous to best if it is better aligned to the trace
            return;
        }
        
        FlowNode select;
        Collection<FlowNode> nexts = new HashSet();
        Map<FlowNode, Collection<FlowNode>> sync = new HashMap();
        Collection<FlowNode> bigSet;
        
        for (FlowNode cur : markings) {
            
            if (helper.getActivities().contains(cur)) {
                previous.add(cur);
            }
            
            bigSet = helper.getSet(cur);
            bigSet.addAll(helper.getAllMerges());
            bigSet.addAll(helper.getAllJoins());
            bigSet.addAll(helper.getActivities());
            bigSet.addAll(helper.getAllForks());
        
            while (cur != null) {
                markings.remove(cur);
                nexts.clear();

                //----------------------------------------------
                // In case of Start Event, Merge, Join, Activity, Fork
                // FIRE the current element, all next active nodes are in nexts set
                //----------------------------------------------
                if (bigSet.contains(cur)) { // FIRE HERE!!!!! (meaning put next nodes into nexts set)
                    nexts.addAll(helper.getTargets(cur));
                    if (helper.getAllJoins().contains(cur)) { // reset in case there is repetition
                        sync.put(cur, new HashSet());
                    }
                    else if (helper.getActivities().contains(cur)) { 
                        if (LogUtility.getConceptName(trace.get(curIndex)).equals(cur.getName())) { //match activity with the event
                            previousMark++;
                        } 
                    }
                    
                }
                
                //-----------------------------------------------
                //In case of XOR-Split
                //-----------------------------------------------
                else if (helper.getAllDecisions().contains(cur)) { 
                    
                }
                
                //-----------------------------------------------
                //In case of OR-Split
                //-----------------------------------------------
                else if (helper.getAllORSplits().contains(cur)) {
                    
                }

                //----------------------------------------------
                //Update marking after the firing
                //Special processing for a join node since it has to wait for 
                //all branch nodes to be active before it can be active
                //----------------------------------------------
                for (FlowNode nextNode : nexts) {
                    if (helper.getAllJoins().contains(nextNode)) {
                        sync.put(nextNode, helper.addToSet(sync.get(nextNode), cur));
                        if (sync.get(nextNode).containsAll(helper.getSources(nextNode))) {
                            markings = helper.addToSet(markings, nextNode);
                        }                    
                    }
                    else {
                        markings = helper.addToSet(markings, nextNode);    
                    }
                }

                //-------------------------------------------
                //Select a node in the current markings to fire for the next event.
                //-------------------------------------------
                if (helper.getTargets(cur).contains(helper.getEndEvent())) { //reach the end of process model
                    if (trace.size() == markings.size() && markings.isEmpty()) {
                    }
                    cur = null; //stop
                }
                else if (trace.isEmpty()) { //reach the end of trace
                    cur = null; //stop
                }
                else {
                    select = null;
                    while ((select == null) && (trace.size() > 0)) {
                        //select = this.next(markings, trace, lastVisitedStates);
                        if (select == null) {
                            trace = trace.dropFirst();
                        }
                    }
                    cur = select;
                }
            }
        }
        
    }     
    
    /*
    * Select a node from nodeSet that could best replay the trace
    * The trace must be replayed continously in order without skipping
    * Try every node in nodeSet and select the best node that could play the trace
    */
    private FlowNode bestChoice(Set<FlowNode> nodeSet, XTrace2 trace) {    
        return null;
    }
    
    private boolean isCurrentEventReachable(FlowNode node, XTrace2 trace) {
        for (FlowNode nextAct : helper.getNextActivities(node)) {
            if (nextAct.getName().equals(trace.getFirstActName())) {
                return true;
            }
        }
        return false;
    }
    
    
    public float getFitness() {
        return fitness;
    }   
    
    
    public double getLogStartTime() {
        return logStartTime;
    }
    
    public double getLogEndTime() {
        return logEndTime;
    }    
    
    public double getTaskAverageDuration(String taskId) {
        return 0;
    }    
    
    public double getEdgeAverageDuration(String edgeId) {
        return 0;
    }
    
    private String getLogColor() {
        curColorIndex++;
        if (curColorIndex < this.colors.length) {
            return this.colors[curColorIndex];
        } else {
            return "black"; //black is used to indicate out of color index
        }
    }
}
