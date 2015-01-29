/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.toolbox.similaritySearch.algorithms;

import org.apromore.toolbox.similaritySearch.common.IdGeneratorHelper;
import org.apromore.toolbox.similaritySearch.common.VertexPair;
import org.apromore.toolbox.similaritySearch.common.algos.GraphEditDistanceGreedy;
import org.apromore.toolbox.similaritySearch.common.algos.TwoVertices;
import org.apromore.toolbox.similaritySearch.common.similarity.AssingmentProblem;
import org.apromore.toolbox.similaritySearch.graph.Edge;
import org.apromore.toolbox.similaritySearch.graph.Graph;
import org.apromore.toolbox.similaritySearch.graph.Vertex;
import org.apromore.toolbox.similaritySearch.graph.Vertex.GWType;
import org.apromore.toolbox.similaritySearch.graph.Vertex.Type;
import org.apromore.toolbox.similaritySearch.graph.VertexObject;
import org.apromore.toolbox.similaritySearch.graph.VertexObjectRef;
import org.apromore.toolbox.similaritySearch.graph.VertexResource;
import org.apromore.toolbox.similaritySearch.graph.VertexResourceRef;
import org.apromore.toolbox.similaritySearch.planarGraphMathing.PlanarGraphMathing.MappingRegions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;


public class MergeModels {

    private static final Logger LOGGER = LoggerFactory.getLogger(MergeModels.class.getName());

    public static Graph mergeModels(Graph g1, Graph g2, IdGeneratorHelper idGenerator, boolean removeEnt, String algortithm, double... param) {

        HashMap<String, String> objectresourceIDMap = new HashMap<String, String>();

        HashSet<String> labelsg1g2 = new HashSet<String>();
        if(!g1.getGraphLabel().equals("merged")) {
            labelsg1g2.add(g1.getGraphLabel());
        }
        if(!g2.getGraphLabel().equals("merged")) {
            labelsg1g2.add(g2.getGraphLabel());
        }

        Graph merged = new Graph();
        merged.setIdGenerator(idGenerator);
        long startTime = System.currentTimeMillis();

        merged.addVertices(g1.getVertices());
        merged.addEdges(g1.getEdges());
        merged.addVertices(g2.getVertices());
        merged.addEdges(g2.getEdges());

        // add all resources from the first models
        merged.getResources().putAll(g1.getResources());
        // and then look if something represent the same thing
        // do not try to merge objects in one modelass
        mergeResources(g2.getResources().values(), objectresourceIDMap, merged);
        merged.getObjects().putAll(g1.getObjects());
        mergeObjects(g2.getObjects().values(), objectresourceIDMap, merged);

        LinkedList<VertexPair> mapping = new LinkedList<VertexPair>();

        if (algortithm.equals("Greedy")) {
            GraphEditDistanceGreedy gedepc = new GraphEditDistanceGreedy();
            Object weights[] = {"ledcutoff", param[0],
                    "cedcutoff", param[1],
                    "vweight", param[2],
                    "sweight", param[3],
                    "eweight", param[4]};

            gedepc.setWeight(weights);

            for (TwoVertices pair : gedepc.compute(g1, g2)) {
                Vertex v1 = g1.getVertexMap().get(pair.v1);
                Vertex v2 = g2.getVertexMap().get(pair.v2);
                if (v1.getType().equals(v2.getType())) {
                    mapping.add(new VertexPair(v1, v2, pair.weight));
                }
            }
        } else if (algortithm.equals("Hungarian")) {
            mapping = AssingmentProblem.getMappingsVetrexUsingNodeMapping(g1, g2, param[0], param[1]);
        }

        // clean mappings from mappings that conflict
        // TODO uncomment
//		removeNonDominanceMappings(mapping);

        if (removeEnt) {
            g1.fillDominanceRelations();
            g2.fillDominanceRelations();
            removeNonDominanceMappings2(mapping);
        }

        MappingRegions mappingRegions = findMaximumCommonRegions(g1, g2, mapping);

        for (LinkedList<VertexPair> region : mappingRegions.getRegions()) {
            for (VertexPair vp : region) {
                LinkedList<Vertex> nodesToProcess = new LinkedList<Vertex>();
                for (Vertex c : vp.getRight().getChildren()) {
                    // the child is also part of the mapping
                    // remove the edge from the merged modelass
                    if (containsVertex(region, c)) {
                        nodesToProcess.add(c);
                    }
                }
                for (Vertex c : nodesToProcess) {
                    HashSet<String> labels = merged.removeEdge(vp.getRight().getID(), c.getID());

                    vp.getRight().removeChild(c.getID());
                    c.removeParent(vp.getRight().getID());

                    Vertex cLeft = getMappingPair(mapping, c);
                    Edge e = merged.containsEdge(vp.getLeft().getID(), cLeft.getID());
                    if (e != null) {
                        e.addLabels(labels);
                    }
                }
            }
            // add annotations for the labels
            for (VertexPair vp : region) {
                Vertex mappingRight = vp.getRight();
                vp.getLeft().addAnnotations(mappingRight.getAnnotationMap());

                // merge object references
                for (VertexObjectRef o : mappingRight.objectRefs) {
                    boolean mergedO = false;
                    for (VertexObjectRef vo : vp.getLeft().objectRefs) {
                        if ((vo.getObjectID().equals(o.getObjectID()) ||
                                objectresourceIDMap.get(o.getObjectID()) != null &&
                                        objectresourceIDMap.get(o.getObjectID()).equals(vo.getObjectID())) &&
                                o.canMerge(vo)) {
                            vo.addModels(o.getModels());
                            mergedO = true;
                            break;
                        }
                    }
                    if (!mergedO) {
                        vp.getLeft().objectRefs.add(o);
                    }
                }

                // merge resource references
                for (VertexResourceRef o : mappingRight.resourceRefs) {
                    boolean mergedO = false;
                    for (VertexResourceRef vo : vp.getLeft().resourceRefs) {
                        if ((vo.getResourceID().equals(o.getResourceID()) ||
                                objectresourceIDMap.get(o.getResourceID()) != null &&
                                        objectresourceIDMap.get(o.getResourceID()).equals(vo.getResourceID())) &&
                                o.canMerge(vo)) {
                            vo.addModels(o.getModels());
                            mergedO = true;
                            break;
                        }
                    }
                    if (!mergedO) {
                        vp.getLeft().resourceRefs.add(o);
                    }
                }
            }
        }

        LinkedList<Vertex> toRemove = new LinkedList<Vertex>();
        // check if some vertices must be removed
        for (Vertex v : merged.getVertices()) {
            if (v.getParents().size() == 0 && v.getChildren().size() == 0) {
                toRemove.add(v);
            }
        }

        for (Vertex v : toRemove) {
            merged.removeVertex(v.getID());
        }

        for (LinkedList<VertexPair> region : mappingRegions.getRegions()) {

//			if (mapping.size() == 1) {
//				continue;
//			}

            LinkedList<VertexPair> sources = findSources(region);
            LinkedList<VertexPair> sinks = findSinks(region);

            // process sources
            for (VertexPair source : sources) {
//				System.out.println("Source "+ source.getLeft().getLabel());
                Vertex g1Source = source.getLeft();
                Vertex g2Source = source.getRight();
                LinkedList<Vertex> g1SourcePrev = new LinkedList<Vertex>(g1Source.getParents());//removeFromList(g1Source.getParents(), mapping);
                LinkedList<Vertex> g2SourcePrev = new LinkedList<Vertex>(g2Source.getParents());//removeFromList(g2Source.getParents(), mapping);

                if (!g1Source.getType().equals(Vertex.Type.gateway)) {

                    Vertex newSource = new Vertex(Vertex.GWType.xor, idGenerator.getNextId());

                    newSource.setConfigurable(true);
                    merged.addVertex(newSource);

                    merged.connectVertices(newSource, g1Source, labelsg1g2);
                    for (Vertex v : g1SourcePrev) {
                        g1Source.removeParent(v.getID());
                        v.removeChild(g1Source.getID());
                        HashSet<String> labels = merged.removeEdge(v.getID(), g1Source.getID());
                        Edge edge = merged.connectVertices(v, newSource, labels);
                        if(!g1.getGraphLabel().equals("merged")) {
                            edge.addLabel(g1.getGraphLabel());
                        }
//						newEdge.addLabelToModel();
                        //					System.out.println(" newSource connect  "+ newSource.getID()+" "+ v.getID()+" ");
                    }

                    for (Vertex v : g2SourcePrev) {
                        //					merged.removeEdge(""+v.getID(), ""+g2Source.getID());
                        v.removeChild(g2Source.getID());
                        HashSet<String> labels = merged.getEdgeLabels(v.getID(), g2Source.getID());
                        Edge edge = merged.connectVertices(v, newSource, labels);
                        if(!g2.getGraphLabel().equals("merged")) {
                            edge.addLabel(g2.getGraphLabel());
                        }
//						newEdge.addLabelToModel();
                        //					System.out.println(" newSource connect  "+ newSource.getID()+" "+ v.getID());
                    }

                    // add fake nodes?
                    if (g1Source.sourceBefore || g2Source.sourceBefore) {
                        //					System.out.println("g1SourcePrev.size() == 0 || g2SourcePrev.size() == 0");
                        Vertex fakeEvent = new Vertex(Vertex.Type.event, "e", idGenerator.getNextId());
                        Vertex fakeFn = new Vertex(Vertex.Type.function, "e", idGenerator.getNextId());
                        merged.addVertex(fakeEvent);
                        merged.addVertex(fakeFn);
                        Edge edge = merged.connectVertices(fakeEvent, fakeFn);
                        Edge newEdge = merged.connectVertices(fakeFn, newSource);
                        if (g1Source.sourceBefore) {
                            if(!g1.getGraphLabel().equals("merged")) {
                                edge.addLabel(g1.getGraphLabel());
                                newEdge.addLabel(g1.getGraphLabel());
                            }
                        }
                        if (g2Source.sourceBefore) {
                            if(!g2.getGraphLabel().equals("merged")) {
                                edge.addLabel(g2.getGraphLabel());
                                newEdge.addLabel(g2.getGraphLabel());
                            }
                        }
//						newEdge.addLabelToModel();
                    }

                }
                // this is gateway
                else {
                    for (Vertex v : g2SourcePrev) {
                        v.removeChild(g2Source.getID());
                        if(!containsVertex(mapping, v)) {
                            HashSet<String> labels = merged.getEdgeLabels(v.getID(), g2Source.getID());
                            Edge edge = merged.connectVertices(v, g1Source, labels);

                            if(!g2.getGraphLabel().equals("merged")) {
                                edge.addLabel(g2.getGraphLabel());
                            }
//							newEdge.addLabelToModel();

                        }
                    }
                }
            }

            // process sinks
            for (VertexPair sink : sinks) {

//				System.out.println(">>newSink "+ sink.getLeft().getLabel()+ "("+sink.getLeft().getID()+")");
                Vertex g1Sink = sink.getLeft();
                Vertex g2Sink = sink.getRight();

                LinkedList<Vertex> g1SourceFoll = new LinkedList<Vertex>(g1Sink.getChildren());//removeFromList(g1Sink.getChildren(), mapping);
                LinkedList<Vertex> g2SourceFoll = new LinkedList<Vertex>(g2Sink.getChildren());//removeFromList(g2Sink.getChildren(), mapping);

                if (!g1Sink.getType().equals(Vertex.Type.gateway)) {
                    Vertex newSink = new Vertex(Vertex.GWType.xor, idGenerator.getNextId());
                    newSink.setConfigurable(true);
//					System.out.println("newSink "+ newSink.getID());
                    try {
                        merged.getVertexLabel(newSink.getID());
//						System.out.println("ALREADY EXISTS");
                    }
                    catch (Exception e) {
                        LOGGER.error("Error "+e.getMessage());
                    }

                    merged.addVertex(newSink);

                    merged.connectVertices(g1Sink, newSink, labelsg1g2);

                    for (Vertex v : g1SourceFoll) {
                        g1Sink.removeChild(v.getID());
                        v.removeParent(g1Sink.getID());
                        HashSet<String> labels = merged.removeEdge(g1Sink.getID(), v.getID());
                        merged.connectVertices(newSink, v, labels);
                    }

                    for (Vertex v : g2SourceFoll) {
                        v.removeParent(g2Sink.getID());
                        HashSet<String> labels = merged.getEdgeLabels(g2Sink.getID(), v.getID());
                        merged.connectVertices(newSink, v, labels);
                    }

                    // add fake nodes?
                    if (g1Sink.sinkBefore || g2Sink.sinkBefore) {
                        Vertex fakeEvent = new Vertex(Vertex.Type.event, "e", idGenerator.getNextId());
                        Vertex fakeFn = new Vertex(Vertex.Type.function, "e", idGenerator.getNextId());
                        merged.addVertex(fakeEvent);
                        merged.addVertex(fakeFn);
                        Edge edge = merged.connectVertices(fakeFn, fakeEvent);
                        Edge newEdge = merged.connectVertices(newSink, fakeFn);

                        if (g1Sink.sinkBefore) {
                            if(!g1.getGraphLabel().equals("merged")) {
                                edge.addLabel(g1.getGraphLabel());
                                newEdge.addLabel(g1.getGraphLabel());
                            }
                        }
                        if (g2Sink.sinkBefore) {
                            if(!g2.getGraphLabel().equals("merged")) {
                                edge.addLabel(g2.getGraphLabel());
                                newEdge.addLabel(g2.getGraphLabel());
                            }
                        }
                    }

                }else {
                    for (Vertex v : g2SourceFoll) {
                        v.removeParent(g2Sink.getID());
                        if(!containsVertex(mapping, v)) {
                            HashSet<String> labels = merged.getEdgeLabels(g2Sink.getID(), v.getID());
                            Edge edge = merged.connectVertices(g1Sink, v, labels);

                            if(!g2.getGraphLabel().equals("merged")) {
                                edge.addLabel(g2.getGraphLabel());
                            }
                        }
                    }
                }
            }

            for (VertexPair vp : mapping) {
                for(Vertex v : vp.getLeft().getParents()) {
                    // this edge is in mapping
                    // save labels from the both graph
                    if (containsVertex(mapping, v)) {
                        Edge e = merged.containsEdge(v.getID(), vp.getLeft().getID());
                        if (e != null) {
                            // this is a part of a mapping
                            Vertex v2 = getMappingPair(mapping, v);
                            if (v2 != null) {
                                Edge e2 = g2.containsEdge(v2.getID(), vp.getRight().getID());
                                if (e2 != null) {
                                    e.addLabels(e2.getLabels());
                                    // the common part should also have the labels of both graph
                                }
                            }
                            if(!g1.getGraphLabel().equals("merged")) {
                                e.addLabel(g1.getGraphLabel());
                            }
                            if(!g2.getGraphLabel().equals("merged")) {
                                e.addLabel(g2.getGraphLabel());
                            }
                        }
                    }
                }
            }

//			System.out.println("REMOVE MAPPiNG start");
            // remove mapping
            for (VertexPair vp : mapping) {
                // remove edges
                for (Vertex v : vp.getRight().getParents()) {
//					System.out.println("parents : "+ v.getID());
                    merged.removeEdge(v.getID(), vp.getRight().getID());
                }
                for (Vertex v : vp.getRight().getChildren()) {
//					System.out.println("children : "+ v.getID());
                    merged.removeEdge(vp.getRight().getID(), v.getID());
                }
//				System.out.println("REMOVE vertex "+ vp.getRight().getLabel());

                if (vp.getLeft().getType().equals(Vertex.Type.gateway) &&
                        vp.getLeft().getGWType().equals(vp.getRight().getGWType())
                        && (vp.getLeft().isAddedGW() || vp.getRight().isAddedGW())) {
                    vp.getLeft().setConfigurable(true);
                }

                if (vp.getLeft().getType().equals(Vertex.Type.gateway)
                        && (vp.getLeft().isInitialGW() || vp.getRight().isInitialGW())) {

                    vp.getLeft().setInitialGW();
                }

                // change gateways
                if (vp.getLeft().getType().equals(Vertex.Type.gateway) &&
                        !vp.getLeft().getGWType().equals(vp.getRight().getGWType())) {
                    vp.getLeft().setGWType(Vertex.GWType.or);
                    vp.getLeft().setConfigurable(true);

                }
                merged.removeVertex(vp.getRight().getID());
            }
//			System.out.println("REMOVE MAPPiNG end");
        }

        int[] gwInf = merged.getNrOfConfigGWs();

        long mergeTime = System.currentTimeMillis();
        merged.cleanGraph();

        gwInf = merged.getNrOfConfigGWs();

        // labels for all edges should be added to the modelass
        for (Edge e : merged.getEdges()) {
            e.addLabelToModel();
        }

        long cleanTime = System.currentTimeMillis();

        merged.mergetime = mergeTime - startTime;
        merged.cleanTime = cleanTime - startTime;

        merged.name = "";
        for (String l : merged.getEdgeLabels()) {
            merged.name += l + ",";
        }
        merged.name = "merged"; //merged.name.substring(0, merged.name.length() - 1);
        merged.ID = "merged"; //String.valueOf(idGenerator.getNextId());

        return merged;
    }

    private static LinkedList<VertexPair> findSources(LinkedList<VertexPair> mapping){
        LinkedList<VertexPair> sources = new LinkedList<VertexPair>();
        for (VertexPair vp : mapping) {
            boolean added = false;
            for (Vertex v : vp.getLeft().getParents()) {
                // the mapping does not contain
                if (!containsVertex(mapping, v)) {
                    sources.add(vp);
                    added = true;
                    break;
                }
            }
            if (!added) {
                for (Vertex v : vp.getRight().getParents()) {
                    // the mapping does not contain
                    if (!containsVertex(mapping, v)) {
                        sources.add(vp);
                        break;
                    }
                }
            }
        }
        return sources;
    }

    private static LinkedList<VertexPair> findSinks(LinkedList<VertexPair> mapping){
        LinkedList<VertexPair> sinks = new LinkedList<VertexPair>();
        for (VertexPair vp : mapping) {
            boolean added = false;
            for (Vertex v : vp.getLeft().getChildren()) {
                // the mapping does not contain
                if (!containsVertex(mapping, v)) {
                    sinks.add(vp);
                    added = true;
                    break;
                }
            }
            if (!added) {
                for (Vertex v : vp.getRight().getChildren()) {
                    // the mapping does not contain
                    if (!containsVertex(mapping, v)) {
                        sinks.add(vp);
                        break;
                    }
                }
            }
        }
        return sinks;
    }

    private static void mergeResources(Collection<VertexResource> existing,
                                       HashMap<String, String> objectresourceIDMap, Graph merged) {
        // add resources and objects
        for (VertexResource v : existing) {
            boolean mergedResource = false;
            for (VertexResource mv : merged.getResources().values()) {
                if (mv.canMerge(v)) {
                    objectresourceIDMap.put(v.getId(), mv.getId());

                    if (v.isConfigurable()) {
                        mv.setConfigurable(true);
                    }
                    mv.addModels(v.getModels());
                    mergedResource = true;
                    break;
                }
            }
            // this resource must be added
            if (!mergedResource) {
                merged.getResources().put(v.getId(), v);
            }
        }
    }

    private static void mergeObjects(Collection<VertexObject> existing,
                                     HashMap<String, String> objectresourceIDMap, Graph merged) {
        // add resources and objects
        for (VertexObject v : existing) {
            boolean mergedResource = false;
            for (VertexObject mv : merged.getObjects().values()) {
                if (mv.canMerge(v)) {
                    objectresourceIDMap.put(v.getId(), mv.getId());

                    if (v.isConfigurable()) {
                        mv.setConfigurable(true);
                    }
                    mv.addModels(v.getModels());
                    mergedResource = true;
                    break;
                }
            }
            // this resource must be added
            if (!mergedResource) {
                merged.getObjects().put(v.getId(), v);
            }
        }
    }

    @SuppressWarnings("unused")
    private void removeNonDominanceMappings(LinkedList<VertexPair> mapping) {

        LinkedList<VertexPair> removeList = new LinkedList<VertexPair>();
        int i = 0;

        for (VertexPair vp : mapping) {
            i++;
            // the mapping is already in removed list
            if (removeList.contains(vp)) {
                continue;
            }

            for (int j = i; j < mapping.size(); j++) {
                VertexPair vp1 = mapping.get(j);
                if (vp.getLeft().getID().equals(vp1.getLeft().getID()) ||
                        vp.getRight().getID().equals(vp1.getRight().getID())) {
                    continue;
                }
                boolean dominanceInG1 = containsInDownwardsPath(vp.getLeft(), vp1.getLeft());
                boolean dominanceInG2 = containsInDownwardsPath(vp.getRight(), vp1.getRight());

                // dominance rule is broken
                if (dominanceInG1 && !dominanceInG2 || !dominanceInG1 && dominanceInG2) {
                    // remove 2 pairs from the pairs list and start with the new pair
                    removeList.add(vp);
                    removeList.add(vp1);
                    break;
                }
            }
        }

        // remove conflicting mappings
        for (VertexPair vp : removeList) {
            mapping.remove(vp);
        }
    }

    @SuppressWarnings("unused")
    private void removeNonDominanceMappings1(LinkedList<VertexPair> mapping) {

        LinkedList<VertexPair> removeList = new LinkedList<VertexPair>();
        int i = 0;

        for (VertexPair vp : mapping) {
            i++;
            // the mapping is already in removed list
            if (removeList.contains(vp)) {
                continue;
            }

            // TODO - if there exists path where A dominances B, then this dominances B
            // even when this is a cycle
            for (int j = i; j < mapping.size(); j++) {
                VertexPair vp1 = mapping.get(j);
                if (vp.getLeft().getID().equals(vp1.getLeft().getID()) ||
                        vp.getRight().getID().equals(vp1.getRight().getID())) {
                    continue;
                }

                // dominance rule is broken
                if (vp.getLeft().dominance.contains(vp1.getLeft().getID())
                        && vp1.getRight().dominance.contains(vp.getRight().getID())
                        || vp1.getLeft().dominance.contains(vp.getLeft().getID())
                        && vp.getRight().dominance.contains(vp1.getRight().getID())) {
                    // remove 2 pairs from the pairs list and start with the new pair
                    removeList.add(vp);
                    removeList.add(vp1);
                    break;
                }
            }
        }

        // remove conflicting mappings
        for (VertexPair vp : removeList) {
            mapping.remove(vp);
        }
    }

    // implementation of Marlon new dominance mapping relation
    private static void removeNonDominanceMappings2(LinkedList<VertexPair> mapping) {

        LinkedList<VertexPair> removeList = new LinkedList<VertexPair>();
        int i = 0;

        for (VertexPair vp : mapping) {
            i++;
            // the mapping is already in removed list
            if (removeList.contains(vp)) {
                continue;
            }

            for (int j = i; j < mapping.size(); j++) {

                VertexPair vp1 = mapping.get(j);

                // the mapping is already in removed list
                if (removeList.contains(vp1)) {
                    continue;
                }

                // same starting or ending point of models
                if (vp.getLeft().getID().equals(vp1.getLeft().getID()) ||
                        vp.getRight().getID().equals(vp1.getRight().getID())) {
                    continue;
                }

                // dominance rule is broken
                if ((vp.getLeft().dominance.contains(vp1.getLeft().getID())
                        && vp1.getRight().dominance.contains(vp.getRight().getID())
                        && !(vp1.getLeft().dominance.contains(vp.getLeft().getID())
                        || vp.getRight().dominance.contains(vp1.getRight().getID())))
                        || (vp1.getLeft().dominance.contains(vp.getLeft().getID())
                        && vp.getRight().dominance.contains(vp1.getRight().getID())
                        && !(vp.getLeft().dominance.contains(vp1.getLeft().getID())
                        || vp1.getRight().dominance.contains(vp.getRight().getID())))) {
                    // remove 2 pairs from the pairs list and start with the new pair
                    removeList.add(vp);
                    removeList.add(vp1);
                    break;
                }
            }
        }

        // remove conflicting mappings
        for (VertexPair vp : removeList) {
            mapping.remove(vp);
        }
    }

    private boolean containsInDownwardsPath(Vertex v1, Vertex v2) {

        LinkedList<Vertex> toProcess = new LinkedList<Vertex>();
        toProcess.addAll(v1.getChildren());

        while (toProcess.size() > 0) {
            Vertex process = toProcess.removeFirst();
            if (process.getID().equals(v2.getID())) {
                return true;
            }
            toProcess.addAll(process.getChildren());
        }
        return false;
    }

    private static void mergeConnectors(MappingRegions mappingRegions, Graph merged, LinkedList<VertexPair> mapping) {
        for (LinkedList<VertexPair> region : mappingRegions.getRegions()) {
            LOGGER.error("region "+region.toString());
            for (VertexPair vp : region) {
                LOGGER.error("vp "+vp.toString());
                LOGGER.error("vp left "+vp.getLeft());
                LOGGER.error("vp right "+vp.getRight());
                if (vp.getLeft().getType().equals(Type.gateway)) {
                    boolean makeConf = false;
                    LinkedList<Vertex> toProcess = new LinkedList<Vertex>();
                    LOGGER.error("vp right parents "+vp.getRight().getParents().size());
                    for (Vertex p : vp.getRight().getParents()) {
                        LOGGER.error("For Each");
                        for (VertexPair zz : region) {
                            if (zz.getLeft().getID().equals(p.getID()) || zz.getRight().getID().equals(p.getID())) {
                                LOGGER.error("Aggiungi");
                                break;
                            }
                        }
                        if (!containsVertex(region, p)) {
                            toProcess.add(p);
                        }
                    }

                    for (Vertex p : toProcess) {
                        LOGGER.error("SET MAKECONF True");
                        makeConf = true;
                        HashSet<String> l = merged.removeEdge(p.getID(), vp.getRight().getID());
                        p.removeChild(vp.getRight().getID());
                        vp.getRight().removeParent(p.getID());
                        merged.connectVertices(p, vp.getLeft(), l);
                    }
                    toProcess = new LinkedList<Vertex>();

                    LOGGER.error("vp right children "+vp.getRight().getChildren().size());
                    for (Vertex p : vp.getRight().getChildren()) {
                        LOGGER.error("For Each");
                        for (VertexPair zz : region) {
                            if (zz.getLeft().getID().equals(p.getID()) || zz.getRight().getID().equals(p.getID())) {
                                LOGGER.error("Aggiungi");
                                break;
                            }
                        }
                        if (!containsVertex(region, p)) {
                            toProcess.add(p);
                        }
                    }

                    for (Vertex p : toProcess) {
                        LOGGER.error("SET MAKECONF True");
                        makeConf = true;
                        HashSet<String> l = merged.removeEdge(vp.getRight().getID(), p.getID());
                        p.removeParent(vp.getRight().getID());
                        vp.getRight().removeChild(p.getID());
                        merged.connectVertices(vp.getLeft(), p, l);
                    }
                    if (makeConf) {
                        LOGGER.error("SET setConfigurable True");
                        vp.getLeft().setConfigurable(true);
                    }
                    if (!vp.getLeft().getGWType().equals(vp.getRight().getGWType())) {
                        vp.getLeft().setGWType(GWType.or);
                    }
                }
            }
        }
    }


    private static VertexPair findNextVertexToProcess(LinkedList<VertexPair> mapping, LinkedList<VertexPair> visited) {
        for (VertexPair vp : mapping) {
            VertexPair process = containsMapping(visited, vp.getLeft(), vp.getRight());
            if (process == null) {
                return vp;
            }
        }
        return null;
    }

    private static VertexPair containsMapping(LinkedList<VertexPair> mapping, Vertex left, Vertex right) {
        for (VertexPair vp : mapping) {
            if (vp.getLeft().getID().equals(left.getID()) &&
                    vp.getRight().getID().equals(right.getID())) {
                return vp;
            }
        }
        return null;
    }

    @SuppressWarnings("unused")
    private static boolean containsMapping(LinkedList<VertexPair> mapping, VertexPair v) {
        for (VertexPair vp : mapping) {
            if (vp.getLeft().getID().equals(v.getLeft().getID()) &&
                    vp.getRight().getID().equals(v.getRight().getID())) {
                return true;
            }
        }
        return false;
    }

    public static MappingRegions findMaximumCommonRegions(Graph g1, Graph g2, LinkedList<VertexPair> mapping) {
        MappingRegions map = new MappingRegions();
        LinkedList<VertexPair> visited = new LinkedList<VertexPair>();

        while (true) {
            VertexPair c = findNextVertexToProcess(mapping, visited);
            if (c == null) {
                break;
            }
            LinkedList<VertexPair> toVisit = new LinkedList<VertexPair>();
            LinkedList<VertexPair> mapRegion = new LinkedList<VertexPair>();

            toVisit.add(c);
            while (toVisit.size() > 0) {
                c = toVisit.removeFirst();
                mapRegion.add(c);

                visited.add(c);
                for (Vertex pLeft : c.getLeft().getParents()) {
                    for (Vertex pRight : c.getRight().getParents()) {
                        VertexPair pairMap = containsMapping(mapping, pLeft, pRight);
                        VertexPair containsMap = containsMapping(visited, pLeft, pRight);
                        VertexPair containsMap1 = containsMapping(toVisit, pLeft, pRight);
                        if (pairMap != null && containsMap == null && containsMap1 == null) {
                            toVisit.add(pairMap);
                        }
                    }
                }

                for (Vertex pLeft : c.getLeft().getChildren()) {
                    for (Vertex pRight : c.getRight().getChildren()) {
                        VertexPair pairMap = containsMapping(mapping, pLeft, pRight);
                        VertexPair containsMap = containsMapping(visited, pLeft, pRight);
                        VertexPair containsMap1 = containsMapping(toVisit, pLeft, pRight);
                        if (pairMap != null && containsMap == null && containsMap1 == null) {
                            toVisit.add(pairMap);
                        }
                    }
                }

            }
            if (mapRegion.size() > 0) {
                map.addRegion(mapRegion);
            }
        }

        return map;
    }

    public static boolean containsVertex(LinkedList<VertexPair> mapping, Vertex v) {
        for (VertexPair vp : mapping) {
            if (vp.getLeft().getID().equals(v.getID()) || vp.getRight().getID().equals(v.getID())) {
                return true;
            }
        }
        return false;
    }

    public static Vertex getMappingPair(LinkedList<VertexPair> mapping, Vertex v) {
        for (VertexPair vp : mapping) {
            if (vp.getLeft().getID().equals(v.getID())) {
                return vp.getRight();
            } else if (vp.getRight().getID().equals(v.getID())) {
                return vp.getLeft();
            }
        }
        return null;
    }
}
