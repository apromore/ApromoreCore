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

import java.util.*;
import java.util.stream.Collectors;

import de.hpi.bpmn2_0.model.*;
import de.hpi.bpmn2_0.model.Process;
import org.apromore.service.loganimation.dijkstra.engine.DijkstraAlgorithm;
import org.apromore.service.loganimation.dijkstra.model.Edge;
import org.apromore.service.loganimation.dijkstra.model.Graph;
import org.apromore.service.loganimation.dijkstra.model.Vertex;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.cycle.JohnsonSimpleCycles;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import de.hpi.bpmn2_0.model.activity.Activity;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import de.hpi.bpmn2_0.model.event.EndEvent;
import de.hpi.bpmn2_0.model.event.StartEvent;
import de.hpi.bpmn2_0.model.gateway.ExclusiveGateway;
import de.hpi.bpmn2_0.model.gateway.InclusiveGateway;
import de.hpi.bpmn2_0.model.gateway.ParallelGateway;

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
    
    private Set<FlowNode> mixedXORs = new HashSet();
    
    private Set<FlowNode> mixedANDs = new HashSet();
    
    private Set<FlowNode> mixedORs = new HashSet();
    
    //key: name of activity, value: activity node
    private Map<String,FlowNode> activities = new HashMap();
    
    private DijkstraAlgorithm dijkstraAlgo = null;
    private Map<FlowNode,Vertex<FlowNode>> bpmnDijikstraNodeMap = new HashMap();

    private DirectedGraph directedGraph = null;
    private List<List<FlowNode>> bpmnCycles = null;
    private Set<FlowNode> ANDJoinsOnViciousCycles = null;

    /**
     * Check validity of the model used for animation
     * @param definition BPMN Definition
     * @return a pair of check result and error message. The error message is empty if check result is true, or
     * non-empty if check result is false.
     */
    public ModelCheckResult checkModel(Definitions definition) {
        String sourceId;
        String targetId;
        Set<FlowNode> targetSet = null;
        Set<FlowNode> sourceSet = null;
        
        List<BaseElement> processElements = definition.getRootElement().stream()
                                            .filter(r -> r instanceof Process)
                                            .collect(Collectors.toList());

        List<BaseElement> poolElements = definition.getRootElement().stream()
                                            .filter(r -> r instanceof Collaboration)
                                            .collect(Collectors.toList());
        
        //Check model validity for the replay
        if (!poolElements.isEmpty()) {
            return ModelCheckResult.of(false, "There are pools in the model. Pools are not yet supported");
        } else if (processElements.size() < 1) {
            return ModelCheckResult.of(false, "There is no process diagram in the model");
        }
        else if (processElements.size() > 1) {
            return ModelCheckResult.of(false, "There are more than one process diagram in the model");
        }
        else {
            rootElement = processElements.get(0);
            Process process = (Process) rootElement;
            ModelChecker checker = new ModelChecker();

            if (process.getFlowElement().isEmpty()) {
                return ModelCheckResult.of(false, "The model is empty");
            }

            //Visit every element once, check syntax and collect information
            for (FlowElement element : process.getFlowElement()) {
                element.acceptVisitor(checker);

                if (element instanceof SequenceFlow) {
                    sourceId = ((SequenceFlow) element).getSourceRef().getId();
                    targetId = ((SequenceFlow) element).getTargetRef().getId();
                    allSequenceFlows.put(sourceId + "-" + targetId, (SequenceFlow) element);
                } else if (element instanceof FlowNode) {

                    allNodes.put(element.getId(), (FlowNode) element);

                    if (element instanceof StartEvent) {
                        startEvent = (StartEvent) element;
                    }
                    if (element instanceof EndEvent) {
                        endEvent = (EndEvent) element;
                    }

                    targetSet = new HashSet();

                    for (SequenceFlow sflow : ((FlowNode) element).getOutgoingSequenceFlows()) {
                        targetSet.add((FlowNode) sflow.getTargetRef());
                    }
                    targets.put((FlowNode) element, targetSet);

                    sourceSet = new HashSet();
                    for (SequenceFlow sflow : ((FlowNode) element).getIncomingSequenceFlows()) {
                        sourceSet.add((FlowNode) sflow.getSourceRef());
                    }
                    sources.put((FlowNode) element, sourceSet);

                    if (isDecision((FlowNode) element)) {
                        decisions.add((FlowNode) element);
                    }

                    if (isMerge((FlowNode) element)) {
                        merges.add((FlowNode) element);
                    }

                    if (isFork((FlowNode) element)) {
                        forks.add((FlowNode) element);
                    }

                    if (isJoin((FlowNode) element)) {
                        joins.add((FlowNode) element);
                    }

                    if (isORSplit((FlowNode) element)) {
                        ORsplits.add((FlowNode) element);
                    }

                    if (isORJoin((FlowNode) element)) {
                        ORjoins.add((FlowNode) element);
                    }

                    if (isActivity((FlowNode) element)) {
                        activities.put(element.getName(), (FlowNode) element);
                    }

                    if (isMixedXOR((FlowNode) element)) {
                        mixedXORs.add((FlowNode) element);
                    }

                    if (isMixedAND((FlowNode) element)) {
                        mixedANDs.add((FlowNode) element);
                    }

                    if (isMixedOR((FlowNode) element)) {
                        mixedORs.add((FlowNode) element);
                    }
                }

            }

            if (!checker.isValid()) {
                return ModelCheckResult.of(false, checker.getFaultMessage());
            } else {
                this.getDijkstraAlgo();
                this.getANDJoinsOnViciousCycles();
                return ModelCheckResult.of(true);
            }
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
    
    public Set<FlowNode> getAllMixedXORs() {
        return mixedXORs;
    }
    
    public Set<FlowNode> getAllMixedANDs() {
        return mixedANDs;
    }
    
    public Set<FlowNode> getAllMixedORs() {
        return mixedORs;
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
//        if ((node instanceof ParallelGateway) &&
//            ((Gateway)node).getGatewayDirection().equals(GatewayDirection.CONVERGING)) {
//            return true;
//        }
//        else {
//            return false;
//        }
        return (node instanceof ParallelGateway &&
                node.getOutgoingSequenceFlows().size() == 1 &&
                node.getIncomingSequenceFlows().size() > 1);
    }
    
    public static boolean isFork(FlowNode node) {
        return (node instanceof ParallelGateway &&
                node.getOutgoingSequenceFlows().size() > 1 &&
                node.getIncomingSequenceFlows().size() == 1);
    }
    
    public static boolean isMixedAND(FlowNode node) {
        return (node instanceof ParallelGateway &&
                node.getOutgoingSequenceFlows().size() > 1 &&
                node.getIncomingSequenceFlows().size() > 1);
    }
    
    public static boolean isDecision(FlowNode node) {
        return (node instanceof ExclusiveGateway &&
                node.getOutgoingSequenceFlows().size() > 1 &&
                node.getIncomingSequenceFlows().size() == 1);
    }
    
    public static boolean isMerge(FlowNode node) {
        return (node instanceof ExclusiveGateway &&
                node.getOutgoingSequenceFlows().size() == 1 &&
                node.getIncomingSequenceFlows().size() > 1);
    }
    
    public static boolean isMixedXOR(FlowNode node) {
        return (node instanceof ExclusiveGateway &&
                node.getOutgoingSequenceFlows().size() > 1 &&
                node.getIncomingSequenceFlows().size() > 1);
    }
    
    public static boolean isActivity(FlowNode node) {
        return (node instanceof Activity);
    }
    
    public static boolean isORSplit(FlowNode node) {
        return (node instanceof InclusiveGateway &&
                node.getOutgoingSequenceFlows().size() > 1 &&
                node.getIncomingSequenceFlows().size() == 1);
    }
    
    public static boolean isORJoin(FlowNode node) {
        return (node instanceof InclusiveGateway &&
                node.getOutgoingSequenceFlows().size() == 1 &&
                node.getIncomingSequenceFlows().size() > 1);
    }
    
    public static boolean isMixedOR(FlowNode node) {
        return (node instanceof InclusiveGateway &&
                node.getOutgoingSequenceFlows().size() > 1 &&
                node.getIncomingSequenceFlows().size() > 1);
    }
    
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

    public DirectedGraph getDirectedGraph() {
        if (directedGraph == null) {
            directedGraph = new DefaultDirectedGraph<FlowNode, DefaultEdge>(DefaultEdge.class);
            for (FlowNode node : this.getAllNodes()) {
                directedGraph.addVertex(node);
            }
            for (SequenceFlow flow : this.getAllSequenceFlows()) {
                directedGraph.addEdge(flow.getSourceRef(), flow.getTargetRef());
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
