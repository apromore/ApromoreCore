/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012, 2014 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.SAXException;

// Local packages
import static org.apromore.canoniser.bpmn.BPMN20Canoniser.requiredName;
import org.apromore.canoniser.bpmn.Initialization;
import org.apromore.canoniser.bpmn.JAXBConstants;
import org.apromore.canoniser.bpmn.bpmn.BpmnDefinitions;
import org.apromore.canoniser.bpmn.bpmn.ProcessWrapper;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.BaseVisitor;
import org.apromore.cpf.CancellationRefType;
import org.apromore.cpf.DepthFirstTraverserImpl;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TraversingVisitor;
import org.apromore.cpf.WorkType;
import org.omg.spec.bpmn._20100524.model.TBaseElement;
import org.omg.spec.bpmn._20100524.model.TCollaboration;
import org.omg.spec.bpmn._20100524.model.TMessage;
import org.omg.spec.bpmn._20100524.model.TMessageFlow;
import org.omg.spec.bpmn._20100524.model.TParticipant;
import org.omg.spec.bpmn._20100524.model.TProcess;
import org.omg.spec.bpmn._20100524.model.TRootElement;

/**
 * CPF 1.0 document root with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CpfCanonicalProcessType extends CanonicalProcessType implements Attributed, JAXBConstants {

    /** Lookup contained CPF elements by indentifier. */
    private final Map<String, Object> elementMap = new HashMap<String, Object>();  // TODO - use diamond operator

    /** No-arg constructor. */
    public CpfCanonicalProcessType() { }

    /**
     * Construct a CPF document corresponding to a BPMN document.
     *
     * The resulting CPF requires its <code>uri</code> property to be set in order to be schema-legal.
     *
     * @param definitions  a BPMN document
     * @throws CanoniserException  if the CPF document can't be constructed
     */
    public CpfCanonicalProcessType(final BpmnDefinitions definitions) throws CanoniserException {

        final Initializer initializer = new Initializer(this, definitions, elementMap);

        // Populate attributes
        setName(requiredName(definitions.getName()));
        setVersion(CPFSchema.CPF_VERSION);

        // CPF Nodes aren't allowed more than one incoming edge or more than one outgoing edge
        definitions.rewriteImplicitGatewaysExplicitly();

        // Traverse the BPMN document, creating CPF elements corresponding to each BPMN element
        for (JAXBElement<? extends TRootElement> jere : definitions.getRootElement()) {
            TRootElement rootElement = jere.getValue();
            if (rootElement instanceof TCollaboration) {
                TCollaboration collaboration = (TCollaboration) rootElement;

                // Participants
                for (final TParticipant participant : collaboration.getParticipant()) {
                    if (participant.getProcessRef() == null) {  // black box pool
                        getResourceType().add(new CpfResourceTypeTypeImpl(participant, initializer));
                    } else {
                        TProcess process = (TProcess) initializer.findBpmnElement(participant.getProcessRef());
                        if (process == null) {
                            throw new CanoniserException("Participant " + participant.getId() + " missing process " + participant.getProcessRef());
                        }
                        initializer.warn("Canonisation ignores pool " + participant.getId() + " for process " + participant.getProcessRef());
                    }
                }

                // Message flows
                for (final TMessageFlow messageFlow : collaboration.getMessageFlow()) {
                    messageFlow.getName();
                    initializer.defer(new Initialization() {
                        public void initialize() throws CanoniserException {
                            TBaseElement source = initializer.findBpmnElement(messageFlow.getSourceRef());
                            if (source == null) {
                                throw new CanoniserException("Message flow " + messageFlow.getId() + " missing source " + messageFlow.getSourceRef());
                            }

                            TBaseElement target = initializer.findBpmnElement(messageFlow.getTargetRef());
                            if (target == null) {
                                throw new CanoniserException("Message flow " + messageFlow.getId() + " missing target " + messageFlow.getTargetRef());
                            }

                            if (messageFlow.getMessageRef() != null) {
                                TMessage message = (TMessage) initializer.findBpmnElement(messageFlow.getMessageRef());
                                if (message == null) {
                                    throw new CanoniserException("Message flow " + messageFlow.getId() + " missing message ref " +
                                                                 messageFlow.getMessageRef());
                                }
                            }
                        }
                    });
                }

            } else if (rootElement instanceof TProcess) {  // Each top-level BPMN Process becomes a CPF Net in the rootIDs list
                new CpfNetType(new ProcessWrapper((TProcess) rootElement), null, initializer, this);
            } else {
                initializer.warn("Canonisation ignores " + rootElement.getId() + " of type " + rootElement.getClass().getCanonicalName());
            }
        }

        // Execute deferred operations
        initializer.close();

        // Assemble the list of boundary events
        final Set<CpfTaskType> tasks = new HashSet<CpfTaskType>();
        accept(new TraversingVisitor(new DepthFirstTraverserImpl(), new BaseVisitor() {
            @Override public void visit(final TaskType task) {
                CpfTaskType cpfTask = (CpfTaskType) task;
                if (cpfTask.getBoundaryEvents().size() > 0) {
                    tasks.add(cpfTask);
                }
            }
        }));

        // Rewrite the boundary events
        for (CpfTaskType cpfTask : tasks) {
            rewriteTaskWithBoundaryEvents(cpfTask, initializer);
        }
    }

    /**
     * Construct an instance from a CPF-formatted stream.
     *
     * @param in  a CPF-formatted stream
     * @param validate  whether to perform schema validation while parsing
     * @return JAXB object model of the parsed stream
     * @throws JAXBException if the stream can't be unmarshalled as CPF
     * @throws SAXException if the CPF schema can't be parsed
     */
    public static CpfCanonicalProcessType newInstance(final InputStream in, final Boolean validate) throws JAXBException, SAXException {
        Unmarshaller unmarshaller = JAXBContext.newInstance(org.apromore.cpf.ObjectFactory.class, com.processconfiguration.ObjectFactory.class)
                                               .createUnmarshaller();
        CpfUnmarshallerListener listener = new CpfUnmarshallerListener();
        unmarshaller.setListener(listener);
        unmarshaller.setProperty(ID_RESOLVER, new CpfIDResolver());
        unmarshaller.setProperty(OBJECT_FACTORY, new ObjectFactory());
        if (validate) {
            unmarshaller.setSchema(CPFSchema.getCPFSchema());
        }
        CpfCanonicalProcessType result = ((JAXBElement<CpfCanonicalProcessType>) unmarshaller.unmarshal(new StreamSource(in))).getValue();
        result.elementMap.clear();
        result.elementMap.putAll(listener.getElementMap());
        return result;
    }

    /**
     * Convert a {@link CanonicalProcessType} to a {@link CpfCanonicalProcessType}.
     *
     * @param cpf  any CPF document
     * @return an instrumented version of the given <code>cpf</code>
     * @throws JAXBException if the remarshalling fails
     * @throws SAXException if the CPF schema can't be parsed
     */
    public static CpfCanonicalProcessType remarshal(final CanonicalProcessType cpf) throws JAXBException, SAXException {
        if (cpf instanceof CpfCanonicalProcessType) {
            return (CpfCanonicalProcessType) cpf;
        } else {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            CPFSchema.marshalCanonicalFormat(out, cpf, false);
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            return newInstance(in, false);
        }
    }

    /**
     * @param id  identifier of a CPF Node contained within this document
     * @return the identified CPF Node
     */
    public Object getElement(final String id) {
        return elementMap.get(id);
    }

    /**
     * Write this instance to a stream.
     *
     * @param out  the destination stream
     * @param validate  whether to perform schema validation during serialization
     * @throws JAXBException if serialization fails
     * @throws SAXException if the CPF schema can't be parsed
     */
    public void marshal(final OutputStream out, final Boolean validate) throws JAXBException, SAXException {
        Marshaller marshaller = JAXBContext.newInstance(CPFSchema.CPF_CONTEXT).createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        if (validate) {
            marshaller.setSchema(CPFSchema.getCPFSchema());
        }
        marshaller.marshal(new ObjectFactory().createCanonicalProcess(this), out);
    }

    // Internal methods

    /**
     * Insert an AND split routing in front of a task, connected to its boundary events.
     *
     * The task and any interrupting boundary events will cancel each other.
     *
     * @param cpfTask  a CPF task corresponding to a BPMN activity with boundary events
     * @param initializer  document construction state
     */
    private void rewriteTaskWithBoundaryEvents(final CpfTaskType task, final Initializer initializer) {
        assert task.getIncomingEdges().size() == 1 : task.getId() + " doesn't have a single incoming edge";

        CpfNetType parent = initializer.findParent(task);

        // What other elements cancel the task?  They must also cancel the elements we create for rewriting.
        Set<WorkType> cancelling = new HashSet<WorkType>();
        for (NodeType node : parent.getNode()) {
            if (node instanceof WorkType) {
                WorkType work = (WorkType) node;
                for (CancellationRefType cancellationRef : work.getCancelNodeId()) {
                    if (task.getId().equals(cancellationRef.getRefId())) {
                         cancelling.add(work);
                    }
                }
            }
        }

        for (CpfEdgeType incomingEdge : new ArrayList<CpfEdgeType>(task.getIncomingEdges())) {

            // Create AND split
            CpfANDSplitType andSplit = new CpfANDSplitType();
            andSplit.setId(initializer.newId(task.getId() + "_boundary_routing"));
            elementMap.put(andSplit.getId(), andSplit);
            parent.getNode().add(andSplit);
            cancel(andSplit, cancelling);

            // Reconnect the incoming edge to the AND split
            incomingEdge.setTargetId(andSplit.getId());
            ((CpfNodeType) elementMap.get(incomingEdge.getTargetId())).getIncomingEdges().remove(incomingEdge);
            andSplit.getIncomingEdges().add(incomingEdge);

            // Create a new edge from the AND split to the task
            CpfEdgeType edge = new CpfEdgeType();
            edge.setId(initializer.newId(task.getId() + "_boundary_edge"));
            elementMap.put(edge.getId(), edge);
            parent.getEdge().add(edge);
            cancel(edge, cancelling);

            edge.setSourceId(andSplit.getId());
            andSplit.getOutgoingEdges().add(edge);

            edge.setTargetId(task.getId());
            task.getIncomingEdges().add(edge);

            for (CpfEventType event : task.getBoundaryEvents()) {

                // Create a new edge from the AND split to the event
                edge = new CpfEdgeType();
                edge.setId(initializer.newId(event.getId() + "_boundary_edge"));
                elementMap.put(edge.getId(), edge);
                parent.getEdge().add(edge);
                cancel(edge, cancelling);

                edge.setSourceId(andSplit.getId());
                andSplit.getOutgoingEdges().add(edge);

                edge.setTargetId(event.getId());
                event.getIncomingEdges().add(edge);

                // The task cancels the boundary event
                CancellationRefType cancellationRef = new CancellationRefType();
                cancellationRef.setRefId(event.getId());
                task.getCancelNodeId().add(cancellationRef);

                if (event.isInterrupting()) {

                    // Cancel the task
                    cancellationRef = new CancellationRefType();
                    cancellationRef.setRefId(task.getId());
                    event.getCancelNodeId().add(cancellationRef);

                    // Cancel any other boundary events
                    for (CpfEventType otherEvent : task.getBoundaryEvents()) {
                        if (!event.equals(otherEvent)) {
                            cancellationRef = new CancellationRefType();
                            cancellationRef.setRefId(otherEvent.getId());
                            event.getCancelNodeId().add(cancellationRef);
                        }
                    }
                }
            }
        }
    }

    /**
     * Add a CPF Edge to the cancellation set of a group of CPF Work elements.
     *
     * @param cancelledEdge  a CPF Edge identifier
     * @param cancellingWorks  a set of CPF Work elements
     */
    private void cancel(final EdgeType cancelledEdge, Set<WorkType> cancellingWorks) {
        if (!cancellingWorks.isEmpty()) {
            CancellationRefType cancellationRef = new CancellationRefType();
            cancellationRef.setRefId(cancelledEdge.getId());

            for (WorkType work : cancellingWorks) {
                work.getCancelEdgeId().add(cancellationRef);
            }
        }
    }

    /**
     * Add a CPF Node to the cancellation set of a group of CPF Work elements.
     *
     * @param cancelledNode  a CPF Node identifier
     * @param cancellingWorks  a set of CPF Work elements
     */
    private void cancel(final NodeType cancelledNode, Set<WorkType> cancellingWorks) {
        if (!cancellingWorks.isEmpty()) {
            CancellationRefType cancellationRef = new CancellationRefType();
            cancellationRef.setRefId(cancelledNode.getId());

            for (WorkType work : cancellingWorks) {
                work.getCancelNodeId().add(cancellationRef);
            }
        }
    }
}
