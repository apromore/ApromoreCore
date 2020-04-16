/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2012, 2014 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
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
 * #L%
 */

package org.apromore.canoniser.bpmn.cpf;

// Local packages
import org.apromore.canoniser.bpmn.Initialization;
import org.apromore.canoniser.exception.CanoniserException;
import static org.apromore.cpf.InputOutputType.INPUT;
import static org.apromore.cpf.InputOutputType.OUTPUT;
import org.apromore.cpf.ObjectRefType;
import org.omg.spec.bpmn._20100524.model.TActivity;
import org.omg.spec.bpmn._20100524.model.TBaseElement;
import org.omg.spec.bpmn._20100524.model.TDataInputAssociation;
import org.omg.spec.bpmn._20100524.model.TDataObject;
import org.omg.spec.bpmn._20100524.model.TDataObjectReference;
import org.omg.spec.bpmn._20100524.model.TDataOutputAssociation;
import org.omg.spec.bpmn._20100524.model.TFlowElement;

/**
 * CPF 1.0 object reference with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CpfObjectRefType extends ObjectRefType implements Attributed {

    // Constructors

    /** No-arg constructor. */
    public CpfObjectRefType() { }

    /**
     * Construct a CPF ObjectRef corresponding to a BPMN DataInputAssociation, DataOutputAssociation, or DataObjectReference.
     *
     * The three cases are distinguished by the <var>type</var> property:
     * <dl>
     * <dt>{@link INPUT}</dt>     <dd>{@link DataInputAssociation}</dd>
     * <dt>{@link OUTPUT}</dt>    <dd>{@link DataOutputAssociation}</dd>
     * <dt><code>null</code></dt> <dd>{@link DataObjectReference}</dd>
     * </dl>
     *
     * @param association  a BPMN DataInputAssociation
     * @param parent  the BPMN Activity containing the <code>association</code>
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfObjectRefType(final TDataInputAssociation association,
                            final TActivity             parent,
                            final Initializer           initializer) throws CanoniserException {

        initializer.populateBaseElement(this, association);

        setType(INPUT);

        initializer.defer(new Initialization() {
            @Override
            public void initialize() throws CanoniserException {

                // A single source is the only thing that makes sense, surely?
                if (association.getSourceRef().size() != 1) {
                    throw new CanoniserException("BPMN data input association " + association.getId() + " has " +
                                                 association.getSourceRef().size() + " sources");
                }

                // Handle targetRef
                TBaseElement target = association.getTargetRef();
                if (target != null) {
                    org.apromore.cpf.TypeAttribute attribute = new org.apromore.cpf.TypeAttribute();
                    attribute.setName("bpmn:dataInputAssociation.targetRef");
                    attribute.setValue(target.getId());
                    getAttribute().add(attribute);
                }

                // Handle objectId
                Object object = initializer.findElement((TFlowElement) association.getSourceRef().get(0).getValue());
                if (object == null) {
                    throw new CanoniserException("DataInputAssociation " + association.getId() + " didn't have an identifiable source object");
                } else if (object instanceof CpfObjectRefType) {
                    CpfObjectRefType cpfObjectRef = (CpfObjectRefType) object;
                    CpfObjectRefType.this.setObjectId(cpfObjectRef.getObjectId());
                } else if (object instanceof CpfObjectType) {
                    CpfObjectType cpfObject = (CpfObjectType) object;
                    CpfObjectRefType.this.setObjectId(cpfObject.getId());
                } else {
                    throw new CanoniserException("DataInputAssociation " + association.getId() + " has unsupported source type " + object.getClass());
                }
            }
        });
    }

    /**
     * Construct a CPF ObjectRef corresponding to a BPMN DataOutputAssociation.
     *
     * @param association  a BPMN DataOutputAssociation
     * @param parent  the BPMN Activity containing the <code>association</code>
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfObjectRefType(final TDataOutputAssociation association,
                            final TActivity              parent,
                            final Initializer            initializer) throws CanoniserException {

        initializer.populateBaseElement(this, association);

        setType(OUTPUT);

        initializer.defer(new Initialization() {
            @Override
            public void initialize() throws CanoniserException {

                switch (association.getSourceRef().size()) {
                case 0:
                    // This is weird, but we'll let the incomplete model slide
                    break;

                case 1:
                    TBaseElement source = (TBaseElement) association.getSourceRef().get(0).getValue();

                    org.apromore.cpf.TypeAttribute attribute = new org.apromore.cpf.TypeAttribute();
                    attribute.setName("bpmn:dataOutputAssociation.sourceRef");
                    attribute.setValue(source.getId());
                    getAttribute().add(attribute);
                    break;

                default:
                    throw new CanoniserException("BPMN data output association " + association.getId() + " has " +
                                                 association.getSourceRef().size() + " sources");
                }

                // Handle targetRef
                TBaseElement target = association.getTargetRef();
                if (target != null) {
                    org.apromore.cpf.TypeAttribute attribute = new org.apromore.cpf.TypeAttribute();
                    attribute.setName("bpmn:dataOutputAssociation.targetRef");
                    attribute.setValue(target.getId());
                    getAttribute().add(attribute);
                }

                // Handle objectId
                Object object = initializer.findElement(association.getTargetRef());
                if (object == null) {
                    throw new CanoniserException("DataOutputAssociation " + association.getId() + " didn't have an identifiable target object");
                } else if (object instanceof CpfObjectRefType) {
                    CpfObjectRefType cpfObjectRef = (CpfObjectRefType) object;
                    CpfObjectRefType.this.setObjectId(cpfObjectRef.getObjectId());
                } else if (object instanceof CpfObjectType) {
                    CpfObjectType cpfObject = (CpfObjectType) object;
                    CpfObjectRefType.this.setObjectId(cpfObject.getId());
                } else {
                    throw new CanoniserException("DataOutputAssociation " + association.getId() + " has unsupported target type " + object.getClass());
                }
            }
        });
    }

    /**
     * Construct a CPF ObjectRef corresponding to a BPMN DataObjectReference.
     *
     * @param dataObjectReference  a BPMN DataObjectReference
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfObjectRefType(final TDataObjectReference dataObjectReference,
                            final Initializer          initializer) throws CanoniserException {

        initializer.populateBaseElement(this, dataObjectReference);

        setType(null);

        initializer.defer(new Initialization() {
            @Override
            public void initialize() {

                // Handle objectId
                CpfObjectType object = (CpfObjectType) initializer.findElement((TDataObject) dataObjectReference.getDataObjectRef());
                if (object != null) {
                    CpfObjectRefType.this.setObjectId(object.getId());
                }
            }
        });
    }
}

