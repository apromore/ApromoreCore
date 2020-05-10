/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012, 2014 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
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

package org.apromore.canoniser.bpmn.bpmn;

// Java 2 Standard packages
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Logger;
import javax.xml.namespace.QName;

// Third party packages
import com.sun.xml.bind.IDResolver;
import org.omg.spec.bpmn._20100524.model.TDataOutputAssociation;
import org.omg.spec.bpmn._20100524.model.TGateway;
import static org.omg.spec.bpmn._20100524.model.TGatewayDirection.CONVERGING;
import static org.omg.spec.bpmn._20100524.model.TGatewayDirection.DIVERGING;
import static org.omg.spec.bpmn._20100524.model.TGatewayDirection.MIXED;
import static org.omg.spec.bpmn._20100524.model.TGatewayDirection.UNSPECIFIED;
import org.omg.spec.bpmn._20100524.model.TSequenceFlow;

/**
 * Custom handler for unmarshalling IDREFs from a {@link BpmnDefinitions} document.
 *
 * This ensures that the {@link org.apromore.cpf.FlowNode#getIncoming} and
 * {@link org.apromore.cpf.FlowNode#getOutgoing} properties are populated during parsing.
 *
 * The proprietary {@link IDResolver} API in the Sun JAXB-RI library is employed as follows:
 * <pre>
 * import com.sun.xml.bind.IDResolver;
 *
 * Unmarshaller unmarshaller = context.createUnmarshaller();
 * unmarshaller.setProperty(IDResolver.class.getName(), new DefinitionsIDResolver());
 * </pre>
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @see <a href="http://weblogs.java.net/blog/kohsuke/archive/2005/08/pluggable_ididr.html">Pluggable
 * ID/IDREF handling in JAXB 2.0</a>
 */
public class BpmnIDResolver extends IDResolver {

    private final Logger logger = Logger.getAnonymousLogger();

    private Set<TGateway> gatewaySet = new HashSet<TGateway>();

    /** Mapping from XML IDs to JAXB objects. */
    private final Map<String, Object> idMap = new HashMap<String, Object>();

    private String targetNamespace;

    /** @param gateway  a gateway instance occuring in the parsed BPMN document */
    void addGateway(final TGateway gateway) {
        gatewaySet.add(gateway);
    }

    /** @param namespace  the target namespace for QName identifiers in the parsed BPMN doument */
    void setTargetNamespace(final String namespace) {
        targetNamespace = namespace;
    }

    // Methods that JAXB-RI magically knows about (pseudo-interface of IDResolver)

    /**
     * Initialize {@link #idMap}.
     *
     * JAXB-RI magically knows about this method signature and will invoke it dynamically.
     */
    public final void startDocument() {
        idMap.clear();
    }

    /** {@inheritDoc} */
    public final void bind(final String id, final Object ref) {
        assert !idMap.containsKey(id): "bind(" + id + ", " + ref + ") failed; id already mapped to " + idMap.get(id);
        idMap.put(id, ref);
    }

    /** {@inheritDoc} */
    public final Callable resolve(final String id, final Class c) {
        return new Callable() {
            public Object call() {
               return idMap.get(id);
            }
        };
    }

    /**
     * After the document has been parsed, and then had all its IDREFs populated, we perform a third
     * pass to populate fields derived from the IDREFs.
     *
     * These are the lists of incoming and outgoing edges on {@link de.hpi.bpmn2_0.model.FlowElement}s,
     * and the attached {@link BoundaryEvent} lists on {@link Activity} elements.
     *
     * JAXB-RI magically knows about this method signature and will invoke it dynamically.
     */
    public final void endDocument() {

        // FlowNodes know about their incoming and outgoing edges
        for (Object ref : idMap.values()) {
            if (ref instanceof TDataOutputAssociation) {
                TDataOutputAssociation dataOutputAssociation = (TDataOutputAssociation) ref;

                if (dataOutputAssociation.getSourceRef() == null) {
                    logger.info("DOA " + dataOutputAssociation.getId() + " lacks a sourceRef");
                }

                if (dataOutputAssociation.getTargetRef() == null) {
                    logger.info("DOA " + dataOutputAssociation.getId() + " lacks an targetRef");
                }
            }

            if (ref instanceof TSequenceFlow) {
                TSequenceFlow sequenceFlow = (TSequenceFlow) ref;

                // Make sure this flow is included within its source's outgoings
                if (sequenceFlow.getSourceRef() != null && !sequenceFlow.getSourceRef().getOutgoing().contains(sequenceFlow)) {
                    sequenceFlow.getSourceRef().getOutgoing().add(new QName(targetNamespace, sequenceFlow.getId()));
                }

                // Make sure this flow is included within its target's incomings
                if (sequenceFlow.getTargetRef() != null && !sequenceFlow.getTargetRef().getIncoming().contains(sequenceFlow)) {
                    sequenceFlow.getTargetRef().getIncoming().add(new QName(targetNamespace, sequenceFlow.getId()));
                }
            }
        }

        // Gateway direction can be derived by the numbers of incoming and outgoing sequence flows
        for (TGateway gateway : gatewaySet) {
            if (gateway.getIncoming().size() <= 1 && gateway.getOutgoing().size() > 1) {
                gateway.setGatewayDirection(DIVERGING);
            } else if (gateway.getIncoming().size() > 1 && gateway.getOutgoing().size() <= 1) {
                gateway.setGatewayDirection(CONVERGING);
            } else if (gateway.getIncoming().size() > 1 && gateway.getOutgoing().size() > 1) {
                gateway.setGatewayDirection(MIXED);
            } else {
                gateway.setGatewayDirection(UNSPECIFIED);
            }
        }
    }
}
