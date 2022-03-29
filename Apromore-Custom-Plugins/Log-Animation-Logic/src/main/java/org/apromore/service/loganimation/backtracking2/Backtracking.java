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

import de.hpi.bpmn2_0.model.connector.SequenceFlow;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import org.apromore.service.loganimation.replay.BPMNDiagramHelper;
import org.apromore.service.loganimation.replay.LogUtility;
import org.apromore.service.loganimation.replay.ReplayParams;
import org.apromore.service.loganimation.replay.Replayer;
import org.deckfour.xes.model.XTrace;

/**
 * Implement state exploration with backtracking algorithm
 * State is represented as nodes, state space search is a tree
 * @author Bruce Nguyen
 */
public class Backtracking {
    private int maxAllowedUnmatch;
    private double maxAllowedCost;
    private int maxAllowedDepth;
    private int progressSize; //progressive trial size of the trace, increase by TraceChunkSize parameter
    private boolean optimalNodeFound = false; //when optimal node found: full trace played with zero cost
    private boolean fullTraceMatched = false; //total trace is achieved
    private Set<Node> visitedNodes = new HashSet(); //keep all nodes created during exploration
    private Node bestNode = null;
    private Set<Node> completeNodesForShortestPathExploration = new HashSet();
    private Node shortestPathNode = null;
    
    private ReplayParams params;
    private XTrace trace;
    private boolean minMMSearch = false; //differentiate a trace replay or a min move on model exploration
    private BPMNDiagramHelper helper;
    private int totalNodesVisited = 0; //total number of nodes of the state space tree
    
    private static final Logger LOGGER = Logger.getLogger(Replayer.class.getCanonicalName());
    private static String indent = ""; //just for printing to log for debugging
    
    private long startTime;
    
   /**
    * Backtracking algorithm is used to search for a solution on a 
    * state space. The problem space here can be viewed as a tree in which every node
    * is a a choice/decision (partial state) and children nodes are next choices.
    * In this view, a solution is formed from series of choices. The purpose is to 
    * search for the best combination of choices.
    * The algorithm is abstracted through 5 main functions
    * - explore: the core search procedure with backtracking capability
    * - nexts: select next states (limit search space)
    * - reject: evaluate a state and decide if it can be rejected (prune)
    * - accept/collect: evaluate a state and decide to take it or not (prune again)
    * - select: select the best state (solution) 
    */  
    public Backtracking (ReplayParams params, XTrace trace, BPMNDiagramHelper helper) {
        this.params = params;
        this.trace = trace;
        this.minMMSearch = false;
        this.helper = helper;
        indent = "";
    }
    
    public Backtracking (ReplayParams params, BPMNDiagramHelper helper) {
        this.params = params;
        this.minMMSearch = true;
        this.helper = helper;
        indent = "";
    }    
    
   /**
    * Explore token play path for a trace, starting from the Start event node.
    * In case of long trace, to avoid very large state space, the trace can 
    * be played in chunks (progressSize) until the trace is finished
    * In case the trace has been played out but the movement on the model not yet
    * reaches a completion state, extra effort is tried to reach a completion state
    * on the model via a shortest token path. The rest of movement is all gateway and activity_skip.
    * Return the leaf node of the selected path (bestNode)
    * The return path is always equal or longer than the trace alignment
    */
    public Node explore() {
        //-------------------------------------------------------
        // The initial state is taking the start event, one token is 
        // on the outgoing sequence flow of the start event, trace index is 
        // pointing at the first trace event.
        //-------------------------------------------------------
        Node selectedNode = new Node(null, new State(new HashSet<SequenceFlow>(helper.getStartEvent().getOutgoingSequenceFlows()), 
                                              helper.getStartEvent(), 
                                              StateElementStatus.STARTEVENT, 
                                              trace, 0, new HashSet(), new HashSet(), helper, params));
        progressSize = 0;
        double matchRatio;
        startTime = System.currentTimeMillis();
        while (selectedNode != null && progressSize < trace.size()) {
            //------------------------------------
            // Reset parameters for new exploration starting from selected node
            //------------------------------------
            progressSize += params.getTraceChunkSize();
            if (progressSize > trace.size()) {
                progressSize = trace.size();
            }
            //selectedNodes.clear();
            optimalNodeFound = false;
            fullTraceMatched = false;
            maxAllowedUnmatch = (progressSize - Long.valueOf(Math.round(params.getMinMatchPercent()*progressSize)).intValue());
            maxAllowedCost = params.getMaxCost();
            maxAllowedDepth = params.getMaxDepth();
            totalNodesVisited = 0; 
            indent = "";
            
            this.clean(); //clean before next chunk replay starting from bestNode
            
            if (params.isBacktrackingDebug()) {
//                LOGGER.info("TRACE_ID:" + LogUtility.getConceptName(trace));
//                LOGGER.info("MAX_COST:" + params.getMaxCost() + " " +
//                            "MAX_DEPTH:" + params.getMaxDepth() + " " +
//                            "MIN_MATCH:" + params.getMinMatchPercent() + " " +
//                            "MAX_MATCH:" + params.getMaxMatchPercent() + " " +
//                            "MAX_ACTIVITY_SKIP:" + params.getMaxActivitySkipPercent() + " " +
//                            "MAX_CONSECUTIVE_UNMATCH:" + params.getMaxConsecutiveUnmatch()+ " " +
//                            "MAX_NODE_DISTANCE:" + params.getMaxNodeDistance() + " " +
//                            "MAX_NO_OF_NODES_VISITED:" + params.getMaxNumberOfNodesVisited() + " " +
//                            "ACTIVITY_SKIPPED_COST:" + params.getActivitySkipCost() + " " +
//                            "EVENT_SKIPPED_COST:" + params.getEventSkipCost() + " " +
//                            "TRACE_CHUNK_SIZE:" + params.getTraceChunkSize());
            }
            
            this.explore(selectedNode);
            selectedNode = bestNode;
            
            //-----------------------------------------
            // Adjust cost of movement after every chunk 
            // to update the knowledge of the trend. 
            // At the beginning, activity skip cost is set to be lower than 
            // event skip cost based on assumption of high fitness log and model
            // However, after every chunk replay, if there are less activity matches, 
            // then activity skip cost must be increased because it is not good to 
            // skip activity and maybe skip event is a better option.
            // In case of there is no match, activity skip is not a good option at all
            // Similarly, event skip cost should be the same as at the beginning (higher than 
            // activity skip cost) if there is high match, but should be lower if 
            // there is not good match between activity and the model
            //-----------------------------------------   
            /*
            if (selectedNode != null) {
                matchRatio = 1.0*selectedNode.getMatchCount()/progressSize;
                if (matchRatio != 0) {
                    params.setActivitySkipCost(params.getActivitySkipCost()/matchRatio);
                } else {
                    params.setActivitySkipCost(Integer.MAX_VALUE);
                }
                params.setEventSkipCost(params.getEventSkipCost()*matchRatio);
            }
            */
        }
        
        //--------------------------------------------------
        // Continue token play to the end if the state on the model not yet
        // reaches the completion state (proper or improper). The remaining
        // movement does not allow loops, so it only flows towards the End event
        //--------------------------------------------------
        if (selectedNode != null && !selectedNode.getState().isProperCompletion() && !selectedNode.getState().isImproperCompletion()) {
            totalNodesVisited = 0;
            indent = "";
            this.clean(); //clean before next chunk replay starting from bestNode
            params.setCurrentShortestPath(Integer.MAX_VALUE); //maximize this param since it will be set value during exploration
            this.exploreShortestPath(selectedNode);
            if (shortestPathNode != null) {
                bestNode = shortestPathNode;
            }
        }
        
        return bestNode;
    }
    
    /**
     * Explore the token play path for a trace, starting from the input Node
     * @param node: input node 
     * During this exploration, bestNode contains the best node found so far
     */
    public void explore(Node node) {
        if (params.isBacktrackingDebug()) {
//            LOGGER.info(indent + "Entering explore(" +
//                        node.getState().getName()+
//                        " markings:" + node.getState().getMarkingsText()+
//                        " trace:" +  node.getState().getTraceWithIndex()+
//                        " cost:" + node.getCost() +
//                        " depth:" + node.getDepth() +
//                        " matches:" + node.getMatchCount() +
//                        " activityskips:" + node.getActivitySkipCount() +
//                        " consecutiveUnmatch:" + node.getConsecutiveUnmatch() +
//                        " totalUnmatch:" + (node.getState().getTraceIndex() - node.getMatchCount()) +
//                        " totalMissPercent:" + ((1.0*node.getState().getTraceIndex() - node.getMatchCount())/progressSize) +
//                        " nodesVisited:" + totalNodesVisited +
//                        " maxAllowedUnmatch:" + maxAllowedUnmatch +
//                        " maxAllowedCost:" + maxAllowedCost +
//                        " maxAllowedDepth:" + maxAllowedDepth +
//                        " fullTraceMatched:" + fullTraceMatched +
//                        " optimalNodeFound:" + optimalNodeFound +
//                        ")");
            indent = indent + "|  ";
        }
        
        visitedNodes.add(node); //keep track of all nodes for clearance
        totalNodesVisited++;
        
        if (prune(node)) {
            select(node); //update the current best node. select(node) updates pruning conditions, so must put it here, not before.
            if (params.isBacktrackingDebug()) {
                indent = indent.substring(3);
//                LOGGER.info(indent + "node(" + node.getState().getName()+ ") is pruned" + " maxAllowedCost:" + maxAllowedCost +
//                            " maxAllowedDepth:" + maxAllowedDepth + " maxAllowedUnmatch:" + maxAllowedUnmatch +
//                            " fullTracePlayed:" + fullTraceMatched + " optimalNodeFound:" + optimalNodeFound);
            }
            totalNodesVisited--;
            return;
        }
        else if (complete(node)) {
            node.setComplete(true);
            select(node); //update the current best node
            if (params.isBacktrackingDebug()) {
                indent = indent.substring(3);
//                LOGGER.info(indent + "node(" + node.getState().getName()+ ") is complete" + " maxAllowedCost:" + maxAllowedCost +
//                            " maxAllowedDepth:" + maxAllowedDepth + " maxAllowedUnmatch:" + maxAllowedUnmatch +
//                            " fullTracePlayed:" + fullTraceMatched + " optimalNodeFound:" + optimalNodeFound);
            }
            totalNodesVisited--;
            return;
        }
        else {
            select(node); //update the current best node
            Collection<Node> childs = nexts(node);
            for (Iterator<Node> it = childs.iterator(); it.hasNext();) {
                explore(it.next());
            }
        }
        
        if (params.isBacktrackingDebug()) {
            indent = indent.substring(3);
//            LOGGER.info(indent + "End of explore(" + node.getState().getName()+ ")");
        }
        totalNodesVisited--;        
    }
    
    /**
     * Explore a shortest token play path from the Start event node
     * @return: the leaf node of the shortest path (shortestPathNode)
     */
    public Node exploreShortestPath() {
        //----------------------------------------------
        // Starting node is Start Event, one token is on the sequence flow
        // followed the Start Event
        //----------------------------------------------
        Node firstNode = new Node(null, new State(new HashSet<SequenceFlow>(helper.getStartEvent().getOutgoingSequenceFlows()), 
                                              helper.getStartEvent(), 
                                              StateElementStatus.STARTEVENT, 
                                              trace, 0, new HashSet(), new HashSet(), 
                                              helper, params));
        totalNodesVisited = 0; 
        indent = "";
        visitedNodes.clear();
        params.setCurrentShortestPath(Integer.MAX_VALUE); //maximize this param since it will be set value during exploration
        startTime = System.currentTimeMillis();
        this.exploreShortestPath(firstNode);
        //bestNode = this.selectNodeForShortestPathExploration();
        bestNode = shortestPathNode;
        return bestNode;
    }
    
    /**
     * Explore a shortest token play path from the input node to the End event
     * @param node: the start node
     * completeNodesForShortestPathExploration: contains all proper and improper completion state nodes
     */
    public void exploreShortestPath(Node node) {
        if ((minMMSearch && params.isExploreShortestPathDebug()) ||
                (!minMMSearch && params.isBacktrackingDebug())) {
            String prefix = minMMSearch ? "exploreShortestPath" : "exploreToEndEvent";
//            LOGGER.info(indent + "Entering " + prefix + "(" +
//                        node.getState().getName()+
//                        " markings:" + node.getState().getMarkingsText()+
//                        " nodesVisited:" + totalNodesVisited +
//                        ")");
            indent = indent + "|  ";
        }
        
        visitedNodes.add(node); //keep track of all nodes for clearance
        totalNodesVisited++;
        long spanTime = System.currentTimeMillis() - startTime;
        
        if ((minMMSearch && (spanTime/1000) > params.getMaxTimeShortestPathExploration()) ||
            (!minMMSearch && (spanTime/1000) > params.getMaxTimePerTrace())) {
            if ((minMMSearch && params.isExploreShortestPathDebug()) ||
                (!minMMSearch && params.isBacktrackingDebug())) {
                indent = indent.substring(3);
//                LOGGER.info(indent + "node(" + node.getState().getName()+ "): stops here due to timeover");
            }
            totalNodesVisited--;
            return;
        }
        else if (shortestPathNode!=null && node.getActivitySkipCount() > shortestPathNode.getActivitySkipCount()) {
            if ((minMMSearch && params.isExploreShortestPathDebug()) ||
                (!minMMSearch && params.isBacktrackingDebug())) {
                indent = indent.substring(3);
//                LOGGER.info(indent + "node(" + node.getState().getName()+ ") is longer than current shortest node!");
            }    
            totalNodesVisited--;
            return;
        }
        else if (node.getState().isProperCompletion() || node.getState().isImproperCompletion()) {
            if ((shortestPathNode == null) || 
                (shortestPathNode != null && node.getActivitySkipCount() < shortestPathNode.getActivitySkipCount())) {
                shortestPathNode = node;
                params.setCurrentShortestPath(node.getActivitySkipCount()); //for pruning in state generation
            }
            
            if ((minMMSearch && params.isExploreShortestPathDebug()) ||
                (!minMMSearch && params.isBacktrackingDebug())) {
                indent = indent.substring(3);
//                LOGGER.info(indent + "node(" + node.getState().getName()+ ") is a completion!");
            }
            totalNodesVisited--;
            return;
        }
        else {
            Collection<Node> childs = nextNodesForShortestPathFinding(node);
            if (childs.size() > 0) {
                for (Iterator<Node> it = childs.iterator(); it.hasNext();) {
                    exploreShortestPath(it.next());
                }
            }
            else {
                if ((minMMSearch && params.isExploreShortestPathDebug()) ||
                    (!minMMSearch && params.isBacktrackingDebug())) {
//                    LOGGER.info(indent + "node(" + node.getState().getName()+ ") leads to a loop or deadlock");
                }
            }
        }
        
        if ((minMMSearch && params.isExploreShortestPathDebug()) ||
            (!minMMSearch && params.isBacktrackingDebug())) {
            indent = indent.substring(3);
//            LOGGER.info(indent + "End of explore(" + node.getState().getName()+ ")");
        }
        totalNodesVisited--;
    }
    
    /**
     * Return list of next nodes to explore
     * This list is prioritized to select the best node leading to highest match
     */
    private Collection<Node> nexts(Node node) {
        Set<Node> nextNodes = node.getChildNodes();
        /*
        if (params.isBacktrackingDebug()) {
            Set<Node> removedNodes = (Set)((HashSet)visitedNodes).clone();
            removedNodes.retainAll(nextNodes);
            for (Node removedNode : removedNodes) {
                LOGGER.info(indent + "Visited: " + removedNode.getState().getName()+ 
                        " markings:" + removedNode.getState().getMarkingsText()+ 
                        " trace:" +  removedNode.getState().getTraceWithIndex());
            }
        } 
        nextNodes.removeAll(visitedNodes);
        */
        Set<Node> toRemove = new HashSet();
        for (Node nextNode : nextNodes) {
            for (Node visitedNode : visitedNodes) {
                if (visitedNode.equals(nextNode) && visitedNode.isBetterOrEqual(nextNode)) {
                    toRemove.add(nextNode);
                    break;
                }
            }
        }
        if (!toRemove.isEmpty() && params.isBacktrackingDebug()) {
            for (Node removedNode : toRemove) {
//                LOGGER.info(indent + "Removed node: " + removedNode.getState().getName()+
//                        " markings:" + removedNode.getState().getMarkingsText()+
//                        " trace:" +  removedNode.getState().getTraceWithIndex());
            }
        }
        nextNodes.removeAll(toRemove);
        return nextNodes;
    }
    
    private Collection<Node> nextNodesForShortestPathFinding(Node node) {
        Collection<Node> nextNodes = node.getChildNodesForShortestPathFinding();
        //nextNodes.removeAll(visitedNodes);
        Set<Node> toRemove = new HashSet();
        for (Node nextNode : nextNodes) {
            for (Node visitedNode : visitedNodes) {
                if (visitedNode.equals(nextNode) && visitedNode.isShorterOrEqual(nextNode)) {
                    toRemove.add(nextNode);
                    break;
                }
            }
        }   
        if (!toRemove.isEmpty() && params.isExploreShortestPathDebug()) {
            for (Node removedNode : toRemove) {
//                LOGGER.info(indent + "Removed node: " + removedNode.getState().getName()+
//                        " markings:" + removedNode.getState().getMarkingsText());
            }
        }        
        nextNodes.removeAll(toRemove);
        return nextNodes;
    }
    
    /**
     * Reject a node and prune the tree from the node as root
     */
    private boolean prune(Node node) {
        boolean rejected = false;
        double totalUnmatch = node.getState().getTraceIndex() - node.getMatchCount();
        long timespan = System.currentTimeMillis() - startTime;

        rejected =  optimalNodeFound ||
                    node.getCost() > maxAllowedCost || 
                    node.getDepth() > maxAllowedDepth ||
                    totalUnmatch > maxAllowedUnmatch ||
                    node.getConsecutiveUnmatch() > params.getMaxConsecutiveUnmatch() ||
                    node.getActivitySkipCount() > 1.0*params.getMaxActivitySkipPercent()*trace.size() ||
                    totalNodesVisited > params.getMaxNumberOfNodesVisited() ||
                    1.0*timespan/1000 > params.getMaxTimePerTrace();
        return rejected;
    }
    
    /*
     * Accept a node and prune the tree from the node as root
     */
    private boolean complete(Node node) {
        if (node.getMatchCount() >= 1.0*params.getMaxMatchPercent()*progressSize && node.getCost() == 0) {
            optimalNodeFound = true;
            fullTraceMatched = true;
            return true;
        }
        else return node.getCost() <= params.getMaxCost() &&
                node.getState().getTraceIndex() >= progressSize &&
                node.getMatchCount() >= 1.0 * params.getMinMatchPercent() * progressSize;
    }
    
    
    
    /*
     *  Select between the bestNode and the input node
     */
    private void select(Node node) {
        /**
         * Select node if bestNode is null or node is better than bestNode or
         * bestNode is not complete and node is complete
         */
        if (bestNode == null ||
           (bestNode != null && !bestNode.isComplete() && node.isComplete()) ||
           (bestNode != null && !bestNode.isComplete() && !node.isComplete() && node.isBetter(bestNode)) ||
           (bestNode != null && bestNode.isComplete() && node.isComplete() && node.isBetter(bestNode))) {

            bestNode = node;

            //---------------------------------------------
            // Set new selection criteria based on the best node so far.
            // Future nodes not meeting these criteria will be rejected
            //---------------------------------------------
            if ((progressSize - node.getMatchCount()) < maxAllowedUnmatch) {
                maxAllowedUnmatch = progressSize - node.getMatchCount();
            }

            //When trace has been fully played, all other nodes must be able 
            //to play full trace at better cost and depth. The cost and depth should
            //be the earliest value that could achieve, not the values at current node.
            if (node.getMatchCount() == progressSize) {
                fullTraceMatched = true;
                Node parent = node;
                while (parent != null) {
                    if (maxAllowedCost > parent.getCost()) {
                        maxAllowedCost = parent.getCost();
                    }
                    if (maxAllowedDepth > parent.getDepth()) {
                        maxAllowedDepth = parent.getDepth();
                    }
                    if (parent.getState().getElementStatus() == StateElementStatus.ACTIVITY_MATCHED) {
                        parent = parent.getParent();
                    } else {
                        break;
                    }
                }
            }  
        }
    }
    
    
    
    /**
     * Select the node with least cost and least depth from
     * completeNodesForShortestPathExploration
     * @return selected node or null
     */
    public Node selectNodeForShortestPathExploration() {
        Node selectedNode = null;
        double minCost = Integer.MAX_VALUE;
        double minDepth = Integer.MAX_VALUE;
        for (Node node : completeNodesForShortestPathExploration) { 
            if (node.getCost() < minCost) {
                selectedNode = node;
                minCost = node.getCost();
                minDepth = node.getDepth();
            }
            else if (node.getCost() == minCost) {
                if (node.getDepth() < minDepth) {
                    selectedNode = node;
                    minDepth = node.getDepth();
                    minCost = node.getCost();
                }
            }
        }
        
        if (selectedNode != null && params.isBacktrackingDebug()) {
//            LOGGER.info("Replayed Path: " + selectedNode.getPathString());
        }
        
        return selectedNode;
    }
    
    /**
     * Clean visitedNodes and the contained nodes except nodes on 
     * the bestNode to the root node.
     */
    private void clean() {
        Set<Node> selectedNodes = new HashSet();
        Node node = bestNode;
        while (node!=null) {
            selectedNodes.add(node);
            node = node.getParent();
        }
        for (Node generatedNode : visitedNodes) {
            if (!selectedNodes.contains(generatedNode)) {
                generatedNode.clear();
            }
        }
        visitedNodes.clear();        
    }
    
    /**
     * Clean all unselected nodes created after backtracking process
     * to avoid memory leaks
     */
    public void clear() {
        clean();
        completeNodesForShortestPathExploration.clear();
        params = null;
        trace = null;
        helper = null;
    }

}
