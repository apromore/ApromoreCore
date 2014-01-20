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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
//import com.sun.xml.bind.IDResolver;

/*
import de.hpi.bpmn2_0.dto.BaseElement;
import de.hpi.bpmn2_0.dto.Definitions;
import de.hpi.bpmn2_0.dto.FlowElement;
import de.hpi.bpmn2_0.dto.FlowNode;
import de.hpi.bpmn2_0.dto.Process;
import de.hpi.bpmn2_0.dto.bpmndi.BPMNDiagram;
import de.hpi.bpmn2_0.dto.bpmndi.BPMNEdge;
import de.hpi.bpmn2_0.dto.bpmndi.BPMNShape;
import de.hpi.bpmn2_0.dto.bpmndi.di.DiagramElement;
import de.hpi.bpmn2_0.dto.connector.Edge;
import de.hpi.bpmn2_0.dto.connector.MessageFlow;
import de.hpi.bpmn2_0.dto.connector.SequenceFlow;
import de.hpi.bpmn2_0.dto.data_object.DataObject;
import de.hpi.bpmn2_0.dto.data_object.DataObjectReference;
import de.hpi.bpmn2_0.dto.data_object.DataStore;
import de.hpi.bpmn2_0.dto.data_object.DataStoreReference;
import de.hpi.bpmn2_0.dto.data_object.Message;
import de.hpi.bpmn2_0.dto.event.EndEvent;
import de.hpi.bpmn2_0.dto.event.StartEvent;
import de.hpi.bpmn2_0.dto.extension.ExtensionElements;
import de.hpi.bpmn2_0.dto.extension.synergia.Configurable;
import de.hpi.bpmn2_0.dto.extension.synergia.Configurable.Configuration;
import de.hpi.bpmn2_0.dto.extension.synergia.ConfigurationAnnotationAssociation;
import de.hpi.bpmn2_0.dto.extension.synergia.ConfigurationAnnotationShape;
import de.hpi.bpmn2_0.dto.extension.synergia.TGatewayType;
import static de.hpi.bpmn2_0.dto.extension.synergia.TGatewayType.DATA_BASED_EXCLUSIVE;
import static de.hpi.bpmn2_0.dto.extension.synergia.TGatewayType.EVENT_BASED_EXCLUSIVE;
import static de.hpi.bpmn2_0.dto.extension.synergia.TGatewayType.INCLUSIVE;
import static de.hpi.bpmn2_0.dto.extension.synergia.TGatewayType.PARALLEL;
import de.hpi.bpmn2_0.dto.gateway.Gateway;
import de.hpi.bpmn2_0.dto.gateway.GatewayWithDefaultFlow;
import de.hpi.bpmn2_0.dto.gateway.EventBasedGateway;
import de.hpi.bpmn2_0.dto.gateway.ExclusiveGateway;
import de.hpi.bpmn2_0.dto.gateway.InclusiveGateway;
import de.hpi.bpmn2_0.dto.gateway.ParallelGateway;
import de.hpi.bpmn2_0.transformation.AbstractVisitor;
import de.hpi.bpmn2_0.transformation.BPMNPrefixMapper;
*/
import org.apromore.canoniser.bpmn.bpmn.BpmnDefinitions;
import org.apromore.canoniser.bpmn.bpmn.BpmnObjectFactory;
import org.apromore.canoniser.exception.CanoniserException;
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
        prune(definitions, (Set) findAbsentSequenceFlows((BpmnDefinitions) definitions, reconfiguredGatewaySet));

        // Replace configured gateways with the configured type, and remove their <configurable> element
        for (TGateway gateway : reconfiguredGatewaySet) {
            replaceConfiguredGateway(definitions, gateway);
        }

        // Remove trivial gateways
        removeTrivialGateways(definitions);

        // Find and remove disconnected elements
        Set<TBaseElement> disconnectedSet = findOrphans(definitions);
        prune(definitions, (Set) disconnectedSet);
    }

    /**
     * Create a mapping from process elements to diagram elements.
     *
     * @param definitions  a BPMN XML document
     * @return  the reverse mapping of the <code>bpmnElement</code> attribute
     */
    public static Map<TBaseElement, DiagramElement> findBpmndiMap(final TDefinitions definitions) {
        final Map<TBaseElement, DiagramElement> bpmndiMap = new HashMap<>();

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
        definitions.accept(new TraversingVisitor(new MyTraverser(), new BaseVisitor() {
            @Override public void visit(final BPMNEdge that) {
                try {
                    TBaseElement processElement = ((BpmnDefinitions) definitions).findElement(that.getBpmnElement());
                    assert processElement != null : "Diagram edge " + that.getId() + " has nonexistent bpmnElement " + that.getBpmnElement();
                    bpmndiMap.put(processElement, that);
                } catch (CanoniserException e) {
                    e.printStackTrace();
                }
            }

            @Override public void visit(final BPMNShape that) {
                try {
                    TBaseElement processElement = ((BpmnDefinitions) definitions).findElement(that.getBpmnElement());
                    assert processElement != null : "Diagram shape " + that.getId() + " has nonexistent bpmnElement " + that.getBpmnElement();
                    bpmndiMap.put(processElement, that);
                } catch (CanoniserException e) {
                    e.printStackTrace();
                }
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

        definitions.accept(new TraversingVisitor(new MyTraverser(), new InheritingVisitor() {
            @Override public void visit(final TGateway that) {
                that.accept(new TraversingVisitor(new MyTraverser(), new BaseVisitor() {
                    @Override public void visit(final Configurable.Configuration configuration) {
                        reconfiguredGatewaySet.add(that);
                    }
                }));
            }
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
    public static Set<TSequenceFlow> findAbsentSequenceFlows(final BpmnDefinitions definitions, final Set<TGateway> reconfiguredGatewaySet) {

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
                    for (QName qName: gateway.getIncoming()) {
                        try {
                            absentFlowSet.add((TSequenceFlow) definitions.findElement(qName));
                        } catch (CanoniserException e) { e.printStackTrace(); }
                    }
                    // Preceding loop replaced: absentFlowSet.addAll(getIncomingSequenceFlows(gateway));
                    absentFlowSet.removeAll(configuration.getSourceRefs());
                    break;
                default:
                    // no action
            }

            // Handle @targetRefs
            switch (gateway.getGatewayDirection()) {
                case DIVERGING:
                case MIXED:
                    for (QName qName: gateway.getOutgoing()) {
                        try {
                            absentFlowSet.add((TSequenceFlow) definitions.findElement(qName));
                        } catch (CanoniserException e) { e.printStackTrace(); }
                    }
                    // Preceding loop replaces: absentFlowSet.addAll(getOutgoingSequenceFlows(gateway));
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
    static Set<TBaseElement> findOrphans(final TDefinitions definitions) {

        final Set<TBaseElement> all      = new HashSet<>();  // all elements
        final Set<TBaseElement> canStart = new HashSet<>();  // elements connected to a start event
        final Set<TBaseElement> canEnd   = new HashSet<>();  // elements connected to an end event

        final Multimap<TBaseElement,TBaseElement> incomingMap = HashMultimap.create();
        final Multimap<TBaseElement,TBaseElement> outgoingMap = HashMultimap.create();

        // Populate incomingMap by traversing all the edges (not the nodes) of the document graph
        definitions.accept(new TraversingVisitor(new MyTraverser() {
            @Override public void traverse(Configurable.Configuration aBean, Visitor aVisitor) {}
        }, new InheritingVisitor() {
            @Override public void visit(final TAssociation that) {
                try {
                    TBaseElement source = ((BpmnDefinitions) definitions).findElement(that.getSourceRef());
                    if (source != null) {
                        incomingMap.put(that, source);
                        incomingMap.put(source, that);  // for the purposes of marking, associations count in both directions
                    }

                    TBaseElement target = ((BpmnDefinitions) definitions).findElement(that.getTargetRef());
                    if (target != null) {
                        incomingMap.put(target, that);
                        incomingMap.put(that, target);  // for the purposes of marking, associations count in both directions
                    }
                } catch (CanoniserException e) {
                    e.printStackTrace();
                }
            }

            @Override public void visit(final TDataAssociation that) {
                for (JAXBElement<Object> jeo: that.getSourceRef()) {
                    TBaseElement source = (TBaseElement) jeo.getValue();
                    incomingMap.put(that, source);
                    incomingMap.put(source, that);  // for the purposes of marking, data associations count in both directions
                }

                TBaseElement target = that.getTargetRef();
                if (target != null) {
                    incomingMap.put(target, that);
                    incomingMap.put(that, target);  // for the purposes of marking, data associations count in both directions
                }
            }

            @Override public void visit(final TMessageFlow that) {
                try {
                    TBaseElement source = ((BpmnDefinitions) definitions).findElement(that.getSourceRef());
                    if (source != null) {
                        incomingMap.put(that, source);
                        incomingMap.put(source, that);  // for the purposes of marking, message flows count in both directions
                    }

                    TBaseElement target = ((BpmnDefinitions) definitions).findElement(that.getTargetRef());
                    if (target != null) {
                        incomingMap.put(target, that);
                        incomingMap.put(that, that);  // for the purposes of marking, message flows count in both directions
                    }
                } catch (CanoniserException e) {
                    e.printStackTrace();
                }
            }

            @Override public void visit(final TSequenceFlow that) {
                if (that.getSourceRef() != null) {
                    incomingMap.put(that, that.getSourceRef());
                }

                if (that.getTargetRef() != null) {
                    incomingMap.put(that.getTargetRef(), that);
                }
            }
        }));

        // Populate outgoingMap
        Multimaps.invertFrom(incomingMap, outgoingMap);

        // Populate the sets of elements: all, canStart, canEnd
        definitions.accept(new TraversingVisitor(new MyTraverser() {
            @Override public void traverse(Configurable.Configuration aBean, Visitor aVisitor) {}
        }, new InheritingVisitor() {
            @Override public void visit(final TArtifact that) {
                super.visit(that);
                all.add(that);
            }

            @Override public void visit(final TDataAssociation that) {
                super.visit(that);
                all.add(that);
            }

            @Override public void visit(final TEndEvent that) {
                super.visit(that);
                mark((BpmnDefinitions) definitions, that, canEnd, Direction.BACKWARDS, incomingMap, outgoingMap);
            }

            @Override public void visit(final TFlowElement that) {
                super.visit(that);
                all.add(that);
            }

            @Override public void visit(final TGroup that) {
                // This artifact doesn't get added to "all", so no super call
            }

            @Override public void visit(final TIntermediateCatchEvent that) {
                super.visit(that);
                for (JAXBElement<? extends TEventDefinition> jed: that.getEventDefinition()) {
                    if (jed.getValue() instanceof TLinkEventDefinition) {
                        mark((BpmnDefinitions) definitions, that, canStart, Direction.FORWARDS, incomingMap, outgoingMap);
                        break;
                    }
                }
            }

            @Override public void visit(final TIntermediateThrowEvent that) {
                super.visit(that);
                for (JAXBElement<? extends TEventDefinition> jed: that.getEventDefinition()) {
                    if (jed.getValue() instanceof TLinkEventDefinition) {
                        mark((BpmnDefinitions) definitions, that, canEnd, Direction.BACKWARDS, incomingMap, outgoingMap);
                        break;
                    }
                }
            }

            /* TODO: support message flow pruning
            @Override public void visit(final TMessageFlow that) {
                super.visit(that);
                all.add(that);
            }
            */

            @Override public void visit(final TStartEvent that) {
                super.visit(that);
                mark((BpmnDefinitions) definitions, that, canStart, Direction.FORWARDS, incomingMap, outgoingMap);
            }
        }));

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
     * @param incomingMap  for each document element, its set of incoming elements
     * @param outgoingMap  for each document element, its set of outgoing elements
     */
    private static void mark(final BpmnDefinitions                     definitions,
                             final TBaseElement                        element,
                             final Set<TBaseElement>                   markedSet,
                             final Direction                           direction,
                             final Multimap<TBaseElement,TBaseElement> incomingMap,
                             final Multimap<TBaseElement,TBaseElement> outgoingMap) {

        // Don't try to traverse null references
        if (element == null) { return; }

        // If this element has already been done, we don't need to do it again
        if (markedSet.contains(element)) { return; }

        markedSet.add(element);

        if (element instanceof TAssociation) {
            TAssociation that = (TAssociation) element;
            if (direction == Direction.ASSOCIATED) {
                try {
                    TBaseElement source = definitions.findElement(that.getSourceRef());
                    if (source != null) {
                        mark(definitions, source, markedSet, Direction.ASSOCIATED, incomingMap, outgoingMap);
                    }

                    TBaseElement target = definitions.findElement(that.getTargetRef());
                    if (target != null) {
                        mark(definitions, target, markedSet, Direction.ASSOCIATED, incomingMap, outgoingMap);
                    }
                } catch (CanoniserException e) {
                    e.printStackTrace();
                }
            }
        }

        if (element instanceof TDataAssociation) {
            TDataAssociation that = (TDataAssociation) element;
            if (direction == Direction.ASSOCIATED) {
                for (JAXBElement<Object> jeo: that.getSourceRef()) {
                    mark(definitions, (TBaseElement) jeo.getValue(), markedSet, Direction.ASSOCIATED, incomingMap, outgoingMap);
                }
                mark(definitions, that.getTargetRef(), markedSet, Direction.ASSOCIATED, incomingMap, outgoingMap);
            }
        }

        if (element instanceof TMessageFlow) {
            TMessageFlow that = (TMessageFlow) element;
            if (direction == Direction.ASSOCIATED) {
                try {
                    TBaseElement source = definitions.findElement(that.getSourceRef());
                    if (source != null) {
                        mark(definitions, source, markedSet, Direction.ASSOCIATED, incomingMap, outgoingMap);
                    }

                    TBaseElement target = definitions.findElement(that.getTargetRef());
                    if (target != null) {
                        mark(definitions, target, markedSet, Direction.ASSOCIATED, incomingMap, outgoingMap);
                    }
                } catch (CanoniserException e) {
                    e.printStackTrace();
                }
            }
        }

        if (element instanceof TSequenceFlow) {
            TSequenceFlow that = (TSequenceFlow) element;
            if (direction == Direction.BACKWARDS) {
                mark(definitions, that.getSourceRef(), markedSet, Direction.BACKWARDS, incomingMap, outgoingMap);
            } else if (direction == Direction.FORWARDS) {
                mark(definitions, that.getTargetRef(), markedSet, Direction.FORWARDS, incomingMap, outgoingMap);
            }
        }

        if (element instanceof TFlowNode) {
            for (TBaseElement incomingElement: incomingMap.get(element)) {
                if (incomingElement instanceof TSequenceFlow) {
                    if (direction == Direction.BACKWARDS) {
                        mark(definitions, incomingElement, markedSet, Direction.BACKWARDS, incomingMap, outgoingMap);
                    }
                }
            }

            for (TBaseElement outgoingElement: outgoingMap.get(element)) {
                if (outgoingElement instanceof TSequenceFlow) {
                    if (direction == Direction.FORWARDS) {
                        mark(definitions, outgoingElement, markedSet, Direction.FORWARDS, incomingMap, outgoingMap);
                    }
                }
            }
         }

         if (element instanceof TFlowElement) {
             for (TBaseElement incomingElement: incomingMap.get(element)) {
                 if (incomingElement instanceof TAssociation || incomingElement instanceof TDataAssociation) {
                     mark(definitions, incomingElement, markedSet, Direction.ASSOCIATED, incomingMap, outgoingMap);
                 }
             }
         }
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
                    substituteReferences((BpmnDefinitions) definitions, flowElement, pruningSet, null);
                }
                // TODO: move the preceding loop into the substituteReferences call instead
                substituteReferences((BpmnDefinitions) definitions, process, pruningSet, null);
            }
        }

        // Remove diagram elements corresponding to any of the pruned process elements
        for (final BPMNDiagram bpmnDiagram : definitions.getBPMNDiagram()) {
            for (JAXBElement<? extends DiagramElement> jaxbElement : new ArrayList<JAXBElement<? extends DiagramElement>>(bpmnDiagram.getBPMNPlane().getDiagramElement())) {
                DiagramElement diagramElement = jaxbElement.getValue();

                try {
                    if (diagramElement instanceof BPMNEdge) {
                        BPMNEdge edge = (BPMNEdge) diagramElement;

                        if (pruningSet.contains(((BpmnDefinitions) definitions).findElement(edge.getBpmnElement()))) {
                            bpmnDiagram.getBPMNPlane().getDiagramElement().remove(jaxbElement);
                        }
                    } else if (diagramElement instanceof BPMNShape) {
                        BPMNShape shape = (BPMNShape) diagramElement;

                        if (pruningSet.contains(((BpmnDefinitions) definitions).findElement(shape.getBpmnElement()))) {
                            bpmnDiagram.getBPMNPlane().getDiagramElement().remove(jaxbElement);
                        }
                    }
                } catch (CanoniserException e) {
                    e.printStackTrace();
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
    private static void substituteReferences(final BpmnDefinitions   definitions,
                                             final TBaseElement      element,
                                             final Set<TBaseElement> substitutionSet,
                                             final TBaseElement      newReference) throws ClassCastException {

        assert !substitutionSet.contains(newReference);

        // create a QName version of substitutionSet
        final Set<QName> substitutionQNameSet = new HashSet<>();
        for (TBaseElement substitutedElement: substitutionSet) {
            substitutionQNameSet.add(new QName(definitions.getTargetNamespace(), substitutedElement.getId()));
        }

        element.accept(new InheritingVisitor() {

            @Override public void visit(final TAssociation that) {
                super.visit(that);

                try {
                    if (substitutionSet.contains(definitions.findElement(that.getSourceRef()))) {
                        that.setSourceRef(newReference == null ? null : new QName(definitions.getTargetNamespace(), newReference.getId()));
                    }

                    if (substitutionSet.contains(definitions.findElement(that.getTargetRef()))) {
                        that.setTargetRef(newReference == null ? null : new QName(definitions.getTargetNamespace(), newReference.getId()));
                    }
                } catch (CanoniserException e) {
                    e.printStackTrace();
                }
            }

            @Override public void visit(final TComplexGateway that) {
                super.visit(that);

                if (substitutionSet.contains(that.getDefault())) {
                    that.setDefault((TSequenceFlow) newReference);
                }
            }

            @Override public void visit(final Configurable.Configuration that) {
                super.visit(that);

                if (that.getSourceRefs().removeAll(substitutionSet)) {
                    if (newReference != null) {
                        that.getSourceRefs().add(newReference);
                    }
                }

                if (that.getTargetRefs().removeAll(substitutionSet)) {
                    if (newReference != null) {
                        that.getTargetRefs().add(newReference);
                    }
                }
            }

            @Override public void visit(final TDataAssociation that) {
                super.visit(that);

                for (JAXBElement<Object> jbe: new ArrayList<>(that.getSourceRef())) {
                    if (substitutionSet.contains(jbe.getValue())) {
                        boolean wasPresent = that.getSourceRef().remove(jbe);
                        if (wasPresent && newReference != null) {
                            that.getSourceRef().add((JAXBElement) (new BpmnObjectFactory()).createBaseElement(newReference));
                        }
                    }
                }

                if (substitutionSet.contains(that.getTargetRef())) {
                    that.setTargetRef(newReference);
                }
            }

            @Override public void visit(final TDataObjectReference that) {
                super.visit(that);

                if (substitutionSet.contains(that.getDataObjectRef())) {
                    that.setDataObjectRef((TDataObject) newReference);
                }
            }

            @Override public void visit(final TDataStoreReference that) {
                super.visit(that);

                if (substitutionSet.contains(that.getDataStoreRef())) {
                    that.setDataStoreRef(new QName(definitions.getTargetNamespace(), newReference.getId()));
                }
            }

            @Override public void visit(final TExclusiveGateway that) {
                super.visit(that);

                if (substitutionSet.contains(that.getDefault())) {
                    that.setDefault((TSequenceFlow) newReference);
                }
            }

            @Override public void visit(final TFlowNode that) {
                super.visit(that);

                if (that.getIncoming().removeAll(substitutionQNameSet)) {
                    if (newReference != null) {
                        that.getIncoming().add(new QName(definitions.getTargetNamespace(), newReference.getId()));
                    }
                }

                if (that.getOutgoing().removeAll(substitutionQNameSet)) {
                    if (newReference != null) {
                        that.getOutgoing().add(new QName(definitions.getTargetNamespace(), newReference.getId()));
                    }
                }
            }

            @Override public void visit(final TInclusiveGateway that) {
                super.visit(that);

                if (substitutionSet.contains(that.getDefault())) {
                    that.setDefault((TSequenceFlow) newReference);
                }
            }

            @Override public void visit(final TMessageFlow that) {
                super.visit(that);

                try {
                    if (substitutionSet.contains(definitions.findElement(that.getMessageRef()))) {
                        that.setMessageRef(new QName(definitions.getTargetNamespace(), newReference.getId()));
                    }
                } catch (CanoniserException e) {
                    e.printStackTrace();
                }
            }

            @Override public void visit(final TProcess that) {
                super.visit(that);

                for (JAXBElement<? extends TFlowElement> jfe: new ArrayList<>(that.getFlowElement())) {
                    if (substitutionSet.contains(jfe.getValue())) {
                        boolean wasPresent = that.getFlowElement().remove(jfe);
                        if (wasPresent && newReference != null) {
                            JAXBElement<TFlowElement> newJfe = (new BpmnObjectFactory()).createFlowElement((TFlowElement) newReference);
                            that.getFlowElement().add(newJfe);
                        }
                    }
                }
            }

            @Override public void visit(final TSequenceFlow that) {
                super.visit(that);

                if (substitutionSet.contains(that.getSourceRef())) {
                    that.setSourceRef((TFlowNode) newReference);
                }

                if (substitutionSet.contains(that.getTargetRef())) {
                    that.setTargetRef((TFlowNode) newReference);
                }
            }
        });
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
                        if (gateway.getIncoming().size() == 1
                         && gateway.getOutgoing().size() == 1) {
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
        final TSequenceFlow incomingFlow, outgoingFlow;

        try {
            incomingFlow = (TSequenceFlow) ((BpmnDefinitions) definitions).findElement(gateway.getIncoming().get(0));
            outgoingFlow = (TSequenceFlow) ((BpmnDefinitions) definitions).findElement(gateway.getOutgoing().get(0));

        Map<TBaseElement, DiagramElement> bpmndiMap = findBpmndiMap(definitions);

        // Append the outgoing flow's waypoints to the incoming flow
        assert bpmndiMap.containsKey(incomingFlow);
        assert bpmndiMap.containsKey(outgoingFlow);
        ((Edge) bpmndiMap.get(incomingFlow)).getWaypoint().addAll(
            ((Edge) bpmndiMap.get(outgoingFlow)).getWaypoint()
        );

        // Connect the incoming flow to the outgoing flow's target
        incomingFlow.setTargetRef(outgoingFlow.getTargetRef());

        /*
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
        */

        // Anything that used to reference the outgoing flow now references the incoming flow
        for (final JAXBElement<? extends TRootElement> jaxbElement : definitions.getRootElement()) {
            TRootElement rootElement = jaxbElement.getValue();
            if (rootElement instanceof TProcess) {
                final TProcess process = (TProcess) rootElement;

                for (final JAXBElement<? extends TFlowElement> jaxbElement2 : new ArrayList<JAXBElement<? extends TFlowElement>>(process.getFlowElement())) {
                    TFlowElement flowElement = jaxbElement2.getValue();
                    substituteReferences((BpmnDefinitions) definitions, flowElement, Collections.singleton((TBaseElement) outgoingFlow), incomingFlow);
                }

                for (final JAXBElement<? extends TArtifact> jaxbElement2 : new ArrayList<JAXBElement<? extends TArtifact>>(process.getArtifact())) {
                    TArtifact artifact = jaxbElement2.getValue();
                    substituteReferences((BpmnDefinitions) definitions, artifact, Collections.singleton((TBaseElement) outgoingFlow), incomingFlow);
                }
            }
        }

        // Remove the gateway and the outgoing flow
        Set<TBaseElement> pruningSet = new HashSet<TBaseElement>();
        pruningSet.add(gateway);
        pruningSet.add(outgoingFlow);
        prune(definitions, pruningSet);

        } catch (CanoniserException e) {
            e.printStackTrace();
        }
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
        JAXBElement<? extends TGateway> jeReconfiguredGateway = null;

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
                        assert gatewayType != null: "Null gateway type for " + gateway.getId();
                        switch (gatewayType) {
                        case DATA_BASED_EXCLUSIVE:
                            reconfiguredGateway = new TExclusiveGateway();
                            jeReconfiguredGateway = (new BpmnObjectFactory()).createExclusiveGateway((TExclusiveGateway) reconfiguredGateway);
                            break;
                        case EVENT_BASED_EXCLUSIVE:
                            reconfiguredGateway = new TEventBasedGateway();
                            jeReconfiguredGateway = (new BpmnObjectFactory()).createEventBasedGateway((TEventBasedGateway) reconfiguredGateway);
                            break;
                        case INCLUSIVE:
                            reconfiguredGateway = new TInclusiveGateway();
                            jeReconfiguredGateway = (new BpmnObjectFactory()).createInclusiveGateway((TInclusiveGateway) reconfiguredGateway);
                            break;
                        case PARALLEL:
                            reconfiguredGateway = new TParallelGateway();
                            jeReconfiguredGateway = (new BpmnObjectFactory()).createParallelGateway((TParallelGateway) reconfiguredGateway);
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
                        int replacementCounter = 0;  // count how many times we replace the gateway; ought be exactly once
                        for (int i = 0; i < process.getFlowElement().size(); i++) {
                            if (process.getFlowElement().get(i).getValue().equals(gateway)) {
                                process.getFlowElement().set(i, jeReconfiguredGateway);
                                replacementCounter++;
                                break;
                            }
                        }
                        assert replacementCounter == 1;
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
                        substituteReferences((BpmnDefinitions) definitions, flowElement, substitutionSet, reconfiguredGateway);
                    }
                }
            }

            // Update the diagram element's @bpmnElement reference (not needed since the reconfiguredGateway has the same ID as the original)
            //((BPMNShape) findBpmndiMap(definitions).get(gateway)).setBpmnElement(reconfiguredGateway);
            try {
                ((BpmnDefinitions) definitions).updateElement(reconfiguredGateway);
            } catch (CanoniserException e) {
                e.printStackTrace();
            }
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
