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

package org.apromore.service.apm.impl;

import hub.top.petrinet.PetriNet;
import nl.rug.ds.bpm.variability.SpecificationToXML;
import nl.rug.ds.bpm.variability.VariabilitySpecification;
import org.apromore.service.apm.APMService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class ApmServiceImpl implements APMService {
    String text = "";
    String xml = "";

    @Override
    public String[] getSpecification(PetriNet[] nets, String silentPrefix) {
        VariabilitySpecification vs = new VariabilitySpecification(nets, "silent");
        return SpecificationToXML.getOutput(vs, "silent");
    }
}