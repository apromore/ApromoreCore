/*
 * Copyright © 2009-2015 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package org.apromore.canoniser.yawl.internal.impl.handler;

import org.apromore.canoniser.yawl.internal.impl.context.ConversionContext;

/**
 * Abstract base class for all conversion Handlers. A Handler usually is responsible for converting one Element of type T.
 * 
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 * 
 * @param <T>
 *            type of Element to be converted
 * @param <E>
 *            type of the already converted Parent
 */
public abstract class ConversionHandlerImpl<T, E> implements ConversionHandler<T, E> {

    protected static final org.apromore.cpf.ObjectFactory CPF_FACTORY = new org.apromore.cpf.ObjectFactory();

    protected static final org.apromore.anf.ObjectFactory ANF_FACTORY = new org.apromore.anf.ObjectFactory();

    protected static final org.yawlfoundation.yawlschema.ObjectFactory YAWL_FACTORY = new org.yawlfoundation.yawlschema.ObjectFactory();

    protected static final org.yawlfoundation.yawlschema.orgdata.ObjectFactory YAWL_ORG_FACTORY = new org.yawlfoundation.yawlschema.orgdata.ObjectFactory();

    protected ConversionContext context;
    private T obj;
    private E parent;
    private Object originalParent;

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandler#setObject(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setObject(final Object obj) {
        // Doing the unchecked cast here is safe as long as the correct ConversionHandler is created for each XML type
        this.obj = (T) obj;
    }

    /**
     * @return the object that has to be converted
     */
    protected T getObject() {
        return obj;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandler#setParent(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setConvertedParent(final Object parent) {
        this.parent = (E) parent;
    }

    /**
     * @return the already converted parent
     */
    protected E getConvertedParent() {
        return parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandler#setOriginalParent(java.lang.Object)
     */
    @Override
    public void setOriginalParent(final Object parent) {
        this.originalParent = parent;
    }

    /**
     * @return the original parent object
     */
    protected Object getOriginalParent() {
        return originalParent;
    }

    /**
     * Set the ConversionContext for this Handler
     * 
     * @param context
     */
    @Override
    public void setContext(final ConversionContext context) {
        this.context = context;
    }

    /**
     * The returned UUID will be the same, if the same prefix and id combination is used.
     * 
     * @param prefix
     *            used if IDs are not unique across different types of elements
     * @param id
     *            of the element
     * @return the UUID associated with this element
     */
    protected String generateUUID(final String prefix, final String id) {
        if (prefix != null) {
            return this.context.getUuidGenerator().getUUID(prefix + "_" + id);
        } else {
            return this.context.getUuidGenerator().getUUID(id);
        }
    }

    /**
     * The returned UUID will be the same, if the same id is used.
     * 
     * @param id
     *            of the element
     * @return the UUID associated with this element
     */
    protected String generateUUID(final String id) {
        // Empty prefix is safe, as ID will always start with "id_"!
        return generateUUID(null, id);
    }

    /**
     * The returned UUID will be the a new one for each call.
     * 
     * @return the newly generated UUID
     */
    protected String generateUUID() {
        return this.context.getUuidGenerator().getUUID(null);
    }

}