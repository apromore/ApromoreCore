package au.edu.qut.processmining.miner;

import au.edu.qut.helper.DiagramHandler;
import au.edu.qut.processmining.log.LogParser;
import au.edu.qut.processmining.log.graph.LogGraph;
import de.hpi.bpt.graph.DirectedEdge;
import de.hpi.bpt.graph.DirectedGraph;
import de.hpi.bpt.graph.abs.IDirectedEdge;
import de.hpi.bpt.graph.abs.IDirectedGraph;
import de.hpi.bpt.graph.algo.rpst.RPST;
import de.hpi.bpt.graph.algo.rpst.RPSTNode;
import de.hpi.bpt.graph.algo.tctree.TCType;
import de.hpi.bpt.hypergraph.abs.Vertex;
import org.deckfour.xes.model.XLog;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.processmining.models.graphbased.directed.bpmn.elements.Swimlane;

import java.util.*;

/**
 * Created by Adriano on 14/06/2016.
 */

public class FakeMiner {

    private boolean unbalancedPaths;
    private boolean optionalTasks;
    private boolean recurrentTasks;
    private boolean inclusiveChoice;
    private boolean applyCleaning;

    private DiagramHandler diagramHandler;
    private LogGraph fuzzyNet;
    private BPMNDiagram diagram;
    private RPST rpst;
    private HashSet<RPSTNode> cycles;
    private HashSet<RPSTNode> parallel;
    private HashSet<RPSTNode> exclusive;
    private HashMap<BPMNNode, RPSTNode> parentSESE;
    private HashMap<String, BPMNNode> idToNode;
    private HashMap<String, Gateway> idToGate;
    private ArrayList<RPSTNode> bottomUpRPST;

    public FakeMiner() {
        diagramHandler = new DiagramHandler();
    }


    public BPMNDiagram optimize(BPMNDiagram inputDiagram, XLog log, boolean unbalancedPaths, boolean optionalTasks, boolean recurrentTasks, boolean inclusiveChoice, boolean applyCleaning) {
        diagram = diagramHandler.copyDiagram(inputDiagram);

        this.inclusiveChoice = inclusiveChoice;
        this.unbalancedPaths = unbalancedPaths;
        this.optionalTasks = optionalTasks;
        this.recurrentTasks = recurrentTasks;
        this.applyCleaning = applyCleaning;

        fuzzyNet = LogParser.generateLogGraph(log);

        if( !generateRPST() ) return inputDiagram;

        checkWeights();

        return diagram;
    }


    private boolean generateRPST() {
        try {
            HashMap<BPMNNode, Vertex> mapping = new HashMap<>();
            idToNode = new HashMap<>();
            idToGate = new HashMap<>();

            IDirectedGraph<DirectedEdge, Vertex> graph = new DirectedGraph();
            Vertex src;
            Vertex tgt;

            BPMNNode bpmnSRC;
            BPMNNode bpmnTGT;

            for( Flow f : diagram.getFlows((Swimlane) null) ) {
                bpmnSRC = f.getSource();
                bpmnTGT = f.getTarget();
                if (!mapping.containsKey(bpmnSRC)) {
                    src = new Vertex(bpmnSRC.getId().toString());
                    if (bpmnSRC instanceof Gateway) idToGate.put(bpmnSRC.getId().toString(), (Gateway) bpmnSRC);
                    mapping.put(bpmnSRC, src);
                    idToNode.put(bpmnSRC.getId().toString(), bpmnSRC);
                } else src = mapping.get(bpmnSRC);

                if (!mapping.containsKey(bpmnTGT)) {
                    tgt = new Vertex(bpmnTGT.getId().toString());
                    if (bpmnTGT instanceof Gateway) idToGate.put(bpmnTGT.getId().toString(), (Gateway) bpmnTGT);
                    mapping.put(bpmnTGT, tgt);
                    idToNode.put(bpmnTGT.getId().toString(), bpmnTGT);
                } else tgt = mapping.get(bpmnTGT);
                graph.addEdge(src, tgt);
            }

            rpst = new RPST(graph);
            bottomUpRPST = new ArrayList<>();

            RPSTNode root = rpst.getRoot();
            LinkedList<RPSTNode> toAnalize = new LinkedList<RPSTNode>();
            toAnalize.addLast(root);

            while (toAnalize.size() != 0) {
                root = toAnalize.removeFirst();
                if( root.getType() == TCType.B ) bottomUpRPST.add(0, root);

                for( RPSTNode n : new HashSet<RPSTNode>(rpst.getChildren(root)) ) {
                    String exitID = n.getExit().getName();
                    String entryID = n.getEntry().getName();
                    switch (n.getType()) {
                        case R:
                            System.out.println("WARNING - rigid found.");
                            toAnalize.addLast(n);
                            return false;
                        case T:
                            break;
                        case P:
                            toAnalize.addLast(n);
                            break;
                        case B:
                            if( idToGate.get(entryID).getGatewayType() == Gateway.GatewayType.DATABASED ) exclusive.add(n);
                            else if( idToGate.get(entryID).getGatewayType() == Gateway.GatewayType.PARALLEL ) parallel.add(n);

                            for(IDirectedEdge e : new HashSet<IDirectedEdge>(n.getFragmentEdges()) ) {
                                bpmnSRC = idToNode.get(e.getSource().getName());
                                bpmnTGT = idToNode.get(e.getTarget().getName());
                                parentSESE.put(bpmnSRC, n);
                                parentSESE.put(bpmnTGT, n);
                                if( e.getSource().getName().equalsIgnoreCase(exitID) && e.getTarget().getName().equalsIgnoreCase(entryID) ) {
                                    cycles.add(n);
                                    exclusive.remove(n);
                                }
                            }
                            toAnalize.addLast(n);
                            break;
                        default:
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.out.println("ERROR - impossible generate RPST.");
            return false;
        }

        return true;
    }

    private void checkWeights(){
        HashSet<RPSTNode> checked = new HashSet<>();
        String exitID;
        String entryID;
        BPMNNode entry;
        BPMNNode exit;
        HashSet<BPMNNode> successors;
        int weight;

        for( RPSTNode node : parallel )
            if( !checked.contains(node) ) {
                successors = new HashSet<>();
                entryID = node.getEntry().getName();
                entry = idToGate.get(entryID);
                exitID = node.getExit().getName();
                exit = idToGate.get(exitID);
                weight = 0;

                successors.addAll(diagramHandler.getSuccessors(entry));

                while( !successors.isEmpty() ) {
                    for( BPMNNode s : successors ) {
                        if( s instanceof Activity ) {
                            if( weight == 0 ) weight = fuzzyNet.getWeight(s.getLabel());
//                            if( fuzzyNet.getWeight(s.getLabel()) != weight && optionalTasks ) diagramHandler.makeSkippable(s);
                        }
                    }
                }
            }
    }

}
