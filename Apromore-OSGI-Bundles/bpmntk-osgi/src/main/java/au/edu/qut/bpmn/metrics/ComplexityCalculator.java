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

package au.edu.qut.bpmn.metrics;

import de.hpi.bpt.graph.DirectedEdge;
import de.hpi.bpt.graph.DirectedGraph;
import de.hpi.bpt.graph.abs.IDirectedGraph;
import de.hpi.bpt.graph.algo.rpst.RPST;
import de.hpi.bpt.graph.algo.rpst.RPSTNode;
import de.hpi.bpt.graph.algo.tctree.TCType;
import de.hpi.bpt.hypergraph.abs.IVertex;
import de.hpi.bpt.hypergraph.abs.Vertex;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;


@Plugin(
        name = "Compute Complexity",
        parameterLabels = { "BPMNDiagram" },
        returnLabels = { "Complexity" },
        returnTypes = { String.class },
        userAccessible = true,
        help = "Compute complexity of a BPMN process model."
)
public class ComplexityCalculator {
    private String bonds;
    private String rigids;
    private Map<String, String> result;
    private BPMNDiagram diagram;

    public ComplexityCalculator() { result = new HashMap<>(); }
    public ComplexityCalculator(BPMNDiagram diagram) {
        result = new HashMap<>();
        this.diagram = diagram;
    }

    public Map<String, String> computeComplexity(  BPMNDiagram model, boolean size, boolean cfc, boolean acd,
                                                   boolean mcd, boolean cnc, boolean density,
                                                   boolean structuredness, boolean separability,
                                                   boolean duplicates) {
        diagram = model;

        if(size) result.put("Size", computeSize());

        if(cfc) result.put("CFC", computeCFC());

        if(acd) result.put("ACD", computeACD());

        if(mcd) result.put("MCD", computeMCD());

        if(cnc) result.put("CNC", computeCNC());

        if(density) result.put("Density",  computeDensity());

        if(structuredness) {
            result.put("Structuredness",  computeStructuredness());
            result.put("Bonds", this.bonds);
            result.put("Rigids", this.rigids);
        }

        if(separability)  result.put("Separability", computeSeparability());

        if(duplicates) result.put("Duplicates", computeDuplicates());

        return result;
    }

    @UITopiaVariant(
            affiliation = "Queensland University of Technology",
            author = "Adriano Augusto",
            email = "a.augusto@qut.edu.au"
    )
    @PluginVariant(variantLabel = "Compute Complexity", requiredParameterLabels = {0})
    public static String computeComplexity(PluginContext context, BPMNDiagram diagram) {
        String measures = "";

        ComplexityCalculator cc = new ComplexityCalculator(diagram);

        measures += ("Size: \t\t" + cc.computeSize() + "\r\n\n");
        measures += ("CFC: \t\t" + cc.computeCFC() + "\r\n\n");
        measures += ("ACD: \t\t" + cc.computeACD() + "\r\n\n");
        measures += ("MCD: \t\t" + cc.computeMCD() + "\r\n\n");
        measures += ("CNC: \t\t" + cc.computeCNC() + "\r\n\n");
        measures += ("Density: \t" + cc.computeDensity() + "\r\n\n");
        measures += ("Structuredness: \t" + cc.computeStructuredness() + "\r\n\n");
        measures += ("Separability: \t" + cc.computeSeparability() + "\r\n\n");
        measures += ("Duplicates: \t" + cc.computeDuplicates() + "\r\n\n");
        measures += ("Bonds: \t" + cc.bonds + "\r\n\n");
        measures += ("Rigids: \t" + cc.rigids + "\r\n\n");

        return measures;
    }

    public String computeSize() {
        int size = 0;
        if(diagram == null) return "n/a";

        size += diagram.getGateways().size();
        size += diagram.getActivities().size();
        size += diagram.getCallActivities().size();
        size += diagram.getSubProcesses().size();
        size += diagram.getEvents().size();

        return Integer.toString(size);
    }

    public String computeCFC() {
        int cfc = 0;
        int outgoingEdges;
        if(diagram == null) return "n/a";

        for(Gateway g : diagram.getGateways()) {
            if( (outgoingEdges = diagram.getOutEdges(g).size()) > 1 )
                switch( g.getGatewayType() ) {
                    case DATABASED:
                    case EVENTBASED:
                        //case XOR
                        cfc += outgoingEdges;
                        break;
                    case INCLUSIVE:
                    case COMPLEX:
                        //case OR
                        cfc += (Math.pow(2.0, outgoingEdges) - 1);
                        break;
                    case PARALLEL:
                        //case AND
                        cfc += 1;
                        break;
                }
        }

        return Integer.toString(cfc);
    }

    public String computeACD() {
        double acd = 0;
        if(diagram == null) return "n/a";

        for(Gateway g : diagram.getGateways()) acd += (diagram.getOutEdges(g).size() + diagram.getInEdges(g).size());

        if( acd == 0 ) return "0";    //this means no gateways!

        acd = acd / (double)diagram.getGateways().size();
        return String.format("%.3f", acd);
    }

    public String computeMCD() {
        int mcd = 0;
        int tmp;
        if(diagram == null) return "n/a";

        for(Gateway g : diagram.getGateways())
            if( mcd < (tmp = (diagram.getOutEdges(g).size() + diagram.getInEdges(g).size())) ) mcd = tmp;

        return Integer.toString(mcd);
    }

    public String computeCNC() {
        int nodes = 0;
        double cnc;
        if(diagram == null) return "n/a";

        nodes += diagram.getGateways().size();
        nodes += diagram.getActivities().size();
        nodes += diagram.getCallActivities().size();
        nodes += diagram.getSubProcesses().size();
        nodes += diagram.getEvents().size();

        if(nodes == 0) return "n/a";

        cnc = (double)diagram.getFlows().size() / (double)nodes;
        return String.format( "%.3f", cnc);
    }

    public String computeDensity() {
        int nodes = 0;
        double density;
        if(diagram == null) return "n/a";

        nodes += diagram.getGateways().size();
        nodes += diagram.getActivities().size();
        nodes += diagram.getCallActivities().size();
        nodes += diagram.getSubProcesses().size();
        for(Event e : diagram.getEvents())
            if((e.getEventType() != Event.EventType.END) && (e.getEventType() != Event.EventType.START)) nodes++;

        if(nodes == 1 || nodes == 0) return "n/a";

        density = (double) diagram.getFlows().size() / (double) (nodes * (nodes - 1));
        return String.format( "%.3f", density);
    }

    public String computeStructuredness() {
        double structuredness;
        double nodes = 0;
        HashSet<RPSTNode> rigids = new HashSet<>();
        HashSet<RPSTNode> bonds = new HashSet<>();

        this.rigids = "n/a";
        this.bonds = "n/a";

        if(diagram == null) return "n/a";

        try {
            HashMap<BPMNNode, Vertex> mapping = new HashMap<BPMNNode, Vertex>();
            HashMap<String, Gateway> gates = new HashMap<String, Gateway>();
            HashSet<String> removed = new HashSet<String>();

            IDirectedGraph<DirectedEdge, Vertex> graph = new DirectedGraph();
            Vertex src;
            Vertex tgt;

            for (Flow f : diagram.getFlows((Swimlane) null)) {
                if (!mapping.containsKey(f.getSource())) {
                    src = new Vertex(f.getSource().getId().toString());
                    if (f.getSource() instanceof Gateway) gates.put(f.getSource().getId().toString(), (Gateway) f.getSource());
                    mapping.put(f.getSource(), src);
                } else src = mapping.get(f.getSource());

                if (!mapping.containsKey(f.getTarget())) {
                    tgt = new Vertex(f.getTarget().getId().toString());
                    if (f.getTarget() instanceof Gateway) gates.put(f.getTarget().getId().toString(), (Gateway) f.getTarget());
                    mapping.put(f.getTarget(), tgt);
                } else tgt = mapping.get(f.getTarget());

                graph.addEdge(src, tgt);
            }

            RPST rpst = new RPST(graph);

            RPSTNode root = rpst.getRoot();
            LinkedList<RPSTNode> toAnalize = new LinkedList<RPSTNode>();
            toAnalize.add(root);

            boolean count = true;

            HashSet<Vertex> rChildren = new HashSet<>();
            HashSet<Vertex> bChildren = new HashSet<>();

            while (toAnalize.size() != 0) {

                root = toAnalize.pollFirst();

                if (!count && (root.getType() == TCType.P) && (rpst.getParent(root).getType() == TCType.B)) {
                    try {
                        Gateway entry = gates.get(rpst.getParent(root).getEntry().getName());
                        Gateway exit = gates.get(rpst.getParent(root).getExit().getName());
                        count = ((entry != null) && (exit != null) && (entry.getGatewayType() == exit.getGatewayType()));
                    } catch (ClassCastException cce) {
                        count = false;
                    }
                }

                for (RPSTNode n : new HashSet<RPSTNode>(rpst.getChildren(root))) {
                    switch (n.getType()) {
                        case R:
                            toAnalize.add(n);
                            rigids.add(n);
                            break;
                        case T:
                            if ((root != rpst.getRoot()) && (root.getType() == TCType.P) && (rpst.getParent(root).getType() == TCType.R)) {
                                rChildren.add((Vertex) n.getEntry());
                                rChildren.add((Vertex) n.getExit());
                            }
                            if ((root != rpst.getRoot()) && (root.getType() == TCType.P) && (rpst.getParent(root).getType() == TCType.B)) {
                                bChildren.add((Vertex) n.getEntry());
                                bChildren.add((Vertex) n.getExit());
                            }
                            if (count) {
                                src = (Vertex) n.getEntry();
                                tgt = (Vertex) n.getExit();
                                if (!gates.containsKey(src.getName())) removed.add(src.getName());
                                if (!gates.containsKey(tgt.getName())) removed.add(tgt.getName());
                            }
                            break;
                        case P:
                            toAnalize.add(n);
                            break;
                        case B:
                            removed.add(n.getEntry().getName());
                            removed.add(n.getExit().getName());
                            toAnalize.add(n);
                            bonds.add(n);
                            break;
                        default:
                    }
                }

                count = false;
                toAnalize.remove(root);
            }

            nodes += diagram.getGateways((Swimlane) null).size();
            nodes += diagram.getActivities((Swimlane) null).size();
            nodes += diagram.getCallActivities((Swimlane) null).size();
            nodes += diagram.getSubProcesses((Swimlane) null).size();
            nodes += diagram.getEvents((Swimlane) null).size();

            structuredness = 1 - ((nodes - removed.size()) / nodes);

        } catch (Exception e) {
            return "n/a";
        }

        this.rigids = Integer.toString(rigids.size());
        this.bonds = Integer.toString(bonds.size());

        return String.format( "%.3f", structuredness);
    }

    public String computeSeparability() {
        double separability;
        double nodes = 0;
        if(diagram == null) return "n/a";

        try {
            HashMap<BPMNNode, Vertex> mapping = new HashMap<BPMNNode, Vertex>();
            IDirectedGraph<DirectedEdge, Vertex> graph = new DirectedGraph();
            Vertex src;
            Vertex tgt;

            for (Flow f : diagram.getFlows((Swimlane) null)) {
                if (!mapping.containsKey(f.getSource())) {
                    src = new Vertex(f.getSource().getLabel());
                    mapping.put(f.getSource(), src);
                } else src = mapping.get(f.getSource());

                if (!mapping.containsKey(f.getTarget())) {
                    tgt = new Vertex(f.getTarget().getLabel());
                    mapping.put(f.getTarget(), tgt);
                } else tgt = mapping.get(f.getTarget());

                graph.addEdge(src, tgt);
            }

            RPST rpst = new RPST(graph);
            RPSTNode root = rpst.getRoot();
            HashSet<IVertex> articulationPoints = new HashSet<IVertex>();

            for (RPSTNode n : new HashSet<RPSTNode>(rpst.getChildren(root))) {
                switch (n.getType()) {
                    case R:
                        articulationPoints.add(n.getEntry());
                        articulationPoints.add(n.getExit());
                        break;
                    case T:
                        articulationPoints.add(n.getEntry());
                        articulationPoints.add(n.getExit());
                        break;
                    case P:
                        articulationPoints.add(n.getEntry());
                        articulationPoints.add(n.getExit());
                        break;
                    case B:
                        articulationPoints.add(n.getEntry());
                        articulationPoints.add(n.getExit());
                        break;
                    default:
                        //context.log("found something weird.");
                }
            }

            nodes += diagram.getGateways((Swimlane) null).size();
            nodes += diagram.getActivities((Swimlane) null).size();
            nodes += diagram.getCallActivities((Swimlane) null).size();
            nodes += diagram.getSubProcesses((Swimlane) null).size();
            nodes += diagram.getEvents((Swimlane) null).size();

            separability = (articulationPoints.size() - 2) / (nodes - 2);
        } catch( Exception e ) {
            return "n/a";
        }

        return String.format( "%.3f", separability);
    }

    public String computeDuplicates() {
        int duplicates = 0;
        HashSet<String> nodes = new HashSet<>();
        String label;
        if(diagram == null) return "n/a";

        for( Activity a : diagram.getActivities() ) {
            label = a.getLabel();
            if( nodes.contains(label) && !label.isEmpty() ) duplicates++;
            else nodes.add(label);
        }

        return Integer.toString(duplicates);
    }
}
