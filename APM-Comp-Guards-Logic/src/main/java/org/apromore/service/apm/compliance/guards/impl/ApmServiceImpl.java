/*
 * Copyright © 2009-2018 The Apromore Initiative.
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

package org.apromore.service.apm.compliance.guards.impl;

import hub.top.petrinet.PetriNet;
import nl.rug.ds.bpm.pnml.verifier.apm.PnmlVerifierAPM;
import org.apromore.service.apm.compliance.guards.APMService;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ApmServiceImpl implements APMService {
    @Override
    public String[] getVerification(PetriNet net, String xmlSpecification, Set<String> conditions, String txtGuards, Set<String> guards) {
        System.out.println("enter compliance logic");
        PnmlVerifierAPM pnmlVerifier = new PnmlVerifierAPM(net, xmlSpecification, conditions, txtGuards, guards, true);
        System.out.println("pnml verifier created");
        return pnmlVerifier.verify();
    }

    @Override
    public String[] getVerification(PetriNet net, String[] specifications, Set<String> conditions, Set<String> guards){
        System.out.println("enter compliance logic");
        PnmlVerifierAPM pnmlVerifier = new PnmlVerifierAPM(net, specifications, conditions, guards, true);
        System.out.println("pnml verifier created");
        return pnmlVerifier.verify();
    }
}