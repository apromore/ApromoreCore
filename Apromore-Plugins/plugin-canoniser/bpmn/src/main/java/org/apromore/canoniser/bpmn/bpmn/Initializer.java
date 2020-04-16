/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2012, 2014 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
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
 * #L%
 */

package org.apromore.canoniser.bpmn.bpmn;

// Java 2 Standard packages
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

// Local classes
import com.processconfiguration.Configurable;
import com.processconfiguration.ConfigurationAnnotation;
import com.processconfiguration.TGatewayType;
import com.processconfiguration.Variants;
import org.apromore.canoniser.bpmn.AbstractInitializer;
import org.apromore.canoniser.bpmn.IdFactory;
import org.apromore.canoniser.bpmn.Initialization;
import org.apromore.canoniser.bpmn.cpf.Attributed;
import org.apromore.canoniser.bpmn.cpf.CpfANDSplitType;
import org.apromore.canoniser.bpmn.cpf.CpfCanonicalProcessType;
import org.apromore.canoniser.bpmn.cpf.CpfEdgeType;
import org.apromore.canoniser.bpmn.cpf.CpfEventType;
import org.apromore.canoniser.bpmn.cpf.CpfMessageType;
import org.apromore.canoniser.bpmn.cpf.CpfNetType;
import org.apromore.canoniser.bpmn.cpf.CpfNodeType;
import org.apromore.canoniser.bpmn.cpf.CpfObjectType;
import org.apromore.canoniser.bpmn.cpf.CpfObjectRefType;
import org.apromore.canoniser.bpmn.cpf.CpfResourceTypeType;
import org.apromore.canoniser.bpmn.cpf.CpfTaskType;
import org.apromore.canoniser.bpmn.cpf.CpfTimerType;
import org.apromore.canoniser.bpmn.cpf.ExtensionConstants;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.anf.AnnotationType;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.BaseVisitor;
import org.apromore.cpf.CancellationRefType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.DepthFirstTraverserImpl;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.JoinType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.ObjectRefType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.RoutingType;
import org.apromore.cpf.SplitType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TraversingVisitor;
import org.apromore.cpf.TypeAttribute;
import org.apromore.cpf.WorkType;
import org.omg.spec.bpmn._20100524.model.TActivity;
import org.omg.spec.bpmn._20100524.model.TBaseElement;
import org.omg.spec.bpmn._20100524.model.TCatchEvent;
import org.omg.spec.bpmn._20100524.model.TCompensateEventDefinition;
import org.omg.spec.bpmn._20100524.model.TComplexGateway;
import org.omg.spec.bpmn._20100524.model.TErrorEventDefinition;
import org.omg.spec.bpmn._20100524.model.TEvent;
import org.omg.spec.bpmn._20100524.model.TEventDefinition;
import org.omg.spec.bpmn._20100524.model.TExclusiveGateway;
import org.omg.spec.bpmn._20100524.model.TExtensionElements;
import org.omg.spec.bpmn._20100524.model.TFlowElement;
import org.omg.spec.bpmn._20100524.model.TFlowNode;
import org.omg.spec.bpmn._20100524.model.TFormalExpression;
import org.omg.spec.bpmn._20100524.model.TGateway;
import org.omg.spec.bpmn._20100524.model.TGatewayDirection;
import org.omg.spec.bpmn._20100524.model.TInclusiveGateway;
import org.omg.spec.bpmn._20100524.model.TInputOutputSpecification;
import org.omg.spec.bpmn._20100524.model.TInputSet;
import org.omg.spec.bpmn._20100524.model.TMessageEventDefinition;
import org.omg.spec.bpmn._20100524.model.TOutputSet;
import org.omg.spec.bpmn._20100524.model.TParallelGateway;
import org.omg.spec.bpmn._20100524.model.TProcess;
import org.omg.spec.bpmn._20100524.model.TRootElement;
import org.omg.spec.bpmn._20100524.model.TSequenceFlow;
import org.omg.spec.bpmn._20100524.model.TSignalEventDefinition;
import org.omg.spec.bpmn._20100524.model.TThrowEvent;
import org.omg.spec.bpmn._20100524.model.TTimerEventDefinition;
import org.omg.spec.dd._20100524.di.DiagramElement;

/**
 * Global state of BPMn document construction used within {@link #BpmnDefinition(CanonicalProcessType)}.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class Initializer extends AbstractInitializer implements ExtensionConstants {

    // Instance under construction by this initializer
    private final BpmnDefinitions bpmn;

    // CPF document root
    private final CpfCanonicalProcessType cpf;

    // Generates all identifiers scoped to the BPMN document
    private final IdFactory bpmnIdFactory = new IdFactory();

    // Used to wrap BPMN elements in JAXBElements
    private final BpmnObjectFactory factory = new BpmnObjectFactory();

    // Map from CPF @cpfId node identifiers to BPMN elements
    private final Map<String, TBaseElement> idMap = new HashMap<String, TBaseElement>();

    // The BPMN target namespace used for all QName references to elements of this document
    private final String targetNamespace;

    // The CPF identifiers which are cancelled by any CPF Task
    private final Map<String, String> cancelNodeIdMap = new HashMap<String, String>();

    // The pc:variants element which catalogues configurable process model versions (non-configurable models won't have one)
    Variants variants = null;

    // Map from variant name (not id!) to pc:variant instance
    HashMap<String, Variants.Variant> variantMap = new HashMap<>();

    /**
     * Sole constructor.
     *
     * @param newBpmnIdFactory;
     * @param newIdMap;
     */
    Initializer(final BpmnDefinitions newBpmn, final CpfCanonicalProcessType newCpf, final String newTargetNamespace) {
        bpmn            = newBpmn;
        cpf             = newCpf;
        targetNamespace = newTargetNamespace;

        // Perform boundary event transformation
        processBoundaryEvents();
    }

    /**
     * Populates {@link #cancelNodeIdMap} so that {@link #findAttachedTaskId} will work, and removes {@link CpfANDSplitType}s
     * which are to be replaced by boundary events.
     */
    private void processBoundaryEvents() {

        // Populate cancelNodeIds
        cpf.accept(new TraversingVisitor(new DepthFirstTraverserImpl(), new BaseVisitor() {
            @Override public void visit(final TaskType task) {
                for (CancellationRefType cancellationRef : task.getCancelNodeId()) {
                    assert !cancelNodeIdMap.containsKey(cancellationRef.getRefId());
                    cancelNodeIdMap.put(cancellationRef.getRefId(), task.getId());
                }
            }
        }));

        // Identify AND splits which can be converted into boundary events
        final Map<CpfANDSplitType, CpfTaskType> splits = new HashMap<CpfANDSplitType, CpfTaskType>();
        cpf.accept(new TraversingVisitor(new DepthFirstTraverserImpl(), new BaseVisitor() {
            @Override public void visit(final ANDSplitType split) {
                CpfANDSplitType cpfSplit = (CpfANDSplitType) split;
                CpfTaskType task = null;

                // Check that there's exactly one task and at least one event targeted by the gateway
                int taskCount  = 0;
                int eventCount = 0;
                for (CpfEdgeType edge : cpfSplit.getOutgoingEdges()) {
                    CpfNodeType node = (CpfNodeType) cpf.getElement(edge.getTargetId());

                    if (node instanceof CpfEventType) {
                        eventCount++;
                    } else if (node instanceof CpfTaskType) {
                        task = (CpfTaskType) node;
                        taskCount++;
                    }
                }

                // Check that the task cancels all the events
                if (eventCount > 0 && taskCount == 1) {
                    assert task != null;

                    // Determine the set of candidate boundary events
                    Set<String> cancelledEdgeIdSet = new HashSet<String>();
                    for (CpfEdgeType edge : cpfSplit.getOutgoingEdges()) {
                        CpfNodeType node = (CpfNodeType) cpf.getElement(edge.getTargetId());
                        if (node instanceof CpfEventType) {
                            cancelledEdgeIdSet.add(node.getId());
                        }
                    }

                    // Determine the task's cancellation set
                    Set<String> taskCancelledIdSet = new HashSet<String>();
                    for (CancellationRefType cancellationRef : task.getCancelNodeId()) {
                        taskCancelledIdSet.add(cancellationRef.getRefId());
                    }

                    // If the task cancels all the candidate boundary events, attempt rewriting
                    if (taskCancelledIdSet.containsAll(cancelledEdgeIdSet)) {
                        splits.put(cpfSplit, task);
                        java.util.logging.Logger.getAnonymousLogger().info(cpfSplit.getId() + " can be made into a boundary event");
                    }
                }
            }
        }));

        // Remove the identified AND splits
        for (CpfANDSplitType split : splits.keySet()) {

            // Retarget the AND split's incoming edge to the attached task
            for (CpfEdgeType in : split.getIncomingEdges()) {
                CpfTaskType task = splits.get(split);
                in.setTargetId(task.getId());
                task.getIncomingEdges().add(in);
            }

            // Remove the AND split and its outgoing edges
            CpfNetType parent = findParent(split);
            parent.getNode().remove(split);
            uncancel(split, parent.getNode());

            parent.getEdge().removeAll(split.getOutgoingEdges());
            for (CpfEdgeType edge : split.getOutgoingEdges()) {
                CpfNodeType node = (CpfNodeType) cpf.getElement(edge.getTargetId());
                node.getIncomingEdges().remove(edge);
                uncancel(edge, parent.getNode());
            }
        }
    }

    private void uncancel(final CpfEdgeType edge, final List<NodeType> nodes) {
        for (NodeType node2 : nodes) {
            if (node2 instanceof WorkType) {
                Iterator<CancellationRefType> i = ((WorkType) node2).getCancelEdgeId().iterator();
                while (i.hasNext()) {
                    CancellationRefType cancellation = i.next();
                    if (edge.getId().equals(cancellation.getRefId())) {
                        i.remove();
                    }
                }   
            }
        }
    }

    private void uncancel(final CpfNodeType node, final List<NodeType> nodes) {
        for (NodeType node2 : nodes) {
            if (node2 instanceof WorkType) {
                Iterator<CancellationRefType> i = ((WorkType) node2).getCancelNodeId().iterator();
                while (i.hasNext()) {
                    CancellationRefType cancellation = i.next();
                    if (node.getId().equals(cancellation.getRefId())) {
                        i.remove();
                    }
                }   
            }
        }
    }

    /**
     * Find a {@link NetType} given its identifier.
     *
     * @param id  the identifier attribute of the sought net
     * @return the net in <code>cpf</code> with the identifier <code>id</code>
     * @throws CanoniserException if <code>id</code> doesn't identify a net in <code>cpf</code>
     */
    public CpfNetType findNet(final String id) throws CanoniserException {

        for (final NetType net : cpf.getNet()) {
            if (id.equals(net.getId())) {
                return (CpfNetType) net;
            }
        }

        // Failed to find the desired name
        throw new CanoniserException("CPF model has no net with id " + id);
    }

    /**
     * @param cpfId  a CPF identifier
     * @return the BPMN element corresponding to the identified CPF element
     */
    public TBaseElement findElement(final String cpfId) {
        return idMap.get(cpfId);
    }

    /**
     * @param node  a CPF Node
     * @return the CPF Net containing the <code>node</code>
     */
    CpfNetType findParent(final CpfNodeType node) {
        for (NetType net : cpf.getNet()) {
            if (net.getNode().contains(node)) {
                return (CpfNetType) net;
            }
        }

        // Didn't find a parent
        return null;
    }

    /** @return shared {@link BpmnObjectFactory} instance */
    public BpmnObjectFactory getFactory() {
        return factory;
    }

    /** @return the CPF ResourceTypes */
    List<ResourceTypeType> getResourceTypes() {
        return cpf.getResourceType();
    }

    /**
     * @return the target namespace of the BPMN document under construction
     */
    String getTargetNamespace() {
        return targetNamespace;
    }

    /**
     * @param event  a CPF event
     * @return the identifier of a CPF task which cancels this event, or <code>null</code> if no such task exists
     */
    public String findAttachedTaskId(final CpfEventType event) {
        return cancelNodeIdMap.get(event.getId());
    }

    /**
     * @param id  requested identifier (typically the identifier of the corresponding CPF element); may be <code>null</code>
     * @return an indentifier unique within the BPMN document
     */
    String newId(final String id) {
        return bpmnIdFactory.newId(id);
    }

    //
    // Pseudo-superclass initialization methods
    //

    // ...for CpfEdgeType

    void populateBaseElement(final TBaseElement baseElement, final CpfEdgeType cpfEdge) {

        // Handle @id attribute
        baseElement.setId(newId(cpfEdge.getId()));
        idMap.put(cpfEdge.getId(), baseElement);

        // Handle extensionElements subelement
        populateBaseElementExtensionElements(baseElement, cpfEdge);
    }

    void populateFlowElement(final TFlowElement flowElement, final CpfEdgeType cpfEdge) {
        populateBaseElement(flowElement, cpfEdge);

        // Handle @name
        flowElement.setName(cpfEdge.getName());

        // TODO - handle the following attributes, which are in the standard but not used in practice
        flowElement.setAuditing(null);
        flowElement.getCategoryValueRef();  // .add((QName)...);
        flowElement.setMonitoring(null);
    }

    // ...for CpfNetType

    /**
     * Initialize a BPMN element, based on the CPF net it corresponds to.
     * Only BPMN Processes ever corresponds to a CPF Net, but we're only interested in their base properties here.
     *
     * @param baseElement  the BPMN element to set
     * @param cpfNode  the CPF element which <code>baseElement</code> corresponds to
     */
    void populateBaseElement(final TProcess baseElement, final CpfNetType cpfNet) {

        // Handle @id attribute
        baseElement.setId(bpmnIdFactory.newId(cpfNet.getId()));
        idMap.put(cpfNet.getId(), baseElement);

        // Handle extensionElements subelement
        populateBaseElementExtensionElements(baseElement, cpfNet);
    };

    /**
     * Initialize the content of a wrapped BPMN {@link TProcess} or {@link TSubProcess}.
     *
     * @param process  the wrapped {@link TProcess} or {@link TSubProcess} to be populated
     * @param net  the CPF net which the <code>process</code> corresponds to
     * @throws CanoniserException if anything goes wrong
     */
    void populateProcess(final ProcessWrapper process, final CpfNetType net) throws CanoniserException {
        ProcessWrapper.populateProcess(process, net, this);
    }

    // ...for CpfNodeType

    /**
     * Initialize a BPMN element, based on the CPF node it corresponds to.
     *
     * @param baseElement  the BPMN element to set
     * @param cpfNode  the CPF element which <code>baseElement</code> corresponds to
     */
    void populateBaseElement(final TBaseElement baseElement, final CpfNodeType cpfNode) {

        // Handle @id attribute
        baseElement.setId(bpmnIdFactory.newId(cpfNode.getId()));
        idMap.put(cpfNode.getId(), baseElement);

        // Handle extensionElements subelement
        populateBaseElementExtensionElements(baseElement, cpfNode);
    };

    void populateFlowElement(final TFlowElement flowElement, final CpfNodeType cpfNode) {
        populateBaseElement(flowElement, cpfNode);

        // Handle @name
        flowElement.setName(cpfNode.getName());
    }

    void populateFlowNode(final TFlowNode flowNode, final CpfNodeType cpfNode) throws CanoniserException {
        populateFlowElement(flowNode, cpfNode);

        // Handle incoming and outgoing
        if (false) {  // TODO - allow this to be configured by the BPMN canoniser PluginRequest
            defer(new Initialization() {
                public void initialize() throws CanoniserException {
                    for (final EdgeType edge : cpfNode.getIncomingEdges()) {
                        flowNode.getIncoming().add(new QName(targetNamespace, findElement(edge.getId()).getId()));
                    }
                    for (final EdgeType edge : cpfNode.getOutgoingEdges()) {
                        flowNode.getOutgoing().add(new QName(targetNamespace, findElement(edge.getId()).getId()));
                    }
                }
            });
        }

        // Some flow nodes may have a default sequence flow
        int defaultEdgeCount = 0;
        for (final EdgeType edge : cpfNode.getOutgoingEdges()) {
            if (edge.isDefault()) {

               // No element should have more than one default edge
               if (++defaultEdgeCount > 1) {
                   throw new CanoniserException("CPF node " + cpfNode.getId() + " has " + defaultEdgeCount + " default edges");
                }

                // The edge will be flagged as default after all elements have been created (it might not exist yet)
                defer(new Initialization() {
                    public void initialize() throws CanoniserException {
                        assert edge.getSourceId() != null;

                        TBaseElement  defaultingElement = idMap.get(edge.getSourceId());
                        TSequenceFlow defaultFlow       = (TSequenceFlow) idMap.get(edge.getId());

                        assert defaultingElement != null : "Could not find BPMN element corresponding to source of default CPF edge " + edge.getId();
                        assert defaultFlow       != null : "Could not find BPMN flow corresponding to default CPF edge " + edge.getId();

                        if (defaultingElement instanceof TActivity) {
                            ((TActivity) defaultingElement).setDefault(defaultFlow);
                        } else if (defaultingElement instanceof TComplexGateway) {
                            ((TComplexGateway) defaultingElement).setDefault(defaultFlow);
                        } else if (defaultingElement instanceof TExclusiveGateway) {
                            ((TExclusiveGateway) defaultingElement).setDefault(defaultFlow);
                        } else if (defaultingElement instanceof TInclusiveGateway) {
                            ((TInclusiveGateway) defaultingElement).setDefault(defaultFlow);
                        } else if (defaultingElement instanceof TParallelGateway) {
                            try {
                                rewriteExplicitSplitImplicitly((TParallelGateway) defaultingElement);
                            } catch (CanoniserException e) {
                                throw new CanoniserException("Default flow " + defaultFlow.getId() + " from gateway " + defaultingElement.getId() +
                                                             " can't be rewritten", e);
                            }
                        } else {
                            throw new CanoniserException("Could not set default sequence flow for " + defaultingElement.getId());
                        }
                    }
                });
            }
        }
    }

    /**
     * Rewrite a diverging parallel gateway implicitly.
     *
     * This can only be called once all the BPMN elements have been created and have indexed identifiers.
     * In other words, it should be called by the {@link #defer} method.
     *
     * @param gateway  a diverging parallel gateway
     * @throws CanoniserException if <code>gateway</code> doesn't have exactly one incoming sequence flow
     */
    private void rewriteExplicitSplitImplicitly(final TParallelGateway gateway) throws CanoniserException {

        // Check that this really is a diverging gateway
        if (gateway.getIncoming().size() != 1) {
            throw new CanoniserException(gateway.getId() + " is not diverging, incoming flows are " + gateway.getIncoming());
        }

        // Remove the gateway and its incoming sequence flow
        BpmnSequenceFlow incomingFlow = (BpmnSequenceFlow) bpmn.findElement(gateway.getIncoming().get(0));

        throw new CanoniserException("Rewriting parallel gateway " + gateway.getId() + " implicitly not yet implemented");
    }

    void populateActivity(final TActivity activity, final CpfNodeType cpfNode) throws CanoniserException {
        populateFlowNode(activity, cpfNode);

        if (cpfNode.getAttribute() != null) {
            for (TypeAttribute attribute: cpfNode.getAttribute()) {
                if ("bpmn:ioSpecification".equals(attribute.getName())) {
                    // Deserialize the input/output specification
                    try {
                        activity.setIoSpecification(bpmn.newContext()
                                                        .createUnmarshaller()
                                                        .unmarshal(new StreamSource(new StringReader(attribute.getValue())), TInputOutputSpecification.class)
                                                        .getValue());
                    } catch (JAXBException e) {
                        throw new CanoniserException("Unable to parse bpmn:ioSpecification property for CPF node " + cpfNode.getId(), e);
                    }
                }
            }
        }

        // Create data associations
        assert cpfNode instanceof WorkType;  // TODO - add a CpfWorkType interface matching WorkType
        for (ObjectRefType objectRef : ((WorkType) cpfNode).getObjectRef()) {
            CpfObjectRefType cpfObjectRef = (CpfObjectRefType) objectRef;

            switch (cpfObjectRef.getType()) {
            case INPUT:  activity.getDataInputAssociation().add(new BpmnDataInputAssociation(cpfObjectRef, activity, this));   break;
            case OUTPUT: activity.getDataOutputAssociation().add(new BpmnDataOutputAssociation(cpfObjectRef, activity, this)); break;
            default:     assert false : "CPF ObjectRef " + cpfObjectRef.getId() + " has unsupported type " + cpfObjectRef.getType();
            }
        }
    }

    void populateEvent(final TCatchEvent event, final CpfEventType cpfEvent) throws CanoniserException {
        populateEvent(event, event.getEventDefinition(), cpfEvent);
    }

    void populateEvent(final TThrowEvent event, final CpfEventType cpfEvent) throws CanoniserException {
        populateEvent(event, event.getEventDefinition(), cpfEvent);
    }

    private void populateEvent(final TEvent                                        event,
                               final List<JAXBElement<? extends TEventDefinition>> eventDefinitionList,
                               final CpfEventType                                  cpfEvent) throws CanoniserException {

        populateFlowNode(event, cpfEvent);

        if (cpfEvent.isCompensation()) {
            TCompensateEventDefinition ced = new TCompensateEventDefinition();
            ced.setActivityRef(cpfEvent.getCompensationActivityRef());
            eventDefinitionList.add(factory.createCompensateEventDefinition(ced));
        }

        if (cpfEvent.isError()) {
            TErrorEventDefinition eed = new TErrorEventDefinition();
            eed.setErrorRef(cpfEvent.getErrorRef());
            eventDefinitionList.add(factory.createErrorEventDefinition(eed));
        }

        if (cpfEvent instanceof CpfMessageType) {
            CpfMessageType cpfMessage = (CpfMessageType) cpfEvent;

            /*
            switch (cpfMessage.getDirection) {
            case INCOMING:
            case OUTGOING:
            }
            */

            TMessageEventDefinition med = new TMessageEventDefinition();
            //med.setMessageRef((QName) ...);
            eventDefinitionList.add(factory.createMessageEventDefinition(med));
        }

        if (cpfEvent.isSignalCatcher()) {
            TSignalEventDefinition sed = new TSignalEventDefinition();
            sed.setSignalRef(cpfEvent.getSignalCaughtRef());
            eventDefinitionList.add(factory.createSignalEventDefinition(sed));
        }

        if (cpfEvent.isSignalThrower()) {
            TSignalEventDefinition sed = new TSignalEventDefinition();
            sed.setSignalRef(cpfEvent.getSignalThrownRef());
            eventDefinitionList.add(factory.createSignalEventDefinition(sed));
        }

        if (cpfEvent instanceof CpfTimerType) {
            CpfTimerType cpfTimer = (CpfTimerType) cpfEvent;

            if (cpfTimer.getTimeDate() != null) {
                TTimerEventDefinition ted = new TTimerEventDefinition();
                TFormalExpression fe = new TFormalExpression();
                try {
                    fe.setEvaluatesToTypeRef(cpfTimer.getTimeDate().getXMLSchemaType());
                } catch (IllegalStateException e) { /* skip @evaluatesToTypeRef if no XSD datatype matches */ }
                fe.getContent().add(cpfTimer.getTimeDate().toXMLFormat());
                ted.setTimeDate(fe);
                eventDefinitionList.add(factory.createTimerEventDefinition(ted));
            }

            if (cpfTimer.getTimeDuration() != null) {
                TTimerEventDefinition ted = new TTimerEventDefinition();
                TFormalExpression fe = new TFormalExpression();
                try {
                    fe.setEvaluatesToTypeRef(cpfTimer.getTimeDuration().getXMLSchemaType());
                } catch (IllegalStateException e) { /* skip @evaluatesToTypeRef if no XSD datatype matches */ }
                fe.getContent().add(cpfTimer.getTimeDuration().toString());
                ted.setTimeDuration(fe);
                eventDefinitionList.add(factory.createTimerEventDefinition(ted));
            }

            if (cpfTimer.getTimeExpression() != null) {
                TTimerEventDefinition ted = new TTimerEventDefinition();
                TFormalExpression fe = new TFormalExpression();
                fe.getContent().add(cpfTimer.getTimeExpression().getExpression());
                ted.setTimeCycle(fe);
                eventDefinitionList.add(factory.createTimerEventDefinition(ted));
            }
        }
    }

    public void populateGateway(final TGateway gateway, final CpfNodeType cpfNode) throws CanoniserException {
        assert cpfNode instanceof RoutingType : "Tried to populate " + cpfNode.getId() + " as if it was a gateway";
        populateFlowNode(gateway, cpfNode);

        // Handle gatewayDirection
        int ins  = cpfNode.getIncomingEdges().size();
        int outs = cpfNode.getOutgoingEdges().size();
        if (ins > 1 && outs <= 1) {
            gateway.setGatewayDirection(TGatewayDirection.CONVERGING);
        } else if (ins <= 1 && outs > 1) {
            gateway.setGatewayDirection(TGatewayDirection.DIVERGING);
        } else if (ins > 1 && outs > 1) {
            gateway.setGatewayDirection(TGatewayDirection.MIXED);
        } else {
            gateway.setGatewayDirection(TGatewayDirection.UNSPECIFIED);
        }
    }

    // ...for CpfObjectType

    /**
     * Initialize a BPMN element, based on the CPF object it corresponds to.
     *
     * @param baseElement  the BPMN element to set
     * @param cpfNode  the CPF element which <code>baseElement</code> corresponds to
     */
    void populateBaseElement(final TBaseElement baseElement, final CpfObjectType cpfObject) {

        // Handle @id attribute
        baseElement.setId(bpmnIdFactory.newId(cpfObject.getId()));
        idMap.put(cpfObject.getId(), baseElement);

        // Handle extensionElements subelement
        populateBaseElementExtensionElements(baseElement, cpfObject);
    };

    /**
     * Initialize a BPMN flow element, based on the CPF object it corresponds to.
     *
     * @param flowElement  the BPMN flow element to set
     * @param cpfNode  the CPF object which <code>flowElement</code> corresponds to
     */
    void populateFlowElement(final TFlowElement flowElement, final CpfObjectType cpfObject) {
        populateBaseElement(flowElement, cpfObject);

        // Handle @name attribute
        flowElement.setName(cpfObject.getName());

        // TODO - handle the following attributes, which are in the standard but not used in practice
        flowElement.setAuditing(null);
        flowElement.getCategoryValueRef();  // .add((QName) ...);
        flowElement.setMonitoring(null);
    };

    // ...for ObjectRefType

    /**
     * Initialize a BPMN element, based on the CPF ObjectRef it corresponds to.
     *
     * @param baseElement  the BPMN element to set
     * @param objectRef  the CPF ObjectRef which <code>baseElement</code> corresponds to
     */
    void populateBaseElement(final TBaseElement baseElement, final CpfObjectRefType cpfObjectRef) {

        // Handle @id attribute
        baseElement.setId(bpmnIdFactory.newId(cpfObjectRef.getId()));
        idMap.put(cpfObjectRef.getId(), baseElement);

        // Handle extensionElements subelement
        populateBaseElementExtensionElements(baseElement, cpfObjectRef);
    };

    // ...for CpfResourceTypeType

    /**
     * Initialize a BPMN element, based on the CPF resource it corresponds to.
     *
     * @param baseElement  the BPMN element to set
     * @param cpfResourceType  the CPF element which <code>baseElement</code> corresponds to
     */
    void populateBaseElement(final TBaseElement baseElement, final CpfResourceTypeType cpfResourceType) {

        // Handle @id attribute
        baseElement.setId(bpmnIdFactory.newId(cpfResourceType.getId()));
        idMap.put(cpfResourceType.getId(), baseElement);

        // Handle extensionElements subelement
        populateBaseElementExtensionElements(baseElement, cpfResourceType);
    };

    // ...for ANF AnnotationType

    /**
     * @param graphics  the ANF annotation
     */
    void populateDiagramElement(final DiagramElement diagramElement, final AnnotationType annotation) {

        // Handle @id attribute
        diagramElement.setId(newId(annotation.getId()));
    }

    // Internal methods

    /**
     * Look up or create a variant by name.
     *
     * This method generates the IDs for the variants and installs the <code>pc:variants</code> catalogue element.
     *
     * @param name  the variant name
     * @return the variant instance, never <code>null</code>
     */
    private Variants.Variant findVariant(String name) {
        if (variantMap.containsKey(name)) {
            return variantMap.get(name);
        } else {
            if (variants == null) {
                variants = new Variants();

                // Add the pc:variants extension element to the first Process in the BPMN document
                defer(new Initialization() {
                    public void initialize() throws CanoniserException {
                        for (JAXBElement<? extends TRootElement> jaxbRoot: bpmn.getRootElement()) {
                            if (jaxbRoot.getValue() instanceof TProcess) {
                                TProcess process = (TProcess) jaxbRoot.getValue();
                                TExtensionElements extensionElements = process.getExtensionElements();
                                if (extensionElements == null) {
                                    extensionElements = new TExtensionElements();
                                    process.setExtensionElements(extensionElements);
                                }
                                assert extensionElements != null;
                                extensionElements.getAny().add(variants);
                                return;  // only add the catalog once
                            }
                        }
                        throw new CanoniserException("Unable to install pc:variants");
                    }
                });
            }
            assert variants != null;

            Variants.Variant variant = new Variants.Variant();
            variant.setId("vid-" + variants.getVariant().size());
            variant.setName(name);

            variants.getVariant().add(variant);
            variantMap.put(name, variant);

            return variant;
	}
    }

    /**
     * Translate a CPF attribute list to the BPMN BaseElement's extensionElements.
     *
     * @param attributes  the attribute list of the source CPF element
     * @param baseElement  the destination BPMN element
     */
    private void populateBaseElementExtensionElements(final TBaseElement baseElement, final Attributed cpfElement) {

        final String name = BPMN_CPF_NS + "/" + EXTENSION_ELEMENTS;

        List<TypeAttribute> attributes = new ArrayList<TypeAttribute>();
        for (TypeAttribute attr : cpfElement.getAttribute()) {
            if (name.equals(attr.getName()) || "epml_cpf/extensions".equals(attr.getName())) {
                attributes.add(attr);
            }
        }

        boolean isConfigurable = (cpfElement instanceof NodeType         && Boolean.TRUE.equals(((NodeType) cpfElement).isConfigurable()))   ||
                                 (cpfElement instanceof ObjectType       && Boolean.TRUE.equals(((ObjectType) cpfElement).isConfigurable())) ||
                                 (cpfElement instanceof ResourceTypeType && Boolean.TRUE.equals(((ResourceTypeType) cpfElement).isConfigurable()));

        if (attributes.size() > 0 || isConfigurable) {
            TExtensionElements extensionElements = baseElement.getExtensionElements();
            if (extensionElements == null) {
                extensionElements = factory.createTExtensionElements();
            }
            assert extensionElements != null;

            for (TypeAttribute attribute : attributes) {
                if (attribute.getAny() == null) {
                    String value = attribute.getValue();
                    if (value != null && value.startsWith("annotation;")) {
                        String[] values = value.split(";");
			assert "annotation".equals(values[0]);

                        if (cpfElement instanceof EdgeType) {
                            EdgeType edge = (EdgeType) cpfElement;
                            Object sourceObject = cpf.getElement(edge.getSourceId());
                            Object targetObject = cpf.getElement(edge.getTargetId());
                            if (sourceObject instanceof SplitType || targetObject instanceof JoinType) {
                                ConfigurationAnnotation configurationAnnotation = new ConfigurationAnnotation();
                                for (int i = 1; i<values.length; i++) {
                                    ConfigurationAnnotation.Configuration configuration = new ConfigurationAnnotation.Configuration();
                                    String[] fields = values[i].split(":", 2);
                                    configuration.setVariantRef(findVariant(fields[0]));
                                    configurationAnnotation.getConfiguration().add(configuration);
			        }
                                extensionElements.getAny().add(configurationAnnotation);
                            }
                        }
                        else if (cpfElement instanceof RoutingType) {
                            final RoutingType routing = (RoutingType) cpfElement;
                            ConfigurationAnnotation configurationAnnotation = new ConfigurationAnnotation();
                            for (int i = 1; i<values.length; i++) {
                                final ConfigurationAnnotation.Configuration configuration = new ConfigurationAnnotation.Configuration();
                                String[] fields = values[i].split(":", 2);
                                configuration.setVariantRef(findVariant(fields[0]));

                                // type attribute
                                switch (fields[1]) {
                                case "and": configuration.setType(TGatewayType.PARALLEL);  break;
                                case "or":  configuration.setType(TGatewayType.INCLUSIVE);  break;
                                case "xor": configuration.setType(TGatewayType.DATA_BASED_EXCLUSIVE);  break;
                                default: throw new RuntimeException("Unsupported gateway type: " + fields[1]);
                                }

                                configurationAnnotation.getConfiguration().add(configuration);
			    }
                            extensionElements.getAny().add(configurationAnnotation);
                            extensionElements.getAny().add(new Configurable());  // mark that gateway as configurable model
                        }
                        else if (cpfElement instanceof WorkType) {
                            ConfigurationAnnotation configurationAnnotation = new ConfigurationAnnotation();
                            for (int i = 1; i<values.length; i++) {
                                ConfigurationAnnotation.Configuration configuration = new ConfigurationAnnotation.Configuration();
                                String[] fields = values[i].split(":", 2);
                                configuration.setVariantRef(findVariant(fields[0]));
                                configuration.setName(fields[1]);
                                configurationAnnotation.getConfiguration().add(configuration);
			    }
                            extensionElements.getAny().add(configurationAnnotation);
                        }
                        else {
                            java.util.logging.Logger.getAnonymousLogger().warning("Unable to interpret annotation attribute of CPF node " + cpfElement);
                        }
		    }
                }
                else if (attribute.getAny() instanceof Element) {
                    Element element = (Element) attribute.getAny();
                    NodeList nodes = element.getChildNodes();
                    for (int i = 0; i < nodes.getLength(); i++) {
                        if (nodes.item(i) instanceof Element) {
                            extensionElements.getAny().add(nodes.item(i));
                        }
                    }
                }
                else {  // the presumption here is that anything that didn't get turned into a DOM Element must be from the cpf: or pc: namespaces
                    extensionElements.getAny().add(attribute.getAny());
                }
            }

            if (isConfigurable) {
                com.processconfiguration.Configurable pcConfigurable = new com.processconfiguration.ObjectFactory().createConfigurable(); 
                extensionElements.getAny().add(pcConfigurable);
            }

            baseElement.setExtensionElements(extensionElements);
        }
    }
}
