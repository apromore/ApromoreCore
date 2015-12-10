/*
 * Copyright © 2009-2015 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package de.hbrs.oryx.yawl.converter.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.elements.YFlow;

import de.hbrs.oryx.yawl.converter.layout.FlowLayout;
import de.hbrs.oryx.yawl.converter.layout.NetElementLayout;
import de.hbrs.oryx.yawl.converter.layout.NetLayout;

/**
 * YAWLConversionContext is the "glue" for the various handlers during an conversion YAWL -> Oryx. Information about the conversion and its results
 * are stored here.
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class YAWLConversionContext extends ConversionContext {

    /**
     * Oryx Diagram with properties of the Specification. This Diagram will contain the RootNet.
     */
    private BasicDiagram specificationDiagram;

    /**
     * Contains the Oryx shapes of all YAWL nets
     */
    private final HashMap<String, BasicDiagram> netMap;

    /**
     * Layout information for each YAWL (sub)-net. (Net-ID -> Layout)
     */
    private final Map<String, NetLayout> layoutMap;

    /**
     * Map of all created Shapes during the conversion (Element-ID -> Shape)
     */
    private final Map<String, BasicShape> shapeMap;

    /**
     * Identifier of the Root Net
     */
    private String rootNetId;

    /**
     * @param rootDir
     *            used to retrieve StencilSet to create new Shapes
     */
    public YAWLConversionContext() {
        super();
        this.layoutMap = new HashMap<String, NetLayout>();
        this.shapeMap = new HashMap<String, BasicShape>();
        this.netMap = new HashMap<String, BasicDiagram>();
    }

    public void setSpecificationDiagram(final BasicDiagram specificationDiagram) {
        this.specificationDiagram = specificationDiagram;
    }

    public BasicDiagram getSpecificationDiagram() {
        return specificationDiagram;
    }

    public void addNet(final String id, final BasicDiagram shape) {
        netMap.put(id, shape);
    }

    public BasicDiagram getNet(final String netId) {
        return netMap.get(netId);
    }

    public Set<Entry<String, BasicDiagram>> getNetSet() {
        return netMap.entrySet();
    }

    public void setRootNetId(final String rootNetId) {
        this.rootNetId = rootNetId;
    }

    public String getRootNetId() {
        return rootNetId;
    }

    public BasicDiagram getRootNet() {
        return netMap.get(getRootNetId());
    }

    public NetLayout getNetLayout(final String id) {
        return layoutMap.get(id);
    }

    public NetElementLayout getVertexLayout(final String netId, final String id) {
        return layoutMap.get(netId).getVertexLayout(id);
    }

    public void putNetLayout(final String yawlId, final NetLayout netLayout) {
        layoutMap.put(yawlId, netLayout);
    }

    public BasicShape getShape(final String id) {
        return shapeMap.get(id);
    }

    public void putShape(final String netId, final String shapeId, final BasicShape shape) {
        getNet(netId).addChildShape(shape);
        shapeMap.put(shapeId, shape);
    }

    public FlowLayout getFlowLayout(final String netId, final String priorElementID, final String nextElementID) {
        return layoutMap.get(netId).getFlowLayout(priorElementID + "|" + nextElementID);
    }

    public void addPostsetFlows(final String netId, final Set<YFlow> postsetFlows) {
        layoutMap.get(netId).addFlows(postsetFlows);
    }

}