/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2012, 2014 - 2017 Queensland University of Technology.
 * Copyright (C) 2018, 2020 The University of Melbourne.
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
 */

package org.apromore.canoniser.bpmn.bpmn;

// Java 2 Standard packages
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.xml.bind.Unmarshaller;

// Local classes
import org.omg.spec.bpmn._20100524.model.TBaseElement;
import org.omg.spec.bpmn._20100524.model.TDefinitions;
import org.omg.spec.bpmn._20100524.model.TFlowNode;
import org.omg.spec.bpmn._20100524.model.TGateway;

/**
 * As BPMN elements are unmarshalled, populate their convenience fields.
 *
 * The implemented convenience fields are:
 * <ul>
 * <li>{@link TGateway#getGatewayDirection}</li>
 * </ul>
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class BpmnUnmarshallerListener extends Unmarshaller.Listener {

    /** Logger.  Named after the class. */
    private final Logger logger = Logger.getLogger(this.getClass().getCanonicalName());

    private final BpmnIDResolver bpmnIdResolver;

    private final Map<String, TBaseElement> idMap = new HashMap<String, TBaseElement>();  // TODO - use diamond operator

    /**
     * Constructor.
     *
     * @param idResolver  linked IDREF handler
     */
    BpmnUnmarshallerListener(final BpmnIDResolver idResolver) {
        bpmnIdResolver = idResolver;
    }

    /**
     * @return a map from all the IDs of {@link TBaseElement}s parsed, to their object references
     */
    Map<String, TBaseElement> getIdMap() { return idMap; }

    // Methods implementing Unmarshaller.Listener

    /** {@inheritDoc} */
    @Override
    public void afterUnmarshal(final Object target, final Object parent) {
        if (target instanceof TBaseElement) {
            TBaseElement baseElement = (TBaseElement) target;

            idMap.put(baseElement.getId(), baseElement);

            if (target instanceof TFlowNode) {
                TFlowNode flowNode = (TFlowNode) target;

                // remove any existing incoming or outgoing lists, since they tend to be wrong anyway and the IDResolver will rebuild them for us
                flowNode.getIncoming().clear();
                flowNode.getOutgoing().clear();

                if (flowNode instanceof TGateway) {
                     // collate the set of gateways, since their directions need to be derived
                     bpmnIdResolver.addGateway((TGateway) target);
                }
            }
        } else if (target instanceof TDefinitions) {
            TDefinitions definitions = (TDefinitions) target;
            bpmnIdResolver.setTargetNamespace(definitions.getTargetNamespace());
        }
    }
}
