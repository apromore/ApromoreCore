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

package au.edu.qut.processmining.repairing;

import au.edu.qut.bpmn.helper.DiagramHandler;
import au.edu.qut.processmining.log.LogAnalizer;
import au.edu.qut.processmining.log.LogParser;
import au.edu.qut.processmining.log.graph.fuzzy.FuzzyNet;
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
import org.processmining.models.graphbased.directed.bpmn.elements.*;

import java.util.*;

/**
 * Created by Adriano on 14/06/2016.
 */

public class Optimizer {

    private boolean unbalancedPaths;
    private boolean optionalActivities;
    private boolean recurrentActivities;
    private boolean inclusiveChoice;
    private boolean applyCleaning;

    private LogAnalizer analizer;
    private DiagramHandler diagramHandler;
    private FuzzyNet fuzzyNet;
    private BPMNDiagram diagram;

    private RPST rpst;
    private HashSet<RPSTNode> cycles;
    private HashSet<RPSTNode> parallel;
    private HashSet<RPSTNode> exclusive;
    private HashSet<RPSTNode> skippable;
    private HashSet<RPSTNode> inclusive;
    private HashMap<BPMNNode, RPSTNode> parentSESE;

    private HashMap<String, BPMNNode> idToNode;
    private HashMap<String, Gateway> idToGate;
    private HashMap<RPSTNode, Gateway> bondEntry;
    private HashMap<RPSTNode, Gateway> bondExit;

    private ArrayList<RPSTNode> bottomUpRPST;
    private HashMap<RPSTNode, HashSet<BPMNNode>> RPSTNodeToBPMNChildren;

    private HashMap<RPSTNode, HashSet<ArrayList<Activity>>> entryToExitPaths;

    private HashMap<Activity, Integer> minTime;
    private HashMap<Activity, Integer> maxTime;

    private HashSet<Activity> removedSkippingActivity;

    public Optimizer() {
        diagramHandler = new DiagramHandler();
    }


    public BPMNDiagram optimize(BPMNDiagram inputDiagram, XLog log, boolean unbalancedPaths, boolean optionalActivities, boolean recurrentActivities, boolean inclusiveChoice, boolean applyCleaning) {
        diagram = diagramHandler.copyDiagram(inputDiagram);
        diagramHandler.setDiagram(diagram);

        this.inclusiveChoice = inclusiveChoice;
        this.unbalancedPaths = unbalancedPaths;
        this.optionalActivities = optionalActivities;
        this.recurrentActivities = recurrentActivities;
        this.applyCleaning = applyCleaning;

        fuzzyNet = LogParser.initFuzzyNet(log);
        analizer = new LogAnalizer(log);
        analizer.runAnalysis();


        if( !updateDataInfo() ) return inputDiagram;
        checkFollowers();
        if( inclusiveChoice ) {
            removeSkippingActivities();
            if( !updateDataInfo() ) return inputDiagram;
            setOrGateways();
        }

        if( !updateDataInfo() ) return inputDiagram;
        if( optionalActivities ) setSkippingActivities();

        return diagram;
    }

    private boolean updateDataInfo() {
        if( !generateRPST() ) return false;
        generatePaths();
        diagramHandler.setDiagram(diagram);
//        computeTimes();

        return true;
    }

    private boolean generateRPST() {
        System.out.println("DEBUG - starting RPST generation.");
        try {
            HashMap<BPMNNode, Vertex> mapping = new HashMap<>();
            idToNode = new HashMap<>();
            idToGate = new HashMap<>();
            bondEntry = new HashMap<>();
            bondExit = new HashMap<>();

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
            parentSESE = new HashMap<>();
            RPSTNodeToBPMNChildren = new HashMap<>();

            cycles = new HashSet<>();
            parallel = new HashSet<>();
            exclusive = new HashSet<>();
            inclusive = new HashSet<>();

            RPSTNode root = rpst.getRoot();
            LinkedList<RPSTNode> toAnalize = new LinkedList<RPSTNode>();
            toAnalize.addLast(root);

            while (toAnalize.size() != 0) {
                root = toAnalize.removeFirst();
                if( root.getType() == TCType.B ) bottomUpRPST.add(0, root);

                for( RPSTNode n : new HashSet<RPSTNode>(rpst.getChildren(root)) ) {
                    String exitID = n.getExit().getName();
                    String entryID = n.getEntry().getName();
                    RPSTNodeToBPMNChildren.put(n, new HashSet<BPMNNode>());
                    switch (n.getType()) {
                        case R:
                            System.out.println("WARNING - rigid found.");
                            toAnalize.addLast(n);
                            return false;
                        case P:
                            toAnalize.addLast(n);
                        case T:
                            if( root.getEntry().getName().equals(exitID) && root.getExit().getName().equals(entryID) ) {
                                cycles.add(root);
                                exclusive.remove(root);
                                System.out.println("DEBUG - rpst node: cycles++");
                            }
                            break;
                        case B:
                            bondEntry.put(n, idToGate.get(entryID));
                            bondExit.put(n, idToGate.get(exitID));
                            if( idToGate.get(entryID).getGatewayType() == Gateway.GatewayType.DATABASED ) {
                                exclusive.add(n);
                                System.out.println("DEBUG - rpst node: exclusive++");
                            } else if( idToGate.get(entryID).getGatewayType() == Gateway.GatewayType.PARALLEL ) {
                                parallel.add(n);
                                System.out.println("DEBUG - rpst node: parallel++");
                            } else if( idToGate.get(entryID).getGatewayType() == Gateway.GatewayType.INCLUSIVE ) {
                                inclusive.add(n);
                                System.out.println("DEBUG - rpst node: inclusive++");
                            }

                            for(IDirectedEdge e : new HashSet<IDirectedEdge>(n.getFragmentEdges()) ) {
                                bpmnSRC = idToNode.get(e.getSource().getName());
                                bpmnTGT = idToNode.get(e.getTarget().getName());
                                if( bpmnSRC == null || bpmnTGT == null ) System.out.println("ERROR - check this one.");
                                parentSESE.put(bpmnSRC, n);
                                parentSESE.put(bpmnTGT, n);
                                RPSTNodeToBPMNChildren.get(n).add(bpmnSRC);
                                RPSTNodeToBPMNChildren.get(n).add(bpmnTGT);
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

        System.out.println("DEBUG - exclusive bonds found: " + exclusive.size());
        System.out.println("DEBUG - parallel bonds found: " + parallel.size());
        System.out.println("DEBUG - cycles bonds found: " + cycles.size());
        System.out.println("DEBUG - RPST generated correctly.");

        return true;
    }

    private void generatePaths() {
        int size = bottomUpRPST.size();
        RPSTNode bond;
        String entryID;
        String exitID;

        skippable = new HashSet<>();
        entryToExitPaths = new HashMap<>();
        HashMap<BPMNNode, BPMNNode> RPSTNodeEntryToRPSTNodeExit;


        for( int i=0; i < size; i++ ) {
            bond = bottomUpRPST.get(i);
            entryToExitPaths.put(bond, new HashSet<ArrayList<Activity>>());
            RPSTNodeEntryToRPSTNodeExit = new HashMap<>();
            System.out.println("DEBUG - generating path of a bond.");
            for( RPSTNode polygon : new HashSet<RPSTNode>(rpst.getChildren(bond)) )
                if( polygon.getType() == TCType.P ) {
                    for( RPSTNode rpstnode : new HashSet<RPSTNode>(rpst.getChildren(polygon)) ) {
                        exitID = rpstnode.getExit().getName();
                        entryID = rpstnode.getEntry().getName();
                        RPSTNodeEntryToRPSTNodeExit.put(idToNode.get(entryID), idToNode.get(exitID));
                    }
                    entryToExitPaths.get(bond).add( generatePath(bond, RPSTNodeEntryToRPSTNodeExit) );
                } else if( polygon.getType() == TCType.T ) {
                    entryID = polygon.getEntry().getName();
                    if( entryID.equalsIgnoreCase(bondEntry.get(bond).getId().toString()) ) skippable.add(bond);
                }
        }
    }

    private ArrayList<Activity> generatePath( RPSTNode bond,  HashMap<BPMNNode, BPMNNode> RPSTNodeEntryToRPSTNodeExit) {
        ArrayList<Activity> path = new ArrayList<>();
        int index = 0;
        Gateway entry = bondEntry.get(bond);
        Gateway exit = bondExit.get(bond);
        BPMNNode tmp;

        tmp = entry;
        while( true ) {
            do {
                tmp = RPSTNodeEntryToRPSTNodeExit.get(tmp);
            } while( (tmp instanceof Gateway) && (!tmp.equals(exit)) );
            if( tmp instanceof Activity) path.add(index, (Activity)tmp);
            else break;
            index++;
        }

        for( int i=0; i<path.size(); i++ ) System.out.println("DEBUG - path: " + path.get(i).getLabel());

        return path;
    }

    private void exploreDiagram(BPMNNode node, int depth) {
        if( !minTime.containsKey(node) ) {
            if(node instanceof Activity) {
                minTime.put((Activity) node, depth);
                depth++;
            }
            for(BPMNNode succ : diagramHandler.getSuccessors(node)) exploreDiagram(succ, depth);
        }
    }

    private void checkFollowers() {
        RPSTNode root = rpst.getRoot();

        for( RPSTNode child : new HashSet<RPSTNode>(rpst.getChildren(root)) ) {
            String exitID = child.getExit().getName();
            String entryID = child.getEntry().getName();
            BPMNNode entry = idToNode.get(entryID);
            BPMNNode exit = idToNode.get(exitID);
            switch(child.getType()) {
                case T:
                    if( fuzzyNet.isDirectlyFollow(entry.getLabel(), exit.getLabel()) &&
                        analizer.isEventuallyFollow(exit.getLabel(), entry.getLabel()) )
                        System.out.println("DEBUG - check this dependency: " + entry.getLabel() + " > " + exit.getLabel() );
                    break;
                case P:
                    break;
                case B:
                    break;
                case R:
                    break;
                default:
            }
        }

        checkInnerFollowers();
    }

    private void checkInnerFollowers() {
        int size = bottomUpRPST.size();
        RPSTNode bond;

        Activity src, tgt;

        for( int i=0; i < size; i++ ) {
            bond = bottomUpRPST.get(i);

            for( ArrayList<Activity> path : entryToExitPaths.get(bond) )
                for( int j=0; j < (path.size()-1); ) {
                    src = path.get(j);
                    j++;
                    tgt = path.get(j);
                    if( fuzzyNet.isDirectlyFollow(src.getLabel(), tgt.getLabel()) &&
                            analizer.isEventuallyFollow(tgt.getLabel(), src.getLabel()) )
                        System.out.println("DEBUG - check this dependency: " + src.getLabel() + " > " + tgt.getLabel() );
                }
        }
    }

    private void removeSkippingActivities() {
        removedSkippingActivity = new HashSet<>();

        for( Activity a : diagram.getActivities() ) {
            if( diagramHandler.checkAndRemoveSkippingActivity(a) ) removedSkippingActivity.add(a);
        }
        for( Gateway g : new HashSet<Gateway>(diagram.getGateways()) ) diagramHandler.checkFakeGateway(diagram, g);
    }

    private void setOrGateways() {
        Set<Integer> frequencies;

        inclusive = new HashSet<>();

        for( RPSTNode parallelBond : parallel ) {
            frequencies = new HashSet<>();
            System.out.println("DEBUG - analyzing a bond.");

            for( ArrayList<Activity> path : entryToExitPaths.get(parallelBond) )
                if (!path.isEmpty()) {
                    System.out.println("DEBUG - adding weight: " + fuzzyNet.getNode(path.get(0).getLabel()).getFrequency());
                    frequencies.add(fuzzyNet.getNode(path.get(0).getLabel()).getFrequency());
                }

            System.out.println("DEBUG - size of frequencies: " + frequencies.size());
            if( frequencies.size() > 1 ) {
                bondEntry.get(parallelBond).setGatewayType(Gateway.GatewayType.INCLUSIVE);
                bondExit.get(parallelBond).setGatewayType(Gateway.GatewayType.INCLUSIVE);
                inclusive.add(parallelBond);
            }
        }

        for( RPSTNode inclusiveBond : inclusive ) parallel.remove(inclusiveBond);
//        for( Activity a : removedSkippingActivity ) if( !inclusive.contains(parentSESE.get(a)) )  diagramHandler.setSkipping(a);
    }

    private void setSkippingActivities() {
        Set<BPMNNode> skipped = new HashSet<>();
        int size = bottomUpRPST.size();
        RPSTNode bond;

        Activity src, tgt;
        int srcFrequency, tgtFrequency;

        for( int i=0; i < size; i++ ) {
            bond = bottomUpRPST.get(i);

            for( ArrayList<Activity> path : entryToExitPaths.get(bond) )
                for( int j=0; j < (path.size()-1); ) {
                    do {
                        src = path.get(j);
                        j++;
                        tgt = path.get(j);
                        srcFrequency = fuzzyNet.getNode(src.getLabel()).getFrequency();
                        tgtFrequency = fuzzyNet.getNode(tgt.getLabel()).getFrequency();
                    } while( (srcFrequency == tgtFrequency) && (j != (path.size()-1)) );

                    if( (srcFrequency < tgtFrequency) && !skipped.contains(src) ) {
                        diagramHandler.setSkipping(src);
                        skipped.add(src);
                    }

                    if( (srcFrequency > tgtFrequency) && !skipped.contains(tgt) ) {
                        diagramHandler.setSkipping(tgt);
                        skipped.add(tgt);
                    }
                }
        }
    }

}
