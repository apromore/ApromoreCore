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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBElement;

import org.apromore.anf.AnnotationType;
import org.apromore.anf.AnnotationsType;
import org.apromore.anf.DocumentationType;
import org.apromore.anf.GraphicsType;
import org.apromore.canoniser.yawl.internal.MessageManager;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ObjectFactory;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.SoftType;
import org.w3c.dom.Element;
import org.yawlfoundation.yawlschema.DecompositionType;
import org.yawlfoundation.yawlschema.ExternalNetElementFactsType;
import org.yawlfoundation.yawlschema.ExternalNetElementType;
import org.yawlfoundation.yawlschema.FlowsIntoType;
import org.yawlfoundation.yawlschema.LayoutContainerFactsType;
import org.yawlfoundation.yawlschema.LayoutDecoratorFactsType;
import org.yawlfoundation.yawlschema.LayoutFactsType;
import org.yawlfoundation.yawlschema.LayoutFactsType.Specification;
import org.yawlfoundation.yawlschema.LayoutFlowFactsType;
import org.yawlfoundation.yawlschema.LayoutLabelFactsType;
import org.yawlfoundation.yawlschema.LayoutLocaleType;
import org.yawlfoundation.yawlschema.LayoutNetFactsType;
import org.yawlfoundation.yawlschema.LayoutVertexFactsType;
import org.yawlfoundation.yawlschema.NetFactsType;
import org.yawlfoundation.yawlschema.NetFactsType.ProcessControlElements;
import org.yawlfoundation.yawlschema.YAWLSpecificationFactsType;
import org.yawlfoundation.yawlschema.orgdata.NonHumanResourceType;
import org.yawlfoundation.yawlschema.orgdata.OrgDataType;
import org.yawlfoundation.yawlschema.orgdata.ParticipantType;
import org.yawlfoundation.yawlschema.orgdata.RoleType;

/**
 * Context for a conversion from YAWL to Apromores canonical format. This is the glue for all handlers.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public final class YAWLConversionContext extends ConversionContext {

    public class ConvertedResource {
        private ResourceTypeType resource;

        public ResourceTypeType getResource() {
            return resource;
        }

        public void setResource(final ResourceTypeType resource) {
            this.resource = resource;
        }
    }

    // ************* Result & Factories *******************

    /**
     * Stores the converted AnnotationType
     */
    private final AnnotationsType annotationResult;

    /**
     * Stores the converted CanonicalProcessType
     */
    private final CanonicalProcessType canonicalResult;

    // ************ YAWL Specification & Organisational Data ***************

    /**
     * Stores the input YAWL specification
     */
    private YAWLSpecificationFactsType specification;

    /**
     * Stores the input YAWL layout
     */
    private Specification specificationLayout;

    /**
     * Stores the locale used in the YAWL layout
     */
    private LayoutLocaleType layoutLocaleElement;

    /**
     * Stores the input organisational model
     */
    private OrgDataType orgDataType;

    /**
     * Number format which is used by YAWL in XML
     */
    private NumberFormat numberFormat;

    // / ************ Lookup Maps **************************

    /**
     * Layout Information for each YAWL element by its ID
     */
    private Map<String, Object> layoutMap;

    /**
     * Successors of each Node in the whole YAWL specification.
     */
    private Map<ElementAdapter, Collection<ExternalNetElementType>> postSetMap;

    /**
     * Predeccessors of each NetElement in the whole YAWL specification
     */
    private Map<ElementAdapter, Collection<ExternalNetElementType>> preSetMap;

    /**
     * Map with yet to be added predecessors per YAWL element
     */
    private Map<ElementAdapter, Collection<PredecessorAdapater>> incomingQueueMap;

    /**
     * Map containing proceeding nodes of the elements, that are introduced during the conversion process
     */
    private Map<ElementAdapter, NodeType> introducedPredecessorMap;

    /**
     * Map of Roles by ID
     */
    private Map<String, RoleType> roleMap;

    /**
     * Map of Decompositions by ID
     */
    private Map<String, DecompositionType> decompositionMap;

    /**
     * Map of all generated Resources
     */
    private Map<String, ConvertedResource> generatedResourceTypeMap;

    /**
     * Map of Objects by Net and Name
     */
    private Map<String, Map<String, SoftType>> netObjectsByName;

    public YAWLConversionContext(final YAWLSpecificationFactsType specification, final LayoutFactsType layoutFactsType,
            final OrgDataType orgDataType, final MessageManager messageManager) {
        super(messageManager);
        this.setOrgDataType(orgDataType);
        this.setLayout(null);
        this.setLayout(layoutFactsType);
        this.setSpecification(specification);
        this.canonicalResult = new ObjectFactory().createCanonicalProcessType();
        this.annotationResult = new org.apromore.anf.ObjectFactory().createAnnotationsType();
    }

    public AnnotationsType getAnnotationResult() {
        return annotationResult;
    }

    public CanonicalProcessType getCanonicalResult() {
        return canonicalResult;
    }

    /**
     * Gets the layout element of a YAWL net
     *
     * @param id of YAWL net
     * @return layout of YAWL net or NULL if not found
     */
    public LayoutNetFactsType getLayoutForNet(final String id) {
        for (final LayoutNetFactsType netLayout : getSpecificationLayout().getNet()) {
            if (netLayout.getId().equals(id)) {
                return netLayout;
            }
        }
        return null;
    }

    private void setLayout(final LayoutFactsType layout) {
        if (layout != null) {
            this.setLayoutLocaleElement(layout.getLocale());
            this.setNumberFormat(NumberFormat.getInstance(getLayoutLocale()));
            this.specificationLayout = layout.getSpecification().get(0);
        } else {
            LayoutLocaleType locale = new LayoutLocaleType();
            locale.setCountry("AU");
            locale.setLanguage("en");
            this.setLayoutLocaleElement(locale);
            this.setNumberFormat(NumberFormat.getInstance(Locale.ENGLISH));
            this.specificationLayout = new Specification();

        }
    }

    private void setNumberFormat(final NumberFormat numberFormat) {
        this.numberFormat = numberFormat;
    }

    /**
     * @return NumberFormat according to the YAWL layout XML locale
     */
    public NumberFormat getNumberFormat() {
        return numberFormat;
    }

    private void setLayoutLocaleElement(final LayoutLocaleType layoutLocale) {
        this.layoutLocaleElement = layoutLocale;
    }

    /**
     * @return locale element from YAWL layout XML
     */
    public LayoutLocaleType getLayoutLocaleElement() {
        return layoutLocaleElement;
    }

    /**
     * @return locale from YAWL layout XML converted to Java Locale
     */
    public Locale getLayoutLocale() {
        return new Locale(layoutLocaleElement.getLanguage(), layoutLocaleElement.getCountry());
    }

    private void setSpecification(final YAWLSpecificationFactsType specification) {
        this.specification = specification;
    }

    /**
     * @return the to be converted YAWL specification
     */
    public YAWLSpecificationFactsType getSpecification() {
        return specification;
    }

    private void setOrgDataType(final OrgDataType orgDataType) {
        if (orgDataType == null) {
            this.orgDataType = new OrgDataType();
        } else {
            this.orgDataType = orgDataType;
        }
    }

    /**
     * @return the layout of the to be converted YAWL specification
     */
    public Specification getSpecificationLayout() {
        return specificationLayout;
    }

    public OrgDataType getOrgDataType() {
        return orgDataType;
    }

    /**
     * Get the successors of this YAWL element, will be initialised on the first call of this methods.
     *
     * @param netElement
     *            any element of a YAWL net
     * @return post set of YAWL element as unmodifiable Collection
     */
    public Collection<ExternalNetElementType> getPostSet(final ExternalNetElementType netElement) {
        if (postSetMap == null) {
            initNeighborsMap();
        }
        Collection<ExternalNetElementType> c = postSetMap.get(new ElementAdapter(netElement));
        if (c == null) {
            c = new ArrayList<ExternalNetElementType>();
        }
        return Collections.unmodifiableCollection(c);
    }

    /**
     * Get the predecessors of this YAWL element, will be initialized on the first call of this method.
     *
     * @param netElement
     *            any element of a YAWL net
     * @return pre set of YAWL element as unmodifiable Collection
     */
    public Collection<ExternalNetElementType> getPreSet(final ExternalNetElementType netElement) {
        if (preSetMap == null) {
            initNeighborsMap();
        }
        Collection<ExternalNetElementType> c = preSetMap.get(new ElementAdapter(netElement));
        if (c == null) {
            c = new ArrayList<ExternalNetElementType>();
        }
        return Collections.unmodifiableCollection(c);
    }

    private void initNeighborsMap() {
        postSetMap = new HashMap<ElementAdapter, Collection<ExternalNetElementType>>();
        preSetMap = new HashMap<ElementAdapter, Collection<ExternalNetElementType>>();

        // First get successors
        for (final DecompositionType d : getSpecification().getDecomposition()) {
            // Only NetFactsType contains Elements
            if (d instanceof NetFactsType) {
                final ProcessControlElements netElements = ((NetFactsType) d).getProcessControlElements();
                postSetMap.put(new ElementAdapter(netElements.getInputCondition()), initPostSet(netElements.getInputCondition()));
                // No need to initalise for OutputCondition as it has no successors
                for (final ExternalNetElementFactsType element : netElements.getTaskOrCondition()) {
                    postSetMap.put(new ElementAdapter(element), initPostSet(element));
                }
            }
        }

        // Now caluclate predecessors based on the successor map
        for (final Entry<ElementAdapter, Collection<ExternalNetElementType>> entry : postSetMap.entrySet()) {
            final ElementAdapter currentElement = entry.getKey();
            // For all our successors
            for (final ExternalNetElementType successor : entry.getValue()) {
                final Collection<ExternalNetElementType> predecessors = initPreSet(successor);
                // Add ourself as predecessors
                predecessors.add(currentElement.getObj());
            }
        }
    }

    private Collection<ExternalNetElementType> initPreSet(final ExternalNetElementType element) {
        // Get Collection of already added predecessors
        Collection<ExternalNetElementType> predecessors = preSetMap.get(new ElementAdapter(element));
        if (predecessors == null) {
            // Create Collection if this is the first predecessor
            predecessors = new ArrayList<ExternalNetElementType>(0);
            preSetMap.put(new ElementAdapter(element), predecessors);
        }
        return predecessors;
    }

    private Collection<ExternalNetElementType> initPostSet(final ExternalNetElementFactsType netElement) {
        final Collection<ExternalNetElementType> successors = new ArrayList<ExternalNetElementType>(0);
        for (final FlowsIntoType f : netElement.getFlowsInto()) {
            successors.add(f.getNextElementRef());
        }
        return successors;
    }

    public LayoutVertexFactsType getLayoutVertexForElement(final String id) {
        initLayoutMap();
        final Object layout = layoutMap.get(id);
        if (layout != null) {
            if (layout instanceof LayoutVertexFactsType) {
                return (LayoutVertexFactsType) layout;
            } else if (layout instanceof LayoutContainerFactsType) {
                final LayoutContainerFactsType container = (LayoutContainerFactsType) layout;
                for (final Object obj : container.getVertexOrLabelOrDecorator()) {
                    if (obj instanceof LayoutVertexFactsType) {
                        return (LayoutVertexFactsType) obj;
                    }
                }
            }
        }
        return null;
    }

    public LayoutLabelFactsType getLayoutLabelForElement(final String id) {
        initLayoutMap();
        final Object layout = layoutMap.get(id);
        if (layout != null) {
            if (layout instanceof LayoutVertexFactsType) {
                return null;
            } else if (layout instanceof LayoutContainerFactsType) {
                final LayoutContainerFactsType container = (LayoutContainerFactsType) layout;
                for (final Object obj : container.getVertexOrLabelOrDecorator()) {
                    if (obj instanceof LayoutLabelFactsType) {
                        return (LayoutLabelFactsType) obj;
                    }
                }
            }
        }
        return null;
    }

    public Collection<LayoutDecoratorFactsType> getLayoutDecoratorForElement(final String id) {
        initLayoutMap();
        final Object layout = layoutMap.get(id);
        if (layout != null) {
            if (layout instanceof LayoutVertexFactsType) {
                return null;
            } else if (layout instanceof LayoutContainerFactsType) {
                final LayoutContainerFactsType container = (LayoutContainerFactsType) layout;
                final Collection<LayoutDecoratorFactsType> resultList = new ArrayList<LayoutDecoratorFactsType>(0);
                for (final Object obj : container.getVertexOrLabelOrDecorator()) {
                    if (obj instanceof LayoutDecoratorFactsType) {
                        resultList.add((LayoutDecoratorFactsType) obj);
                    }
                }
                if (!resultList.isEmpty()) {
                    return resultList;
                }
            }
        }
        return null;
    }

    public LayoutFlowFactsType getLayoutFlow(final String id) {
        initLayoutMap();
        final Object layout = layoutMap.get(id);
        if (layout != null) {
            return (LayoutFlowFactsType) layout;
        }
        return null;
    }

    private void initLayoutMap() {
        if (layoutMap == null) {
            layoutMap = new HashMap<String, Object>();
            for (final LayoutNetFactsType netLayout : getSpecificationLayout().getNet()) {
                for (final JAXBElement<?> elementLayout : netLayout.getBoundsOrFrameOrViewport()) {
                    if (elementLayout.getValue() instanceof LayoutContainerFactsType) {
                        final LayoutContainerFactsType container = (LayoutContainerFactsType) elementLayout.getValue();
                        layoutMap.put(container.getId(), container);
                    } else if (elementLayout.getValue() instanceof LayoutVertexFactsType) {
                        final LayoutVertexFactsType vertex = (LayoutVertexFactsType) elementLayout.getValue();
                        layoutMap.put(vertex.getId(), vertex);
                    } else if (elementLayout.getValue() instanceof LayoutFlowFactsType) {
                        final LayoutFlowFactsType flow = (LayoutFlowFactsType) elementLayout.getValue();
                        layoutMap.put(buildEdgeId(flow.getSource(), flow.getTarget()), flow);
                    }
                }
            }
        }
    }

    /**
     * Returns a specially concatenated Edge id
     *
     * @param sourceId
     * @param targetId
     * @return concatenated Edge id
     */
    public String buildEdgeId(final String sourceId, final String targetId) {
        // TODO why is this here?
        return sourceId + "-" + targetId;
    }

    public Collection<PredecessorAdapater> getIncomingQueue(final ExternalNetElementType element) {
        return initIncomingQueue(new ElementAdapter(element));
    }

    private Collection<PredecessorAdapater> initIncomingQueue(final ElementAdapter elementAdapter) {
        if (this.incomingQueueMap == null) {
            this.incomingQueueMap = new HashMap<ElementAdapter, Collection<PredecessorAdapater>>();
        }
        Collection<PredecessorAdapater> incomingQueue = this.incomingQueueMap.get(elementAdapter);
        if (incomingQueue == null) {
            incomingQueue = new ArrayList<PredecessorAdapater>(2);
            this.incomingQueueMap.put(elementAdapter, incomingQueue);
        }
        return incomingQueue;
    }

    public void addIntroducedPredecessor(final ExternalNetElementType netElement, final NodeType node) {
        initIntroducedPredecessor();
        this.introducedPredecessorMap.put(new ElementAdapter(netElement), node);
    }

    private void initIntroducedPredecessor() {
        if (this.introducedPredecessorMap == null) {
            this.introducedPredecessorMap = new HashMap<ElementAdapter, NodeType>(2);
        }
    }

    public NodeType getIntroducedPredecessor(final ExternalNetElementType netElement) {
        initIntroducedPredecessor();
        return this.introducedPredecessorMap.get(new ElementAdapter(netElement));
    }

    public DecompositionType getDecompositionByID(final String id) {
        initDecompositionMap(specification.getDecomposition().size());
        return decompositionMap.get(id);
    }

    private void initDecompositionMap(final int initialSize) {
        if (decompositionMap == null) {
            decompositionMap = new HashMap<String, DecompositionType>(initialSize);
            for (final DecompositionType decomposition : specification.getDecomposition()) {
                decompositionMap.put(decomposition.getId(), decomposition);
            }
        }
    }

    public RoleType getRoleById(final String roleId) {
        if (getOrgDataType().getRoles() != null) {
            initRoleMap(getOrgDataType().getRoles().getRole().size());
            return this.roleMap.get(roleId);
        }
        return null;
    }

    private void initRoleMap(final int initialSize) {
        if (roleMap == null) {
            roleMap = new HashMap<String, RoleType>(initialSize);
            for (final RoleType role : getOrgDataType().getRoles().getRole()) {
                roleMap.put(role.getId(), role);
            }
        }
    }

    public NonHumanResourceType getNonHumanById(final String nonHumanId) {
        //TODO optimize
        if (getOrgDataType().getNonhumanresources() != null) {
            for (NonHumanResourceType nonHuman : getOrgDataType().getNonhumanresources().getNonhumanresource()) {
                if (nonHumanId.equals(nonHuman.getId())) {
                    return nonHuman;
                }
            }
        }
        return null;
    }

    public void setGeneratedResourceType(final String yawlId, final ResourceTypeType resourceType) {
        initSubResourceTypeMap();
        ConvertedResource convertedRole = generatedResourceTypeMap.get(yawlId);
        if (convertedRole != null) {
            convertedRole.setResource(resourceType);
        } else {
            convertedRole = new ConvertedResource();
            convertedRole.setResource(resourceType);
            generatedResourceTypeMap.put(yawlId, convertedRole);
        }
    }

    private void initSubResourceTypeMap() {
        if (generatedResourceTypeMap == null) {
            generatedResourceTypeMap = new HashMap<String, ConvertedResource>();
        }
    }

    public ResourceTypeType getGeneratedResourceType(final String id) {
        initSubResourceTypeMap();
        final ConvertedResource convertedResource = generatedResourceTypeMap.get(id);
        if (convertedResource != null) {
            return convertedResource.getResource();
        }
        return null;
    }

    public ParticipantType getParticipantById(final String participantId) {
        if (getOrgDataType().getParticipants() != null) {
            for (final ParticipantType p : getOrgDataType().getParticipants().getParticipant()) {
                if (p.getId().equals(participantId)) {
                    return p;
                }
            }
        }
        return null;
    }

    public NetType getNetForTaskId(final String id) {
        for (final NetType net : canonicalResult.getNet()) {
            for (final NodeType node : net.getNode()) {
                if (node.getId().equals(id)) {
                    return net;
                }
            }
        }
        return null;
    }

    public void addObjectForNet(final SoftType obj, final NetType net) {
        initNetObjectsByName();
        Map<String, SoftType> objectsByName = netObjectsByName.get(net.getId());
        if (objectsByName == null) {
            objectsByName = new HashMap<String, SoftType>(net.getObject().size());
            netObjectsByName.put(net.getId(), objectsByName);
        }
        objectsByName.put(obj.getName(), obj);
    }

    public SoftType getObjectByName(final String objectName, final NetType net) {
        initNetObjectsByName();
        final Map<String, SoftType> objectsByName = netObjectsByName.get(net.getId());
        if (objectsByName != null) {
            return objectsByName.get(objectName);
        }
        return null;
    }

    private void initNetObjectsByName() {
        if (netObjectsByName == null) {
            this.netObjectsByName = new HashMap<String, Map<String, SoftType>>();
        }
    }

    /**
     * Add the extension Element (XML) to the ANF as xs:any
     *
     * @param element
     *            any XML element
     * @param nodeId
     *            of the CPF element
     */
    public void addToAnnotations(final Element element, final String nodeId) {
        AnnotationType extension = findAnnotation(nodeId);
        if (extension == null) {
            // Create new Annotation
            extension = new org.apromore.anf.ObjectFactory().createAnnotationType();
            extension.setCpfId(nodeId);
            extension.setId(getUuidGenerator().getUUID(null));
            extension.getAny().add(element);
            getAnnotationResult().getAnnotation().add(extension);
        } else {
            extension.getAny().add(element);
        }
    }

    /**
     * Add the extension Element (XML) to the ANF as xs:any for the whole process model
     *
     * @param element
     *            any XML element
     */
    public void addToAnnotations(final Element element) {
        // Create new Annotation
        addToAnnotations(element, null);
    }

    private AnnotationType findAnnotation(final String nodeId) {
        for (final AnnotationType annotation : getAnnotationResult().getAnnotation()) {
            if (!(annotation instanceof DocumentationType || annotation instanceof GraphicsType)) {
                // We just want to add an plain 'AnnotationType'
                if (nodeId == null && annotation.getCpfId() == null) {
                    return annotation;
                } else if (nodeId != null && nodeId.equals(annotation.getCpfId())) {
                    return annotation;
                }
            }
        }
        return null;
    }

}
