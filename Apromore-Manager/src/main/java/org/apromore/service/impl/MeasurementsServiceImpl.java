package org.apromore.service.impl;

import de.hpi.bpt.graph.DirectedEdge;
import de.hpi.bpt.graph.DirectedGraph;
import de.hpi.bpt.graph.abs.IDirectedGraph;
import de.hpi.bpt.graph.abs.IGraph;
import de.hpi.bpt.graph.algo.GraphAlgorithms;
import de.hpi.bpt.graph.algo.rpst.RPST;
import de.hpi.bpt.graph.algo.rpst.RPSTNode;
import de.hpi.bpt.graph.algo.tctree.TCType;
import de.hpi.bpt.hypergraph.abs.IVertex;
import de.hpi.bpt.hypergraph.abs.Vertex;
import org.apromore.service.BPMNDiagramImporter;
import org.apromore.service.MeasurementsService;
import org.json.JSONObject;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;


/**
 * Created by Adriano on 08/01/2016.
 */

@Service
public class MeasurementsServiceImpl implements MeasurementsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MeasurementsServiceImpl.class);

    private BPMNDiagram diagram;
    private BPMNDiagramImporter diagramImporter;
    private JSONObject result;


    public MeasurementsServiceImpl() {
        diagram = null;
        diagramImporter = new BPMNDiagramImporterImpl();
    }

    public MeasurementsServiceImpl(String process) {
        diagramImporter = new BPMNDiagramImporterImpl();
        try {
            diagram = diagramImporter.importBPMNDiagram(process);
        } catch(Exception e) {
            diagram = null;
        }
    }

    public boolean setProcess(String process) {
        try {
            diagram = diagramImporter.importBPMNDiagram(process);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    @Override
    public String computeSimplicity() {
        if( diagram == null ) return null;
        result = new JSONObject();

        try {
            result.put("size", computeSize());
            result.put("CFC", computeCFC());
            result.put("CNC", computeCNC());
            result.put("ACD", computeACD());

            return result.toString();
        } catch(Exception e) {
            return null;
        }
    }

    @Override
    public String computeSimplicity(String process) {
        result = new JSONObject();

        try {
            diagram = diagramImporter.importBPMNDiagram(process);

            result.put("size", computeSize());
            result.put("density", computeDensity());
            result.put("structuredness", computeStructuredness());
            result.put("CFC", computeCFC());
            result.put("CNC", computeCNC());
            result.put("ACD", computeACD());
            result.put("MCD", computeMCD());
            result.put("separability", computeSeparability());

            return result.toString();
        } catch(Exception e) {
            return null;
        }
    }

    public int computeSize() {
        int size = 0;
        if(diagram == null) return -1;

        size += diagram.getGateways().size();
        size += diagram.getActivities().size();
        size += diagram.getCallActivities().size();
        size += diagram.getSubProcesses().size();
        size += diagram.getEvents().size();

        return size;
    }

    public int computeCFC() {
        int cfc = 0;
        int outgoingEdges;
        if(diagram == null) return -1;

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

        return cfc;
    }

    public double computeACD() {
        double acd = 0;
        if(diagram == null) return -1;

        for(Gateway g : diagram.getGateways()) acd += (diagram.getOutEdges(g).size() + diagram.getInEdges(g).size());

        if( acd == 0 ) return 0;    //this means no gateways!

        acd = acd / (double)diagram.getGateways().size();
        return acd;
    }

    public int computeMCD() {
        int mcd = 0;
        int tmp;
        if(diagram == null) return -1;

        for(Gateway g : diagram.getGateways())
            if( mcd < (tmp = (diagram.getOutEdges(g).size() + diagram.getInEdges(g).size())) ) mcd = tmp;

        return mcd;
    }

    public double computeCNC() {
        int nodes = 0;
        double cnc;
        if(diagram == null) return -1;

        nodes += diagram.getGateways().size();
        nodes += diagram.getActivities().size();
        nodes += diagram.getCallActivities().size();
        nodes += diagram.getSubProcesses().size();
        nodes += diagram.getEvents().size();

        if(nodes == 0) return 0;

        cnc = (double)diagram.getFlows().size() / (double)nodes;
        return cnc;
    }

    public double computeDensity() {
        int nodes = 0;
        double density;
        if(diagram == null) return -1;

        nodes += diagram.getGateways().size();
        nodes += diagram.getActivities().size();
        nodes += diagram.getCallActivities().size();
        nodes += diagram.getSubProcesses().size();
        for(Event e : diagram.getEvents())
            if((e.getEventType() != Event.EventType.END) && (e.getEventType() != Event.EventType.START)) nodes++;

        if(nodes == 1 || nodes == 0) return 0;

        density = (double) diagram.getFlows().size() / (double) (nodes * (nodes - 1));
        return density;
    }

    public double computeStructuredness() {
        double structuredness;
        double nodes = 0;

        if(diagram == null) return -1;

        HashMap<BPMNNode, Vertex> mapping = new HashMap<>();
        HashSet<String> gates = new HashSet<>();
        HashSet<String> removed = new HashSet<>();

        IDirectedGraph<DirectedEdge, Vertex> graph = new DirectedGraph();
        Vertex src;
        Vertex tgt;

        for( Flow f : diagram.getFlows() ) {
            if( !mapping.containsKey(f.getSource()) ) {
                src = new Vertex(f.getSource().getId().toString());
                if( f.getSource() instanceof Gateway ) gates.add(f.getSource().getId().toString());
                mapping.put(f.getSource(), src);
            } else src = mapping.get(f.getSource());

            if( !mapping.containsKey(f.getTarget()) ) {
                tgt = new Vertex(f.getTarget().getId().toString());
                if( f.getTarget() instanceof Gateway ) gates.add(f.getTarget().getId().toString());
                mapping.put(f.getTarget(), tgt);
            } else tgt = mapping.get(f.getTarget());

            graph.addEdge(src, tgt);
        }

        RPST rpst = new RPST(graph);

        RPSTNode root = rpst.getRoot();
        LinkedList<RPSTNode> toAnalize = new LinkedList<>();
        toAnalize.add(root);

        boolean count = true;

        while( toAnalize.size() != 0 ) {

            root = toAnalize.pollFirst();
            //LOGGER.info("Iteration on a: " + root.getType());

            if( !count && (root.getType() == TCType.P) && (rpst.getParent(root).getType() == TCType.B) ) {
                //LOGGER.info("counter enabled.");
                count = true;
            }

            for( RPSTNode n : new HashSet<RPSTNode>(rpst.getChildren(root)) ) {
                switch(n.getType()) {
                    case R:
                        //LOGGER.info("found a: RIGID with: " +  n.getFragment().getVertices().size() + " fragment nodes.");
                        toAnalize.add(n);
                        break;
                    case T:
                        //LOGGER.info("found a: TRIVIAL with: " +  n.getFragment().getVertices().size() + " fragment nodes.");
                        if( count ) {
                            src = (Vertex) n.getEntry();
                            tgt = (Vertex) n.getExit();
                            if( !gates.contains(src.getName())) removed.add(src.getName());
                            if( !gates.contains(tgt.getName()) ) removed.add(tgt.getName());
                        }
                        break;
                    case P:
                        //LOGGER.info("found a: POLYGON with: " +  n.getFragment().getVertices().size() + " fragment nodes.");
                        toAnalize.add(n);
                        break;
                    case B:
                        //LOGGER.info("found a: BOND with: " +  n.getFragment().getVertices().size() + " fragment nodes.");
                        toAnalize.add(n);
                        break;
                    default:
                        LOGGER.info("found something weird.");
                }
            }

            count = false;
            toAnalize.remove(root);
        }

        LOGGER.info("*removable: " + removed.size());

        nodes += diagram.getGateways().size();
        nodes += diagram.getActivities().size();
        nodes += diagram.getCallActivities().size();
        nodes += diagram.getSubProcesses().size();
        nodes += diagram.getEvents().size();

        structuredness = 1 - ((nodes-removed.size())/nodes);

        return structuredness;
    }

    public double computeSeparability() {
        double separability;
        double nodes = 0;
        if(diagram == null) return -1;

        HashMap<BPMNNode, Vertex> mapping = new HashMap<>();
        IDirectedGraph<DirectedEdge, Vertex> graph = new DirectedGraph();
        Vertex src;
        Vertex tgt;

        for( Flow f : diagram.getFlows() ) {
            if( !mapping.containsKey(f.getSource()) ) {
                src = new Vertex(f.getSource().getLabel());
                //if( f.getSource() instanceof Event && (((Event) f.getSource()).getEventType()==Event.EventType.START) )  root = src;
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
        HashSet<IVertex> articulationPoints = new HashSet<>();

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
                    LOGGER.info("found something weird.");
            }
        }

        nodes += diagram.getGateways().size();
        nodes += diagram.getActivities().size();
        nodes += diagram.getCallActivities().size();
        nodes += diagram.getSubProcesses().size();
        nodes += diagram.getEvents().size();

        LOGGER.info("Articulation points: " + (articulationPoints.size()-2));
        separability = (articulationPoints.size()-2) / nodes;

        return separability;
    }

    public Set<Vertex> getArticulationPoints(Vertex root, IGraph<DirectedEdge, Vertex> graph) {
        HashSet<Vertex> articulationPoints = new HashSet<>();
        HashMap<Vertex, Vertex> visited = new HashMap<>();
        HashMap<Vertex, Integer> time = new HashMap<>();
        HashMap<Vertex, Integer> lowTime = new HashMap<>();

        visit(root, graph, visited, time, lowTime, articulationPoints, null);

        LOGGER.info("Algorithm:");
        for( Vertex v : time.keySet() ) LOGGER.info("[" + visited.get(v) + "]" + v.getName() + " : " + time.get(v) + "/" + lowTime.get(v));

        LOGGER.info("Articulation points: " + articulationPoints.size());
        for( Vertex v : articulationPoints ) LOGGER.info(v.getName());

        return articulationPoints;
    }

    private void visit( Vertex root, IGraph<DirectedEdge, Vertex> graph,
                           HashMap<Vertex, Vertex> visited,
                           HashMap<Vertex, Integer> time, HashMap<Vertex, Integer> lowTime,
                           HashSet<Vertex> articulationPoints, Vertex parent ) {

        HashSet<Vertex> backVertices = new HashSet<>();
        boolean ap = (graph.getAdjacent(root).size() > 1);

        visited.put(root, parent);
        time.put(root, visited.size());
        lowTime.put(root, visited.size());

        for( Vertex v : graph.getAdjacent(root) ) {
            if( !visited.containsKey(v) ) visit(v, graph, visited, time, lowTime, articulationPoints, root);
            backVertices.add(v);
        }

        for( Vertex v : backVertices )
            if( (v != parent) && (time.get(root) > lowTime.get(v)) ) {
                ap = false;
                lowTime.put(root, lowTime.get(v));
            }

        if( ap ) articulationPoints.add(root);
    }

}
