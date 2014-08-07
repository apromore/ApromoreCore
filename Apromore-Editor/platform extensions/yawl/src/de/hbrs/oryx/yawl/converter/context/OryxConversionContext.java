/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of “Apromore”.
 *
 * “Apromore” is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * “Apromore” is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package de.hbrs.oryx.yawl.converter.context;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicEdge;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.editor.core.layout.YLayout;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.YSpecification;
import org.yawlfoundation.yawl.elements.YTask;

import de.hbrs.oryx.yawl.util.YAWLUtils;

/**
 * Context of a Conversion Oryx -> YAWL
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class OryxConversionContext extends ConversionContext {

    private YSpecification specification;

    /**
     * Contains all (Sub-)Nets
     */
    final private Map<String, YNet> netMap;

    /**
     * Contains all Shapes an their connected Edges
     */
    final private Map<String, Set<BasicEdge>> flowMap;

    final private Map<String, BasicDiagram> subnetDiagramMap;

    final private Map<YNet, Map<YTask, List<String>>> cancellationSetMap;

    final private NumberFormat numberFormat;

    private YLayout layout;

    private String rootNetID;

    /**
     * Create a new OryxConversionContext used to store information about the conversion.
     * 
     * @param oryxBackendUrl
     */
    public OryxConversionContext() {
        super();
        this.netMap = new HashMap<String, YNet>();
        this.flowMap = new HashMap<String, Set<BasicEdge>>();
        this.subnetDiagramMap = new HashMap<String, BasicDiagram>();
        this.cancellationSetMap = new HashMap<YNet, Map<YTask, List<String>>>();
        this.numberFormat = NumberFormat.getInstance(Locale.GERMANY);
        this.numberFormat.setMinimumFractionDigits(1);
    }

    public void setSpecification(final YSpecification yawlSpec) {
        this.specification = yawlSpec;
        this.layout = new YLayout(yawlSpec);
    }

    public YSpecification getSpecification() {
        return specification;
    }

    public void addNet(final BasicShape shape, final YNet net) {
        netMap.put(YAWLUtils.convertYawlId(shape), net);
    }

    public YNet getNet(final BasicShape shape) {
        return netMap.get(YAWLUtils.convertYawlId(shape));
    }

    public void addFlow(final BasicShape net, final BasicEdge flow) {
        String netId = YAWLUtils.convertYawlId(net);
        if (flowMap.get(netId) != null) {
            flowMap.get(netId).add(flow);
        } else {
            Set<BasicEdge> flowSet = new HashSet<BasicEdge>();
            flowSet.add(flow);
            flowMap.put(netId, flowSet);
        }
    }

    public Set<BasicEdge> getFlowSet(final BasicShape shape) {
        String shapeId = YAWLUtils.convertYawlId(shape);
        if (flowMap.get(shapeId) != null) {
            return flowMap.get(shapeId);
        } else {
            // Create empty one
            Set<BasicEdge> flowSet = new HashSet<BasicEdge>();
            flowMap.put(shapeId, flowSet);
            return Collections.unmodifiableSet(flowSet);
        }
    }

    /**
     * Adds a Oryx Diagram of a YAWL Subnet, that will later be used to compile subnets for each composite task.
     * 
     * @param id
     *            of the YAWL subnet
     * @param subnetDiagram
     *            of a YAWL subnet used in the specification to be converted
     */
    public void addSubnetDiagram(final String id, final BasicDiagram subnetDiagram) {
        subnetDiagramMap.put(id, subnetDiagram);
    }

    public BasicDiagram getSubnetDiagram(final String id) {
        return subnetDiagramMap.get(id);
    }

    /**
     * Add the Element with ID to the Elements cancelled by task.
     * 
     * @param task
     *            the Task that cancels the Element
     * @param id
     *            of the Element to be cancelled
     */
    public void addToCancellationSet(final YTask task, final String id) {
        if (!cancellationSetMap.containsKey(task.getNet())) {
            cancellationSetMap.put(task.getNet(), new HashMap<YTask, List<String>>());
        }

        Map<YTask, List<String>> cancellationSetForNet = cancellationSetMap.get(task.getNet());

        if (!cancellationSetForNet.containsKey(task)) {
            cancellationSetForNet.put(task, new ArrayList<String>());
        }

        cancellationSetForNet.get(task).add(id);
    }

    /**
     * Get a unmodifiable view on the Cancellation Set
     * 
     * @param net
     * 
     * @return
     */
    public Set<Entry<YTask, List<String>>> getCancellationSets(final YNet net) {
        if (cancellationSetMap.get(net) != null) {
            return Collections.unmodifiableSet(cancellationSetMap.get(net).entrySet());
        } else {
            return Collections.unmodifiableSet(new HashSet<Entry<YTask, List<String>>>());
        }
    }

    public List<String> getCancellationSet(final YNet net, final YTask task) {
        if (cancellationSetMap.get(net) != null) {
            return cancellationSetMap.get(net).get(task);
        } else {
            return new ArrayList<String>();
        }
    }

    public YLayout getLayout() {
        return layout;
    }

    public NumberFormat getNumberFormat() {
        return numberFormat;
    }

    public void setRootNetID(final String rootId) {
        this.rootNetID = rootId;
    }

    public String getRootNetID() {
        return rootNetID;
    }

}
