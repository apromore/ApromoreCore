/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.service.impl;

import de.hpi.bpt.graph.DirectedEdge;
import de.hpi.bpt.graph.DirectedGraph;
import de.hpi.bpt.graph.abs.IDirectedGraph;
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


/**
 * Created by Adriano on 08/01/2016.
 */

@Service
public class MeasurementsServiceImpl implements MeasurementsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MeasurementsServiceImpl.class);

    private BPMNDiagram diagram;
    private BPMNDiagramImporter diagramImporter;
    private JSONObject result;

    private Integer nBond = 0;
    private Integer nRigid = 0;
    private Double avgBNodes = 0.0;
    private Double avgRNodes = 0.0;

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
            result.put("nBond", nBond);
            result.put("nRigid", nRigid);
            result.put("avgBNodes", avgBNodes);
            result.put("avgRNodes", avgRNodes);
            result.put("separability", computeSeparability());
            result.put("CFC", computeCFC());
            result.put("CNC", computeCNC());
            result.put("ACD", computeACD());
            result.put("MCD", computeMCD());

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
        LOGGER.info("computing structuredness...");

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


            if( !count && (root.getType() == TCType.P) && (rpst.getParent(root).getType() == TCType.B) ) {
                try {
                    Gateway entry = gates.get(rpst.getParent(root).getEntry().getName());
                    Gateway exit = gates.get(rpst.getParent(root).getExit().getName());
                    count = ((entry != null) && (exit != null) && (entry.getGatewayType() == exit.getGatewayType()));
                    //LOGGER.info("Counter: " + count);
                } catch(ClassCastException cce) {
                    count = false;
                    LOGGER.info("Error, found entry or exit point different than a gateway.");
                }
            }

            for( RPSTNode n : new HashSet<RPSTNode>(rpst.getChildren(root)) ) {
                switch(n.getType()) {
                    case R:
                        //LOGGER.info("found a: RIGID with: " +  n.getFragment().getVertices().size() + " fragment nodes.");
                        toAnalize.add(n);
                        rigids.add(n);
                        break;
                    case T:
                        //LOGGER.info("found a: TRIVIAL with: " +  n.getFragment().getVertices().size() + " fragment nodes.");
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
                        //LOGGER.info("found a: POLYGON with: " +  n.getFragment().getVertices().size() + " fragment nodes.");
                        toAnalize.add(n);
                        break;
                    case B:
                        //LOGGER.info("found a: BOND with: " +  n.getFragment().getVertices().size() + " fragment nodes.");
                        removed.add(n.getEntry().getName());
                        removed.add(n.getExit().getName());
                        toAnalize.add(n);
                        bonds.add(n);
                        break;
                    default:
                        LOGGER.info("found something weird.");
                }
            }

            count = false;
            toAnalize.remove(root);
        }

        LOGGER.info("Removable nodes: " + removed.size());

        nodes += diagram.getGateways().size();
        nodes += diagram.getActivities().size();
        nodes += diagram.getCallActivities().size();
        nodes += diagram.getSubProcesses().size();
        nodes += diagram.getEvents().size();

        structuredness = 1 - ((nodes-removed.size())/nodes);

        nBond = bonds.size();
        nRigid = rigids.size();
        avgBNodes = ((double)bChildren.size())/nBond;
        avgRNodes = ((double)rChildren.size())/nRigid;

        LOGGER.info(" done!");
        return structuredness;
    }

    
    public double computeSeparability() {
        double separability;
        double nodes = 0;
        if(diagram == null) return -1;
        LOGGER.info("computing separability...");

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
                    LOGGER.info("found something weird.");
            }
        }

        nodes += diagram.getGateways().size();
        nodes += diagram.getActivities().size();
        nodes += diagram.getCallActivities().size();
        nodes += diagram.getSubProcesses().size();
        nodes += diagram.getEvents().size();

        LOGGER.info("Articulation points: " + (articulationPoints.size()-2));
        separability = (articulationPoints.size()-2) / (nodes-2);

        LOGGER.info(" done!");
        return separability;
    }

}
