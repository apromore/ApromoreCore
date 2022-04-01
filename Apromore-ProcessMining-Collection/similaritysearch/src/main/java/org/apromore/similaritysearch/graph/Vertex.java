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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apromore.similaritysearch.common.Settings;


public class Vertex {

    private Graphics graphics;
    public Set<String> dominance;
    private HashMap<String, String> annotationMap = new HashMap<String, String>();
    public Set<VertexObjectRef> objectRefs = new HashSet<VertexObjectRef>();
    public Set<VertexResourceRef> resourceRefs = new HashSet<VertexResourceRef>();
    
    public enum Type {
        node,
        function,
        gateway,
        event,
        state
    }

    public enum GWType {
        or,
        xor,
        and,
        place // petri nets
    }


    private String ID;
    // node type
    private Type vertexType;
    // label
    private String label;
    private GWType gwType;
    private ArrayList<Vertex> childNodes = new ArrayList<Vertex>();
    private ArrayList<Vertex> parentNodes = new ArrayList<Vertex>();
    boolean isProcessed = false;
    boolean processedGW = false;
//  ArrayList<Node> toAddConfigurable = new ArrayList<Node>();

    //  ArrayList<Edge> toAddEdges = new ArrayList<Edge>();
    Vertex prevConfVertex = null;
    Edge toAddEdge = null;

    public boolean sourceBefore = false;
    public boolean sinkBefore = false;
    Vertex closeGW;
    private boolean isConfigurable = false;

    boolean labelContributed = false;

    boolean initialGW = false;
    
    public Vertex(Type type, String label, String ID) {
        vertexType = type;
        this.label = label;
        this.ID = ID;
    }

    public Vertex(GWType gwType, String ID) {
        vertexType = Type.gateway;
        this.gwType = gwType;
        this.ID = ID;
    }
    
    public Vertex(String gwTypeString, String ID) {
        vertexType = Type.gateway;
        if (gwTypeString.equalsIgnoreCase("xor")) {
            gwType = GWType.xor;
        } else if (gwTypeString.equalsIgnoreCase("or")) {
            gwType = GWType.or;
        } else if (gwTypeString.equalsIgnoreCase("and")) {
            gwType = GWType.and;
        }
        // for petri nets
        else if (gwTypeString.equalsIgnoreCase("place")) {
            gwType = GWType.place;
        }

        this.ID = ID;
    }

    public HashMap<String, String> getAnnotationMap() {
        return annotationMap;
    }

    public void addAnnotationsForGw(Set<String> edgeLabels) {
        for (String model : edgeLabels) {
            if (!annotationMap.containsKey(model)) {
                annotationMap.put(model, "xor");
            }
        }
    }

    public void mergeAnnotationsForGw(Vertex v) {
        if (!v.initialGW) {
            return;
        }
        annotationMap = v.getAnnotationMap();
//		HashMap<String, String> annotationMapGw = v.getAnnotationMap();
//		for (Entry<String, String> annotation : annotationMapGw.entrySet()) {
//			if (!annotationMap.containsKey(annotation.getKey())) {
//				annotationMap.put(annotation.getKey(), annotation.getValue());
//			} else {
//				// merge annotations - they are not the same type 
//				if (!annotation.getValue().equals(annotationMap.get(annotation.getValue())))  {
//					annotationMap.put(annotation.getKey(), "or");
//				}
//			}
//		}
    }

    public void addAnnotations(HashMap<String, String> annotationMap1) {
        annotationMap.putAll(annotationMap1);
    }

    public Graphics getGraphics() {
        return graphics;
    }

    public void setGraphics(Graphics graphics) {
        this.graphics = graphics;
    }

    public boolean isInitialGW() {
        return initialGW;
    }

    public void setInitialGW() {
        initialGW = true;
    }

    public static Vertex copyVertex(Vertex v) {
        Vertex toReturn = new Vertex(v.getType(), v.getLabel(), v.getID());
        toReturn.gwType = v.gwType;
        toReturn.childNodes = Graph.copyVertices(v.childNodes);
        toReturn.parentNodes = Graph.copyVertices(v.parentNodes);
        toReturn.isConfigurable = v.isConfigurable;

        return toReturn;
    }

    public Vertex copyVertex() {
        Vertex toReturn = new Vertex(getType(), getLabel(), getID());
        toReturn.gwType = gwType;
        toReturn.isConfigurable = isConfigurable;

        return toReturn;
    }

    public boolean isAddedGW() {
        return !initialGW;
    }

    public void setAddedGW(boolean isAddedGW) {
        this.initialGW = !isAddedGW;
    }

    public boolean isConfigurable() {
        return isConfigurable;
    }

    public void setConfigurable(boolean isConfigurable) {
        this.isConfigurable = isConfigurable;
//		if (isConfigurable) {
//			initialGW = false;
//		}
    }

    public void addChild(Vertex child) {
        if (!childNodes.contains(child))
            childNodes.add(child);
    }

    public ArrayList<Vertex> getChildren() {
        return childNodes;
    }

    public void removeChildren() {
        childNodes = new ArrayList<Vertex>();
    }

    public void removeChild(String id) {
        Vertex toRemove = null;
        for (Vertex v : childNodes) {
            if (v.getID().equals(id)) {
                toRemove = v;
                break;
            }
        }
        if (toRemove != null) {
            childNodes.remove(toRemove);
        }
    }


    public void removeParent(String id) {
        Vertex toRemove = null;
        for (Vertex v : parentNodes) {
            if (v.getID().equals(id)) {
                toRemove = v;
                break;
            }
        }
        if (toRemove != null) {
            parentNodes.remove(toRemove);
        }
    }

    public void removeParents() {
        parentNodes = new ArrayList<Vertex>();
    }

    public static void removeParents(ArrayList<Vertex> list) {
        for (Vertex v : list) {
            v.removeParents();
        }
    }

    public static void removeChildren(ArrayList<Vertex> list) {
        for (Vertex v : list) {
            v.removeChildren();
        }
    }

    public ArrayList<Vertex> getChildrenList() {
        ArrayList<Vertex> result = new ArrayList<Vertex>();

        for (Vertex v : childNodes) {
            if (v.getType().equals(Type.function)
                    || (Settings.considerEvents && v.getType().equals(Type.event))
                    || (Settings.considerGateways && v.getType().equals(Type.gateway))) {
                result.add(v);
            }
        }

        return result;
    }

    public ArrayList<Vertex> getChildrenListAll() {
        ArrayList<Vertex> result = new ArrayList<Vertex>();

        for (Vertex v : childNodes) {
            result.add(v);
        }

        return result;
    }


    public ArrayList<Vertex> getParents() {
        return parentNodes;
    }

    public ArrayList<Vertex> getParentsList() {
        ArrayList<Vertex> result = new ArrayList<Vertex>();

        if (parentNodes == null) {
            return result;
        }


        for (Vertex v : parentNodes) {
            if (v.getType().equals(Type.function) || (Settings.considerEvents && v.getType().equals(Type.event))
                    || (Settings.considerGateways && v.getType().equals(Type.gateway))) {
                result.add(v);
            }
        }

        return result;
    }

    public ArrayList<Vertex> getParentsListAll() {
        ArrayList<Vertex> result = new ArrayList<Vertex>();

        if (parentNodes == null) {
            return result;
        }

        for (Vertex v : parentNodes) {
            result.add(v);
        }

        return result;
    }


    public void addParent(Vertex parent) {
        if (parentNodes == null) {
            parentNodes = new ArrayList<Vertex>();
        }
        if (!parentNodes.contains(parent))
            parentNodes.add(parent);
    }

//
//    public boolean equals(Vertex v2) {
//        return this.ID.equals(v2.getID());
//
//    }


    public GWType getGWType() {
        return gwType;
    }

    public void setGWType(GWType gwType) {
        this.gwType = gwType;
    }


    public void setID(String id) {
        ID = id;
    }

    public String getID() {
        return ID;
    }


    public void setVertexType(Type vertexType) {
        this.vertexType = vertexType;
    }

    public void setVertexGWType(GWType gwType) {
        this.gwType = gwType;
    }


    public Type getType() {
        return vertexType;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ArrayList<Vertex> getAllNonGWParents() {
        ArrayList<Vertex> toReturn = new ArrayList<Vertex>();
        ArrayList<Vertex> toProcesGWs = new ArrayList<Vertex>();

        Vertex currentVertex = this;
        while (true) {
            for (Vertex p : currentVertex.getParents()) {
                if (p.getType().equals(Type.gateway)) {
                    toProcesGWs.add(p);
                }
                // we have function or event
                else {
                    toReturn.add(p);
                }
            }
            if (toProcesGWs.size() > 0) {
                currentVertex = toProcesGWs.remove(0);
            } else {
                break;
            }
        }

        return toReturn;
    }

    public ArrayList<Vertex> getAllNonGWChildren() {
        ArrayList<Vertex> toReturn = new ArrayList<Vertex>();
        ArrayList<Vertex> toProcesGWs = new ArrayList<Vertex>();

        Vertex currentVertex = this;
        while (true) {
            for (Vertex p : currentVertex.getChildren()) {
                if (p.getType().equals(Type.gateway)) {
                    toProcesGWs.add(p);
                }
                // we have function or event
                else {
                    toReturn.add(p);
                }
            }
            if (toProcesGWs.size() > 0) {
                currentVertex = toProcesGWs.remove(0);
            } else {
                break;
            }
        }
        return toReturn;
    }

    public int compareTo(Vertex another) {
        return this.getID().compareTo(another.getID());
    }

    @Override
    public String toString() {
        if (getType().equals(Type.event)) {
            return "Event(" + getID() + ", " + getLabel() + ")";
        } else if (getType().equals(Type.function)) {
            return "Function(" + getID() + ", " + getLabel() + ")";
        } else if (getType().equals(Type.gateway)) {
            return "CpfGateway(" + getID() + ", " + getGWType()/*+ "P("+printList(parentNodes)+"), CH("+printList(childNodes)+"))"*/;
        }
        return "Node(" + getID() + ", " + getLabel() + ")";
    }


    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(ID)
                .append(vertexType)
                .append(label)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) {
            return false;
        }
        Vertex rhs = (Vertex) obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .append(ID, rhs.ID)
                .isEquals();
    }
}
