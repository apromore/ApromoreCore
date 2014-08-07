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
package org.apromore.canoniser.yawl.internal.impl.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.yawlfoundation.yawlschema.ControlTypeType;
import org.yawlfoundation.yawlschema.DecompositionFactsType;
import org.yawlfoundation.yawlschema.ExternalNetElementType;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.FlowsIntoType;
import org.yawlfoundation.yawlschema.LayoutRectangleType;
import org.yawlfoundation.yawlschema.NetFactsType;

/**
 * Context information about the control flow perspective of a CPF -> YAWL conversion.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 *
 */
public class CanonicalControlFlowConversionContext {
    /**
     * Map of all already converted DecompositionFactsType by their CPF ID
     */
    private Map<String, DecompositionFactsType> convertedDecompositionMap;
    /**
     * Map containing the collection of all Composite Tasks that are using the Net with ID
     */
    private Map<String, Collection<ExternalTaskFactsType>> compositeTaskMap;
    /**
     * Map of all already converted NetElements by their CPF ID
     */
    private Map<String, CanonicalToYAWLElementInfo> elementInfoMap;
    /**
     * Map of all already converted Flows by their CPF ID
     */
    private Map<String, FlowsIntoType> convertedFlowsMap;
    /**
     * Set of all YAWL tasks (by their YAWL ID) that have a split routing
     */
    private Set<String> splitRoutingSet;
    /**
     * Set of all YAWL tasks (by their YAWL ID) that have a join routing
     */
    private Set<String> joinRoutingSet;

    public CanonicalControlFlowConversionContext() {
    }

    /**
     * Add a DecompositionFactsType for the given ID
     *
     * @param id
     *            of the CPF Net or CPF Task
     * @param decomposition
     *            the converted NetFactsType or WebServiceGatewayFactsType of YAWL
     */
    public void addConvertedDecompositon(final String id, final DecompositionFactsType decomposition) {
        initDecompositionMap();
        convertedDecompositionMap.put(id, decomposition);
    }

    /**
     * Get the already converted NetFactsType if CPF Net with ID is already converted.
     *
     * @param elementId
     *            of a CPF Net or a CPF Task
     * @return NetFactsType or WebServiceGatewayFactsType or NULL
     */
    public DecompositionFactsType getConvertedDecomposition(final String elementId) {
        initDecompositionMap();
        return convertedDecompositionMap.get(elementId);
    }

    /**
     * Get all converted YAWL NetFactsType
     *
     * @return Set of NetFactsType and their IDs
     */
    public Set<Entry<String, NetFactsType>> getConvertedNets() {
        Map<String, NetFactsType> netMap = new HashMap<String, NetFactsType>();
        for (Entry<String, DecompositionFactsType> d: convertedDecompositionMap.entrySet()) {
            if (d.getValue() instanceof NetFactsType) {
                netMap.put(d.getKey(), (NetFactsType) d.getValue());
            }
        }
        return Collections.unmodifiableSet(netMap.entrySet());
    }

    private void initDecompositionMap() {
        if (convertedDecompositionMap == null) {
            convertedDecompositionMap = new HashMap<String, DecompositionFactsType>();
        }
    }


    /**
     * Add an element that just has been converted
     *
     * @param nodeId
     *            of the CPF node
     * @param element
     *            the converted YAWL element
     */
    public void setElement(final String nodeId, final ExternalNetElementType element) {
        initElementMap(nodeId).setElement(element);
    }

    /**
     * Adds layout bounds to the element information.
     *
     * @param nodeId
     *            of the CPF node
     * @param elementSize
     */
    public void setElementBounds(final String nodeId, final LayoutRectangleType elementSize) {
        initElementMap(nodeId).setElementSize(elementSize);
    }

    /**
     * Adds the join type to the element information.
     *
     * @param nodeId
     *            of the CPF node
     * @param joinType
     */
    public void setElementJoinType(final String nodeId, final ControlTypeType joinType) {
        initElementMap(nodeId).setJoinType(joinType);
    }

    /**
     * Adds the split type to the element information.
     *
     * @param nodeId
     *            of the CPF node
     * @param splitType
     */
    public void setElementSplitType(final String nodeId, final ControlTypeType splitType) {
        initElementMap(nodeId).setSplitType(splitType);
    }

    /**
     * Get information about the conversion of the CPF Node to an YAWL element.
     *
     * @param nodeId
     *            of CPF node
     * @return YAWL element information
     */
    public CanonicalToYAWLElementInfo getElementInfo(final String nodeId) {
        return initElementMap(nodeId);
    }

    private CanonicalToYAWLElementInfo initElementMap(final String nodeId) {
        if (elementInfoMap == null) {
            elementInfoMap = new HashMap<String, CanonicalToYAWLElementInfo>();
        }
        final CanonicalToYAWLElementInfo elemenInfo = elementInfoMap.get(nodeId);
        if (elemenInfo != null) {
            return elemenInfo;
        } else {
            final CanonicalToYAWLElementInfo newElementInfo = new CanonicalToYAWLElementInfo();
            elementInfoMap.put(nodeId, newElementInfo);
            return newElementInfo;
        }

    }

    /**
     * Add an flow that just has been converted
     *
     * @param edgeId
     *            of the CPF edge
     * @param flow
     *            the converted YAWL flow
     */
    public void addConvertedFlow(final String edgeId, final FlowsIntoType flow) {
        initConvertedFlowsMap();
        convertedFlowsMap.put(edgeId, flow);
    }

    /**
     * Gets the converted flow for edge CPF id
     *
     * @param edgeId
     *            of CPF edge
     * @return YAWL flow
     */
    public FlowsIntoType getConvertedFlow(final String edgeId) {
        initConvertedFlowsMap();
        return convertedFlowsMap.get(edgeId);
    }

    public Set<Entry<String, FlowsIntoType>> getConvertedFlows() {
        return Collections.unmodifiableSet(convertedFlowsMap.entrySet());
    }

    private void initConvertedFlowsMap() {
        if (convertedFlowsMap == null) {
            convertedFlowsMap = new HashMap<String, FlowsIntoType>();
        }
    }

    /**
     * Adds the task element to a list of composite tasks that unfold to the sub net with given ID.
     *
     * @param subnetId
     *            of CPF net
     * @param taskFacts
     *            the YAWL element
     */
    public void addCompositeTask(final String subnetId, final ExternalTaskFactsType taskFacts) {
        initCompositeTaskMap();
        Collection<ExternalTaskFactsType> collection = compositeTaskMap.get(subnetId);
        if (collection == null) {
            collection = new ArrayList<ExternalTaskFactsType>(0);
            compositeTaskMap.put(subnetId, collection);
        }
        collection.add(taskFacts);
    }

    /**
     * Returns a collection of already converted YAWL tasks that unfold to a given sub net. There may be tasks missing that have not been converted
     * yet.
     *
     * @param subnetId
     *            of the CPF net
     * @return unmodifiable collection of YAWL tasks that unfold to this sub net
     */
    public Collection<ExternalTaskFactsType> getCompositeTasks(final String subnetId) {
        initCompositeTaskMap();
        final Collection<ExternalTaskFactsType> collection = compositeTaskMap.get(subnetId);
        if (collection != null) {
            return Collections.unmodifiableCollection(collection);
        } else {
            return Collections.unmodifiableCollection(new ArrayList<ExternalTaskFactsType>());
        }
    }

    private void initCompositeTaskMap() {
        if (compositeTaskMap == null) {
            compositeTaskMap = new HashMap<String, Collection<ExternalTaskFactsType>>();
        }
    }

    /**
     * Remember that a YAWL task (given by its ID) has a JOIN routing attached.
     *
     * @param yawlId
     */
    public void setSplitRouting(final String yawlId) {
        initSplitRoutingMap();
        splitRoutingSet.add(yawlId);
    }

    /**
     *
     * @param yawlId
     * @return true if Task has a SPLIT routing
     */
    public boolean hasSplitRouting(final String yawlId) {
        initSplitRoutingMap();
        return splitRoutingSet.contains(yawlId);
    }

    private void initSplitRoutingMap() {
        if (splitRoutingSet == null) {
            splitRoutingSet = new HashSet<String>();
        }
    }

    /**
     * Remember that a YAWL task (given by its ID) has a JOIN routing attached.
     *
     * @param yawlId
     */
    public void setJoinRouting(final String yawlId) {
        initJoinRoutingMap();
        joinRoutingSet.add(yawlId);
    }

    /**
     * @param yawlId
     * @return true if Task has a JOIN routing
     */
    public boolean hasJoinRouting(final String yawlId) {
        initJoinRoutingMap();
        return joinRoutingSet.contains(yawlId);
    }

    private void initJoinRoutingMap() {
        if (joinRoutingSet == null) {
            joinRoutingSet = new HashSet<String>();
        }
    }
}