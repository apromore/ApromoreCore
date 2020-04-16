/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package de.hpi.bpmn2_0.replay;

import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.event.StartEvent;
import java.util.ArrayList;
import java.util.List;
import de.hpi.bpmn2_0.model.Process;
import de.hpi.bpmn2_0.model.activity.Activity;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import de.hpi.bpmn2_0.model.event.EndEvent;
import de.hpi.bpmn2_0.model.gateway.ExclusiveGateway;
import de.hpi.bpmn2_0.model.gateway.Gateway;
import de.hpi.bpmn2_0.model.gateway.GatewayDirection;
import de.hpi.bpmn2_0.model.gateway.InclusiveGateway;
import de.hpi.bpmn2_0.model.gateway.ParallelGateway;
import de.vogella.algorithms.dijkstra.engine.DijkstraAlgorithm;
import de.vogella.algorithms.dijkstra.model.Edge;
import de.vogella.algorithms.dijkstra.model.Graph;
import de.vogella.algorithms.dijkstra.model.Vertex;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
//import org.jbpt.algo.graph.StronglyConnectedComponents;
//import org.jbpt.pm.ProcessModel;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.cycle.JohnsonSimpleCycles;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class BPMNDiagramHelper {
    private static BPMNDiagramHelper instance = null;
    
    private BaseElement rootElement=null;
    private FlowNode startEvent=null;
    private FlowNode endEvent=null;
    
    // key: taskId, value is reference to the node
    private Map<String, FlowNode> allNodes = new HashMap();    
    
    // key: concatenation of taskId of source and target node
    // value: reference to edge object
    private Map<String, SequenceFlow> allSequenceFlows = new HashMap();
    
    private Map<FlowNode,Set<FlowNode>> targets = new HashMap();
    
    private Map<FlowNode,Set<FlowNode>> sources = new HashMap();
    
    private Set<FlowNode> decisions = new HashSet();
    
    private Set<FlowNode> merges = new HashSet();
    
    private Set<FlowNode> forks = new HashSet();
    
    private Set<FlowNode> joins = new HashSet();
    
    private Set<FlowNode> ORsplits = new HashSet();
    
    private Set<FlowNode> ORjoins = new HashSet();
    
    //key: name of activity, value: activity node
    private Map<String,FlowNode> activities = new HashMap();
    
    // key and value are reference to node
    //private Map<FlowNode, Set<FlowNode>> nextActivitiesMap = new HashMap();
    
    private DijkstraAlgorithm dijkstraAlgo = null;
    private Map<FlowNode,Vertex<FlowNode>> bpmnDijikstraNodeMap = new HashMap();
    
//    private ProcessModel jbptProcessModel = null;
    private Set<Set<FlowNode>> bpmnSCCSet = null;
//    private BidiMap<FlowNode, org.jbpt.pm.FlowNode> jbptNodeMap = new DualHashBidiMap<>(); 
    
    private DirectedGraph directedGraph = null;
    private List<List<FlowNode>> bpmnCycles = null;
    private Set<FlowNode> ANDJoinsOnViciousCycles = null;
    
    public BPMNDiagramHelper() {
    }

    /*
    public static BPMNDiagramHelper getInstance() {
      if(instance == null) {
         instance = new BPMNDiagramHelper();
      }
      return instance;
    }
    */
    
    /*
    * The helper class also does a preliminary check on the validity of the 
    * BPMN defintion, to make sure it complies with restrictions
    * for animation.
    * Set rootElement, startEvent, endEvent.
    */
    public void checkModel(Definitions definition) throws Exception {
        String sourceId;
        String targetId;
        Set<FlowNode> targetSet = null;
        Set<FlowNode> sourceSet = null;
        
        List<BaseElement> rootElements = definition.getRootElement();
        
        //Check model validity for the replay
        if (rootElements.size() == 1) {
            rootElement = rootElements.get(0);
            
            if (rootElement instanceof Process) {
                Process process = (Process)rootElement;
                ModelChecker checker = new ModelChecker();
                
                //Visit every element once, check syntax and collect information
                for (FlowElement element : process.getFlowElement()) {
                    element.acceptVisitor(checker);

                    if (element instanceof SequenceFlow) {
                        sourceId = ((SequenceFlow)element).getSourceRef().getId();
                        targetId = ((SequenceFlow)element).getTargetRef().getId();
                        allSequenceFlows.put(sourceId+"-"+targetId, (SequenceFlow)element);
                    }  
                    else if (element instanceof FlowNode) {
                        
                        allNodes.put(element.getId(), (FlowNode)element);

                        if (element instanceof StartEvent) {
                            startEvent = (StartEvent)element;
                        }
                        if (element instanceof EndEvent) {
                            endEvent = (EndEvent)element;
                        } 
                        
                        targetSet = new HashSet();
                        
                        for (SequenceFlow sflow : ((FlowNode)element).getOutgoingSequenceFlows()) {
                            targetSet.add((FlowNode)sflow.getTargetRef());
                        }
                        targets.put((FlowNode)element, targetSet);
                        
                        sourceSet = new HashSet();
                        for (SequenceFlow sflow : ((FlowNode)element).getIncomingSequenceFlows()) {
                            sourceSet.add((FlowNode)sflow.getSourceRef());
                        }
                        sources.put((FlowNode)element, sourceSet);
                        
                        if (isDecision((FlowNode)element)) {
                            decisions.add((FlowNode)element);
                        }
                        
                        if (isMerge((FlowNode)element)) {
                            merges.add((FlowNode)element);
                        }
                        
                        if (isFork((FlowNode)element)) {
                            forks.add((FlowNode)element);
                        }
                        
                        if (isJoin((FlowNode)element)) {
                            joins.add((FlowNode)element);
                        }
                        
                        if (isORSplit((FlowNode)element)) {
                            ORsplits.add((FlowNode)element);
                        }
                        
                        if (isORJoin((FlowNode)element)) {
                            ORjoins.add((FlowNode)element);
                        }
                        
                        if (isActivity((FlowNode)element)) {
                            activities.put(element.getName(), (FlowNode)element);
                        }
                        
                        
                    }                    
                    
                }
                
                if (!checker.isValid()) {
                    throw new Exception(checker.getFaultMessage());
                }
                else {
                    //this.getJoin2ForkMap(); commented out to avoid stack overflow, due to relaxing block-structure
                    //this.getForkJoinMapORGate();
                    this.getDijkstraAlgo();
                    //this.getJBPTProcessModel();
                    //this.getStronglyConnectedComponents();
                    this.getANDJoinsOnViciousCycles();
                }
                
            } else {
                throw new Exception("Root element " + rootElement.getId() + " is not a process");
            }
        } else {
            throw new Exception("There is more than 1 root elemnent found. IDs = " + rootElements.toString());
        }
        
        
    }
    
    public FlowNode getStartEvent() {
        return startEvent;
    }
    
    public FlowNode getEndEvent() {
        return endEvent;
    }
    
    public FlowNode getFlowNode(String Id) {
        return allNodes.get(Id);
    } 
    
    public Collection<FlowNode> getAllNodes()  {
        return allNodes.values();
    }     
    
    public FlowNode getActivity(String Id) {
        if (allNodes.get(Id) instanceof Activity) {
            return allNodes.get(Id);
        } else {
            return null;
        }
    } 
    
    public Collection<FlowNode> getActivities() {
        return this.activities.values();
    }
    
    public Set<String> getActivityNames() {
        return this.activities.keySet();
    }
    
    public FlowNode getNodeFromEvent(String eventName) {
        if (this.activities.containsKey(eventName)) {
            return this.activities.get(eventName);
        } 
        else if (this.startEvent.getName().equals(eventName)) {
            return this.startEvent;
        }
        else if (this.endEvent.getName().equals(eventName)) {
            return this.endEvent;
        }
        else {
            return null;
        }
    }
    
    public Set<FlowNode> getAllDecisions() {
        return decisions;
    }
    
    public Set<FlowNode> getAllMerges() {
        return merges;
    }    
    
    public Set<FlowNode> getAllForks() {
        return forks;
    }     
    
    public Set<FlowNode> getAllJoins() {
        return joins;
    }  
    
    public Set<FlowNode> getAllORSplits() {
        return ORsplits;
    }     
    
    public Set<FlowNode> getAllORJoins() {
        return ORjoins;
    }
    
    public Collection<FlowNode> getSet(FlowNode node) {
        Set<FlowNode> set = new HashSet();
        set.add(node);
        return set;
    }
    
    public Collection<SequenceFlow> getSet(SequenceFlow flow) {
        Collection<SequenceFlow> set = new HashSet();
        set.add(flow);
        return set;
    }    
    
    public Collection<FlowNode> addToSet(Collection<FlowNode> set, FlowNode node) {
        Collection<FlowNode> temp;
        if (set == null) {
            temp = new HashSet();
        } 
        else {
            temp = set;
        }
        temp.add(node);
        return temp;
    }
    
    public Set<TraceNode> addToSet(Set<TraceNode> set, TraceNode node) {
        Set<TraceNode> temp;
        if (set == null) {
            temp = new HashSet();
        } 
        else {
            temp = set;
        }
        temp.add(node);
        return temp;
    }    
    
    public SequenceFlow getSequenceFlow(FlowNode source, FlowNode target) {
        String key = source.getId()+"-"+target.getId();
        if (allSequenceFlows.containsKey(key)) {
            return allSequenceFlows.get(key);
        } else {
            return null;
        }
    }
    
    public Collection<SequenceFlow> getAllSequenceFlows() {
        return allSequenceFlows.values();
    }
    
    public Set<FlowNode> getTargets(FlowNode element) {
        if (targets.containsKey(element)) {
            return targets.get(element);
        } else {
            return new HashSet();
        }
    }
    
    public Set<FlowNode> getSources(FlowNode element) {
        if (sources.containsKey(element)) {
            return sources.get(element);
        } else {
            return new HashSet();
        }
    }    
    
    public static boolean isJoin(FlowNode node) {
        if ((node instanceof ParallelGateway) && 
            ((Gateway)node).getGatewayDirection().equals(GatewayDirection.CONVERGING)) {
            return true;
        }
        else {
            return false;
        }        
    }
    
    public static boolean isFork(FlowNode node) {
        if ((node instanceof ParallelGateway) && 
            ((Gateway)node).getGatewayDirection().equals(GatewayDirection.DIVERGING)) {
            return true;
        }
        else {
            return false;
        }
    }    
    
    public static boolean isDecision(FlowNode node) {
        if ((node instanceof ExclusiveGateway) && 
            ((Gateway)node).getGatewayDirection().equals(GatewayDirection.DIVERGING)) {
            return true;
        }
        else {
            return false;
        }        
    }    
    
    public static boolean isMerge(FlowNode node) {
        if ((node instanceof ExclusiveGateway) && 
            ((Gateway)node).getGatewayDirection().equals(GatewayDirection.CONVERGING)) {
            return true;
        }
        else {
            return false;
        }         
    }    
    
    public static boolean isActivity(FlowNode node) {
        return (node instanceof Activity);
    }     
    
    public static boolean isORSplit(FlowNode node) {
        if ((node instanceof InclusiveGateway) && 
            ((Gateway)node).getGatewayDirection().equals(GatewayDirection.DIVERGING)) {
            return true;
        }
        else {
            return false;
        }
    }
    
    public static boolean isORJoin(FlowNode node) {
        if ((node instanceof InclusiveGateway) && 
            ((Gateway)node).getGatewayDirection().equals(GatewayDirection.CONVERGING)) {
            return true;
        }
        else {
            return false;
        }
    }    
    
    /*
    public Set<FlowNode> getNextActivities(FlowNode node) {
        Set<FlowNode> nextActivities;
        if (nextActivitiesMap.isEmpty()) {
            nextActivitiesMap = (new NextActivitiesMap()).getNextActivitiesMap();
        }
        nextActivities = nextActivitiesMap.get(node);
        
        return nextActivities;
    } 
    */
    
    /*
    * Check if an event name is part of all activities of a node
    */
    /*
    public boolean isNextActivityName(FlowNode node, String eventName) {
        for (FlowNode next : this.getNextActivities(node)) {
            if (next.getName().equals(eventName)) {
                return true;
            }
        }
        return false;
    } 
    */
    
    /*
    * Require a strict block-structure of gateways: a split must be accompanied with a join gate
    * AND-AND, XOR-XOR, OR-OR.
    */
    /*
    public Map<Gateway, Gateway> getJoin2ForkMap() {
        if (j2fMap.isEmpty()) {
            j2fMap = (new Join2ForkMap(startEvent, false)).getJoin2ForkMap();
        }
        return j2fMap;
    }
    */
    
    /*
    * Require a strict block-structure of gateways: a split must be accompanied with a join gate
    * AND-AND, XOR-XOR, OR-OR.
    */
    /*
    public Map<Gateway, Gateway> getForkJoinMapORGate() {
        if (j2fMapORGate.isEmpty()) {
            j2fMapORGate = (new Join2ForkMap(startEvent, true)).getJoin2ForkMap();
        }
        return j2fMapORGate;
    } 
    */
    
    public DijkstraAlgorithm getDijkstraAlgo() {
        if (dijkstraAlgo == null) {
            //Create Dijistra nodes but keep a mapping from BPMN node to Dijistra node
            //so that we can apply for Dijistra edge in the next step
            for (FlowNode node : this.getAllNodes()) {
                bpmnDijikstraNodeMap.put(node, new Vertex(node.getId(), node.getName(), node));
            }
            
            List<Edge<FlowNode>> edges = new ArrayList();
            FlowNode source;
            FlowNode target;
            for (SequenceFlow flow : this.getAllSequenceFlows()) {
                source = (FlowNode)flow.getSourceRef();
                target = (FlowNode)flow.getTargetRef();
                edges.add(new Edge(source.getId()+"-"+target.getId(),
                                   bpmnDijikstraNodeMap.get(source),
                                   bpmnDijikstraNodeMap.get(target),
                                   1));
            }
            
            dijkstraAlgo = new DijkstraAlgorithm(new Graph(new ArrayList(bpmnDijikstraNodeMap.values()),edges));
            
        
        }
        return dijkstraAlgo;
    }
    
    public Vertex<FlowNode> getDijikstraVertex(FlowNode node) {
        if (bpmnDijikstraNodeMap.containsKey(node)) {
            return bpmnDijikstraNodeMap.get(node);
        } else {
            return null;
        }
    }
    
    public boolean existPath(FlowNode source, FlowNode target) {
        if (source == target) {
            return true;
        }
        else {
            DijkstraAlgorithm algo = this.getDijkstraAlgo();
            algo.execute(this.getDijikstraVertex(source));
            ArrayList<Vertex> path = algo.getPath(this.getDijikstraVertex(target));
            return (path != null && path.size() > 0);
        }
    }
    
    /*
    * Return empty list if source == target
    * Return non-empty list if path found
    * Return null if no path found    
    */
    public ArrayList<Vertex> getPath(FlowNode source, FlowNode target) {
        ArrayList<Vertex> path = new ArrayList();
        if (source == target) {
            return path;
        }
        else {
            DijkstraAlgorithm algo = this.getDijkstraAlgo();
            algo.execute(this.getDijikstraVertex(source));
            path = algo.getPath(this.getDijikstraVertex(target));
            return path;
        }
    }
    
    /*
    * Return the shortest path from a set of sequence flows to the target   
    * Path length is the number of activity nodes on the path
    * The set cannot reach the node if all nodes in set cannot reach the node
    * Otherwise, the shortest path of all nodes is returned.
    */
    public ArrayList<Vertex> getPath(Set<SequenceFlow> sourceFlows, FlowNode target) {
        ArrayList<Vertex> path = null;
        ArrayList<Vertex> shortest=null;
        int shortestLength = Integer.MAX_VALUE;
        int nullPathCount = 0;
        
        for (SequenceFlow flow : sourceFlows) {
            path = getPath((FlowNode)flow.getTargetRef(), target);
            if (path != null) {
                if (shortestLength > path.size()) {
                    shortestLength = path.size();
                    shortest = path;
                }
            } else {
                nullPathCount++;
            }
        }
        
        if (nullPathCount == sourceFlows.size()) {
            return null;
        }
        else {
            return shortest;
        }
    }    
    
    public boolean existPath(Set<FlowNode> sourceSet, FlowNode target) {
        for (FlowNode source : sourceSet) {
            if (existPath(source, target)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean existPathFromFlows(Set<SequenceFlow> sourceFlows, FlowNode target) {
        Set<FlowNode> nodes = new HashSet();
        for (SequenceFlow flow : sourceFlows) {
            nodes.add((FlowNode)flow.getTargetRef());
        }
        return (existPath(nodes, target));
    }
    
    /*
    * Get all source edges of targetFlow, i.e. exist a path that reaches targetFlow but not via the avoidNode
    * Note that we need to avoid loop.
    */
    public void getSourceEdges(SequenceFlow targetFlow, FlowNode avoidNode, Set<SequenceFlow> flowSet) {
        if ((FlowNode)targetFlow.getSourceRef() != avoidNode) { 
            for (SequenceFlow incoming : ((FlowNode)targetFlow.getSourceRef()).getIncomingSequenceFlows()) {
                if (!flowSet.contains(incoming)) { //this is to avoid loop
                    flowSet.add(incoming);
                    getSourceEdges(incoming, avoidNode, flowSet);
                } 
            }
        }
    }
    
    /*
    * Return number of activity nodes in the list
    * This is used after getting Dijkstra path from one source to a target
    * REturn Integer.MAX_VALUE if path is null (no path found), which
    * means never reach the target
    */
    public int countActivities(ArrayList<Vertex> path) {
        int count = 0;
        
        if (path == null) {
            return Integer.MAX_VALUE;
        }
        
        for (Vertex vertex : path) {
            if (BPMNDiagramHelper.isActivity((FlowNode)vertex.getObject())) {
                count++;
            }
        }
        return count;
    }
    
    /*
    * Return number of nodes in the list
    */
    public int countNodes(ArrayList<Vertex> path) {
        if (path == null) {
            return Integer.MAX_VALUE;
        }
        else {
            return path.size();
        }
    }    

    
//    public ProcessModel getJBPTProcessModel() {
//        if (jbptProcessModel == null) {           
//            org.jbpt.pm.FlowNode jbptNode = null;
//            jbptProcessModel = new ProcessModel();
//            for (FlowNode node : this.getAllNodes()) {
//                if (node == this.getStartEvent() || node == this.getEndEvent()) {
//                    jbptNode = new org.jbpt.pm.Event(node.getName());
//                }
//                else if (this.getActivities().contains(node)) {
//                    jbptNode = new org.jbpt.pm.Activity(node.getName());
//                }
//                else if (this.getAllDecisions().contains(node) || this.getAllMerges().contains(node)) {
//                    jbptNode = new org.jbpt.pm.XorGateway(node.getName());
//                }
//                else if (this.getAllForks().contains(node) || this.getAllJoins().contains(node)) {
//                    jbptNode = new org.jbpt.pm.AndGateway(node.getName());
//                }
//                else if (this.getAllORSplits().contains(node) || this.getAllORJoins().contains(node)) {
//                    jbptNode = new org.jbpt.pm.OrGateway(node.getName());
//                }
//                jbptNodeMap.put(node, jbptNode);
//                jbptProcessModel.addFlowNode(jbptNode);
//            }
//            
//            org.jbpt.pm.FlowNode from = null;
//            org.jbpt.pm.FlowNode to = null;
//            for (SequenceFlow flow : this.getAllSequenceFlows()) {
//                from = jbptNodeMap.get((FlowNode)flow.getSourceRef());
//                to = jbptNodeMap.get((FlowNode)flow.getTargetRef());
//                jbptProcessModel.addControlFlow(from, to);
//            }
//        }
//        
//        return jbptProcessModel;    
//    }
     
    
    /**
     * Compute to get strongly connected components in the model.
     * These are sets of nodes which form a cycle with only activity nodes,
     * AND split/join gateways and OR/XOR split gateways.
     * Assume that jbptProcessModel has been created
     * @return set of subset, each represents strongly connected components
     */
//    public Set<Set<FlowNode>> getStronglyConnectedComponents() {
//        if (bpmnSCCSet == null) {
//            bpmnSCCSet = new HashSet();
//            Set<FlowNode> bpmnSCC;
//
//            StronglyConnectedComponents scc = new StronglyConnectedComponents();
//            Set<Set<org.jbpt.pm.FlowNode>> jbptSCCSet = scc.compute(jbptProcessModel);
//            FlowNode node;
//            for (Set<org.jbpt.pm.FlowNode> jbptSCC : jbptSCCSet) {
//                if (jbptSCC.size() > 1) {
//                    bpmnSCC = new HashSet();
//                    boolean trueSCC = true;
//                    for (org.jbpt.pm.FlowNode jbptNode : jbptSCC) {
//                        node = jbptNodeMap.getKey(jbptNode);
//                        bpmnSCC.add(node);
//                        if (!getAllForks().contains(node) && !getAllJoins().contains(node) &&
//                                node.getIncomingSequenceFlows().size() >= 2) {
//                            trueSCC = false;
//                            break;
//                        }
//                    }
//                    if (trueSCC) {
//                        bpmnSCCSet.add(bpmnSCC);
//                    } else {
//                        bpmnSCC.clear();
//                    }
//                }
//            }
//        }
//        return bpmnSCCSet;
//    }
    
    
    public DirectedGraph getDirectedGraph() {
        if (directedGraph == null) {           
            directedGraph = new DefaultDirectedGraph<FlowNode, DefaultEdge>(DefaultEdge.class);
            for (FlowNode node : this.getAllNodes()) {
                directedGraph.addVertex(node);
            }
            for (SequenceFlow flow : this.getAllSequenceFlows()) {
                directedGraph.addEdge((FlowNode)flow.getSourceRef(), (FlowNode)flow.getTargetRef());
            }
        }
        
        return directedGraph;    
    }    
    
    public List<List<FlowNode>> getSimpleCycles() {
        if (directedGraph == null) {
            this.getDirectedGraph();
        }
        
        if (bpmnCycles == null) {
            JohnsonSimpleCycles cycleAlgo = new JohnsonSimpleCycles(directedGraph);
            bpmnCycles = cycleAlgo.findSimpleCycles();
            List<List<FlowNode>> toRemove = new ArrayList();
            for (List<FlowNode> cycle : bpmnCycles) {
                boolean viciousCycle = true;
                for (FlowNode node : cycle) {
                    if (!getAllJoins().contains(node) && node.getIncomingSequenceFlows().size() >= 2) {
                        viciousCycle = false;
                        break;
                    }
                }
                if (!viciousCycle) {
                    toRemove.add(cycle);
                }
            }
            bpmnCycles.removeAll(toRemove);
        }
        return bpmnCycles;
        
    }
    
    public Collection<FlowNode> getANDJoinsOnViciousCycles() {
        if (bpmnCycles == null) {
            this.getSimpleCycles();
        }
        
        if (ANDJoinsOnViciousCycles == null) {
            ANDJoinsOnViciousCycles = new HashSet();
            for (List<FlowNode> cycle : bpmnCycles) {
                for (FlowNode node : cycle) {
                    if (this.getAllJoins().contains(node)) {
                        ANDJoinsOnViciousCycles.add(node);
                    }
                }
            }
        }
        return ANDJoinsOnViciousCycles;
    }
    
    /**
     * Return all activity nodes in a chain starting from the input activity
     * Used for looking ahead on the model. Done by traversing from the start node
     * until encountering a splitting gateway or End event.
     * @param startNode: starting node
     * @return set of activities including the input activity, or empty set
     */
    public Set<FlowNode> getActivityChain(FlowNode startNode) {
        Set<FlowNode> activityChain = new HashSet();
        Set<FlowNode> visited = new HashSet();
        FlowNode node = startNode;
        while (node.getOutgoingSequenceFlows().size() == 1 && !visited.contains(node)) {
            visited.add(node);
            if (this.getActivities().contains(node)) {
                activityChain.add(node);
            }            
            node = (FlowNode)node.getOutgoingSequenceFlows().get(0).getTargetRef();
        }
        return activityChain;
    } 
}