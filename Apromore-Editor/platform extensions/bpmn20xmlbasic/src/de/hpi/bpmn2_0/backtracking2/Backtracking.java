package de.hpi.bpmn2_0.backtracking2;

import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import de.hpi.bpmn2_0.replay.BPMNDiagramHelper;
import de.hpi.bpmn2_0.replay.LogUtility;
import de.hpi.bpmn2_0.replay.ReplayParams;
import de.hpi.bpmn2_0.replay.Replayer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.logging.Logger;
import org.deckfour.xes.model.XTrace;

public class Backtracking {
    private int maxAllowedMiss;
    private double maxAllowedCost;
    private int maxAllowedDepth;
    private double bestProgressMark;
    private int progressSize; //progressive trial size of the trace, increase by TraceChunkSize parameter
    
    private boolean optimumFound = false; //when optimum node found: full trace played with zero cost
    private boolean fullTraceMatched = false; //total trace is achieved
    private Collection<Node> selectedNodes = null; // to contain selected nodes after finishing exploration
    
    private ReplayParams params;
    private XTrace trace;
    private BPMNDiagramHelper helper;
    private int totalNodesCount = 0; //total number of nodes of the state space tree
    
    private static final Logger LOGGER = Logger.getLogger(Replayer.class.getCanonicalName());
    private static String indent = ""; //just for printing to log for debugging
    
    /*
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
    * - select: select the best state (solution) after the exploration
    */  
    public Backtracking (ReplayParams params, XTrace trace, BPMNDiagramHelper helper) {
        this.params = params;
        this.trace = trace;
        this.helper = helper;
        selectedNodes = new HashSet(); 
        indent = "";
    }
    
    /*
    * Explore long trace by chunk. 
    * At beginning, it is one starting node.    
    * selectedNodes contain nodes selected after every chunk.
    * Select the best nodes for next exploration after finishing every chunk
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
                                              trace, 0, helper, params));
        progressSize = 0;
        double matchRatio;
        while (selectedNode != null && progressSize < trace.size()) {
            //------------------------------------
            // Reset parameters for new exploration starting from selected node
            //------------------------------------
            progressSize += params.getTraceChunkSize();
            if (progressSize > trace.size()) {
                progressSize = trace.size();
            }
            selectedNodes.clear();
            optimumFound = false;
            fullTraceMatched = false;
            maxAllowedMiss = (progressSize - Long.valueOf(Math.round(params.getMinMatch()*progressSize)).intValue());
            maxAllowedCost = params.getMaxCost();
            maxAllowedDepth = params.getMaxDepth();
            totalNodesCount = 1; //the first node
            
            if (params.isBacktrackingDebug()) {
                LOGGER.info("TRACE_ID:" + LogUtility.getConceptName(trace));
                LOGGER.info("MAX_COST:" + params.getMaxCost() + " " +
                            "MAX_DEPTH:" + params.getMaxDepth() + " " +
                            "MIN_MATCH:" + params.getMinMatch() + " " +
                            "MAX_MATCH:" + params.getMaxMatch() + " " +
                            "MIN_FITNESS:" + params.getMinFitness() + " " +
                            "MAX_DIFFSERIES:" + params.getMaxDiffSeries() + " " +
                            "ACTIVITY_SKIPPED:" + params.getActivitySkipCost() + " " +
                            "EVENT_SKIPPED:" + params.getEventSkipCost() + " " +
                            "TRACE_CHUNK_SIZE:" + params.getTraceChunkSize() + " " + 
                            "MAX_ACTIVITY_SKIP:" + params.getMaxActivitySkip() + " " + 
                            "MAX_NODE_DISTANCE:" + params.getMaxNodeDistance());
            }
            
            this.explore(selectedNode);
            selectedNode = this.select();
            
            if (selectedNode != null) {
                matchRatio = 1.0*selectedNode.getMatchCount()/progressSize;
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
                if (matchRatio != 0) {
                    params.setActivitySkipCost(params.getActivitySkipCost()/matchRatio);
                } else {
                    params.setActivitySkipCost(Integer.MAX_VALUE);
                }
                params.setEventSkipCost(params.getEventSkipCost()*matchRatio);
            }
        }
        return selectedNode;
    }
        
    public void explore(Node node) {
        Node nextNode;
        if (params.isBacktrackingDebug()) {
            LOGGER.info(indent + "Entering explore(" + 
                        node.getState().getName()+ 
                        " markings:" + node.getState().getMarkingsText()+ 
                        " trace:" +  node.getState().getTraceWithIndex()+ 
                        " cost:" + node.getCost() + 
                        " depth:" + node.getDepth() + 
                        " matches:" + node.getMatchCount() + 
                        " activityskips:" + node.getActivitySkipCount() + 
                        " diffseries:" + node.getDiffSeries() + 
                        " totalMiss:" + (node.getState().getTraceIndex() - node.getMatchCount()) + 
                        " totalMissPercent:" + ((1.0*node.getState().getTraceIndex() - node.getMatchCount())/progressSize) +
                        " nodesCount:" + totalNodesCount + 
                        " selectedNodesCount:" + selectedNodes.size() +                 
                        " maxAllowedMiss:" + maxAllowedMiss +
                        " maxAllowedCost:" + maxAllowedCost +
                        " maxAllowedDepth:" + maxAllowedDepth + 
                        " fullTraceMatched:" + fullTraceMatched + 
                        " optimumFound:" + optimumFound + 
                        ")");
        }
        indent = indent + "|  ";
        
        if (reject(node)) {
            if (params.isBacktrackingDebug()) {
                indent = indent.substring(3);
                LOGGER.info(indent + "node(" + node.getState().getName()+ ") is rejected");
            }
            totalNodesCount--;
            return;
        }
        else if (accept(node)) {
            collect(node);
            
            if (params.isBacktrackingDebug()) {
                indent = indent.substring(3);
                LOGGER.info(indent + "node(" + node.getState().getName()+ ") is accepted" + " maxAllowedCost:" + maxAllowedCost +
                            " maxAllowedDepth:" + maxAllowedDepth + " fullTracePlayed:" + fullTraceMatched + " optimumFound:" + optimumFound);
            }
            totalNodesCount--;
            return;
        }
        else {
            Collection<Node> childs = nexts(node);
            totalNodesCount += (childs.size()-1);
            for (Iterator<Node> it = childs.iterator(); it.hasNext();) {
                nextNode = it.next();
                if (!optimumFound && selectedNodes.size() <= 100) {
                    explore(nextNode);
                }
                else if (selectedNodes.size() > 100) {
                    totalNodesCount--;
                    if (params.isBacktrackingDebug()) {
                        indent = indent.substring(3);
                        LOGGER.info(indent + "Stop due to excessive selected nodes found. SelectedNodes: " + selectedNodes.size());
                    }
                    return;
                }
                else {
                    totalNodesCount--;
                    if (params.isBacktrackingDebug()) {
                        indent = indent.substring(3);
                        LOGGER.info(indent + "Stop since optimum node found! NodesCount:" + totalNodesCount);
                    }
                    return;
                }
            }
        }
        
        if (params.isBacktrackingDebug()) {
            indent = indent.substring(3);
            LOGGER.info(indent + "explore(" + node.getState().getName()+ ")");
        }
    }
    
    //Simply get all child nodes but can apply further improvement here
    private Collection<Node> nexts(Node node) {
        return node.getChildren();
    }
    
    private boolean reject(Node node) {
        boolean rejected = false;
        double totalMiss = node.getState().getTraceIndex() - node.getMatchCount();
        double totalMissPercent = 1.0*totalMiss/progressSize;
        double progress;
        if (node.getState().getTraceIndex() > 0) {
            progress = 1.0*(2*node.getMatchCount() - node.getState().getTraceIndex())/node.getState().getTraceIndex();
        }
        else {
            progress = bestProgressMark;
        }
        rejected =  node.getCost() > params.getMaxCost() || 
                    node.getDepth() > params.getMaxDepth() ||
                    node.getActivitySkipCount() > 1.0*params.getMaxActivitySkip()*helper.getActivities().size() ||
                    totalMiss > maxAllowedMiss ||
                    (totalMissPercent > (1 - params.getMinFitness())) ||
                    node.getDiffSeries() > params.getMaxDiffSeries() ||
                    (fullTraceMatched && (node.getCost() >= maxAllowedCost || node.getDepth() > maxAllowedDepth));
                    //progress < (bestProgressMark-0.1)
        return rejected;
    }
    
    private boolean accept(Node node) {
        if (node.getMatchCount() >= 1.0*params.getMaxMatch()*progressSize && node.getCost() == 0) {
            optimumFound = true;
            fullTraceMatched = true;
            return true;
        }
        else if (node.getCost() <= params.getMaxCost() && 
                 node.getState().getTraceIndex() >= progressSize && 
                 node.getMatchCount() >= 1.0*params.getMinMatch()*progressSize) {
            
            if ((progressSize - node.getMatchCount()) < maxAllowedMiss) {
                maxAllowedMiss = progressSize - node.getMatchCount();
            }
            
            //--------------------------------------------------------------------------
            //When trace is fully played, all other nodes must be able 
            //to play full trace at better cost and depth. The cost and depth should
            //be the earliest value that could achieve, not the values at current node.
            //--------------------------------------------------------------------------
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
            
            //------------------------------------------------------------------
            // Progress Mark
            //------------------------------------------------------------------
            double nodeProgress = 1.0*(2*node.getMatchCount() - progressSize)/progressSize;
            if (bestProgressMark < nodeProgress) {
                bestProgressMark = nodeProgress;
            }
            
            return true;
        }
        else {
            return false;
        }
    }
    
    private void collect(Node node) {
        selectedNodes.add(node);
    }
    
    //Select node with highest hits, lowest cost and lowest depth in that priority order
    //Return null if no nodes found in selectedNodes
    public Node select() {
        Node selectedNode = null;
        double minCost = Integer.MAX_VALUE;
        double minDepth = Integer.MAX_VALUE;
        int maxMatch = Integer.MIN_VALUE;
        for (Node node : selectedNodes) { 
            if (node.getMatchCount() > maxMatch) {
                selectedNode = node;
                maxMatch = node.getMatchCount();
                minCost = node.getCost();
                minDepth = node.getDepth();
            }
            else if (node.getMatchCount() == maxMatch) {
                if (node.getCost() < minCost) {
                    selectedNode = node;
                    minCost = node.getCost();
                    maxMatch = node.getMatchCount();
                    minDepth = node.getDepth();
                }
                else if (node.getCost() == minCost) {
                    if (node.getDepth() < minDepth) {
                        minDepth = node.getDepth();
                        minCost = node.getCost();
                        maxMatch = node.getMatchCount();
                    }
                }
            }
        }
        
        if (selectedNode != null && params.isBacktrackingDebug()) {
            LOGGER.info("Replayed Path: " + selectedNode.getPathString());
        }
        
        return selectedNode;
    }

}