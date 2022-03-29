/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2013 - 2016 Reina Uba.
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.similaritysearch.algorithms;

import org.apromore.similaritysearch.common.IdGeneratorHelper;
import org.apromore.similaritysearch.common.VertexPair;
import org.apromore.similaritysearch.common.algos.GraphEditDistanceGreedy;
import org.apromore.similaritysearch.common.algos.TwoVertices;
import org.apromore.similaritysearch.common.similarity.AssingmentProblem;
import org.apromore.similaritysearch.graph.*;
import org.apromore.similaritysearch.planarGraphMathing.PlanarGraphMathing.MappingRegions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class MergeModels {

    private static final Logger LOGGER = LoggerFactory.getLogger(MergeModels.class.getName());

    public static String find(String name, Graph g1) {
        for(Vertex v : g1.getVertices()) {
            if(name.equals(v.getLabel())) {
                return v.getID();
            }
        }
        return null;
    }

    private static HashSet<String> extractLabels(Graph g) {
        HashSet<String> labels = new HashSet<String>();
        for(Edge edge : g.getEdges()) {
            labels.addAll(edge.getLabels());
        }
        return labels;
    }

    private static int countGateways(Graph g) {
        int gateways = 0;
        for(Vertex v : g.getVertices()) {
            if(v.getType().equals(Vertex.Type.gateway)) {
                gateways++;
            }
        }
        return gateways;
    }


    public static Graph mergeModels(Graph g1, Graph g2, IdGeneratorHelper idGenerator, boolean removeEnt, String algortithm, double... param) {

        int g1_gateways = countGateways(g1);
        int g2_gateways = countGateways(g2);

        if(g1_gateways < g2_gateways) {
            Graph tmp = g1;
            g1 = g2;
            g2 = tmp;
        }

        HashMap<String, String> objectresourceIDMap = new HashMap<String, String>();

        HashSet<String> labelsg1 = extractLabels(g1);
        HashSet<String> labelsg2 = extractLabels(g2);

        for(Edge e : g1.getEdges()) {
            if(labelsg1.size() == e.getLabels().size()) {
                e.getLabels().clear();
            }
        }
        for(Edge e : g2.getEdges()) {
            if(labelsg2.size() == e.getLabels().size()) {
                e.getLabels().clear();
            }
        }

        HashSet<String> labelsg1g2 = new HashSet<String>();
        labelsg1g2.addAll(labelsg1);
        labelsg1g2.addAll(labelsg2);

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
        // do not try to merge objects in one models
        mergeResources(g2.getResources().values(), objectresourceIDMap, merged);
        merged.getObjects().putAll(g1.getObjects());
        mergeObjects(g2.getObjects().values(), objectresourceIDMap, merged);

        ArrayList<VertexPair> mapping = new ArrayList<VertexPair>();

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

        if (removeEnt) {
            g1.fillDominanceRelations();
            g2.fillDominanceRelations();
            removeNonDominanceMappings2(mapping);
        }

        MappingRegions mappingRegions = findMaximumCommonRegions(g1, g2, mapping);

        ArrayList<ArrayList<VertexPair>> regions = mappingRegions.getRegions();
        Collections.sort(regions, new Comparator<ArrayList<VertexPair>>() {
            @Override
            public int compare(ArrayList<VertexPair> o1, ArrayList<VertexPair> o2) {
                int o1_o2 = 0;
                int o2_o1 = 0;
                for(VertexPair pair1 : o1) {
                    for(VertexPair pair2 : o2) {
                        if(pair1.getLeft().getChildren().contains(pair2.getLeft()) ||
                                pair1.getRight().getChildren().contains(pair2.getRight())) {
                            o1_o2++;
                        }
                        if(pair2.getLeft().getChildren().contains(pair1.getLeft()) ||
                                pair2.getRight().getChildren().contains(pair1.getRight())) {
                            o2_o1++;
                        }
                    }
                }
                return Integer.compare(o2_o1, o1_o2);
            }
        });

        for (ArrayList<VertexPair> region : regions) {
            for (VertexPair vp : region) {
                ArrayList<Vertex> nodesToProcess = new ArrayList<Vertex>();
                for (Vertex c : vp.getRight().getChildren()) {
                    // the child is also part of the mapping
                    // remove the edge from the merged modelass
                    if (containsVertex(region, c)) {
                        nodesToProcess.add(c);
                    }
                }
                for (Vertex c : nodesToProcess) {
                    Set<String> labels = merged.removeEdge(vp.getRight().getID(), c.getID());

                    vp.getRight().removeChild(c.getID());
                    c.removeParent(vp.getRight().getID());

                    Vertex cLeft = getMappingPair(mapping, c);
                    Edge e = merged.containsEdge(vp.getLeft().getID(), cLeft.getID());
                    if(e != null) e.addLabels(labels);
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

        ArrayList<Vertex> toRemove = new ArrayList<Vertex>();
        // check if some vertices must be removed
        for (Vertex v : merged.getVertices()) {
            if (v.getParents().size() == 0 && v.getChildren().size() == 0) {
                toRemove.add(v);
            }
        }

        for (Vertex v : toRemove) {
            merged.removeVertex(v.getID());
        }

        for (ArrayList<VertexPair> region : regions) {

            ArrayList<VertexPair> sources = findSources(region);
            ArrayList<VertexPair> sinks = findSinks(region);

            // process sources (JOIN)
            for (VertexPair source : sources) {
                Vertex g1Source = source.getLeft();
                Vertex g2Source = source.getRight();
                ArrayList<Vertex> g1SourcePrev = new ArrayList<Vertex>(g1Source.getParents());
                ArrayList<Vertex> g2SourcePrev = new ArrayList<Vertex>(g2Source.getParents());

                if (!g1Source.getType().equals(Vertex.Type.gateway)) {

                    Vertex newSource = new Vertex(Vertex.GWType.xor, idGenerator.getNextId());

                    newSource.setConfigurable(true);
                    merged.addVertex(newSource);

                    merged.connectVertices(newSource, g1Source);

                    for (Vertex v : g1SourcePrev) {
                        Set<String> labels = merged.removeEdge(v.getID(), g1Source.getID());
                        g1Source.removeParent(v.getID());
                        v.removeChild(g1Source.getID());
                        Edge e = merged.containsEdge(v.getID(), newSource.getID());
                        if(e != null) {
                            if(e.getLabels().size() > 0) {
                                if(labels.size() > 0) e.addLabels(labels);
                                else e.addLabels(labelsg1);
                            }
                        }else {
                            merged.connectVertices(v, newSource, labels);
                        }
                    }

                    for (Vertex v : g2SourcePrev) {
                        Set<String> labels = merged.removeEdge(v.getID(), g2Source.getID());
                        g2Source.removeParent(v.getID());
                        v.removeChild(g2Source.getID());
                        Edge e = merged.containsEdge(v.getID(), newSource.getID());
                        if(e != null) {
                            if(e.getLabels().size() > 0) {
                                if(labels.size() > 0) e.addLabels(labels);
                                else e.addLabels(labelsg2);
                            }
                        }else {
                            merged.connectVertices(v, newSource, labels);
                        }
                    }
                }
                // this is gateway
                else {
                    for (Vertex v : g2SourcePrev) {
                        v.removeChild(g2Source.getID());
                        if (!containsVertex(mapping, v)) {
                            Set<String> labels = merged.removeEdge(v.getID(), g2Source.getID());
                            merged.connectVertices(v, g1Source, labels);
                        }
                    }
                }
            }

            // process sinks (SPLIT)
            for (VertexPair sink : sinks) {

                Vertex g1Sink = sink.getLeft();
                Vertex g2Sink = sink.getRight();

                ArrayList<Vertex> g1SourceFoll = new ArrayList<Vertex>(g1Sink.getChildren());
                ArrayList<Vertex> g2SourceFoll = new ArrayList<Vertex>(g2Sink.getChildren());

                if (!g1Sink.getType().equals(Vertex.Type.gateway)) {
                    Vertex newSink = new Vertex(Vertex.GWType.xor, idGenerator.getNextId());
                    newSink.setConfigurable(true);
                    try {
                        merged.getVertexLabel(newSink.getID());
                    } catch (Exception e) {
                        LOGGER.error("Error " + e.getMessage());
                    }

                    merged.addVertex(newSink);
                    merged.connectVertices(g1Sink, newSink);

                    for (Vertex v : g1SourceFoll) {
                        Set<String> labels = merged.removeEdge(g1Sink.getID(), v.getID());

                        g1Sink.removeChild(v.getID());
                        v.removeParent(g1Sink.getID());
                        Edge e = merged.connectVertices(newSink, v);
                        if(labels.size() > 0) e.addLabels(labels);
                        else e.addLabels(labelsg1);
                    }

                    for (Vertex v : g2SourceFoll) {
                        Set<String> labels = merged.removeEdge(g2Sink.getID(), v.getID());

                        g1Sink.removeChild(v.getID());
                        v.removeParent(g2Sink.getID());
                        Edge e = merged.connectVertices(newSink, v);
                        if(labels.size() > 0) e.addLabels(labels);
                        else e.addLabels(labelsg2);
                    }
                } else {
                    for (Vertex v : g2SourceFoll) {
                        v.removeParent(g2Sink.getID());
                        if (!containsVertex(mapping, v)) {
                            Set<String> labels = merged.removeEdge(g2Sink.getID(), v.getID());

                            Edge e = merged.connectVertices(g1Sink, v);
                            if(e.getLabels().size() > 0) {
                                if (labels.size() > 0) e.addLabels(labels);
                                else e.addLabels(labelsg2);
                            }else if (labels.size() > 0) {
                                e.addLabels(labelsg1);
                                e.addLabels(labels);
                            }
                        }
                    }
                }
            }
        }

        for (VertexPair vp : mapping) {
            for (Vertex v : vp.getLeft().getParents()) {
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
                                if(e.getLabels().size() > 0) {
                                    if(e2.getLabels().size() > 0) e.addLabels(e2.getLabels());
                                    else e.addLabels(labelsg2);
                                }else if(e2.getLabels().size() > 0) {
                                    e.addLabels(labelsg1);
                                    e.addLabels(e2.getLabels());
                                }
                                // the common part should also have the labels of both graph
                            }
                        }
                    }
                }
            }
        }

        // remove mapping
        for (VertexPair vp : mapping) {
            // remove edges
            for (Vertex v : vp.getRight().getParents()) {;
                merged.removeEdge(v.getID(), vp.getRight().getID());
            }
            for (Vertex v : vp.getRight().getChildren()) {
                merged.removeEdge(vp.getRight().getID(), v.getID());
            }

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

        long mergeTime = System.currentTimeMillis();

        for (Edge e : merged.getEdges()) {
            if(e.getLabels().size() == 0) {
               e.addLabels(labelsg1g2);
            }
        }

        merged.cleanGraph();

        boolean changed = true;
        while(changed) {
            changed = false;
            outterloop:
            for (Edge e1 : merged.getEdges()) {
                if(e1.getLabels().size() > 0) {
                    for (Vertex v : merged.getVertices()) {
                        if (e1.getToVertex().equals(v.getID()) && v.getType().equals(Vertex.Type.function)) {
                            for (Edge e2 : merged.getEdges()) {
                                if (e2.getFromVertex().equals(v.getID()) &&
                                        e2.getLabels().size() == 0) {
                                    e2.addLabels(e1.getLabels());
                                    changed = true;
                                    break outterloop;
                                }
                            }
                        }
                    }
                    for (Vertex v : merged.getVertices()) {
                        if (e1.getFromVertex().equals(v.getID()) && v.getType().equals(Vertex.Type.function)) {
                            for (Edge e2 : merged.getEdges()) {
                                if (e2.getToVertex().equals(v.getID()) &&
                                        e2.getLabels().size() == 0) {
                                    e2.addLabels(e1.getLabels());
                                    changed = true;
                                    break outterloop;
                                }
                            }
                        }
                    }
                }
            }
        }

        merged.cleanGraph();

        for (Edge e : merged.getEdges()) {
            if(e.getLabels().size() == 0) e.addLabels(labelsg1g2);
        }

        merged.cleanGraph();

        // labels for all edges should be added to the model
        for (Edge e : merged.getEdges()) {
            e.addLabelToModel();
        }

        for (Edge e : merged.getEdges()) {
            if(e.getLabels().size() == labelsg1g2.size()) {
                e.getLabels().clear();
            }
        }


        long cleanTime = System.currentTimeMillis();

        merged.mergetime = mergeTime - startTime;
        merged.cleanTime = cleanTime - startTime;

        merged.name = "";
        for (String l : merged.getEdgeLabels()) {
            merged.name += l + ",";
        }
        merged.name = "###merged###"; //merged.name.substring(0, merged.name.length() - 1);
        merged.ID = "###merged###"; //String.valueOf(idGenerator.getNextId());

        return merged;
    }

    private static boolean isSplit(Graph g, Vertex v) {
        int countFrom = 0;
        for (Edge e : g.getEdges()) {
            if(e.getFromVertex().equals(v.getID())) {
                countFrom++;
            }
        }
        return countFrom > 1;
    }

    private static boolean isJoin(Graph g, Vertex v) {
        int countTo = 0;
        for (Edge e : g.getEdges()) {
            if(e.getToVertex().equals(v.getID())) {
                countTo++;
            }
        }
        return countTo > 1;
    }

    private static HashSet<String> getLabelsExclusive(HashSet<String> labels, HashSet<String> labelSet) {
//        if(labels != null && labels.size() > 0 && labels.size() < labelSet.size()) {
        if(labels != null && labels.size() > 0 && !labels.containsAll(labelSet)) {
            return labels;
        }else {
            return new HashSet<>();
        }
    }

    private static HashSet<String> getLabelsInclusive(HashSet<String> labels, HashSet<String> labelSet) {
//        if(labels != null && labels.size() > 0 && labels.size() < labelSet.size()) {
        if(labels != null && labels.size() > 0 && !labels.containsAll(labelSet)) {
            return labels;
        }else {
            return labelSet;
        }
    }

    private static ArrayList<VertexPair> findSources(ArrayList<VertexPair> mapping){
        ArrayList<VertexPair> sources = new ArrayList<VertexPair>();
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

    private static ArrayList<VertexPair> findSinks(ArrayList<VertexPair> mapping){
        ArrayList<VertexPair> sinks = new ArrayList<VertexPair>();
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
    private void removeNonDominanceMappings(ArrayList<VertexPair> mapping) {

        ArrayList<VertexPair> removeList = new ArrayList<VertexPair>();
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
    private void removeNonDominanceMappings1(ArrayList<VertexPair> mapping) {

        ArrayList<VertexPair> removeList = new ArrayList<VertexPair>();
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
    private static void removeNonDominanceMappings2(ArrayList<VertexPair> mapping) {

        ArrayList<VertexPair> removeList = new ArrayList<VertexPair>();
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

        ArrayList<Vertex> toProcess = new ArrayList<Vertex>();
        toProcess.addAll(v1.getChildren());

        while (toProcess.size() > 0) {
            Vertex process = toProcess.remove(0);
            if (process.getID().equals(v2.getID())) {
                return true;
            }
            toProcess.addAll(process.getChildren());
        }
        return false;
    }

    private static VertexPair findNextVertexToProcess(ArrayList<VertexPair> mapping, ArrayList<VertexPair> visited) {
        for (VertexPair vp : mapping) {
            VertexPair process = containsMapping(visited, vp.getLeft(), vp.getRight());
            if (process == null) {
                return vp;
            }
        }
        return null;
    }

    private static VertexPair containsMapping(ArrayList<VertexPair> mapping, Vertex left, Vertex right) {
        for (VertexPair vp : mapping) {
            if (vp.getLeft().getID().equals(left.getID()) &&
                    vp.getRight().getID().equals(right.getID())) {
                return vp;
            }
        }
        return null;
    }

    @SuppressWarnings("unused")
    private static boolean containsMapping(ArrayList<VertexPair> mapping, VertexPair v) {
        for (VertexPair vp : mapping) {
            if (vp.getLeft().getID().equals(v.getLeft().getID()) &&
                    vp.getRight().getID().equals(v.getRight().getID())) {
                return true;
            }
        }
        return false;
    }

    public static MappingRegions findMaximumCommonRegions(Graph g1, Graph g2, ArrayList<VertexPair> mapping) {
        MappingRegions map = new MappingRegions();
        ArrayList<VertexPair> visited = new ArrayList<VertexPair>();

        while (true) {
            VertexPair c = findNextVertexToProcess(mapping, visited);
            if (c == null) {
                break;
            }
            ArrayList<VertexPair> toVisit = new ArrayList<VertexPair>();
            ArrayList<VertexPair> mapRegion = new ArrayList<VertexPair>();

            toVisit.add(c);
            while (toVisit.size() > 0) {
                c = toVisit.remove(0);
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

    public static boolean containsVertex(ArrayList<VertexPair> mapping, Vertex v) {
        for (VertexPair vp : mapping) {
            if (vp.getLeft().getID().equals(v.getID()) || vp.getRight().getID().equals(v.getID())) {
                return true;
            }
        }
        return false;
    }

    public static Vertex getMappingPair(ArrayList<VertexPair> mapping, Vertex v) {
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
