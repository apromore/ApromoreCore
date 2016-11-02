package au.edu.qut.processmining.miners.heuristic;

import au.edu.qut.processmining.log.LogParser;
import au.edu.qut.processmining.log.SimpleLog;
import au.edu.qut.processmining.miners.heuristic.net.HeuristicNet;
import au.edu.qut.processmining.miners.heuristic.oracle.Oracle;
import au.edu.qut.processmining.miners.heuristic.oracle.OracleItem;
import org.deckfour.xes.model.XLog;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.processmining.models.graphbased.directed.bpmn.elements.Flow;

import java.util.ArrayList;
import java.util.HashSet;

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

        return bpmnDiagram;
    }

    public HeuristicNet getHeuristicNet() { return heuristicNet; }
    public BPMNDiagram getHeuristicDiagram() { return heuristicDiagram; }
    public BPMNDiagram getBPMNDiagram() { return bpmnDiagram; }

    private void mineHeuristicNet(double dependencyThreshold, double positiveObservations, double relative2BestThreshold) {
        System.out.println("HM+ - mining heuristic net: starting");
        heuristicNet = new HeuristicNet(log, dependencyThreshold, positiveObservations, relative2BestThreshold);
        heuristicNet.generateHeuristicNet();
        heuristicDiagram = heuristicNet.getHeuristicDiagram();
        System.out.println("HM+ - mining heuristic net: done ");
    }

    private void generateBPMNDiagram() {
        BPMNNode entry = null;
        BPMNNode exit = null;

        BPMNNode tgt;

        ArrayList<BPMNNode> toVisit = new ArrayList<>();
        HashSet<BPMNNode> visited = new HashSet<>();

        HashSet<Integer> successors;
        HashSet<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> removableEdges;

        Oracle oracle = new Oracle();
        OracleItem oracleItem;
        OracleItem finalOracleItem;
        HashSet<OracleItem> oracleItems;

        bpmnDiagram = heuristicNet.convertIntoBPMNDiagram();

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

            } else {
                tgt = ((new ArrayList<>(bpmnDiagram.getOutEdges(entry))).get(0)).getTarget();
                if( !toVisit.contains(tgt) && !visited.contains(tgt) ) toVisit.add(tgt);
            }
        }

        System.out.println("HM+ - bpmn diagram generated successfully");
    }

}
