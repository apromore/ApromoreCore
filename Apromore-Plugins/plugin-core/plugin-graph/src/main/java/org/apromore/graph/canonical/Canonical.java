/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * Copyright (C) 2013 Felix Mannhardt.
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

package org.apromore.graph.canonical;

import org.apromore.cpf.EdgeType;
import org.jbpt.algo.graph.DirectedGraphAlgorithms;

import java.util.*;

/**
 * An implementation of ICanonical interface.
 * <p/>
 *
 * @author Cameron James
 */
public class Canonical extends AbstractCanonical<CPFEdge, CPFNode> {

    public static final DirectedGraphAlgorithms<CPFEdge, CPFNode> DIRECTED_GRAPH_ALGORITHMS = new DirectedGraphAlgorithms<>();

    private CPFNode entry = null;
    private CPFNode exit = null;

    private String uri;
    private String version;
    private String author;
    private String creationDate;
    private String modifiedDate;

    private Set<ICPFObject> objects = new HashSet<>();
    private Set<ICPFResource> resources = new HashSet<>();
    private Map<String, IAttribute> properties = new HashMap<>();
    private final Map<String, Map<String, String>> nodeProperties = new HashMap<>();
    private final Map<String, String> originalNodeMapping = new HashMap<>();

    public final Map<Object, String> variantMap = new HashMap<>();


    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public void setUri(String newUri) {
        uri = newUri;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public void setVersion(String newVersion) {
        version = newVersion;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public void setAuthor(String newAuthor) {
        author = newAuthor;
    }

    @Override
    public String getCreationDate() {
        return creationDate;
    }

    @Override
    public void setCreationDate(String newCreationDate) {
        creationDate = newCreationDate;
    }

    @Override
    public String getModifiedDate() {
        return modifiedDate;
    }

    @Override
    public void setModifiedDate(String newModifiedDate) {
        modifiedDate = newModifiedDate;
    }

    @Override
    public CPFNode getEntry() {
        return entry;
    }

    @Override
    public void setEntry(CPFNode entry) {
        this.entry = entry;
    }

    @Override
    public CPFNode getExit() {
        return exit;
    }

    @Override
    public void setExit(CPFNode exit) {
        this.exit = exit;
    }


    @Override
    public Set<ICPFResource> getResources() {
        return resources;
    }

    @Override
    public void setResources(final Set<ICPFResource> newResources) {
        this.resources = newResources;
    }

    @Override
    public void addResource(ICPFResource newResource) {
        resources.add(newResource);
    }

    @Override
    public Set<ICPFObject> getObjects() {
        return objects;
    }

    @Override
    public void addObject(ICPFObject object) {
        objects.add(object);
    }

    @Override
    public void setObjects(final Set<ICPFObject> newObjects) {
        this.objects = newObjects;
    }

    @Override
    public Collection<CPFNode> addNodes(Collection<CPFNode> nodes) {
        for (CPFNode node : nodes) {
            node.setGraph(this);
        }
        Collection<CPFNode> result = this.addVertices(nodes);
        return result == null ? new ArrayList<CPFNode>() : result;
    }

    @Override
    public CPFEdge addEdge(CPFEdge newEdge) {
        Collection<CPFNode> ss = new ArrayList<>();
        Collection<CPFNode> ts = new ArrayList<>();

        ss.add(newEdge.getSource());
        ts.add(newEdge.getTarget());

        if (!this.checkEdge(ss, ts)) {
            return null;
        }

        return new CPFEdge(this, newEdge);
    }

    @Override
    public CPFEdge addEdge(CPFNode from, CPFNode to) {
        if (from == null || to == null) {
            return null;
        }

        Collection<CPFNode> ss = new ArrayList<>();
        Collection<CPFNode> ts = new ArrayList<>();

        ss.add(from);
        ts.add(to);

        if (!this.checkEdge(ss, ts)) {
            return null;
        }

        return new CPFEdge(this, from, to);
    }

    @Override
    public CPFEdge addEdge(String id, CPFNode from, CPFNode to) {
        if (from == null || to == null) {
            return null;
        }

        Collection<CPFNode> ss = new ArrayList<>();
        Collection<CPFNode> ts = new ArrayList<>();

        ss.add(from);
        ts.add(to);

        if (!this.checkEdge(ss, ts)) {
            return null;
        }

        return new CPFEdge(this, id, from, to);
    }

    @Override
    public Collection<CPFEdge> addEdges(Collection<CPFEdge> edges) {
        if (edges == null || edges.isEmpty()) {
            return null;
        }

        for (CPFEdge edge : edges) {
            addEdge(edge);
        }

        return getEdges();
    }

    @Override
    public void updateEdge(CPFEdge edge, EdgeType edgeType, CPFExpression expr) {
        if (edge == null) {
            return;
        }

        for (CPFEdge e : this.getEdges()) {
            if (e.getSource().getId().equals(edge.getSource().getId()) && e.getSource().getId().equals(edge.getSource().getId())) {
                e.setId(edgeType.getId());
                e.setOriginalId(edgeType.getOriginalID());
                e.setDefault(edgeType.isDefault());
                e.setConditionExpr(expr);
                this.getEdges().remove(e);
                this.addEdge(e);
            }
        }
    }


    @Override
    public Set<CPFNode> getSourceNodes() {
        return Canonical.DIRECTED_GRAPH_ALGORITHMS.getSources(this);
    }

    @Override
    public Set<CPFNode> getSinkNodes() {
        return Canonical.DIRECTED_GRAPH_ALGORITHMS.getSinks(this);
    }


    @Override
    public Collection<CPFNode> getAllPredecessors(CPFNode fn) {
        Set<CPFNode> result = new HashSet<>();

        Set<CPFNode> temp = new HashSet<>();
        temp.addAll(getDirectPredecessors(fn));
        result.addAll(temp);
        while(!(temp.isEmpty())) {
            Set<CPFNode> temp2 = new HashSet<>();
            for (CPFNode flowNode : temp) {
                temp2.addAll(getDirectPredecessors(flowNode));
            }
            temp = temp2;
            Set<CPFNode> temp3 = new HashSet<>();
            for (CPFNode flowNode : temp) {
                if(!(result.contains(flowNode))) {
                    result.add(flowNode);
                } else {
                    temp3.add(flowNode);
                }
            }
            for (CPFNode flowNode : temp3) {
                temp.remove(flowNode);
            }
        }

        return result;
    }

    @Override
    public Collection<CPFNode> getAllSuccessors(CPFNode fn) {
        Set<CPFNode> result = new HashSet<>();

        Set<CPFNode> temp = new HashSet<>();
        temp.addAll(getDirectSuccessors(fn));
        result.addAll(temp);
        while(!(temp.isEmpty())) {
            Set<CPFNode> temp2 = new HashSet<>();
            for (CPFNode flowNode : temp) {
                temp2.addAll(getDirectSuccessors(flowNode));
            }
            temp = temp2;
            Set<CPFNode> temp3 = new HashSet<>();
            for (CPFNode flowNode : temp) {
                if(!(result.contains(flowNode))) {
                    result.add(flowNode);
                } else {
                    temp3.add(flowNode);
                }
            }
            for (CPFNode flowNode : temp3) {
                temp.remove(flowNode);
            }
        }

        return result;
    }

    @Override
    public Collection<CPFNode> getDirectPredecessors(CPFNode node) {
        Set<CPFNode> result = new HashSet<>();

        Collection<CPFEdge> es = this.getIncomingEdges(node);
        for (CPFEdge e : es) {
            result.addAll(e.getSourceVertices());
        }

        return result;
    }

    @Override
    public Collection<CPFNode> getDirectPredecessors(Collection<CPFNode> vs) {
        Set<CPFNode> result = new HashSet<>();

        Collection<CPFEdge> es = this.getEdgesWithTargets(vs);
        for (CPFEdge e : es) {
            result.addAll(e.getSourceVertices());
        }

        return result;
    }


    @Override
    public void setNodeProperty(final String nodeId, final String propertyName, final String propertyValue) {
        Map<String, String> props = nodeProperties.get(nodeId);
        if (props == null) {
            props = new HashMap<>();
        }
        props.put(propertyName, propertyValue);
        nodeProperties.put(nodeId, props);
    }

    @Override
    public String getNodeProperty(final String nodeId, final String propertyName) {
        String result = null;
        Map<String, String> props = nodeProperties.get(nodeId);
        if (properties != null) {
            result = props.get(propertyName);
        }
        return result;
    }

    @Override
    public void setProperties(final Map<String, IAttribute> properties) {
        this.properties = properties;
    }

    @Override
    public Map<String, IAttribute> getProperties() {
        return properties;
    }

    @Override
    public IAttribute getProperty(final String name) {
        return properties.get(name);
    }

    @Override
    public void setProperty(final String name, final String value, final java.lang.Object any) {
        properties.put(name, new CPFAttribute(value, any));
    }

    @Override
    public void setProperty(final String name, final String value) {
        setProperty(name, value, null);
    }

    @Override
    public Map<String, CPFNode> getNodeMap() {
        Map<String, CPFNode> map = new HashMap<>();
        for (CPFNode node : getNodes()) {
            map.put(node.getId(), node);
        }
        return map;
    }



    public void populateDominantRelationships() {
        for (CPFNode v : getNodes()) {
            v.setDominance(performFullDominanceSearch(v));
        }
    }

    public Map<String, String> getOriginalNodeMapping() {
        return originalNodeMapping;
    }

    public void addOriginalNodeMapping(final String duplicateNode, final String originalNode) {
        originalNodeMapping.put(duplicateNode, originalNode);
    }

    public boolean isDuplicateNode(final String node) {
        return originalNodeMapping.keySet().contains(node);
    }

    public String getOriginalNode(final String duplicateNode) {
        return originalNodeMapping.get(duplicateNode);
    }


    /* Used to find the dominant relationships. */
    private Set<String> performFullDominanceSearch(CPFNode nodes) {
        LinkedList<CPFNode> toProcess = new LinkedList<>(nodes.getChildren());
        HashSet<String> domList = new HashSet<>();

        while (toProcess.size() > 0) {
            CPFNode node = toProcess.removeFirst();
            if (domList.contains(node.getId())) {
                continue;
            }
            domList.add(node.getId());
            for (CPFNode ch : node.getChildren()) {
                if (!domList.contains(ch.getId()) && !toProcess.contains(ch)) {
                    toProcess.add(ch);
                }
            }
        }
        return domList;
    }

}
