package au.edu.qut.bpmn.helper;

import de.hpi.bpt.graph.DirectedEdge;
import de.hpi.bpt.graph.DirectedGraph;
import de.hpi.bpt.graph.abs.IDirectedGraph;
import de.hpi.bpt.graph.algo.rpst.RPST;
import de.hpi.bpt.graph.algo.rpst.RPSTNode;
import de.hpi.bpt.hypergraph.abs.Vertex;
import ee.ut.comptech.*;
import org.apache.commons.collections.bag.HashBag;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramImpl;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.processmining.models.shapes.Gate;

import java.util.*;

/**
 * Created by Adriano on 28/11/2016.
 */
public class GatewayMap {
//    flows and gates IDs
    private int FID;
    private int GID;

//    input diagram and diagram handler
    private BPMNDiagram bpmnDiagram;
    private DiagramHandler helper;

//    data used during initialization
    private BPMNNode entry;
    private BPMNNode exit;
    private Map<BPMNNode, List<BPMNNode>> children;
    private Map<BPMNNode, List<BPMNNode>> parents;

//    gateway map data
    private Set<Gateway> gateways;
    private Set<GatewayMapFlow> flows;
    private Map<Gateway, Set<GatewayMapFlow>> incomings;    //this keep tracks of the incoming flows of a gate
    private Map<Gateway, Set<GatewayMapFlow>> outgoings;    //this keep tracks of the outgoing flows of a gate
    private Map<Gateway, Set<Gateway>> successors;          //all the successor gateways of the key gate
    private Map<Gateway, Set<Gateway>> predecessors;        //all the predecessor gateways of the key gate
    private Map<Gateway, Map<Gateway, Set<GatewayMapFlow>>> map;  //the map itself

//    data used to evaluate the dominators
    private Map<Gateway, Integer> gateIDs;
    private Map<Integer, Gateway> idToGate;
    private LinkedList<Gateway> iorHierachy;
    private DominatorTree domTree;


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


//  this methods are about the initialization of the gateway map

    public boolean generateMap(BPMNDiagram diagram) {
//        given an input BPMN diagram, we generate a map containing only its gateways
//        successively we will edit concurrently the gateway map and the BPMN diagram as a mirror
        ArrayList<BPMNNode> toVisit = new ArrayList<>();
        HashSet<BPMNNode> visited = new HashSet<>();
        ArrayList<BPMNNode> tasks;
        BPMNNode tmpChild;
        BPMNNode entry;
        BPMNNode exitGate = null;

//        the input diagram is saved as object attribute and given in input to the diagram handler
        bpmnDiagram = diagram;
        helper = new DiagramHandler();

//        we need to remove any gateway that is both join and split
        helper.removeJoinSplit(diagram);

//        initializing all the data structures of the gateway map
        flows = new HashSet<>();
        gateways = new HashSet<>();
        incomings = new HashMap<>();
        outgoings = new HashMap<>();
        successors = new HashMap<>();
        predecessors = new HashMap<>();
        map = new HashMap<>();


//        we firstly read the diagram to check that it is correct
        if( !init(diagram) ) return false;

//        we move from the start event to the first gateway,
//        and we through away all the activities we find
        tmpChild = this.entry;
        while( !gateways.contains(tmpChild) && (children.get(tmpChild).size() == 1) )
            tmpChild = children.get(tmpChild).get(0);

//        we check that we found the first gateway of the map,
//        and we set it as global entry of the gateway map
        if( gateways.contains(tmpChild) ) {
            this.entry = tmpChild;
            entry = tmpChild;
        } else {
            System.out.println("ERROR - first gateway not found");
            return false;
        }

//        now, we create the map starting from the entry gateway
//        exploration is performed depth-first
        System.out.println("Gatemap - generating paths ...");
        toVisit.add(0, entry);
        visited.add(exit);

        FID = 0;
        while( toVisit.size() != 0 ) {
            entry = toVisit.remove(0);
            visited.add(entry);

            for( BPMNNode child : children.get(entry) ) {
                tmpChild = child;
                tasks = new ArrayList<>();

                tasks.add(0, entry);
                while( !gateways.contains(tmpChild) && (children.get(tmpChild).size() == 1) ) {
//                    tmpChild is not a gateway neither the main exit neither something weird
                    tasks.add(0, tmpChild);
                    tmpChild = children.get(tmpChild).get(0);
                }
                tasks.add(0, tmpChild);
//                tasks is an array list with a gateway in position 0, and a gateway in position length-1
//                in between it contains all the activities (if any) executed between the two gateway
//                note: last element is the entry gateway, the first element is the exit gateway

                if( !gateways.contains(tmpChild) && !tmpChild.equals(exit) ) {
//                    we found a node with multiple children that is not a gateway OR
//                    a node with zero children that is not the main exit
                    System.out.println("ERROR - found a weird node while generating a paths");
                    return false;
                }

                if( gateways.contains(tmpChild) ) this.addFlow(entry, tmpChild, tasks.get(tasks.size()-2), tasks.get(1));
                if( tmpChild.equals(exit) ) exitGate = entry;
                if( !toVisit.contains(tmpChild) && !visited.contains(tmpChild) ) toVisit.add(0, tmpChild);
            }
        }

        exit = exitGate;

//        System.out.println("DEBUG - exit gate: " + exit.getLabel() );
        System.out.println("Gatemap - gateways: " + gateways.size() );

//        at this point we need to check that there are NO join/split gateways (just for debug)
        if( !checkGateways() ) return false;

        System.out.println("Gatemap - flows: " + flows.size() );

//        finally, we explore the gateway map created in order to:
//        1. find loops
//        2. find the IORs hierarchy
//        3. find dominators
        exploreMap();

        return true;
    }

    private boolean init(BPMNDiagram diagram) {
        HashSet<BPMNNode> starts = new HashSet<>();
        HashSet<BPMNNode> ends = new HashSet<>();

        BPMNNode src;
        BPMNNode tgt;

        System.out.println("Gatemap - starting initialization");

        this.children = new HashMap<>();
        this.parents = new HashMap<>();

//        we add all the gateways to the map
        for( Gateway g : diagram.getGateways() ) this.addGateway(g);

//        then, we look for the start and the end event of the BPMN diagram
        starts.addAll(diagram.getNodes());
        ends.addAll(diagram.getNodes());

//        data structures: children and parents are filled
//        they will be used lately to generate the map
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

        System.out.println("Gatemap - initialization completed");
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

        System.out.println("Gatemap - splits: " + splits );
        System.out.println("Gatemap - joins: " + joins );

        if( errors != 0 ) {
            System.out.println("ERROR - found join/split gateways: " + errors );
            return false;
        }

        return true;
    }

    private void exploreMap() {
        if( ! (entry instanceof Gateway) ) {
            System.out.println("ERROR - the entry gateway is NOT a gateway");
            return;
        }

//        this must be the first method to be executed
        detectLoops((Gateway) entry);

//        after we detected the loops, we turn each join gateway that has both:
//        incoming loop edges and incoming forward edges
//        into two different joins: one only for the forward edges, one only for the loops
        normalizeLoopJoins();

//        we explore the map to find the IORs gateway in a hierarchical order
        generateGateHierarchy((Gateway) entry);

//        finally we generate the dominators tree for we will need it later for the conversion of the IORs
        generateDominatorsTree();
    }

    private void detectLoops(Gateway entry) {
//        we perform a depth-first exploration marking each flow as:
//        forward-flow if it can go forward in the execution of the process
//        loop-flow if it can go backward in the execution of the process
//        finally, we mark as loops all those flows which can go only backward

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

        System.out.println("Gatemap - loops: " + l);
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

//        if we reached the exit gateway, it means we can definitely go forward to the end event,
//        but it is not present in the gateway map
        if( entry == exit ) forwardEdge = true;

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

    private void normalizeLoopJoins() {
//        this method turn all the joins which have incoming
        HashSet<GatewayMapFlow> loops;
        HashSet<GatewayMapFlow> fwds;
        HashSet<BPMNNode> srcs;
        Gateway loopJoin;
        GatewayMapFlow outgoing = null;
        int counter = 0;

        for( Gateway join : new HashSet<>(gateways) )
            if( incomings.get(join).size() > 1 ) {
//                we have to perform an action only for the join gateways
                loops = new HashSet<>();
                fwds = new HashSet<>();
                srcs = new HashSet<>();

                for (GatewayMapFlow f : incomings.get(join)) {
//                    we check all the incoming edges of the join
                    if (f.isLoop()) {
                        loops.add(f);
                        srcs.add(f.last);
                    } else fwds.add(f);
                }

                if( !loops.isEmpty() && (fwds.size() > 1) ) {
//                    if the join has more than 1 forward edge, and at least 1 loop edge
//                    we split it into two new joins
                    counter++;

//                    TODO: not sure about the gateway type to put here, it may be optimized using a DATABASED (maybe)
//                    we create a new gateway both in the BPMN diagram and the gateway map
                    loopJoin = bpmnDiagram.addGateway("loop_join+" + counter, Gateway.GatewayType.INCLUSIVE);
                    this.addGateway(loopJoin);

//                    editing the gateway map
//                    this is a 1-length loop because we removed the join/split gateways
                    for( GatewayMapFlow f : new HashSet<>(outgoings.get(join)) ) outgoing = f;
                    this.changeFlowSRC(outgoing, loopJoin);
//                    this must execute after the loop on the outgoings of the join
                    this.addFlow(join, loopJoin, loopJoin, join);

//                    we redirect all the loops to the new join
                    for( GatewayMapFlow f : loops ) this.changeFlowTGT(f, loopJoin);

//                    editing the BPMN diagram
                    for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> oe : new HashSet<>(bpmnDiagram.getOutEdges(join)) ) {
//                        this is a 1-length loop because we removed the join/split gateways
                        bpmnDiagram.addFlow(loopJoin, oe.getTarget(), "");
                        bpmnDiagram.removeEdge(oe);
                    }
//                    this must execute after the loop on the outgoings of the join (as above)
                    bpmnDiagram.addFlow(join, loopJoin, "");

//                    here we are redirecting the loop edges found before, detected through the source node
                    for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> ie : new HashSet<>(bpmnDiagram.getInEdges(join)) )
                        if( srcs.contains(ie.getSource()) ) {
                            bpmnDiagram.addFlow(ie.getSource(), loopJoin, "");
                            bpmnDiagram.removeEdge(ie);
                        }
                }
            }

        System.out.println("Gatemap - loop joins: " + counter);
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

        System.out.println("Gatemap - IORs: " + iorHierachy.size());
    }

    private void generateDominatorsTree() {
        GID = 0;
        gateIDs = new HashMap<>();
        idToGate = new HashMap<>();
        Map<Integer, List<Integer>> reachableGates = new HashMap<>();

        for( Gateway g : gateways ) {
            GID++;
            reachableGates.put(GID, new ArrayList<Integer>());
            gateIDs.put(g, GID);
            idToGate.put(GID, g);
        }

        for( Gateway g : gateways )
            for( Gateway s : successors.get(g) )
                reachableGates.get(gateIDs.get(g)).add(gateIDs.get(s));

        domTree = new DominatorTree(reachableGates);
        domTree.analyse(gateIDs.get(entry));
    }


//  this methods are about the conversion of the IOR gateways into ANDs or XORs

    public void detectAndReplaceIORs() {
        Gateway.GatewayType gatetype;
        GatewayMapFlow gmFlow;
        Gateway ior;
        Gateway xor;
        BPMNNode last;

        Map<GatewayMapFlow, Gateway> xors;
        Map<GatewayMapFlow, Set<Gateway>> visitedGates;
        Map<GatewayMapFlow, Set<GatewayMapFlow>> visitedFlows;
        Map<GatewayMapFlow, Set<Gateway>> toVisit;

        boolean loop;

//        we check each IOR gateway and its incoming flows
//        then, we follow each incoming flow up to the dominator of the IOR through a backward exploration
//        for each XOR gateway found during this backward exploration
//        for each of its outgoing edges that do not lead to the incoming flow from which we started the backward exploration
//        we place a token generator on that outgoing edge
//        algorithm in: "The Difficulty of Replacing an Inclusive OR-Join" (Favre et Volzer) - p. 12(167)
        int length = iorHierachy.size();
        for( int i = 0; i < length; i++ ) {
            ior = iorHierachy.get(i);

            xors = new HashMap<>();
            visitedGates = new HashMap<>();
            visitedFlows = new HashMap<>();
            toVisit = new HashMap<>();

            loop = false;

            for( GatewayMapFlow iFlow : new HashSet<>(incomings.get(ior)) ) {
                if( iFlow.loop ) {
//                    TODO: special routine for loop joins
//                    Favre et Volzer do not handle loops, therefore we need a special routine for them
                    System.out.println("DEBUG - found a loop join (" + ior.getLabel() + ") skipping it");
//                    ior.setGatewayType(Gateway.GatewayType.DATABASED);
                    loop = true;
                    break;
                }

//                we get the previous node that lead to the IOR on the incoming flow (iFlow)
                last = iFlow.last;
                for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> oe : new HashSet<>(bpmnDiagram.getOutEdges(last)) ) {
                    if( oe.getTarget() == ior ) {
//                        editing the BPMN diagram
//                        we add a XOR gateway on each incoming flow to the IOR
                        bpmnDiagram.removeEdge(oe);
                        xor = bpmnDiagram.addGateway("xor_"+iFlow.id, Gateway.GatewayType.DATABASED);
                        bpmnDiagram.addFlow(last, xor, "");
                        bpmnDiagram.addFlow(xor, ior, "");

//                        editing the gateway map
//                        we add a XOR gateway on each incoming flow to the IOR
                        this.addGateway(xor);
                        this.addFlow(xor, ior, ior, xor);
                        gmFlow = this.changeFlowTGT(iFlow, xor);

//                        this map will keep track of all the gateway we have to visit
//                        from this flow path during the backward exploration
                        toVisit.put(gmFlow, new HashSet<Gateway>());
                        toVisit.get(gmFlow).add(gmFlow.src);

//                        this map will keep track of all the gateway we have visited
//                        from this flow path during the backward exploration
                        visitedGates.put(gmFlow, new HashSet<Gateway>());
                        visitedGates.get(gmFlow).add(gmFlow.src);

//                        this map will keep track of all the flows we have visited
//                        from this flow path during the backward exploration
                        visitedFlows.put(gmFlow, new HashSet<GatewayMapFlow>());
                        visitedFlows.get(gmFlow).add(gmFlow);

//                        this map will keep track of XOR we added for this flow
//                        we have to through a token to its corresponding XOR
                        xors.put(gmFlow, xor);
                        break;
                    }
                }
            }

            if( !loop ) {
                System.out.println("DEBUG - looking for dominator of: " + ior.getLabel());
                System.out.println("DEBUG - xors: " + xors.size());
                gatetype = replaceIOR(getDominator(ior), toVisit, visitedGates, visitedFlows, new HashSet<GatewayMapFlow>(), xors);
                ior.setGatewayType(gatetype);
            }
        }

        helper.removeTrivialGateways(bpmnDiagram);
    }

    private Gateway getDominator(Gateway ior) {
        int domID = domTree.getInfo(gateIDs.get(ior)).getDom().getNode();
        Gateway dominator = idToGate.get(domID);
        System.out.println("DEBUG - DOM: " + dominator.getLabel() + " > " + ior.getLabel());
        return dominator;
    }

    private Gateway.GatewayType replaceIOR(Gateway dominator, Map<GatewayMapFlow, Set<Gateway>> toVisit,
                                           Map<GatewayMapFlow, Set<Gateway>> visitedGates, Map<GatewayMapFlow, Set<GatewayMapFlow>> visitedFlows,
                                           Set<GatewayMapFlow> frontier, Map<GatewayMapFlow, Gateway> xors)
    {
//        this was described in comments above, in previous method, also this
//        algorithm is in: "The Difficulty of Replacing an Inclusive OR-Join" (Favre et Volzer) - p. 12(167)

        HashSet<Gateway> tmp;
        boolean empty;
        boolean onlyXORs;

        empty = true;
        for( GatewayMapFlow f : toVisit.keySet() ) {
            tmp = new HashSet<>();
            for( Gateway g : toVisit.get(f) ) {
                if( g == dominator ) {
                    System.out.println("WARNING - the dominator should not end up in the 'toVisit' set");
                    continue;
                }

                for( GatewayMapFlow igmf : incomings.get(g) )
//                    NOTE: we are skipping the loops
//                          also, if this is a loop, it means that 'g' is an IOR join
                    if( !visitedFlows.get(f).contains(igmf) && !igmf.isLoop() ) {
                        visitedFlows.get(f).add(igmf);
                        if( !visitedGates.get(f).contains(igmf.src) ) {
                            visitedGates.get(f).add(igmf.src);
                            if( igmf.src == dominator ) {
                                frontier.add(igmf);
                            } else {
                                tmp.add(igmf.src);
                                empty = false;
                            }
                        }
                    }
            }
            toVisit.get(f).clear();
            toVisit.get(f).addAll(tmp);
        }

        if( empty ) {
            onlyXORs = true;
            for( GatewayMapFlow f : visitedGates.keySet() )
                for( Gateway g : visitedGates.get(f) )
                    for( GatewayMapFlow ff : new HashSet<>(outgoings.get(g)) ) {
                        if( g.getGatewayType() == Gateway.GatewayType.PARALLEL ) {
                            onlyXORs = false;
                            continue;
                        }

                        if( (g == dominator) && !frontier.contains(ff) ) continue;
                        if( visitedFlows.get(f).contains(ff) ) continue;

                        createTokenGenerator(g, ff, xors.get(f));
                    }
        } else return replaceIOR(dominator, toVisit, visitedGates, visitedFlows, frontier, xors);

//        if during the backward exploration we found only XORs gateways,
//        we are inside a xorHomogeneous RIGID, therefore this inner gate or exit of the RIGID has to be a XOR as well
        if( onlyXORs ) return Gateway.GatewayType.DATABASED;
        else return Gateway.GatewayType.PARALLEL;
    }

    private void createTokenGenerator(Gateway eGate, GatewayMapFlow eFlow, Gateway xor) {
        Gateway and;

        if( (eFlow.first instanceof Gateway) && (((Gateway) eFlow.first).getGatewayType() == Gateway.GatewayType.PARALLEL) && (outgoings.get(eFlow.first).size() > 1) ) and = (Gateway) eFlow.first;
        else {
            and = bpmnDiagram.addGateway("and_"+eFlow.id, Gateway.GatewayType.PARALLEL);
            bpmnDiagram.addFlow(eGate, and, "");
            bpmnDiagram.addFlow(and, eFlow.first, "");

            this.addGateway(and);
            this.addFlow(eGate, and, and, eGate);
            this.changeFlowSRC(eFlow, and);

            for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> oe : new HashSet<>(bpmnDiagram.getOutEdges(eGate)) )
                if( oe.getTarget() == eFlow.first ) bpmnDiagram.removeEdge(oe);
        }

        bpmnDiagram.addFlow(and, xor, "fake-token");
        this.addFlow(and, xor, xor, and);
    }


//  this methods are about the data structures management

    private void addGateway(Gateway gate) {
        gateways.add(gate);
        incomings.put(gate, new HashSet<GatewayMapFlow>());
        outgoings.put(gate, new HashSet<GatewayMapFlow>());
        successors.put(gate, new HashSet<Gateway>());
        predecessors.put(gate, new HashSet<Gateway>());
        map.put(gate, new HashMap<Gateway, Set<GatewayMapFlow>>());
    }

    private void removeGateway(Gateway gate) {
        gateways.remove(gate);
        incomings.remove(gate);
        outgoings.remove(gate);
        successors.remove(gate);
        predecessors.remove(gate);
        map.remove(gate);
    }

    private GatewayMapFlow addFlow(BPMNNode src, BPMNNode tgt, BPMNNode first, BPMNNode last) {
//        NOTE: this method does not work if the SRC or the TGT are not present in the gateway map
//              remember to add first the gateways and then the flows
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

        if( !map.get(entry).containsKey(exit) ) map.get(entry).put(exit, new HashSet<GatewayMapFlow>());
        map.get(entry).get(exit).add(flow);

        return flow;
    }

    private GatewayMapFlow changeFlowSRC(GatewayMapFlow flow, Gateway newSRC) {
        GatewayMapFlow newFlow;

        BPMNNode last = (flow.last == flow.src ? newSRC : flow.last);
        newFlow = this.addFlow(newSRC, flow.tgt, flow.first, last);
        if(flow.isLoop()) newFlow.setLoop();
        this.removeFlow(flow);
        return newFlow;
    }

    private GatewayMapFlow changeFlowTGT(GatewayMapFlow flow, Gateway newTGT) {
        GatewayMapFlow newFlow;

        BPMNNode first = (flow.first == flow.tgt ? newTGT : flow.first);
        newFlow = this.addFlow(flow.src, newTGT, first, flow.last);
        if(flow.isLoop()) newFlow.setLoop();
        this.removeFlow(flow);
        return newFlow;
    }

    private void removeFlow(GatewayMapFlow flow) {
        Gateway entry = flow.getSource();
        Gateway exit = flow.getTarget();

//        System.out.println("DEBUG - removing flow");

        flows.remove(flow);
        outgoings.get(entry).remove(flow);
        incomings.get(exit).remove(flow);
        successors.get(entry).remove(exit);
        predecessors.get(exit).remove(entry);

        map.get(entry).get(exit).remove(flow);
    }


//    supporting private classes

    private class GatewayMapFlow implements Comparable {
        int id;
        Gateway src; //src = the entry gateway of this Flow
        Gateway tgt; //tgt = the exit gateway of this Flow

        BPMNNode first; //this is the successor BPMN node seen from the entry of this Flow (that is src)
        BPMNNode last;  //this is the predecessor BPMN node seen from the exit of this Flow (that is: tgt)
        //NOTE: if this flow is a direct edge from SRC to TGT (i.e. no activities in between)
        //      it holds: (first == tgt) AND (last == src)

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

