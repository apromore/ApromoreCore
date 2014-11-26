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
    
    // key and value are reference to gateway
    //private Map<Gateway, Gateway> j2fMap = new HashMap();
    //private Map<Gateway, Gateway> j2fMapORGate = new HashMap();
    
    // key and value are reference to node
    private Map<FlowNode, Set<FlowNode>> nextActivitiesMap = new HashMap();
    
    DijkstraAlgorithm dijkstraAlgo;
    Map<FlowNode,Vertex<FlowNode>> bpmnDijikstraNodeMap = new HashMap();
    
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
        if (dijkstraAlgo != null) {
            return dijkstraAlgo;
        }
        else {
            //Create Dijistra nodes but keep a mapping from BPMN node to Dijistra node
            //so that we can apply for Dijistra edge in the next step
            synchronized (bpmnDijikstraNodeMap) {
                if (bpmnDijikstraNodeMap.isEmpty()) {
                    for (FlowNode node : this.getAllNodes()) {
                        bpmnDijikstraNodeMap.put(node, new Vertex(node.getId(), node.getName(), node));
                    }
                }
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
            return dijkstraAlgo;
        
        }
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

    
    
    
}