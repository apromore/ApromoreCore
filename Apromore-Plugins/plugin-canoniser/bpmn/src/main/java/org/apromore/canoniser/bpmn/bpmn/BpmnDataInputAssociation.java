/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.canoniser.bpmn.bpmn;

// Java 2 Standard packages
import java.util.logging.Logger;
import javax.xml.namespace.QName;

// Local packages
import org.apromore.canoniser.bpmn.Initialization;
import org.apromore.canoniser.bpmn.cpf.CpfObjectRefType;
import org.apromore.canoniser.exception.CanoniserException;
import static org.apromore.cpf.InputOutputType.INPUT;
import org.apromore.cpf.TypeAttribute;
import org.omg.spec.bpmn._20100524.model.TActivity;
import org.omg.spec.bpmn._20100524.model.TDataInput;
import org.omg.spec.bpmn._20100524.model.TDataInputAssociation;

/**
 * BPMN Data Input Association with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class BpmnDataInputAssociation extends TDataInputAssociation {

    private static final Logger LOGGER = Logger.getLogger(BpmnDataInputAssociation.class.getCanonicalName());

    /** No-arg constructor. */
    public BpmnDataInputAssociation() { }

    /**
     * Construct a BPMN Data Input Association corresponding to a CPF ObjectRef.
     *
     * @param objectRef  a CPF Object Reference of type {@link #INPUT}
     * @param parent  the BPMN activity this instance belongs to
     * @param initializer  BPMN document construction state
     * @throws CanoniserException  if the association can't be constructed
     */
    public BpmnDataInputAssociation(final CpfObjectRefType objectRef,
                                    final TActivity        parent,
                                    final Initializer      initializer) throws CanoniserException {
        assert INPUT.equals(objectRef.getType()) : objectRef.getId() + " is not typed as an input";
        initializer.populateBaseElement(this, objectRef);

        initializer.defer(new Initialization() {
            public void initialize() {

                if (objectRef.getAttribute() != null) {
                    for (TypeAttribute attribute: objectRef.getAttribute()) {
                        switch (attribute.getName()) {
                        case "bpmn:dataInputAssociation.targetRef":
                            String id = attribute.getValue();
                            if (parent.getIoSpecification() != null && parent.getIoSpecification().getDataInput() != null) {
                                for (TDataInput dataInput: parent.getIoSpecification().getDataInput()) {
                                    if (id.equals(dataInput.getId())) {
                                        setTargetRef(dataInput);
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                }

                if (getTargetRef() == null) {
                    LOGGER.warning("Incorrectly linking bpmn:dataInputAssociation targetRef to parent activity " + parent.getId() + " instead of an (item aware) dataInput");
                    // TODO: synthesize a dummy ioSpecification/dataInput instead
                    setTargetRef(parent);
                }

                // There's a bug in JAXB that makes it impossible to directly add elements to collections of IDREFs, like sourceRef
                // As a workaround, I put the id of the sourceRef into an attribute and fix it later using XSLT
                getOtherAttributes().put(new QName("workaround"), initializer.findElement(objectRef.getObjectId()).getId());
            }
        });
    }
}
