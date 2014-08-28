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
package org.apromore.canoniser.yawl.internal;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CanonicalProcessType;
import org.yawlfoundation.yawlschema.SpecificationSetFactsType;
import org.yawlfoundation.yawlschema.orgdata.OrgDataType;

/**
 * Interface for a converter from YAWL to CPF and ANF.
 * 
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 * 
 */
public interface YAWL2Canonical {

    /**
     * Convert the YAWL SpecificationSet to the Canonical Process Format (CPF).
     * 
     * @param specificationSet
     *            of YAWL
     * @throws CanoniserException
     *             in case the conversion was not successful.
     */
    void convertToCanonical(SpecificationSetFactsType specificationSet) throws CanoniserException;

    /**
     * Converts the YAWL SpecificationSet using the supplied organisational model to look up information about resources.
     * 
     * @param specificationSet
     *            of YAWL
     * @param orgDataType
     *            of YAWL
     * @throws CanoniserException
     *             in case the conversion was not successful.
     */
    void convertToCanonical(SpecificationSetFactsType specificationSet, OrgDataType orgDataType) throws CanoniserException;

    /**
     * Get the annotation format result of the conversion.
     * 
     * @return AnnotationsType
     */
    AnnotationsType getAnf();

    /**
     * Get the canonical process format result of the conversion.
     * 
     * @return CanonicalProcessType
     */
    CanonicalProcessType getCpf();

}
