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
package de.hbrs.oryx.yawl.converter.layout;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.oryxeditor.server.diagram.Bounds;
import org.yawlfoundation.yawl.elements.YFlow;

/**
 * Layout information of a YAWL Net/Subnet and its elements
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class NetLayout {

    /**
     * Map of VertexID <-> LayoutInformation for Vertex (e.g. Task or Condition)
     */
    private final Map<String, NetElementLayout> vertexLayoutMap;

    /**
     * Map of "Prior Vertex ID"|"Next Vertex ID" <-> LayoutInformation for Flow
     */
    private final Map<String, FlowLayout> flowLayoutMap;

    /**
     * Bounds of the Net itself
     */
    private Bounds bounds;

    /**
     * Set of all flows (edges) between the YAWL elements of this net. TODO: This is not a layout information, so maybe store it elsewhere!
     */
    private final Set<YFlow> flowSet;

    public NetLayout(final Bounds bounds) {
        super();
        this.bounds = bounds;
        this.vertexLayoutMap = new HashMap<String, NetElementLayout>();
        this.flowLayoutMap = new HashMap<String, FlowLayout>();
        this.flowSet = new HashSet<YFlow>();
    }

    public void setBounds(final Bounds bounds) {
        this.bounds = bounds;
    }

    public Bounds getBounds() {
        return bounds;
    }

    public void putVertexLayout(final String vertexID, final NetElementLayout value) {
        vertexLayoutMap.put(vertexID, value);
    }

    public NetElementLayout getVertexLayout(final String vertexID) {
        return vertexLayoutMap.get(vertexID);
    }

    public void putFlowLayout(final String flowID, final FlowLayout value) {
        flowLayoutMap.put(flowID, value);
    }

    public FlowLayout getFlowLayout(final String flowID) {
        return flowLayoutMap.get(flowID);
    }

    public Set<YFlow> getFlowSet() {
        return flowSet;
    }

    public void addFlows(final Set<YFlow> flows) {
        flowSet.addAll(flows);

    }

}
