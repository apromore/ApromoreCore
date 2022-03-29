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

import java.util.HashSet;
import java.util.Set;

public class Edge {

    private Graphics fromG;

    public Graphics getFromG() {
        return fromG;
    }

    public void setFromG(Graphics fromG) {
        this.fromG = fromG;
    }

//    public void printLabels() {
//        System.out.print("\n\t");
//        for (String l : labels) {
//            System.out.print(l + ",");
//        }
//        System.out.print("\n");
//    }

    public Graphics getToG() {
        return toG;
    }

    public void setToG(Graphics toG) {
        this.toG = toG;
    }

    private Graphics toG;

    // the starting point
    private String fromVertex;
    // end point
    private String toVertex;

    private String id;

    private boolean labelAddedToModel = false;

    public static Edge copyEdge(Edge e) {

        Edge toReturn = new Edge(e.getFromVertex(), e.getToVertex(), e.id);
        toReturn.labelAddedToModel = e.labelAddedToModel;
        return toReturn;
    }

    public boolean isLabelAddedToModel() {
        return labelAddedToModel;
    }

    public void addLabelToModel() {
        labelAddedToModel = true;
    }

    public void removeLabelFromModel() {
        labelAddedToModel = false;
    }

    private Set<String> labels = new HashSet<String>();

    public Set<String> getLabels() {
        return labels;
    }

    public void addLabels(Set<String> labels) {
        this.labels.addAll(labels);
    }

    public void addLabel(String label) {
        labels.add(label);
    }

    /**
     * The constructor for new edge
     *
     * @param fromVertex the starting point of the edge (the identifier of the node)
     * @param toVertex   the ending point of the edge (the identifier of the node)
     */
    public Edge(String fromVertex, String toVertex, String id) {
        this.fromVertex = fromVertex;
        this.toVertex = toVertex;
        this.id = id;
    }

    // for greedy algorithm
    public Edge(String fromVertex, String toVertex) {
        this.fromVertex = fromVertex;
        this.toVertex = toVertex;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    /**
     * Metgod for getting stating point of the edge
     *
     * @return the identifier of the ending point of an edge
     */
    public String getToVertex() {
        return toVertex;
    }

    /**
     * Method for getting starting point of an edge.
     *
     * @return an identifier of the starting point of an egge
     */
    public String getFromVertex() {
        return fromVertex;
    }

    /**
     * Method for setting the identifier of an ending vertex of an edge.
     *
     * @param newToVertex the identifier of an new ending point edge
     */
    public void setToVertex(String newToVertex) {
        toVertex = newToVertex;
    }

    /**
     * Method for setting the identifier of an starting vertex of an edge.
     *
     * @param newFromVertex the identifier of an new starting point edge
     */
    public void setFromVertex(String newFromVertex) {
        fromVertex = newFromVertex;
    }

    public int hashCode() {
        return fromVertex.hashCode() + toVertex.hashCode();
    }

    public boolean equals(Object pair2) {
        return pair2 instanceof Edge ? (fromVertex.equals(((Edge) pair2).fromVertex) && toVertex.equals(((Edge) pair2).toVertex)) : false;
    }

    public String toString() {
        return "Edge(" + fromVertex + " -> " + toVertex + ")";
    }
}
	
