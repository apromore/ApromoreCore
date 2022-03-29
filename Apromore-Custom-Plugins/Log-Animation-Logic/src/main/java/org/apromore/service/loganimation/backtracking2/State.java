/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
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

package org.apromore.service.loganimation.backtracking2;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.apromore.service.loganimation.replay.BPMNDiagramHelper;
import org.apromore.service.loganimation.replay.LogUtility;
import org.apromore.service.loganimation.replay.ORJoinEnactmentManager;
import org.apromore.service.loganimation.replay.ReplayParams;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XTrace;

import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;

/*
* An abstraction of state of a node
* For replaying a log on a process model, this state implementation is consisted of:
*   - The current markings of the process (tokens and their locations)
*   - The trace and current trace index
*   - The element (selected process activity) and its status (take or skip)
*/
public class State {
    private Set<SequenceFlow> markings = new HashSet<>(); //markings with token on the sequence flow
    private FlowNode element;
    private StateElementStatus elementStatus;
    private XTrace trace;
    private int traceIndex;
    //private Set<FlowNode> visitedNodes = new HashSet(); //contain visited nodes used in shortest path finding
    private Set<Set<SequenceFlow>> visitedMarkings = new HashSet(); //contain visited marking, used in nextStatesForShortestPathExploration
    private Set<State> visitedStates = new HashSet(); //contain visited states, used in nextStates
    private BPMNDiagramHelper helper;
    private ReplayParams params;
    private XConceptExtension xce = XConceptExtension.instance();
    
    private static final Logger LOGGER = Logger.getLogger(State.class.getCanonicalName());
  
    
    public State(Set<SequenceFlow> markings, FlowNode element, StateElementStatus elementStatus,
                 XTrace trace, int traceIndex, Set<State> visitedStates, Set<Set<SequenceFlow>> visitedMarkings,
                 BPMNDiagramHelper helper, ReplayParams params) {
        this.markings = markings;
        this.element = element;
        this.elementStatus = elementStatus;
        this.trace = trace;
        this.traceIndex = traceIndex;
        this.visitedStates = visitedStates;
        this.visitedMarkings = visitedMarkings;
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
    public Set<State> nextStates (Node currentNode) {
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
        
        //------------------------------------------
        // Set up parameters for look-ahead strategy
        //------------------------------------------
        int curActSkipCount = currentNode.getActivitySkipCount();
        int curMatchCount = currentNode.getMatchCount();
        int curConsecutiveUnmatch = currentNode.getConsecutiveUnmatch();
        double curCost = currentNode.getCost();
        
        //----------------------------------------------
        // Generate next states with pruning to avoid
        // sub-optimal states according to heuristics
        //----------------------------------------------
        if (!this.isEndState()) {
            FlowNode node;
            FlowNode eventNode = helper.getNodeFromEvent(LogUtility.getConceptName(trace.get(traceIndex)));
            Set<SequenceFlow> newMarkings;
            Set<State> newVisitedStates = (HashSet)((HashSet)visitedStates).clone();
            newVisitedStates.add(this);
            for (SequenceFlow sequence : markings) {
                node = (FlowNode)sequence.getTargetRef();

                if (helper.getActivities().contains(node)) {
                    //Take Activity
//                    if (node.getName().equals(LogUtility.getConceptName(trace.get(traceIndex)))) {
                    if (node.getNameRef() == xce.extractName(trace.get(traceIndex))) {
                        newMarkings = (HashSet)((HashSet)this.markings).clone();
                        newMarkings.remove(sequence);
                        newMarkings.add(node.getOutgoingSequenceFlows().get(0));
                        if (!params.isCheckViciousCycle() || (!containsORViciousCycle(newMarkings) && !containsANDViciousCycle(newMarkings))) {
                            states.add(new State(newMarkings, node, StateElementStatus.ACTIVITY_MATCHED, this.trace, this.traceIndex+1, newVisitedStates, visitedMarkings, helper, params));
                        }
                        else if (params.isBacktrackingDebug()) {
//                            LOGGER.info(node.getName() + ".ACTIVITY_MATCHED: this state is pruned!" +
//                                        " marking:" + getMarkingsText(newMarkings) +
//                                        " trace:" + getTraceWithIndex(traceIndex+1));
                        }
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
                                if (!contains(newVisitedStates, newMarkings,traceIndex) &&
                                    //!checkPruningConditions(newMarkings, traceIndex, StateElementStatus.ACTIVITY_SKIPPED, curActSkipCount, curMatchCount, curConsecutiveUnmatch, curCost) &&
                                    (!params.isCheckViciousCycle() || (!containsORViciousCycle(newMarkings) && !containsANDViciousCycle(newMarkings)))) {
                                    states.add(new State(newMarkings, node, StateElementStatus.ACTIVITY_SKIPPED, this.trace, this.traceIndex, newVisitedStates, visitedMarkings, helper,params));
                                }
                                else if (params.isBacktrackingDebug()) {
//                                    LOGGER.info(node.getName() + ".ACTIVITY_SKIP: this state is pruned!" +
//                                                " marking:" + getMarkingsText(newMarkings) +
//                                                " trace:" + getTraceWithIndex(traceIndex));
                                }
                            }
                        }
                    }
                }
                //Only create a new state from an XOR-branch if it can reach the current event within acceptable distance
                //Because it will never reach that event no matter after how many activity skips -> always be rejected
                else if (helper.getAllDecisions().contains(node) ||
                         helper.getAllMixedXORs().contains(node)) {
                    for (SequenceFlow branch : node.getOutgoingSequenceFlows()) {
                        if (helper.countNodes(helper.getPath((FlowNode)branch.getTargetRef(), eventNode)) <= params.getMaxNodeDistance()) {
                            newMarkings = (HashSet)((HashSet)this.markings).clone();
                            newMarkings.remove(sequence);
                            newMarkings.add(branch);
                            if (!contains(newVisitedStates, newMarkings,traceIndex) &&
                                //!checkPruningConditions(newMarkings, traceIndex, StateElementStatus.XORSPLIT, curActSkipCount, curMatchCount, curConsecutiveUnmatch, curCost) &&
                                (!params.isCheckViciousCycle() || (!containsORViciousCycle(newMarkings) && !containsANDViciousCycle(newMarkings)))) {
                                states.add(new State(newMarkings, node, StateElementStatus.XORSPLIT, this.trace, this.traceIndex, newVisitedStates, visitedMarkings, helper, params));
                            }
                            else if (params.isBacktrackingDebug()) {
//                                LOGGER.info(node.getName() + ".XORSPLIT: this state is pruned!" +
//                                            " marking:" + getMarkingsText(newMarkings) +
//                                            " trace:" + getTraceWithIndex(traceIndex));
                            }
                            
                        }
                    }
                }
                // Only add new node if the shortest path to next event node is within acceptable distance
                else if (helper.getAllForks().contains(node) ||
                         (helper.getAllMixedANDs().contains(node) && markings.containsAll(node.getIncomingSequenceFlows()))) {
                    if (helper.countNodes(helper.getPath(new HashSet(node.getOutgoingSequenceFlows()), eventNode)) <= params.getMaxNodeDistance()) {
                        newMarkings = (HashSet)((HashSet)this.markings).clone();
                        newMarkings.remove(sequence);
                        newMarkings.addAll(node.getOutgoingSequenceFlows());
                        if (!contains(newVisitedStates, newMarkings,traceIndex) &&
                            //!checkPruningConditions(newMarkings, traceIndex, StateElementStatus.ANDSPLIT, curActSkipCount, curMatchCount, curConsecutiveUnmatch, curCost) &&
                            (!params.isCheckViciousCycle() || (!containsORViciousCycle(newMarkings) && !containsANDViciousCycle(newMarkings)))) {
                            states.add(new State(newMarkings, node, StateElementStatus.ANDSPLIT, this.trace, this.traceIndex, newVisitedStates, visitedMarkings, helper, params));
                        }
                        else if (params.isBacktrackingDebug()) {
//                            LOGGER.info(node.getName() + ".ANDSPLIT: this state is pruned!" +
//                                        " marking:" + getMarkingsText(newMarkings) +
//                                        " trace:" + getTraceWithIndex(traceIndex));
                        }
                    }
                }
                //Only add set of flows that can reach the current event within acceptable distance
                else if  (helper.getAllORSplits().contains(node) ||
                          (helper.getAllMixedORs().contains(node) && ORJoinEnactmentManager.isEnabled(node, this.markings))) {
                    Set<Set<SequenceFlow>> sequenceORSet = SetUtils.powerSet(new HashSet(node.getOutgoingSequenceFlows()));
                    for (Set<SequenceFlow> flows : sequenceORSet) {
                        if (!flows.isEmpty() &&
                            helper.countNodes(helper.getPath(flows, eventNode)) <= 1.0*params.getMaxNodeDistance()) {
                            newMarkings = (HashSet)((HashSet)this.markings).clone();
                            newMarkings.remove(sequence);
                            newMarkings.addAll(flows);
                            if (!contains(newVisitedStates, newMarkings,traceIndex) &&
                                //!checkPruningConditions(newMarkings, traceIndex, StateElementStatus.ORSPLIT, curActSkipCount, curMatchCount, curConsecutiveUnmatch, curCost) &&
                                (!params.isCheckViciousCycle() || (!containsORViciousCycle(newMarkings) && !containsANDViciousCycle(newMarkings)))) {
                                states.add(new State(newMarkings, node, StateElementStatus.ORSPLIT, this.trace, this.traceIndex, newVisitedStates, visitedMarkings, helper, params));
                            }
                            else if (params.isBacktrackingDebug()) {
//                                LOGGER.info(node.getName() + ".ORSPLIT: this state is pruned!" +
//                                            " marking:" + getMarkingsText(newMarkings) +
//                                            " trace:" + getTraceWithIndex(traceIndex));
                            }
                        }
                    }
                }
                else if (helper.getAllJoins().contains(node)) {
                    if (markings.containsAll(node.getIncomingSequenceFlows())) {
                        newMarkings = (HashSet)((HashSet)this.markings).clone();
                        newMarkings.removeAll(node.getIncomingSequenceFlows());
                        newMarkings.add(node.getOutgoingSequenceFlows().get(0));
                        if (!contains(newVisitedStates, newMarkings,traceIndex) &&
                            //!checkPruningConditions(newMarkings, traceIndex, StateElementStatus.ANDJOIN, curActSkipCount, curMatchCount, curConsecutiveUnmatch, curCost) &&
                            (!params.isCheckViciousCycle() || (!containsORViciousCycle(newMarkings) && !containsANDViciousCycle(newMarkings)))) {
                            states.add(new State(newMarkings, node, StateElementStatus.ANDJOIN, this.trace, this.traceIndex, newVisitedStates, visitedMarkings, helper, params));
                        }
                        else if (params.isBacktrackingDebug()) {
//                            LOGGER.info(node.getName() + ".ANDJOIN: this state is pruned!" +
//                                        " marking:" + getMarkingsText(newMarkings) +
//                                        " trace:" + getTraceWithIndex(traceIndex));
                        }
                    }
                }
                else if (helper.getAllMerges().contains(node)) {
                    newMarkings = (HashSet)((HashSet)this.markings).clone();
                    newMarkings.remove(sequence);
                    newMarkings.add(node.getOutgoingSequenceFlows().get(0));
                    
                    if (!contains(newVisitedStates, newMarkings,traceIndex) &&
                        //!checkPruningConditions(newMarkings, traceIndex, StateElementStatus.XORJOIN, curActSkipCount, curMatchCount, curConsecutiveUnmatch, curCost) &&
                        (!params.isCheckViciousCycle() || (!containsORViciousCycle(newMarkings) && !containsANDViciousCycle(newMarkings)))) {
                        states.add(new State(newMarkings, node, StateElementStatus.XORJOIN, this.trace, this.traceIndex, newVisitedStates, visitedMarkings, helper, params));
                    }
                    else if (params.isBacktrackingDebug()) {
//                        LOGGER.info(node.getName() + ".XORJOIN: this state is pruned!" +
//                                    " marking:" + getMarkingsText(newMarkings) +
//                                    " trace:" + getTraceWithIndex(traceIndex));
                    }
                }
                else if (helper.getAllORJoins().contains(node)) {
                    if (ORJoinEnactmentManager.isEnabled(node, this.markings)) {
                        newMarkings = (HashSet)((HashSet)this.markings).clone();
                        newMarkings.removeAll(node.getIncomingSequenceFlows());
                        newMarkings.add(node.getOutgoingSequenceFlows().get(0));
                        
                        if (!contains(newVisitedStates, newMarkings,traceIndex) &&
                            //!checkPruningConditions(newMarkings, traceIndex, StateElementStatus.ORJOIN, curActSkipCount, curMatchCount, curConsecutiveUnmatch, curCost) &&
                            (!params.isCheckViciousCycle() || (!containsORViciousCycle(newMarkings) && !containsANDViciousCycle(newMarkings)))) {
                            states.add(new State(newMarkings, node, StateElementStatus.ORJOIN, this.trace, this.traceIndex, newVisitedStates, visitedMarkings, helper, params));
                        }
                        else if (params.isBacktrackingDebug()) {
//                            LOGGER.info(node.getName() + ".ORJOIN: this state is pruned!" +
//                                        " marking:" + getMarkingsText(newMarkings) +
//                                        " trace:" + getTraceWithIndex(traceIndex));
                        }
                    }
                    else if (params.isBacktrackingDebug()) {
//                        LOGGER.info(node.getName() + ".ORJOIN: not enabled");
                    }
                }
                else if (node == helper.getStartEvent() || node == helper.getEndEvent()) {
                    //Do nothing
                }
            }

            //Skip Event (there is always a skip event state created)
            newMarkings = (HashSet)((HashSet)this.markings).clone();
            //if (!checkPruningConditions(newMarkings, traceIndex+1, StateElementStatus.EVENT_SKIPPED, curActSkipCount, curMatchCount, curConsecutiveUnmatch, curCost)) {
            states.add(new State(newMarkings, new EventNode(LogUtility.getConceptName(trace.get(traceIndex))), StateElementStatus.EVENT_SKIPPED, this.trace, this.traceIndex+1, newVisitedStates, visitedMarkings, helper, params));
            //}
        }
        return states;
    }
    
    /**
     * Return next states for searching a path to the end event
     * The move excludes those loop-back moves and keeps going forward to the end event
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
    public Set<State> nextStatesForShortestPathExploration (Node currentNode) {
        Set<State> states = new HashSet();
        int currentActivityCount = currentNode.getActivitySkipCount();
        
        if (!isProperCompletion() && !isImproperCompletion()) {
            FlowNode node;
            Set<SequenceFlow> newMarking;
            //Set<FlowNode> newVisitedNodes;
            Set<Set<SequenceFlow>> newVisitedMarkings;
            boolean nextMoveDone = false;
            for (SequenceFlow sequence : markings) {
                
                node = (FlowNode)sequence.getTargetRef();
                
                if (helper.getActivities().contains(node)) {
                    newMarking = (HashSet)((HashSet)this.markings).clone();
                    newMarking.remove(sequence);
                    newMarking.add(node.getOutgoingSequenceFlows().get(0));
                    
                    if (!visitedMarkings.contains(newMarking) &&
                        isLessActivitiesThanShortestPath(newMarking, StateElementStatus.ACTIVITY_SKIPPED, currentActivityCount) &&
                        (!params.isCheckViciousCycle() || (!containsORViciousCycle(newMarking) && !containsANDViciousCycle(newMarking)))) {
                        
                        newVisitedMarkings = (HashSet)((HashSet)this.visitedMarkings).clone();
                        newVisitedMarkings.add(newMarking);
                        states.add(new State(newMarking, node, StateElementStatus.ACTIVITY_SKIPPED, this.trace, this.traceIndex, visitedStates, newVisitedMarkings, helper, params));
                        nextMoveDone = true;
                    }
                    else if (params.isExploreShortestPathDebug()) {
//                        LOGGER.info(node.getName() + ": this state is pruned!" + " marking:" + getMarkingsText(newMarking));
                    }
                }
                //XOR gateway generates one state for every branch
                else if (helper.getAllDecisions().contains(node)) {
                    for (SequenceFlow branch : node.getOutgoingSequenceFlows()) {
                        newMarking = (HashSet)((HashSet)this.markings).clone();
                        newMarking.remove(sequence);
                        newMarking.add(branch);
                        
                        if (!visitedMarkings.contains(newMarking) &&
                            isLessActivitiesThanShortestPath(newMarking, StateElementStatus.XORSPLIT, currentActivityCount) &&
                            (!params.isCheckViciousCycle() || (!containsORViciousCycle(newMarking) && !containsANDViciousCycle(newMarking)))) {
                            
                            newVisitedMarkings = (HashSet)((HashSet)this.visitedMarkings).clone();
                            newVisitedMarkings.add(newMarking);
                            states.add(new State(newMarking, node, StateElementStatus.XORSPLIT, this.trace, this.traceIndex, visitedStates, newVisitedMarkings, helper, params));
                            nextMoveDone = true;
                        }
                        else if (params.isExploreShortestPathDebug()) {
//                            LOGGER.info(node.getName() + ": this state is pruned!" + " marking:" + getMarkingsText(newMarking));
                        }
                    }
                }
                else if (helper.getAllForks().contains(node)) {
                    newMarking = (HashSet)((HashSet)this.markings).clone();
                    newMarking.remove(sequence);
                    newMarking.addAll(node.getOutgoingSequenceFlows());
                    
                    if (!visitedMarkings.contains(newMarking) &&
                        isLessActivitiesThanShortestPath(newMarking, StateElementStatus.ANDSPLIT, currentActivityCount) &&
                        (!params.isCheckViciousCycle() || (!containsORViciousCycle(newMarking) && !containsANDViciousCycle(newMarking)))) {
                        
                        newVisitedMarkings = (HashSet)((HashSet)this.visitedMarkings).clone();
                        newVisitedMarkings.add(newMarking);
                        states.add(new State(newMarking, node, StateElementStatus.ANDSPLIT, this.trace, this.traceIndex, visitedStates, newVisitedMarkings, helper, params));
                        nextMoveDone = true;
                    }
                    else if (params.isExploreShortestPathDebug()) {
//                        LOGGER.info(node.getName() + ": this state is pruned!" + " marking:" + getMarkingsText(newMarking));
                    }
                }
                else if (helper.getAllORSplits().contains(node)) {
                    Set<Set<SequenceFlow>> sequenceORSet = SetUtils.powerSet(new HashSet(node.getOutgoingSequenceFlows()));
                    for (Set<SequenceFlow> flows : sequenceORSet) {
                        if (!flows.isEmpty()) {
                            newMarking = (HashSet)((HashSet)this.markings).clone();
                            newMarking.remove(sequence);
                            newMarking.addAll(flows);
                            
                            if (!visitedMarkings.contains(newMarking) &&
                                isLessActivitiesThanShortestPath(newMarking, StateElementStatus.ORSPLIT, currentActivityCount) &&
                                (!params.isCheckViciousCycle() || (!containsORViciousCycle(newMarking) && !containsANDViciousCycle(newMarking)))) {
                                
                                newVisitedMarkings = (HashSet)((HashSet)this.visitedMarkings).clone();
                                newVisitedMarkings.add(newMarking);
                                states.add(new State(newMarking, node, StateElementStatus.ORSPLIT, this.trace, this.traceIndex, visitedStates, newVisitedMarkings, helper, params));
                                nextMoveDone = true;
                            }
                            else if (params.isExploreShortestPathDebug()) {
//                                LOGGER.info(node.getName() + ": this state is pruned!" + " marking:" + getMarkingsText(newMarking));
                            }
                        }
                    }
                }
                else if (helper.getAllJoins().contains(node)) {
                    if (markings.containsAll(node.getIncomingSequenceFlows())) {
                        newMarking = (HashSet)((HashSet)this.markings).clone();
                        newMarking.removeAll(node.getIncomingSequenceFlows());
                        newMarking.add(node.getOutgoingSequenceFlows().get(0));
                        
                        if (!visitedMarkings.contains(newMarking) &&
                            isLessActivitiesThanShortestPath(newMarking, StateElementStatus.ANDJOIN, currentActivityCount) &&
                            (!params.isCheckViciousCycle() || (!containsORViciousCycle(newMarking) && !containsANDViciousCycle(newMarking)))) {
                            
                            newVisitedMarkings = (HashSet)((HashSet)this.visitedMarkings).clone();
                            newVisitedMarkings.add(newMarking);
                            states.add(new State(newMarking, node, StateElementStatus.ANDJOIN, this.trace, this.traceIndex, visitedStates, newVisitedMarkings, helper, params));
                            nextMoveDone = true;
                        }
                        else if (params.isExploreShortestPathDebug()) {
//                            LOGGER.info(node.getName() + ": this state is pruned!" + " marking:" + getMarkingsText(newMarking));
                        }
                    }
                }
                else if (helper.getAllMerges().contains(node)) {
                    newMarking = (HashSet)((HashSet)this.markings).clone();
                    newMarking.remove(sequence);
                    newMarking.add(node.getOutgoingSequenceFlows().get(0));
                    
                    if (!visitedMarkings.contains(newMarking) &&
                        isLessActivitiesThanShortestPath(newMarking, StateElementStatus.XORJOIN, currentActivityCount) &&
                        (!params.isCheckViciousCycle() || (!containsORViciousCycle(newMarking) && !containsANDViciousCycle(newMarking)))) {
                        
                        newVisitedMarkings = (HashSet)((HashSet)this.visitedMarkings).clone();
                        newVisitedMarkings.add(newMarking);
                        states.add(new State(newMarking, node, StateElementStatus.XORJOIN, this.trace, this.traceIndex, visitedStates, newVisitedMarkings, helper, params));
                        nextMoveDone = true;
                    }
                    else if (params.isExploreShortestPathDebug()) {
//                        LOGGER.info(node.getName() + ": this state is pruned!" + " marking:" + getMarkingsText(newMarking));
                    }
                }
                else if (helper.getAllORJoins().contains(node)) {
                    if (ORJoinEnactmentManager.isEnabled(node, this.markings)) {
                        newMarking = (HashSet)((HashSet)this.markings).clone();
                        for (SequenceFlow incoming : node.getIncomingSequenceFlows()) {
                            newMarking.remove(incoming);
                        }
                        newMarking.add(node.getOutgoingSequenceFlows().get(0));
                        
                        if (!visitedMarkings.contains(newMarking) &&
                            isLessActivitiesThanShortestPath(newMarking, StateElementStatus.ORJOIN, currentActivityCount) &&
                            (!params.isCheckViciousCycle() || (!containsORViciousCycle(newMarking) && !containsANDViciousCycle(newMarking)))) {
                            
                            newVisitedMarkings = (HashSet)((HashSet)this.visitedMarkings).clone();
                            newVisitedMarkings.add(newMarking);
                            states.add(new State(newMarking, node, StateElementStatus.ORJOIN, this.trace, this.traceIndex, visitedStates, newVisitedMarkings, helper, params));
                            nextMoveDone = true;
                        }
                        else if (params.isExploreShortestPathDebug()) {
//                            LOGGER.info(node.getName() + ": this state is pruned!" + " marking:" + getMarkingsText(newMarking));
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
        return getCost(this.elementStatus);
    }
    
    private double getCost(StateElementStatus elementStatus) {
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
    
    public int getDepth() {
        if (elementStatus == StateElementStatus.STARTEVENT) {
            return 0;
        }
        else {
            return 1;
        }
    }
   
    public String getTraceWithIndex() {
        return getTraceWithIndex(this.traceIndex);
    }
    
    private String getTraceWithIndex(int traceIndex) {
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
        return getMarkingsText(this.markings);
    }
    
    private String getMarkingsText(Set<SequenceFlow> marking) {
        String markingStr = "";
        for (SequenceFlow flow : marking) {
            markingStr += "*" + flow.getTargetRef().getName();
        }
        return markingStr;
    }
     
    
    /**
     * Check if the visited states contain a state represented by a marking and a trace index
     * This is to avoid token movement in a loop
     * @param marking
     * @param traceIndex
     * @return
     */
    private boolean contains(Set<State> states, Set<SequenceFlow> marking, int traceIndex) {
        for (State state : states) {
            if (state.getMarkings().equals(marking) && state.getTraceIndex() == traceIndex) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check pruning conditions for the next state. This is done by looking to
     * the end of the trace and compare the difference between trace events and
     * the future model activities based on the current criteria, the moved element
     * and the future state (marking and traceIndex)
     * @param newMarking: the marking of next state
     * @param newTraceIndex: the trace index of next state
     * @param movedElementStatus: the moved element to go to the next state
     * @param currentActivitySkipCount: the current activity skip count (from root node)
     * @param currentMatchCount: the current match count (from root node)
     * @param currentConsecutiveUnmatch: the current diff series (from root node)
     * @param currentCost: the current cost (from root node)
     * @return true if pruning conditions are met
     */
    private boolean checkPruningConditions(Set<SequenceFlow> newMarking,
                                                int newTraceIndex,
                                                StateElementStatus movedElementStatus,
                                                int currentActivitySkipCount,
                                                int currentMatchCount,
                                                int currentConsecutiveUnmatch,
                                                double currentCost) {
        Set<String> activityNameSet = new HashSet();
        
        //Collect all immediate and reachable activities from the marking
        //Note: we get all activities on a chain on the model. They are minimum
        //activities to match with the remaining events of trace
        for (SequenceFlow flow : newMarking) {
            if (helper.getActivities().contains((flow.getTargetRef()))) {
                for (FlowNode node : helper.getActivityChain((FlowNode)flow.getTargetRef())) {
                    activityNameSet.add(node.getName());
                }
            }
        }
        
        //Note: collect all remaining events to the end of the trace
        Set<String> eventNameSet = new HashSet();
        for (int i=newTraceIndex; i<=(trace.size()-1); i++) {
            eventNameSet.add(LogUtility.getConceptName(trace.get(i)));
        }
        
        //Get activities differing from the remaining events
        activityNameSet.removeAll(eventNameSet);
        
        //Calculate criteria value to check with
        int futureUnmatchCount = activityNameSet.size();
        double futureCost = futureUnmatchCount*Math.min(params.getActivitySkipCost(), params.getEventSkipCost());
        int maxAllowedUnmatch = (trace.size() - Long.valueOf(Math.round(params.getMinMatchPercent()*trace.size())).intValue());
        double maxActivitySkipCount = 1.0*params.getMaxActivitySkipPercent()*trace.size();
        
        int moveDiffCount;
        if (movedElementStatus == StateElementStatus.ACTIVITY_SKIPPED ||
            movedElementStatus == StateElementStatus.EVENT_SKIPPED) {
            moveDiffCount = 1;
        }
        else {
            moveDiffCount = 0;
        }
        int moveSkipCount = ((movedElementStatus == StateElementStatus.ACTIVITY_SKIPPED) ? 1 : 0);
        
        boolean condActivitySkip = ((moveSkipCount + futureUnmatchCount + currentActivitySkipCount) > maxActivitySkipCount);
        boolean condMatchCount = ((traceIndex - currentMatchCount + futureUnmatchCount) > maxAllowedUnmatch);
        boolean condCost = ((currentCost + getCost(movedElementStatus) + futureCost) > params.getMaxCost());
        boolean condConsecutiveUnmatch = ((currentConsecutiveUnmatch + moveDiffCount) > params.getMaxConsecutiveUnmatch());
        
        return (condActivitySkip || condMatchCount || condCost || condConsecutiveUnmatch);
    }
    
    /**
     * Check if this marking can result in number of activities less than
     * the MaxActivitySkip param value. If it results in greater than number,
     * it should not be selected into next state for shortest path finding.
     * The MaxActivitySkip should have been set to the number of activities of
     * the most recent shortest path
     * @param marking
     * @return
     */
    private boolean isLessActivitiesThanShortestPath(Set<SequenceFlow> marking,
                                                        StateElementStatus movedElementStatus,
                                                        int currentActivityCount) {
        Set<String> activityNameSet = new HashSet();
        //Collect all immediate and reachable activities from the marking
        //Note: we get all activities on a chain on the model
        for (SequenceFlow flow : marking) {
            for (FlowNode node : helper.getActivityChain((FlowNode)flow.getTargetRef())) {
                activityNameSet.add(node.getName());
            }
        }
        int movedActivityCount = ((movedElementStatus == StateElementStatus.ACTIVITY_SKIPPED) ? 1 : 0);
        
        return ((currentActivityCount + movedActivityCount + activityNameSet.size()) <= params.getCurrentShortestPath());
    }
    
    /**
     * Check if a marking contains a vicious cycle. A vicious cycle has two OR gates
     * wait for input tokens of each other which leads to either
     * deadlock state or lengthy movement.
     * @return
     */
    private boolean containsORViciousCycle(Set<SequenceFlow> marking) {
        //--------------------------------------
        // Get all not enabled OR-join in the marking
        //--------------------------------------
        FlowNode node;
        Set<FlowNode> notEnabledORJoins = new HashSet();
        for (SequenceFlow flow : marking) {
            node = (FlowNode)flow.getTargetRef();
            if (helper.getAllORJoins().contains(node) && !ORJoinEnactmentManager.isEnabled(node, marking)) {
                notEnabledORJoins.add(node);
            }
        }
        
        //--------------------------------------
        // Check vicious cycle existence
        //--------------------------------------
        Set<SequenceFlow> ORNode1WaitingFlows = new HashSet();
        Set<SequenceFlow> ORNode2WaitingFlows = new HashSet();
        FlowNode ORNode2;
        boolean existViciousCycle = false;
        if (notEnabledORJoins.size() >= 2) {
            for (FlowNode ORNode1 : notEnabledORJoins) {
                ORNode1WaitingFlows = ORJoinEnactmentManager.getSequencesWithTokenBeingWaitedFor(ORNode1, marking);
                for (SequenceFlow flow1 : ORNode1WaitingFlows) {
                    ORNode2 = (FlowNode)flow1.getTargetRef();
                    if (helper.getAllORJoins().contains(ORNode2)) {
                        ORNode2WaitingFlows = ORJoinEnactmentManager.getSequencesWithTokenBeingWaitedFor(ORNode2, marking);
                        ORNode2WaitingFlows.retainAll(ORNode1.getIncomingSequenceFlows());
                        if (!ORNode2WaitingFlows.isEmpty()) {
                            existViciousCycle = true;
                            break;
                        }
                    }
                }
            }
        }
        return existViciousCycle;
    }
    
    private boolean containsANDViciousCycle(Set<SequenceFlow> marking) {
        FlowNode node;
        for (SequenceFlow flow : marking) {
            node = (FlowNode)flow.getTargetRef();
            if (helper.getAllJoins().contains(node) && helper.getANDJoinsOnViciousCycles().contains(node)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean belongsToOneViciousCycle(FlowNode nodeAND, FlowNode source) {
        boolean commonCycle = false;
        for (List<FlowNode> cycle : helper.getSimpleCycles()) {
            if (cycle.contains(source) && cycle.contains(nodeAND)) {
                commonCycle = true;
                break;
            }
        }
        return commonCycle;
    }
    
    /**
     *
     * @param state
     * @return
     */
    @Override
    public boolean equals(Object state) {
        if (state == null) {
            return false;
        }
        if (state == this) {
            return true;
        }
        if (state.getClass() != this.getClass()) {
            return false;
        }
        return (markings.equals(((State)state).getMarkings()) && traceIndex == ((State)state).getTraceIndex());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        for (SequenceFlow flow : markings) {
            hash = 23 * hash + Objects.hashCode(flow);
        }
        hash = 23 * hash + this.traceIndex;
        return hash;
    }
    
    public void clear() {
        markings.clear();
        visitedMarkings.clear();
        visitedStates.clear();
        element = null;
        trace = null;
        helper = null;
        params = null;
    }
    
    
   
}
