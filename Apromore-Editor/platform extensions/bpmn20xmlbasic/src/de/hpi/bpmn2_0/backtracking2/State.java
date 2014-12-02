package de.hpi.bpmn2_0.backtracking2;

import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import de.hpi.bpmn2_0.replay.BPMNDiagramHelper;
import de.hpi.bpmn2_0.replay.FlowNode2;
import de.hpi.bpmn2_0.replay.LogUtility;
import de.hpi.bpmn2_0.replay.ORJoinEnactmentManager;
import de.hpi.bpmn2_0.replay.ReplayParams;
import de.hpi.bpmn2_0.replay.ReplayTrace;
import de.hpi.bpmn2_0.replay.XTrace2;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XTrace;

/*
* An abstraction of state of a node
* For replaying a log on a process model, this state implementation is consisted of:
*   - The current markings of the process (tokens and their locations)
*   - The trace and current trace index
*   - The element (selected process activity) and its status (take or skip)
* There are three possible moves on a state:
*   - Take current process activity (if matched)
*   - Skip current process activity (can be matched or unmatched)
*   - Skip current trace event (can be matched or unmatched)
*/
public class State {
    private Set<SequenceFlow> markings = new HashSet<>(); //markings with token on the sequence flow
    private FlowNode element;
    private StateElementStatus elementStatus;
    private XTrace trace;
    private int traceIndex;
    //private Set<FlowNode> visitedNodes = new HashSet(); //contain visited nodes used in shortest path finding
    private Set<Set<SequenceFlow>> visitedStates = new HashSet(); //contain visited states, used in shortest path finding
    private BPMNDiagramHelper helper;
    private ReplayParams params;
    
    private static final Logger LOGGER = Logger.getLogger(State.class.getCanonicalName());
  
    
    public State(Set<SequenceFlow> markings, FlowNode element, StateElementStatus elementStatus,
                 XTrace trace, int traceIndex, BPMNDiagramHelper helper, ReplayParams params) {
        this.markings = markings;
        this.element = element;
        this.elementStatus = elementStatus;
        this.trace = trace;
        this.traceIndex = traceIndex;
        this.helper = helper;
        this.params = params;
    }
    
    public State(Set<SequenceFlow> markings, FlowNode element, StateElementStatus elementStatus,
                 XTrace trace, int traceIndex, Set<Set<SequenceFlow>> visitedStates, BPMNDiagramHelper helper, ReplayParams params) {
        this.markings = markings;
        this.element = element;
        this.elementStatus = elementStatus;
        this.trace = trace;
        this.traceIndex = traceIndex;
        this.visitedStates = visitedStates;
        this.helper = helper;
        this.params = params;
    }    
    
    public String getName() {
        return element.getName() + "." + elementStatus.name();
    }
    
    public FlowNode getElement() {
        return element;
    }
    
    public StateElementStatus getElementStatus() {
        return elementStatus;
    }
    
    /**
     * Generate next possible states from this state
     * Pruning conditions apply.
     * Possible scenarios from one state:
     *  - New states are produced, including activity_matched, activity_skipped or event_skipped
     *  - No new states are created due to pruning conditions, return empty set
     *  - No new states created due to deadlock encountered: no token moves occurred
     *  - No new states are created because this state is end state: end of trace 
     */
    public Set<State> nextStates () {
        //Use comparator to prevent adding duplicate states
        SortedSet<State> states = new TreeSet<>(
                                    new Comparator<State>() {
                                        @Override
                                        public int compare(State s1, State s2) {
                                            if ((s1.getElement() == s2.getElement() || 
                                                    (s1.getElement() instanceof EventNode && s2.getElement() instanceof EventNode)) &&
                                                s1.getElementStatus().name().equals(s2.getElementStatus().name()) &&
                                                s1.getMarkings().size() == s2.getMarkings().size() &&
                                                s1.getMarkings().containsAll(s2.getMarkings()) &&
                                                s1.getTraceIndex() == s2.getTraceIndex()) {
                                                return 0;
                                            }
                                            else {
                                                return -1;
                                            }
                                    }
                                }); 
        
        //Select a node of the current markings and 
        //and the possible move on the trace (skip or not)
        if (!this.isEndState()) {
            FlowNode node;
            FlowNode eventNode = helper.getNodeFromEvent(LogUtility.getConceptName(trace.get(traceIndex)));
            Set<SequenceFlow> newMarkings;
            for (SequenceFlow sequence : markings) {
                node = (FlowNode)sequence.getTargetRef();

                if (helper.getActivities().contains(node)) {
                    //Take Activity
                    if (node.getName().equals(LogUtility.getConceptName(trace.get(traceIndex)))) {
                    //if (node.getNameRef().equals(((XAttributeLiteral)(trace.get(traceIndex).getAttributes().get("concept:name"))).getValue())) {    
                        newMarkings = (HashSet)((HashSet)this.markings).clone();
                        newMarkings.remove(sequence);
                        newMarkings.add(node.getOutgoingSequenceFlows().get(0));
                        states.add(new State(newMarkings, node, StateElementStatus.ACTIVITY_MATCHED, this.trace, this.traceIndex+1, helper, params));
                    }
                    else {
                        //Skip Activity
                        //Only skip if this activity can reach the current event within the threshold of distance
                        //Otherwise, it is either rejected later or it can be covered by skip event state
                        if (helper.countNodes(helper.getPath(node, eventNode)) <= params.getMaxNodeDistance()) {
                            if (!node.getOutgoingSequenceFlows().isEmpty()) {
                                newMarkings = (HashSet)((HashSet)this.markings).clone();
                                newMarkings.remove(sequence);
                                newMarkings.add(node.getOutgoingSequenceFlows().get(0));
                                states.add(new State(newMarkings, node, StateElementStatus.ACTIVITY_SKIPPED, this.trace, this.traceIndex,helper,params));
                            }
                        }
                    }
                }
                //Only create a new state from an XOR-branch if it can reach the current event within acceptable distance
                //Because it will never reach that event no matter after how many activity skips -> always be rejected
                else if (helper.getAllDecisions().contains(node)) {
                    for (SequenceFlow branch : node.getOutgoingSequenceFlows()) {
                        if (helper.countNodes(helper.getPath((FlowNode)branch.getTargetRef(), eventNode)) <= params.getMaxNodeDistance()) {
                            newMarkings = (HashSet)((HashSet)this.markings).clone();
                            newMarkings.remove(sequence);
                            newMarkings.add(branch);
                            states.add(new State(newMarkings, node, StateElementStatus.XORSPLIT, this.trace, this.traceIndex, helper, params));
                        }
                    }
                }
                // Only add new node if the shortest path to next event node is within acceptable distance
                else if (helper.getAllForks().contains(node)) {
                    if (helper.countNodes(helper.getPath(new HashSet(node.getOutgoingSequenceFlows()), eventNode)) <= params.getMaxNodeDistance()) {
                        newMarkings = (HashSet)((HashSet)this.markings).clone();
                        newMarkings.remove(sequence);
                        newMarkings.addAll(node.getOutgoingSequenceFlows());
                        states.add(new State(newMarkings, node, StateElementStatus.ANDSPLIT, this.trace, this.traceIndex, helper, params));
                    }
                }
                //Only add set of flows that can reach the current event within acceptable distance
                else if (helper.getAllORSplits().contains(node)) {
                    Set<Set<SequenceFlow>> sequenceORSet = SetUtils.powerSet(new HashSet(node.getOutgoingSequenceFlows()));
                    for (Set<SequenceFlow> flows : sequenceORSet) {
                        if (!flows.isEmpty() && 
                            helper.countNodes(helper.getPath(flows, eventNode)) <= 1.0*params.getMaxNodeDistance()) {
                            newMarkings = (HashSet)((HashSet)this.markings).clone();
                            newMarkings.remove(sequence);
                            newMarkings.addAll(flows);
                            states.add(new State(newMarkings, node, StateElementStatus.ORSPLIT, this.trace, this.traceIndex, helper, params));
                        }
                    }
                }
                else if (helper.getAllJoins().contains(node)) {
                    if (markings.containsAll(node.getIncomingSequenceFlows())) {
                        newMarkings = (HashSet)((HashSet)this.markings).clone();
                        newMarkings.removeAll(node.getIncomingSequenceFlows());
                        newMarkings.add(node.getOutgoingSequenceFlows().get(0));
                        states.add(new State(newMarkings, node, StateElementStatus.ANDJOIN, this.trace, this.traceIndex, helper, params));
                    }
                } 
                else if (helper.getAllMerges().contains(node)) {
                    newMarkings = (HashSet)((HashSet)this.markings).clone();
                    newMarkings.remove(sequence);
                    newMarkings.add(node.getOutgoingSequenceFlows().get(0));
                    states.add(new State(newMarkings, node, StateElementStatus.XORJOIN, this.trace, this.traceIndex, helper, params));
                }
                else if (helper.getAllORJoins().contains(node)) {
                    if (ORJoinEnactmentManager.isEnabled(node, this)) {
                        newMarkings = (HashSet)((HashSet)this.markings).clone();
                        for (SequenceFlow incoming : node.getIncomingSequenceFlows()) {
                            newMarkings.remove(incoming);
                        }
                        newMarkings.add(node.getOutgoingSequenceFlows().get(0));
                        states.add(new State(newMarkings, node, StateElementStatus.ORJOIN, this.trace, this.traceIndex, helper, params));
                    }
                }
                else if (node == helper.getStartEvent() || node == helper.getEndEvent()) {
                    //Do nothing
                }
            }

            //Skip Event (there is always a skip event state created)
            newMarkings = (HashSet)((HashSet)this.markings).clone();
            states.add(new State(newMarkings, new EventNode(LogUtility.getConceptName(trace.get(traceIndex))), 
                                 StateElementStatus.EVENT_SKIPPED, this.trace, this.traceIndex+1, helper, params));
        }
        return states;
    }
    
    /**
     * Return next states for searching a path to the end event
     * The move excludes those loop-back moves, just go forward to the end event
     * Every time it generates only one next state except the case of XOR and OR split
     * For XOR split, one new state is generated per branch
     * For OR split, one new state is generated per combination of branches
     * @return set of next states for movement
     * There are six possible scenarios
     *  - move to new state(s): return set contains one or more states 
     *  - deadlock encountered: no token move is possible, return set is empty 
     *  - end event is reached, but not a proper completion state: return set is empty
     *  - loop encountered: new state belongs to set of visited states, return set is empty
     *  - proper completion encountered: return set is empty
     *  - improper completion encountered: return set is empty
     */
    public Set<State> nextStatesForShortestPathExploration () {
        Set<State> states = new HashSet();
        
        if (!isProperCompletion() && !isImproperCompletion()) {
            FlowNode node;
            Set<SequenceFlow> newMarkings;
            //Set<FlowNode> newVisitedNodes;
            Set<Set<SequenceFlow>> newVisitedStates;
            boolean nextMoveDone = false;
            for (SequenceFlow sequence : markings) {
                
                node = (FlowNode)sequence.getTargetRef();
                
                if (helper.getActivities().contains(node)) {
                    newMarkings = (HashSet)((HashSet)this.markings).clone();
                    newMarkings.remove(sequence);
                    newMarkings.add(node.getOutgoingSequenceFlows().get(0));
                    
                    if (!visitedStates.contains(newMarkings)) {
                        newVisitedStates = (HashSet)((HashSet)this.visitedStates).clone();
                        newVisitedStates.add(newMarkings);
                        states.add(new State(newMarkings, node, StateElementStatus.ACTIVITY_SKIPPED, this.trace, this.traceIndex, newVisitedStates, helper, params));
                        nextMoveDone = true;
                    }
                }
                //XOR gateway generates one state for every branch
                else if (helper.getAllDecisions().contains(node)) {
                    for (SequenceFlow branch : node.getOutgoingSequenceFlows()) {
                        newMarkings = (HashSet)((HashSet)this.markings).clone();
                        newMarkings.remove(sequence);
                        newMarkings.add(branch);
                        
                        if (!visitedStates.contains(newMarkings)) {
                            newVisitedStates = (HashSet)((HashSet)this.visitedStates).clone();
                            newVisitedStates.add(newMarkings);
                            states.add(new State(newMarkings, node, StateElementStatus.XORSPLIT, this.trace, this.traceIndex, newVisitedStates, helper, params));
                            nextMoveDone = true;
                        }
                    }
                }
                else if (helper.getAllForks().contains(node)) {
                    newMarkings = (HashSet)((HashSet)this.markings).clone();
                    newMarkings.remove(sequence);
                    newMarkings.addAll(node.getOutgoingSequenceFlows());
                    
                    if (!visitedStates.contains(newMarkings)) {
                        newVisitedStates = (HashSet)((HashSet)this.visitedStates).clone();
                        newVisitedStates.add(newMarkings);
                        states.add(new State(newMarkings, node, StateElementStatus.ANDSPLIT, this.trace, this.traceIndex, newVisitedStates, helper, params));
                        nextMoveDone = true;
                    }
                }
                else if (helper.getAllORSplits().contains(node)) {
                    Set<Set<SequenceFlow>> sequenceORSet = SetUtils.powerSet(new HashSet(node.getOutgoingSequenceFlows()));
                    for (Set<SequenceFlow> flows : sequenceORSet) {
                        if (!flows.isEmpty()) {
                            newMarkings = (HashSet)((HashSet)this.markings).clone();
                            newMarkings.remove(sequence);
                            newMarkings.addAll(flows);
                            
                            if (!visitedStates.contains(newMarkings)) {
                                newVisitedStates = (HashSet)((HashSet)this.visitedStates).clone();
                                newVisitedStates.add(newMarkings);
                                states.add(new State(newMarkings, node, StateElementStatus.ORSPLIT, this.trace, this.traceIndex, newVisitedStates, helper, params));
                                nextMoveDone = true;
                            }
                        }
                    }
                }                    
                else if (helper.getAllJoins().contains(node)) {
                    if (markings.containsAll(node.getIncomingSequenceFlows())) {
                        newMarkings = (HashSet)((HashSet)this.markings).clone();
                        newMarkings.removeAll(node.getIncomingSequenceFlows());
                        newMarkings.add(node.getOutgoingSequenceFlows().get(0));
                        
                        if (!visitedStates.contains(newMarkings)) {
                            newVisitedStates = (HashSet)((HashSet)this.visitedStates).clone();
                            newVisitedStates.add(newMarkings);
                            states.add(new State(newMarkings, node, StateElementStatus.ANDJOIN, this.trace, this.traceIndex, newVisitedStates, helper, params));
                            nextMoveDone = true;
                        }
                    }
                } 
                else if (helper.getAllMerges().contains(node)) {
                    newMarkings = (HashSet)((HashSet)this.markings).clone();
                    newMarkings.remove(sequence);
                    newMarkings.add(node.getOutgoingSequenceFlows().get(0));
                    
                    if (!visitedStates.contains(newMarkings)) {
                        newVisitedStates = (HashSet)((HashSet)this.visitedStates).clone();
                        newVisitedStates.add(newMarkings);
                        states.add(new State(newMarkings, node, StateElementStatus.XORJOIN, this.trace, this.traceIndex, newVisitedStates, helper, params));
                        nextMoveDone = true;
                    }
                }
                else if (helper.getAllORJoins().contains(node)) {
                    if (ORJoinEnactmentManager.isEnabled(node, this)) {
                        newMarkings = (HashSet)((HashSet)this.markings).clone();
                        for (SequenceFlow incoming : node.getIncomingSequenceFlows()) {
                            newMarkings.remove(incoming);
                        }
                        newMarkings.add(node.getOutgoingSequenceFlows().get(0));
                        
                        if (!visitedStates.contains(newMarkings)) {
                            newVisitedStates = (HashSet)((HashSet)this.visitedStates).clone();
                            newVisitedStates.add(newMarkings);
                            states.add(new State(newMarkings, node, StateElementStatus.ORJOIN, this.trace, this.traceIndex, newVisitedStates, helper, params));
                            nextMoveDone = true;
                        }
                    }
                }
                else if (node == helper.getStartEvent() || node == helper.getEndEvent()) {
                    //Do nothing
                }

                if (nextMoveDone) {
                    break; //only move one token at a time
                }
            }
        }
        return states; //return empty set if no move is performed
    }
    
    public boolean isProperCompletion() {
        return (helper.getEndEvent().getIncomingSequenceFlows().containsAll(markings) && markings.size()==1);
    }
    
    public boolean isImproperCompletion() {
        return (markings.containsAll(helper.getEndEvent().getIncomingSequenceFlows()) && markings.size()>=2);
    }    
    
    public boolean isEndState() {
        return (traceIndex >= trace.size() || markings.isEmpty());
    }
    
    public boolean isTraceFinished() {
        return (traceIndex >= trace.size());
    }
    
    /**
     * Only reliable if the marking is proper complete and trace has been finished
     * @return true if reliable
     */
    /*
    public boolean isReliable() {
        return (markings.size()==1 && 
                markings.contains(helper.getEndEvent().getIncomingSequenceFlows().get(0)) &&
                traceIndex >= trace.size());
    }
    */
    
    public boolean isMatch() {
        return (elementStatus == StateElementStatus.ACTIVITY_MATCHED);
    }
    
    public boolean isActivitySkip() {
        return (elementStatus == StateElementStatus.ACTIVITY_SKIPPED);
    }
    
    //Cost of reaching this state
    public double getCost() {
        if (elementStatus == StateElementStatus.ACTIVITY_MATCHED) {
            return params.getActivityMatchCost();
        }
        else if (elementStatus == StateElementStatus.ACTIVITY_SKIPPED) {
            return params.getActivitySkipCost();
        }
        else if (elementStatus == StateElementStatus.EVENT_SKIPPED) {
            return params.getEventSkipCost();
        }
        else { // start/end event, gateways (gateways should have cost = 0 for nested gateways process)
            return params.getNonActivityMoveCost(); 
        }
    } 

    //Benefit of reaching this state
    public int getBenefit() {
        if (elementStatus == StateElementStatus.ACTIVITY_MATCHED) {
            return 2;
        }
        else if (elementStatus == StateElementStatus.ACTIVITY_SKIPPED) {
            return 0;
        }
        else if (elementStatus == StateElementStatus.EVENT_SKIPPED) {
            return 0;
        }
        else {
            return 0;
        }
    } 
    
    public int getDepth() {
        if (elementStatus == StateElementStatus.STARTEVENT) {
            return 0;
        }
        else {
            return 1;
        }
    }
   
    public String getTraceWithIndex() {
        String res = "";
        for (int i=0; i<trace.size(); i++) {
            if (traceIndex < trace.size() && i==traceIndex) {
                res += "*";
            }
            res += LogUtility.getConceptName(trace.get(i));
        }
        if (traceIndex >= trace.size()) {
            res += "*";
        }
        return res;
    }
    
    public int getTraceIndex() {
        return traceIndex;
    }
    
    public XTrace getTrace() {
        return trace;
    }
    
    public void setTrace(XTrace trace) {
        this.trace = trace;
        this.traceIndex = 0;
    }
   
    public Set<SequenceFlow> getMarkings() {
        return this.markings;
    }
    
    public String getMarkingsText() {
        String markingStr = "";
        for (SequenceFlow flow : markings) {
            markingStr += "*" + flow.getTargetRef().getName();
        }
        return markingStr;
    }
    
    public void clear() {
        markings.clear();
        visitedStates.clear();
        element = null;
        trace = null;
        helper = null;
        params = null;        
    }
    
    
   
}