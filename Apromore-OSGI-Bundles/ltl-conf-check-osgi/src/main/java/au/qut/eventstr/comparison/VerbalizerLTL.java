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

package au.qut.eventstr.comparison;

import au.ltl.utils.ModelAbstractions;
import au.qut.eventstr.comparison.differences.DifferencesML;
import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Transition;

import java.util.*;

public class VerbalizerLTL {
    private DifferencesML differences;

    // Model abstractions
    private ModelAbstractions model;
    private PetriNet net;
    private PetriNet unfolding;


    public VerbalizerLTL(ModelAbstractions model, HashSet<String> rules, HashSet<String> silents) throws Exception {
        this.model = model;
        this.net = model.getNet();
        this.unfolding = model.getUnfolding();

        for (Transition t : net.getTransitions())
            if (!model.getLabels().contains(t.getName()))
                silents.add(t.getName());

        this.differences = new DifferencesML();

    }

    public void verbalize() {
    }

    public DifferencesML getDifferences() {
        return differences;
    }
}
