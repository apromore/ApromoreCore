package au.edu.qut.bpmn.helper;

import de.hpi.bpt.graph.DirectedEdge;
import de.hpi.bpt.graph.DirectedGraph;
import de.hpi.bpt.graph.abs.IDirectedGraph;
import de.hpi.bpt.graph.algo.rpst.RPST;
import de.hpi.bpt.graph.algo.rpst.RPSTNode;
import de.hpi.bpt.hypergraph.abs.Vertex;
import org.apache.commons.collections.bag.HashBag;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramImpl;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;

import java.util.*;

/**
 * Created by Adriano on 28/11/2016.
 */
public class GatewayMap {
    private int FID;

    private BPMNDiagram bpmnDiagram;
    private DiagramHandler helper;

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

    private LinkedList<Gateway> iorHierachy;

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
                nSRC = diagram.addGateway(src.getLabel(), src.getGatewayType());
                mapping.put(src, nSRC);
            } else nSRC = mapping.get(src);

            if( !mapping.containsKey(tgt) ) {
                nTGT = diagram.addGateway(tgt.getLabel(), tgt.getGatewayType());
                mapping.put(tgt, nTGT);
            } else nTGT = mapping.get(tgt);

            diagram.addFlow(nSRC, nTGT, "");
        }

        return diagram;
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
            boolean xor_homogeneous;
            int fixedJoins = 0;
            while( !rigidHierarchy.isEmpty() ) {
                RPSTNode rigid = rigidHierarchy.remove(0);

                joins = new HashSet<>();
                and_homogeneous = true;
                xor_homogeneous = true;

                IDirectedGraph<DirectedEdge, Vertex> rigidGraph = rigid.getFragment();
                for( Vertex v : rigidGraph.getVertices() ) {
                    gate = idToGate.get(v.getName());
                    if( (outgoings.get(gate).size() > 1) && (gate.getGatewayType() == Gateway.GatewayType.PARALLEL) && (!checked.contains(gate)) ) xor_homogeneous = false;
                    if( (outgoings.get(gate).size() > 1) && (gate.getGatewayType() == Gateway.GatewayType.DATABASED) && (!checked.contains(gate)) ) and_homogeneous = false;
                    if( (incomings.get(gate).size() > 1) && (!checked.contains(gate)) ) joins.add(gate);
                    checked.add(gate);
                }

                if( and_homogeneous ) {
                    fixedJoins += joins.size();
                    for(Gateway j : joins) j.setGatewayType(Gateway.GatewayType.PARALLEL);
                }

                if( xor_homogeneous ) {
                    fixedJoins += joins.size();
                    for(Gateway j : joins) j.setGatewayType(Gateway.GatewayType.DATABASED);
                }

                System.out.println("DEBUG - joins set: " + fixedJoins);
            }

        } catch( Exception e ) {
            e.printStackTrace(System.out);
            System.out.println("ERROR - impossible to set join gateways inside the rigids");
        }
    }

    public boolean generateMap(BPMNDiagram diagram) {
        ArrayList<BPMNNode> toVisit = new ArrayList<>();
        HashSet<BPMNNode> visited = new HashSet<>();
        ArrayList<BPMNNode> tasks;
        BPMNNode tmpChild;
        BPMNNode entry;
        BPMNNode exitGate = null;

        bpmnDiagram = diagram;

        flows = new HashSet<>();
        gateways = new HashSet<>();
        incomings = new HashMap<>();
        outgoings = new HashMap<>();
        successors = new HashMap<>();
        predecessors = new HashMap<>();
        graph = new HashMap<>();

        helper = new DiagramHandler();
        helper.normalizeGateways(diagram);

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


        if( gateways.contains(tmpChild) ) {
            this.entry = tmpChild;
            entry = tmpChild;
        }

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
                tasks.add(0, tmpChild);

                if( !gateways.contains(tmpChild) && !tmpChild.equals(exit) ) {
                    //found a node with multiple children that is not a gateway OR a node with zero children that is not the mainExit
                    System.out.println("ERROR - found a weird node");
                    return false;
                }

                if( gateways.contains(tmpChild) ) this.addFlow(entry, tmpChild, tasks.get(tasks.size()-2), tasks.get(1));
                if( tmpChild.equals(exit) ) exitGate = entry;
                if( !toVisit.contains(tmpChild) && !visited.contains(tmpChild) ) toVisit.add(0, tmpChild);
            }
        }
        exit = exitGate;
        System.out.println("DEBUG - exit gate: " + exit.getLabel() );
        System.out.println("DEBUG - gateways: " + gateways.size() );
        System.out.println("DEBUG - flows: " + flows.size() );
        checkGateways();
//        removeOneBlockBonds();
        exploreMap();

        return true;
    }

    private boolean init(BPMNDiagram diagram) {
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

            for( BPMNNode s : starts ) {
                entry = s;
                //System.out.println("DEBUG - entry: " + s);
                //System.out.println("DEBUG - graph.entry: " + graph.getEntry());
                if( !parents.containsKey(s) ) {
                    parents.put(s, new ArrayList<BPMNNode>());
                    //System.out.println("DEBUG - added entry in parents.");
                } else {
                    System.out.println("ERROR - found one single entry but with parent nodes.");
                    return false;
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
                    return false;
                }
            }
        } else {
            System.out.println("ERROR - found multiple entry(" + starts.size() + ") or exit(" + ends.size() + ") points.");
            return false;
        }

        System.out.println("DEBUG - initialization done.");
        return true;
    }

    private boolean checkGateways() {
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

    private void exploreMap() {
        if( ! (entry instanceof Gateway) ) {
            System.out.println("ERROR - something went wrong with the entry gateway");
            return;
        }

        generateGateHierarchy((Gateway) entry);
        detectLoops((Gateway) entry);
    }

    private void generateGateHierarchy(Gateway entry) {
        iorHierachy = new LinkedList<>();

        LinkedList<Gateway> toVisit = new LinkedList<>();
        Set<Gateway> visited = new HashSet<>();

        toVisit.add(entry);
        visited.add(entry);

        while( !toVisit.isEmpty() ) {
            entry = toVisit.remove(0);
            if( entry.getGatewayType() == Gateway.GatewayType.INCLUSIVE ) iorHierachy.addLast(entry);

            for( Gateway next : successors.get(entry) )
                if( !visited.contains(next) ) {
                    toVisit.addLast(next);
                    visited.add(next);
                }
        }

        System.out.println("DEBUG - ior hierarchy size: " + iorHierachy.size());
    }

    private void detectLoops(Gateway entry) {
        int l = 0;


        HashSet<Gateway> unvisited = new HashSet<>(outgoings.keySet());
        HashSet<Gateway> visiting = new HashSet<>();
        HashMap<Gateway, Boolean> visited = new HashMap<>();

        HashSet<GatewayMapFlow> loopEdges = new HashSet<>();
        HashSet<GatewayMapFlow> forwardEdges = new HashSet<>();

        //System.out.println("DEBUG - outgoing size: " + unvisited.size() );

        exploreLoops(entry, unvisited, visiting, visited, loopEdges, forwardEdges);

        //System.out.println("DEBUG - forwardEdges size: " + forwardEdges.size() );
        //System.out.println("DEBUG - loops size: " + loopEdges.size() );
        for( GatewayMapFlow flow : loopEdges )
            if( !forwardEdges.contains(flow) ) {
                flow.setLoop();
                l++;
            }

        System.out.println("DEBUG - detected loops: " + l);
    }

    private boolean exploreLoops(Gateway entry, HashSet<Gateway> unvisited, HashSet<Gateway> visiting,
                                 HashMap<Gateway, Boolean> visited, HashSet<GatewayMapFlow> loopEdges,
                                 HashSet<GatewayMapFlow> forwardEdges )
    {
        Gateway next;
        boolean loopEdge = false;
        boolean forwardEdge = false;

        unvisited.remove(entry);
        visiting.add(entry);

        if( entry == exit ) forwardEdge = true; //if we reached the exit gateway, it means we can definitely go forward (to the end event, that is not present in the gateway map)

        for( GatewayMapFlow flow : outgoings.get(entry) ) {
            next = flow.tgt;
            if( unvisited.contains(next) ) {
                if( exploreLoops(next, unvisited, visiting, visited, loopEdges, forwardEdges) ) {
                    loopEdge = true;
                    loopEdges.add(flow);
                } else {
                    forwardEdge = true;
                    forwardEdges.add(flow);
                }
            } else if( visiting.contains(next) ) {
                loopEdge = true;
                loopEdges.add(flow);
            } else if( visited.containsKey(next) ) {
                if( visited.get(next) ) {
                    loopEdge = true;
                    loopEdges.add(flow);
                } else {
                    forwardEdge = true;
                    forwardEdges.add(flow);
                }
            }
        }

        visiting.remove(entry);
        visited.put(entry, (loopEdge && !forwardEdge));

        return (loopEdge && !forwardEdge);
    }


    /* this part is about the soundness fixing */

    public void detectIORs() {
        Gateway.GatewayType dType;
        GatewayMapFlow gmFlow;
        Gateway ior;
        Gateway xor;
        BPMNNode first;
        BPMNNode last;
        HashMap<GatewayMapFlow, Gateway> xors;
        HashMap<GatewayMapFlow, ArrayList<Gateway>> backGates;
        HashMap<GatewayMapFlow, HashSet<GatewayMapFlow>> visited;
        HashMap<GatewayMapFlow, HashSet<Gateway>> toVisit;

        int length = iorHierachy.size();

        for(int i = 0; i < length; i++) {
            ior = iorHierachy.get(i);

            xors = new HashMap<>();
            backGates = new HashMap<>();
            visited = new HashMap<>();
            toVisit = new HashMap<>();

            for( GatewayMapFlow iFlow : new HashSet<>(incomings.get(ior)) ) {
                if( iFlow.loop ) continue;
                last = iFlow.last;

                for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> oe : new HashSet<>(bpmnDiagram.getOutEdges(last)) ) {
                    if( oe.getTarget() == ior ) {
                        bpmnDiagram.removeEdge(oe);
                        xor = bpmnDiagram.addGateway("xor_"+iFlow.id, Gateway.GatewayType.DATABASED);
                        bpmnDiagram.addFlow(last, xor, "");
                        bpmnDiagram.addFlow(xor, ior, "");

                        this.addGateway(xor);

                        first = (iFlow.first == ior ? xor : iFlow.first);
                        gmFlow = this.addFlow(iFlow.src, xor, first, last);

                        backGates.put(gmFlow, new ArrayList<Gateway>());
                        toVisit.put(gmFlow, new HashSet<Gateway>());
                        toVisit.get(gmFlow).add(gmFlow.src);
                        visited.put(gmFlow, new HashSet<GatewayMapFlow>());
                        visited.get(gmFlow).add(gmFlow);
                        xors.put(gmFlow, xor);

                        this.addFlow(xor, ior, ior, xor);
                        this.removeFlow(iFlow);
                        break;
                    }
                }
            }
            System.out.println("DEBUG - looking for dominator of: " + ior.getLabel());
            System.out.println("DEBUG - xors: " + xors.size());
            dType = replaceIOR(backGates, toVisit, visited, xors);
            ior.setGatewayType(dType);
        }

        helper.removeTrivialGateways(bpmnDiagram);
    }

    private Gateway.GatewayType replaceIOR(HashMap<GatewayMapFlow, ArrayList<Gateway>> backGates, HashMap<GatewayMapFlow,
                            HashSet<Gateway>> toVisit, HashMap<GatewayMapFlow, HashSet<GatewayMapFlow>> visited,
                                           HashMap<GatewayMapFlow, Gateway> xors)
    {
        HashSet<Gateway> tmp;
        Gateway dominator = null;
        boolean stop = true;

        for( GatewayMapFlow f : toVisit.keySet() ) {
            stop = false;
            tmp = new HashSet<>();
            for( Gateway g : toVisit.get(f) ) {
                System.out.println("DEBUG - to visit: " + g.getLabel());
                backGates.get(f).add(0, g);
                visited.get(f).addAll(incomings.get(g));
                tmp.addAll(predecessors.get(g));
            }
            toVisit.get(f).clear();
            tmp.removeAll(backGates.get(f));
            toVisit.get(f).addAll(tmp);
        }

        if( stop ) {
            System.out.println("WARNING - no dominators can be found");
            return null;
        }

        tmp = new HashSet<>();
        for( GatewayMapFlow f : backGates.keySet() ) tmp.addAll(backGates.get(f));
        for( GatewayMapFlow f : backGates.keySet() ) tmp.retainAll(backGates.get(f));

        if( tmp.isEmpty() ) {
            System.out.println("DEBUG - no dominators");
            return replaceIOR(backGates, toVisit, visited, xors);
        } else {
            for(Gateway d : tmp) {
                //here we should select the closest dominator!
                System.out.println("DEBUG - found dominators: " + d.getLabel());
                dominator = d;
            }

            if( dominator.getGatewayType() == Gateway.GatewayType.DATABASED ) {
                System.out.println("WARNING - found a XOR dominator");
                return Gateway.GatewayType.DATABASED;
            }

            for( GatewayMapFlow f : backGates.keySet() ) {
                while( backGates.get(f).remove(0) != dominator );
                for( Gateway g : backGates.get(f) )
                    for( GatewayMapFlow ff : new HashSet<>(outgoings.get(g)) ) {
                        if( visited.get(f).contains(ff) || (g.getGatewayType() == Gateway.GatewayType.PARALLEL) ) continue;
                        else createTokenGenerator(g, ff, xors.get(f));
                    }
            }
        }

        return Gateway.GatewayType.PARALLEL;
    }

    private void createTokenGenerator(Gateway eGate, GatewayMapFlow eFlow, Gateway xor) {
        Gateway and;
        BPMNNode last;

        if( (eFlow.first instanceof Gateway) && (((Gateway) eFlow.first).getGatewayType() == Gateway.GatewayType.PARALLEL) && (outgoings.get(eFlow.first).size() > 1) ) and = (Gateway) eFlow.first;
        else {
            and = bpmnDiagram.addGateway("and"+eFlow.id, Gateway.GatewayType.PARALLEL);
            bpmnDiagram.addFlow(eGate, and, "");
            bpmnDiagram.addFlow(and, eFlow.first, "");

            this.addGateway(and);
            this.addFlow(eGate, and, and, eGate);

            last = (eFlow.last == eGate ? and : eFlow.last);
            this.addFlow(and, eFlow.tgt, eFlow.first, last);

            this.removeFlow(eFlow);

            for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> oe : new HashSet<>(bpmnDiagram.getOutEdges(eGate)) )
                if( oe.getTarget() == eFlow.first ) bpmnDiagram.removeEdge(oe);
        }

        bpmnDiagram.addFlow(and, xor, "fake-token");
        this.addFlow(and, xor, xor, and);

    }


    /* data objects management */

    private void addGateway(Gateway gate) {
        gateways.add(gate);
        incomings.put(gate, new HashSet<GatewayMapFlow>());
        outgoings.put(gate, new HashSet<GatewayMapFlow>());
        successors.put(gate, new HashSet<Gateway>());
        predecessors.put(gate, new HashSet<Gateway>());
        graph.put(gate, new HashMap<Gateway, Set<GatewayMapFlow>>());
    }

    private void removeGateway(Gateway gate) {
        gateways.remove(gate);
        incomings.remove(gate);
        outgoings.remove(gate);
        successors.remove(gate);
        predecessors.remove(gate);
        graph.remove(gate);
    }

    private GatewayMapFlow addFlow(BPMNNode src, BPMNNode tgt, BPMNNode first, BPMNNode last) {
        if( !((src instanceof Gateway) && (tgt instanceof Gateway)) ) {
            System.out.println("WARNING - cannot add the flow: not a flow instance");
            return null;
        }
        Gateway entry = (Gateway) src;
        Gateway exit = (Gateway) tgt;
        GatewayMapFlow flow = new GatewayMapFlow(++FID, entry, exit, first, last);

        flows.add(flow);
        incomings.get(exit).add(flow);
        outgoings.get(entry).add(flow);
        successors.get(entry).add(exit);
        predecessors.get(exit).add(entry);

        if( !graph.get(entry).containsKey(exit) ) graph.get(entry).put(exit, new HashSet<GatewayMapFlow>());
        graph.get(entry).get(exit).add(flow);

        return flow;
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
    }


    /* private classes */

    private class GatewayMapFlow implements Comparable {
        int id;
        Gateway src; //src = the entry gateway of this Flow
        Gateway tgt; //tgt = the exit gateway of this Flow

        BPMNNode first; //this is the successor BPMN node seen from the entry of this Flow (that is src)
        BPMNNode last;  //this is the predecessor BPMN node seen from the exit of this Flow (that is: tgt)
        //NOTE: if this flow is a direct edge from SRC to TGT (i.e. no activities in between) -> first == tgt AND last == src

        boolean loop;

        GatewayMapFlow(int id, Gateway src, Gateway tgt, BPMNNode first, BPMNNode last) {
            this.id = id;
            this.src = src;
            this.tgt = tgt;
            this.last = last;
            this.first = first;
            this.loop = false;
        }

        boolean isLoop(){ return loop; }
        void setLoop() { loop = true; }

        BPMNNode getFirst() { return first; }
        BPMNNode getLast() { return last; }
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

