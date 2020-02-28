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
import java.util.Iterator;
import javax.xml.namespace.QName;

// Local packages
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.utils.ExtensionUtils;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.TypeAttribute;
import org.omg.spec.bpmn._20100524.model.TDataObject;
import org.omg.spec.bpmn._20100524.model.TDataState;
import org.omg.spec.bpmn._20100524.model.TDataStoreReference;

/**
 * CPF 1.0 object with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CpfObjectTypeImpl extends ObjectType implements CpfObjectType {

    // Extension attribute names

    /** {@link TypeAttribute#name} indicating BPMN DataObject is a collection. */
    public static final String DATA_STORE = "dataStore";

    /** {@link TypeAttribute#name} indicating BPMN DataObject is a collection. */
    public static final String IS_COLLECTION = "isCollection";

    /** {@link TypeAttribute#name} indicating BPMN DataObject is a collection. */
    public static final String ORIGINAL_NAME = "originalName";

    // Convenience properties

    /** The parent Net this instance belongs to. */
    private CpfNetType net;

    // Constructors

    /** No-arg constructor. */
    public CpfObjectTypeImpl() { }

    /**
     * Construct a CPF Object corresponding to a BPMN Data Object.
     *
     * @param dataObject  a BPMN Data Object
     * @param parent  the CPF Net this Object will belong to
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfObjectTypeImpl(final TDataObject dataObject,
                             final CpfNetType  parent,
                             final Initializer initializer) throws CanoniserException {

        //setConfigurable(false);  // Ignore configurability until we implement Configurable BPMN
        setIsCollection(dataObject.isIsCollection());

        setNet(parent);
        initializer.populateFlowElement(this, dataObject);
    }

    /**
     * Construct a CPF Object corresponding to a BPMN Data Store Reference.
     *
     * @param dataStoreReference a BPMN Data Store Reference
     * @param parent  the CPF Net this Object will belong to
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfObjectTypeImpl(final TDataStoreReference dataStoreReference,
                             final CpfNetType          parent,
                             final Initializer         initializer) throws CanoniserException {

        TDataState dataState = dataStoreReference.getDataState();
        QName dataStoreRef = dataStoreReference.getDataStoreRef();
        QName itemSubjectRef = dataStoreReference.getItemSubjectRef();

        //setConfigurable(false);  // Ignore until Configurable BPMN gets implemented
        setDataStore(dataStoreReference.getDataStoreRef());
        setIsCollection(false);

        setNet(parent);
        initializer.populateFlowElement(this, dataStoreReference);
    }

    // Accessors for CPF extension attributes

    /** @return current value of the <code>dataStore</code> property */
    public QName getDataStore() {
        String s = ExtensionUtils.getString(getAttribute(), DATA_STORE);
        return s == null ? null : QName.valueOf(s);
    }

    /** @param value  new value for the dataStore property */
    public void setDataStore(final QName value) {
        ExtensionUtils.setString(getAttribute(), DATA_STORE, value == null ? null : value.toString());
    }

    /** @return whether this task has any attribute named {@link #IS_COLLECTION}. */
    public boolean isIsCollection() {
        return ExtensionUtils.hasExtension(getAttribute(), IS_COLLECTION);
    }

    /** @param value  whether this CPF task corresponds to a BPMN collection data object */
    public void setIsCollection(final Boolean value) {
        ExtensionUtils.flagExtension(getAttribute(), IS_COLLECTION, value);
    }

    /** @return the CPF Net this instance belongs to */
    public CpfNetType getNet() {
        return net;
    }

    /** @param newNet  the CPF Net this instance belongs to */
    public void setNet(final CpfNetType newNet) {
        net = newNet;
    }

    /**
     * Usually this is the same as the CPF name, but it can sometimes differ if the canoniser had to rename this Object to avoid
     * having the same name as other Objects in the same Net.
     *
     * @return the name of the corresponding flow element from the original BPMN document
     */
    public String getOriginalName() {
        return ExtensionUtils.getString(getAttribute(), ORIGINAL_NAME);
    }

    /**
     * Record the name of the corresponding element from the BPMN, if the CPF name needed to be renamed to guarantee uniqueness within the CPF Net.
     *
     * @param value  the name of the corresponding flow element from the original BPMN document
     */
    public void setOriginalName(final String value) {
        ExtensionUtils.setString(getAttribute(), ORIGINAL_NAME, value);
    }
}
