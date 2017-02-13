/*
 * Copyright © 2009-2017 The Apromore Initiative.
 *
 * This file is part of "Apromore".
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
package org.apromore.canoniser.yawl.internal.impl;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.MessageManager;
import org.apromore.canoniser.yawl.internal.YAWL2Canonical;
import org.apromore.canoniser.yawl.internal.impl.context.YAWLConversionContext;
import org.apromore.canoniser.yawl.internal.impl.factory.ConversionFactory;
import org.apromore.canoniser.yawl.internal.impl.factory.YAWLConversionFactory;
import org.apromore.cpf.CanonicalProcessType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.SpecificationSetFactsType;
import org.yawlfoundation.yawlschema.YAWLSpecificationFactsType;
import org.yawlfoundation.yawlschema.orgdata.OrgDataType;

/**
 * Converting YAWL to the Canonical Process Format
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public final class YAWL2CanonicalImpl implements YAWL2Canonical {

    private static final Logger LOGGER = LoggerFactory.getLogger(YAWL2CanonicalImpl.class);

    /**
     * The underlying organisational model
     */
    private OrgDataType orgDataType;

    /**
     * Conversion Context
     */
    private YAWLConversionContext context;

    /**
     *
     */
    private final MessageManager messageManager;

    public YAWL2CanonicalImpl(final MessageManager messageManager) {
        this.messageManager = messageManager;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.internal.YAWL2Canonical#convertToCanonical(org.yawlfoundation.yawlschema.SpecificationSetFactsType)
     */
    @Override
    public void convertToCanonical(final SpecificationSetFactsType value) throws CanoniserException {
        checkVersion(value.getVersion());
        checkValidSpecification(value);

        // YAWL allows only for one Specification per File
        final YAWLSpecificationFactsType specification = value.getSpecification().get(0);

        // If there is no Organisational Model supplied then create an empty one
        if (orgDataType == null) {
            LOGGER.warn("Canonising YAWL without use of an organisational model!");
            orgDataType = new OrgDataType();
        }

        // First create the Context class for this conversion, the Context is used to store all kind of information that needs to be shared between
        // Handlers
        this.setContext(new YAWLConversionContext(specification, value.getLayout(), orgDataType, messageManager));
        // Second create the Factory class that will create the conversion Handlers
        final ConversionFactory factory = new YAWLConversionFactory(this.getContext());

        // Start conversion on the YAWL speciication
        factory.createHandler(specification, null, null).convert();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.internal.YAWL2Canonical#convertToCanonical(org.yawlfoundation.yawlschema.SpecificationSetFactsType,
     * org.yawlfoundation.orgdataschema.OrgDataType)
     */
    @Override
    public void convertToCanonical(final SpecificationSetFactsType value, final OrgDataType orgDataType) throws CanoniserException {
        this.orgDataType = orgDataType;
        convertToCanonical(value);
    }

    private void checkValidSpecification(final SpecificationSetFactsType value) throws CanoniserException {
        if (value.getSpecification().size() == 1) {
            return;
        }
        throw new CanoniserException("Missing specification, a YAWL workflow should contain exact 1 specification!");
    }

    /**
     * We're trying to be relaxed though it is not guaranteed that the conversion will work with any other version than 2.2!
     *
     * @param version
     * @throws CanoniserException
     */
    private void checkVersion(final String version) throws CanoniserException {
        if (version.equalsIgnoreCase("2.2")) {
            return;
        }
        if (version.equalsIgnoreCase("2.1") || version.equalsIgnoreCase("2.0")) {
            LOGGER.warn("Converting an old YAWL specification with version {}. We'll try our very best, but the result could be indetermined!",
                    version);
            return;
        }
        throw new CanoniserException("Wrong YAWL version " + version + " this Canoniser can only convert YAWL 2.2");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.internal.YAWL2Canonical#getAnf()
     */
    @Override
    public AnnotationsType getAnf() {
        return getContext().getAnnotationResult();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.internal.YAWL2Canonical#getCpf()
     */
    @Override
    public CanonicalProcessType getCpf() {
        return getContext().getCanonicalResult();
    }

    public YAWLConversionContext getContext() {
        return context;
    }

    private void setContext(final YAWLConversionContext context) {
        this.context = context;
    }

}
