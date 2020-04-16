/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package org.apromore.toolbox.clustering.dissimilarity.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Efficient implementation of a simple graph: (Vertices, Edges, labels)
 * Only for reading, cannot be modified
 */
public class SimpleGraph {

    public String id;
    public Set<Integer> vertices;
    public Set<TwoVertices> edges = null;
    public Set<Integer> connectors;
    public Set<Integer> events;
    public Set<Integer> functions;


    protected Map<Integer, Set<Integer>> outgoingEdges;
    protected Map<Integer, Set<Integer>> incomingEdges;
    protected Map<Integer, String> labels;
    protected Set<String> functionLabels;
    protected Set<String> eventLabels;

    public SimpleGraph() {
    }

    public SimpleGraph(SimpleGraph g) {
        vertices = new HashSet<>();
        for (Integer v : g.getVertices()) {
            vertices.add(v);
        }

        edges = new HashSet<>();
        for (TwoVertices gtw : g.getEdges()) {
            TwoVertices tw = new TwoVertices(gtw.v1, gtw.v2);
            edges.add(tw);
        }

        connectors = new HashSet<>();
        for (Integer c : g.getConnectors()) {
            connectors.add(c);
        }

        events = new HashSet<>();
        for (Integer e : g.getEvents()) {
            events.add(e);
        }

        functions = new HashSet<>();
        for (Integer f : g.getFunctions()) {
            functions.add(f);
        }

        outgoingEdges = new HashMap<>();
        outgoingEdges.putAll(g.getOutgoingEdges());

        incomingEdges = new HashMap<>();
        incomingEdges.putAll(g.getIncomingEdges());

        labels = new HashMap<>();
        labels.putAll(g.getLabelsAsMap());

        functionLabels = new HashSet<>();
        functionLabels.addAll(g.getFunctionLabels());

        eventLabels = new HashSet<>();
        eventLabels.addAll(g.getEventLabels());
    }

    private SimpleGraph(Set<Integer> vertices, Map<Integer, Set<Integer>> outgoingEdges, Map<Integer, Set<Integer>> incomingEdges, Map<Integer, String> labels) {
        this.vertices = vertices;
        this.outgoingEdges = outgoingEdges;
        this.incomingEdges = incomingEdges;
        this.labels = labels;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Set<Integer> getVertices() {
        return vertices;
    }

    public Collection<TwoVertices> getEdgesAsCollection() {
        return new ArrayList<>(edges);
    }

    public Set<TwoVertices> getEdges() {
        return new HashSet<>(edges);
    }

    public Set<Integer> getConnectors() {
        return connectors;
    }

    public Set<Integer> getEvents() {
        return events;
    }

    public Set<Integer> getFunctions() {
        return functions;
    }

    public Map<Integer, Set<Integer>> getOutgoingEdges() {
        return outgoingEdges;
    }

    public Map<Integer, Set<Integer>> getIncomingEdges() {
        return incomingEdges;
    }

    public Set<String> getFunctionLabels() {
        return functionLabels;
    }

    public Set<String> getEventLabels() {
        return eventLabels;
    }

    public Set<Integer> postSet(int vertex) {
        return outgoingEdges.get(vertex);
    }

    public Set<Integer> preSet(int vertex) {
        return incomingEdges.get(vertex);
    }

    public LinkedList<String> getLabels() {
        return new LinkedList<>(labels.values());
    }

    public Map<Integer, String> getLabelsAsMap() {
        return labels;
    }

    public String getLabel(int vertex) {
        return labels.get(vertex);
    }

    public Set<String> getLabels(Set<Integer> nodes) {
        Set<String> result = new HashSet<>();

        for (Integer node : nodes) {
            result.add(getLabel(node));
        }

        return result;
    }

    public Integer getVertex(String label) {
        for (Integer v : vertices) {
            if (labels.get(v).equals(label)) {
                return v;
            }
        }
        return Integer.MAX_VALUE;
    }

    /**
     * @return vertices that do not have an incoming edge.
     */
    public Set<Integer> sourceVertices() {
        Set<Integer> result = new HashSet<>();
        for (Integer i : vertices) {
            if (incomingEdges.get(i).isEmpty()) {
                result.add(i);
            }
        }
        return result;
    }

    /**
     * @return vertices that do not have an outgoing edge.
     */
    public Set<Integer> sinkVertices() {
        Set<Integer> result = new HashSet<>();
        for (Integer i : vertices) {
            if (outgoingEdges.get(i).isEmpty()) {
                result.add(i);
            }
        }
        return result;
    }

    public String toString() {
        String result = "";
        for (Integer i : vertices) {
            result += i + "(" + labels.get(i) + ") {";
            for (Iterator<Integer> j = incomingEdges.get(i).iterator(); j.hasNext(); ) {
                int vertex = j.next();
                result += vertex;
                result += j.hasNext() ? "," : "";
            }
            result += "} {";
            for (Iterator<Integer> j = outgoingEdges.get(i).iterator(); j.hasNext(); ) {
                int vertex = j.next();
                result += vertex;
                result += j.hasNext() ? "," : "";
            }
            result += "}\n";
        }
        return result;
    }

    /**
     * @param vertex Vertex to determine the postSet for
     * @param silent Set of vertices that should not be considered
     * @return the postSet(vertex), in which all v \in silent are (recursively) replaced by their postSet(v)
     */
    public Set<Integer> nonSilentPostSet(Integer vertex, Set<Integer> silent) {
        return nonSilentPostSetHelper(vertex, silent, new HashSet<Integer>());
    }

    private Set<Integer> nonSilentPostSetHelper(Integer vertex, Set<Integer> silent, Set<Integer> visited) {
        Set<Integer> result = new HashSet<>();
        Set<Integer> visitedP = new HashSet<>(visited);
        visitedP.add(vertex);

        for (Integer post : postSet(vertex)) {
            if (!visited.contains(post)) {
                if (silent.contains(post)) {
                    result.addAll(nonSilentPostSetHelper(post, silent, visitedP));
                } else {
                    result.add(post);
                }
            }
        }
        return result;
    }

    /**
     * @param vertex Vertex to determine the preSet for
     * @param silent Set of vertices that should not be considered
     * @return the preSet(vertex), in which all v \in silent are (recursively) replaced by their preSet(v)
     */
    public Set<Integer> nonSilentPreSet(Integer vertex, Set<Integer> silent) {
        return nonSilentPreSetHelper(vertex, silent, new HashSet<Integer>());
    }

    private Set<Integer> nonSilentPreSetHelper(Integer vertex, Set<Integer> silent, Set<Integer> visited) {
        Set<Integer> result = new HashSet<>();
        Set<Integer> visitedP = new HashSet<>(visited);
        visitedP.add(vertex);

        for (Integer pre : preSet(vertex)) {
            if (!visited.contains(pre)) {
                if (silent.contains(pre)) {
                    result.addAll(nonSilentPreSetHelper(pre, silent, visitedP));
                } else {
                    result.add(pre);
                }
            }
        }
        return result;
    }

    /**
     * Returns A COPY OF the graph, such that all vertices from the given set are removed.
     * All paths (v1,v),(v,v2) via a vertex v from that set are replaced by direct arcs (v1,v2).
     * <p/>
     * Formally: for G = (V, E, l)
     * return (V-vertices, E', l-(vertices x labels)), where
     * E' = E - ((V x vertices) U (vertices X V))
     * U {(v1, v2)|v \in vertices, (v1,v) \in E \land (v,v2) \in E}
     */
    public SimpleGraph removeVertices(Set<Integer> toRemove) {
        Set<Integer> newVertices = new HashSet<>(vertices);
        newVertices.removeAll(toRemove);

        Map<Integer, Set<Integer>> newOutgoingEdges = new HashMap<>();
        Map<Integer, Set<Integer>> newIncomingEdges = new HashMap<>();
        Map<Integer, String> newLabels = new HashMap<>();

        for (Integer newVertex : newVertices) {
            newOutgoingEdges.put(newVertex, nonSilentPostSet(newVertex, toRemove));
            newIncomingEdges.put(newVertex, nonSilentPreSet(newVertex, toRemove));
            newLabels.put(newVertex, labels.get(newVertex));
        }

        return new SimpleGraph(newVertices, newOutgoingEdges, newIncomingEdges, newLabels);
    }

    /**
     * Given subset of vertices of this graph, the method builds the corresponding subgraph.
     *
     * @param _vertices Set of vertices in the subgraph
     * @return The subgraph
     */
    public SimpleGraph subgraph(Set<Integer> _vertices) {
        Set<Integer> newVertices = new HashSet<>(vertices);
        newVertices.removeAll(_vertices);

        Map<Integer, Set<Integer>> newOutgoingEdges = new HashMap<>();
        Map<Integer, Set<Integer>> newIncomingEdges = new HashMap<>();
        Map<Integer, String> newLabels = new HashMap<>();

        for (Integer newVertex : newVertices) {
            HashSet<Integer> vertexSet = new HashSet<>();
            for (Integer source : preSet(newVertex))
                if (newVertices.contains(source))
                    vertexSet.add(source);
            newIncomingEdges.put(newVertex, vertexSet);

            vertexSet = new HashSet<>();
            for (Integer target : postSet(newVertex))
                if (newVertices.contains(target))
                    vertexSet.add(target);
            newOutgoingEdges.put(newVertex, vertexSet);

            newLabels.put(newVertex, labels.get(newVertex));
        }

        return new SimpleGraph(newVertices, newOutgoingEdges, newIncomingEdges, newLabels);
    }
}
