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
package org.apromore.canoniser.yawl.internal;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CanonicalProcessType;
import org.yawlfoundation.yawlschema.SpecificationSetFactsType;
import org.yawlfoundation.yawlschema.orgdata.OrgDataType;

/**
 * Interface for a converter from CPF and ANF to YAWL.
 * 
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 * 
 */
public interface Canonical2YAWL {

    /**
     * Converts the CPF and ANF to YAWL.
     * 
     * @param canonicalType
     *            CPF
     * @param annotationsType
     *            ANF
     * @throws CanoniserException
     */
    void convertToYAWL(CanonicalProcessType canonicalType, AnnotationsType annotationsType) throws CanoniserException;

    /**
     * Converts the CPF to YAWL.
     * 
     * @param canonicalType
     *            CPF
     * @throws CanoniserException
     */
    void convertToYAWL(CanonicalProcessType canonicalType) throws CanoniserException;

    /**
     * Get the YAWL SpecificationSet result of the conversion.
     * 
     * @return SpecificationSetFactsType
     */
    SpecificationSetFactsType getYAWL();

    /**
     * Get the YAWL organisational data result of the conversion.
     * 
     * @return OrgDataType
     */
    OrgDataType getOrgData();

}
