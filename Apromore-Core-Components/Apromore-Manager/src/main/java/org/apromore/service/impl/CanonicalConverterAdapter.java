/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package org.apromore.service.impl;

import org.apromore.cpf.CanonicalProcessType;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.converter.CanonicalToGraph;
import org.apromore.graph.canonical.converter.GraphToCanonical;
import org.apromore.service.CanonicalConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * Implementation of the CanonicalConverter. Allows the conversion of the different formats between each other.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class CanonicalConverterAdapter implements CanonicalConverter {

    @Inject
    private CanonicalToGraph canonicalToGraph;
    @Inject
    private GraphToCanonical graphToCanonical;


    /**
     * @see CanonicalConverter#convert(org.apromore.cpf.CanonicalProcessType)
     * {@inheritDoc}
     */
    @Override
    public Canonical convert(CanonicalProcessType cpt) {
        return canonicalToGraph.convert(cpt);
    }

    /**
     * @see CanonicalConverter#convert(org.apromore.graph.canonical.Canonical)
     * {@inheritDoc}
     */
    @Override
    public CanonicalProcessType convert(Canonical canonical) {
        return graphToCanonical.convert(canonical);
    }
}
