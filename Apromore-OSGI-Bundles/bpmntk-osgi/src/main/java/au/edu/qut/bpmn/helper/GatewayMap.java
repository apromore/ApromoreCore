package au.edu.qut.bpmn.helper;

import de.hpi.bpt.graph.DirectedEdge;
import de.hpi.bpt.graph.DirectedGraph;
import de.hpi.bpt.graph.abs.IDirectedGraph;
import de.hpi.bpt.graph.algo.rpst.RPST;
import de.hpi.bpt.graph.algo.rpst.RPSTNode;
import de.hpi.bpt.hypergraph.abs.Vertex;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramImpl;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.processmining.models.graphbased.directed.bpmn.elements.Swimlane;

import java.util.*;

/**
 * Created by Adriano on 28/11/2016.
 */
public class GatewayMap {
    private int FID;

    /* about the initialization */
    private BPMNNode entry;
    private BPMNNode exit;
    private Map<BPMNNode, List<BPMNNode>> children;
    private Map<BPMNNode, List<BPMNNode>> parents;

    /* about the map itself */
    private Set<Gateway> gateways;
    private Set<GatewayMapFlow> flows;
    private Map<Gateway, Set<GatewayMapFlow>> incomings;
    private Map<Gateway, Set<GatewayMapFlow>> outgoings;
    private Map<Gateway, Set<Gateway>> successors; //successor gateways
    private Map<Gateway, Set<Gateway>> predecessors; //predecessor gateways
    private Map<Gateway, Map<Gateway, Set<GatewayMapFlow>>> graph;


    public GatewayMap() {}

    public BPMNDiagram getGatewayMap() {
        BPMNDiagram diagram = new BPMNDiagramImpl("gatemap");
        Gateway src, tgt;
        Gateway nSRC, nTGT;

        HashMap<Gateway, Gateway> mapping = new HashMap<>();

        for( GatewayMapFlow flow : flows ) {
            src = flow.getSource();
            tgt = flow.getTarget();

            if( !mapping.containsKey(src) ) {
                nSRC = diagram.addGateway("", src.getGatewayType());
                mapping.put(src, nSRC);
            } else nSRC = mapping.get(src);

            if( !mapping.containsKey(tgt) ) {
                nTGT = diagram.addGateway("", tgt.getGatewayType());
                mapping.put(tgt, nTGT);
            } else nTGT = mapping.get(tgt);

            diagram.addFlow(nSRC, nTGT, "");
        }

        return diagram;
    }

    public boolean generateMap(BPMNDiagram diagram) {
        ArrayList<BPMNNode> toVisit = new ArrayList<>();
        HashSet<BPMNNode> visited = new HashSet<>();
        ArrayList<BPMNNode> tasks;
        BPMNNode tmpChild;
        BPMNNode entry;

        flows = new HashSet<>();
        gateways = new HashSet<>();
        incomings = new HashMap<>();
        outgoings = new HashMap<>();
        successors = new HashMap<>();
        predecessors = new HashMap<>();
        graph = new HashMap<>();

        DiagramHandler.normalizeGateways(diagram);

        if( !init(diagram) ) return false;

        System.out.println("DEBUG - starting generation of the map");

        entry = this.entry;
        tmpChild = this.entry;

        System.out.println("DEBUG - path entry: " + tmpChild);
        while( !gateways.contains(tmpChild) && (children.get(tmpChild).size() == 1) ) {
            //tmpChild is not a gateway neither the mainExit neither something weird
            tmpChild = children.get(tmpChild).get(0);
            System.out.println("DEBUG - next child: " + tmpChild);
        }
        System.out.println("DEBUG - path exit: " + tmpChild);


        if( gateways.contains(tmpChild) ) { entry = tmpChild; }

        //System.out.println("DEBUG - generating paths.");
        toVisit.add(0, entry);
        visited.add(exit);

        FID = 0;
        while( toVisit.size() != 0 ) {
            entry = toVisit.remove(0);
            visited.add(entry);

            //System.out.println("DEBUG - visiting: " + entry);

            for( BPMNNode child : children.get(entry) ) {
                tmpChild = child;
                tasks = new ArrayList<>();

                tasks.add(0, entry);
                while( !gateways.contains(tmpChild) && (children.get(tmpChild).size() == 1) ) {
                    //tmpChild is not a gateway neither the mainExit neither something weird
                    tasks.add(0, tmpChild);
                    tmpChild = children.get(tmpChild).get(0);
                }

                if( !gateways.contains(tmpChild) && !tmpChild.equals(exit) ) {
                    //found a node with multiple children that is not a gateway OR a node with zero children that is not the mainExit
                    System.out.println("ERROR - found a weird node");
                    return false;
                }

                if( gateways.contains(tmpChild) ) this.addFlow(entry, tmpChild, tasks.get(0));
                if( !toVisit.contains(tmpChild) && !visited.contains(tmpChild) ) toVisit.add(0, tmpChild);
            }
        }

        System.out.println("DEBUG - gateways: " + gateways.size() );
        System.out.println("DEBUG - flows: " + flows.size() );
        checkGateways();
        return true;
    }

    public int removeOneBlockBonds() {
        int bonds = 0;
        HashSet<GatewayMapFlow> removableFlows;

        do {
            removableFlows = new HashSet<>();
            for( Gateway entry : gateways )
                if( (successors.get(entry).size() == 1) && (outgoings.get(entry).size() > 1) ) {
                    System.out.println("DEBUG - found a removable bond");
                    //this means g is the entry of a one-block-BOND and its successor is the exit
                    bonds++;
                    for (GatewayMapFlow f : outgoings.get(entry)) removableFlows.add(f);
                    break;
                }
            for (GatewayMapFlow rf : removableFlows) this.removeFlow(rf);
        } while ( !removableFlows.isEmpty() );

        System.out.println("DEBUG - bonds removed: " + bonds);
        return bonds;
    }



    public void setHomogenousRigidJoins() {
        try {
            HashMap<String, Gateway> idToGate = new HashMap<>();
            HashMap<BPMNNode, Vertex> vertexes = new HashMap<BPMNNode, Vertex>();
            ArrayList<RPSTNode> rigidHierarchy = new ArrayList<RPSTNode>();
            HashSet<Gateway> checked = new HashSet<>();

            IDirectedGraph<DirectedEdge, Vertex> graph = new DirectedGraph();
            Vertex src;
            Vertex tgt;

            Gateway gSRC;
            Gateway gTGT;

            /* building the graph from the bpmnDiagram, the graph is necessary to generate the RPST */

            for( GatewayMapFlow f : flows ) {
                gSRC = f.getSource();
                gTGT = f.getTarget();
                if( !vertexes.containsKey(gSRC) ) {
                    src = new Vertex(gSRC.getId().toString());  //this is still a unique number
                    vertexes.put(gSRC, src);
                    idToGate.put(gSRC.getId().toString(), gSRC);
                } else src = vertexes.get(gSRC);

                if( !vertexes.containsKey(gTGT) ) {
                    tgt = new Vertex(gTGT.getId().toString());  //this is still a unique number
                    vertexes.put(gTGT, tgt);
                    idToGate.put(gTGT.getId().toString(), gTGT);
                } else tgt = vertexes.get(gTGT);

                graph.addEdge(src, tgt);
            }

            /* graph ready - building the RPST */

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
                            rigidHierarchy.add(0, n);
                            break;
                        case T:
                            break;
                        case P:
                            toAnalize.addLast(n);
                            break;
                        case B:
                            toAnalize.addLast(n);
                            System.out.println("WARNING - found a bond in the gateway map, after the removal.");
                            break;
                        default:
                    }
                }
            }

            /* working on the hierarchy of the rigids */
            Gateway gate;
            HashSet<Gateway> joins;
            boolean and_homogeneous;
            int fixedJoins = 0;
            while( !rigidHierarchy.isEmpty() ) {
                RPSTNode rigid = rigidHierarchy.remove(0);

                joins = new HashSet<>();
                and_homogeneous = true;

                IDirectedGraph<DirectedEdge, Vertex> rigidGraph = rigid.getFragment();
                for( Vertex v : rigidGraph.getVertices() ) {
                    gate = idToGate.get(v.getName());
                    if( (outgoings.get(gate).size() > 1) && (gate.getGatewayType() == Gateway.GatewayType.DATABASED) && (!checked.contains(gate)) ) and_homogeneous = false;
                    if( (incomings.get(gate).size() > 1) && (!checked.contains(gate)) ) joins.add(gate);
                    checked.add(gate);
                }

                if( and_homogeneous ) {
                    fixedJoins += joins.size();
                    for(Gateway j : joins) j.setGatewayType(Gateway.GatewayType.PARALLEL);
                }

                System.out.println("DEBUG - joins set: " + fixedJoins);
            }

        } catch( Exception e ) {
            e.printStackTrace(System.out);
            System.out.println("ERROR - impossible to set join gateways inside the rigids");
        }
    }

    public boolean checkGateways() {
        int splits = 0;
        int joins = 0;
        int errors = 0;
        boolean isSplit, isJoin;

        for(Gateway g : gateways) {
            if( isSplit = (outgoings.get(g).size() > 1) ) splits++;
            if( isJoin = (incomings.get(g).size() > 1) ) joins++;
            if( isJoin && isSplit ) errors++;
        }

        System.out.println("DEBUG - splits found: " + splits );
        System.out.println("DEBUG - joins found: " + joins );
        System.out.println("DEBUG - errors (join/split) found: " + errors );

        return (errors == 0);
    }

    private boolean init(BPMNDiagram diagram) {
        boolean isValid;

        HashSet<BPMNNode> starts = new HashSet<>();
        HashSet<BPMNNode> ends = new HashSet<>();

        BPMNNode src;
        BPMNNode tgt;

        this.children = new HashMap<>();
        this.parents = new HashMap<>();

        for( Gateway g : diagram.getGateways() ) this.addGateway(g);

        starts.addAll(diagram.getNodes());
        ends.addAll(diagram.getNodes());

        for( Flow f : diagram.getFlows() ) {
            src = f.getSource();
            tgt = f.getTarget();

            if( !children.containsKey(src) ) children.put(src, new ArrayList<BPMNNode>());
            children.get(src).add(tgt);

            if( !parents.containsKey(tgt) ) parents.put(tgt, new ArrayList<BPMNNode>());
            parents.get(tgt).add(src);

            ends.remove(src);
            starts.remove(tgt);
        }

        if( (ends.size() == 1) && (starts.size() == 1) ) {
            isValid = true;

            for( BPMNNode s : starts ) {
                entry = s;
                //System.out.println("DEBUG - entry: " + s);
                //System.out.println("DEBUG - graph.entry: " + graph.getEntry());
                if( !parents.containsKey(s) ) {
                    parents.put(s, new ArrayList<BPMNNode>());
                    //System.out.println("DEBUG - added entry in parents.");
                } else {
                    System.out.println("ERROR - found one single entry but with parent nodes.");
                    isValid = false;
                }
            }

            for( BPMNNode s : ends ) {
                exit = s;
                //System.out.println("DEBUG - exit: " + s);
                //System.out.println("DEBUG - graph.exit: " + graph.getExit());
                if( !children.containsKey(s) ) {
                    children.put(s, new ArrayList<BPMNNode>());
                    //System.out.println("DEBUG - added exit in children.");
                } else {
                    System.out.println("ERROR - found one single exit but with children nodes.");
                    isValid = false;
                }
            }
        } else {
            System.out.println("ERROR - found multiple entry(" + starts.size() + ") or exit(" + ends.size() + ") points.");
            isValid = false;
        }

        System.out.println("DEBUG - initialization done.");
        return isValid;
    }


    private void addGateway(Gateway gate) {
        gateways.add(gate);
        incomings.put(gate, new HashSet<GatewayMapFlow>());
        outgoings.put(gate, new HashSet<GatewayMapFlow>());
        successors.put(gate, new HashSet<Gateway>());
        predecessors.put(gate, new HashSet<Gateway>());
        graph.put(gate, new HashMap<Gateway, Set<GatewayMapFlow>>());
    }

    private void addFlow(BPMNNode src, BPMNNode tgt, BPMNNode ref) {
        if( !((src instanceof Gateway) && (tgt instanceof Gateway)) ) {
            System.out.println("WARNING - not a flow instance");
            return;
        }
        Gateway entry = (Gateway) src;
        Gateway exit = (Gateway) tgt;

        GatewayMapFlow flow = new GatewayMapFlow(++FID, entry, exit, ref);

        flows.add(flow);

        incomings.get(exit).add(flow);
        outgoings.get(entry).add(flow);

        successors.get(entry).add(exit);
        predecessors.get(exit).add(entry);

        if( !graph.get(entry).containsKey(exit) ) graph.get(entry).put(exit, new HashSet<GatewayMapFlow>());
        graph.get(entry).get(exit).add(flow);
    }

    private void removeFlow(GatewayMapFlow flow) {
        Gateway entry = flow.getSource();
        Gateway exit = flow.getTarget();

        System.out.println("DEBUG - removing flow");

        flows.remove(flow);

        outgoings.get(entry).remove(flow);
        incomings.get(exit).remove(flow);

        successors.get(entry).remove(exit);
        predecessors.get(exit).remove(entry);

        graph.get(entry).get(exit).remove(flow);

        if( graph.get(entry).get(exit).isEmpty() ) mergeGateways(entry, exit);
    }

    private void mergeGateways(Gateway entry, Gateway exit) {
        Gateway prevSRC = null;
        Gateway nextTGT = null;
        BPMNNode ref = null;

        System.out.println("DEBUG - merging gateways");
        GatewayMapFlow iRemovable = null;
        GatewayMapFlow oRemovable = null;

        if( (incomings.get(entry).size() != 1) || (outgoings.get(exit).size() != 1) ) {
            //this can happen when entry or exit is a gateway that is going to be removed (i.e. with no incoming and outgoing flows), this is not a error
            //also, this can happen in the presence of JOIN/SPLIT gateways, this would be an error
            //however, this latte case should never happen, because the map is checked for JOIN/SPLIT gateways and eventually transformed into joins followed by splits
            System.out.println("DEBUG - cannot merge gateways [" + incomings.get(entry).size() + " - " + outgoings.get(exit).size() + "]");
            return;
        }

        for( GatewayMapFlow i : incomings.get(entry) ) {
            prevSRC = i.getSource();
            iRemovable = i;
        }

        for( GatewayMapFlow o : outgoings.get(exit) ) {
            nextTGT = o.getTarget();
            ref = o.getRef();
            oRemovable = o;
        }

        if( outgoings.get(entry).size() != 0 ) {
            prevSRC = entry;
            System.out.println("WARNING - this should not happen: outgoings.size != 0");
        } else {
            removeFlow(iRemovable);
            removeGateway(entry);
        }

        if( incomings.get(exit).size() != 0 ) {
            nextTGT = exit;
            ref = null;
            System.out.println("WARNING - this should not happen: incomings.size != 0");
        } else {
            removeFlow(oRemovable);
            removeGateway(exit);
        }

        this.addFlow(prevSRC, nextTGT, ref);
    }

    private void removeGateway(Gateway gate) {
        gateways.remove(gate);
        incomings.remove(gate);
        outgoings.remove(gate);
        successors.remove(gate);
        predecessors.remove(gate);
        graph.remove(gate);
    }


    private class GatewayMapFlow implements Comparable {
        int id;
        Gateway src, tgt;
        BPMNNode ref;

        GatewayMapFlow(int id, Gateway src, Gateway tgt, BPMNNode ref) {
            this.id = id;
            this.src = src;
            this.tgt = tgt;
            this.ref = ref;
        }

        BPMNNode getRef() { return ref; }
        Gateway getSource() { return src; }
        Gateway getTarget() { return tgt; }

        @Override
        public int compareTo(Object o) {
            if(o instanceof GatewayMapFlow) return (this.id - ((GatewayMapFlow)o).id);
            return 1;
        }

        @Override
        public boolean equals(Object o) {
            if(o instanceof GatewayMapFlow) return (this.id == ((GatewayMapFlow)o).id);
            return false;
        }

    }

}

