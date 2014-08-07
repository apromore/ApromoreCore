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

package org.apromore.canoniser.bpmn.bpmn;

// Java 2 Standard packages
import javax.xml.namespace.QName;

// Local packages
import org.apromore.canoniser.bpmn.Initialization;
import org.apromore.canoniser.bpmn.cpf.CpfObjectRefType;
import org.apromore.canoniser.exception.CanoniserException;
import static  org.apromore.cpf.InputOutputType.OUTPUT;
import org.omg.spec.bpmn._20100524.model.TActivity;
import org.omg.spec.bpmn._20100524.model.TDataOutputAssociation;

/**
 * BPMN Data Output Association with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class BpmnDataOutputAssociation extends TDataOutputAssociation {

    /** No-arg constructor. */
    public BpmnDataOutputAssociation() { }

    /**
     * Construct a BPMN Data Output Association corresponding to a CPF ObjectRef.
     *
     * @param objectRef  a CPF Object Reference of type {@link #OUTPUT}
     * @param parent  the BPMN activity containing this instance
     * @param initializer  BPMN document construction state
     * @throws CanoniserException  if the association can't be constructed
     */
    public BpmnDataOutputAssociation(final CpfObjectRefType objectRef,
                                     final TActivity        parent,
                                     final Initializer      initializer) throws CanoniserException {
        assert OUTPUT.equals(objectRef.getType()) : objectRef.getId() + " is not typed as an output";
        initializer.populateBaseElement(this, objectRef);

        // There's a bug in JAXB that makes it impossible to directly add elements to collections of IDREFs, like sourceRef
        // As a workaround, I put the id of the sourceRef into an attribute and fix it later using XSLT
        getOtherAttributes().put(new QName("workaround"), parent.getId());

        initializer.defer(new Initialization() {
            public void initialize() {
                assert initializer.findElement(objectRef.getObjectId()) != null;
                setTargetRef(initializer.findElement(objectRef.getObjectId()));
            }
        });
    }
}
