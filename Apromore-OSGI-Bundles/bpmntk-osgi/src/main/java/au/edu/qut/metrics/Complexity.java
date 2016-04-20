package au.edu.qut.metrics;

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
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class Complexity {

    @Plugin(
            name = "Compute Complexity",
            parameterLabels = { "BPMNDiagram" },
            returnLabels = { "Complexity" },
            returnTypes = { String.class },
            userAccessible = true,
            help = "Compute complexity of a BPMN process model."
    )
    @UITopiaVariant(
            affiliation = "Queensland University of Technology",
            author = "Adriano Augusto",
            email = "a.augusto@qut.edu.au"
    )
    public static String computeComplexity(PluginContext context, BPMNDiagram diagram) {
        String measures = "";

        measures += ("Size: \t\t" + computeSize(diagram, context) + "\r\n\n");
        measures += ("CFC: \t\t" + computeCFC(diagram, context) + "\r\n\n");

        try {
            measures += ("Structuredness: " + computeStructuredness(diagram, context) + "\r\n\n");
        } catch(Exception e) {
            context.log("Something went wrong computing structuredness :S");
        }

        measures += ("Duplicates: " + computeDuplicates(diagram, context));

/*
        try {
            measures += ("Separability: \t" + computeSeparability(diagram, context) + "\r\n\n");
        } catch(Exception e) {
            context.log("Something went wrong computing separability :S");
            context.log(e);
        }

        try {
            measures += ("MCD: \t\t" + computeMCD(diagram, context) + "\r\n\n");
            measures += ("ACD: \t\t" + computeACD(diagram, context) + "\r\n\n");
            measures += ("CNC: \t\t" + computeCNC(diagram, context) + "\r\n\n");
            measures += ("Density: \t" + computeDensity(diagram, context) + "\r\n\n");
        } catch(Exception e) {
            context.log("this should not happen :O");
            context.log(e);
        }
*/
        return measures;
    }

    private static int computeDuplicates(BPMNDiagram diagram, PluginContext context) {
        int duplicates = 0;
        HashSet<String> nodes = new HashSet<>();
        String label;

        for( Activity a : diagram.getActivities() ) {
            label = a.getLabel();
            if( nodes.contains(label) && !label.isEmpty() ) duplicates++;
            else nodes.add(label);
        }

        return duplicates;
    }

    private static int computeSize(BPMNDiagram diagram, PluginContext context) {
        int size = 0;
        if(diagram == null) return -1;
        //context.log("computing size...");

        size += diagram.getGateways().size();
        size += diagram.getActivities().size();
        size += diagram.getCallActivities().size();
        size += diagram.getSubProcesses().size();
        size += diagram.getEvents().size();

        context.log("size: " + size);
        return size;
    }

    private static int computeCFC(BPMNDiagram diagram, PluginContext context) {
        int cfc = 0;
        int outgoingEdges;
        if(diagram == null) return -1;
        //context.log("computing CFC...");

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

        context.log("cfc: " + cfc);
        return cfc;
    }

    private static double computeACD(BPMNDiagram diagram, PluginContext context) {
        double acd = 0;
        if(diagram == null) return -1;
        //context.log("computing ACD...");

        for(Gateway g : diagram.getGateways()) acd += (diagram.getOutEdges(g).size() + diagram.getInEdges(g).size());

        if( acd == 0 ) return 0;    //this means no gateways!

        acd = acd / (double)diagram.getGateways().size();
        //context.log(" done!");
        return acd;
    }

    private static int computeMCD(BPMNDiagram diagram, PluginContext context) {
        int mcd = 0;
        int tmp;
        if(diagram == null) return -1;
        //context.log("computing MCD...");

        for(Gateway g : diagram.getGateways())
            if( mcd < (tmp = (diagram.getOutEdges(g).size() + diagram.getInEdges(g).size())) ) mcd = tmp;

        //context.log("done: " + mcd);
        return mcd;
    }

    private static double computeCNC(BPMNDiagram diagram, PluginContext context) {
        int nodes = 0;
        double cnc;
        if(diagram == null) return -1;
        //context.log("computing CNC...");

        nodes += diagram.getGateways().size();
        nodes += diagram.getActivities().size();
        nodes += diagram.getCallActivities().size();
        nodes += diagram.getSubProcesses().size();
        nodes += diagram.getEvents().size();

        if(nodes == 0) return 0;

        cnc = (double)diagram.getFlows().size() / (double)nodes;
        //context.log(" done!");
        return cnc;
    }

    private static double computeDensity(BPMNDiagram diagram, PluginContext context) {
        int nodes = 0;
        double density;
        if(diagram == null) return -1;
        //context.log("computing density...");

        nodes += diagram.getGateways().size();
        nodes += diagram.getActivities().size();
        nodes += diagram.getCallActivities().size();
        nodes += diagram.getSubProcesses().size();
        for(Event e : diagram.getEvents())
            if((e.getEventType() != Event.EventType.END) && (e.getEventType() != Event.EventType.START)) nodes++;

        if(nodes == 1 || nodes == 0) return 0;

        density = (double) diagram.getFlows().size() / (double) (nodes * (nodes - 1));
        //context.log("done: " + density);
        return density;
    }

    private static String computeStructuredness(BPMNDiagram diagram, PluginContext context) {
        String structuredness = new String();
        double nodes = 0;

        if(diagram == null) return "null";
        //context.log("computing structuredness...");

        HashMap<BPMNNode, Vertex> mapping = new HashMap<BPMNNode, Vertex>();
        HashMap<String, Gateway> gates = new HashMap<String, Gateway>();
        HashSet<String> removed = new HashSet<String>();

        IDirectedGraph<DirectedEdge, Vertex> graph = new DirectedGraph();
        Vertex src;
        Vertex tgt;

        for( Flow f : diagram.getFlows() ) {
            if( !mapping.containsKey(f.getSource()) ) {
                src = new Vertex(f.getSource().getId().toString());
                if( f.getSource() instanceof Gateway ) gates.put(f.getSource().getId().toString(), (Gateway) f.getSource());
                mapping.put(f.getSource(), src);
            } else src = mapping.get(f.getSource());

            if( !mapping.containsKey(f.getTarget()) ) {
                tgt = new Vertex(f.getTarget().getId().toString());
                if( f.getTarget() instanceof Gateway ) gates.put(f.getTarget().getId().toString(), (Gateway) f.getTarget());
                mapping.put(f.getTarget(), tgt);
            } else tgt = mapping.get(f.getTarget());

            graph.addEdge(src, tgt);
        }

        RPST rpst = new RPST(graph);

        RPSTNode root = rpst.getRoot();
        LinkedList<RPSTNode> toAnalize = new LinkedList<RPSTNode>();
        toAnalize.add(root);

        boolean count = true;

        HashSet<RPSTNode> rigids = new HashSet<>();
        HashSet<RPSTNode> bonds = new HashSet<>();
        HashSet<Vertex> rChildren = new HashSet<>();
        HashSet<Vertex> bChildren = new HashSet<>();

        while( toAnalize.size() != 0 ) {

            root = toAnalize.pollFirst();
            //context.log("Iteration on a: " + root.getType());

            if( !count && (root.getType() == TCType.P) && (rpst.getParent(root).getType() == TCType.B) ) {
                //context.log("counter enabled.");
                try {
                    Gateway entry = gates.get(rpst.getParent(root).getEntry().getName());
                    Gateway exit = gates.get(rpst.getParent(root).getExit().getName());
                    count = ((entry != null) && (exit != null) && (entry.getGatewayType() == exit.getGatewayType()));
                    //context.log("Counter: " + count);
                } catch(ClassCastException cce) {
                    count = false;
                    context.log("Error, found entry or exit point different than a gateway.");
                }
            }

            for( RPSTNode n : new HashSet<RPSTNode>(rpst.getChildren(root)) ) {
                switch(n.getType()) {
                    case R:
                        //context.log("found a: RIGID with: " +  n.getFragment().getVertices().size() + " fragment nodes.");
                        toAnalize.add(n);
                        rigids.add(n);
                        break;
                    case T:
                        //context.log("found a: TRIVIAL with: " +  n.getFragment().getVertices().size() + " fragment nodes.");
                        if( (root != rpst.getRoot()) && (root.getType() == TCType.P) && (rpst.getParent(root).getType() == TCType.R)) {
                            rChildren.add((Vertex) n.getEntry());
                            rChildren.add((Vertex) n.getExit());
                        }
                        if( (root != rpst.getRoot()) && (root.getType() == TCType.P) && (rpst.getParent(root).getType() == TCType.B)) {
                            bChildren.add((Vertex) n.getEntry());
                            bChildren.add((Vertex) n.getExit());
                        }
                        if( count ) {
                            src = (Vertex) n.getEntry();
                            tgt = (Vertex) n.getExit();
                            if (!gates.containsKey(src.getName())) removed.add(src.getName());
                            if (!gates.containsKey(tgt.getName())) removed.add(tgt.getName());
                        }
                        break;
                    case P:
                        //context.log("found a: POLYGON with: " +  n.getFragment().getVertices().size() + " fragment nodes.");
                        toAnalize.add(n);
                        break;
                    case B:
                        //context.log("found a: BOND with: " +  n.getFragment().getVertices().size() + " fragment nodes.");
                        removed.add(n.getEntry().getName());
                        removed.add(n.getExit().getName());
                        toAnalize.add(n);
                        bonds.add(n);
                        break;
                    default:
                        context.log("found something weird.");
                        return "null";
                }
            }

            count = false;
            toAnalize.remove(root);
        }

        //context.log("Removable nodes: " + removed.size());

        nodes += diagram.getGateways().size();
        nodes += diagram.getActivities().size();
        nodes += diagram.getCallActivities().size();
        nodes += diagram.getSubProcesses().size();
        nodes += diagram.getEvents().size();

        structuredness += 1 - ((nodes-removed.size())/nodes);
        //structuredness += "\r\n\nBonds: " + bonds.size();
        //structuredness += "\r\n\nBond's Nodes: " + bChildren.size();
        //structuredness += "\r\n\nRigids: " + rigids.size();
        //structuredness += "\r\n\nRigid's Nodes: " + rChildren.size();

        context.log("structuredness: " + structuredness);
        return structuredness;
    }

    private static double computeSeparability(BPMNDiagram diagram, PluginContext context) {
        double separability;
        double nodes = 0;
        if(diagram == null) return -1;
        context.log("computing separability...");

        HashMap<BPMNNode, Vertex> mapping = new HashMap<BPMNNode, Vertex>();
        IDirectedGraph<DirectedEdge, Vertex> graph = new DirectedGraph();
        Vertex src;
        Vertex tgt;

        for( Flow f : diagram.getFlows() ) {
            if( !mapping.containsKey(f.getSource()) ) {
                src = new Vertex(f.getSource().getLabel());
                mapping.put(f.getSource(), src);
            } else src = mapping.get(f.getSource());

            if( !mapping.containsKey(f.getTarget()) ) {
                tgt = new Vertex(f.getTarget().getLabel());
                mapping.put(f.getTarget(), tgt);
            } else tgt = mapping.get(f.getTarget());

            graph.addEdge(src, tgt);
        }

        RPST rpst = new RPST(graph);
        RPSTNode root = rpst.getRoot();
        HashSet<IVertex> articulationPoints = new HashSet<IVertex>();

        for( RPSTNode n : new HashSet<RPSTNode>(rpst.getChildren(root)) ) {
            switch(n.getType()) {
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

        nodes += diagram.getGateways().size();
        nodes += diagram.getActivities().size();
        nodes += diagram.getCallActivities().size();
        nodes += diagram.getSubProcesses().size();
        nodes += diagram.getEvents().size();

        //context.log("Articulation points: " + (articulationPoints.size()-2));
        separability = (articulationPoints.size()-2) / (nodes-2);

        context.log("done: " + separability );
        return separability;
    }
}
