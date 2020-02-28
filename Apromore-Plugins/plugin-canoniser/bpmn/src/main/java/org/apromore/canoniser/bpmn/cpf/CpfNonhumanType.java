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
import java.util.List;

// Local packages
import org.apromore.cpf.NonhumanType;

/**
 * CPF 1.0 nonhuman resource type with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CpfNonhumanType extends NonhumanType implements CpfResourceTypeType {

    /** Secondary superclass. */
    private final CpfResourceTypeType super2;

    /** No-arg constructor. */
    public CpfNonhumanType() {
        super2 = new CpfResourceTypeTypeImpl();
    }

    /** {@inheritDoc} */
    public List<CpfResourceTypeType> getGeneralizationRefs() {
        return super2.getGeneralizationRefs();
    }
}
