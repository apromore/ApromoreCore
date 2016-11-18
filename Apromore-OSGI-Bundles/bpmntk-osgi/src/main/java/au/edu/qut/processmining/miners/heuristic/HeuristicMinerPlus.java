package au.edu.qut.processmining.miners.heuristic;

import au.edu.qut.helper.DiagramHandler;
import au.edu.qut.processmining.log.LogParser;
import au.edu.qut.processmining.log.SimpleLog;
import au.edu.qut.processmining.miners.heuristic.net.HeuristicNet;
import au.edu.qut.processmining.miners.heuristic.oracle.Oracle;
import au.edu.qut.processmining.miners.heuristic.oracle.OracleItem;
import org.deckfour.xes.model.XLog;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramImpl;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.*;

import java.util.*;

/**
 * Created by Adriano on 24/10/2016.
 */
public class HeuristicMinerPlus {

    private SimpleLog log;
    private HeuristicNet heuristicNet;

    private BPMNDiagram heuristicDiagram;
    private BPMNDiagram bpmnDiagram;

    public HeuristicMinerPlus() {}

    public BPMNDiagram mineBPMNModel(XLog log, double dependencyThreshold, double positiveObservations, double relative2BestThreshold) {

        System.out.println("HM+ - starting... ");
        System.out.println("HM+ - [Setting] dependency threshold: " + dependencyThreshold);
        System.out.println("HM+ - [Setting] positive observations: " + positiveObservations);
        System.out.println("HM+ - [Setting] relative to best threshold: " + relative2BestThreshold);

        this.log = LogParser.getSimpleLog(log);
        System.out.println("HM+ - log parsed successfully");

        mineHeuristicNet(dependencyThreshold, positiveObservations, relative2BestThreshold);
        generateBPMNDiagram();
        updateLabels(this.log.getEvents());

        return bpmnDiagram;
    }

    public HeuristicNet getHeuristicNet() { return heuristicNet; }
    public BPMNDiagram getHeuristicDiagram() { return heuristicDiagram; }
    public BPMNDiagram getBPMNDiagram() { return bpmnDiagram; }

    public void mineHeuristicNet(XLog log, double dependencyThreshold, double positiveObservations, double relative2BestThreshold) {
        System.out.println("HM+ - starting... ");
        System.out.println("HM+ - [Setting] dependency threshold: " + dependencyThreshold);
        System.out.println("HM+ - [Setting] positive observations: " + positiveObservations);
        System.out.println("HM+ - [Setting] relative to best threshold: " + relative2BestThreshold);

        this.log = LogParser.getSimpleLog(log);
        System.out.println("HM+ - log parsed successfully");
        mineHeuristicNet(dependencyThreshold, positiveObservations, relative2BestThreshold);
    }

    private void mineHeuristicNet(double dependencyThreshold, double positiveObservations, double relative2BestThreshold) {
        System.out.println("HM+ - mining heuristic net: starting");
        heuristicNet = new HeuristicNet(log, dependencyThreshold, positiveObservations, relative2BestThreshold);
        heuristicNet.generateHeuristicNet();
        heuristicDiagram = heuristicNet.getHeuristicDiagram(true);
        System.out.println("HM+ - mining heuristic net: done ");
    }

    private void generateBPMNDiagram() {
        HashMap<Integer, BPMNNode> mapping = new HashMap<>();
        BPMNNode entry = null;
        BPMNNode exit = null;

        BPMNNode src;
        BPMNNode tgt;
        Gateway gate;

        ArrayList<BPMNNode> toVisit = new ArrayList<>();
        HashSet<BPMNNode> visited = new HashSet<>();

        HashSet<Integer> successors;
        HashSet<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> removableEdges;

        Oracle oracle = new Oracle();
        OracleItem oracleItem;
        OracleItem finalOracleItem;
        HashSet<OracleItem> oracleItems;

        bpmnDiagram = heuristicNet.convertIntoBPMNDiagram();


        /* generating all the splits gateways */

        for( Event e : bpmnDiagram.getEvents() )
            if( e.getEventType() == Event.EventType.START ) entry = e;
            else exit = e;

        if( entry == null || exit == null ) {
            System.out.println("ERROR - ");
            return;
        }

        toVisit.add(0, entry);

        System.out.println("DEBUG - generating bpmn diagram");

        while( toVisit.size() != 0 ) {
            entry = toVisit.remove(0);
            visited.add(entry);
            System.out.println("DEBUG - visiting: " + entry.getLabel());

            if( entry == exit ) continue;

            if( bpmnDiagram.getOutEdges(entry).size() > 1 ) {
                successors = new HashSet<>();
                removableEdges = new HashSet<>();

                for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> oe : bpmnDiagram.getOutEdges(entry) ) {
                    tgt = oe.getTarget();
                    removableEdges.add(oe);
                    successors.add( Integer.valueOf(tgt.getLabel()) );
                    mapping.put(Integer.valueOf(tgt.getLabel()), tgt);
                    if( !toVisit.contains(tgt) && !visited.contains(tgt) ) toVisit.add(tgt);
                }

                for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> oe : removableEdges ) bpmnDiagram.removeEdge(oe);

                //generation of the oracle items
                oracleItems = new HashSet<>();
                for( int a : successors ) {
                    oracleItem = new OracleItem();
                    oracleItem.fillPast(a);
                    for (int b : successors) if( (a !=  b) && (heuristicNet.areConcurrent(a, b)) ) oracleItem.fillFuture(b);
                    oracleItem.engrave();
                    oracleItems.add(oracleItem);
                }

                finalOracleItem = oracle.getFinalOracleItem(oracleItems);

                generateSplitGateways(entry, finalOracleItem, mapping);

            } else {
                tgt = ((new ArrayList<>(bpmnDiagram.getOutEdges(entry))).get(0)).getTarget();
                if( !toVisit.contains(tgt) && !visited.contains(tgt) ) toVisit.add(tgt);
            }
        }

        /* generating all the joins gateways */
//        Set<BPMNNode> nodes = new HashSet<>(bpmnDiagram.getNodes());
//        for( BPMNNode n : nodes ) {
//            removableEdges = new HashSet<>(bpmnDiagram.getInEdges(n));
//            if( removableEdges.size() > 1 ) {
////                System.out.println("DEBUG - generating a new join");
//                gate = bpmnDiagram.addGateway("", Gateway.GatewayType.DATABASED);
//                bpmnDiagram.addFlow(gate, n, "");
//                for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> oe : removableEdges ) {
//                    bpmnDiagram.removeEdge(oe);
//                    bpmnDiagram.addFlow(oe.getSource(), gate, "");
//                }
//            }
//        }

        System.out.println("HM+ - bpmn diagram generated successfully");
    }

    private void generateSplitGateways(BPMNNode entry, OracleItem nextOracleItem, Map<Integer, BPMNNode> mapping) {
        Gateway.GatewayType type = nextOracleItem.getGateType();
        BPMNNode node;
        Integer nodeCode;
        Gateway gate;

        System.out.println("DEBUG - generating split from Oracle ~ [xor|and]: " + nextOracleItem + " ~ [" + nextOracleItem.getXorBrothers().size() + "|" + nextOracleItem.getAndBrothers().size() + "]");

        if( type == null ) {
            nodeCode = nextOracleItem.getNodeCode();
            if( nodeCode != null ) {
                node = mapping.get(nodeCode);
                bpmnDiagram.addFlow(entry, node, "");
            } else System.out.println("ERROR - found an oracle item without brother and more than one element in its past");
            return;
        }

        gate = bpmnDiagram.addGateway("", type);
        bpmnDiagram.addFlow(entry, gate, "");
        for( OracleItem next : nextOracleItem.getXorBrothers() ) generateSplitGateways(gate, next, mapping);
        for( OracleItem next : nextOracleItem.getAndBrothers() ) generateSplitGateways(gate, next, mapping);
    }

    private void updateLabels(Map<Integer, String> events) {
        DiagramHandler helper = new DiagramHandler();
        BPMNDiagram duplicateDiagram = new BPMNDiagramImpl(bpmnDiagram.getLabel());
        HashMap<BPMNNode, BPMNNode> originalToCopy = new HashMap<>();
        BPMNNode src, tgt;
        BPMNNode copy;
        String label;

        for( BPMNNode n : bpmnDiagram.getNodes() ) {
            if( n instanceof Activity ) label = events.get(Integer.valueOf(n.getLabel()));
            else label = n.getLabel();
            copy = helper.copyNode(duplicateDiagram, n, label);
            if( copy != null ) originalToCopy.put(n, copy);
            else System.out.println("ERROR - diagram labels updating failed [1].");
        }

        for( Flow f : bpmnDiagram.getFlows() ) {
            src = originalToCopy.get(f.getSource());
            tgt = originalToCopy.get(f.getTarget());

            if( src != null && tgt != null ) duplicateDiagram.addFlow(src, tgt, f.getLabel());
            else System.out.println("ERROR - diagram labels updating failed [2].");
        }
        bpmnDiagram = duplicateDiagram;
    }

}
