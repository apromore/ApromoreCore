/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * Copyright (C) 2013 Felix Mannhardt.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
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

import org.jbpt.hypergraph.abs.IVertex;

import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Set;

/**
 * Interface to a Canonical node.
 *
 * @author Cameron James
 */
public interface INode extends IVertex {

    /**
     * Sets the Node Type.
     * @param newNodeType The Node Type. Event, Timer....
     */
    void setNodeType(NodeTypeEnum newNodeType);

    /**
     * Returns the Type of Node we are deailing with.
     * @return the type of Node.
     */
    NodeTypeEnum getNodeType();

    /**
     * Sets the Original Id.
     * @param newOriginalId the new Original Id
     */
    void setOriginalId(String newOriginalId);

    /**
     * returns if this Node configurable.
     * @return true or false
     */
    String getOriginalId();

    /**
     * Set if this node is configurable.
     * @param config the config boolean
     */
    void setConfigurable(boolean config);

    /**
     * returns if this Node configurable.
     * @return true or false
     */
    boolean isConfigurable();

    /**
     * Add an attribute to the {@link INode}.
     * @param name  the name of the attribute
     * @param value the simple value of the {@link IAttribute}
     * @param any the complex value of the {@link IAttribute}
     */
    void addAttribute(String name, String value, Object any);

    /**
     * Add an attribute to the {@link INode}.
     * @param name  the name of the attribute
     * @param value the simple value of the {@link IAttribute}
     */
    void addAttribute(String name, String value);

    /**
     * Set if this {@link INode} attributes.
     * @param attributes the map of attributes
     */
    void setAttributes(Map<String, IAttribute> attributes);

    /**
     * Returns the Attribute for the supplied name.
     * @param name the name
     * @return the attribute it founds.
     */
    IAttribute getAttribute(String name);

    /**
     * Return this {@link INode} attributes.
     * @return the attributes
     */
    Map<String, IAttribute> getAttributes();



    /*  ************************************ Task Methods ***********************************  */

    /**
     * set the current net Id
     * @param newId the current net id
     */
    void setNetId(final String newId);

    /**
     * return the current net Id
     * @return the current net Id
     */
    String getNetId();

    /**
     * set the Sub net Id
     * @param newId the sub net id
     */
    void setSubNetId(String newId);

    /**
     * return the sub net Id
     * @return the sub net Id
     */
    String getSubNetId();

    /**
     * set if this task is external
     * @param isExternal either true or false
     */
    void setExternal(boolean isExternal);

    /**
     * returns if the task is external or not.
     * @return if external or not
     */
    boolean isExternal();



    /*  ************************************ Timer Methods ***********************************  */

    /**
     * Returns the Time Expression.
     * @return the time expression
     */
    CPFExpression getTimeExpression();

    /**
     * Set the Time Expression
     * @param newExpr the time expression
     */
    void setTimeExpression(CPFExpression newExpr);

    /**
     * Returns the Time Duration.
     * @return the time duration.
     */
    String getTimeDuration();

    /**
     * Sets the time Duration.
     * @param newTimeDuration the time duration
     */
    void setTimeDuration(String newTimeDuration);

    /**
     * Return the Time Date.
     * @return the Calendar
     */
    GregorianCalendar getTimeDate();

    /**
     * The Time Date.
     * @param newTimeDate the time date
     */
    void setTimeDate(GregorianCalendar newTimeDate);



    /*  ************************************ Message Methods ***********************************  */

    /**
     * get Direction of the message.
     * @return the direction enumeration
     */
    DirectionEnum getDirection();

    /**
     * sets the message direction.
     * @param direction the direction.
     */
    void setDirection(DirectionEnum direction);


    /*  ************************************ Work Methods ***********************************  */

    /**
     * @return a {@link java.util.Collection} of all {@link ICPFObject}s of this {@link INode}.
     */
    Set<ICPFObjectReference> getObjectReferences();

    /**
     * Add a given {@link ICPFObject} to this {@link INode}.
     * @param object to add to this {@link INode}
     */
    void addObjectReference(ICPFObjectReference object);

    /**
     * @return a {@link java.util.Collection} of all {@link ICPFResource}s of this {@link INode}.
     */
    Set<ICPFResourceReference> getResourceReferences();

    /**
     * Add a given {@link ICPFResource} to this {@link INode}.
     * @param newResource to add to this {@link INode}
     */
    void addResourceReference(ICPFResourceReference newResource);

    /**
     * IS this Node apart of a team work.
     * @return returns true or false
     */
    boolean isTeamWork();

    /**
     * Sets the Team work for this node.
     * @param teamWork the team work value.
     */
    void setTeamWork(boolean teamWork);

    /**
     * returns the Input Expression of the
     * @return
     */
    Set<CPFExpression> getInputExpr();

    void addInputExpr(CPFExpression inputExpr);

    Set<CPFExpression> getOutputExpr();

    void addOutputExpr(CPFExpression outputExpr);

    void addCancelNode(final String cancelNode);

    void setCancelNodes(final Set<String> cancelNodes);

    Set<String> getCancelNodes();

    void addCancelEdge(final String cancelEdge);

    void setCancelEdges(final Set<String> cancelEdges);

    Set<String> getCancelEdges();

    CPFExpression getResourceDataExpr();

    void setResourceDataExpr(CPFExpression resourceDataExpr);

    CPFExpression getResourceRuntimeExpr();

    void setResourceRuntimeExpr(CPFExpression resourceRuntimeExpr);

    AllocationStrategyEnum getAllocation();

    void setAllocation(AllocationStrategyEnum allocation);

    Canonical getGraph();

    void setGraph(Canonical canonicalGraph);
}
