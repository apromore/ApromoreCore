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

package au.edu.qut.processmining.miners.splitminer;

import au.edu.qut.bpmn.helper.DiagramHandler;
import au.edu.qut.bpmn.helper.GatewayMap;
import au.edu.qut.bpmn.structuring.StructuringService;
import au.edu.qut.processmining.log.LogParser;
import au.edu.qut.processmining.log.SimpleLog;
import au.edu.qut.processmining.miners.splitminer.dfgp.DirectlyFollowGraphPlus;
import au.edu.qut.processmining.miners.splitminer.oracle.Oracle;
import au.edu.qut.processmining.miners.splitminer.oracle.OracleItem;
import au.edu.qut.processmining.miners.splitminer.ui.dfgp.DFGPUIResult;
import au.edu.qut.processmining.miners.splitminer.ui.miner.SplitMinerUIResult;

import au.edu.unimelb.processmining.optimization.SimpleDirectlyFollowGraph;
import de.hpi.bpt.graph.DirectedEdge;
import de.hpi.bpt.graph.DirectedGraph;
import de.hpi.bpt.graph.abs.IDirectedGraph;
import de.hpi.bpt.graph.algo.rpst.RPST;
import de.hpi.bpt.graph.algo.rpst.RPSTNode;
import de.hpi.bpt.graph.algo.tctree.TCType;
import de.hpi.bpt.hypergraph.abs.Vertex;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIContext;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramImpl;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.*;
import org.processmining.plugins.bpmn.plugins.BpmnExportPlugin;
import javax.swing.UIManager;

import java.io.File;
import java.util.*;

import javax.swing.UIManager;

/**
 * Created by Adriano on 24/10/2016.
 * Modified by Bruce Nguyen
 * 		- Add constructor
 */
public class SplitMiner {

    private SimpleLog log;
    private DirectlyFollowGraphPlus dfgp;
    private BPMNDiagram bpmnDiagram;

    private boolean replaceIORs;
    private boolean removeLoopActivities;
    private SplitMinerUIResult.StructuringTime structuringTime;

    private int gateCounter;
    private HashMap<String, Gateway> candidateJoins;

    private Set<Gateway> bondsEntries;
    private Set<Gateway> rigidsEntries;

    public static void main(String[] args) {
    	SplitMiner miner = new SplitMiner();
    	
    }
    
    //Bruce added
    public SplitMiner(boolean replaceIORs, boolean removeLoopActivities, SplitMinerUIResult.StructuringTime structuringTime) {
        this.replaceIORs = replaceIORs;
        this.removeLoopActivities = removeLoopActivities;
        this.structuringTime = structuringTime;
    }

    public SplitMiner() {
        this.replaceIORs = true;
        this.removeLoopActivities = true;
        this.structuringTime = SplitMinerUIResult.StructuringTime.NONE;
    }

    public DirectlyFollowGraphPlus getDFGP() { return dfgp; }

    public BPMNDiagram getBPMNDiagram() { return bpmnDiagram; }

    public BPMNDiagram mineBPMNModel(XLog log, XEventClassifier xEventClassifier, double percentileFrequencyThreshold, double parallelismsThreshold,
                                     DFGPUIResult.FilterType filterType, boolean parallelismsFirst,
                                     boolean replaceIORs, boolean removeLoopActivities, SplitMinerUIResult.StructuringTime structuringTime)
    {
        this.replaceIORs = replaceIORs;
        this.removeLoopActivities = removeLoopActivities;
        this.structuringTime = structuringTime;

//        this.log = (new LogParser()).getSimpleLog(log, xEventClassifier, 1.00);
        this.log = LogParser.getSimpleLog(log, xEventClassifier);

        generateDFGP(percentileFrequencyThreshold, parallelismsThreshold, filterType, parallelismsFirst);
        try {
            transformDFGPintoBPMN();
            if (structuringTime == SplitMinerUIResult.StructuringTime.POST) structure();
        } catch(Exception e) {
            System.out.println("ERROR - something went wrong translating DFG to BPMN, trying a second time");
            e.printStackTrace();
            try{
                dfgp = new DirectlyFollowGraphPlus(this.log, percentileFrequencyThreshold, parallelismsThreshold, filterType, parallelismsFirst);
                dfgp.buildSafeDFGP();
                transformDFGPintoBPMN();
                if (structuringTime == SplitMinerUIResult.StructuringTime.POST) structure();
            } catch ( Exception ee ) {
                System.out.println("ERROR - nothing to do, returning the bare DFGP");
                return dfgp.convertIntoBPMNDiagram();
            }
        }

        return bpmnDiagram;
    }

    public BPMNDiagram mineBPMNModel(SimpleLog log, XEventClassifier xEventClassifier, double percentileFrequencyThreshold, double parallelismsThreshold,
                                     DFGPUIResult.FilterType filterType, boolean parallelismsFirst,
                                     boolean replaceIORs, boolean removeLoopActivities, SplitMinerUIResult.StructuringTime structuringTime)
    {
        this.replaceIORs = replaceIORs;
        this.removeLoopActivities = removeLoopActivities;
        this.structuringTime = structuringTime;

//        this.log = (new LogParser()).getSimpleLog(log, xEventClassifier, 1.00);
        this.log = log;

        generateDFGP(percentileFrequencyThreshold, parallelismsThreshold, filterType, parallelismsFirst);
        try {
            transformDFGPintoBPMN();
            if (structuringTime == SplitMinerUIResult.StructuringTime.POST) structure();
        } catch(Exception e) {
            System.out.println("ERROR - something went wrong translating DFG to BPMN, trying a second time");
            e.printStackTrace();
            try{
                dfgp = new DirectlyFollowGraphPlus(log, percentileFrequencyThreshold, parallelismsThreshold, filterType, parallelismsFirst);
                dfgp.buildSafeDFGP();
                transformDFGPintoBPMN();
                if (structuringTime == SplitMinerUIResult.StructuringTime.POST) structure();
            } catch ( Exception ee ) {
                System.out.println("ERROR - nothing to do, returning the bare DFGP");
                return dfgp.convertIntoBPMNDiagram();
            }
        }

        return bpmnDiagram;
    }

    private void generateDFGP(double percentileFrequencyThreshold, double parallelismsThreshold, DFGPUIResult.FilterType filterType, boolean parallelismsFirst) {
        dfgp = new DirectlyFollowGraphPlus(log, percentileFrequencyThreshold, parallelismsThreshold, filterType, parallelismsFirst);
        dfgp.buildDFGP();
    }

    public BPMNDiagram discoverFromDFGP(DirectlyFollowGraphPlus idfgp) throws Exception {
        this.log = idfgp.getSimpleLog();
        dfgp = idfgp;
        transformDFGPintoBPMN();
        if (structuringTime == SplitMinerUIResult.StructuringTime.POST) structure();
        try {
            transformDFGPintoBPMN();
            if (structuringTime == SplitMinerUIResult.StructuringTime.POST) structure();
        } catch(Exception e) {
//        	System.out.println("ERROR - something went wrong translating DFG to BPMN, trying a second time");
//            e.printStackTrace();
//            try{
//            	dfgp.resetDFGPStructures();
//                dfgp.buildSafeDFGP(); //recreate a safe data structure for the graph in case errors happen (the graph could be disconnected)  
//                transformDFGPintoBPMN();
//                if (structuringTime == SplitMinerUIResult.StructuringTime.POST) structure();
//            } catch ( Exception ee ) {
//                System.out.println("ERROR - nothing to do, returning the bare DFGP");
//                return dfgp.convertIntoBPMNDiagram();
//            }
        	System.out.println("ERROR - something went wrong in translating DFG to BPMN");
        	e.printStackTrace();        	
            throw e;
        	
        }
        return bpmnDiagram;
    }

    public BPMNDiagram discoverFromSDFG(SimpleDirectlyFollowGraph sdfg) throws Exception {
        this.log = sdfg.getSimpleLog();
        dfgp = sdfg;
        transformDFGPintoBPMN();
        if (structuringTime == SplitMinerUIResult.StructuringTime.POST) structure();
        return bpmnDiagram;
    }

    private void transformDFGPintoBPMN() {
        DiagramHandler helper = new DiagramHandler();
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
        
//        we retrieve the starting BPMN diagram from the DFGP,
//        it is a DFGP with start and end events, but no gateways
        bpmnDiagram = dfgp.convertIntoBPMNDiagram();
        
        candidateJoins = new HashMap<>();

//        firstly we generate the split gateways

//        there are only two events in the initial BPMN diagram,
//        one is the START and for exclusion the second is the END
        for( Event e : bpmnDiagram.getEvents() )
            if( e.getEventType() == Event.EventType.START ) entry = e;
            else exit = e;

        if( entry == null || exit == null ) {
//            this should never happen
            System.out.println("ERROR - entry(" + entry + ") OR exit(" + exit + ") not found in the DFGP-diagram");
            return;
        }

//        System.out.println("SplitMiner - generating bpmn diagram");

//        we perform a breadth-first exploration of the DFGP-diagram
//        every time we find a node with multiple outgoing edges we stop
//        and we generate the corresponding hierarchy of gateways

        toVisit.add(0, entry);
        while( toVisit.size() != 0 ) {
            entry = toVisit.remove(0);
            visited.add(entry);
//            System.out.println("DEBUG - visiting: " + entry.getLabel());

            if( entry == exit ) continue;

            if( bpmnDiagram.getOutEdges(entry).size() > 1 ) {
//                entry is a node with multiple outgoing edges

                successors = new HashSet<>();
                removableEdges = new HashSet<>();
                for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> oe : bpmnDiagram.getOutEdges(entry) ) {
                    tgt = oe.getTarget();
//                    we remove all the outgoing edges, because we will restore them with the split gateways
                    removableEdges.add(oe);
                    successors.add(Integer.valueOf(tgt.getLabel()));
                    mapping.put(Integer.valueOf(tgt.getLabel()), tgt);
                    if( !toVisit.contains(tgt) && !visited.contains(tgt) ) toVisit.add(tgt);
                }

                for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : removableEdges ) bpmnDiagram.removeEdge(e);

//                to decide the hierarchy of the gateways we use an Oracle item
//                an Oracle item is a string of the type past|future
//                more info about this object in its own class
                oracleItems = new HashSet<>();
                for( int a : successors ) {
//                    we generate one Oracle item for each successor of the entry
//                    the successor will be the past
                    oracleItem = new OracleItem();
                    oracleItem.fillPast(a);

//                    then we fill its future with all the successors which are in a concurrency relationship with it
//                    if a successor is not concurrent, it means it will we exclusive or directly follow
//                    if exclusive we do not have to care about it
//                    if directly follow, it will be processed later
                    for( int b : successors )
                        if( (a !=  b) && (dfgp.areConcurrent(a, b)) ) oracleItem.fillFuture(b);

                    oracleItem.engrave();
                    oracleItems.add(oracleItem);
                }

                finalOracleItem = oracle.getFinalOracleItem(oracleItems);

//                the finalOracleItem is a matryoshka containing the info about the gateway hierarchy
//                the following method will explore inside-out this matryoshka and will place the gateways accordingly
//                the entry will be the last node to be linked to the outer gateway of the hierarchy
                generateSplitsHierarchy(entry, finalOracleItem, mapping);
            } else {
//                we save the only successor of the src
                tgt = ((new ArrayList<>(bpmnDiagram.getOutEdges(entry))).get(0)).getTarget();
                if( !toVisit.contains(tgt) && !visited.contains(tgt) ) toVisit.add(tgt);
            }
        }

//        after generating the split hierarchy we should have only SPLITs,
//        however, it may happen that some JOINs are generated as well (due to shared future)
//        it is important that we do not leave any gateway that is both a SPLIT and a JOIN
        helper.removeJoinSplit(bpmnDiagram);

//        at this point, all the splits were generated, along with just a few joins
//        now we focus only on the joins. we use the RPST in order to place INCLUSIVE joins
//        which will be turned into AND or XOR joins later
        bondsEntries = new HashSet<>();
        rigidsEntries = new HashSet<>();
//        System.out.println("SplitMiner - generating SESE joins ...");
        
        while( generateSESEjoins() );

//        this second method adds the remaining joins, which were no entry neither exits of any RPST node
//        System.out.println("SplitMiner - generating inner joins ...");
        generateInnerJoins();

        if( structuringTime == SplitMinerUIResult.StructuringTime.PRE ) structure();
        helper.fixSoundness(bpmnDiagram);

//        finally, we turn all the inclusive joins placed, into proper joins: ANDs or XORs
//        System.out.println("SplitMiner - turning inclusive joins ...");
        replaceIORs();

        updateLabels(this.log.getEvents());

        if(removeLoopActivities) helper.removeLoopActivityMarkers(bpmnDiagram);

        if( replaceIORs ) {
            helper.collapseSplitGateways(bpmnDiagram);
            helper.collapseJoinGateways(bpmnDiagram);
        } else {
            helper.collapseSplitGateways(bpmnDiagram);
            helper.collapseJoinGateways(bpmnDiagram);
        }

//        System.out.println("SplitMiner - bpmn diagram generated successfully");
    }

    private void generateSplitsHierarchy(BPMNNode entry, OracleItem nextOracleItem, Map<Integer, BPMNNode> mapping) {
//        first of all we retrieve the type of the gateway we should place
        Gateway.GatewayType type = nextOracleItem.getGateType();
        BPMNNode node;
        Integer nodeCode;
        Gateway gate;
        Gateway candidateJoin;

//        System.out.println("DEBUG - generating split from Oracle ~ [xor|and]: " + nextOracleItem + " ~ [" + nextOracleItem.getXorBrothers().size() + "|" + nextOracleItem.getAndBrothers().size() + "]");

        if( candidateJoins.containsKey(nextOracleItem.toString()) ) {
//            these are joins, they are created considering the fact they share the same future (finalOracleItem)
            candidateJoin = candidateJoins.get(nextOracleItem.toString());
//            System.out.println("DEBUG - found " + candidateJoin.getGatewayType() + " join for the Oracle item: " + nextOracleItem.toString());
            bpmnDiagram.addFlow(entry, candidateJoin, "");
            return;
        }

        if( type == null ) {
//            if the type was null, it means we reached a simple activity, so we can link the entry with the activity
            nodeCode = nextOracleItem.getNodeCode();
            if( nodeCode != null ) {
                node = mapping.get(nodeCode);
                bpmnDiagram.addFlow(entry, node, "");
            } else System.out.println("ERROR - found an oracle item without brother and more than one element in its past");
            return;
        }

        gate = bpmnDiagram.addGateway(Integer.toString(gateCounter++), type);
        bpmnDiagram.addFlow(entry, gate, "");
        for( OracleItem next : nextOracleItem.getXorBrothers() ) generateSplitsHierarchy(gate, next, mapping);
        for( OracleItem next : nextOracleItem.getAndBrothers() ) generateSplitsHierarchy(gate, next, mapping);

        candidateJoins.put(nextOracleItem.toString(), gate);
    }

    private boolean generateSESEjoins() {
        int counter = 0;
        HashSet<String> changed = new HashSet<>();

        try {
            HashMap<String, BPMNNode> nodes = new HashMap<>();
            HashMap<BPMNNode, Vertex> vertexes = new HashMap<BPMNNode, Vertex>();

            HashMap<String, Gateway.GatewayType> gates = new HashMap<String, Gateway.GatewayType>();
            ArrayList<RPSTNode> rpstBottomUpHierarchy = new ArrayList<RPSTNode>();
            HashSet<RPSTNode> loops = new HashSet<>();
            HashSet<RPSTNode> rigids = new HashSet<>();

            IDirectedGraph<DirectedEdge, Vertex> graph = new DirectedGraph();
            Vertex src;
            Vertex tgt;

            BPMNNode bpmnSRC;
            BPMNNode bpmnTGT;
            Gateway gate;

            String entry, exit, gatify, matchingGate, srcVertex;


//            we build the graph from the BPMN Diagram, the graph is necessary to generate the RPST
//            we build the graph from the BPMN Diagram, the graph is necessary to generate the RPST

            for( Flow f : bpmnDiagram.getFlows((Swimlane) null) ) {
                bpmnSRC = f.getSource();
                bpmnTGT = f.getTarget();
                if( !vertexes.containsKey(bpmnSRC) ) {
                    src = new Vertex(bpmnSRC.getLabel());  //this may not be anymore a unique number, but still a unique label
                    if( bpmnSRC instanceof Gateway ) gates.put(bpmnSRC.getLabel(), ((Gateway) bpmnSRC).getGatewayType());
                    vertexes.put(bpmnSRC, src);
                    nodes.put(bpmnSRC.getLabel(), bpmnSRC);
                } else src = vertexes.get(bpmnSRC);

                if( !vertexes.containsKey(bpmnTGT) ) {
                    tgt = new Vertex(bpmnTGT.getLabel());  //this may not be anymore a unique number, but still a unique label
                    if( bpmnTGT instanceof Gateway ) gates.put(bpmnTGT.getLabel(), ((Gateway) bpmnTGT).getGatewayType());
                    vertexes.put(bpmnTGT, tgt);
                    nodes.put(bpmnTGT.getLabel(), bpmnTGT);
                } else tgt = vertexes.get(bpmnTGT);

                graph.addEdge(src, tgt);
            }


//            we use the graph to get the RPST of it
            RPST rpst = new RPST(graph);

//            then, we explore the RPST top-down, but just to save its bottom-up structure
//            in particular, we will focus on rigids and bonds
            RPSTNode root = rpst.getRoot();
            LinkedList<RPSTNode> toAnalize = new LinkedList<RPSTNode>();
            toAnalize.addLast(root);
            while( toAnalize.size() != 0 ) {
                root = toAnalize.removeFirst();

                for( RPSTNode n : new HashSet<RPSTNode>(rpst.getChildren(root)) ) {
                    switch( n.getType() ) {
                        case T:
                            break;
                        case P:
                            toAnalize.addLast(n);
                            break;
                        case R:
                            rigids.add(n);
                        case B:
                            exit = n.getExit().getName();
                            if( !gates.containsKey(exit) ) {
//                                System.out.println("DEBUG - found a bond exit (" + exit + ") that is not a gateway");
                                rpstBottomUpHierarchy.add(0, n);
                            } else {
                                entry = n.getEntry().getName();
                                if( !gates.containsKey(entry) ) {
//                                    this is the case when an RPSTNode is a LOOP
//                                    System.out.println("DEBUG - found a bond entry (" + entry + ") that is not a gateway");
                                    rpstBottomUpHierarchy.add(0, n);
                                    loops.add(n);
                                } else {
                                    if( n.getType() == TCType.R ) rigidsEntries.add((Gateway)nodes.get(entry));
                                    else bondsEntries.add((Gateway)nodes.get(entry));
                                }
                            }
                            toAnalize.addLast(n);
                            break;
                        default:
                    }
                }
            }

//            System.out.println("DEBUG - starting analysing RPST node: " + rpstBottomUpHierarchy.size() );
            HashMap<String, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> removableEdges;
            HashSet<String> toRemove;
            RPSTNode rpstNode;
            boolean isLoop;
            boolean isRigid;
            Gateway.GatewayType gType;

            int d = 1;
            while( !rpstBottomUpHierarchy.isEmpty() ) {
                rpstNode = rpstBottomUpHierarchy.remove(0);
                entry = rpstNode.getEntry().getName();
                exit = rpstNode.getExit().getName();
                isLoop = loops.contains(rpstNode);
                isRigid = rigids.contains(rpstNode);

//                we have to transform an activity-join into a gateway-join
//                but: if the RPST node is a loop, its entry is the join and its exit is the split,
//                     if not, it would be vice-versa.
                gatify = isLoop ? entry : exit;
                matchingGate = isLoop ? exit : entry;

//                if we are analysing a RIGID, we cannot match the join with the split
//                otherwise, if it is the case of a BOND, we can
                gType = isRigid ? Gateway.GatewayType.INCLUSIVE : gates.get(matchingGate);

//                if we already turned this activity into a gateway, we cannot edit it anymore
//                we will go through it again (if needed) in the next call of this method
//                this explain the outer while loop, and the boolean value returned by this method
                if( changed.contains(gatify) ) continue;
                changed.add(gatify);

                removableEdges = new HashMap<>();
                toRemove = new HashSet<>();
                bpmnTGT = nodes.get(gatify);

//                we save all the incoming edges to the activity to be turned into gateway,
//                because they must be removed and substituted by edges to a split gateway
//                whether they are inside the RPST node graph
                for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> ie : bpmnDiagram.getInEdges(bpmnTGT) )
                    removableEdges.put(ie.getSource().getLabel(), ie);

                IDirectedGraph<DirectedEdge, Vertex> rpstNodeGraph = rpstNode.getFragment();
                for( Vertex v : rpstNodeGraph.getVertices() )
                    if( v.getName().equals(gatify) ) {
//                        at this point we have everything we need to update the BPMN diagram and place the join
                        gate = bpmnDiagram.addGateway(Integer.toString(gateCounter++), gType);
                        counter++;
                        bpmnDiagram.addFlow(gate, bpmnTGT, "");

                        for( de.hpi.bpt.graph.abs.AbstractDirectedEdge e : rpstNodeGraph.getEdgesWithTarget(v) ) {
                            srcVertex = e.getSource().getName();
                            toRemove.add(srcVertex);
                            bpmnSRC = nodes.get(srcVertex);
                            bpmnDiagram.addFlow(bpmnSRC, gate, "");
                        }

                        for( String label : removableEdges.keySet() ) {
                            if( toRemove.contains(label) ) bpmnDiagram.removeEdge(removableEdges.get(label));
                            else if(isLoop) {
//                                loops require this special treatment
//                                we must remove ALL the incoming edges
//                                also those which are outside the RPST node graph,
//                                this is due to the fact the join in a loop is the entry of the RPST node,
//                                so that its incoming edges will be both inside the RPST node graph and outside
//                                and consequentially we couldn't catch those which were outside
                                bpmnSRC = nodes.get(label);
                                bpmnDiagram.addFlow(bpmnSRC, gate, "");
                                bpmnDiagram.removeEdge(removableEdges.get(label));
                            }
                        }
                    }
            }
        } catch( Error e ) {
            e.printStackTrace(System.out);
            System.out.println("ERROR - impossible to generate split gateways");
            return false;
        }
        
//        System.out.println("DEBUG - SESE joins placed: " + counter );
        return !changed.isEmpty();
    }

    private void generateInnerJoins() {
        HashSet<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> removableEdges;
        Set<BPMNNode> nodes = new HashSet<>(bpmnDiagram.getNodes());
        Gateway gate;
        int counter = 0;

//        all the activities found with multiple incoming edges are turned into inclusive joins
//        these activities are inside RIGID fragments of the BPMN model
        for( BPMNNode n : nodes ) {
            removableEdges = new HashSet<>(bpmnDiagram.getInEdges(n));
            if( (removableEdges.size() <= 1) || (n instanceof Gateway) ) continue;
            gate = bpmnDiagram.addGateway(Integer.toString(gateCounter++), Gateway.GatewayType.INCLUSIVE);
            counter++;
            bpmnDiagram.addFlow(gate, n, "");
            for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : removableEdges ) {
                bpmnDiagram.removeEdge(e);
                bpmnDiagram.addFlow(e.getSource(), gate, "");
            }
        }
//        System.out.println("DEBUG - inner joins placed: " + counter );
    }

    private void replaceIORs() {
        bondsEntries.removeAll(rigidsEntries);
        GatewayMap gatemap = new GatewayMap(bondsEntries, replaceIORs);
//        System.out.println("DEBUG - doing the magic ...");
        if( gatemap.generateMap(bpmnDiagram) ) gatemap.detectAndReplaceIORs();
        else System.out.println("ERROR - something went wrong initializing the gateway map");
    }

    private void structure() {
        StructuringService ss = new StructuringService();
        BPMNDiagram structureDiagram = ss.structureDiagram(bpmnDiagram);
        bpmnDiagram = structureDiagram;
    }

    private void updateLabels(Map<Integer, String> events) {
//        this method just replace the labels of the activities in the BPMN diagram,
//        that so far have been numbers (in order to speed up the computation complexity)
        DiagramHandler helper = new DiagramHandler();
        BPMNDiagram duplicateDiagram = new BPMNDiagramImpl(bpmnDiagram.getLabel());
        HashMap<BPMNNode, BPMNNode> originalToCopy = new HashMap<>();
        BPMNNode src, tgt;
        BPMNNode copy;
        String label;

        for( BPMNNode n : bpmnDiagram.getNodes() ) {
            if( n instanceof Activity ) label = events.get(Integer.valueOf(n.getLabel()));
            else label = "";
            copy = helper.copyNode(duplicateDiagram, n, label);
            if( copy != null ) originalToCopy.put(n, copy);
            else System.out.println("ERROR - diagram labels updating failed [1].");
        }

        for( Flow f : bpmnDiagram.getFlows() ) {
            src = originalToCopy.get(f.getSource());
            tgt = originalToCopy.get(f.getTarget());

            if( src != null && tgt != null ) duplicateDiagram.addFlow(src, tgt, "");
            else System.out.println("ERROR - diagram labels updating failed [2].");
        }
        bpmnDiagram = duplicateDiagram;
    }
    
    // Bruce: for debug only
    private void writeDiagram(BPMNDiagram d, String filename) {
	    try {
	        UIContext context = new UIContext();
	        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
	        UIPluginContext uiPluginContext = context.getMainPluginContext();
	        BpmnExportPlugin exportPlugin = new BpmnExportPlugin();
	        exportPlugin.export(uiPluginContext, d, new File(filename));
	    }
	    catch (Exception ex) {
	    	ex.printStackTrace();
	    }
    }

}
