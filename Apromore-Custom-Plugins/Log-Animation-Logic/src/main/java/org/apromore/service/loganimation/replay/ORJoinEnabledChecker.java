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

package org.apromore.service.loganimation.replay;

import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apromore.service.loganimation.backtracking2.State;

/*
* Manage enabledness of one OR-join following Hagen Volzer's algorithm for OR-Join
* This class implements the four main algorithms of OR-join enactment paper
* These algorithms are implemented in the same method name as the paper: preProcess, init, update and query
* The mapping is as follows: every edge of the model is mapped to a set of incoming flows of the OR-Join that
* the edge can reach, each unique set is called a label (several edges can have the same label).
* At run-time of checking enabledness, based on the incoming flows of the OR-join, 
* those labels containing at least one non-empty incoming flows would be marked as BLUE, 
* those labels not containing any non-empty incoming flows would be marked as RED. So, any edges 
* in the process model attached with a BLUE label is considered as BLUE edge, and with RED label is a RED edge.
* If any RED edge has a token, then the OR-join must wait. If all RED edge has no token, the OR-join is enabled.
*/
public class ORJoinEnabledChecker {
    private final FlowNode orNode;
    //map sequence flow Id to ORJoinLabel, for every sequence flows in the model
    private final Map<SequenceFlow, ORJoinLabel> edgeToLabelMap = new HashMap<>(); 
    private boolean someTokenArrived = false;
    private int nonZeroNonIgnoredCounters = 0;
    private boolean hasBeenPreProcessed = false;
    private final BPMNDiagramHelper helper;
    
    public ORJoinEnabledChecker(FlowNode orNode, BPMNDiagramHelper helper) {
        this.orNode = orNode;
        this.helper = helper;
        this.preProcess();
        this.init();
    }
    
    public FlowNode getORNode() {
        return this.orNode;
    }
    
    public Map<SequenceFlow,ORJoinLabel> getLabelMap() {
        return edgeToLabelMap;
    }
    
    public boolean someTokenArrived() {
        return this.someTokenArrived;
    }
    
    public void setTokenArrived(boolean tokenArrived) {
        this.someTokenArrived = tokenArrived;
    }
    
    public int getNonZeroNonIgnored() {
        return this.nonZeroNonIgnoredCounters;
    }
    
    /*
    * Create ORJoinLabel for sequence flow in the process model
    * Result: edgeToLabelMap contains mapping from sequence flow Id to ORJoinLabel
    * Note: those sequence flow with empty set or fully contains all incoming
    * sequence flows of the OR Join will be omitted. They don't affect the enabledness of OR-join
    * Also note that multiple sequence flows can be mapped to the same ORJoinLabel object
    * Sequence flows cannot access to any OR-Join sources are not included in edgeToLabelMap
    * How: traverse all nodes of the model, except the OR-join and its source nodes
    *      Use Dijkstra to check each node if it can reach any of the OR-join source nodes and collect flows into set
    *      Then, those sequence flows that has a node as target also gets that set
    */
    private void preProcess() {
        hasBeenPreProcessed = true; 
        Map<SequenceFlow, Set<SequenceFlow>> orInputMap = new HashMap<>();
        Map<SequenceFlow, Set<SequenceFlow>> flowSetMap = new HashMap<>();

        //orInputMap contains mapping from OR input flow to set of sequence flows that can reach it
        Set<SequenceFlow> flowSet;
        for (SequenceFlow incoming : this.orNode.getIncomingSequenceFlows()) {
            flowSet = new HashSet();
            helper.getSourceEdges(incoming, this.orNode, flowSet);
            orInputMap.put(incoming, flowSet);
        }
        
        //flowSetMap contains mapping from a flow to set of OR input flows it can reach
        for (SequenceFlow flow : helper.getAllSequenceFlows()) {
            for (SequenceFlow orinputFlow : orInputMap.keySet()) {
                if (orInputMap.get(orinputFlow).contains(flow)) {
                    if (!flowSetMap.containsKey(flow)) {
                        flowSetMap.put(flow, new HashSet());
                    }
                    flowSetMap.get(flow).add(orinputFlow);
                }
            }
        }
        
        //----------------------------------------------------------------------
        //Unify sequence flow set since visited can contain duplicate sequence flow set
        //i.e., multiple flows (key) can have similar sequence set. They should point to 
        //one ORJoinLabel object. Then, add a mapping from the sequence flow to the ORJoinLabel object
        //----------------------------------------------------------------------
        ORJoinLabel foundLabel;
        for (SequenceFlow flow : flowSetMap.keySet()) {
            foundLabel = null;
            for (ORJoinLabel label : this.edgeToLabelMap.values()) {
                if (label.getEdges().equals(flowSetMap.get(flow))) { //set equals means same size and same elements
                    foundLabel = label;
                    break;
                }
            }
            if (foundLabel == null) {
                this.edgeToLabelMap.put(flow, new ORJoinLabel(flowSetMap.get(flow)));
            } else {
                this.edgeToLabelMap.put(flow, foundLabel);
            }
        }
       
    }
    

    
    private void init() {
        if (!hasBeenPreProcessed) {
            this.preProcess();
        }
        this.someTokenArrived = false;
        this.nonZeroNonIgnoredCounters = 0;
    }
    
    /*
    * Update the checker state when there are changes of tokens on edges during replay
    */
    public void update(Map<SequenceFlow,Integer> edgeToTokenChanges) {
        ORJoinCounter counter;
        
        //Update status from the changing number of tokens on every edge
        for (SequenceFlow flow : edgeToTokenChanges.keySet()) {
            if (this.edgeToLabelMap.containsKey(flow.getId())) {
                counter = this.edgeToLabelMap.get(flow.getId()).getCounter();

                //Update counter with the number of token changes
                if (counter.getValue() == 0 && !counter.isIgnored() && edgeToTokenChanges.get(flow) > 0) {
                    this.nonZeroNonIgnoredCounters++;
                }
                counter.setValue(counter.getValue() + edgeToTokenChanges.get(flow));

                //In case a token arrives at an incoming edge of this OR-Join
                if (edgeToTokenChanges.get(flow) == 1 && this.orNode.getIncomingSequenceFlows().contains(flow)) {
                    this.someTokenArrived = true;
                    for (ORJoinLabel label : this.edgeToLabelMap.values()) {
                        if (!label.getCounter().isIgnored()) {
                            if (label.contains(flow)) {
                                label.getCounter().setIgnored(true);
                                if (label.getCounter().getValue() > 0) {
                                    this.nonZeroNonIgnoredCounters--;
                                }
                            }
                        }
                    }
                }

                //In case the update shows that this OR-Join has been executed
                if (edgeToTokenChanges.get(flow) == 1 && this.orNode.getOutgoingSequenceFlows().contains(flow)) {
                    this.someTokenArrived = false;
                    for (ORJoinLabel label : this.edgeToLabelMap.values()) {
                        if (label.getCounter().isIgnored()) {
                            if (label.getCounter().getValue() > 0) {
                                this.nonZeroNonIgnoredCounters++;
                                label.getCounter().setIgnored(false);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public boolean query() {
        return (this.getNonZeroNonIgnored()==0 && this.someTokenArrived());
    }
    
    /**
     * Check state of OR-join given the input process marking
     * @param marking
     * @return true if enabled, else false
     */
    public boolean query(Set<SequenceFlow> processMarking) {
        //--------------------------------------------------
        // Specify filled and empty slots (incoming sequences 
        // with or without any tokens to this OR-join)
        //--------------------------------------------------
        Set<SequenceFlow> filledSlots = new HashSet(); // filledSlots are incoming edges of OR-Join having at least one token
        Set<SequenceFlow> emptySlots = new HashSet();  // emptySlots slots are incoming edges of OR-Join having no tokens
        for (SequenceFlow incoming : this.orNode.getIncomingSequenceFlows()) {
            if (processMarking.contains(incoming)) {
                filledSlots.add(incoming);
            }
            else {
                emptySlots.add(incoming);
            }
        }
        
        //--------------------------------------------------
        // Color edges, start with edge containing filled slots (blue) and then
        // those containing empty slots (red)
        //--------------------------------------------------
        Set<SequenceFlow> temp;
        for (ORJoinLabel label : edgeToLabelMap.values()) {
            temp = new HashSet(label.getEdges());
            temp.retainAll(filledSlots);
            if (!temp.isEmpty()) {
                label.setColor(ORJoinLabelColor.BLUE);
            }
            else {
                label.setColor(ORJoinLabelColor.RED);
            }
        }
        
        //--------------------------------------------------
        // Check if there are any red labels with tokens. If yes, it means
        // the OR-join needs to wait. Otherwise, Or-join is enabled
        //--------------------------------------------------
        boolean enabled = true;
        for (SequenceFlow flowWithToken : processMarking) {
            if (this.edgeToLabelMap.containsKey(flowWithToken)) {
                if (this.edgeToLabelMap.get(flowWithToken).isRed()) {
                    enabled = false;
                    break;
                }
            }
        }
        return enabled;
    }
    
    /**
     * Assume that query(State) has been called to color all labels of this OR-join
     * Get all sequence flows containing tokens that this OR-join is waiting
     * @param state: the current state of model
     * @return all sequence flows in red (those with tokens and connected to red labels)
     * or exceptions or unclear result if query(State) has not been called
     */
    public Set<SequenceFlow> getSequenceFlowsInRed(Set<SequenceFlow> processMarking) {
        Set<SequenceFlow> redFlows = new HashSet();
        for (SequenceFlow flowWithToken : processMarking) {
            if (this.edgeToLabelMap.containsKey(flowWithToken)) {
                if (this.edgeToLabelMap.get(flowWithToken).isRed()) {
                    redFlows.add(flowWithToken);
                }
            }
        }
        return redFlows;
    }
    
    public void reset() {
        someTokenArrived = false;
        nonZeroNonIgnoredCounters = 0;
        hasBeenPreProcessed = false;
        for (ORJoinLabel label : this.edgeToLabelMap.values()) {
            label.reset();
        }
    }
}
