/*
 * Copyright © 2009-2016 The Apromore Initiative.
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
package org.apromore.canoniser.yawl.internal.impl;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.Canonical2YAWL;
import org.apromore.canoniser.yawl.internal.MessageManager;
import org.apromore.canoniser.yawl.internal.impl.context.CanonicalConversionContext;
import org.apromore.canoniser.yawl.internal.impl.factory.CanonicalConversionFactory;
import org.apromore.canoniser.yawl.internal.impl.factory.ConversionFactory;
import org.apromore.cpf.CanonicalProcessType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.SpecificationSetFactsType;
import org.yawlfoundation.yawlschema.orgdata.OrgDataType;

/**
 * Converting the Canonical Process Format to YAWL
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public final class Canonical2YAWLImpl implements Canonical2YAWL {

    private static final Logger LOGGER = LoggerFactory.getLogger(Canonical2YAWLImpl.class);

    private CanonicalConversionContext context;

    private final MessageManager messageInterface;

    public Canonical2YAWLImpl(final MessageManager messageManager) {
        this.messageInterface = messageManager;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.internal.Canonical2YAWL#convertToYAWL(org.apromore.cpf.CanonicalProcessType, org.apromore.anf.AnnotationsType)
     */
    @Override
    public void convertToYAWL(final CanonicalProcessType canonicalType, final AnnotationsType annotationsType) throws CanoniserException {

        // First create the Context class for this conversion, the Context is used to store all kind of information that needs to be shared between
        // Handlers
        this.context = new CanonicalConversionContext(canonicalType, annotationsType, messageInterface);

        // Second create the Factory class that will create the conversion Handlers
        final ConversionFactory factory = new CanonicalConversionFactory(this.getContext());
        this.getContext().setHandlerFactory(factory);

        if (canonicalType != null) {
            // Start conversion by using the Handler for the Canonical Process itself
            LOGGER.debug("Converting canonical process format with name: {}", canonicalType.getName());
            factory.createHandler(canonicalType, null, null).convert();
        } else {
            throw new IllegalArgumentException("CanonicalProcessType must be not-NULL!");
        }

        if (annotationsType != null) {
            // Start conversion by using the Handler for the Annotation itself
            factory.createHandler(annotationsType, null, null).convert();
        } else {
            LOGGER.warn("AnnotationsType should not be NULL if this method is called!");
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.internal.Canonical2YAWL#convertToYAWL(org.apromore.cpf.CanonicalProcessType)
     */
    @Override
    public void convertToYAWL(final CanonicalProcessType canonicalType) throws CanoniserException {
        if (canonicalType != null) {
            convertToYAWL(canonicalType, new org.apromore.anf.ObjectFactory().createAnnotationsType());
        } else {
            throw new IllegalArgumentException("CanonicalProcessType must be not-NULL!");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.internal.Canonical2YAWL#getYAWL()
     */
    @Override
    public SpecificationSetFactsType getYAWL() {
        return getContext().getYAWLSpecificationSet();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.internal.Canonical2YAWL#getOrgData()
     */
    @Override
    public OrgDataType getOrgData() {
        return getContext().getResourceContext().getYawlOrgData();
    }

    /**
     * @return the conversion context
     */
    public CanonicalConversionContext getContext() {
        return context;
    }

}
