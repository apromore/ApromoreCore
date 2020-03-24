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

import de.hpi.bpt.graph.DirectedEdge;
import de.hpi.bpt.graph.DirectedGraph;
import de.hpi.bpt.graph.abs.IDirectedEdge;
import de.hpi.bpt.graph.abs.IDirectedGraph;
import de.hpi.bpt.graph.algo.rpst.RPST;
import de.hpi.bpt.graph.algo.rpst.RPSTNode;
import de.hpi.bpt.graph.algo.tctree.TCType;
import de.hpi.bpt.hypergraph.abs.IVertex;
import de.hpi.bpt.hypergraph.abs.Vertex;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagramImpl;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.*;

import java.util.*;

/**
 * Created by Adriano on 16/05/2016.
 */

public class DiagramHandler {
    private BPMNDiagram diagram;
    private Map<String, BPMNNode> nodes;
    private Map<BPMNNode, HashSet<BPMNNode>> successors;
    private Map<BPMNNode, HashSet<BPMNNode>> predecessors;

    private Map<BPMNNode, HashSet<BPMNNode>> logicSuccessors;
    private Map<BPMNNode, HashSet<BPMNNode>> logicPredecessors;

    public DiagramHandler(){
        diagram = null;
        nodes = null;
        successors = null;
        predecessors = null;
    }

    public void expandSplitGateways(BPMNDiagram diagram) {
        HashSet<Gateway> gates = new HashSet<>(diagram.getGateways());
        Gateway nGate;
        BPMNNode tgt1, tgt2;
        BPMNEdge<? extends BPMNNode, ? extends BPMNNode> out1, out2;


        for(Gateway g : gates) {
            if( g.getGatewayType() == Gateway.GatewayType.PARALLEL ) continue;
            while( diagram.getOutEdges(g).size() > 2 ) {
                out1 = null;
                out2 = null;
                for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getOutEdges(g) ) {
                    if(out1 == null) out1 = e;
                    else {
                        out2 = e;
                        break;
                    }
                }
                tgt1 = out1.getTarget();
                tgt2 = out2.getTarget();
                diagram.removeEdge(out1);
                diagram.removeEdge(out2);
                nGate = diagram.addGateway("", g.getGatewayType());
                diagram.addFlow(g, nGate, "");
                diagram.addFlow(nGate, tgt1, "");
                diagram.addFlow(nGate, tgt2, "");
            }
        }
    }

    public void expandJoinGateways(BPMNDiagram diagram) {
        HashSet<Gateway> gates = new HashSet<>(diagram.getGateways());
        Gateway nGate;
        BPMNNode src1, src2;
        BPMNEdge<? extends BPMNNode, ? extends BPMNNode> in1, in2;


        for(Gateway g : gates) {
            while( diagram.getInEdges(g).size() > 2 ) {
                in1 = null;
                in2 = null;
                for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getInEdges(g) ) {
                    if(in1 == null) in1 = e;
                    else {
                        in2 = e;
                        break;
                    }
                }
                src1 = in1.getSource();
                src2 = in2.getSource();
                diagram.removeEdge(in1);
                diagram.removeEdge(in2);
                nGate = diagram.addGateway("", g.getGatewayType());
                diagram.addFlow(nGate, g, "");
                diagram.addFlow(src1, nGate, "");
                diagram.addFlow(src2, nGate, "");
            }
        }
    }

    public void removeLoopActivityMarkers(BPMNDiagram diagram) {
        Gateway entry, exit;
        BPMNEdge<? extends BPMNNode, ? extends BPMNNode> in, out;
        in = null;
        out = null;

        for( Activity a : diagram.getActivities() )
            if( a.isBLooped() ) {
                a.setBLooped(false);
                entry = diagram.addGateway("", Gateway.GatewayType.DATABASED, a.getParentSubProcess());
                exit = diagram.addGateway("", Gateway.GatewayType.DATABASED, a.getParentSubProcess());
                for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getInEdges(a) ) in = e;
                for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getOutEdges(a) ) out = e;
                diagram.addFlow(in.getSource(), entry, "");
                diagram.addFlow(exit, out.getTarget(), "");
                diagram.addFlow(exit, entry, "");
                diagram.addFlow(entry, a, "");
                diagram.addFlow(a, exit, "");
                diagram.removeEdge(in);
                diagram.removeEdge(out);
            }
    }

    public void removeJoinSplit(BPMNDiagram diagram) {
//        this method removes join/split gateways, transforming them into a sequence of a join and a split
        Gateway split;
        HashSet<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> removable;
        int jsCounter = 0;

        for( Gateway join : new HashSet<Gateway>(diagram.getGateways()) )
            if( (diagram.getInEdges(join).size() > 1) && (diagram.getOutEdges(join).size() > 1) ) {
                jsCounter++;

//                this is going to be the new split, and we will keep js as join only
//                split = diagram.addGateway("", join.getGatewayType());
                split = diagram.addGateway("js"+jsCounter, join.getGatewayType());
                join.setGatewayType(Gateway.GatewayType.INCLUSIVE);

                removable = new HashSet<>();
                for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getOutEdges(join) ) {
                    diagram.addFlow(split, e.getTarget(), "");
                    removable.add(e);
                }

                diagram.addFlow(join, split, "");
                
                for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : removable ) diagram.removeEdge(e);
            }

//       System.out.println("DEBUG - join/split removed: " + jsCounter);
    }

    public void touch(BPMNDiagram diagram) {
        int l = 0;
        for( Flow f : diagram.getFlows() ) f.setLabel(Integer.toString(l++));
    }

    public void reduceUnsoundness(BPMNDiagram diagram) {
        BPMNNode src, tgt;
        Gateway oldXOR = null;
        Gateway newXOR = null;
        HashSet<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> toRemove;
        BPMNEdge<? extends BPMNNode, ? extends BPMNNode> inFlow = null;
        BPMNEdge<? extends BPMNNode, ? extends BPMNNode> outFlow = null;
        boolean keepGoing;

        do {
            keepGoing = false;
            toRemove = new HashSet<>();

            for(Gateway g : diagram.getGateways() )
                if( g.getGatewayType() == Gateway.GatewayType.PARALLEL ) {
                    if( diagram.getOutEdges(g).size() > 1 && diagram.getInEdges(g).size() == 1 ) {
                        for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> f : diagram.getOutEdges(g) ) {
                            tgt = f.getTarget();
                            if( tgt instanceof Gateway && (((Gateway) tgt).getGatewayType() == Gateway.GatewayType.DATABASED) ) {
                                keepGoing = true;
                                oldXOR = (Gateway) tgt;
                                outFlow = f;
                                for(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> ff : diagram.getInEdges(g)) inFlow = ff;
                                break;
                            }
                        }
                        if( keepGoing ) break;
                    }
                }

            if( keepGoing ) {
                diagram.removeEdge(outFlow);
                src = inFlow.getSource();
                tgt = inFlow.getTarget();
                if( src instanceof Gateway && ((Gateway) src).getGatewayType() == Gateway.GatewayType.DATABASED ) {
                    newXOR = (Gateway) src;
                } else {
                    newXOR = diagram.addGateway("new-XOR", Gateway.GatewayType.DATABASED);
                    diagram.addFlow(src, newXOR, "");
                    diagram.addFlow(newXOR, tgt, "");
                    diagram.removeEdge(inFlow);
                }

                for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> f : diagram.getInEdges(oldXOR) ) {
                    diagram.addFlow(f.getSource(), newXOR, "");
                    toRemove.add(f);
                }

                for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> f : diagram.getOutEdges(oldXOR) ) {
                    diagram.addFlow(tgt, f.getTarget(), "");
                    toRemove.add(f);
                }

                for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> f : toRemove ) diagram.removeEdge(f);
                removeNode(diagram, oldXOR);
            }
        }while( keepGoing );
    }

    public void removeEmptyParallelFlows(BPMNDiagram diagram) {
        BPMNNode src, tgt;
        boolean keepGoing;
        HashSet<Flow> toRemove;
        HashSet<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> toExtend;
        HashSet<Gateway> toCheck;

        for( Gateway g : new HashSet<Gateway>(diagram.getGateways()) ) checkFakeGateway(diagram, g);

        do{
            toRemove = new HashSet<>();
            toCheck = new HashSet<>();
            keepGoing = false;
            for( Flow f : diagram.getFlows() ) {
                src = f.getSource();
                tgt = f.getTarget();
                if( src instanceof Gateway &&
                    tgt instanceof Gateway &&
                    ((Gateway) src).getGatewayType() == ((Gateway) tgt).getGatewayType() &&
                    ((Gateway) src).getGatewayType() == Gateway.GatewayType.PARALLEL ) {
                    toRemove.add(f);
                    System.out.println("DEBUG - removing empty parallel flow.");
                    toCheck.add((Gateway) src);
                    toCheck.add((Gateway) tgt);
                    keepGoing = true;
                }
            }

            for( Flow f : toRemove ) {
                src = f.getSource();
                tgt = f.getTarget();
                diagram.removeEdge(f);

                if( diagram.getOutEdges(src).size() == 0 ) {
                    toExtend = new HashSet<>();
                    for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> ff : diagram.getInEdges(src)) toExtend.add(ff);

                    for(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> ff : toExtend) {
                        src = ff.getSource();
                        diagram.removeEdge(ff);
                        diagram.addFlow(src, tgt, "");
                    }
                } else if( diagram.getInEdges(tgt).size() == 0 ) {
                    toExtend = new HashSet<>();
                    for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> ff : diagram.getOutEdges(tgt)) toExtend.add(ff);

                    for(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> ff : toExtend) {
                        tgt = ff.getTarget();
                        diagram.removeEdge(ff);
                        diagram.addFlow(src, tgt, "");
                    }
                }
            }

            if( keepGoing ) {
                for( Gateway g : toCheck ) checkFakeGateway(diagram, g);
            }
        } while( keepGoing );
    }

    public void removeDuplicates(BPMNDiagram diagram) {
        while( checkDuplicates(diagram) ) ;
    }

    public boolean checkDuplicates(BPMNDiagram diagram) {
        if(diagram == null) return false;
        boolean changed = false;

        try {
            HashMap<String, BPMNNode> bpmnNodes = new HashMap<>();
            HashMap<BPMNNode, Vertex> mapping = new HashMap<BPMNNode, Vertex>();
            HashMap<String, Gateway> gates = new HashMap<String, Gateway>();
            HashMap<Gateway, Map<RPSTNode, HashSet<RPSTNode>>> checkMap = new HashMap<>();
            HashMap<Gateway, ArrayList<RPSTNode>> hierarchy = new HashMap<>();
            ArrayList<RPSTNode> rpstNodeHierarchy = new ArrayList<>();
            HashMap<RPSTNode, String> rpstNodeToID = new HashMap<>();

            IDirectedGraph<DirectedEdge, Vertex> graph = new DirectedGraph();
            Vertex src;
            Vertex tgt;

            Gateway parentExit;
            Gateway childExit;

            BPMNNode bpmnSRC;
            BPMNNode bpmnTGT;

            for (Flow f : diagram.getFlows((Swimlane) null)) {
                bpmnSRC = f.getSource();
                bpmnTGT = f.getTarget();
                if (!mapping.containsKey(bpmnSRC)) {
                    src = new Vertex(bpmnSRC.getId().toString());
                    if (bpmnSRC instanceof Gateway) gates.put(bpmnSRC.getId().toString(), (Gateway) bpmnSRC);
                    mapping.put(bpmnSRC, src);
                    bpmnNodes.put(bpmnSRC.getId().toString(), bpmnSRC);
                } else src = mapping.get(bpmnSRC);

                if (!mapping.containsKey(bpmnTGT)) {
                    tgt = new Vertex(bpmnTGT.getId().toString());
                    if (bpmnTGT instanceof Gateway) gates.put(bpmnTGT.getId().toString(), (Gateway) bpmnTGT);
                    mapping.put(bpmnTGT, tgt);
                    bpmnNodes.put(bpmnTGT.getId().toString(), bpmnTGT);
                } else tgt = mapping.get(bpmnTGT);

                graph.addEdge(src, tgt);
            }

            RPST rpst = new RPST(graph);

            RPSTNode root = rpst.getRoot();
            RPSTNode rootParent;
            RPSTNode key;
            LinkedList<RPSTNode> toAnalize = new LinkedList<RPSTNode>();
            toAnalize.addLast(root);

            while (toAnalize.size() != 0) {
                root = toAnalize.removeFirst();
                rootParent = rpst.getParent(root);
                rpstNodeHierarchy.add(0, root);

                if( root.getType() == TCType.T ) continue;

                if( root.getType() == TCType.B ) {
                    parentExit = gates.get(root.getExit().getName());
                    if( !checkMap.containsKey(parentExit) ) {
                        checkMap.put(parentExit, new HashMap<RPSTNode, HashSet<RPSTNode>>());
                        hierarchy.put(parentExit, new ArrayList<RPSTNode>());
                    }
                    if( !checkMap.get(parentExit).containsKey(root) ) checkMap.get(parentExit).put(root, new HashSet<RPSTNode>());
                    key = root;
                    hierarchy.get(parentExit).add(0, key);
                } else if( (root.getType() == TCType.P) && (rootParent != null) && (rootParent.getType() == TCType.B) ) {
                    parentExit = gates.get(root.getExit().getName());
                    if( !checkMap.containsKey(parentExit) ) {
                        checkMap.put(parentExit, new HashMap<RPSTNode, HashSet<RPSTNode>>());
                        hierarchy.put(parentExit, new ArrayList<RPSTNode>());
                    }
                    if( !checkMap.get(parentExit).containsKey(rootParent) ) checkMap.get(parentExit).put(rootParent, new HashSet<RPSTNode>());
                    key = rootParent;
                    hierarchy.get(parentExit).add(0, key);
                } else {
                    parentExit = null;
                    key = null;
                }

                for (RPSTNode n : new HashSet<RPSTNode>(rpst.getChildren(root))) {
                    String exitID = n.getExit().getName();
                    String entryID = n.getEntry().getName();
                    switch (n.getType()) {
                        case R:
                            System.out.println("WARNING - rigid found.");
                            return false;
                        case T:
                            if( gates.containsKey(exitID) && !gates.containsKey(entryID) ) {
                                childExit = gates.get(exitID);
                                if( childExit == parentExit ) {
                                    //System.out.println("DEBUG - adding trivial for gate: " + childExit.getLabel());
                                    checkMap.get(parentExit).get(key).add(n);
                                }
                            }
                            toAnalize.addLast(n);
                            break;
                        case P:
                            toAnalize.addLast(n);
                            break;
                        case B:
                            childExit = gates.get(exitID);
                            if( childExit == parentExit ) {
                                //System.out.println("DEBUG - adding bond for gate: " + childExit.getLabel());
                                checkMap.get(parentExit).get(key).add(n);
                            }
                            toAnalize.addLast(n);
                            break;
                        default:
                    }
                }
            }

            while( !rpstNodeHierarchy.isEmpty() )
                generateRPSTNodeCode(rpst, rpstNodeHierarchy.remove(0), rpstNodeToID, bpmnNodes);

            for( Gateway gate : checkMap.keySet() )
            while( !hierarchy.get(gate).isEmpty() ) {
                    RPSTNode bondParent = hierarchy.get(gate).remove(0);
                    HashSet<RPSTNode> enteringRPSTNodes = checkMap.get(gate).get(bondParent);
                    Set<RPSTNode> bondToCompare = new HashSet<>();
                    Set<RPSTNode> trivialToCompare = new HashSet<>();

                    for( RPSTNode node : enteringRPSTNodes ) {
                        if( node.getType() == TCType.T ) trivialToCompare.add(node);
                        else bondToCompare.add(node);
                    }

                    if( !trivialToCompare.isEmpty() ) changed = mergeTrivials(trivialToCompare, bpmnNodes, diagram, gate);
                    if( changed ) return changed;

                    if( !bondToCompare.isEmpty() ) changed = mergeBonds(bondToCompare, rpstNodeToID, bpmnNodes, diagram);
                    if( changed ) return changed;
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.out.println("WARNING - impossible remove duplicates.");
        }

        return changed;
    }

    private void generateRPSTNodeCode(RPST rpst, RPSTNode node, HashMap<RPSTNode, String> rpstNodeToID, Map<String, BPMNNode> mapping) {
        String code = new String();
        LinkedList<String> childrenCodes = new LinkedList<>();
        BPMNNode entry = mapping.get(node.getEntry().getName());
        BPMNNode exit = mapping.get(node.getExit().getName());

        switch( node.getType() ) {
            case T:
                //System.out.println("DEBUG - generating code for a Trivial.");
                String entryCode, exitCode;
                if(entry instanceof Gateway) entryCode = ((Gateway) entry).getGatewayType().toString();
                else entryCode = entry.getLabel();

                if(exit instanceof Gateway) exitCode = ((Gateway) exit).getGatewayType().toString();
                else exitCode = exit.getLabel();

                code += "T." + entryCode + "." + exitCode;
                break;

            case P:
                //System.out.println("DEBUG - generating code for a Polygon.");
                String entryID = entry.getId().toString();
                String exitID = exit.getId().toString();
                do {
                    for( RPSTNode next : new HashSet<RPSTNode>(rpst.getChildren(node)) )
                        if( entryID.equalsIgnoreCase(next.getEntry().getName()) ) {
                            if( rpstNodeToID.containsKey(next) ) childrenCodes.addLast(rpstNodeToID.get(next));
                            else System.out.println("ERROR - code not found. Wrong tree traversal.");
                            entryID = next.getExit().getName();
                            //System.out.println("DEBUG - updating entry.");
                            break;
                        }
                    //System.out.println("DEBUG - cycling.");
                } while( !entryID.equalsIgnoreCase(exitID) );

                code += "P.";
                while( childrenCodes.size() != 1 ) code += childrenCodes.removeFirst() + ".";
                code += childrenCodes.removeFirst();
                break;

            case B:
                //System.out.println("DEBUG - generating code for a Bond.");
                for( RPSTNode n : new HashSet<RPSTNode>(rpst.getChildren(node)) )
                    if( rpstNodeToID.containsKey(n) ) childrenCodes.add(rpstNodeToID.get(n));
                    else System.out.println("ERROR - code not found. Wrong tree traversal.");

                Collections.sort(childrenCodes);
                code += "B.";
                while( childrenCodes.size() != 1 ) code += childrenCodes.remove(0) + ".";
                code += childrenCodes.remove(0);
                break;

            case R:
                System.out.println("WARNING - impossible is happening.");
                code += "rigid";
                break;
        }

        //System.out.println("DEBUG - CODE: " + code);
        rpstNodeToID.put(node, code);
    }

    private boolean mergeTrivials(Set<RPSTNode> trivialToCompare, Map<String, BPMNNode> mapping, BPMNDiagram diagram, Gateway exit) {
        Map<String, Set<BPMNNode>> duplicates = new HashMap<>();
        Set<BPMNNode> toKeep = new HashSet<>();
        BPMNNode bpmnNode;
        boolean changed = false;

        //System.out.println("DEBUG - trying to merge trivials: " + trivialToCompare.size());

        for( RPSTNode n : trivialToCompare ) {
            bpmnNode = mapping.get(n.getEntry().getName());
            if(bpmnNode instanceof Gateway) continue;
            String label = bpmnNode.getLabel();
            if( !duplicates.containsKey(label) ) {
                duplicates.put(label, new HashSet<BPMNNode>());
                toKeep.add(bpmnNode);
            } else duplicates.get(label).add(bpmnNode);
        }

        for( BPMNNode survivor : toKeep ) {
            String label = survivor.getLabel();
            if( !duplicates.get(label).isEmpty() ) {
                Gateway gate = diagram.addGateway("", exit.getGatewayType(), survivor.getParentSubProcess());
                for( BPMNNode n : duplicates.get(label) ) {
                    for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getOutEdges(n) ) diagram.removeEdge(e);
                    for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getInEdges(n) ) {
                        diagram.addFlow(e.getSource(), gate, "");
                        diagram.removeEdge(e);
                    }
                    removeNode(diagram, n);
                }
                for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getInEdges(survivor) ) {
                    diagram.addFlow(e.getSource(), gate, "");
                    diagram.removeEdge(e);
                }
                diagram.addFlow(gate, survivor, "");
                changed = true;
                System.out.println("SIMPLIFY - removed TRIVIALS: " + duplicates.get(label).size());
            }
        }

        return changed;
    }


    private boolean mergeBonds(Set<RPSTNode> bondsToCompare, HashMap<RPSTNode, String> rpstNodeToID, Map<String, BPMNNode> mapping, BPMNDiagram diagram) {
        Map<String, Set<RPSTNode>> duplicates = new HashMap<>();
        Set<RPSTNode> toKeep = new HashSet<>();

        boolean changed = false;

//        System.out.println("DEBUG - trying to merge bonds: " + bondsToCompare.size());

        for( RPSTNode bond : bondsToCompare ) {
            String code = rpstNodeToID.get(bond);
            if( !duplicates.containsKey(code) ) {
                duplicates.put(code, new HashSet<RPSTNode>());
                toKeep.add(bond);
            } else duplicates.get(code).add(bond);
        }

        for( RPSTNode bond : toKeep ) {
            String code = rpstNodeToID.get(bond);
            BPMNNode entry = mapping.get(bond.getEntry().getName());
            if( !duplicates.get(code).isEmpty() ) {
                for( RPSTNode duplicateBond : duplicates.get(code) ) removeBond(duplicateBond, mapping, diagram, entry);
                changed = true;
                System.out.println("SIMPLIFY - removed BONDS: " + duplicates.get(code).size());
            }
        }

        return changed;
    }

    private void removeBond(RPSTNode bond, Map<String, BPMNNode> mapping, BPMNDiagram diagram, BPMNNode newEntry) {
        HashSet<IDirectedEdge> edges = new HashSet<>(bond.getFragmentEdges());
        HashSet<BPMNNode> nodes = new HashSet<>();
        IVertex src, tgt;
        BPMNNode bpmnSRC, bpmnTGT;

        BPMNNode entry;
        BPMNNode exit;

        entry = mapping.get(bond.getEntry().getName());
        exit = mapping.get(bond.getExit().getName());

        if(entry == null) {
            System.out.println("ERROR - impossible remove bond: gateway not found in mapping.");
            return;
        }

        for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getInEdges(entry) ) {
            diagram.addFlow(e.getSource(), newEntry, "");
            diagram.removeEdge(e);
        }

        for(IDirectedEdge e : edges) {
            src = e.getSource();
            tgt = e.getTarget();

            bpmnSRC = mapping.get(src.getName());
            bpmnTGT = mapping.get(tgt.getName());

            if (bpmnSRC == null) {
                System.out.println("ERROR - bpmnSRC not found in mapping.");
                return;
            }

            if (bpmnTGT == null) {
                System.out.println("ERROR - bpmnTGT not found in mapping.");
                return;
            }

            nodes.add(bpmnSRC);
            for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> ee : diagram.getOutEdges(bpmnSRC)) diagram.removeEdge(ee);
            for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> ee : diagram.getInEdges(bpmnSRC)) diagram.removeEdge(ee);

            if (bpmnTGT != exit) {
                nodes.add(bpmnTGT);
                for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> ee : diagram.getOutEdges(bpmnTGT)) diagram.removeEdge(ee);
                for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> ee : diagram.getInEdges(bpmnTGT)) diagram.removeEdge(ee);
            }
        }
        for(BPMNNode n : nodes) removeNode(diagram, n);
    }

    public void fixSoundness(BPMNDiagram diagram) {
        if(diagram == null) return;

        try {
            HashMap<BPMNNode, Vertex> mapping = new HashMap<BPMNNode, Vertex>();
            HashMap<String, Gateway> gates = new HashMap<String, Gateway>();

            IDirectedGraph<DirectedEdge, Vertex> graph = new DirectedGraph();
            Vertex src;
            Vertex tgt;

            BPMNNode bpmnSRC;
            BPMNNode bpmnTGT;

            for (Flow f : diagram.getFlows((Swimlane) null)) {
                bpmnSRC = f.getSource();
                bpmnTGT = f.getTarget();
                if (!mapping.containsKey(bpmnSRC)) {
                    src = new Vertex(bpmnSRC.getId().toString());
                    if (bpmnSRC instanceof Gateway) gates.put(bpmnSRC.getId().toString(), (Gateway) bpmnSRC);
                    mapping.put(bpmnSRC, src);
                } else src = mapping.get(bpmnSRC);

                if (!mapping.containsKey(bpmnTGT)) {
                    tgt = new Vertex(bpmnTGT.getId().toString());
                    if (bpmnTGT instanceof Gateway) gates.put(bpmnTGT.getId().toString(), (Gateway) bpmnTGT);
                    mapping.put(bpmnTGT, tgt);
                } else tgt = mapping.get(bpmnTGT);

                graph.addEdge(src, tgt);
            }

            RPST rpst = new RPST(graph);

            RPSTNode root = rpst.getRoot();
            LinkedList<RPSTNode> toAnalize = new LinkedList<RPSTNode>();
            toAnalize.add(root);

            while (toAnalize.size() != 0) {
                root = toAnalize.pollFirst();

                for (RPSTNode n : new HashSet<RPSTNode>(rpst.getChildren(root))) {
                    switch (n.getType()) {
                        case R:
                            toAnalize.add(n);
                            break;
                        case T:
                            break;
                        case P:
                            toAnalize.add(n);
                            break;
                        case B:
                            Gateway entry = gates.get(n.getEntry().getName());
                            Gateway exit = gates.get(n.getExit().getName());
                            exit.setGatewayType(entry.getGatewayType());
                            toAnalize.add(n);
                            break;
                        default:
                    }
                }
                toAnalize.remove(root);
            }

        } catch (Exception e) {
            System.out.println("WARNING - impossible fix soundness.");
        }
    }

    public void fixImplicitGateways(BPMNDiagram diagram) {
        Gateway g;
        HashSet<Flow> inFlows;
        HashSet<Flow> outFlows;

        for (BPMNNode n : diagram.getNodes()) {
            if( n instanceof Activity || n instanceof CallActivity) {
                inFlows = new HashSet<>();
                outFlows = new HashSet<>();

                for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getInEdges(n) )
                    if(e instanceof Flow) inFlows.add((Flow)e);

                for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getOutEdges(n) )
                    if(e instanceof Flow) outFlows.add((Flow)e);

                if( inFlows.size() > 1 ) {
                    g = diagram.addGateway("exGate", Gateway.GatewayType.DATABASED, n.getParentSubProcess());
                    g.setParentSwimlane(n.getParentSwimlane());
                    for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : inFlows ) {
                        diagram.addFlow(e.getSource(), g, "");
                        diagram.removeEdge(e);
                    }
                    diagram.addFlow(g, n, "");
                }

                if( outFlows.size() > 1 ) {
                    g = diagram.addGateway("exGate", Gateway.GatewayType.PARALLEL, n.getParentSubProcess());
                    g.setParentSwimlane(n.getParentSwimlane());
                    for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : outFlows ) {
                        diagram.addFlow(g, e.getTarget(), "");
                        diagram.removeEdge(e);
                    }
                    diagram.addFlow(n, g, "");
                }
            }
        }
    }

    public void removeTrivialGateways(BPMNDiagram diagram) {
        BPMNNode src = null;
        BPMNNode tgt = null;
        HashSet<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> removable;

        for( Gateway g : new HashSet<>(diagram.getGateways()) ) {
            removable = new HashSet<>();

            removable.addAll(diagram.getInEdges(g));
            if( removable.size() != 1 ) continue;

            removable.addAll(diagram.getOutEdges(g));
            if( removable.size() != 2 ) continue;

            for(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : removable) {
                if(e.getTarget() == g)  src = e.getSource();
                else tgt = e.getTarget();
                diagram.removeEdge(e);
            }
            diagram.removeGateway(g);
            diagram.addFlow(src, tgt, "");
        }
    }

    public void checkFakeGateway(BPMNDiagram diagram, Gateway g) {
        //System.out.println("DEBUG - checking fake gateways: " + g.getId());
        BPMNEdge<? extends BPMNNode, ? extends BPMNNode> in = null;
        BPMNEdge<? extends BPMNNode, ? extends BPMNNode> out = null;
        int incoming = 0;
        int outgoing = 0;

        for( Flow f : diagram.getFlows() ) {
            if( f.getSource() == g ) {
                out = f;
                outgoing++;
            }
            if( f.getTarget() == g ) {
                in = f;
                incoming++;
            }
        }

        if( (outgoing == 0) && (incoming == 0) ) removeNode(diagram, g);
        if( (outgoing == 1) && (incoming == 1) ) {
            diagram.addFlow(in.getSource(), out.getTarget(), "");
            diagram.removeEdge(in);
            diagram.removeEdge(out);
            removeNode(diagram, g);
            //System.out.println("DEBUG - found and removed a fake gate: " + g.getId());
        }
    }

    public void removeDoubleEdges(BPMNDiagram diagram) {
        HashSet<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> towAway = new HashSet<>();
        HashMap<BPMNNode, HashSet<BPMNNode>> flows = new HashMap<>();

        for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> f : diagram.getEdges() ) {
            if( !flows.containsKey(f.getSource()) ) {
                flows.put(f.getSource(), new HashSet<BPMNNode>());
                flows.get(f.getSource()).add(f.getTarget());
            } else {
                if( flows.get(f.getSource()).contains(f.getTarget()) ) towAway.add(f);
                else flows.get(f.getSource()).add(f.getTarget());
            }
        }

        for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> ff : towAway ) {
            //System.out.println("DEBUG - doubleFlow removed: " + ff.getSource().getId() + " > " + ff.getTarget().getId());
            diagram.removeEdge(ff);
            if( ff.getSource() instanceof Gateway )	checkFakeGateway(diagram, (Gateway) ff.getSource());
            if( ff.getTarget() instanceof Gateway )	checkFakeGateway(diagram, (Gateway) ff.getTarget());
        }
    }

    public void collapseSplitGateways(BPMNDiagram diagram) {
        LinkedList<Gateway> gates = new LinkedList<>(diagram.getGateways());
        Set<Gateway> eaten = new HashSet<>();
        Gateway eater;
        Gateway meal;
        boolean unhealthy;

        do {
            eater = gates.pollFirst();
            while( !eaten.contains(eater) ) {
                meal = null;
                unhealthy = true;

                for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getOutEdges(eater) )
                    if( (e.getTarget() instanceof Gateway) && ((meal = (Gateway) e.getTarget()).getGatewayType() == eater.getGatewayType()) ) {
                        unhealthy = false;
                        for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> ee : diagram.getInEdges(meal) )
                            if( ee.getSource() != eater ) {
                                unhealthy = true;
                                break;
                            }

                        if( unhealthy ) continue;
                        break;
                    }

                if( unhealthy ) break;
                else {
                    eatSplit(diagram, meal, eater);
                    eaten.add(meal);
                }
            }
        } while( eater != null );
//        System.out.println("DEBUG - collapsed gateways [split]: " + eaten.size());
    }

    public void collapseJoinGateways(BPMNDiagram diagram) {
        LinkedList<Gateway> gates = new LinkedList<>(diagram.getGateways());
        Set<Gateway> eaten = new HashSet<>();
        Gateway eater;
        Gateway meal;
        boolean unhealthy;

        do {
            eater = gates.pollFirst();
            while( !eaten.contains(eater) ) {
                meal = null;
                unhealthy = true;

                for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getInEdges(eater) )
                    if( (e.getSource() instanceof Gateway) && ((meal = (Gateway) e.getSource()).getGatewayType() == eater.getGatewayType()) ) {
                        unhealthy = false;
                        for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> ee : diagram.getOutEdges(meal) )
                            if( ee.getTarget() != eater ) {
                                unhealthy = true;
                                break;
                            }

                        if( unhealthy ) continue;
                        break;
                    }

                if( unhealthy ) break;
                else {
                    eatJoin(diagram, meal, eater);
                    eaten.add(meal);
                }
            }
        } while( eater != null );
//        System.out.println("DEBUG - collapsed gateways [join]: " + eaten.size());
    }

    private void eatSplit(BPMNDiagram diagram, Gateway meal, Gateway eater) {
        Set<BPMNEdge> mealRemains = new HashSet<>();

        for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getInEdges(meal) ) mealRemains.add(e);
        for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getOutEdges(meal) ) {
            mealRemains.add(e);
            diagram.addFlow(eater, e.getTarget(), "");
        }

        for(BPMNEdge e : mealRemains) diagram.removeEdge(e);
        removeNode(diagram, meal);
    }

    private void eatJoin(BPMNDiagram diagram, Gateway meal, Gateway eater) {
        Set<BPMNEdge> mealRemains = new HashSet<>();

        for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getOutEdges(meal) ) mealRemains.add(e);
        for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getInEdges(meal) ) {
            mealRemains.add(e);
            diagram.addFlow(e.getSource(), eater, "");
        }

        for(BPMNEdge e : mealRemains) diagram.removeEdge(e);
        removeNode(diagram, meal);
    }

    public BPMNDiagram copyDiagram (BPMNDiagram diagram) {
        BPMNDiagram duplicateDiagram = new BPMNDiagramImpl(diagram.getLabel());
        HashMap<BPMNNode, BPMNNode> originalToCopy = new HashMap<>();
        BPMNNode src, tgt;
        BPMNNode copy;
        SubProcess parentSubprocess;
        Swimlane parentSwimlane;
        Activity exceptionFor;
        BPMNNode newExceptionFor;

        for( BPMNNode n : diagram.getNodes() ) {
            copy = copyNode(duplicateDiagram, n, n.getLabel());
            if( copy != null ) originalToCopy.put(n, copy);
            else System.out.println("ERROR - diagram duplication failed [1].");
        }

        for( Flow f : diagram.getFlows() ) {
            src = originalToCopy.get(f.getSource());
            tgt = originalToCopy.get(f.getTarget());

            if( src != null && tgt != null ) duplicateDiagram.addFlow(src, tgt, "");
            else System.out.println("ERROR - diagram duplication failed [2].");
        }

        for( BPMNNode n : originalToCopy.keySet() ) {
            parentSubprocess = (SubProcess) originalToCopy.get(n.getParentSubProcess());
            parentSwimlane = (Swimlane) originalToCopy.get(n.getParentSwimlane());

            copy = originalToCopy.get(n);
            copy.setParentSubprocess(parentSubprocess);
            copy.setParentSwimlane(parentSwimlane);

            if( (n instanceof Event) && (copy instanceof Event) ) {
                exceptionFor = ((Event)n).getBoundingNode();
                if( exceptionFor != null ) {
                    newExceptionFor = originalToCopy.get(exceptionFor);
                    if( newExceptionFor instanceof Activity ) ((Event) copy).setExceptionFor((Activity) newExceptionFor);
                }
            }
        }

        return duplicateDiagram;
    }


    public BPMNNode copyNode(BPMNDiagram diagram, BPMNNode node, String label) {
        BPMNNode duplicate = null;

        if( node instanceof SubProcess) {
            duplicate = diagram.addSubProcess( label,
                    ((Activity) node).isBLooped(),
                    ((Activity) node).isBAdhoc(),
                    ((Activity) node).isBCompensation(),
                    ((Activity) node).isBMultiinstance(),
                    ((Activity) node).isBCollapsed(),
                    (SubProcess) null);

        } else if( node instanceof Activity) {
            duplicate = diagram.addActivity( label,
                    ((Activity) node).isBLooped(),
                    ((Activity) node).isBAdhoc(),
                    ((Activity) node).isBCompensation(),
                    ((Activity) node).isBMultiinstance(),
                    ((Activity) node).isBCollapsed(),
                    (SubProcess) null);

        } else if( node instanceof CallActivity) {
            duplicate = diagram.addCallActivity( label,
                    ((CallActivity) node).isBLooped(),
                    ((CallActivity) node).isBAdhoc(),
                    ((CallActivity) node).isBCompensation(),
                    ((CallActivity) node).isBMultiinstance(),
                    ((CallActivity) node).isBCollapsed(),
                    (SubProcess) null);

        } else if( node instanceof Event) {
            duplicate = diagram.addEvent( label,
                    ((Event) node).getEventType(),
                    ((Event) node).getEventTrigger(),
                    ((Event) node).getEventUse(),
                    (SubProcess) null,
                    true,
                    null);

        } else if( node instanceof Gateway) {
            duplicate = diagram.addGateway( label,
                    ((Gateway) node).getGatewayType(),
                    (SubProcess) null);

            duplicate.setParentSwimlane(node.getParentSwimlane());
            ((Gateway) duplicate).setMarkerVisible(((Gateway) node).isMarkerVisible());
            ((Gateway) duplicate).setDecorator(((Gateway) node).getDecorator());
        }

        return duplicate;
    }


    private void removeNode(BPMNDiagram diagram, BPMNNode n) {
        diagram.removeNode(n);
        if( n.getParentSubProcess() != null ) n.getParentSubProcess().getChildren().remove(n);
        if( n.getParentSwimlane() != null ) n.getParentSwimlane().getChildren().remove(n);
    }

    public void setDiagram(BPMNDiagram diagram) {
        this.diagram = diagram;
        nodes = new HashMap<>();
        predecessors = new HashMap<>();
        successors = new HashMap<>();
        BPMNNode src, tgt;

        for( BPMNNode node : diagram.getNodes() ) {
            successors.put(node, new HashSet<BPMNNode>());
            predecessors.put(node, new HashSet<BPMNNode>());
            nodes.put(node.getLabel(), node);
        }

        for( Flow f : diagram.getFlows()) {
            src = f.getSource();
            tgt = f.getTarget();

            successors.get(src).add(tgt);
            predecessors.get(tgt).add(src);
        }
    }

    public Set<BPMNNode> getPredecessors(BPMNNode node) { return new HashSet<>(predecessors.get(node)); }
    public Set<BPMNNode> getSuccessors(BPMNNode node) { return new HashSet<>(successors.get(node)); }

    public BPMNNode getSuccessor(Activity a) { return (new ArrayList<BPMNNode>(successors.get(a))).get(0); }
    public BPMNNode getPredecessor(Activity a) { return (new ArrayList<BPMNNode>(predecessors.get(a))).get(0); }


    public boolean checkAndRemoveSkippingActivity(Activity a) {
        BPMNEdge<? extends BPMNNode, ? extends BPMNNode> toRemove = null;
        BPMNNode prev = getPredecessor(a);
        BPMNNode succ = getSuccessor(a);

        if( (prev instanceof Gateway) && (succ instanceof Gateway) && (successors.get(prev).size() == 2) ) {
            for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getOutEdges(prev) )
                if( e.getTarget().equals(succ) ) {
                    toRemove = e;
                    break;
                }

            if( toRemove != null ) {
                diagram.removeEdge(toRemove);
                System.out.println("DEBUG - removed skypping activity: " + a.getLabel());
                return true;
            }
        }

        return false;
    }

    public void removeEdge(BPMNNode src, BPMNNode tgt) {
        BPMNEdge<? extends BPMNNode, ? extends BPMNNode> toRemove = null;

        for( BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getOutEdges(src) )
            if( e.getTarget().equals(tgt) ) {
                toRemove = e;
                break;
            }

        if( toRemove != null ) diagram.removeEdge(toRemove);
    }

    public void setSkipping(Activity a) {
        System.out.println("DEBUG - adding skipping activity: " + a.getLabel());
        BPMNNode prev = getPredecessor(a);
        BPMNNode succ = getSuccessor(a);
        Gateway  gEntry = diagram.addGateway("", Gateway.GatewayType.DATABASED);
        Gateway  gExit = diagram.addGateway("", Gateway.GatewayType.DATABASED);
        Set<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> toRemove = new HashSet();

        for(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getInEdges(a)) toRemove.add(e);
        for(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : diagram.getOutEdges(a)) toRemove.add(e);

        for(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : toRemove) diagram.removeEdge(e);

        diagram.addFlow(gEntry, a, "");
        diagram.addFlow(a, gExit, "");
        diagram.addFlow(gEntry, gExit, "");
        diagram.addFlow(prev, gEntry, "");
        diagram.addFlow(gExit, succ, "");
    }

}
