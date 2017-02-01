/*
 * Copyright © 2009-2017 The Apromore Initiative.
 *
 * This file is part of "Apromore".
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
 */

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
    private HashSet<Gateway> loopJoins;
    private DominatorTree domTree;

    private Set<Gateway> bondsEntries;


    public GatewayMap() {
        this.bondsEntries = new HashSet<>();
    }

    public GatewayMap(Set<Gateway> bondsEntries) {
        this.bondsEntries = bondsEntries;
        System.out.println("Gatemap - bonds entries: " + bondsEntries.size());
    }

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
//        System.out.println("Gatemap - generating paths ...");
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
//        System.out.println("DEBUG - gateways: " + gateways.size() );

//        at this point we need to check that there are NO join/split gateways (just for debug)
        if( !checkGateways() ) return false;

//        System.out.println("DEBUG - flows: " + flows.size() );

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

//        System.out.println("Gatemap - starting initialization");

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

//        System.out.println("Gatemap - initialization completed");
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

//        System.out.println("Gatemap - splits: " + splits );
//        System.out.println("Gatemap - joins: " + joins );

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
        HashMap<Gateway, Boolean> visitedGates = new HashMap<>();
        HashSet<GatewayMapFlow> visitedFlows = new HashSet<>();

        HashSet<GatewayMapFlow> loopEdges = new HashSet<>();
        HashSet<GatewayMapFlow> forwardEdges = new HashSet<>();

        //System.out.println("DEBUG - outgoing size: " + unvisited.size() );

        exploreLoops(entry, unvisited, visiting, visitedGates, visitedFlows, loopEdges, forwardEdges);

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
                                 HashMap<Gateway, Boolean> visitedGates, HashSet<GatewayMapFlow> visitedEdges,
                                 HashSet<GatewayMapFlow> loopEdges, HashSet<GatewayMapFlow> forwardEdges )
    {
        Gateway next;
        boolean loopEdge = false;
        boolean forwardEdge = false;
        boolean visited = true;

        unvisited.remove(entry);
        visiting.add(entry);

//        if we reached the exit gateway, it means we can definitely go forward to the end event,
//        but it is not present in the gateway map
        if( entry == exit ) forwardEdge = true;

        for( GatewayMapFlow oflow : outgoings.get(entry) ) {
            next = oflow.tgt;
            if( unvisited.contains(next) ) {
                if( exploreLoops(next, unvisited, visiting, visitedGates, visitedEdges, loopEdges, forwardEdges) ) {
                    loopEdge = true;
                    loopEdges.add(oflow);
                } else {
                    forwardEdge = true;
                    forwardEdges.add(oflow);
                }
            } else if( visiting.contains(next) ) {
                loopEdge = true;
                loopEdges.add(oflow);
            } else if( visitedGates.containsKey(next) ) {
                if( visitedGates.get(next) ) {
                    loopEdge = true;
                    loopEdges.add(oflow);
                } else {
                    forwardEdge = true;
                    forwardEdges.add(oflow);
                }
            }
            visitedEdges.add(oflow);
        }

        visiting.remove(entry);
        for( GatewayMapFlow iflow : incomings.get(entry) ) if( !visitedEdges.contains(iflow) ) visited = false;
        if( visited ) visitedGates.put(entry, (loopEdge && !forwardEdge));
        else unvisited.add(entry);

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

        loopJoins = new HashSet<>();
        for( Gateway join : new HashSet<>(gateways) )
            if( incomings.get(join).size() > 1 ) {
//                we have to perform an action only for the join gateways
                loops = new HashSet<>();
                fwds = new HashSet<>();
                srcs = new HashSet<>();

                for( GatewayMapFlow f : incomings.get(join) ) {
//                    we check all the incoming edges of the join
                    if (f.isLoop()) {
                        loops.add(f);
                        srcs.add(f.last);
                    } else fwds.add(f);
                }

                if( !loops.isEmpty() ) {
                    if( fwds.size() > 1 ) {
//                        if the join has more than 1 forward edge, and at least 1 loop edge
//                        we split it into two new joins
                        counter++;

//                        we create a new gateway both in the BPMN diagram and the gateway map
                        loopJoin = bpmnDiagram.addGateway("loop_join+" + counter, Gateway.GatewayType.DATABASED);
                        this.addGateway(loopJoin);

//                        editing the gateway map
//                        this is a 1-length loop because we removed the join/split gateways
                        for( GatewayMapFlow f : new HashSet<>(outgoings.get(join)) ) outgoing = f;
                        this.changeFlowSRC(outgoing, loopJoin);
//                        this must execute after the loop on the outgoings of the join
                        this.addFlow(join, loopJoin, loopJoin, join);

//                        we redirect all the loops to the new join
                        for( GatewayMapFlow f : loops ) this.changeFlowTGT(f, loopJoin);

//                        editing the BPMN diagram
                        for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> oe : new HashSet<>(bpmnDiagram.getOutEdges(join)) ) {
//                            this is a 1-length loop because we removed the join/split gateways
                            bpmnDiagram.addFlow(loopJoin, oe.getTarget(), "");
                            bpmnDiagram.removeEdge(oe);
                        }
//                        this must execute after the loop on the outgoings of the join (as above)
                        bpmnDiagram.addFlow(join, loopJoin, "");

//                        here we are redirecting the loop edges found before, detected through the source node
                        for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> ie : new HashSet<>(bpmnDiagram.getInEdges(join)) )
                            if( srcs.contains(ie.getSource()) ) {
                                bpmnDiagram.addFlow(ie.getSource(), loopJoin, "");
                                bpmnDiagram.removeEdge(ie);
                            }

                        loopJoins.add(loopJoin);
                    } else {
//                        in this case the loop-join is already normalized,
//                        we need just to save it and turn it into a databased
                        loopJoins.add(join);
                        join.setGatewayType(Gateway.GatewayType.DATABASED);
                    }
                }
            }

//        System.out.println("Gatemap - loop-joins: " + counter);
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

//        System.out.println("Gatemap - IORs: " + iorHierachy.size());
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
        Gateway.GatewayType iorType;
        Gateway dominator;
        Gateway ior;
        Gateway xor;
        BPMNNode last;

        Map<Gateway, Set<Gateway>> toVisit;
        Map<Gateway, Set<Gateway>> visitedGates;
        Map<Gateway, Set<GatewayMapFlow>> visitedFlows;
        Set<GatewayMapFlow> domFrontier;

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
            dominator = getDominator(ior);

            toVisit = new HashMap<>();
            visitedGates = new HashMap<>();
            visitedFlows = new HashMap<>();
            domFrontier = new HashSet<>();

            loop = false;
            for( GatewayMapFlow igmf : new HashSet<>(incomings.get(ior)) ) {
                if( igmf.loop ) {
//                    Favre et Volzer do not handle loops, therefore we need a special routine for them:
//                    in presence of IORs loop-joins we do not look for a dominator
//                    we turn them into XORs, and successively if they inject into fragments with an IOR
//                    join as fragment exit we put a token generator which will throw a token to each
//                    other incoming flow of the IOR fragment exit
//                    memo: we should never get inside this IF, because we set all the loop-joins to XORs before
                    System.out.println("WARNING - this should not happen!: found a loop-join: " + ior.getLabel());
                    ior.setGatewayType(Gateway.GatewayType.DATABASED);
                    loop = true;
                    break;
                }

//                we get the previous node that lead to the IOR on the incoming flow (iFlow)
                last = igmf.last;
                for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> oe : new HashSet<>(bpmnDiagram.getOutEdges(last)) ) {
                    if( oe.getTarget() == ior ) {
//                        editing the BPMN diagram
//                        we add a XOR gateway on each incoming flow to the IOR
                        bpmnDiagram.removeEdge(oe);
                        xor = bpmnDiagram.addGateway("xor_"+igmf.id, Gateway.GatewayType.DATABASED);
                        bpmnDiagram.addFlow(last, xor, "");
                        bpmnDiagram.addFlow(xor, ior, "");

//                        editing the gateway map
//                        we add a XOR gateway on each incoming flow to the IOR
                        this.addGateway(xor);
                        this.addFlow(xor, ior, ior, xor);
                        this.changeFlowTGT(igmf, xor);

//                        this map will keep track of all the gateways we have to visit
//                        from this xor during the backward exploration
                        toVisit.put(xor, new HashSet<Gateway>());
                        toVisit.get(xor).add(xor);

//                        this map will keep track of all the gateway we have visited
//                        from this xor during the backward exploration
                        visitedGates.put(xor, new HashSet<Gateway>());
                        visitedGates.get(xor).add(dominator);

//                        this map will keep track of all the flows we have visited
//                        from this xor during the backward exploration
                        visitedFlows.put(xor, new HashSet<GatewayMapFlow>());

                        break;
                    }
                }
            }

            if( !loop ) {
                System.out.println("DEBUG - changing IOR: " + ior.getLabel());
                System.out.println("DEBUG - xors: " + toVisit.size());
                iorType = replaceIOR(dominator, toVisit, visitedGates, visitedFlows, domFrontier, new HashSet<Gateway>());
                ior.setGatewayType(iorType);
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

    private Gateway.GatewayType replaceIOR( Gateway dominator,
                                            Map<Gateway, Set<Gateway>> toVisit,
                                            Map<Gateway, Set<Gateway>> visitedGates,
                                            Map<Gateway, Set<GatewayMapFlow>> visitedFlows,
                                            Set<GatewayMapFlow> domFrontier,
                                            Set<Gateway> loopInjections)
    {
//        this was described in comments above, in previous method, also this
//        algorithm is in: "The Difficulty of Replacing an Inclusive OR-Join" (Favre et Volzer) - p. 12(167)

        HashSet<Gateway> tmp;
        boolean empty;
        boolean onlyXORs;
        ArrayList<SingleTokenGen> changes;
        ArrayList<MultipleTokenGen> loopChanges;

//        this first part is about the backward exploration
        empty = true;
        for( Gateway xor : toVisit.keySet() ) {
            tmp = new HashSet<>();
            for( Gateway g : toVisit.get(xor) ) for( GatewayMapFlow igmf : incomings.get(g) ) {
//                NOTE: we are skipping the loops, also, if this is a loop, it means that 'g' is an IOR join
//                      successively, we will place a token generator for each of this gateways found
                if( !visitedFlows.get(xor).contains(igmf) && !igmf.isLoop() ) {
                    visitedFlows.get(xor).add(igmf);
                    if( igmf.src == dominator ) domFrontier.add(igmf);
                    if( loopJoins.contains(igmf.src) ) {
                        System.out.println("DEBUG - " + xor.getLabel() + " found a loop join during b-exp: " + igmf.src.getLabel());
                        loopInjections.add(igmf.src);
                    }
                    if( !visitedGates.get(xor).contains(igmf.src) ) {
                        visitedGates.get(xor).add(igmf.src);
                        tmp.add(igmf.src);
                        empty = false;
                    }
                }
            }
            toVisit.get(xor).clear();
            toVisit.get(xor).addAll(tmp);
        }

//        this second part is about the placing of the token generators
        if( empty ) {
//            firstly we check if we need to set the IOR as AND or as XOR
//            if this IOR is an exit of a fragment containing only XORs, OR
//            if the fragment contains AND but they are entries/exits of BONDs
//            the IOR is turned into a XOR, otherwise it is turned into an AND
//            in this latter case we apply the algorithm of Favre et Volzer
            onlyXORs = true;
            for( Gateway xor : visitedGates.keySet() ) {
                for( Gateway g : visitedGates.get(xor) ) {
                    if( (g.getGatewayType() == Gateway.GatewayType.PARALLEL) && (outgoings.get(g).size() > 1) ) {
//                            in case of an AND gateway we do not need to place token generators
//                            because a token will arrive anyway at the IOR that will be replaced by an AND
//                            however, if there are no AND gateways or they are only entry or exit of BONDs
//                            we can set the IORs as a XOR, for this reason we need to keep track of the
//                            presence of AND gateways that are not entries or exits of BONDs, this is done here
//                        System.out.println("DEBUG - found an AND gateway: " + g.getLabel());

                        if( !bondsEntries.contains(g) ) {
                            onlyXORs = false;
                            break;
                        }

                        for( GatewayMapFlow ogmf : outgoings.get(g) ) {
//                                we need to check only the AND split and not the AND join,
//                                because we went through all the incoming flows of an AND join
//                                during the backward exploration, so what we need to check is that
//                                the outgoing flows of an AND split are all in our backward exploration
//                                as well, otherwise we are in the presence of an 'escaping' flow from an AND split
//                                therefore we do not know where it will go, and we need to turn the IOR into an AND
//                                NOTE: the only AND or XOR joins were placed because they are exits of BONDs!
//                                      only during the generation of the BONDs exit gateways we put joins that are not IORs
                            if( !visitedFlows.get(xor).contains(ogmf) ) onlyXORs = false;
                        }
                    }
                    if( !onlyXORs ) break;
                }
                if( !onlyXORs ) break;
            }

            if( onlyXORs ) {
                System.out.println("DEBUG - turning an IOR into a XOR");
                return Gateway.GatewayType.DATABASED;
            }

            changes = new ArrayList<>();
            loopChanges = new ArrayList<>();
            for( Gateway xor : visitedGates.keySet() ) {
//                System.out.println("DEBUG - visited gates for: " + xor.getLabel());
                for( Gateway g : visitedGates.get(xor) ) {
//                    System.out.println("DEBUG - gate: " + g.getLabel());
                    if( g.getGatewayType() == Gateway.GatewayType.PARALLEL ) continue;
//                    if we are here, it means g is a decision (XOR split gateway)
                    for( GatewayMapFlow of : outgoings.get(g) ) {
//                        System.out.println("DEBUG - outgoing: " + of.getSource().getLabel() + " -> " + of.getTarget().getLabel());
                        if( (g == dominator) && !domFrontier.contains(of) ) {
//                            System.out.println("DEBUG - not frontier");
                            continue;
                        }
                        if( visitedFlows.get(xor).contains(of) ) {
//                            System.out.println("DEBUG - visited");
                            continue;
                        }
//                        System.out.println("DEBUG - adding a token generator");
//                        here we keep track of where we should put new token generators
                        changes.add(new SingleTokenGen(xor, g, of));
                    }
                }
            }

//            at this point, we have to deal with the loop-injections inside this fragment,
//            we have collected these during the backward exploration
            for( Gateway loopInj : loopInjections ) {
                System.out.println("DEBUG - checking loopjoin: " + loopInj.getLabel());
                tmp = new HashSet<>();
                for( Gateway xor : visitedGates.keySet() ) {
                    if( !visitedGates.get(xor).contains(loopInj) ) tmp.add(xor);
                    else System.out.println("DEBUG - visited from: " + xor.getLabel());
                }
                loopChanges.add(new MultipleTokenGen(tmp, loopInj));
            }

//            here we are placing the token generators for the escaping edges we found
            for( SingleTokenGen tg : changes ) createTokenGenerator(tg);
            for( MultipleTokenGen mtg : loopChanges ) createMultipleTokenGenerator(mtg);

        } else return replaceIOR(dominator, toVisit, visitedGates, visitedFlows, domFrontier, loopInjections);

        System.out.println("DEBUG - turning an IOR into a AND");
        return Gateway.GatewayType.PARALLEL;
    }

    private void createTokenGenerator(SingleTokenGen tg) {
        Gateway and;
        Gateway xor = tg.xor;
        Gateway eGate = tg.escapingGate;
        GatewayMapFlow eFlow = tg.escapingFlow;

        if( (eFlow.first instanceof Gateway) && (((Gateway) eFlow.first).getGatewayType() == Gateway.GatewayType.PARALLEL) && (outgoings.get(eFlow.first).size() > 1) ) and = (Gateway) eFlow.first;
        else {
//            System.out.println("DEBUG - creating a token generator" + "and_" + eFlow.id);
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


    private void createMultipleTokenGenerator(MultipleTokenGen mtg) {
        Gateway and = null;
        Gateway xor;
        Set<Gateway> xors = mtg.xors;
        Gateway loopInjection = mtg.loopInjection;
        HashSet<BPMNNode> srcs = new HashSet<>();

        System.out.println("DEBUG - placing a multiple token generator: " + xors.size());
        if( xors.isEmpty() ) return;

        if( incomings.get(loopInjection).size() > 2 ) {
//            if we have a loop-join with more than two incoming flows,
//            it means we have more than one loop flow injecting in this loop-join
//            therefore, we need to create an AND gateway to merge all these flows

//            firstly we create the AND join
            xor = bpmnDiagram.addGateway("xor_"+loopInjection.getLabel(), Gateway.GatewayType.DATABASED);
            this.addGateway(xor);

//            then we redirect all the loop incoming flows of the loop-join to the new AND in this gateway map
            for( GatewayMapFlow igmf : incomings.get(loopInjection) )
                if( igmf.isLoop() ) {
                    srcs.add(igmf.last);
                    this.changeFlowTGT(igmf, xor);
                }

//            now we can add a new outgoing flow from the AND to the loop-join
            this.addFlow(xor, loopInjection, loopInjection, xor).setLoop();

//            then we redirect all the loop incoming flows of the loop-join to the new AND on the BPMN diagram
            for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> ie : new HashSet<>(bpmnDiagram.getInEdges(loopInjection)) )
                if( srcs.contains(ie.getSource()) ) {
                    bpmnDiagram.removeEdge(ie);
                    bpmnDiagram.addFlow(ie.getSource(), xor, "");
                }

//            now we can add a new outgoing flow from the AND to the loop-join
            bpmnDiagram.addFlow(xor, loopInjection, "");
        }

//        at this point there will be only one incoming flow to loopInjection that is a loop
        for( GatewayMapFlow igmf : incomings.get(loopInjection) )
            if( igmf.isLoop() ) {
                if( (igmf.last instanceof Gateway) && (((Gateway) igmf.last).getGatewayType() == Gateway.GatewayType.PARALLEL) && (outgoings.get(igmf.last).size() > 1) ) {
                    and = (Gateway) igmf.last;
                } else {
                    and = bpmnDiagram.addGateway("and_"+loopInjection.getLabel(), Gateway.GatewayType.PARALLEL);
                    this.addGateway(and);
                    this.changeFlowTGT(igmf, and);

                    for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> ie : new HashSet<>(bpmnDiagram.getInEdges(loopInjection)) )
                        if( ie.getSource() == igmf.last ) {
                            bpmnDiagram.removeEdge(ie);
                            bpmnDiagram.addFlow(igmf.last, and, "");
                        }

                    this.addFlow(and, loopInjection, loopInjection, and).setLoop();
                    bpmnDiagram.addFlow(and, loopInjection, "");
                }
            }

        for( Gateway x : xors ) {
            bpmnDiagram.addFlow(and, x, "fake-token");
            this.addFlow(and, x, x, and);
        }
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

        @Override
        public int hashCode() { return this.id; }

    }

    private class SingleTokenGen {
        Gateway xor;
        Gateway escapingGate;
        GatewayMapFlow escapingFlow;

        SingleTokenGen(Gateway xor, Gateway escapingGate, GatewayMapFlow escapingFlow) {
            this.xor = xor;
            this.escapingGate = escapingGate;
            this.escapingFlow = escapingFlow;
        }
    }

    private class MultipleTokenGen {
        Set<Gateway> xors;
        Gateway loopInjection;

        MultipleTokenGen(Set<Gateway> xors, Gateway loopInjection) {
            this.xors = new HashSet<>(xors);
            this.loopInjection = loopInjection;
        }
    }

}

