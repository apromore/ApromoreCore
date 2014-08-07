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
import static  org.apromore.cpf.InputOutputType.INPUT;
import org.omg.spec.bpmn._20100524.model.TActivity;
import org.omg.spec.bpmn._20100524.model.TDataInputAssociation;

/**
 * BPMN Data Input Association with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class BpmnDataInputAssociation extends TDataInputAssociation {

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
                setTargetRef(parent);

                // There's a bug in JAXB that makes it impossible to directly add elements to collections of IDREFs, like sourceRef
                // As a workaround, I put the id of the sourceRef into an attribute and fix it later using XSLT
                getOtherAttributes().put(new QName("workaround"), initializer.findElement(objectRef.getObjectId()).getId());
            }
        });
    }
}
