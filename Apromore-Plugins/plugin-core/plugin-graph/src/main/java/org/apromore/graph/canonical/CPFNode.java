/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * Copyright (C) 2013 Felix Mannhardt.
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.graph.canonical;

import org.jbpt.hypergraph.abs.Vertex;

import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of a Canonical node.
 * @author Cameron James
 */
public class CPFNode extends Vertex implements INode {

    private NodeTypeEnum nodeType;

    private String originalId;
    private String netId;
    private String subNetId;
    private String timeDuration;
    private boolean configurable = false;
    private boolean external = false;
    private boolean teamWork = false;
    private DirectionEnum direction;
    private GregorianCalendar timeDate;
    private CPFExpression timeExpression;
    private CPFExpression resourceDataExpr;
    private CPFExpression resourceRuntimeExpr;
    private AllocationStrategyEnum allocation;

    private Set<ICPFObjectReference> objectReferences = new HashSet<>();
    private Set<ICPFResourceReference> resourceReferences = new HashSet<>();
    private Set<String> cancelNodes = new HashSet<>();
    private Set<String> cancelEdges = new HashSet<>();
    private Set<CPFExpression> inputExpr = new HashSet<>();
    private Set<CPFExpression> outputExpr = new HashSet<>();
    private Map<String, IAttribute> attributes = new HashMap<>();

    // Used in the Merge, find something else to use.
    private Set<String> dominance;

    private Canonical graph;


    /**
     * Empty constructor.
     */
    public CPFNode() {
        super();
    }


    /**  ************************************ Processing Methods  ***********************************  */

    public Collection<CPFNode> getChildren() {
        return graph.getAllSuccessors(this) ;
    }

    public Collection<CPFNode> getParents() {
        return graph.getAllPredecessors(this) ;
    }

    public void removeChild(CPFNode toBeRemoved) {
        removeNode(toBeRemoved);
    }

    public void removeParent(CPFNode toBeRemoved) {
        removeNode(toBeRemoved);
    }

    private void removeNode(CPFNode toBeRemoved) {
        if (toBeRemoved != null) {
            graph.removeNode(toBeRemoved);
        }
    }


    /**  ************************************ Standard Methods ***********************************  */

    /**
     * Returns the type of Node this is.
     * @return the node type Enumeration.
     */
    public NodeTypeEnum getNodeType() {
        return nodeType;
    }

    /**
     * Sets the Node type.
     * @param nodeType the nodes type
     */
    public void setNodeType(NodeTypeEnum nodeType) {
        this.nodeType = nodeType;
    }

    /**
     * Set the original Id of this Node.
     * @param newOriginalId the originalId
     */
    @Override
    public void setOriginalId(String newOriginalId) {
        originalId = newOriginalId;
    }

    /**
     * returns if this Node configurable.
     * @return true or false
     */
    @Override
    public String getOriginalId() {
        return originalId;
    }

    /**
     * Set if this node is configurable.
     * @param config the config boolean
     */
    @Override
    public void setConfigurable(boolean config) {
        configurable = config;
    }

    /**
     * returns if this Node configurable.
     * @return true or false
     */
    @Override
    public boolean isConfigurable() {
        return configurable;
    }


    @Override
    public void addAttribute(final String name, final String value, final Object any) {
        attributes.put(name, new CPFAttribute(value, any));
    }

    @Override
    public void addAttribute(String name, String value) {
        addAttribute(name, value, null);
    }

    @Override
    public void setAttributes(Map<String, IAttribute> newAttributes) {
        attributes = newAttributes;
    }

    @Override
    public Map<String, IAttribute> getAttributes() {
        return attributes;
    }

    @Override
    public IAttribute getAttribute(String name) {
        return attributes.get(name);
    }



    /**  ************************************ Task Methods ***********************************  */

    /**
     * returns if the task is external or not.
     * @return if external or not
     */
    @Override
    public boolean isExternal() {
        return external;
    }

    /**
     * set the current net Id
     * @param newId the current net id
     */
    @Override
    public void setNetId(final String newId) {
        netId = newId;
    }

    /**
     * return the current net Id
     * @return the current net Id
     */
    @Override
    public String getNetId() {
        return netId;
    }

    /**
     * set the Sub net Id
     * @param newId the sub net id
     */
    @Override
    public void setSubNetId(final String newId) {
        subNetId = newId;
    }

    /**
     * return the sub net Id
     * @return the sub net Id
     */
    @Override
    public String getSubNetId() {
        return subNetId;
    }

    /**
     * set if this task is external
     * @param newExternal either true or false
     */
    @Override
    public void setExternal(final boolean newExternal) {
        external = newExternal;
    }



    /**  ************************************ Timer Methods ***********************************  */

    /**
     * Returns the Time Expression.
     * @return the time expression
     */
    public CPFExpression getTimeExpression() {
        return timeExpression;
    }

    /**
     * Set the Time Expression
     * @param newExpr the time expression
     */
    public void setTimeExpression(CPFExpression newExpr) {
        timeExpression = newExpr;
    }

    /**
     * Returns the Time Duration.
     * @return the time duration.
     */
    public String getTimeDuration() {
        return timeDuration;
    }

    /**
     * Sets the time Duration.
     * @param newTimeDuration the time duration
     */
    public void setTimeDuration(String newTimeDuration) {
        timeDuration = newTimeDuration;
    }

    /**
     * get Direction of the message.
     * @return the direction enumeration
     */
    public DirectionEnum getDirection() {
        return direction;
    }

    /**
     * sets the message direction.
     * @param direction the direction.
     */
    public void setDirection(DirectionEnum direction) {
        this.direction = direction;
    }

    /**
     * Return the Time Date.
     * @return the Calendar
     */
    public GregorianCalendar getTimeDate() {
        return timeDate;
    }

    /**
     * The Time Date.
     * @param newTimeDate the time date
     */
    public void setTimeDate(GregorianCalendar newTimeDate) {
        timeDate = newTimeDate;
    }



    /**  ************************************ Work Methods ***********************************  */

    @Override
    public Set<ICPFResourceReference> getResourceReferences() {
        return resourceReferences;
    }

    /**
     * Add a given {@link ICPFResource} to this {@link INode}.
     * @param newResource to add to this {@link INode}
     */
    @Override
    public void addResourceReference(ICPFResourceReference newResource) {
        resourceReferences.add(newResource);
    }

    /**
     * @return a {@link java.util.Collection} of all {@link ICPFObject}s of this {@link INode}.
     */
    @Override
    public Set<ICPFObjectReference> getObjectReferences() {
        return objectReferences;
    }

    /**
     * Add a given {@link ICPFObject} to this {@link INode}.
     * @param object to add to this {@link INode}
     */
    @Override
    public void addObjectReference(ICPFObjectReference object) {
        objectReferences.add(object);
    }

    @Override
    public boolean isTeamWork() {
        return teamWork;
    }

    @Override
    public void setTeamWork(final boolean teamWork) {
        this.teamWork = teamWork;
    }

    @Override
    public Set<CPFExpression> getInputExpr() {
        return inputExpr;
    }

    @Override
    public void addInputExpr(final CPFExpression newInputExpr) {
        this.inputExpr.add(newInputExpr);
    }

    @Override
    public Set<CPFExpression> getOutputExpr() {
        return this.outputExpr;
    }

    @Override
    public void addOutputExpr(final CPFExpression newOutputExpr) {
        this.outputExpr.add(newOutputExpr);
    }

    @Override
    public void addCancelNode(final String newCancelNode) {
        this.cancelNodes.add(newCancelNode);
    }

    @Override
    public void setCancelNodes(final Set<String> newCancelNodes) {
        this.cancelNodes.addAll(newCancelNodes);
    }

    @Override
    public Set<String> getCancelNodes() {
        return cancelNodes;
    }

    @Override
    public void addCancelEdge(final String newCancelEdge) {
        this.cancelEdges.add(newCancelEdge);
    }

    @Override
    public void setCancelEdges(final Set<String> newCancelEdges) {
        this.cancelEdges.addAll(newCancelEdges);
    }

    @Override
    public Set<String> getCancelEdges() {
        return cancelEdges;
    }

    @Override
    public CPFExpression getResourceDataExpr() {
        return resourceDataExpr;
    }

    @Override
    public void setResourceDataExpr(final CPFExpression resourceDataExpr) {
        this.resourceDataExpr = resourceDataExpr;
    }

    @Override
    public CPFExpression getResourceRuntimeExpr() {
        return resourceRuntimeExpr;
    }

    @Override
    public void setResourceRuntimeExpr(final CPFExpression resourceRuntimeExpr) {
        this.resourceRuntimeExpr = resourceRuntimeExpr;
    }

    @Override
    public AllocationStrategyEnum getAllocation() {
        return allocation;
    }

    @Override
    public void setAllocation(final AllocationStrategyEnum allocation) {
        this.allocation = allocation;
    }

    @Override
    public Canonical getGraph() {
        return graph;
    }

    @Override
    public void setGraph(Canonical canonicalGraph) {
        graph = canonicalGraph;
    }


    public Set<String> getDominance() {
        return dominance;
    }

    public void setDominance(Set<String> dominance) {
        this.dominance = dominance;
    }
}
