/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2013 - 2016 Technical University of Eindhoven, Reina Uba.
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

package org.apromore.similaritysearch.graph;

import org.apromore.similaritysearch.common.IdGeneratorHelper;
import org.apromore.similaritysearch.graph.Vertex.GWType;
import org.apromore.similaritysearch.graph.Vertex.Type;

import java.util.*;

public class Graph {

    private List<Edge> edges = new ArrayList<Edge>();
    private List<Vertex> vertices = new ArrayList<Vertex>();

    private Map<String, VertexObject> objectMap = new HashMap<String, VertexObject>();
    private Map<String, VertexResource> resourceMap = new HashMap<String, VertexResource>();

    public String name;
    public String ID;

    private IdGeneratorHelper idGenerator;
    public int beforeReduction = 0;

    // time that merging takes (without cleaning)
    public long mergetime = 0;
    // time that merging takes with graph cleaning
    public long cleanTime = 0;

    private Set<String> graphLabels = new HashSet<String>();
    private Map<String, Vertex> vertexMap = new HashMap<String, Vertex>();
    private static Map<String, String> edgeLabelMap = new HashMap<String, String>();
    private boolean isConfigurableGraph = false;


    public Set<String> getGraphLabel1() {
        return graphLabels;
    }

    public IdGeneratorHelper getIdGenerator() {
        return idGenerator;
    }

    public void setIdGenerator(IdGeneratorHelper idGenerator) {
        this.idGenerator = idGenerator;
    }

    public void fillDominanceRelations() {
        for (Vertex v : vertices) {
            // TODO find these more sophisticated way
            v.dominance = performFullDominanceSearch(v);
        }
    }

    private Set<String> performFullDominanceSearch(Vertex v) {
        ArrayList<Vertex> toProcess = new ArrayList<Vertex>(v.getChildren());
        Set<String> domList = new HashSet<String>();

        while (toProcess.size() > 0) {
            Vertex process = toProcess.remove(0);
            if (domList.contains(process)) {
                continue;
            }
            domList.add(process.getID());
            for (Vertex ch : process.getChildren()) {
                if (!domList.contains(ch.getID()) && !toProcess.contains(ch)) {
                    toProcess.add(ch);
                }
            }
        }
        return domList;
    }

    @SuppressWarnings("unused")
    private static ArrayList<Edge> copyEdges(List<Edge> toCopy) {
        ArrayList<Edge> toReturn = new ArrayList<Edge>();
        for (Edge e : toCopy) {
            toReturn.add(Edge.copyEdge(e));
        }
        return toReturn;
    }

    public static ArrayList<Vertex> copyVertices(List<Vertex> toCopy) {
        ArrayList<Vertex> toReturn = new ArrayList<Vertex>();
        for (Vertex e : toCopy) {
            toReturn.add(Vertex.copyVertex(e));
        }
        return toReturn;
    }

    public String getGraphLabel() {
        return name;
    }

    public List<Vertex> getPreset(String vertexId) {
        List<Vertex> result = null;
        for (Vertex vertex : vertices) {
            if (vertex.getID().equalsIgnoreCase(vertexId)) {
                result = vertex.getChildren();
                break;
            }
        }
        return result;
    }

    public List<Vertex> getPostset(String vertexId) {
        List<Vertex> result = null;
        for (Vertex vertex : vertices) {
            if (vertex.getID().equalsIgnoreCase(vertexId)) {
                result = vertex.getParents();
                break;
            }
        }
        return result;
    }


    public void addGraphLabel(String graphLabel) {
        graphLabels.add(graphLabel);
    }

    public void addGraphLabels(Set<String> graphLabel) {
        graphLabels.addAll(graphLabel);
    }

    public Set<String> getEdgeLabels() {
        return edgeLabelMap.keySet();
    }

    public boolean addEdgeLabel(String label, String modelComment) {
        // this label is already in edgeLabelMap
        if (edgeLabelMap.containsKey(label)) {
            return false;
        }
        edgeLabelMap.put(label, modelComment);
        return true;
    }

    public void reorganizeEdgeLabels() {
        // get all the current edge labels

        for (Edge e : edges) {
            // edge has a label
            if (e.getLabels().size() > 0) {
                for (String label : e.getLabels()) {
                    addEdgeLabel(label, "");
                }
            }
        }
    }

    public String getVertexLabel(String vertexId) {
        String result = null;
        for (Vertex vertex : vertices) {
            if (vertex.getID().equalsIgnoreCase(vertexId)) {
                result = vertex.getLabel();
                break;
            }
        }
        return result;
    }

    private Set<String> getCombinedLabels(Vertex v, boolean parents) {
        Set<String> labels = new HashSet<String>();

        if (parents) {
            for (Vertex p : v.getParents()) {
                Edge e = containsEdge(p.getID(), v.getID());
                Set<String> eLabels = e.getLabels();
                if (e != null && eLabels != null && eLabels.size() > 0) {
                    labels.addAll(eLabels);
                }
            }
        } else {
            for (Vertex p : v.getChildren()) {
                Edge e = containsEdge(v.getID(), p.getID());
                Set<String> eLabels = e.getLabels();
                if (e != null && eLabels != null && eLabels.size() > 0) {
                    labels.addAll(eLabels);
                }
            }
        }
        return labels;
    }

    private boolean containsNewEdgeLabels(Set<String> labels1, Set<String> labels2) {

        for (String l : labels1) {
            if (!labels2.contains(l)) {
                return true;
            }
        }
        // all edge labels are presented in second Set
        return false;
    }

    private void setLabelsToParents(Set<String> labels, Vertex v, ArrayList<Vertex> toProcessConfGWs) {
        ArrayList<Vertex> toProcessVertices = new ArrayList<Vertex>();
        toProcessVertices.add(v);

        while (toProcessVertices.size() > 0) {
            Vertex current = toProcessVertices.remove(0);

            for (Vertex p : current.getParents()) {
                Edge e = containsEdge(p.getID(), current.getID());
                if (e != null) {
                    if (p.getType().equals(Type.gateway) && p.isConfigurable()) {
                        // label of this gateway is already contributed
                        // check if new labels are added to this
                        if (p.labelContributed) {
                            if (containsNewEdgeLabels(labels, e.getLabels())) {
                                // check if new labels are added to this
                                e.addLabels(labels);
                                p.labelContributed = false;
                                toProcessConfGWs.add(p);
                            }
                        } else {
                            e.addLabels(labels);
                        }
                    } else {
                        e.addLabels(labels);
                        toProcessVertices.add(p);
                    }
                }
            }
        }
    }

    private void setLabelsToChildren(Set<String> labels, Vertex v, ArrayList<Vertex> toProcessConfGWs) {
        ArrayList<Vertex> toProcessVertices = new ArrayList<Vertex>();
        toProcessVertices.add(v);

        while (toProcessVertices.size() > 0) {
            Vertex current = toProcessVertices.remove(0);

            for (Vertex ch : current.getChildren()) {
                Edge e = containsEdge(current.getID(), ch.getID());
                if (e != null) {
                    if (ch.getType().equals(Type.gateway) && ch.isConfigurable()) {
                        // label of this gateway is already contributed
                        // check if new labels are added to this
                        if (ch.labelContributed) {
                            if (containsNewEdgeLabels(labels, e.getLabels())) {
                                // check if new labels are added to this
                                e.addLabels(labels);
                                ch.labelContributed = false;
                                toProcessConfGWs.add(ch);
                            }
                        } else {
                            e.addLabels(labels);
                        }
                    } else {
                        e.addLabels(labels);
                        toProcessVertices.add(ch);
                    }
                }
            }
        }
    }

    private Set<String> extractLabels() {
        Set<String> labels = new HashSet<String>();
        for(Edge edge : getEdges()) {
            labels.addAll(edge.getLabels());
        }
        return labels;
    }

    public void addLabelsToUnNamedEdges() {
        // the graph is not configurable
        // add all new labels

        if (!isConfigurableGraph) {
            // add the graph name to the labels
            // the graphs that are to be merged must have different names
            String label = this.name;
            addEdgeLabel(label, "");
            addGraphLabel(label);

            for (VertexResource r : resourceMap.values()) {
                r.addModel(label);
            }

            for (VertexObject o : objectMap.values()) {
                o.addModel(label);
            }

            // find the labels for edges that does not have labels
            Set<String> labels = extractLabels();
            if(labels.size() == 0) labels.add(label);

            for (Edge e : edges) {
                if(e.getLabels().size() == 0) e.addLabels(labels);
            }

            for (Vertex v : vertices) {
                if (v.getType().equals(Vertex.Type.gateway)) {
                    if (v.getGWType().equals(Vertex.GWType.and)) {
                        v.getAnnotationMap().put(label, "and");
                    } else if (v.getGWType().equals(Vertex.GWType.or)) {
                        v.getAnnotationMap().put(label, "or");
                    } else if (v.getGWType().equals(Vertex.GWType.xor)) {
                        v.getAnnotationMap().put(label, "xor");
                    }
                } else {
                    if(v.getLabel() == null || v.getLabel().isEmpty()) {
                       if(v.getParents() == null || v.getParents().size() == 0) {
                           v.setLabel("startEvent");
                       }else if(v.getChildren() == null || v.getChildren().size() == 0) {
                           v.setLabel("endEvent");
                       }
                    }

                    for (VertexObjectRef o : v.objectRefs) {
                        o.addModel(label);
                    }

                    for (VertexResourceRef r : v.resourceRefs) {
                        r.addModel(label);
                    }

                    v.getAnnotationMap().put(label, v.getLabel());
                }
            }
            return;
        }
        // contribute edge labels
        ArrayList<Vertex> toProcessConfGWs = new ArrayList<Vertex>();
        // get configurable gw-s
        for (Vertex v : vertices) {
            if (v.getType().equals(Type.gateway) && v.isConfigurable()) {
                toProcessConfGWs.add(v);
            }
        }

        // contribute labels
        while (toProcessConfGWs.size() > 0) {
            Vertex currentGW = toProcessConfGWs.remove(0);
            if (isJoin(currentGW)) {
                Set<String> labelsForChildren = getCombinedLabels(currentGW, true);
                setLabelsToChildren(labelsForChildren, currentGW, toProcessConfGWs);

                for (Vertex p : currentGW.getParents()) {
                    if (!(p.getType().equals(Type.gateway) && p.isConfigurable())) {
                        Edge e = containsEdge(p.getID(), currentGW.getID());
                        if (e != null) {
                            setLabelsToParents(e.getLabels(), p, toProcessConfGWs);
                        }
                    }
                }

                // set labels for children
            }
            // must be split
            else {
                Set<String> labelsForParents = getCombinedLabels(currentGW, false);
                setLabelsToParents(labelsForParents, currentGW, toProcessConfGWs);

                for (Vertex ch : currentGW.getChildren()) {
                    if (!(ch.getType().equals(Type.gateway) && ch.isConfigurable())) {
                        Edge e = containsEdge(currentGW.getID(), ch.getID());
                        if (e != null) {
                            setLabelsToChildren(e.getLabels(), ch, toProcessConfGWs);
                        }
                    }
                }

            }
            currentGW.labelContributed = true;
        }
    }

    public void reorganizeIDs() {
        Map<String, String> idMap = new HashMap<String, String>();
        Map<String, Vertex> vertexMapTmp = new HashMap<String, Vertex>();

        for (Vertex v : vertices) {
            String oldID = v.getID();
            String next = idGenerator.getNextId();
            idMap.put(oldID, next);
            v.setID(next);

            vertexMapTmp.put(v.getID(), v);
        }

        for (Edge e : edges) {
            e.setId(idGenerator.getNextId());
            e.setFromVertex(idMap.get(e.getFromVertex()));
            e.setToVertex(idMap.get(e.getToVertex()));

        }
        // add edge labels to map
        if (isConfigurableGraph) {
            reorganizeEdgeLabels();
        }
        vertexMap = vertexMapTmp;
    }

    public void addEdge(Edge e) {
        if (!edges.contains(e)) {
            edges.add(e);
        }
    }

    public void removeEmptyNodes() {
        ArrayList<Vertex> vToRemove = new ArrayList<Vertex>();
        ArrayList<Vertex> vToRemove2 = new ArrayList<Vertex>();
        for (Vertex v : vertices) {
            if (v.getChildren().size() == 0 && v.getParents().size() == 0) {
                vToRemove2.add(v);
            } else if ((v.getType().equals(Vertex.Type.function)
                    || v.getType().equals(Vertex.Type.event)
                    || v.getType().equals(Vertex.Type.state)
                    || v.getType().equals(Vertex.Type.node))
                    && v.getChildren().size() == 1 && v.getParents().size() ==1
                    && (v.getLabel() == null || v.getLabel().length() == 0)) {
                if(v.getType().equals(Vertex.Type.event) && ((v.getChildren().size() == 0 && v.getParents().size() > 0) || (v.getChildren().size() > 0 && v.getParents().size() == 0))) {
                    continue;
                }
                vToRemove.add(v);
            }
        }

        // vertex with empty label
        for (Vertex v : vToRemove) {
            if (v.getChildren().size() == 0 && v.getParents().size() == 0) {
                vToRemove2.add(v);
                continue;
            }
            // we have a source node
            if (v.getParents().size() == 0) {
                v.getChildren().get(0).removeParent(v.getID());
                removeEdge(v.getID(), v.getChildren().get(0).getID());
                removeVertex(v.getID());
            }
            //  we have fall node
            else if (v.getChildren().size() == 0) {
                v.getParents().get(0).removeChild(v.getID());
                removeEdge(v.getParents().get(0).getID(), v.getID());
                removeVertex(v.getID());
            } else {
                Vertex vChild = v.getChildren().get(0);
                Vertex vParent = v.getParents().get(0);

                vChild.removeParent(v.getID());
                Set<String> labels = removeEdge(v.getID(), vChild.getID());
                vParent.removeChild(v.getID());
                labels.addAll(removeEdge(vParent.getID(), v.getID()));
                connectVertices(vParent, vChild, labels);
                removeVertex(v.getID());
            }
        }

        // remove separate nodes
        for (Vertex v : vToRemove2) {
            removeVertex(v.getID());
        }
    }

    private boolean canMerge(Vertex v1, Vertex v2) {
        return !(v1.isInitialGW() && v2.isInitialGW());
    }

    public void removeSplitJoins() {
        ArrayList<Vertex> gateways = new ArrayList<Vertex>();
        beforeReduction = vertices.size();

        // get all gateways
        for (Vertex v : vertices) {
            if (v.getType() == Type.gateway) {
                gateways.add(v);
            }
        }

        removeSplitJoins(gateways);
    }

    // remove gateways that have no sense
    public void cleanGraph() {

        ArrayList<Vertex> gateways = new ArrayList<Vertex>();
        beforeReduction = vertices.size();

        // get all gateways
        for (Vertex v : vertices) {
            if (v.getType() == Type.gateway) {
                gateways.add(v);
            }
        }

//        removeSplitJoins(gateways);

        boolean process = true;
        while (process) {
            removeSplitJoins(gateways);
            process = mergeSplitsAndJoins(gateways);
            process |= removeCycles(gateways);
            process |= cleanGatewaysRemove(gateways);
            process |= removeLabelsFromJoinSplit(gateways);
        }

        for (Vertex gw : gateways) {
            gw.processedGW = false;
        }
    }

    private boolean removeLabelsFromJoinSplit(ArrayList<Vertex> gateways) {
        boolean changed = false;
        for (Vertex v1 : gateways) {
            if (isJoin(v1)) {
                for (Vertex v2 : gateways) {
                    if (isSplit(v2)) {
                        Edge e = containsEdge(v1.getID(), v2.getID());
                        if(e != null && e.getLabels().size() > 0) {
                            e.getLabels().clear();
                            changed = true;
                        }
                    }
                }
            }
        }
        return changed;
    }


    public void setEdgeLabelsVisible() {
        ArrayList<Vertex> gateways = new ArrayList<Vertex>();

        for (Vertex v : vertices) {
            if (v.getType() == Type.gateway) {
                gateways.add(v);
            }
        }

        for (Edge e : edges) {
            e.removeLabelFromModel();
        }

        for (Vertex gw : gateways) {
            if (gw.isConfigurable()) {
                if (isJoin(gw)) {
                    for (Vertex v : gw.getParents()) {
                        Edge e = containsEdge(v.getID(), gw.getID());
                        if (e != null) {
                            e.addLabelToModel();
                        }
                    }
                } else if (isSplit(gw)) {
                    for (Vertex v : gw.getChildren()) {
                        Edge e = containsEdge(gw.getID(), v.getID());
                        if (e != null) {
                            e.addLabelToModel();
                        }
                    }
                }
            }
        }

    }


    public void removeSplitJoins(ArrayList<Vertex> gateways) {
        ArrayList<Vertex> toAdd = new ArrayList<Vertex>();
        for (Vertex gw : gateways) {
            if (gw.getParents().size() > 1 && gw.getChildren().size() > 1) {
                Vertex v = new Vertex(gw.getGWType(), idGenerator.getNextId());
                if (gw.isConfigurable()) {
                    v.setConfigurable(true);
                }
                ArrayList<Vertex> gwChildren = new ArrayList<Vertex>(gw.getChildren());

                addVertex(v);
                toAdd.add(v);
                v.addAnnotations(gw.getAnnotationMap());

                for (Vertex child : gwChildren) {
                    gw.removeChild(child.getID());
                    child.removeParent(gw.getID());
                    Set<String> labels = removeEdge(gw.getID(), child.getID());
                    connectVertices(v, child, labels);
                }
                connectVertices(gw, v);
            }
        }

        gateways.addAll(toAdd);
    }


    @SuppressWarnings("unused")
    private boolean removeCrossingGWs(ArrayList<Vertex> gateways) {

        for (int i = 0; i < gateways.size() - 1; i++) {
            for (int j = i + 1; j < gateways.size(); j++) {
                Vertex g1 = gateways.get(i);
                Vertex g2 = gateways.get(j);
                if (g1.getParents().size() == 1 &&
                        g2.getParents().size() == 1 &&
                        g1.getChildren().size() > 1 &&
                        g1.getChildren().size() == g2.getChildren().size()) {
                    boolean crossing = true;
                    ArrayList<Vertex> children = g1.getChildren();
                    for (Vertex gwChild : children) {
                        if (!gwChild.getType().equals(Type.gateway)
                                || !containsVertex(gwChild.getParents(), g2)
                                || containsAny(gwChild.getChildren(), children)
                                || gwChild.getParents().size() != 2) {
                            crossing = false;
                            break;
                        }
                    }

                    if (crossing) {
                        // merge first gateways
                        if (!g1.getGWType().equals(g2.getGWType())) {
                            g1.setGWType(GWType.or);
                        }

                        if (g2.isConfigurable()) {
                            g1.setConfigurable(true);
                        }

                        Set<String> labels = removeEdge(g2.getParents().get(0).getID(), g2.getID());

                        g2.getParents().get(0).removeChild(g2.getID());
                        connectVertices(g2.getParents().get(0), g1, labels);

                        // remove childs
                        Vertex gwC = g1.getChildren().get(0);
                        gwC.removeParent(g2.getID());
                        labels = removeEdge(g2.getID(), gwC.getID());

                        Edge c1 = containsEdge(g1.getID(), gwC.getID());
                        if (c1 != null) {
                            c1.addLabels(labels);
                            labels = c1.getLabels();
                        }

                        Edge c2 = containsEdge(gwC.getID(), gwC.getChildren().get(0).getID());
                        if (c2 != null) {
                            c2.addLabels(labels);
                        }

                        ArrayList<Vertex> toRemoveG1Child = new ArrayList<Vertex>();

                        for (int k = 1; k < g1.getChildren().size(); k++) {
                            Vertex toRemove = g1.getChildren().get(k);
                            toRemove.removeParent(g2.getID());
                            toRemove.removeParent(g1.getID());
                            toRemoveG1Child.add(toRemove);

                            if (toRemove.isConfigurable()) {
                                gwC.setConfigurable(true);
                            }

                            Set<String> l1 = removeEdge(g1.getID(), toRemove.getID());
                            l1.addAll(removeEdge(g2.getID(), toRemove.getID()));

                            if (c1 != null) {
                                c1.addLabels(l1);
                            }

                            for (Vertex parent : toRemove.getParents()) {
                                Set<String> labels1 = removeEdge(parent.getID(), toRemove.getID());
                                parent.removeChild(toRemove.getID());
                                connectVertices(parent, gwC, labels1);
                                l1.addAll(labels1);
                            }

                            for (Vertex child : toRemove.getChildren()) {
                                Set<String> labels1 = removeEdge(toRemove.getID(), child.getID());
                                child.removeParent(toRemove.getID());
                                labels1.addAll(l1);
                                connectVertices(gwC, child, labels1);
                            }
                        }

                        for (Vertex v : toRemoveG1Child) {
                            g1.removeChild(v.getID());
                            removeVertex(v.getID());
                            gateways.remove(v);
                        }

                        removeVertex(g2.getID());
                        gateways.remove(g2);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean containsAny(ArrayList<Vertex> list1, ArrayList<Vertex> list2) {

        for (Vertex v1 : list1) {
            for (Vertex ch : v1.getChildren()) {
                for (Vertex v2 : list2) {
                    if (ch.getID() == v2.getID()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private ArrayList<Vertex> getChildGWs(Vertex gw) {
        ArrayList<Vertex> toReturn = new ArrayList<Vertex>();

        for (Vertex v : gw.getChildren()) {
            if (v.getType().equals(Vertex.Type.gateway)) {
                toReturn.add(v);
            }
        }
        return toReturn;
    }

    private ArrayList<Vertex> getAllChildGWs(Vertex gw) {
        ArrayList<Vertex> toReturn = new ArrayList<Vertex>();
        ArrayList<Vertex> toProcess = new ArrayList<Vertex>();

        toProcess = getChildGWs(gw);
        toReturn.addAll(toProcess);

        while (toProcess.size() > 0) {
            Vertex pr = toProcess.remove(0);
            ArrayList<Vertex> prCh = getChildGWs(pr);
            toProcess.addAll(prCh);
            toReturn.addAll(prCh);
        }

        return toReturn;
    }

    private ArrayList<Vertex> getParentGWs(Vertex gw) {
        ArrayList<Vertex> toReturn = new ArrayList<Vertex>();

        for (Vertex v : gw.getParents()) {
            if (v.getType().equals(Vertex.Type.gateway)) {
                toReturn.add(v);
            }
        }
        return toReturn;
    }

    private ArrayList<Vertex> getAllParentGWs(Vertex gw) {
        ArrayList<Vertex> toReturn = new ArrayList<Vertex>();
        ArrayList<Vertex> toProcess = new ArrayList<Vertex>();

        toProcess = getParentGWs(gw);
        toReturn.addAll(toProcess);

        while (toProcess.size() > 0) {
            Vertex pr = toProcess.remove(0);
            ArrayList<Vertex> prCh = getParentGWs(pr);
            toProcess.addAll(prCh);
            toReturn.addAll(prCh);
        }

        return toReturn;
    }

    private void addConfList(ArrayList<Vertex> vList, Vertex gw) {
        for (Vertex v : vList) {
            Edge e = containsEdge(gw.getID(), v.getID());
            if (e != null) {
                v.toAddEdge = e;
            }
            v.prevConfVertex = gw;
        }
    }

    @SuppressWarnings("unused")
    private void addConf(Vertex v, Vertex gw) {
        Edge e = containsEdge(gw.getID(), v.getID());
        if (e != null) {
            v.toAddEdge = e;
        }
        v.prevConfVertex = gw;
    }

    private boolean containsVertex(ArrayList<Vertex> list, Vertex v1) {
        for (Vertex v : list) {
            if (v.getID().equals(v1.getID())) {
                return true;
            }
        }
        return false;
    }

    private boolean removeCycles(ArrayList<Vertex> gateways) {
        for (Vertex gw : gateways) {
            if (gw.processedGW == false) {
                ArrayList<Vertex> childGWs = getChildGWs(gw);
                ArrayList<Vertex> gWsToProcess = new ArrayList<Vertex>();
                ArrayList<Vertex> gWsProcessed = new ArrayList<Vertex>();
                addConfList(childGWs, gw);

                for (Vertex g : childGWs) {

                    ArrayList<Vertex> toA = getChildGWs(g);
                    addConfList(toA, g);
                    gWsToProcess.addAll(toA);
                }

                while (gWsToProcess.size() > 0) {
                    Vertex toProcess = gWsToProcess.remove(0);

                    if (containsVertex(gWsProcessed, toProcess)) {
                        continue;
                    }

                    gWsProcessed.add(toProcess);

                    // this is a cycle
                    if (containsVertex(childGWs, toProcess) && canRemoveCycle(gw, toProcess)) {
                        gw.removeChild(toProcess.getID());
                        toProcess.removeParent(gw.getID());

                        Set<String> labels = removeEdge(gw.getID(), toProcess.getID());
                        Vertex v = toProcess;
                        boolean needConf = false;
                        while (v != null) {
                            if (needConf) {
                                v.setConfigurable(true);
                            } else {
                                needConf = true;
                            }
//							// change the gateway types of all splits that are in the path
//							// (if they have the different type than the starting gateway
                            if (!v.getGWType().equals(Vertex.GWType.xor)) {
                                v.setGWType(GWType.or);
                            }

                            Edge e = v.toAddEdge;
                            if (e != null) {
                                e.addLabels(labels);
                            }
                            v.addAnnotationsForGw(labels);
                            v = v.prevConfVertex;
                        }

                        return true;
                    } else {
                        ArrayList<Vertex> toA = getChildGWs(toProcess);
                        addConfList(toA, toProcess);
                        gWsToProcess.addAll(toA);
                    }
                }

                gw.processedGW = false;
            }
        }

        return false;
    }

    private boolean canRemoveCycle(Vertex gw, Vertex toProcess) {
        Set<String> edgeLabels = getEdgeLabels(gw.getID(), toProcess.getID());

        // process outgoing arcs
        Vertex v = toProcess.prevConfVertex;
        Vertex lastV = toProcess;
        while (v != null
                // just in case, maybe not needed
                && !v.getID().equals(gw.getID())) {
            for (Vertex vCh : v.getChildren()) {
                if (vCh.equals(lastV)) {
                    continue;
                }
                Set<String> childLabels = getEdgeLabels(v.getID(), vCh.getID());
                for (String label : edgeLabels) {
                    if (childLabels.contains(label)) {
                        return false;
                    }
                }
            }
            lastV = v;
            v = v.prevConfVertex;
        }

        return true;
    }

    public static boolean isSplit(Vertex v) {
        if (v.getParentsList().size() == 1 && v.getChildrenList().size() > 1) {
            return true;
        }
        return false;
    }

    private Vertex getSplit(ArrayList<Vertex> vList) {
        for (Vertex v : vList) {
            if (v.getType().equals(Vertex.Type.gateway) && isSplit(v)) {
                return v;
            }
        }
        return null;
    }

    private Vertex getJoin(ArrayList<Vertex> vList) {
        for (Vertex v : vList) {
            if (v.getType().equals(Vertex.Type.gateway) && isJoin(v)) {
                return v;
            }
        }
        return null;
    }


    public static boolean isJoin(Vertex v) {
        if (v.getParentsList().size() > 1 && v.getChildrenList().size() == 1) {
            return true;
        }
        return false;
    }

    private boolean mergeSplitsAndJoins(ArrayList<Vertex> gateways) {
        Vertex tmp = null;

        for (Vertex v : gateways) {
            // merge splits
            if (isSplit(v)) {
                tmp = getSplit(v.getChildrenList());
                // merge these spilts
                if (tmp != null/* && tmp.isConfigurable()*/ && canMerge(tmp, v)) {
                    v.removeChild(tmp.getID());
                    Set<String> label = removeEdge(v.getID(), tmp.getID());

                    ArrayList<Vertex> toConnect = tmp.getChildren();

                    for (Vertex tmpChild : toConnect) {
                        Set<String> labels = removeEdge(tmp.getID(), tmpChild.getID());

                        Set<String> labelsToExclude = new HashSet<>(labels.size());
                        labelsToExclude.addAll(labels);
                        labelsToExclude.removeAll(label);

                        Set<String> labelsToAdd = new HashSet<>(labels.size());
                        labelsToAdd.addAll(labels);
                        labelsToAdd.removeAll(labelsToExclude);

                        tmpChild.removeParent(tmp.getID());
                        connectVertices(v, tmpChild, labelsToAdd);
                    }
                    v.setConfigurable(true);
                    if (!v.getGWType().equals(tmp.getGWType())) {
                        v.setVertexGWType(Vertex.GWType.or);
                    }
                    if (tmp.initialGW) {
                        v.setInitialGW();
                    }

                    // merge annotations
                    v.mergeAnnotationsForGw(tmp);

                    gateways.remove(tmp);
                    removeVertex(tmp.getID());

                    return true;
                }
            }

            if (isJoin(v)) {
                tmp = getJoin(v.getChildrenList());
                // merge these spilts
                if (tmp != null /*&& tmp.isConfigurable()*/ && canMerge(tmp, v)) {
                    tmp.removeParent(v.getID());
                    Set<String> label = removeEdge(v.getID(), tmp.getID());

                    ArrayList<Vertex> toConnect = v.getParents();

                    for (Vertex vParent : toConnect) {
                        Set<String> labels = removeEdge(vParent.getID(), v.getID());

                        Set<String> labelsToExclude = new HashSet<>(labels);
                        labelsToExclude.removeAll(label);

                        Set<String> labelsToAdd = new HashSet<>(labels);
                        labelsToAdd.removeAll(labelsToExclude);

                        vParent.removeChild(v.getID());
                        connectVertices(vParent, tmp, labelsToAdd);
                    }
                    tmp.setConfigurable(true);
                    if (!v.getGWType().equals(tmp.getGWType())) {
                        tmp.setVertexGWType(Vertex.GWType.or);
                    }

                    if (v.initialGW) {
                        tmp.setInitialGW();
                    }

                    // merge annotations
                    tmp.mergeAnnotationsForGw(v);

                    gateways.remove(v);
                    removeVertex(v.getID());

                    return true;
                }
            }
        }

        return false;
    }


    @SuppressWarnings("unused")
    private boolean canMove(Vertex gw, boolean move) {

        if (!gw.initialGW) {
            return true;
        }

        ArrayList<Vertex> childGWs = getAllChildGWs(gw);
        for (Vertex v : childGWs) {
            if (!v.initialGW) {
                if (move) {
                    v.setInitialGW();
                }
                return true;
            }
        }
        // look parents
        ArrayList<Vertex> parentGWs = getAllParentGWs(gw);
        for (Vertex v : parentGWs) {
            if (!v.initialGW) {
                if (move) {
                    v.setInitialGW();
                }
                return true;
            }
        }
        return false;
    }

    private boolean cleanGatewaysRemove(ArrayList<Vertex> gateways) {
        ArrayList<Vertex> toRemove = new ArrayList<Vertex>();

        for (Vertex v : gateways) {
            if (v.getParents().size() < 2 && v.getChildren().size() < 2) {
                toRemove.add(v);
            }
        }

        if (toRemove.size() == 0) {
            return false;
        }
        for (Vertex v : toRemove) {
            // first node
            if (v.getParents().size() == 0 && v.getChildren().size() == 0) {
                gateways.remove(v);
            } else if (v.getParents().size() == 0) {
                Vertex child = v.getChildren().get(0);
                removeEdge(v.getID(), child.getID());
                removeVertex(v.getID());
                child.removeParent(v.getID());
                gateways.remove(v);
            } else if (v.getChildren().size() == 0) {
                Vertex parent = v.getParents().get(0);
                parent.removeChild(v.getID());
                removeEdge(parent.getID(), v.getID());
                removeVertex(v.getID());
                gateways.remove(v);
            }
            // first two should not happen in normal situations ...
            else {
//				if (v.initialGW) {
//					// maybe this is already moved in this phase
//					if (canMove(v, false)) {
//						canMove(v, true);
//					}
//					else {
//						continue;
//					}
//				}
                Vertex parent = v.getParents().get(0);
                Vertex child = v.getChildren().get(0);
                Set<String> parentLabels = removeEdge(parent.getID(), v.getID());
                Set<String> childLabels = removeEdge(v.getID(), child.getID());

                removeVertex(v.getID());
                parent.removeChild(v.getID());
                child.removeParent(v.getID());

                Set<String> labels = new HashSet<>(parentLabels);
                labels.addAll(childLabels);

                Set<String> labelsToExclude1 = new HashSet<>(labels);
                labelsToExclude1.removeAll(parentLabels);

                Set<String> labelsToExclude2 = new HashSet<>(labels);
                labelsToExclude2.removeAll(childLabels);

                Set<String> labelsToAdd = new HashSet<>(labels);
                labelsToAdd.removeAll(labelsToExclude1);
                labelsToAdd.removeAll(labelsToExclude2);

                Edge e = connectVertices(parent, child, labelsToAdd);
//                e.addLabels(childLabels);
                gateways.remove(v);
            }
        }
        return true;

    }

    public Edge connectVertices(Vertex v1, Vertex v2, Set<String> labels) {
        if (v1.getID().equals(v2.getID())) {
            return null;
        }

        Edge e1 = containsEdge(v1.getID(), v2.getID());
        if (e1 == null) {
            e1 = new Edge(v1.getID(), v2.getID(), idGenerator.getNextId());
            v1.addChild(v2);
            v2.addParent(v1);
            addEdge(e1);
        }
        if (labels != null && labels.size() > 0) {
            e1.addLabels(labels);
        }

        return e1;
    }

    public Edge connectVertices(Vertex v1, Vertex v2) {
        Edge e1 = containsEdge(v1.getID(), v2.getID());
        if (e1 == null) {
            e1 = new Edge(v1.getID(), v2.getID(), idGenerator.getNextId());
            v1.addChild(v2);
            v2.addParent(v1);
            addEdge(e1);
        }
        return e1;
    }

    public void addEdges(List<Edge> list) {
        edges.addAll(list);
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void addVertices(List<Vertex> list) {
        for (Vertex v : list) {
            addVertex(v);
        }
    }

    public Edge getEdge(int i) throws NoSuchElementException {
        if (i >= 0 && i < edges.size()) {
            return edges.get(i);
        } else {
            throw new NoSuchElementException();
        }
    }

    public Map<String, Vertex> getVertexMap() {
        return vertexMap;
    }

    public void linkVertices() {

        for (Edge e : edges) {
            Vertex from = vertexMap.get(e.getFromVertex());
            Vertex to = vertexMap.get(e.getToVertex());

            if (from == null || to == null) {
                continue;
            }

            from.addChild(to);
            to.addParent(from);
        }
    }

    public Edge containsEdge(String from, String to) {
        for (Edge e : edges) {
            if (from.equals(e.getFromVertex()) && to.equals(e.getToVertex())) {
                return e;
            }
        }
        return null;
    }

    public Set<String> removeEdge(String from, String to) {
        Edge toremove = containsEdge(from, to);

        if (toremove == null) {
            return new HashSet<String>();
        } else {
            edges.remove(toremove);
            return toremove.getLabels();
        }
    }

    public Set<String> getEdgeLabels(String from, String to) {
        Edge e = containsEdge(from, to);

        if (e == null) {
            return null;
        } else {
            return e.getLabels();
        }
    }

    public void removeVertex(String id) {
        Vertex toremove = null;
        for (Vertex v : vertices) {
            if (v.getID().equals(id)) {
                toremove = v;
                break;
            }
        }
        if (toremove == null) {
        } else {
            vertices.remove(toremove);
        }
    }

    public void removeGateways() {
        ArrayList<Vertex> toProcess = new ArrayList<Vertex>();
        ArrayList<Vertex> gateways = new ArrayList<Vertex>();

        for (Vertex v : vertices) {
            if (v.getType() == Type.gateway) {
                gateways.add(v);
            }
        }

        // parent flooding
        while (true) {

            int processed = 0;
            toProcess.clear();

            for (Vertex v : gateways) {
                if (!v.isProcessed) {
                    boolean canProcess = true;

                    for (Vertex p : v.getParents()) {
                        if (p.getType() == Type.gateway && !p.isProcessed) {
                            canProcess = false;
                            break;
                        }
                    }

                    if (canProcess) {
                        toProcess.add(v);
                    }
                } else {
                    processed++;
                }
            }
            if (processed == gateways.size()) {
                break;
            }

            for (Vertex toPr : toProcess) {
                ArrayList<Vertex> toPrParents = toPr.getParentsListAll();
                for (Vertex toPrCh : toPr.getChildren()) {
                    toPrCh.getParents().addAll(toPrParents);
                }

                toPr.isProcessed = true;
            }
        }

        for (Vertex v : gateways) {
            v.isProcessed = false;
        }

        // parent flooding
        while (true) {
            int processed = 0;
            toProcess.clear();
            for (Vertex v : gateways) {
                if (!v.isProcessed) {
                    boolean canProcess = true;

                    for (Vertex p : v.getChildrenListAll()) {
                        if (p.getType() == Type.gateway && !p.isProcessed) {
                            canProcess = false;
                            break;
                        }
                    }

                    if (canProcess) {
                        toProcess.add(v);
                    }
                } else {
                    processed++;
                }
            }

            if (processed == gateways.size()) {
                break;
            }

            for (Vertex toPr : toProcess) {
                ArrayList<Vertex> toPrChildren = toPr.getChildrenListAll();
                for (Vertex toPrCh : toPr.getParentsListAll()) {
                    toPrCh.getChildren().addAll(toPrChildren);
                }
                toPr.isProcessed = true;
            }
        }
    }

    public void addVertex(Vertex e) {
        if (!vertices.contains(e)) {
            vertexMap.put(e.getID(), e);
            vertices.add(e);
        }
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public int[] getNrOfConfigGWs() {
        int total = 0;
        int gws = 0;
        int xor = 0;
        int or = 0;
        int and = 0;
        for (Vertex v : vertices) {

            if (v.getType().equals(Vertex.Type.gateway)) {
                total++;
            }

            if (v.getType().equals(Vertex.Type.gateway) && v.isConfigurable()) {
                gws++;
                if (v.getGWType().equals(GWType.and)) {
                    and++;
                } else if (v.getGWType().equals(GWType.or)) {
                    or++;
                } else if (v.getGWType().equals(GWType.xor)) {
                    xor++;
                }
            }
        }
        return new int[]{gws, and, or, xor, total};
    }

    public int[] getNrOfVertices() {

        int gws = 0;
        int events = 0;
        int functions = 0;

        for (Vertex v : vertices) {
            if (v.getType().equals(Vertex.Type.gateway)) {
                gws++;
            } else if (v.getType().equals(Vertex.Type.event)) {
                events++;
            } else if (v.getType().equals(Vertex.Type.function)) {
                functions++;
            }
        }
        return new int[]{vertices.size(), functions, events, gws};
    }


    public ArrayList<Vertex> getFunctions() {
        ArrayList<Vertex> functions = new ArrayList<Vertex>();
        for (Vertex v : vertices) {
            if (v.getType().equals(Vertex.Type.function)) {
                functions.add(v);
            }
        }
        return functions;
    }

    public ArrayList<Vertex> getEvents() {
        ArrayList<Vertex> events = new ArrayList<Vertex>();
        for (Vertex v : vertices) {
            if (v.getType().equals(Vertex.Type.event)) {
                events.add(v);
            }
        }
        return events;
    }

    public boolean isConfigurableGraph() {
        return isConfigurableGraph;
    }

    public void setGraphConfigurable() {
        isConfigurableGraph = true;
    }

    public VertexObject getObject(long id) {
        return objectMap.get(id);
    }

    public void addObject(VertexObject o) {
        objectMap.put(o.getId(), o);
    }

    public Map<String, VertexObject> getObjects() {
        return objectMap;
    }

    public VertexResource getResource(Long id) {
        return resourceMap.get(id);
    }

    public void addResource(VertexResource v) {
        resourceMap.put(v.getId(), v);
    }

    public Map<String, VertexResource> getResources() {
        return resourceMap;
    }
}
