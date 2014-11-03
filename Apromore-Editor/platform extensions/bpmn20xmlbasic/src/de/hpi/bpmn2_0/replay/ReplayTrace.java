package de.hpi.bpmn2_0.replay;

import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.activity.Activity;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Logger;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.Seconds;
import servlet.BPMNAnimationServlet;

/*
* Note that this trace can be incomplete: not having end node
* depending on a particular log trace. For example, a fork node might not be
* always has a corresponding join node in the trace since halfway during replay
* the log trace runs out of events.
*/
public class ReplayTrace {
    private TraceNode startNode = null;
    private DateTime startDate = null;
    private DateTime endDate = null;
    private float fitness;
    private XTrace2 logTrace = null;
    
    //All nodes in the trace. Different from model nodes because there can be different trace nodes
    //corresponding to one model node (as a result of loop effect in the model)
    private ArrayList<TraceNode> traceNodes = new ArrayList();
    
    //Contains replayed node in the order of timing. Only contain node of model that is actually played
    //Every element points to a trace node in the replayed trace
    //This is to support for the replayed trace, like its flatten form in timing order
    private ArrayList<TraceNode> timeOrderedReplayedNodes = new ArrayList();
    
    private ArrayList<TraceNode> forkNodes = new ArrayList();
    
    //All sequence flows in the trace.
    private ArrayList<SequenceFlow> sequenceFlows = new ArrayList();
    
    //This is a mapping from the marking in BPMN model to the marking of this trace
    //Note that the different between BPMN model and this trace is this trace has flatten structure
    //Meaning repeated path of events on BPMN model is replicated as new paths on this trace
    //key: one node in the current marking of BPMN model
    //value: corresponding node in current marking of this trace
    //During replay, markings of BPMN model and this trace must be kept synchronized.
    private Map<FlowNode,TraceNode> markingsMap = new HashMap();
    
    private BidiMap<TraceNode,TraceNode> forkJoinMap = new DualHashBidiMap<>();
    private BidiMap<TraceNode,TraceNode> ORforkJoinMap = new DualHashBidiMap<>();
    
    private Metrics metrics = new Metrics();
    
    private static final Logger LOGGER = Logger.getLogger(ReplayTrace.class.getCanonicalName());
    
    public ReplayTrace(XTrace2 trace) {
        this.logTrace = trace;
    }
    
    //startNode has been replayed
    public ReplayTrace(TraceNode startNode, XTrace2 trace) {
        this.startNode = startNode;
        this.markingsMap.put(startNode.getModelNode(), startNode);
        if (!this.traceNodes.contains(startNode)) {
            this.traceNodes.add(startNode);
        }
        this.logTrace = trace;
    }
    
    public String getId() {
        if (logTrace != null) {
            return logTrace.getName();
        }
        else {
            return null;
        }
    }
    
    public XTrace2 getOriginalTrace() {
        return this.logTrace;
    }
    
    public TraceNode getStart() {
        return startNode;
    }
    
    public void setStart(TraceNode startNode) {
        this.startNode = startNode;
        this.markingsMap.put(startNode.getModelNode(), startNode);
        if (!this.traceNodes.contains(startNode)) {
            this.traceNodes.add(startNode);
        }
    }
    
    public DateTime getStartDate() {
        return timeOrderedReplayedNodes.get(0).getStart();
    }
    
    public DateTime getEndDate() {
        return timeOrderedReplayedNodes.get(timeOrderedReplayedNodes.size()-1).getStart();
    }
    
    public Interval getInterval() {
        if (this.startDate != null && this.endDate != null) {
            return new Interval(new DateTime(this.startDate), new DateTime(this.endDate));
        }
        else {
            return null;
        }
    }
    
    //newNode: node to add
    //curModelNode: source node to be connected with the new node
    //Note that any collection with elements added here must be considered in pruning process
    public void add(FlowNode curModelNode, TraceNode newNode) {
        FlowNode newModelNode = newNode.getModelNode();
        SequenceFlow modelFlow = null;
        
        TraceNode curNode = this.getMarkingsMap().get(curModelNode);
        
        SequenceFlow traceFlow = new SequenceFlow();
        traceFlow.setSourceRef(curNode);
        traceFlow.setTargetRef(newNode);
        
        //Search for original sequence id
        for (SequenceFlow flow : curModelNode.getOutgoingSequenceFlows()) {
            if (newModelNode == flow.getTargetRef()) {
                modelFlow = flow;
                break;
            }
        }
        if (modelFlow != null) {
            traceFlow.setId(modelFlow.getId());
        }
        
        curNode.getOutgoing().add(traceFlow);
        newNode.getIncoming().add(traceFlow);
        
        traceNodes.add(newNode);
        if (newNode.isFork()) {
            forkNodes.add(newNode);
        }
        this.markingsMap.put(newNode.getModelNode(), newNode);
    }   
    
    //nodeSet: set of branch nodes of a join
    //joinNode: join node to add
    //Note that any collection with elements added here must be considered in pruning process
    public void add(Collection<FlowNode> nodeSet, TraceNode joinNode) {
        SequenceFlow traceFlow;
        SequenceFlow modelFlow = null;
        TraceNode branchNode;
        FlowNode newModelNode = joinNode.getModelNode();
        
        for (FlowNode node : nodeSet) {
            branchNode = this.getMarkingsMap().get(node);
            
            traceFlow = new SequenceFlow();
            traceFlow.setSourceRef(branchNode);
            traceFlow.setTargetRef(joinNode);
            
            for (SequenceFlow flow : node.getOutgoingSequenceFlows()) {
                if (newModelNode == flow.getTargetRef()) {
                    modelFlow = flow;
                    break;
                }
            }
            if (modelFlow != null) {
                traceFlow.setId(modelFlow.getId());
            }
            
            branchNode.getOutgoing().add(traceFlow);
            joinNode.getIncoming().add(traceFlow);
        }
        this.traceNodes.add(joinNode);
        this.markingsMap.put(joinNode.getModelNode(), joinNode);
    }    
    
    //Add the input node to the list of nodes which have been replayed
    //This method assumes that the replayed node has been added to the list of 
    //all nodes in the replayed trace.
    public void setReplayed(FlowNode curModelNode) {
        this.timeOrderedReplayedNodes.add(this.markingsMap.get(curModelNode));
    }
    
    //Return the replayed trace nodes in the replayed timing order
    public List<TraceNode> getTimeOrderedReplayedNodes() {
        return this.timeOrderedReplayedNodes;
    }
    
    public void setMatchedActivity(FlowNode curNode) {
        TraceNode traceNode = this.markingsMap.get(curNode);
        traceNode.setIsMatched(true);
    }
    
    public void setVirtual(FlowNode curNode) {
        TraceNode traceNode = this.markingsMap.get(curNode);
        traceNode.setVirtual(true);
    }
    
    //Orphan nodes are those activity node without true match with trace event
    //or non-activity nodes with no outgoing sequence flows
    public void pruneOrphanNodes() {
        boolean stop = false;
        SequenceFlow seqFlow;
        TraceNode source;
        TraceNode target;
        ArrayList<TraceNode> toRemove = new ArrayList();
        
        //Perform until there is no node to be removed
        while (!stop) {
            stop = true;
            for (TraceNode node : this.traceNodes) {
                if (node.isActivity() && !toRemove.contains(node)) {
                    if (!node.isMatched() && !node.isVirtual()) {
                        stop = false;
                        toRemove.add(node);
                        for (SequenceFlow flow : node.getIncomingSequenceFlows()) {
                            source = (TraceNode)flow.getSourceRef();
                            source.getOutgoing().remove(flow);
                            node.getIncoming().remove(flow);
                            flow.setSourceRef(null);
                            flow.setTargetRef(null);
                        }
                        
                        for (SequenceFlow flow : node.getOutgoingSequenceFlows()) {
                            target = (TraceNode)flow.getTargetRef();
                            target.getIncoming().remove(flow);
                            node.getOutgoing().remove(flow);
                            flow.setSourceRef(null);
                            flow.setTargetRef(null);                            
                        }
                    }
                } else {
                    if (node.getOutgoing().isEmpty() && !toRemove.contains(node) && !node.isEndEvent()) {
                        stop = false;
                        toRemove.add(node);
                        for (SequenceFlow flow : node.getIncomingSequenceFlows()) {
                            source = (TraceNode)flow.getSourceRef();
                            source.getOutgoing().remove(flow);
                            node.getIncoming().remove(flow);
                        }
                    }
                }
            }
        }
            
        for (TraceNode toRemoveNode : toRemove) {
            this.traceNodes.remove(toRemoveNode);
        }
        
        for (TraceNode toRemoveNode : toRemove) {
            this.forkNodes.remove(toRemoveNode);
        }        
    }
   
    public void setNodeTime(FlowNode node, Date date) {
        this.markingsMap.get(node).setStart(new DateTime(date));
    }
    
    public ArrayList<TraceNode> getNodes() {
        return traceNodes;
    }
    
    public ArrayList<SequenceFlow> getSequenceFlows() {
        if (this.sequenceFlows.isEmpty()) {
            for (TraceNode node : this.getNodes()) {
                for (SequenceFlow flow : node.getOutgoingSequenceFlows()) {
                    this.sequenceFlows.add(flow);
                }
            }
        }
        return this.sequenceFlows;
    }   
    
   
    public Map<FlowNode,TraceNode> getMarkingsMap() {
        return markingsMap;
    }
    
    
    public Collection<TraceNode> getLeafNodes() {
        Collection<TraceNode> leafNodes = new HashSet();
        for (TraceNode node : this.traceNodes) {
            if (node.getTargets().isEmpty()) {
                leafNodes.add(node);
            }
        }
        return leafNodes;
    }
    
    public Collection<FlowNode> getLeafModelNodes() {
        Collection<FlowNode> leafNodes = new HashSet();
        for (TraceNode node : this.traceNodes) {
            if (node.getTargets().isEmpty()) {
                leafNodes.add(node.getModelNode());
            }
        }
        return leafNodes;        
    }
    
    //Auto grow this trace by extending every leaf node to its next targets
    //The growth is basd on the underlying model node targets
    //Used for searching for a path to a node while not knowing the exact start node
    public void growAuto() {
        FlowNode flowNode;
        FlowNode target;
        for (TraceNode traceNode : this.getLeafNodes()) {
            flowNode = traceNode.getModelNode();
            for (SequenceFlow seqFlow : flowNode.getOutgoingSequenceFlows()) {
                target = (FlowNode)seqFlow.getTargetRef();
                if (target != null) {
                    this.add(flowNode, new TraceNode(target));
                }
            }
        }
    }
            
    public void clear() {
        startNode = null;
        this.traceNodes.clear();
        this.markingsMap.clear();
        this.forkJoinMap.clear();
        this.forkNodes.clear();
        this.sequenceFlows.clear();
    }
    
    public boolean isEmpty() {
        return (startNode == null);
    }
    
    public float getFitness() {
        return fitness;
    }    
    
    public void setFitness(int fitness) {
        this.fitness = fitness;
    }
    
    public boolean isFlat() {
        boolean isFlat = true;
        for (TraceNode node: traceNodes) {
            if (node.isFork() || node.isJoin()) {
                isFlat = false;
                break;
            }
        }
        return isFlat;
    }
    
    public void calcTiming() {
        LOGGER.info("REPLAYED TRACE BEFORE TIMING CALC");
        this.print(startNode, 0);
        
        calcTimingAll();
        LOGGER.info("REPLAYED TRACE AFTER TIMING CALC");
        this.print(startNode, 0);
        
        calculateCompleteTimestamp();
    }
    
    /*
    * timeOrderedReplayedNodes keeps the tracenode in replay order. Note that
    * replay order follows the order of the trace event, so they are in ascending timing order.
    * In addition, in this order, the split gateway is always after the joining gateway and their
    * branch nodes are all in between them.
    * Use the flatten form of replayed trace to calculate timing for forks and joins
    * From a fork on the flatten trace, traverse forward and backward to search for 
    * a timed node (node with real timing data). Remember there is always either a timed start event 
    * or a timed activity or end event (also timed) as two ends on the traversing direction
    * Calculate in timing order from start to end
    */
    private void calcTimingAll() {
        TraceNode node;
        DateTime timeBefore=null;
        int backwardDistance = 0;
        DateTime timeAfter=null;
        int forwardDistance = 0;
        double duration;
        TraceNode traverse = null;
        int timeBeforePos = 0;
        int timeAfterPos = 0;
        
        for (int i=0; i<timeOrderedReplayedNodes.size();i++) {
            node = timeOrderedReplayedNodes.get(i);
            if (!node.isTimed()) {
                
                //----------------------------------------
                //go backward and look for time node 
                //known that start node is always timed
                //----------------------------------------
                for (int j=i-1;j>=0;j--) {
                    if (timeOrderedReplayedNodes.get(j).isTimed()) {
                        timeBefore = timeOrderedReplayedNodes.get(j).getStart();
                        timeBeforePos = j;
                        break;
                    }
                }
                
                //----------------------------------------
                //go forward and look for time node
                //known that end node is always timed or there is always a timed activity 
                //----------------------------------------
                for (int j=i+1;j<timeOrderedReplayedNodes.size();j++) {
                    if (timeOrderedReplayedNodes.get(j).isTimed()) {
                        timeAfter = timeOrderedReplayedNodes.get(j).getStart();
                        timeAfterPos = j;
                        break;
                    }
                }
                
                //----------------------------------------
                //It can happen that timeBefore = timeAfter due to two activities
                //on parallel branches and have the same timestamp
                //----------------------------------------
                if (timeBefore != null && timeAfter != null && timeBefore.isEqual(timeAfter)) {
                    if (node.getSources().size() <= 1) { //for activity or split gateway: continue search backward
                        for (int j=timeBeforePos-1;j>=0;j--) {
                            if (timeOrderedReplayedNodes.get(j).isTimed() &&
                                timeOrderedReplayedNodes.get(j).getStart().isBefore(timeAfter)) {
                                timeBefore = timeOrderedReplayedNodes.get(j).getStart();
                                timeBeforePos = j;
                                break;
                            }
                        }
                    }
                    else {  //for joining gateway: continue search forward
                        for (int j=timeAfterPos+1;j<timeOrderedReplayedNodes.size();j++) {
                            if (timeOrderedReplayedNodes.get(j).isTimed() && 
                                timeOrderedReplayedNodes.get(j).getStart().isAfter(timeBefore)) {
                                timeAfter = timeOrderedReplayedNodes.get(j).getStart();
                                timeAfterPos = j;
                                break;
                            }
                        }
                    }
                }
                
                //----------------------------------------------
                //Always take two ends of the trace plus a buffer as time limit
                //in case the replay trace has no timestamped activity at two ends 
                //----------------------------------------------
                if (timeBefore == null) {
                    timeBefore = (new DateTime(LogUtility.getTimestamp(logTrace.getTrace().get(0)))).minusSeconds(20);
                }
                if (timeAfter == null) {
                    timeAfter = (new DateTime(LogUtility.getTimestamp(logTrace.getTrace().get(logTrace.getTrace().size()-1)))).plusSeconds(20);
                }
                
                //----------------------------------------------
                // Take average timestamp between TimeBefore and TimeAfter
                //----------------------------------------------
                duration = 1.0*(new Duration(timeBefore, timeAfter)).getMillis();
                duration = 1.0*duration*(i-timeBeforePos)/(timeAfterPos - timeBeforePos);
                node.setStart(timeBefore.plus(Double.valueOf(duration).longValue()));
            }
        }
    }
    
    
    
    private BidiMap<TraceNode,TraceNode> getForkJoinMap() {
        if (forkJoinMap.isEmpty()) {
            forkJoinMap = (new ForkJoinMap(this.startNode)).getFork2JoinMap();
        }
        return forkJoinMap;
    }

    /*
    * Set complete timestamp for every node since event log only contains one timestamp
    * By default event timestamp is set to start date of a trace node
    * This is to take a small portion of the start date to be a complete date
    * Need to check start timestamp of all target nodes to ensure complete timestamp is before all of them
    * Assume that all nodes have start timestamp calculated and assigned (not null).
    */
    private void calculateCompleteTimestamp() {
        DateTime earliestTarget=null;
        long transferDuration;
        for (TraceNode node : this.traceNodes) {
            if (node.isActivity()) {
                if (node.getTargets().size() > 0) {
                    earliestTarget = node.getTargets().get(0).getStart();
                    for (TraceNode target : node.getTargets()) {
                        if (earliestTarget.isAfter(target.getStart())) {
                            earliestTarget = target.getStart();
                        }
                    }
                    transferDuration = (new Duration(node.getStart(),earliestTarget)).getMillis();
                    node.setComplete(node.getStart().plusMillis(Long.valueOf(Math.round(transferDuration*0.1)).intValue()));
                }
                else {
                    node.setComplete(node.getStart().plusMillis(5000));
                }
            }
            else {
                node.setComplete(node.getStart());
            }
            

        }
    }
   
    
    /*
    * Require a process model with block structure 
    * ANDsplit-ANDjoin, ORsplit-ORjoin, or ANDsplit-ORjoin
    * Note that ORsplit-ANDjoin creates an unsound model and should not exist
    */
    class ForkJoinMap {
        BidiMap<TraceNode, TraceNode> f2j = new DualHashBidiMap();
        Set visited = new HashSet(); //used to stop the recursive call chain as well as multiple visits to the same join gate
        Stack<TraceNode> s = new Stack();
        TraceNode firstElement = null;

        public ForkJoinMap(TraceNode firstElement) {
            this.firstElement = firstElement;
            depthfirst(firstElement);
        }
        
        private void depthfirst(TraceNode node) {
            if (!visited.contains(node)) {
                visited.add(node);
                
                if (node.isFork() || node.isORSplit()) {
                    s.push(node);
                } else if (node.isJoin() || node.isORJoin()) {
                    if (!s.isEmpty()) { // check stack in case of join node without split node (non-block structure)
                        f2j.put(s.pop(),node);
                    }
                }
                for (TraceNode nextNode : node.getTargets()) {
                    depthfirst(nextNode);
                }
            }
        }
        
        public BidiMap<TraceNode, TraceNode> getFork2JoinMap() {
            return f2j;
        }
    }  
    
    public void print(TraceNode node, Integer indentNum) {
        FlowNode flowNode;
                
        if (node == null) {
            return;
        }
        
        String printString = "";
        String nodeType = "";
        String branchNodes = "";
        String dateString = "";
        
        if (node.isActivity()) {
            nodeType = "Activity";
            branchNodes = "";
        }
        else if (node.isStartEvent()) {
            nodeType = "StartEvent";
        }
        else if (node.isEndEvent()) {
            nodeType = "EndEvent";
        }
        else if (node.isFork()) {
            nodeType = "Fork";
            branchNodes += " => ";
            for (SequenceFlow flow : node.getOutgoingSequenceFlows()) {
                flowNode = (FlowNode)flow.getTargetRef();
                branchNodes += flowNode.getName();
                branchNodes += "+";
            }
        }
        else if (node.isJoin()) {
            nodeType = "Join";
            branchNodes += " <= ";
            for (SequenceFlow flow : node.getIncomingSequenceFlows()) {
                flowNode = (FlowNode)flow.getSourceRef();
                branchNodes += flowNode.getName();
                branchNodes += "+";
            }            
        }
        else if (node.isDecision()) {
            nodeType = "Decision";
            branchNodes += " -> ";
            for (SequenceFlow flow : node.getOutgoingSequenceFlows()) {
                flowNode = (FlowNode)flow.getTargetRef();
                branchNodes += flowNode.getName();
                branchNodes += "+";
            }            
        }
        else if (node.isMerge()) {
            nodeType = "Merge";
            branchNodes += " <- ";
            for (SequenceFlow flow : node.getIncomingSequenceFlows()) {
                flowNode = (FlowNode)flow.getSourceRef();
                branchNodes += flowNode.getName();
                branchNodes += "+";
            }            
        }
        else if (node.isORSplit()) {
            nodeType = "OR-Split";
            branchNodes += " => ";
            for (SequenceFlow flow : node.getOutgoingSequenceFlows()) {
                flowNode = (FlowNode)flow.getTargetRef();
                branchNodes += flowNode.getName();
                branchNodes += "+";
            }
        }
        else if (node.isORJoin()) {
            nodeType = "OR-Join";
            branchNodes += " <= ";
            for (SequenceFlow flow : node.getIncomingSequenceFlows()) {
                flowNode = (FlowNode)flow.getSourceRef();
                branchNodes += flowNode.getName();
                branchNodes += "+";
            }            
        }
        
        if (node.getStart() != null) {
            dateString = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(node.getStart().toDate());
        }
        
        printString += (nodeType + ":" + node.getName() + ":" + dateString + ":" + branchNodes);
        printString = this.padLeft(printString, indentNum);
                
        LOGGER.info(printString);
        
        if (node.isDecision() || node.isMerge() || node.isActivity() || 
            node.isJoin() || node.isORJoin() ||
            node.isStartEvent() || node.isEndEvent()) {
            if (node.isDecision()) {
                indentNum += 2;
            }
            else if (node.isMerge()) {
                //indentNum -= 2;
            }
            
            if (node.getTargets().size() > 0) {
                if ((!node.getTargets().get(0).isJoin() && !node.getTargets().get(0).isORJoin()) ||
                    !this.getForkJoinMap().containsValue(node.getTargets().get(0))) { // to do for join node without split node
                    node = node.getTargets().get(0);
                    indentNum += 2;
                    this.print(node, indentNum);
                }
            }
        }
        else if (node.isFork() || node.isORSplit()){
            indentNum += 2;
            for (TraceNode branchNode : node.getTargets()) {
                this.print(branchNode, indentNum);
            }
            node = this.getForkJoinMap().get(node);
            indentNum -= 2;
            this.print(node, indentNum);
        }
        /*
        else if (node.isORSplit()) {
            indentNum += 2;
            for (TraceNode branchNode : node.getTargets()) {
                this.print(branchNode, indentNum);
            }
            node = this.getForkJoinMap().get(node);
            indentNum -= 2;
            this.print(node, indentNum);
        }
        */
        
    }
    
    private String padLeft(String s, int n) {
        if (n <= 0)
            return s;
        int noOfSpaces = n * 2;
        StringBuilder output = new StringBuilder(s.length() + noOfSpaces);
        while (noOfSpaces > 0) {
            output.append(" ");
            noOfSpaces--;
        }
        output.append(s);
        return output.toString();
    }
    
    public Metrics getMetrics() {
        return this.metrics;
    }
    
}