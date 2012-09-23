/**
 * Copyright 2012, Felix Mannhardt
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.apromore.canoniser.yawl.internal.impl.context;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.apromore.anf.AnnotationType;
import org.apromore.anf.AnnotationsType;
import org.apromore.anf.DocumentationType;
import org.apromore.anf.GraphicsType;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.annotations.YAWLAutoLayouter;
import org.apromore.canoniser.yawl.internal.utils.ConversionUUIDGenerator;
import org.apromore.canoniser.yawl.internal.utils.ConversionUtils;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.InputOutputType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ObjectRefType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.TaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.ControlTypeType;
import org.yawlfoundation.yawlschema.ExternalNetElementType;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.FlowsIntoType;
import org.yawlfoundation.yawlschema.LayoutContainerFactsType;
import org.yawlfoundation.yawlschema.LayoutFactsType.Specification;
import org.yawlfoundation.yawlschema.LayoutNetFactsType;
import org.yawlfoundation.yawlschema.LayoutRectangleType;
import org.yawlfoundation.yawlschema.NetFactsType;
import org.yawlfoundation.yawlschema.SpecificationSetFactsType;
import org.yawlfoundation.yawlschema.TimerTriggerType;
import org.yawlfoundation.yawlschema.VariableBaseType;
import org.yawlfoundation.yawlschema.YAWLSpecificationFactsType;
import org.yawlfoundation.yawlschema.orgdata.OrgDataType;

/**
 * Context for a conversion CPF -> YAWL
 *
 * @author <a href="felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public final class CanonicalConversionContext extends ConversionContext {

    public class ElementInfo {
        public ExternalNetElementType element;
        public LayoutRectangleType elementSize;
        public ControlTypeType joinType;
        public ControlTypeType splitType;
        public org.yawlfoundation.yawlschema.TimerType timer;
        public boolean isAutomatic = false;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CanonicalConversionContext.class);

    private static final Locale DEFAULT_LOCALE = Locale.US;

    private final AnnotationsType annotationsType;

    private final CanonicalProcessType canonicalProcess;

    private final SpecificationSetFactsType yawlSpecification;

    private final org.yawlfoundation.yawlschema.ObjectFactory yawlObjectFactory;

    private final OrgDataType yawlOrgData;

    private final org.yawlfoundation.yawlschema.orgdata.ObjectFactory yawlOrgDataObjectFactory;

    private final Locale yawlLocale;

    private final NumberFormat yawlNumberFormat;

    private final ConversionUUIDGenerator uuidGenerator;

    /* CPF data structures */

    /**
     * Successors of each Node in the CPF
     */
    private Map<String, List<NodeType>> postsetMap;

    /**
     * Predecessors of each Node in the CPF
     */
    private Map<String, List<NodeType>> presetMap;

    /**
     * Map of all Node in CPF by their ID
     */
    private Map<String, NodeType> nodesMap;

    /**
     * Map of all Node in CPF by their ID
     */
    private Map<String, EdgeType> edgesMap;

    /**
     * Map of all ResourceType in CPF by their ID
     */
    private Map<String, ResourceTypeType> resourceTypeMap;

    /**
     * Map of all Objects in CPF by their ID
     */
    private Map<String, ObjectType> objectMap;

    /**
     * Map of all Annotations by CPF-ID
     */
    private Map<String, Collection<AnnotationType>> annotationMap;

    /* YAWL data structures */

    /**
     * Map of all already converted NetFactsTypes by their CPF ID
     */
    private Map<String, NetFactsType> convertedNetMap;

    /**
     * Map of all already converted NetElements by their CPF ID
     */
    private Map<String, ElementInfo> elementInfoMap;

    /**
     * Map of all already converted Flows by their CPF ID
     */
    private Map<String, FlowsIntoType> convertedFlowsMap;

    /**
     * Map of all already converted Variables by their Object-ID (Both Net and Task Scope variables as all Objects have unique IDs)
     */
    private Map<String, VariableBaseType> convertedParameterMap;

    /**
     * Map containing the collection of all Composite Tasks that are using the Net with ID
     */
    private Map<String, Collection<ExternalTaskFactsType>> compositeTaskMap;

    /**
     * Set of all YAWL tasks (by their YAWL ID) that have a split routing
     */
    private Set<String> splitRoutingSet;

    /**
     * Set of all YAWL tasks (by their YAWL ID) that have a join routing
     */
    private Set<String> joinRoutingSet;

    /**
     * Map of all artificial (non-existant in CPF) variables
     */
    private HashMap<String, Collection<VariableBaseType>> introducedVariableMap;

    private Map<String, TimerTriggerType> timerMap;

    private YAWLAutoLayouter autoLayoutInfo;

    /**
     * Create the Context for one CPF -> YAWL conversion
     *
     * @param canonicalProcess
     * @param annotationsType
     */
    public CanonicalConversionContext(final CanonicalProcessType canonicalProcess, final AnnotationsType annotationsType) {
        super();
        this.canonicalProcess = canonicalProcess;
        this.annotationsType = annotationsType;
        this.yawlObjectFactory = new org.yawlfoundation.yawlschema.ObjectFactory();
        this.yawlSpecification = getYawlObjectFactory().createSpecificationSetFactsType();
        this.yawlOrgDataObjectFactory = new org.yawlfoundation.yawlschema.orgdata.ObjectFactory();
        this.yawlOrgData = getYawlOrgDataObjectFactory().createOrgDataType();
        this.uuidGenerator = new ConversionUUIDGenerator();
        this.yawlLocale = DEFAULT_LOCALE;
        this.yawlNumberFormat = NumberFormat.getNumberInstance(getYawlLocale());
        this.yawlNumberFormat.setMinimumFractionDigits(1);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.internal.impl.context.ConversionContext#getUuidGenerator()
     */
    @Override
    public ConversionUUIDGenerator getUuidGenerator() {
        return uuidGenerator;
    }

    /**
     * @return CPF of the process
     */
    public CanonicalProcessType getCanonicalProcess() {
        return canonicalProcess;
    }

    /**
     * @return AND of the process
     */
    public AnnotationsType getAnnotationsType() {
        return annotationsType;
    }

    /**
     * @return the newly created YAWL specification set
     */
    public SpecificationSetFactsType getYAWLSpecificationSet() {
        return yawlSpecification;
    }

    /**
     * @return the first specification of the YAWL specification set
     */
    public YAWLSpecificationFactsType getYAWLRootSpecification() {
        // There is always just exactly one specification in a YAWL XML file
        return getYAWLSpecificationSet().getSpecification().get(0);
    }

    /**
     * @return default number format for YAWL locale
     */
    public NumberFormat getYawlNumberFormat() {
        return yawlNumberFormat;
    }

    /**
     * @return the locale of the exported YAWL XML
     */
    public Locale getYawlLocale() {
        return yawlLocale;
    }

    /**
     * @return the JAXB Object Factory for the YAWL schema
     */
    public org.yawlfoundation.yawlschema.ObjectFactory getYawlObjectFactory() {
        return yawlObjectFactory;
    }

    public OrgDataType getYawlOrgData() {
        return yawlOrgData;
    }

    public org.yawlfoundation.yawlschema.orgdata.ObjectFactory getYawlOrgDataObjectFactory() {
        return yawlOrgDataObjectFactory;
    }

    /**
     * Get Net of CPF by ID
     *
     * @param id
     *            of CPF Net
     * @return NetType or NULL
     */
    public NetType getNetById(final String id) {
        // Usually there are just a few Nets so a Map is probably not needed
        for (final NetType net : getCanonicalProcess().getNet()) {
            if (net.getId().equals(id)) {
                return net;
            }
        }
        return null;
    }

    /**
     * Get Node of CPF by ID
     *
     * @param id
     *            of CPF Node
     * @return NodeType or NULL
     */
    public NodeType getNodeById(final String id) {
        initNodesMap();
        return nodesMap.get(id);
    }

    private void initNodesMap() {
        if (nodesMap != null) {
            return;
        }
        nodesMap = new HashMap<String, NodeType>();

        for (final NetType net : getCanonicalProcess().getNet()) {
            for (final NodeType node : net.getNode()) {
                nodesMap.put(node.getId(), node);
            }
        }
    }

    /**
     * Get ResourceType of CPF by ID
     *
     * @param id
     *            of CPF ResourceType
     * @return ResourceTypeType or NULL
     */
    public ResourceTypeType getResourceTypeById(final String id) {
        initResourceTypeMap();
        return resourceTypeMap.get(id);
    }

    private void initResourceTypeMap() {
        if (resourceTypeMap != null) {
            return;
        }
        resourceTypeMap = new HashMap<String, ResourceTypeType>();

        for (final ResourceTypeType resource : getCanonicalProcess().getResourceType()) {
            resourceTypeMap.put(resource.getId(), resource);
        }
    }

    /**
     * Get ObjectType of CPF by ID
     *
     * @param id
     *            of CPF ObjectType
     * @return ObjectType or NULL
     */
    public ObjectType getObjectTypeById(final String id) {
        initObjectTypeMap();
        return objectMap.get(id);
    }

    private void initObjectTypeMap() {
        if (objectMap != null) {
            return;
        }
        objectMap = new HashMap<String, ObjectType>();

        for (final NetType net : getCanonicalProcess().getNet()) {
            for (final ObjectType obj : net.getObject()) {
                objectMap.put(obj.getId(), obj);
            }
        }
    }

    /**
     * Get Edge of CPF by ID
     *
     * @param id
     *            of CPF Edge
     * @return EdgeType or NULL
     */
    public EdgeType getEdgeById(final String id) {
        initEdgesMap();
        return edgesMap.get(id);
    }

    private void initEdgesMap() {
        if (edgesMap != null) {
            return;
        }
        edgesMap = new HashMap<String, EdgeType>();

        for (final NetType net : getCanonicalProcess().getNet()) {
            for (final EdgeType edge : net.getEdge()) {
                edgesMap.put(edge.getId(), edge);
            }
        }
    }

    /**
     * Get all preceding elements of the Node with given ID. (i.e. the pre set)
     *
     * @param id
     *            of a CPF Node
     * @return UnmodifiableCollection of predecessors, empty if Node has none.
     */
    public List<NodeType> getPreSet(final String id) {
        initNeighborsMap();
        final List<NodeType> collection = presetMap.get(id);
        if (collection != null) {
            return Collections.unmodifiableList(collection);
        } else {
            return Collections.unmodifiableList(new ArrayList<NodeType>());
        }
    }

    /**
     * Get all succeeding elements of the Node with given ID. (i.e. the post set)
     *
     * @param id
     *            of a CPF Node
     * @return UnmodifiableCollection of successors, empty if Node has none.
     */
    public List<NodeType> getPostSet(final String id) {
        initNeighborsMap();
        final List<NodeType> collection = postsetMap.get(id);
        if (collection != null) {
            return Collections.unmodifiableList(collection);
        } else {
            return Collections.unmodifiableList(new ArrayList<NodeType>());
        }
    }

    private void initNeighborsMap() {
        if (presetMap != null && postsetMap != null) {
            return;
        }

        presetMap = new HashMap<String, List<NodeType>>();
        postsetMap = new HashMap<String, List<NodeType>>();

        for (final NetType net : getCanonicalProcess().getNet()) {
            for (final EdgeType edge : net.getEdge()) {
                final String sourceId = edge.getSourceId();
                final String targetId = edge.getTargetId();
                NodeType preNode = getNodeById(sourceId);
                if (preNode != null) {
                    addToPreSet(targetId, preNode);
                } else {
                    LOGGER.warn("Could not find target {} for edge {}", targetId, edge.getId());
                }
                NodeType postNode = getNodeById(targetId);
                if (postNode != null) {
                    addToPostSet(sourceId, postNode);
                } else {
                    LOGGER.warn("Could not find source {} for edge {}", sourceId, edge.getId());
                }
            }
        }
    }

    private void addToPostSet(final String sourceId, final NodeType node) {
        List<NodeType> nodeCollection = postsetMap.get(sourceId);
        if (nodeCollection == null) {
            nodeCollection = new ArrayList<NodeType>();
            postsetMap.put(sourceId, nodeCollection);
        }
        nodeCollection.add(node);
    }

    private void addToPreSet(final String targetId, final NodeType node) {
        List<NodeType> nodeCollection = presetMap.get(targetId);
        if (nodeCollection == null) {
            nodeCollection = new ArrayList<NodeType>();
            presetMap.put(targetId, nodeCollection);
        }
        nodeCollection.add(node);
    }

    /**
     * Get the first Successor of CPF Node with given ID.
     *
     * @param id
     *            of a CPF Node
     * @return NodeType or NULL is no successor found
     */
    public NodeType getFirstSuccessor(final String id) {
        if (!getPostSet(id).isEmpty()) {
            return getPostSet(id).get(0);
        } else {
            return null;
        }
    }

    /**
     * Get the first Predecessor of CPF Node with given ID.
     *
     * @param id
     *            of a CPF Node
     * @return NodeType or NULL is no predecessor found
     */
    public NodeType getFirstPredecessor(final String id) {
        if (!getPreSet(id).isEmpty()) {
            return getPreSet(id).get(0);
        } else {
            return null;
        }
    }

    /**
     * Checks if the given CPF Net has more than one Node that has no successors. Such a Net has multiple exit nodes and is Multiple Exit.
     *
     * @param net
     *            a CPF net
     * @return true if Net is MESE or MEME, false otherwise
     */
    public boolean hasMultipleExits(final NetType net) {
        return getSinkNodes(net).size() > 1;
    }

    /**
     * Get the Nodes of a CPF Net that just have one predecessor.
     *
     * @param net
     *            the CPF Net
     * @return Unmodifiable Collection of entry Nodes
     */
    public Collection<NodeType> getSinkNodes(final NetType net) {
        final Collection<NodeType> exitNodes = new ArrayList<NodeType>();
        for (final NodeType node : net.getNode()) {
            if (getPostSet(node.getId()).isEmpty()) {
                exitNodes.add(node);
            }
        }
        return Collections.unmodifiableCollection(exitNodes);
    }

    /**
     * Checks if the given CPF Net has more than one Node that has no predecessors. Such a Net has multiple starting nodes and is Multiple Entry.
     *
     * @param net
     *            the CPF Net
     * @return true if Net is SEME or MEME, false otherwise
     */
    public boolean hasMultipleEntries(final NetType net) {
        return getSourceNodes(net).size() > 1;
    }

    /**
     * Get the Nodes of a CPF Net that don't have a predecessor.
     *
     * @param net
     *            the CPF Net
     * @return Unmodifiable Collection of entry Nodes
     */
    public Collection<NodeType> getSourceNodes(final NetType net) {
        final Collection<NodeType> entryNodes = new ArrayList<NodeType>();
        for (final NodeType node : net.getNode()) {
            if (getPreSet(node.getId()).isEmpty()) {
                entryNodes.add(node);
            }
        }
        return Collections.unmodifiableCollection(entryNodes);
    }

    /**
     * Clears all caches that store information about the CPF. Must be called every time the CPF is changed!
     */
    public void invalidateCPFCaches() {
        LOGGER.debug("Invalidate CPF caches");
        this.presetMap = null;
        this.postsetMap = null;
        this.nodesMap = null;
        this.edgesMap = null;
        this.objectMap = null;
        this.resourceTypeMap = null;
    }

    /**
     * Add a NetFactsType for the given ID
     *
     * @param id
     *            of the CPF Net
     * @param net
     *            the converted NetFactsType of YAWL
     */
    public void addConvertedNet(final String id, final NetFactsType net) {
        initNetMap();
        convertedNetMap.put(id, net);
    }

    /**
     * Get the already converted NetFactsType if CPF Net with ID is already converted.
     *
     * @param id
     *            of a CPF Net
     * @return NetFactsType or NULL
     */
    public NetFactsType getConvertedNet(final String id) {
        initNetMap();
        return convertedNetMap.get(id);
    }

    public Set<Entry<String, NetFactsType>> getConvertedNets() {
        return Collections.unmodifiableSet(convertedNetMap.entrySet());
    }

    private void initNetMap() {
        if (convertedNetMap == null) {
            convertedNetMap = new HashMap<String, NetFactsType>();
        }
    }

    /**
     * Remembers a YAWL variable/parameter
     *
     * @param id
     *            of the CPF Object
     * @param inputParam
     */
    public void addConvertedParameter(final String id, final VariableBaseType param) {
        initConvertedParameterMap();
        convertedParameterMap.put(id, param);
    }

    /**
     * Get a YAWL variable/parameter by the CPF Object-ID
     *
     * @param id
     *            of the CPF Object
     * @return VariableBaseType
     */
    public VariableBaseType getConvertedParameter(final String id) {
        initConvertedParameterMap();
        return convertedParameterMap.get(id);
    }

    private void initConvertedParameterMap() {
        if (convertedParameterMap == null) {
            convertedParameterMap = new HashMap<String, VariableBaseType>();
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
        initElementMap(nodeId).element = element;
    }

    /**
     * Adds layout bounds to the element information.
     *
     * @param nodeId
     *            of the CPF node
     * @param elementSize
     */
    public void setElementBounds(final String nodeId, final LayoutRectangleType elementSize) {
        initElementMap(nodeId).elementSize = elementSize;
    }

    /**
     * Adds the join type to the element information.
     *
     * @param nodeId
     *            of the CPF node
     * @param joinType
     */
    public void setElementJoinType(final String nodeId, final ControlTypeType joinType) {
        initElementMap(nodeId).joinType = joinType;
    }

    /**
     * Adds the split type to the element information.
     *
     * @param nodeId
     *            of the CPF node
     * @param splitType
     */
    public void setElementSplitType(final String nodeId, final ControlTypeType splitType) {
        initElementMap(nodeId).splitType = splitType;
    }

    /**
     * Get information about the conversion of the CPF Node to an YAWL element.
     *
     * @param nodeId
     *            of CPF node
     * @return YAWL element information
     */
    public ElementInfo getElementInfo(final String nodeId) {
        return initElementMap(nodeId);
    }

    private ElementInfo initElementMap(final String nodeId) {
        if (elementInfoMap == null) {
            elementInfoMap = new HashMap<String, ElementInfo>();
        }
        final ElementInfo elemenInfo = elementInfoMap.get(nodeId);
        if (elemenInfo != null) {
            return elemenInfo;
        } else {
            final ElementInfo newElementInfo = new ElementInfo();
            elementInfoMap.put(nodeId, newElementInfo);
            return newElementInfo;
        }

    }

    /**
     * Add an flow that just has been converted
     *
     * @param nodeId
     *            of the CPF node
     * @param element
     *            the converted YAWL element
     */
    public void addConvertedFlow(final String edgeId, final FlowsIntoType flow) {
        initConvertedFlowsMap();
        convertedFlowsMap.put(edgeId, flow);
    }

    /**
     * Gets the converted flow for edge CPF id
     *
     * @param nodeId
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
     * Get the already converted LayoutNetFactsType for the Net with provided YAWL ID.
     *
     * @param id
     *            of YAWL Net
     * @return LayoutNetFactsType or NULL is none found
     */
    public LayoutNetFactsType getConvertedNetLayout(final String id) {
        // Usually there are just a few Nets so a Map is probably not needed
        final Specification specification = getYAWLSpecificationSet().getLayout().getSpecification().get(0);
        for (final LayoutNetFactsType netLayout : specification.getNet()) {
            if (netLayout.getId().equals(id)) {
                return netLayout;
            }
        }
        return null;
    }

    /**
     * Get the already converted LayoutContainerFactsType for the Element with provided YAWL ID.
     *
     * @param id
     *            of YAWL Element
     * @return LayoutContainerFactsType or NULL is none found
     */
    public LayoutContainerFactsType getConvertedElementLayout(final String id) {
        // TODO optimize with Map
        final Specification specification = getYAWLSpecificationSet().getLayout().getSpecification().get(0);
        for (final LayoutNetFactsType netLayout : specification.getNet()) {
            for (final JAXBElement<?> element : netLayout.getBoundsOrFrameOrViewport()) {
                if (element.getValue() instanceof LayoutContainerFactsType) {
                    final LayoutContainerFactsType container = (LayoutContainerFactsType) element.getValue();
                    if (container.getId().equals(id)) {
                        return container;
                    }
                }
            }
        }
        return null;
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
            collection = new ArrayList<ExternalTaskFactsType>();
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

    /**
     * Checks if the Object with ID (in CPF) is used as input any composite task that points to the given Net
     *
     * @param objectId
     * @return
     */
    public boolean isInputObjectForNet(final String objectId, final String netId) {
        // TODO optimize
        for (final NetType net : getCanonicalProcess().getNet()) {
            for (final NodeType node : net.getNode()) {
                if (node instanceof TaskType) {
                    final TaskType task = (TaskType) node;
                    if (task.getSubnetId() != null && ((TaskType) node).getSubnetId().equals(netId)) {
                        for (final ObjectRefType ref : task.getObjectRef()) {
                            if (ref.getObjectId().equals(objectId)) {
                                return ref.getType() == InputOutputType.OUTPUT;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks if the Object with ID (in CPF) is used as output in any composite task that points to the given Net
     *
     * @param objectId
     * @param netId
     * @return
     */
    public boolean isOutputObjectOfNet(final String objectId, final String netId) {
        // TODO optimize
        for (final NetType net : getCanonicalProcess().getNet()) {
            for (final NodeType node : net.getNode()) {
                if (node instanceof TaskType) {
                    final TaskType task = (TaskType) node;
                    if (task.getSubnetId() != null && ((TaskType) node).getSubnetId().equals(netId)) {
                        // Task has given Net as decomposition
                        for (final ObjectRefType ref : task.getObjectRef()) {
                            if (ref.getObjectId().equals(objectId)) {
                                return ref.getType() == InputOutputType.INPUT;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Remembers a Variable that needs to be added to a sub net later on.
     *
     * @param subnetId
     *            CPF id of the Net
     * @param var
     *            is in fact an input or output parameter
     */
    public void addIntroducedVariable(final String subnetId, final VariableBaseType var) {
        final Collection<VariableBaseType> variables = initIntroducedVariableMap(subnetId);
        variables.add(var);
    }

    /**
     * Get all Variables that need to be added to this sub net.
     *
     * @param subnetId
     *            CPF id of the Net
     * @return unmodifiableCollection of VariableBaseType
     */
    public Collection<VariableBaseType> getIntroducedVariables(final String subnetId) {
        return Collections.unmodifiableCollection(initIntroducedVariableMap(subnetId));
    }

    private Collection<VariableBaseType> initIntroducedVariableMap(final String subnetId) {
        if (introducedVariableMap == null) {
            introducedVariableMap = new HashMap<String, Collection<VariableBaseType>>();
        }
        Collection<VariableBaseType> variablesForNet = introducedVariableMap.get(subnetId);
        if (variablesForNet == null) {
            variablesForNet = new ArrayList<VariableBaseType>();
            introducedVariableMap.put(subnetId, variablesForNet);
        }
        return variablesForNet;
    }

    public <T> T getYAWLExtensionFromAnnotations(final String cpfId, final String elementName, final Class<T> expectedClass) {
        for (final AnnotationType ann : getAnnotations(cpfId)) {
            if (!(ann instanceof DocumentationType || ann instanceof GraphicsType)) {
                for (final Object extObj : ann.getAny()) {
                    try {
                        if (ConversionUtils.isValidFragment(extObj, ConversionUtils.YAWLSCHEMA_URL, elementName)) {
                            return ConversionUtils.unmarshalYAWLFragment(extObj, expectedClass);
                        }
                    } catch (final JAXBException e) {
                        LOGGER.warn("Could not convert YAWL extension {} of Node {} with type {}",
                                new String[] { elementName, cpfId, expectedClass.getSimpleName() });
                        LOGGER.warn("Original Exception", e);
                    }
                }
            }
        }
        return null;
    }

    public Collection<AnnotationType> getAnnotations(final String cpfId) {
        initialiseAnnotationsMap();
        Collection<AnnotationType> annotationList = annotationMap.get(cpfId);
        if (annotationList == null) {
            annotationList = new ArrayList<AnnotationType>();
            for (final AnnotationType annotation : getAnnotationsType().getAnnotation()) {
                annotationList.add(annotation);
            }
            annotationMap.put(cpfId, annotationList);
        }
        return annotationList;
    }

    private void initialiseAnnotationsMap() {
        if (annotationMap == null) {
            annotationMap = new HashMap<String, Collection<AnnotationType>>();
        }
    }

    public YAWLAutoLayouter getAutoLayoutInfo() {
        if (autoLayoutInfo == null) {
            autoLayoutInfo = new YAWLAutoLayouter(getYawlNumberFormat());
        }
        return autoLayoutInfo;
    }

    public void setOnEnablementTimer(final String id) {
        initTimerMap();
        timerMap.put(id, TimerTriggerType.ON_ENABLED);
    }

    public void setOnStartTimer(final String id) {
        initTimerMap();
        timerMap.put(id, TimerTriggerType.ON_EXECUTING);
    }

    public boolean isOnEnablementTimer(final String id) {
        initTimerMap();
        return timerMap.get(id) == TimerTriggerType.ON_ENABLED;
    }

    public boolean isOnStartTimer(final String id) {
        initTimerMap();
        return timerMap.get(id) == TimerTriggerType.ON_EXECUTING;
    }

    private void initTimerMap() {
        if (timerMap == null) {
            timerMap = new HashMap<String, TimerTriggerType>();
        }
    }

}
