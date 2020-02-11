
package com.processconfiguration;

/*-
 * #%L
 * Signavio Core Components
 * %%
 * Copyright (C) 2006 - 2020 Philipp Berger, Martin Czuchra, Gero Decker,
 * Ole Eckermann, Lutz Gericke,
 * Alexander Hold, Alexander Koglin, Oliver Kopp, Stefan Krumnow,
 * Matthias Kunze, Philipp Maschke, Falko Menge, Christoph Neijenhuis,
 * Hagen Overdick, Zhen Peng, Nicolas Peters, Kerstin Pfitzner, Daniel Polak,
 * Steffen Ryll, Kai Schlichting, Jan-Felix Schwarz, Daniel Taschik,
 * Willi Tscheschner, Björn Wagner, Sven Wagner-Boysen, Matthias Weidlich
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * 
 * 
 * 
 * Ext JS (http://extjs.com/) is used under the terms of the Open Source LGPL 3.0
 * license.
 * The license and the source files can be found in our SVN repository at:
 * http://oryx-editor.googlecode.com/.
 * #L%
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.sun.xml.bind.IDResolver;

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
    public static void configure(final Definitions definitions) {

        // Identify configured gateways
        final Set<Gateway> reconfiguredGatewaySet = findConfiguredGateways(definitions);

        // Find and remove absent sequence flows
        prune(definitions, (Set) findAbsentSequenceFlows(reconfiguredGatewaySet));

        // Replace configured gateways with the configured type, and remove their <configurable> element
        for (Gateway gateway : reconfiguredGatewaySet) {
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
    public static Map<BaseElement, DiagramElement> findBpmndiMap(final Definitions definitions) {
        final Map<BaseElement, DiagramElement> bpmndiMap = new HashMap<BaseElement, DiagramElement>();

        for (BPMNDiagram bpmnDiagram : definitions.getDiagram()) {
            for (DiagramElement element : bpmnDiagram.getBPMNPlane().getDiagramElement()) {
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

        return bpmndiMap;
    }

    /**
     * Find all configurable gateways with a configuration specified.
     *
     * @param definitions  a Configurable BPMN document
     * @return the gateways which have a <code>configurable/configuration</code> extension element
     */
    public static Set<Gateway> findConfiguredGateways(final Definitions definitions) {

        // Output value
        final Set<Gateway> reconfiguredGatewaySet = new HashSet<Gateway>();

        for (BPMNDiagram bpmnDiagram : definitions.getDiagram()) {
            for (DiagramElement element : bpmnDiagram.getBPMNPlane().getDiagramElement()) {
                element.acceptVisitor(new AbstractVisitor() {
                    @Override public void visitBpmnShape(final BPMNShape that) {
                        super.visitBpmnShape(that);
                        that.getBpmnElement().acceptVisitor(this);
                    }

                    @Override public void visitGateway(final Gateway that) {
                        super.visitBaseElement(that);

                        if (gatewayConfigurationType(that) != null) {
                            reconfiguredGatewaySet.add(that);
                        }
                    }
                });
            }
        }

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
    public static Set<SequenceFlow> findAbsentSequenceFlows(final Set<Gateway> reconfiguredGatewaySet) {

        // Return value
        final Set<SequenceFlow> absentFlowSet = new HashSet<SequenceFlow>();

        for (Gateway gateway : reconfiguredGatewaySet) {

            assert gateway != null : "Null gateway";
            assert gateway.getExtensionElements() != null
                : gateway.getId() + " has no extension elements";
            assert gateway.getExtensionElements().getFirstExtensionElementOfType(Configurable.class) != null
                : gateway.getId() + " is not configurable";
            assert gateway.getExtensionElements().getFirstExtensionElementOfType(Configurable.class).getConfiguration() != null
                : gateway.getId() + " is not configured";
            Configuration configuration = gateway.getExtensionElements()
                                                 .getFirstExtensionElementOfType(Configurable.class)
                                                 .getConfiguration();

            // Handle @sourceRefs
            switch (gateway.getGatewayDirection()) {
                case CONVERGING:
                case MIXED:
                    absentFlowSet.addAll(gateway.get_incomingSequenceFlows());
                    absentFlowSet.removeAll(configuration.getSourceRefs());
                    break;
                default:
                    // no action
            }

            // Handle @targetRefs
            switch (gateway.getGatewayDirection()) {
                case DIVERGING:
                case MIXED:
                    absentFlowSet.addAll(gateway.get_outgoingSequenceFlows());
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
    static Set<FlowElement> findOrphans(final Definitions definitions) {

        Set<FlowElement> all = new HashSet<FlowElement>();
        Set<FlowElement> canStart = new HashSet<FlowElement>();
        Set<FlowElement> canEnd = new HashSet<FlowElement>();

        for (final BaseElement rootElement : definitions.getRootElement()) {
            if (rootElement instanceof Process) {
                for (final FlowElement element : ((Process) rootElement).getFlowElement()) {
                    all.add(element);
                    if (element instanceof StartEvent) {
                        mark(element, canStart, Direction.FORWARDS);
                    } else if (element instanceof EndEvent) {
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
    private static void mark(final FlowElement element, final Set<FlowElement> markedSet, final Direction direction) {

        // Don't try to traverse null references
        if (element == null) { return; }

        // If this element has already been done, we don't need to do it again
        if (markedSet.contains(element)) { return; }

        // Recursively mark the element and all elements upstream/downstream from it
        element.acceptVisitor(new AbstractVisitor() {
            @Override public void visitBaseElement(final BaseElement that) {
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

            @Override public void visitSequenceFlow(final SequenceFlow that) {
                super.visitSequenceFlow(that);

                if (direction == Direction.BACKWARDS) {
                    mark(that.getSourceRef(), markedSet, Direction.BACKWARDS);
                } else if (direction == Direction.FORWARDS) {
                    mark(that.getTargetRef(), markedSet, Direction.FORWARDS);
                }
            }

            @Override public void visitFlowElement(final FlowElement that) {
                super.visitFlowElement(that);

                for (Edge edge : that.getIncoming()) {
                    if (!(edge instanceof SequenceFlow)) {
                        mark(edge, markedSet, Direction.ASSOCIATED);
                    }
                }

                for (Edge edge : that.getOutgoing()) {
                    if (!(edge instanceof SequenceFlow)) {
                        mark(edge, markedSet, Direction.ASSOCIATED);
                    }
                }
            }

            @Override public void visitFlowNode(final FlowNode that) {
                super.visitFlowNode(that);

                if (direction == Direction.BACKWARDS) {
                    for (Edge edge : that.getIncomingSequenceFlows()) {
                        mark(edge, markedSet, Direction.BACKWARDS);
                    }
                }

                if (direction == Direction.FORWARDS) {
                    for (Edge edge : that.getOutgoingSequenceFlows()) {
                        mark(edge, markedSet, Direction.FORWARDS);
                    }
                }
            }
        });
    }

    /**
     * Remove process elements from a model.
     *
     * The corresponding diagram elements will also be removed.
     *
     * @param definitions  a BPMN model
     * @param pruningSet  process elements to be removed from the model
     */
    static void prune(final Definitions definitions, final Set<BaseElement> pruningSet) {

        // Remove process elements in the pruning set
        for (final BaseElement rootElement : definitions.getRootElement()) {
            if (rootElement instanceof Process) {
                final Process process = (Process) rootElement;

                // Remove any references this process contains to pruned elements
                for (final FlowElement flowElement : process.getFlowElement()) {
                    substituteReferences(flowElement, pruningSet, null);
                }

                // Remove the pruned elements from this process
                process.getFlowElement().removeAll(pruningSet);
            }
        }

        // Remove diagram elements corresponding to any of the pruned process elements
        for (final BPMNDiagram bpmnDiagram : definitions.getDiagram()) {
            for (DiagramElement element : new ArrayList<DiagramElement>(bpmnDiagram.getBPMNPlane().getDiagramElement())) {
                if (element instanceof BPMNEdge) {
                    BPMNEdge edge = (BPMNEdge) element;

                    if (pruningSet.contains(edge.getBpmnElement())) {
                        bpmnDiagram.getBPMNPlane().getDiagramElement().remove(edge);
                    }
                } else if (element instanceof BPMNShape) {
                    BPMNShape shape = (BPMNShape) element;

                    if (pruningSet.contains(shape.getBpmnElement())) {
                        bpmnDiagram.getBPMNPlane().getDiagramElement().remove(shape);
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
    private static void substituteReferences(final BaseElement      element,
                                             final Set<BaseElement> substitutionSet,
                                             final BaseElement      newReference) throws ClassCastException {

        assert !substitutionSet.contains(newReference);

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
    }

    /**
     * Find and remove all trivial gateways from a BPMN document.
     *
     * @param definitions  a BPMN XML model, which will be mutated
     */
    static void removeTrivialGateways(final Definitions definitions) {
        for (final BaseElement rootElement : definitions.getRootElement()) {
            if (rootElement instanceof Process) {
                final Process process = (Process) rootElement;

                for (final FlowElement flowElement : new ArrayList<FlowElement>(process.getFlowElement())) {
                    if (flowElement instanceof Gateway) {
                        Gateway gateway = (Gateway) flowElement;
                        if (gateway.getIncomingSequenceFlows().size() == 1
                         && gateway.getOutgoingSequenceFlows().size() == 1) {
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
    static void removeTrivialGateway(final Definitions definitions, final Gateway gateway) {
        final SequenceFlow incomingFlow = gateway.getIncomingSequenceFlows().get(0),
                           outgoingFlow = gateway.getOutgoingSequenceFlows().get(0);

        Map<BaseElement, DiagramElement> bpmndiMap = findBpmndiMap(definitions);

        // Append the outgoing flow's waypoints to the incoming flow
        assert bpmndiMap.containsKey(incomingFlow);
        assert bpmndiMap.containsKey(outgoingFlow);
        ((de.hpi.bpmn2_0.model.bpmndi.di.Edge) bpmndiMap.get(incomingFlow)).getWaypoint().addAll(
            ((de.hpi.bpmn2_0.model.bpmndi.di.Edge) bpmndiMap.get(outgoingFlow)).getWaypoint()
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
        for (final BaseElement rootElement : definitions.getRootElement()) {
            if (rootElement instanceof Process) {
                final Process process = (Process) rootElement;

                for (final FlowElement flowElement : new ArrayList<FlowElement>(process.getFlowElement())) {
                    substituteReferences(flowElement, Collections.singleton((BaseElement) outgoingFlow), incomingFlow);
                }
            }
        }

        // Remove the gateway and the outgoing flow
        Set<BaseElement> pruningSet = new HashSet<BaseElement>();
        pruningSet.add(gateway);
        pruningSet.add(outgoingFlow);
        prune(definitions, pruningSet);
    }

    /**
     * Extract the configured type of a configured gateway.
     *
     * @param gateway  a configured gateway
     * @return the <code>configurable/configuration/@type</code> extension attribute of
     *     the gateway, or <code>null</code> if the gateway has no such attribute
     */
    static TGatewayType gatewayConfigurationType(final Gateway gateway) {

        // Drill down to the configurable/configuration/@type attribute
        ExtensionElements extensionElements = gateway.getExtensionElements();
        if (extensionElements != null) {
            Configurable configurable = extensionElements.getFirstExtensionElementOfType(Configurable.class);
            if (configurable != null) {
                Configuration configuration = configurable.getConfiguration();
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
    static void replaceConfiguredGateway(final Definitions definitions, final Gateway gateway) {

        Gateway reconfiguredGateway = null;

        // From the processes, replace gateways
        for (final BaseElement rootElement : definitions.getRootElement()) {
            if (rootElement instanceof Process) {
                final Process process = (Process) rootElement;

                for (final FlowElement flowElement : new ArrayList<FlowElement>(process.getFlowElement())) {
                    if (flowElement.equals(gateway)) {

                        // Create the replacement gateway
                        TGatewayType gatewayType = gatewayConfigurationType(gateway);
                        switch (gatewayType) {
                        case DATA_BASED_EXCLUSIVE:
                            reconfiguredGateway = new ExclusiveGateway();
                            break;
                        case EVENT_BASED_EXCLUSIVE:
                            reconfiguredGateway = new EventBasedGateway();
                            break;
                        case INCLUSIVE:
                            reconfiguredGateway = new InclusiveGateway();
                            break;
                        case PARALLEL:
                            reconfiguredGateway = new ParallelGateway();
                            break;
                        default:
                            assert false : "Unknown gateway type: " + gatewayType;
                        }
                        assert reconfiguredGateway != null;

                        // Populate the base element properties of the replacement gateway
                        reconfiguredGateway.getAny().addAll(flowElement.getAny());
                        reconfiguredGateway.getDocumentation().addAll(flowElement.getDocumentation());
                        reconfiguredGateway.setId(flowElement.getId());


                        // Extension elements will be mutated (removing <configurable>), so need to clone the reference

                        // Clone the lists of extension elements
                        ExtensionElements extensionElements = new ExtensionElements();
                        extensionElements.getAny().addAll(flowElement.getExtensionElements().getAny());
                        extensionElements.getAnyExternal().addAll(flowElement.getExtensionElements().getAnyExternal());

                        // Configured gateway loses its "configurable" element
                        Configurable configurable = extensionElements.getFirstExtensionElementOfType(Configurable.class);
                        assert configurable != null : "No <configurable> element was found";
                        boolean removed = extensionElements.getAny().remove(configurable);
                        assert removed : "No <configurable> element was removed";

                        // If "configurable" was the only extension element, extensionElements goes away
                        if (!(extensionElements.getAny().isEmpty() && extensionElements.getAnyExternal().isEmpty())) {
                            reconfiguredGateway.setExtensionElements(extensionElements);
                        }


                        // Populate the flow element properties
                        reconfiguredGateway.setAuditing(flowElement.getAuditing());
                        reconfiguredGateway.getIncoming().addAll(flowElement.getIncoming());
                        reconfiguredGateway.setMonitoring(flowElement.getMonitoring());
                        reconfiguredGateway.setName(flowElement.getName());
                        reconfiguredGateway.getOutgoing().addAll(flowElement.getOutgoing());

                        // Populate the gateway properties
                        reconfiguredGateway.setGatewayDirection(((Gateway) flowElement).getGatewayDirection());

                        // Populate gateway-with-default-flow properties
                        if (flowElement instanceof GatewayWithDefaultFlow
                            && reconfiguredGateway instanceof GatewayWithDefaultFlow) {
                            ((GatewayWithDefaultFlow) reconfiguredGateway).setDefault(((GatewayWithDefaultFlow) flowElement).getDefault());
                        }

                        // Replace the original gateway with the the reconfigured one, in situ
                        process.getFlowElement().set(process.getFlowElement().indexOf(gateway), reconfiguredGateway);
                    }
                }
            }
        }

        if (reconfiguredGateway != null) {
            // Update all references to the old gateway to refer to the new gateway
            for (final BaseElement rootElement : definitions.getRootElement()) {
                if (rootElement instanceof Process) {
                    final Process process = (Process) rootElement;

                    // Substitute references to the new gateway for the old one in each process element
                    for (final FlowElement flowElement : process.getFlowElement()) {
                        Set<BaseElement> substitutionSet = new HashSet();
                        substitutionSet.add(gateway);
                        substituteReferences(flowElement, substitutionSet, reconfiguredGateway);
                    }
                }
            }

            // Update the diagram element's @bpmnElement reference
            ((BPMNShape) findBpmndiMap(definitions).get(gateway)).setBpmnElement(reconfiguredGateway);
        }
    }

    /**
     * Read a Configurable BPMN XML document from stdin, configure it, and write the configured BPMN XML to stdout.
     *
     * @param arg  command line arguments are ignored
     * @throws JAXBException if XML parsing fails
     */
    public static void main(final String[] arg) throws JAXBException {

        // Read a BPMN XML document from standard input
        JAXBContext context = JAXBContext.newInstance(Definitions.class,
                                                      ConfigurationAnnotationAssociation.class,
                                                      ConfigurationAnnotationShape.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setProperty(IDResolver.class.getName(), new DefinitionsIDResolver());
        Definitions definitions = (Definitions) unmarshaller.unmarshal(System.in);

        // Exercise the method
        ConfigurationAlgorithm.configure(definitions);

        // Write the configured BPMN XML document to standard output
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new BPMNPrefixMapper());
        marshaller.marshal(definitions, System.out);
    }
}
