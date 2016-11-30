package au.edu.qut.processmining.miners.heuristic;

import au.edu.qut.bpmn.helper.DiagramHandler;
import au.edu.qut.bpmn.helper.GatewayMap;
import au.edu.qut.processmining.log.LogParser;
import au.edu.qut.processmining.log.SimpleLog;
import au.edu.qut.processmining.miners.heuristic.net.HeuristicNet;
import au.edu.qut.processmining.miners.heuristic.oracle.Oracle;
import au.edu.qut.processmining.miners.heuristic.oracle.OracleItem;
import de.hpi.bpt.graph.DirectedEdge;
import de.hpi.bpt.graph.DirectedGraph;
import de.hpi.bpt.graph.abs.IDirectedGraph;
import de.hpi.bpt.graph.algo.rpst.RPST;
import de.hpi.bpt.graph.algo.rpst.RPSTNode;
import de.hpi.bpt.hypergraph.abs.Vertex;
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

    private int gateCounter;
    private HashMap<String, Gateway> candidateJoins;

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

        BPMNNode tgt;
        gateCounter = Integer.MIN_VALUE;

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
        candidateJoins = new HashMap<>();

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

        /* generating join gateways */
        while( generateBondJoins() );

        /* generating join gateways for sound rigids */
        generateRigidJoins();

        System.out.println("HM+ - bpmn diagram generated successfully");
    }

    private void generateSplitGateways(BPMNNode entry, OracleItem nextOracleItem, Map<Integer, BPMNNode> mapping) {
        Gateway.GatewayType type = nextOracleItem.getGateType();
        BPMNNode node;
        Integer nodeCode;
        Gateway gate;
        Gateway candidateJoin;

        System.out.println("DEBUG - generating split from Oracle ~ [xor|and]: " + nextOracleItem + " ~ [" + nextOracleItem.getXorBrothers().size() + "|" + nextOracleItem.getAndBrothers().size() + "]");

        if( candidateJoins.containsKey(nextOracleItem.toString()) ) {
            //these are joins, they are generated considering the fact they shares the same future (finalOracleItem)
            System.out.println("DEBUG - found JOIN for: " + nextOracleItem.toString());
            candidateJoin = candidateJoins.get(nextOracleItem.toString());
            bpmnDiagram.addFlow(entry, candidateJoin, "");
            return;
        }

        if( type == null ) {
            nodeCode = nextOracleItem.getNodeCode();
            if( nodeCode != null ) {
                node = mapping.get(nodeCode);
                bpmnDiagram.addFlow(entry, node, "");
            } else System.out.println("ERROR - found an oracle item without brother and more than one element in its past");
            return;
        }

        gate = bpmnDiagram.addGateway(Integer.toString(gateCounter++), type);
        bpmnDiagram.addFlow(entry, gate, "");
        for( OracleItem next : nextOracleItem.getXorBrothers() ) generateSplitGateways(gate, next, mapping);
        for( OracleItem next : nextOracleItem.getAndBrothers() ) generateSplitGateways(gate, next, mapping);

        candidateJoins.put(nextOracleItem.toString(), gate);
    }

    private boolean generateBondJoins() {
        HashSet<String> changed = new HashSet<>();

        try {
            HashMap<String, BPMNNode> nodes = new HashMap<>();
            HashMap<BPMNNode, Vertex> vertexes = new HashMap<BPMNNode, Vertex>();

            HashMap<String, Gateway.GatewayType> gates = new HashMap<String, Gateway.GatewayType>();
            ArrayList<RPSTNode> bondHierarchy = new ArrayList<RPSTNode>();

            IDirectedGraph<DirectedEdge, Vertex> graph = new DirectedGraph();
            Vertex src;
            Vertex tgt;

            BPMNNode bpmnSRC;
            BPMNNode bpmnTGT;
            Gateway gate;

            String entry, exit;


            /* building the graph from the bpmnDiagram, the graph is necessary to generate the RPST */

            for( Flow f : bpmnDiagram.getFlows((Swimlane) null) ) {
                bpmnSRC = f.getSource();
                bpmnTGT = f.getTarget();
                if( !vertexes.containsKey(bpmnSRC) ) {
                    src = new Vertex(bpmnSRC.getLabel());  //this is still a unique number
                    if( bpmnSRC instanceof Gateway ) gates.put(bpmnSRC.getLabel(), ((Gateway) bpmnSRC).getGatewayType());
                    vertexes.put(bpmnSRC, src);
                    nodes.put(bpmnSRC.getLabel(), bpmnSRC);
                } else src = vertexes.get(bpmnSRC);

                if( !vertexes.containsKey(bpmnTGT) ) {
                    tgt = new Vertex(bpmnTGT.getLabel());  //this is still a unique number
                    if( bpmnTGT instanceof Gateway ) gates.put(bpmnTGT.getLabel(), ((Gateway) bpmnTGT).getGatewayType());
                    vertexes.put(bpmnTGT, tgt);
                    nodes.put(bpmnTGT.getLabel(), bpmnTGT);
                } else tgt = vertexes.get(bpmnTGT);

                graph.addEdge(src, tgt);
            }

            /* graph ready */


            RPST rpst = new RPST(graph);

            RPSTNode root = rpst.getRoot();
            LinkedList<RPSTNode> toAnalize = new LinkedList<RPSTNode>();
            toAnalize.addLast(root);

            while( toAnalize.size() != 0 ) {
                root = toAnalize.removeFirst();

                for( RPSTNode n : new HashSet<RPSTNode>(rpst.getChildren(root)) ) {
                    switch( n.getType() ) {
                        case R:
                            toAnalize.addLast(n);
                            break;
                        case T:
                            break;
                        case P:
                            toAnalize.addLast(n);
                            break;
                        case B:
                            exit = n.getExit().getName();
                            if( !gates.containsKey(exit) ) {
                                System.out.println("DEBUG - found a bond exit (" + exit + ") that is not a gateway");
                                bondHierarchy.add(0, n);
                            }
                            toAnalize.addLast(n);
                            break;
                        default:
                    }
                }
            }

            System.out.println("DEBUG - starting analysing bonds: " + bondHierarchy.size() );
            int debugChecker;
            HashMap<String, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> removableEdges;
            HashSet<String> toRemove;

            while( !bondHierarchy.isEmpty() ) {
                RPSTNode bond = bondHierarchy.remove(0);
                entry = bond.getEntry().getName();
                exit = bond.getExit().getName();
                System.out.println("DEBUG - analysing a bond with [ split gate : join task ] = [ " + gates.get(entry) + " : " + exit + " ]");

                if( changed.contains(exit) ) continue;
                changed.add(exit);

                debugChecker = rpst.getChildren(bond).size();

                removableEdges = new HashMap<>();
                toRemove = new HashSet<>();
                bpmnTGT = nodes.get(exit);

                for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> oe : bpmnDiagram.getInEdges(bpmnTGT) )
                    removableEdges.put(oe.getSource().getLabel(), oe);

                IDirectedGraph<DirectedEdge, Vertex> bondGraph = bond.getFragment();
                for( Vertex v : bondGraph.getVertices() )
                    if( v.getName().equals(exit) ) {
                        // editing the bpmn diagram
                        gate = bpmnDiagram.addGateway(Integer.toString(gateCounter++), gates.get(entry));
                        System.out.println("DEBUG - added a split");
                        bpmnDiagram.addFlow(gate, bpmnTGT, "");
                        for( de.hpi.bpt.graph.abs.AbstractDirectedEdge e : bondGraph.getEdgesWithTarget(v) ) {
                            debugChecker--;
                            entry = e.getSource().getName();
                            toRemove.add(entry);
                            bpmnSRC = nodes.get(entry);
                            bpmnDiagram.addFlow(bpmnSRC, gate, "");
                        }
                    }

                for( String re : toRemove ) bpmnDiagram.removeEdge(removableEdges.get(re));
                System.out.println("DEBUG - checker: 0 = " + debugChecker );
            }

        } catch( Exception e ) {
            e.printStackTrace(System.out);
            System.out.println("ERROR - impossible to generate split gateways");
            return false;
        }

        System.out.println("DEBUG - generate bond join: " + (!changed.isEmpty()) );
        return !changed.isEmpty();
    }

    private boolean generateRigidJoins() {
        HashSet<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> removableEdges;
        Set<BPMNNode> nodes = new HashSet<>(bpmnDiagram.getNodes());
        Gateway gate;

        /* step 1. adding a xor join gateway where needed */
        for( BPMNNode n : nodes ) {
            removableEdges = new HashSet<>(bpmnDiagram.getInEdges(n));
            if( removableEdges.size() <= 1 ) continue;
            if( (n instanceof Gateway) && (bpmnDiagram.getOutEdges(n).size() <= 1) ) continue; //we generate a new join only for those gateways that are both join and split
//            System.out.println("DEBUG - generating a new join");
            gate = bpmnDiagram.addGateway(Integer.toString(gateCounter++), Gateway.GatewayType.DATABASED);
            bpmnDiagram.addFlow(gate, n, "");
            for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> oe : removableEdges) {
                bpmnDiagram.removeEdge(oe);
                bpmnDiagram.addFlow(oe.getSource(), gate, "");
            }
        }

        /* at this point, all the xor-homogeneous rigids are already set
        *  what is left is:
        *  and-homogeneous (will be fixed in the next step)
        *  heterogeneous rigids (will be fixed with bartek)
        *  */

        /* step 2. checking all the rigids in the RPST bottom-up
        * if a rigid contains only XOR nothing is done
        * if a rigid contains only AND splits, all the XOR joins (formerly created at step 1) are turned into AND joins
        * if none of the above cases, the rigid soundness will be successively fixed with Bartek technique
         */

        GatewayMap gatemap = new GatewayMap();
        gatemap.generateMap(bpmnDiagram);
        gatemap.removeOneBlockBonds();
        gatemap.setHomogenousRigidJoins();

        return true;
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
