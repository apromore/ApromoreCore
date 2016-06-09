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

package org.apromore.service.compare;

import hub.top.petrinet.PetriNet;
import org.deckfour.xes.model.XLog;

import java.util.HashSet;
import java.util.Set;

/**
 * Created on 04/26/2016.
 */
public interface CompareService {

    Set<String> discoverBPMNModel(PetriNet net, XLog log) throws Exception;
    Set<String> discoverModelModel(PetriNet net1, PetriNet net2, HashSet<String> silent1, HashSet<String> silent2) throws Exception;
}
