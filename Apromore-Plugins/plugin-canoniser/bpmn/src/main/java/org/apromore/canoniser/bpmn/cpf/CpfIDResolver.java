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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

// Third party packages
import com.sun.xml.bind.IDResolver;

/**
 * Custom handler for unmarshalling IDREFs from a {@link de.hpi.bpmn2_0.model.Definitions} document.
 *
 * This ensures that the {@link de.hpi.bpmn2_0.model.FlowElement#getIncoming} and
 * {@link de.hpi.bpmn2_0.model.FlowElement#getOutgoing} properties are populated during parsing.
 *
 * It also populates the {@link Activity#getAttachedBoundaryEvents} fields.
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
public class CpfIDResolver extends IDResolver {

    /** Mapping from XML IDs to JAXB objects. */
    private final Map<String, Object> idMap = new HashMap<String, Object>();

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
        idMap.put(id, ref);
    }

    /** {@inheritDoc} */
    public final Callable resolve(final String id, final Class c) {
        return new Callable() {
            public Object call() { return idMap.get(id); }
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
        for (Object ref : idMap.values()) {

            /*
            // FlowElements know about their incoming and outgoing edges
            if (ref instanceof EdgeType) {
                EdgeType edge = (EdgeType) ref;

                // Add this edge to its source's outgoings
                if (edge.getSourceRef() != null && !edge.getSourceRef().getOutgoing().contains(edge)) {
                    edge.getSourceRef().getOutgoing().add(edge);
                }

                // Add this edge to its target's incomings
                if (edge.getTargetRef() != null && !edge.getTargetRef().getIncoming().contains(edge)) {
                    edge.getTargetRef().getIncoming().add(edge);
                }
            }

            // Activities know about their attached boundary events
            if (ref instanceof BoundaryEvent) {
                Activity attachedTo = ((BoundaryEvent) ref).getAttachedToRef();

                attachedTo.getAttachedBoundaryEvents().addAll(attachedTo.getBoundaryEventRefs());
            }
            */
        }
    }
}
