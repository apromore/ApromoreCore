package com.processconfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

//import com.sun.xml.bind.IDResolver;

/*
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.Process;
import de.hpi.bpmn2_0.model.bpmndi.BPMNDiagram;
import de.hpi.bpmn2_0.model.bpmndi.BPMNEdge;
import de.hpi.bpmn2_0.model.bpmndi.BPMNShape;
import de.hpi.bpmn2_0.model.bpmndi.di.DiagramElement;
import de.hpi.bpmn2_0.model.connector.Edge;
import de.hpi.bpmn2_0.model.connector.MessageFlow;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import de.hpi.bpmn2_0.model.data_object.DataObject;
import de.hpi.bpmn2_0.model.data_object.DataObjectReference;
import de.hpi.bpmn2_0.model.data_object.DataStore;
import de.hpi.bpmn2_0.model.data_object.DataStoreReference;
import de.hpi.bpmn2_0.model.data_object.Message;
import de.hpi.bpmn2_0.model.event.EndEvent;
import de.hpi.bpmn2_0.model.event.StartEvent;
import de.hpi.bpmn2_0.model.extension.ExtensionElements;
import de.hpi.bpmn2_0.model.extension.synergia.Configurable;
import de.hpi.bpmn2_0.model.extension.synergia.Configurable.Configuration;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationAnnotationAssociation;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationAnnotationShape;
import de.hpi.bpmn2_0.model.extension.synergia.TGatewayType;
import static de.hpi.bpmn2_0.model.extension.synergia.TGatewayType.DATA_BASED_EXCLUSIVE;
import static de.hpi.bpmn2_0.model.extension.synergia.TGatewayType.EVENT_BASED_EXCLUSIVE;
import static de.hpi.bpmn2_0.model.extension.synergia.TGatewayType.INCLUSIVE;
import static de.hpi.bpmn2_0.model.extension.synergia.TGatewayType.PARALLEL;
import de.hpi.bpmn2_0.model.gateway.Gateway;
import de.hpi.bpmn2_0.model.gateway.GatewayWithDefaultFlow;
import de.hpi.bpmn2_0.model.gateway.EventBasedGateway;
import de.hpi.bpmn2_0.model.gateway.ExclusiveGateway;
import de.hpi.bpmn2_0.model.gateway.InclusiveGateway;
import de.hpi.bpmn2_0.model.gateway.ParallelGateway;
import de.hpi.bpmn2_0.transformation.AbstractVisitor;
import de.hpi.bpmn2_0.transformation.BPMNPrefixMapper;
*/
import org.omg.spec.bpmn._20100524.di.*;
import org.omg.spec.bpmn._20100524.model.*;
import org.omg.spec.dd._20100524.di.*;

/**
 * Configuring an annotated BPMN document proceeds in two passes.
 * In the first pass, the following steps are taken:
 * <ol>
 * <li>Remove sequence flows which are absent from the gateway's {@linkplain Configuration#sourceRefs source flows}
 *     if the gateway is {@linkplain de.hpi.bpmn2_0.model.gateway.GatewayDirection#CONVERGING converging}, or from its
 *     {@linkplain Configuration#targetRefs target flows} if it is
 *     {@linkplain de.hpi.bpmn2_0.model.gateway.GatewayDirection#DIVERGING diverging}.</li>
 * <li>For configurable gateways, change the type of the gateway to match its configured {@link Configuration#type type}.</li>
 * <li>Remove the {@link Configurable} extension element from configured gateways.</li>
 * </ol>
 *
 * In the second pass, the process structure is cleaned up via the following steps:
 * <ol>
 * <li>Remove trivial gateways; i.e. converging gateways with a single source, or diverging gateways with a single target.</li>
 * <li>Prune any elements which are no longer on a path from a start event to an end event.</li>
 * </ol>
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public abstract class ConfigurationAlgorithm {

    /** Logger.  This is named after the class. */
    private static final Logger logger = Logger.getLogger(ConfigurationAlgorithm.class.getCanonicalName());

    /**
     * Given a document with configuration extensions, mutate it
     * to apply the changes indicated for the configuration.  The
     * result is a BPMN document without configuration extensions.
     *
     * @param definitions  a configurable BPMN document, which will be mutated
     */
    public static void configure(final TDefinitions definitions) {

        // Identify configured gateways
        final Set<TGateway> reconfiguredGatewaySet = findConfiguredGateways(definitions);

        // Find and remove absent sequence flows
        prune(definitions, (Set) findAbsentSequenceFlows(reconfiguredGatewaySet));

        // Replace configured gateways with the configured type, and remove their <configurable> element
        for (TGateway gateway : reconfiguredGatewaySet) {
            replaceConfiguredGateway(definitions, gateway);
        }

        // Remove trivial gateways
        removeTrivialGateways(definitions);

        // Find and remove disconnected elements
        prune(definitions, (Set) findOrphans(definitions));
    }

    /**
     * Create a mapping from process elements to diagram elements.
     *
     * @param definitions  a BPMN XML document
     * @return  the reverse mapping of the <code>bpmnElement</code> attribute
     */
    public static Map<QName, DiagramElement> findBpmndiMap(final TDefinitions definitions) {
        final Map<QName, DiagramElement> bpmndiMap = new HashMap<>();

	/* FOO
        for (BPMNDiagram bpmnDiagram : definitions.getBPMNDiagram()) {
            for (JAXBElement<? extends DiagramElement> jaxbElement : bpmnDiagram.getBPMNPlane().getDiagramElement()) {
                DiagramElement element = jaxbElement.getValue();
                element.acceptVisitor(new AbstractVisitor() {
                    @Override public void visitBpmnEdge(final BPMNEdge that) {
                        bpmndiMap.put(that.getBpmnElement(), that);
                    }

                    @Override public void visitBpmnShape(final BPMNShape that) {
                        bpmndiMap.put(that.getBpmnElement(), that);
                    }
                });
            }
        }
	*/
        definitions.accept(new TraversingVisitor(new DepthFirstTraverserImpl(), new BaseVisitor() {
            @Override public void visit(final BPMNEdge that) {
                bpmndiMap.put(that.getBpmnElement(), that);
            }

            @Override public void visit(final BPMNShape that) {
                bpmndiMap.put(that.getBpmnElement(), that);
            }
        }));

        return bpmndiMap;
    }

    /**
     * Find all configurable gateways with a configuration specified.
     *
     * @param definitions  a Configurable BPMN document
     * @return the gateways which have a <code>configurable/configuration</code> extension element
     */
    public static Set<TGateway> findConfiguredGateways(final TDefinitions definitions) {

        // Output value
        final Set<TGateway> reconfiguredGatewaySet = new HashSet<TGateway>();

	/*
        for (BPMNDiagram bpmnDiagram : definitions.getBPMNDiagram()) {
            for (JAXBElement<? extends DiagramElement> jaxbElement : bpmnDiagram.getBPMNPlane().getDiagramElement()) {
                DiagramElement element = jaxbElement.getValue();
                element.acceptVisitor(new BaseVisitor() {
                    @Override public void visit(final BPMNShape that) {
                        super.visitBpmnShape(that);
                        that.getBpmnElement().acceptVisitor(this);
                    }

                    @Override public void visit(final TGateway that) {
                        super.visitBaseElement(that);

                        if (gatewayConfigurationType(that) != null) {
                            reconfiguredGatewaySet.add(that);
                        }
                    }
                });
            }
        }
	*/
        definitions.accept(new TraversingVisitor(new DepthFirstTraverserImpl(), new BaseVisitor() {
            @Override public void visit(final TGateway that) {
                // TODO - add a check for the configuration element(!)
                reconfiguredGatewaySet.add(that);
            }

            @Override public void visit(final TComplexGateway that)    { visit((TGateway) that); }
            @Override public void visit(final TEventBasedGateway that) { visit((TGateway) that); }
            @Override public void visit(final TExclusiveGateway that)  { visit((TGateway) that); }
            @Override public void visit(final TInclusiveGateway that)  { visit((TGateway) that); }
            @Override public void visit(final TParallelGateway that)   { visit((TGateway) that); }
        }));

        return reconfiguredGatewaySet;
    }

    /**
     * Find sequence flows which have been configured via the <code>configurable/configuration/@sourceRef</code>
     * and <code>@targetRef</code> attributes to be absent from the configured process.
     *
     * @param reconfiguredGatewaySet  a (non-null) set of gateways, all which must have
     *     <code>configurable/configuration</code> extension elements
     * @return all sequence flows which are configured to be absent
     */
    public static Set<TSequenceFlow> findAbsentSequenceFlows(final Set<TGateway> reconfiguredGatewaySet) {

        // Return value
        final Set<TSequenceFlow> absentFlowSet = new HashSet<TSequenceFlow>();

        for (TGateway gateway : reconfiguredGatewaySet) {

            assert gateway != null : "Null gateway";
            assert gateway.getExtensionElements() != null
                : gateway.getId() + " has no extension elements";
            assert getFirstExtensionElementOfType(gateway.getExtensionElements()) != null
                : gateway.getId() + " is not configurable";
            assert getFirstExtensionElementOfType(gateway.getExtensionElements()).getConfiguration() != null
                : gateway.getId() + " is not configured";
            Configurable.Configuration configuration = getFirstExtensionElementOfType(gateway.getExtensionElements())
                                                              .getConfiguration();

            // Handle @sourceRefs
            switch (gateway.getGatewayDirection()) {
                case CONVERGING:
                case MIXED:
                    absentFlowSet.addAll(getIncomingSequenceFlows(gateway));  // was originally gateway.get_incomingSequenceFlows()
                    absentFlowSet.removeAll(configuration.getSourceRefs());
                    break;
                default:
                    // no action
            }

            // Handle @targetRefs
            switch (gateway.getGatewayDirection()) {
                case DIVERGING:
                case MIXED:
                    absentFlowSet.addAll(getOutgoingSequenceFlows(gateway));  // was originally gateway.get_outgoingSequenceFlows()
                    absentFlowSet.removeAll(configuration.getTargetRefs());
                    break;
                default:
                    // no action
            }
        }

        return absentFlowSet;
    }

    /**
     * Identify process elements which do not occur in any possible process execution trace.
     *
     * @param definitions  a BPMN model
     * @return any elements of the model aren't connected to both a start event and an end event
     */
    static Set<TFlowElement> findOrphans(final TDefinitions definitions) {

        Set<TFlowElement> all      = new HashSet<>();
        Set<TFlowElement> canStart = new HashSet<>();
        Set<TFlowElement> canEnd   = new HashSet<>();

        for (final JAXBElement<? extends TRootElement> jaxbElement : definitions.getRootElement()) {
            TRootElement rootElement = jaxbElement.getValue();
            if (rootElement instanceof TProcess) {
                for (final JAXBElement<? extends TFlowElement> jaxbElement2 : ((TProcess) rootElement).getFlowElement()) {
                    TFlowElement element = jaxbElement2.getValue();
                    all.add(element);
                    if (element instanceof TStartEvent) {
                        mark(element, canStart, Direction.FORWARDS);
                    } else if (element instanceof TEndEvent) {
                        mark(element, canEnd, Direction.BACKWARDS);
                    }
                }
            }
        }

        // Compose and return the set of orphan elements
        canStart.retainAll(canEnd);  // the intersection of elements with starts and with ends
        all.removeAll(canStart);      // elements lacking either a start or an end
        return all;
    }

    /**
     * Parameter type for the {@link #mark} method.
     */
    private enum Direction {
        /** Traverse incoming sequence flows. */
        BACKWARDS,
        /** Traverse outgoing sequence flows. */
        FORWARDS,
        /** Traverse associations and message flows. */
        ASSOCIATED
    };

    /**
     * Recursively populate a set with a flow element and its incoming or outgoing elements.
     *
     * @param element  the initial element to mark
     * @param markedSet  the set of marked elements
     * @param direction  whether to propagate marking to incoming or outgoing flows
     */
    private static void mark(final TFlowElement element, final Set<TFlowElement> markedSet, final Direction direction) {

        // Don't try to traverse null references
        if (element == null) { return; }

        // If this element has already been done, we don't need to do it again
        if (markedSet.contains(element)) { return; }

        // Recursively mark the element and all elements upstream/downstream from it
	/* FOO
        element.acceptVisitor(new AbstractVisitor() {
            @Override public void visitBaseElement(final TBaseElement that) {
                super.visitBaseElement(that);

                markedSet.add(element);
            }

            @Override public void visitEdge(final Edge that) {
                super.visitEdge(that);

                if (direction == Direction.ASSOCIATED) {
                    mark(that.getSourceRef(), markedSet, Direction.ASSOCIATED);
                    mark(that.getTargetRef(), markedSet, Direction.ASSOCIATED);
                }
            }

            @Override public void visitSequenceFlow(final TSequenceFlow that) {
                super.visitSequenceFlow(that);

                if (direction == Direction.BACKWARDS) {
                    mark(that.getSourceRef(), markedSet, Direction.BACKWARDS);
                } else if (direction == Direction.FORWARDS) {
                    mark(that.getTargetRef(), markedSet, Direction.FORWARDS);
                }
            }

            @Override public void visitFlowElement(final TFlowElement that) {
                super.visitFlowElement(that);

                for (Edge edge : that.getIncoming()) {
                    if (!(edge instanceof TSequenceFlow)) {
                        mark(edge, markedSet, Direction.ASSOCIATED);
                    }
                }

                for (Edge edge : that.getOutgoing()) {
                    if (!(edge instanceof TSequenceFlow)) {
                        mark(edge, markedSet, Direction.ASSOCIATED);
                    }
                }
            }

            @Override public void visitFlowNode(final TFlowNode that) {
                super.visitFlowNode(that);

                if (direction == Direction.BACKWARDS) {
                    for (Edge edge : getIncomingSequenceFlows(that)) {
                        mark(edge, markedSet, Direction.BACKWARDS);
                    }
                }

                if (direction == Direction.FORWARDS) {
                    for (Edge edge : getOutgoingSequenceFlows(that)) {
                        mark(edge, markedSet, Direction.FORWARDS);
                    }
                }
            }
        });
        */
    }

    /**
     * Remove process elements from a model.
     *
     * The corresponding diagram elements will also be removed.
     *
     * @param definitions  a BPMN model
     * @param pruningSet  process elements to be removed from the model
     */
    static void prune(final TDefinitions definitions, final Set<TBaseElement> pruningSet) {

        // Remove process elements in the pruning set
        for (final JAXBElement<? extends TRootElement> jaxbElement : definitions.getRootElement()) {
            TRootElement rootElement = jaxbElement.getValue();
            if (rootElement instanceof TProcess) {
                final TProcess process = (TProcess) rootElement;

                // Remove any references this process contains to pruned elements
                for (final JAXBElement<? extends TFlowElement> jaxbElement2 : process.getFlowElement()) {
                    TFlowElement flowElement = jaxbElement2.getValue();
                    substituteReferences(flowElement, pruningSet, null);
                }

                // Remove the pruned elements from this process
                process.getFlowElement().removeAll(pruningSet);
            }
        }

        // Remove diagram elements corresponding to any of the pruned process elements
        for (final BPMNDiagram bpmnDiagram : definitions.getBPMNDiagram()) {
            for (JAXBElement<? extends DiagramElement> jaxbElement : new ArrayList<JAXBElement<? extends DiagramElement>>(bpmnDiagram.getBPMNPlane().getDiagramElement())) {
                DiagramElement diagramElement = jaxbElement.getValue();
                if (diagramElement instanceof BPMNEdge) {
                    BPMNEdge edge = (BPMNEdge) diagramElement;

                    if (pruningSet.contains(edge.getBpmnElement())) {
                        bpmnDiagram.getBPMNPlane().getDiagramElement().remove(jaxbElement);
                    }
                } else if (diagramElement instanceof BPMNShape) {
                    BPMNShape shape = (BPMNShape) diagramElement;

                    if (pruningSet.contains(shape.getBpmnElement())) {
                        bpmnDiagram.getBPMNPlane().getDiagramElement().remove(jaxbElement);
                    }
                }
            }
        }
    }

    /**
     * Substitutes any references on a BPMN element which occur in the specified set.
     *
     * @param element  an arbitrary BPMN element, which will be mutated
     * @param substitutionSet  process elements to be removed from the model
     * @param newReference  Any of the elements references in the substitutionSet are changed to this value.
     *     Note that <code>null</code> is both a valid and a typical substitution value, and indicates simple deletion.
     * @throws ClassCastException if newReference isn't a suitable substitution
     */
    private static void substituteReferences(final TBaseElement      element,
                                             final Set<TBaseElement> substitutionSet,
                                             final TBaseElement      newReference) throws ClassCastException {

        assert !substitutionSet.contains(newReference);

	/*
        element.acceptVisitor(new AbstractVisitor() {

            @Override public void visitDataObjectReference(final DataObjectReference that) {
                super.visitDataObjectReference(that);

                if (substitutionSet.contains(that.getDataObjectRef())) {
                    that.setDataObjectRef((DataObject) newReference);
                }
            }

            @Override public void visitDataStoreReference(final DataStoreReference that) {
                super.visitDataStoreReference(that);

                if (substitutionSet.contains(that.getDataStoreRef())) {
                    that.setDataStoreRef((DataStore) newReference);
                }
            }

            @Override public void visitEdge(final Edge that) {
                super.visitEdge(that);

                if (substitutionSet.contains(that.getSourceRef())) {
                    that.setSourceRef((FlowElement) newReference);
                }

                if (substitutionSet.contains(that.getTargetRef())) {
                    that.setTargetRef((FlowElement) newReference);
                }
            }

            @Override public void visitFlowElement(final FlowElement that) {
                super.visitFlowElement(that);

                if (that.getIncoming().removeAll(substitutionSet)) {
                    that.getIncoming().add((Edge) newReference);
                }

                if (that.getOutgoing().removeAll(substitutionSet)) {
                    that.getOutgoing().add((Edge) newReference);
                }
            }

            @Override public void visitGatewayWithDefaultFlow(final GatewayWithDefaultFlow that) {
                super.visitGatewayWithDefaultFlow(that);

                if (substitutionSet.contains(that.getDefault())) {
                    that.setDefault((SequenceFlow) newReference);
                }
            }

            @Override public void visitMessageFlow(final MessageFlow that) {
                super.visitMessageFlow(that);

                if (substitutionSet.contains(that.getMessageRef())) {
                    that.setMessageRef((Message) newReference);
                }
            }
        });
        */
    }

    /**
     * Find and remove all trivial gateways from a BPMN document.
     *
     * @param definitions  a BPMN XML model, which will be mutated
     */
    static void removeTrivialGateways(final TDefinitions definitions) {
        for (final JAXBElement<? extends TRootElement> jaxbElement : definitions.getRootElement()) {
            TRootElement rootElement = jaxbElement.getValue();
            if (rootElement instanceof TProcess) {
                final TProcess process = (TProcess) rootElement;

                for (final JAXBElement<? extends TFlowElement> jaxbElement2 : new ArrayList<JAXBElement<? extends TFlowElement>>(process.getFlowElement())) {
                    TFlowElement flowElement = jaxbElement2.getValue();
                    if (flowElement instanceof TGateway) {
                        TGateway gateway = (TGateway) flowElement;
                        if (getIncomingSequenceFlows(gateway).size() == 1
                         && getOutgoingSequenceFlows(gateway).size() == 1) {
                            removeTrivialGateway(definitions, gateway);
                        }
                    }
                }
            }
        }
    }

    /**
     * Remove a trivial gateway from the BPMN document.
     *
     * A trivial gateway is a gateway with just one incoming and one outgoing sequence flow.
     *
     * @param definitions  a BPMN XML model, which will be mutated
     * @param gateway  a trivial gateway
     */
    static void removeTrivialGateway(final TDefinitions definitions, final TGateway gateway) {
        final TSequenceFlow incomingFlow = getIncomingSequenceFlows(gateway).get(0),
                            outgoingFlow = getOutgoingSequenceFlows(gateway).get(0);

	throw new RuntimeException("Not yet reimplemented");
/*
        Map<TBaseElement, DiagramElement> bpmndiMap = findBpmndiMap(definitions);

        // Append the outgoing flow's waypoints to the incoming flow
        assert bpmndiMap.containsKey(incomingFlow);
        assert bpmndiMap.containsKey(outgoingFlow);
        ((Edge) bpmndiMap.get(incomingFlow)).getWaypoint().addAll(
            ((Edge) bpmndiMap.get(outgoingFlow)).getWaypoint()
        );

        // Connect the incoming flow to the outgoing flow's target
        incomingFlow.setTargetRef(outgoingFlow.getTargetRef());

        // Anything connected to the outgoing flow is now connected to the incoming flow instead
        for (Edge edge : outgoingFlow.getIncoming()) {
            assert edge.getTargetRef() == outgoingFlow;
            edge.setTargetRef(incomingFlow);
            incomingFlow.getIncoming().add(edge);
        }
        outgoingFlow.getIncoming().clear();

        for (Edge edge : outgoingFlow.getOutgoing()) {
            assert edge.getSourceRef() == outgoingFlow;
            edge.setSourceRef(incomingFlow);
            incomingFlow.getOutgoing().add(edge);
        }
        outgoingFlow.getOutgoing().clear();

        // Anything that used to reference the outgoing flow now references the incoming flow
        for (final JAXBElement<? extends TRootElement> jaxbElement : definitions.getRootElement()) {
            TRootElement rootElement = jaxbElement.getValue();
            if (rootElement instanceof TProcess) {
                final TProcess process = (TProcess) rootElement;

                for (final JAXBElement<? extends TFlowElement> jaxbElement2 : new ArrayList<JAXBElement<? extends TFlowElement>>(process.getFlowElement())) {
                    TFlowElement flowElement = jaxbElement2.getValue();
                    substituteReferences(flowElement, Collections.singleton((TBaseElement) outgoingFlow), incomingFlow);
                }
            }
        }

        // Remove the gateway and the outgoing flow
        Set<TBaseElement> pruningSet = new HashSet<TBaseElement>();
        pruningSet.add(gateway);
        pruningSet.add(outgoingFlow);
        prune(definitions, pruningSet);
*/
    }

    /**
     * Extract the configured type of a configured gateway.
     *
     * @param gateway  a configured gateway
     * @return the <code>configurable/configuration/@type</code> extension attribute of
     *     the gateway, or <code>null</code> if the gateway has no such attribute
     */
    static TGatewayType gatewayConfigurationType(final TGateway gateway) {

        // Drill down to the configurable/configuration/@type attribute
        TExtensionElements extensionElements = gateway.getExtensionElements();
        if (extensionElements != null) {
            Configurable configurable = getFirstExtensionElementOfType(extensionElements);
            if (configurable != null) {
                Configurable.Configuration configuration = configurable.getConfiguration();
                if (configuration != null) {
                    return configuration.getType();
                }
            }
        }

        // Otherwise, indicate that the attribute is absent
        return null;
    }

    /**
     * Change the type of a configured gateway to match its configured type and remove its {@link Configurable} extension element.
     *
     * @param definitions  a BPMN model
     * @param gateway  a configured gateway
     */
    static void replaceConfiguredGateway(final TDefinitions definitions, final TGateway gateway) {

        TGateway reconfiguredGateway = null;

        // From the processes, replace gateways
        for (final JAXBElement<? extends TRootElement> jaxbElement : definitions.getRootElement()) {
            TRootElement rootElement = jaxbElement.getValue();
            if (rootElement instanceof TProcess) {
                final TProcess process = (TProcess) rootElement;

                for (final JAXBElement<? extends TFlowElement> jaxbElement2 : new ArrayList<JAXBElement<? extends TFlowElement>>(process.getFlowElement())) {
                    TFlowElement flowElement = jaxbElement2.getValue();
                    if (flowElement.equals(gateway)) {

                        // Create the replacement gateway
                        TGatewayType gatewayType = gatewayConfigurationType(gateway);
                        switch (gatewayType) {
                        case DATA_BASED_EXCLUSIVE:
                            reconfiguredGateway = new TExclusiveGateway();
                            break;
                        case EVENT_BASED_EXCLUSIVE:
                            reconfiguredGateway = new TEventBasedGateway();
                            break;
                        case INCLUSIVE:
                            reconfiguredGateway = new TInclusiveGateway();
                            break;
                        case PARALLEL:
                            reconfiguredGateway = new TParallelGateway();
                            break;
                        default:
                            assert false : "Unknown gateway type: " + gatewayType;
                        }
                        assert reconfiguredGateway != null;

                        // Populate the base element properties of the replacement gateway
                        //reconfiguredGateway.getAny().addAll(flowElement.getAny());
                        reconfiguredGateway.getDocumentation().addAll(flowElement.getDocumentation());
                        reconfiguredGateway.setId(flowElement.getId());


                        // Extension elements will be mutated (removing <configurable>), so need to clone the reference

                        // Clone the lists of extension elements
                        TExtensionElements extensionElements = new TExtensionElements();
                        extensionElements.getAny().addAll(flowElement.getExtensionElements().getAny());
                        //extensionElements.getAnyExternal().addAll(flowElement.getExtensionElements().getAnyExternal());

                        // Configured gateway loses its "configurable" element
                        Configurable configurable = getFirstExtensionElementOfType(extensionElements);
                        assert configurable != null : "No <configurable> element was found";
                        boolean removed = extensionElements.getAny().remove(configurable);
                        assert removed : "No <configurable> element was removed";

                        // If "configurable" was the only extension element, extensionElements goes away
                        if (!(extensionElements.getAny().isEmpty() /* && extensionElements.getAnyExternal().isEmpty() */)) {
                            reconfiguredGateway.setExtensionElements(extensionElements);
                        }


                        // Populate the flow element properties
                        reconfiguredGateway.setAuditing(flowElement.getAuditing());
                        reconfiguredGateway.setMonitoring(flowElement.getMonitoring());
                        reconfiguredGateway.setName(flowElement.getName());

                        // Populate the flow node properties
                        reconfiguredGateway.getIncoming().addAll(((TFlowNode) flowElement).getIncoming());
                        reconfiguredGateway.getOutgoing().addAll(((TFlowNode) flowElement).getOutgoing());

                        // Populate the gateway properties
                        reconfiguredGateway.setGatewayDirection(((TGateway) flowElement).getGatewayDirection());

                        // Populate the default sequence flow property if it's present in both the original and reconfigured gateways
                        TSequenceFlow defaultSequenceFlow = null;
                        if (flowElement instanceof TComplexGateway) {
                            defaultSequenceFlow = ((TComplexGateway) flowElement).getDefault();
                        } else if (flowElement instanceof TExclusiveGateway) {
                            defaultSequenceFlow = ((TExclusiveGateway) flowElement).getDefault();
                        } else if (flowElement instanceof TInclusiveGateway) {
                            defaultSequenceFlow = ((TInclusiveGateway) flowElement).getDefault();
                        }

                        if (defaultSequenceFlow != null) {
                            if (reconfiguredGateway instanceof TComplexGateway) {
                                ((TComplexGateway) reconfiguredGateway).setDefault(defaultSequenceFlow);
                            } else if (flowElement instanceof TExclusiveGateway) {
                                ((TExclusiveGateway) reconfiguredGateway).setDefault(defaultSequenceFlow);
                            } else if (flowElement instanceof TInclusiveGateway) {
                                ((TInclusiveGateway) reconfiguredGateway).setDefault(defaultSequenceFlow);
                            }
                        }

                        // Replace the original gateway with the the reconfigured one, in situ
                        process.getFlowElement().set(process.getFlowElement().indexOf(gateway), (new org.omg.spec.bpmn._20100524.model.ObjectFactory()).createGateway(reconfiguredGateway));
                    }
                }
            }
        }

        if (reconfiguredGateway != null) {
            // Update all references to the old gateway to refer to the new gateway
            for (final JAXBElement<? extends TRootElement> jaxbElement2 : definitions.getRootElement()) {
                TRootElement rootElement = jaxbElement2.getValue();
                if (rootElement instanceof TProcess) {
                    final TProcess process = (TProcess) rootElement;

                    // Substitute references to the new gateway for the old one in each process element
                    for (final JAXBElement<? extends TFlowElement> jaxbElement : process.getFlowElement()) {
                        TFlowElement flowElement = jaxbElement.getValue();
                        Set<TBaseElement> substitutionSet = new HashSet();
                        substitutionSet.add(gateway);
                        substituteReferences(flowElement, substitutionSet, reconfiguredGateway);
                    }
                }
            }

            // Update the diagram element's @bpmnElement reference (not needed since the reconfiguredGateway has the same ID as the original)
            //((BPMNShape) findBpmndiMap(definitions).get(gateway)).setBpmnElement(reconfiguredGateway);
        }
    }

    /**
     * @param extensionElements
     * @return the first extension element of the given type, or <code>null</code> if no such element exists
     */
    static Configurable getFirstExtensionElementOfType(TExtensionElements extensionElements) {
        for (Object extensionElement: extensionElements.getAny()) {
            if (extensionElement instanceof Configurable) {
                return (Configurable) extensionElement;
            }
        }

        return null;  // indicates no Configurable extension elements were present
    }

    private static List<TSequenceFlow> getIncomingSequenceFlows(TFlowNode node) {
	throw new RuntimeException("Not yet implemented");
    }

    private static List<TSequenceFlow> getOutgoingSequenceFlows(TFlowNode node) {
	throw new RuntimeException("Not yet implemented");
    }

    /**
     * Read a Configurable BPMN XML document from stdin, configure it, and write the configured BPMN XML to stdout.
     *
     * @param arg  command line arguments are ignored
     * @throws JAXBException if XML parsing fails
     */
    /*
    public static void main(final String[] arg) throws JAXBException {

        // Read a BPMN XML document from standard input
        JAXBContext context = JAXBContext.newInstance(TDefinitions.class,
                                                      ConfigurationAnnotationAssociation.class,
                                                      ConfigurationAnnotationShape.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setProperty(IDResolver.class.getName(), new DefinitionsIDResolver());
        TDefinitions definitions = (TDefinitions) unmarshaller.unmarshal(System.in);

        // Exercise the method
        ConfigurationAlgorithm.configure(definitions);

        // Write the configured BPMN XML document to standard output
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new BPMNPrefixMapper());
        marshaller.marshal(definitions, System.out);
    }
    */
}
