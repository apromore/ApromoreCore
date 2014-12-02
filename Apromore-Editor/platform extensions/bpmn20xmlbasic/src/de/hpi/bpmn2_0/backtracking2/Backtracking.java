package de.hpi.bpmn2_0.backtracking2;

import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import de.hpi.bpmn2_0.replay.BPMNDiagramHelper;
import de.hpi.bpmn2_0.replay.LogUtility;
import de.hpi.bpmn2_0.replay.ReplayParams;
import de.hpi.bpmn2_0.replay.Replayer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
import org.deckfour.xes.model.XTrace;

public class Backtracking {
    private int maxAllowedUnmatch;
    private double maxAllowedCost;
    private int maxAllowedDepth;
    private int progressSize; //progressive trial size of the trace, increase by TraceChunkSize parameter
    private boolean optimalNodeFound = false; //when optimal node found: full trace played with zero cost
    private boolean fullTraceMatched = false; //total trace is achieved
    private Set<Node> generatedNodes = new HashSet(); //keep all nodes created during exploration
    private Node bestNode = null;
    private Set<Node> completeNodesForShortestPathExploration = new HashSet();
    private Node shortestPathNode = null;
    
    private ReplayParams params;
    private XTrace trace;
    private BPMNDiagramHelper helper;
    private int totalNodesVisited = 0; //total number of nodes of the state space tree
    
    private static final Logger LOGGER = Logger.getLogger(Replayer.class.getCanonicalName());
    private static String indent = ""; //just for printing to log for debugging
    
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
        this.helper = helper;
        indent = "";
    }
    
    public Backtracking (ReplayParams params, BPMNDiagramHelper helper) {
        this.params = params;
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
    * Return the leaf node of the selected path
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
            //selectedNodes.clear();
            optimalNodeFound = false;
            fullTraceMatched = false;
            maxAllowedUnmatch = (progressSize - Long.valueOf(Math.round(params.getMinMatch()*progressSize)).intValue());
            maxAllowedCost = params.getMaxCost();
            maxAllowedDepth = params.getMaxDepth();
            totalNodesVisited = 0; 
            indent = "";
            
            if (params.isBacktrackingDebug()) {
                LOGGER.info("TRACE_ID:" + LogUtility.getConceptName(trace));
                LOGGER.info("MAX_COST:" + params.getMaxCost() + " " +
                            "MAX_DEPTH:" + params.getMaxDepth() + " " +
                            "MIN_MATCH:" + params.getMinMatch() + " " +
                            "MAX_MATCH:" + params.getMaxMatch() + " " +
                            "MAX_ACTIVITY_SKIP:" + params.getMaxActivitySkip() + " " +                         
                            "MAX_DIFFSERIES:" + params.getMaxDiffSeries() + " " +
                            "MAX_NODE_DISTANCE:" + params.getMaxNodeDistance() + " " + 
                            "MAX_NO_OF_NODES_VISITED:" + params.getMaxNumberOfNodesVisited() + " " +
                            "ACTIVITY_SKIPPED_COST:" + params.getActivitySkipCost() + " " +
                            "EVENT_SKIPPED_COST:" + params.getEventSkipCost() + " " +
                            "TRACE_CHUNK_SIZE:" + params.getTraceChunkSize());                        
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
        Node completionNode=null;
        if (selectedNode != null && !selectedNode.getState().isProperCompletion() && !selectedNode.getState().isImproperCompletion()) {
            totalNodesVisited = 0;
            indent = "";
            this.exploreShortestPath(selectedNode);
            //bestNode = this.selectNodeForShortestPathExploration();
            bestNode = shortestPathNode;
            completionNode = bestNode;
        }
        
        if (completionNode != null) {
            return completionNode;
        }
        else {
            return selectedNode;
        }
    }
    
    /**
     * Explore the token play path for a trace, starting from the input Node
     * @param node: input node
     * During this exploration, bestNode contains the best node found so far
     */
    public void explore(Node node) {
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
                        " totalUnmatch:" + (node.getState().getTraceIndex() - node.getMatchCount()) + 
                        " totalMissPercent:" + ((1.0*node.getState().getTraceIndex() - node.getMatchCount())/progressSize) +
                        " nodesVisited:" + totalNodesVisited + 
                        " maxAllowedUnmatch:" + maxAllowedUnmatch +
                        " maxAllowedCost:" + maxAllowedCost +
                        " maxAllowedDepth:" + maxAllowedDepth + 
                        " fullTraceMatched:" + fullTraceMatched + 
                        " optimalNodeFound:" + optimalNodeFound + 
                        ")");
            indent = indent + "|  ";
        }
        
        generatedNodes.add(node); //keep track of all nodes for clearance
        totalNodesVisited++;
        
        if (reject(node)) {
            if (params.isBacktrackingDebug()) {
                indent = indent.substring(3);
                LOGGER.info(indent + "node(" + node.getState().getName()+ ") is rejected");
            }
            totalNodesVisited--;
            return;
        }
        else if (accept(node)) {
            select(node); //update the current best node
            
            if (params.isBacktrackingDebug() && (bestNode == node)) {
                indent = indent.substring(3);
                LOGGER.info(indent + "node(" + node.getState().getName()+ ") is accepted" + " maxAllowedCost:" + maxAllowedCost +
                            " maxAllowedDepth:" + maxAllowedDepth + " fullTracePlayed:" + fullTraceMatched + " optimalNodeFound:" + optimalNodeFound);
            }
            totalNodesVisited--;
            return;
        }
        else {
            select(node); //update the current best node, even if it is not accepted yet
            Collection<Node> childs = nexts(node);
            for (Iterator<Node> it = childs.iterator(); it.hasNext();) {
                explore(it.next());
            }
        }
        
        totalNodesVisited--;
        if (params.isBacktrackingDebug()) {
            indent = indent.substring(3);
            LOGGER.info(indent + "explore(" + node.getState().getName()+ ")");
        }
    }
    
    /**
     * Explore a shortest token play path from the Start event node
     * @return: the leaf node of the shortest path
     */
    public Node exploreShortestPath() {
        //----------------------------------------------
        // Starting node is Start Event, one token is on the sequence flow
        // followed the Start Event
        //----------------------------------------------
        Node firstNode = new Node(null, new State(new HashSet<SequenceFlow>(helper.getStartEvent().getOutgoingSequenceFlows()), 
                                              helper.getStartEvent(), 
                                              StateElementStatus.STARTEVENT, 
                                              trace, 0, helper, params));
        totalNodesVisited = 0; 
        indent = "";
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
        if (params.isBacktrackingDebug()) {
            LOGGER.info(indent + "Entering explorePathToEnd(" + 
                        node.getState().getName()+ 
                        " markings:" + node.getState().getMarkingsText()+ 
                        " nodesVisited:" + totalNodesVisited + 
                        ")");
            indent = indent + "|  ";
        }
        
        generatedNodes.add(node); //keep track of all nodes for clearance
        totalNodesVisited++;
        
        if (shortestPathNode!=null && node.getActivitySkipCount() > shortestPathNode.getActivitySkipCount()) {
            if (params.isBacktrackingDebug()) {
                LOGGER.info(indent + "node(" + node.getState().getName()+ ") is rejected!");
            }            
        }
        else if (node.getState().isProperCompletion() || node.getState().isImproperCompletion()) {
            //completeNodesForShortestPathExploration.add(node); //add to list of complete nodes
            if ((shortestPathNode == null) || 
                (shortestPathNode != null && node.getActivitySkipCount() < shortestPathNode.getActivitySkipCount())) {
                shortestPathNode = node;
            }
            
            if (params.isBacktrackingDebug()) {
                LOGGER.info(indent + "node(" + node.getState().getName()+ ") is proper completion!");
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
                if (params.isBacktrackingDebug()) {
                    LOGGER.info(indent + "node(" + node.getState().getName()+ ") leads to a loop or deadlock");
                }
            }
        }
        
        totalNodesVisited--;
        if (params.isBacktrackingDebug()) {
            indent = indent.substring(3);
            LOGGER.info(indent + "explorePathToEnd(" + node.getState().getName()+ ")");
        }
    }
    
    /**
     * Return list of next nodes to explore
     * This list is prioritized to select the best node leading to highest match
     */
    private Collection<Node> nexts(Node node) {
        return node.getChildren();
    }
    
    private Collection<Node> nextNodesForShortestPathFinding(Node node) {
        return node.getChildrenForShortestPathFinding();
    }
    
    /**
     * Reject a node and prune the tree from the node as root
     */
    private boolean reject(Node node) {
        boolean rejected = false;
        double totalUnmatch = node.getState().getTraceIndex() - node.getMatchCount();

        rejected =  optimalNodeFound ||
                    node.getCost() > maxAllowedCost || 
                    node.getDepth() > maxAllowedDepth ||
                    totalUnmatch > maxAllowedUnmatch ||
                    node.getDiffSeries() > params.getMaxDiffSeries() ||
                    node.getActivitySkipCount() > 1.0*params.getMaxActivitySkip()*helper.getActivities().size() ||
                    totalNodesVisited > params.getMaxNumberOfNodesVisited();
        return rejected;
    }
    
    /*
     * Accept a node and prune the tree from the node as root
     */
    private boolean accept(Node node) {
        if (node.getMatchCount() >= 1.0*params.getMaxMatch()*progressSize && node.getCost() == 0) {
            optimalNodeFound = true;
            fullTraceMatched = true;
            return true;
        }
        else if (node.getCost() <= params.getMaxCost() && 
                 node.getState().getTraceIndex() >= progressSize && 
                 node.getMatchCount() >= 1.0*params.getMinMatch()*progressSize) {
            return true;
        }
        else {
            return false;
        }
    }
    
    
    
    /*
     *  Select between the bestNode and the input node
     */
    private void select(Node node) {
        if (bestNode != null && !node.isBetter(bestNode)) {
            return;
        }
        else {
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
            LOGGER.info("Replayed Path: " + selectedNode.getPathString());
        }
        
        return selectedNode;
    }
    
    /**
     * Clean all unselected nodes created after backtracking process
     * to avoid memory leaks
     */
    public void clear() {
        Set<Node> selectedNodes = new HashSet();
        Node node = bestNode;
        while (node!=null) {
            selectedNodes.add(node);
            node = node.getParent();
        }
        for (Node generatedNode : generatedNodes) {
            if (!selectedNodes.contains(generatedNode)) {
                generatedNode.clear();
            }
        }
        generatedNodes.clear();
        completeNodesForShortestPathExploration.clear();
        
        params = null;
        trace = null;
        helper = null;
    }

}