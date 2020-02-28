/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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

package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import javax.xml.namespace.QName;

// Local packages
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.HardType;
import org.omg.spec.bpmn._20100524.model.TDataObject;
import org.omg.spec.bpmn._20100524.model.TDataStoreReference;

/**
 * CPF 1.0 object with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CpfHardType extends HardType implements CpfObjectType {

    /** Second superclass. */
    private final CpfObjectType super2;

    // Constructors

    /** No-arg constructor. */
    public CpfHardType() {
        super2 = new CpfObjectTypeImpl();
    }

    /**
     * Construct a CPF Hard Object corresponding to a BPMN Data Object.
     *
     * @param dataObject  a BPMN Data Object
     * @param parent  the CPF Net this Object will belong to
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfHardType(final TDataObject dataObject,
                       final CpfNetType  parent,
                       final Initializer initializer) throws CanoniserException {

        super2 = new CpfObjectTypeImpl(dataObject, parent, initializer);
    }

    /**
     * Construct a CPF Hard Object corresponding to a BPMN Data Store Reference.
     *
     * @param dataStoreReference a BPMN Data Store Reference
     * @param parent  the CPF Net this Object will belong to
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfHardType(final TDataStoreReference dataStoreReference,
                       final CpfNetType          parent,
                       final Initializer         initializer) throws CanoniserException {

        super2 = new CpfObjectTypeImpl(dataStoreReference, parent, initializer);
    }

    // Accessors for CPF extension attributes

    /** {@inheritDoc} */
    public QName getDataStore() {
        return super2.getDataStore();
    }

    /** {@inheritDoc} */
    public void setDataStore(final QName value) {
        super2.setDataStore(value);
    }

    /** {@inheritDoc} */
    public boolean isIsCollection() {
        return super2.isIsCollection();
    }

    /** {@inheritDoc} */
    public void setIsCollection(final Boolean value) {
        super2.setIsCollection(value);
    }

    /** {@inheritDoc} */
    public CpfNetType getNet() {
        return super2.getNet();
    }

    /** {@inheritDoc} */
    public void setNet(final CpfNetType newNet) {
        super2.setNet(newNet);
    }

    /** {@inheritDoc} */
    public String getOriginalName() {
        return super2.getOriginalName();
    }

    /** {@inheritDoc} */
    public void setOriginalName(final String value) {
        super2.setOriginalName(value);
    }
}
