/*
 * Copyright © 2009-2018 The Apromore Initiative.
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

import au.edu.qut.processmining.miners.splitminer.dfgp.DFGEdge;
import ee.ut.comptech.*;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagramImpl;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.eclipse.collections.impl.set.mutable.primitive.IntHashSet;

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
    HashMap<Gateway, Integer> gatesDepth;
    private IORsHierarchy iorsHierarchy;
    private HashSet<Gateway> loopJoins;
    private DominatorTree domTree;

    private Set<Gateway> bondsEntries;
    private boolean applyHagen;


    public GatewayMap(boolean applyHagen) {
        this.applyHagen = applyHagen;
        this.bondsEntries = new HashSet<>();
    }

    public GatewayMap(Set<Gateway> bondsEntries, boolean applyHagen) {
        this.applyHagen = applyHagen;
        this.bondsEntries = bondsEntries;
//        debug("Gatemap - bonds entries: " + bondsEntries.size());
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
//        and we throw away all the activities we find
        tmpChild = this.entry;
        while( !gateways.contains(tmpChild) && (children.get(tmpChild).size() == 1) )
            tmpChild = children.get(tmpChild).get(0);

//        we check that we found the first gateway of the map,
//        and we set it as global entry of the gateway map
        if( gateways.contains(tmpChild) ) {
            this.entry = tmpChild;
            entry = tmpChild;
        } else {
            debug("WARNING - first gateway not found");
            return false;
        }

//        now, we create the map starting from the entry gateway
//        exploration is performed depth-first
//        debug("Gatemap - generating paths ...");
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
                    debug("ERROR - found a weird node while generating a paths");
                    return false;
                }

                if( gateways.contains(tmpChild) ) this.addFlow(entry, tmpChild, tasks.get(tasks.size()-2), tasks.get(1));
                if( tmpChild.equals(exit) ) exitGate = entry;
                if( !toVisit.contains(tmpChild) && !visited.contains(tmpChild) ) toVisit.add(0, tmpChild);
            }
        }

        exit = exitGate;

        generateFakeEntryAndExit();

//        debug("DEBUG - exit gate: " + exit.getLabel() );
//        debug("DEBUG - gateways: " + gateways.size() );

//        at this point we need to check that there are NO join/split gateways (just for debug)
        if( !checkGateways() ) return false;

//        debug("DEBUG - flows: " + flows.size() );

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

//        debug("Gatemap - starting initialization");

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
                //debug("DEBUG - entry: " + s);
                //debug("DEBUG - graph.entry: " + graph.getEntry());
                if( !parents.containsKey(s) ) {
                    parents.put(s, new ArrayList<BPMNNode>());
                    //debug("DEBUG - added entry in parents.");
                } else {
                    debug("ERROR - found one single entry but with parent nodes.");
                    return false;
                }
            }

            for( BPMNNode s : ends ) {
                exit = s;
                //debug("DEBUG - exit: " + s);
                //debug("DEBUG - graph.exit: " + graph.getExit());
                if( !children.containsKey(s) ) {
                    children.put(s, new ArrayList<BPMNNode>());
                    //debug("DEBUG - added exit in children.");
                } else {
                    debug("ERROR - found one single exit but with children nodes.");
                    return false;
                }
            }
        } else {
            debug("ERROR - found multiple entry(" + starts.size() + ") or exit(" + ends.size() + ") points.");
            return false;
        }

//        debug("Gatemap - initialization completed");
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

//        debug("Gatemap - splits: " + splits );
//        debug("Gatemap - joins: " + joins );

        if( errors != 0 ) {
            debug("ERROR - found join/split gateways: " + errors );
            return false;
        }

        return true;
    }

    private void generateFakeEntryAndExit(){
        Gateway entry = new Gateway(null, "", Gateway.GatewayType.DATABASED);
        Gateway exit = new Gateway(null, "", Gateway.GatewayType.DATABASED);
        this.addGateway(entry);
        this.addGateway(exit);
        this.addFlow(entry, this.entry, null, null);
        this.addFlow(this.exit, exit, null, null);
        this.entry = entry;
        this.exit = exit;
    }

    private void exploreMap() {
        if( !(entry instanceof Gateway) || !(exit instanceof Gateway)) {
            debug("ERROR - the entry gateway is NOT a gateway");
            return;
        }

//        debug("DEBUG - exploring gatemap ...");

//        this must be the first method to be executed
        detectLoops((Gateway) entry);

//        after we detected the loops, we turn each join gateway that has both:
//        incoming loop edges and incoming forward edges
//        into two different joins: one only for the forward edges, one only for the loops
        normalizeLoopJoins();

//        we explore the map to find the IORs gateway in a hierarchical order
        populatedIORHierarchy((Gateway)entry);

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

        //debug("DEBUG - outgoing size: " + unvisited.size() );

        exploreLoops(entry, unvisited, visiting, visitedGates, visitedFlows, loopEdges, forwardEdges);

        //debug("DEBUG - forwardEdges size: " + forwardEdges.size() );
        //debug("DEBUG - loops size: " + loopEdges.size() );
        for( GatewayMapFlow flow : loopEdges )
            if( !forwardEdges.contains(flow) ) {
                flow.setLoop();
                l++;
            }

//        debug("Gatemap - loops: " + l);
//        debug("Gatemap - unvisited gates: " + unvisited.size());
//        debug("Gatemap - visited gates: " + visitedGates.size());
//        debug("Gatemap - visited flows: " + visitedFlows.size());
    }

    private boolean exploreLoops(Gateway entry, HashSet<Gateway> unvisited, HashSet<Gateway> visiting,
                                 HashMap<Gateway, Boolean> visitedGates, HashSet<GatewayMapFlow> visitedEdges,
                                 HashSet<GatewayMapFlow> loopEdges, HashSet<GatewayMapFlow> forwardEdges)
    {
        Gateway next;
        boolean loopEdge = false;
        boolean forwardEdge = false;
        boolean visited = true;

//        debug("DEBUG - loop visiting: " + entry.getLabel());

        unvisited.remove(entry);
        visiting.add(entry);

//        if we reached the exit gateway, it means we can definitely go forward to the end event,
//        but it is not present in the gateway map
        if( entry == exit ) forwardEdge = true;

        for( GatewayMapFlow oflow : outgoings.get(entry) ) {
            visitedEdges.add(oflow);
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
                        loopJoin = bpmnDiagram.addGateway("lj+" + counter, Gateway.GatewayType.DATABASED);
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
                        if( join.getGatewayType() == Gateway.GatewayType.INCLUSIVE ) join.setGatewayType(Gateway.GatewayType.DATABASED);
                    }
                }
            }

//        debug("Gatemap - loop-joins: " + loopJoins.size());
    }

    private void populatedIORHierarchy(Gateway entry) {
        int depth = 0;
        int sDepth;
        LinkedList<Gateway> toVisit = new LinkedList<>();
        Set<Gateway> visited = new HashSet<>();

        toVisit.add(entry);
        visited.add(entry);

        gatesDepth = new HashMap<>();
        gatesDepth.put(entry, depth);

        while( !toVisit.isEmpty() ) {
            entry = toVisit.remove(0);
            depth = gatesDepth.get(entry);
            sDepth = depth+1;

            for( Gateway next : successors.get(entry) ) {
                if(!gatesDepth.containsKey(next)) gatesDepth.put(next, sDepth);
                else if( (gatesDepth.get(next) < sDepth) && !loopJoins.contains(next) ) gatesDepth.put(next, sDepth);
                if( !visited.contains(next) || !loopJoins.contains(next)) {
                    toVisit.addLast(next);
                    visited.add(next);
                }
            }
        }

        iorsHierarchy = new IORsHierarchy();
        for( Gateway g : gatesDepth.keySet() ) iorsHierarchy.put(g, gatesDepth.get(g));
//        debug("DEBUG - IORs found: " + iorsHierarchy.size());
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
        int counter = 0;
        Gateway.GatewayType iorType;
        Gateway dominator;
        Gateway ior;
        Gateway xor;
        BPMNNode last;

        Map<Gateway, Set<Gateway>> toVisit;
        Map<Gateway, Set<Gateway>> visitedGates;
        Map<Gateway, Set<GatewayMapFlow>> visitedFlows;

        boolean loop;

//        we check each IOR gateway and its incoming flows
//        then, we follow each incoming flow up to the dominator of the IOR through a backward exploration
//        for each XOR gateway found during this backward exploration
//        for each of its outgoing edges that do not lead to the incoming flow from which we started the backward exploration
//        we place a token generator on that outgoing edge
//        algorithm in: "The Difficulty of Replacing an Inclusive OR-Join" (Favre et Volzer) - p. 12(167)
        while( !iorsHierarchy.isEmpty() ) {
            ior = iorsHierarchy.next();
            dominator = getDominator(ior);

            toVisit = new HashMap<>();
            visitedGates = new HashMap<>();
            visitedFlows = new HashMap<>();

            loop = false;
            for( GatewayMapFlow igmf : new HashSet<>(incomings.get(ior)) ) {
                if( igmf.loop ) {
//                    Favre et Volzer do not handle loops, therefore we need a special routine for them:
//                    in presence of IORs loop-joins we do not look for a dominator
//                    we turn them into XORs, and successively if they inject into fragments with an IOR
//                    join as fragment exit we put a token generator which will throw a token to each
//                    other incoming flow of the IOR fragment exit
//                    memo: we should never get inside this IF, because we set all the loop-joins to XORs before
                    debug("WARNING - this should not happen!: found a loop-join: " + ior.getLabel());
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
                        GID++;
                        xor = bpmnDiagram.addGateway("xor_"+GID, Gateway.GatewayType.DATABASED);
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
//                debug("DEBUG - changing IOR: " + ior.getLabel() + "");
//                debug("DEBUG - xors: " + toVisit.size());
                iorType = replaceIOR(dominator, gatesDepth.get(ior), toVisit, visitedGates, visitedFlows, new HashSet<GatewayMapFlow>(), new HashMap<Gateway, Set<GatewayMapFlow>>(), new HashSet<Gateway>());
                ior.setGatewayType(iorType);
                counter++;
            }
        }

//        debug("DEBUG - IORs removed: " + counter);
        helper.removeTrivialGateways(bpmnDiagram);
    }

    private Gateway getDominator(Gateway ior) {
        int domID = domTree.getInfo(gateIDs.get(ior)).getDom().getNode();
        Gateway dominator = idToGate.get(domID);
//        debug("DEBUG - DOM: " + dominator.getLabel() + " > " + ior.getLabel());
        return dominator;
    }

    private Gateway.GatewayType replaceIOR( Gateway dominator, int iorDepth,
                                            Map<Gateway, Set<Gateway>> toVisit,
                                            Map<Gateway, Set<Gateway>> visitedGates,
                                            Map<Gateway, Set<GatewayMapFlow>> visitedFlows,
                                            Set<GatewayMapFlow> domFrontier,
                                            Map<Gateway, Set<GatewayMapFlow>> loopInjections,
                                            Set<Gateway> ANDs)
    {
//        this was described in comments above, in previous method, also this
//        algorithm is in: "The Difficulty of Replacing an Inclusive OR-Join" (Favre et Volzer) - p. 12(167)

        HashSet<Gateway> tmp;
        boolean empty;
        Gateway src;
        Map<GatewayMapFlow, SingleTokenGen> changes;
        ArrayList<MultipleTokenGen> loopChanges;

//        this first part is about the backward exploration
        empty = true;
        for( Gateway xor : toVisit.keySet() ) {
            tmp = new HashSet<>();
            for( Gateway g : toVisit.get(xor) ) for( GatewayMapFlow igmf : incomings.get(g) ) {
                src = igmf.src;
                if( !visitedFlows.get(xor).contains(igmf) ) {
                    visitedFlows.get(xor).add(igmf);
                    if( src == dominator ) domFrontier.add(igmf);
                    if( loopJoins.contains(src) && !loopInjections.containsKey(src) ) {
                        loopInjections.put(src, new HashSet<GatewayMapFlow>());
                        for( GatewayMapFlow injection : incomings.get(src) )
                            if( !gatesDepth.containsKey(injection.src) || (gatesDepth.get(injection.src) > iorDepth) ) loopInjections.get(src).add(injection);
                    }
                    if( !visitedGates.get(xor).contains(src) && (!gatesDepth.containsKey(src) || !(gatesDepth.get(src) > iorDepth)) ) {
//                        NOTE: if we are on a loop edge, and its depth is greater than the IOR to replace
//                              it means this is an injecting back-edge, therefore we do not visit that gateway
                        visitedGates.get(xor).add(src);
                        if( (src.getGatewayType() == Gateway.GatewayType.PARALLEL) && (outgoings.get(src).size() > 1) ) ANDs.add(src);
                        tmp.add(src);
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
//            if this IOR is an exit of a fragment containing only XORs, or
//            if the fragment contains AND-splits but each of them produces tokens only
//            for one incoming edge of the IOR, or for multiple incoming edge of the IOR
//            but mutually exclusive, we set the IOR as XOR.
//            otherwise we apply Haven & Volzer algorithm for IOR replacement
            if( checkXOR(visitedGates, visitedFlows, ANDs) ) {
//                System.out.println("DEBUG - found a XOR");
                return Gateway.GatewayType.DATABASED;
            }

            changes = new HashMap<>();
            loopChanges = new ArrayList<>();
            for( Gateway xor : visitedGates.keySet() ) {
//                debug("DEBUG - visited gates for: " + xor.getLabel());
                for( Gateway g : visitedGates.get(xor) ) {
//                    debug("DEBUG - gate: " + g.getLabel());
                    if( (g.getGatewayType() == Gateway.GatewayType.PARALLEL) || (outgoings.get(g).size() == 1) ) continue;
//                    if we are here, it means g is a decision (XOR split gateway)
                    for( GatewayMapFlow of : outgoings.get(g) ) {
//                        debug("DEBUG - outgoing: " + of.getSource().getLabel() + " -> " + of.getTarget().getLabel());
                        if( visitedFlows.get(xor).contains(of) || ((g == dominator) && !domFrontier.contains(of)) ) continue;
//                        debug("DEBUG - adding a token generator");
//                        here we keep track of where we should put new token generators
                        if( changes.containsKey(of) ) changes.get(of).addXOR2BeFeed(xor);
                        else changes.put(of, new SingleTokenGen(xor, g, of));
                    }
                }
            }

            if( !applyHagen ) return Gateway.GatewayType.INCLUSIVE;

//            at this point, we have to deal with the loop-injections inside this fragment,
//            we have collected these during the backward exploration
            for( Gateway loopInj : loopInjections.keySet() ) {
//                debug("DEBUG - checking loopjoin: " + loopInj.getLabel());
                if( loopInjections.get(loopInj).isEmpty() ) continue;
                tmp = new HashSet<>();
                for( Gateway xor : visitedGates.keySet() ) {
                    if( !visitedGates.get(xor).contains(loopInj) ) tmp.add(xor);
//                    else debug("DEBUG - visited from: " + xor.getLabel());
                }
                if(!tmp.isEmpty()) loopChanges.add(new MultipleTokenGen(tmp, loopInjections.get(loopInj), loopInj));
            }

//            here we are placing the token generators for the escaping edges we found
            for( SingleTokenGen tg : changes.values() ) placeTokenGenerator(tg);
            for( MultipleTokenGen mtg : loopChanges ) placeMultipleTokenGenerator(mtg);

        } else return replaceIOR(dominator, iorDepth, toVisit, visitedGates, visitedFlows, domFrontier, loopInjections, ANDs);

//        debug("DEBUG - turning an IOR into a AND");
        return Gateway.GatewayType.PARALLEL;
    }

    private boolean checkXOR(Map<Gateway, Set<Gateway>> visitedGates, Map<Gateway, Set<GatewayMapFlow>> visitedFlows, Set<Gateway> ANDs) {

        Set<GatewayMapFlow> V;
        IntHashSet V1, V2, U1, U2;
        HashMap<Gateway, HashMap<Gateway, IntHashSet>> visitedEdgesIDs = new HashMap<>();
        HashMap<Gateway, HashMap<Gateway, IntHashSet>> unvisitedEdgesIDs = new HashMap<>();
//        System.out.println("DEBUG - ANDs: " + ANDs.size());
        for( Gateway and : ANDs ) {
//            System.out.println("DEBUG - visiting AND: " + and.getLabel());
            visitedEdgesIDs.put(and, new HashMap<Gateway, IntHashSet>());
            unvisitedEdgesIDs.put(and, new HashMap<Gateway, IntHashSet>());
            for( Gateway xor : visitedGates.keySet() ) {
//                System.out.println("DEBUG - XOR: " + xor.getLabel());
                V1 = new IntHashSet();
                U1 = new IntHashSet();
                V = visitedFlows.get(xor);
                for( GatewayMapFlow oe : outgoings.get(and) ) {
                    if( V.contains(oe) ) V1.add(oe.id);
                    else U1.add(oe.id);
                }
                if( !V1.isEmpty() ) {
                    visitedEdgesIDs.get(and).put(xor, V1);
                    unvisitedEdgesIDs.get(and).put(xor, U1);
                }
//                System.out.println("DEBUG - Visited: " + visitedEdgesIDs.get(and).get(xor));
//                System.out.println("DEBUG - Unvisited: " + unvisitedEdgesIDs.get(and).get(xor));
            }
        }

        for( Gateway and : ANDs ) {
//            System.out.println("DEBUG - checking AND: " + and.getLabel());
            for( Gateway xor1 : visitedEdgesIDs.get(and).keySet() ) {
//                System.out.println("DEBUG - analizing: " + xor1.getLabel());
                V1 = new IntHashSet(visitedEdgesIDs.get(and).get(xor1));
                U1 = new IntHashSet(unvisitedEdgesIDs.get(and).get(xor1));
                for( Gateway xor2 : visitedEdgesIDs.get(and).keySet() ) {
                    V2 = new IntHashSet(visitedEdgesIDs.get(and).get(xor2));
                    U2 = new IntHashSet(unvisitedEdgesIDs.get(and).get(xor2));
//                    System.out.println("DEBUG - comparing with: " + xor2.getLabel());
                    if( U2.retainAll(U1) || (V2.retainAll(V1) && !V2.isEmpty()) ) return false;
                }
            }
        }

        return true;
    }

    private void placeTokenGenerator(SingleTokenGen tg) {
        Gateway and;
        Set<Gateway> xors = tg.xors;
        Gateway eGate = tg.escapingGate;
        GatewayMapFlow eFlow = tg.escapingFlow;

        if( (eFlow.first instanceof Gateway) && (((Gateway) eFlow.first).getGatewayType() == Gateway.GatewayType.PARALLEL) && (outgoings.get(eFlow.first).size() > 1) ) and = (Gateway) eFlow.first;
        else {
//            debug("DEBUG - creating a token generator" + "and_" + eFlow.id);
            GID++;
            and = bpmnDiagram.addGateway("tg_"+GID, Gateway.GatewayType.PARALLEL);
            bpmnDiagram.addFlow(eGate, and, "");
            bpmnDiagram.addFlow(and, eFlow.first, "");

            this.addGateway(and);
            this.addFlow(eGate, and, and, eGate);
            this.changeFlowSRC(eFlow, and);

            for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> oe : new HashSet<>(bpmnDiagram.getOutEdges(eGate)) )
                if( oe.getTarget() == eFlow.first ) bpmnDiagram.removeEdge(oe);
        }

        for( Gateway xor : xors ) {
            bpmnDiagram.addFlow(and, xor, "fake-token");
            this.addFlow(and, xor, xor, and);
        }
    }


    private void placeMultipleTokenGenerator(MultipleTokenGen mtg) {
        Gateway and = null;
        Gateway xor;
        Set<Gateway> xors = mtg.xors;
        Set<GatewayMapFlow> injections = mtg.injections;
        Gateway loopInjection = mtg.loopInjection;
        HashSet<BPMNNode> srcs = new HashSet<>();
        GatewayMapFlow finalInjection;

        if( xors.isEmpty() ) return;
//        debug("DEBUG - placing a multiple token generator: " + xors.size());

        if( injections.size() > 1 ) {
//            if we have a loop-join with more than two injection flows,
//            we need to create a XOR gateway to merge all these flows

//            firstly we create the XOR join
            GID++;
            xor = bpmnDiagram.addGateway("mtg_"+GID, Gateway.GatewayType.DATABASED);
            this.addGateway(xor);

//            then we redirect all the loop incoming flows of the loop-join to the new XOR in this gateway map
            for( GatewayMapFlow injFlow : injections ) {
                srcs.add(injFlow.last);
                this.changeFlowTGT(injFlow, xor);
            }

//            now we can add a new outgoing flow from the XOR to the loop-join
            finalInjection = this.addFlow(xor, loopInjection, loopInjection, xor);
            finalInjection.setLoop();
            injections.clear();
            injections.add(finalInjection);

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
        for( GatewayMapFlow inj : injections ) {
            if( (inj.last instanceof Gateway) && (((Gateway) inj.last).getGatewayType() == Gateway.GatewayType.PARALLEL) && (outgoings.get(inj.last).size() > 1) ) {
                and = (Gateway) inj.last;
            } else {
                and = bpmnDiagram.addGateway("mtg_"+GID, Gateway.GatewayType.PARALLEL);
                this.addGateway(and);
                this.changeFlowTGT(inj, and);

                for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> ie : new HashSet<>(bpmnDiagram.getInEdges(loopInjection)) )
                    if( ie.getSource() == inj.last ) {
                        bpmnDiagram.removeEdge(ie);
                        bpmnDiagram.addFlow(inj.last, and, "");
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
            debug("WARNING - cannot add the flow: not a flow instance");
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

//        debug("DEBUG - removing flow");

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
        Set<Gateway> xors;
        Gateway escapingGate;
        GatewayMapFlow escapingFlow;

        SingleTokenGen(Gateway xor, Gateway escapingGate, GatewayMapFlow escapingFlow) {
            this.xors = new HashSet<>();
            this.xors.add(xor);
            this.escapingGate = escapingGate;
            this.escapingFlow = escapingFlow;
        }

        void addXOR2BeFeed(Gateway xor) {
            this.xors.add(xor);
        }
    }

    private class MultipleTokenGen {
        Set<Gateway> xors;
        Set<GatewayMapFlow> injections;
        Gateway loopInjection;

        MultipleTokenGen(Set<Gateway> xors, Set<GatewayMapFlow> injections, Gateway loopInjection) {
            this.xors = new HashSet<>(xors);
            this.injections = injections;
            this.loopInjection = loopInjection;
        }
    }

    private class IORsHierarchy {
        HashMap<Integer, ArrayList<Gateway>> hierarchy;
        int size;

        IORsHierarchy() { hierarchy = new HashMap<>(); size = 0; }

        void put(Gateway gate, int depth) {
//            debug("DEBUG - adding: " + gate.getLabel() + " - depth: " + depth);
            if( gate.getGatewayType() != Gateway.GatewayType.INCLUSIVE ) return;
            if( !hierarchy.containsKey(depth) ) hierarchy.put(depth, new ArrayList<Gateway>());
            hierarchy.get(depth).add(gate);
            size++;
        }

        Gateway next() {
            int min = Collections.min(hierarchy.keySet());
            Gateway ior = hierarchy.get(min).remove(0);
            if( hierarchy.get(min).isEmpty() ) hierarchy.remove(min);
            size--;
//            debug("DEBUG - extracting: " + ior.getLabel() + " - depth: " + min);
            return ior;
        }

        int size() { return size; }
        boolean isEmpty() { return (size == 0); }
    }


    private void debug(String s) {
        if( s.contains("DEBUG") && false ) System.out.println(s);
        else System.out.println(s);
    }

}

