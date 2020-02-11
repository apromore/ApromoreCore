/*
 * Copyright © 2019 The University of Melbourne.
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
package org.apromore.logfilter.criteria.impl;

import org.apromore.logfilter.criteria.model.Action;
import org.apromore.logfilter.criteria.model.Containment;
import org.apromore.logfilter.criteria.model.Level;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

import java.util.Set;

/**
 * @author Chii Chang (16/08/2019)
 * Modified: Chii Chang (20/01/2020)
 */
public class LogFilterCriterionCaseVariant extends AbstractLogFilterCriterion {
    public LogFilterCriterionCaseVariant(Action action, Containment containment, Level level, String label, String attribute, Set<String> value) {
        super(action, containment, level, label, attribute, value);
    }

    @Override
    public boolean matchesCriterion(XTrace trace) {

        if(level == Level.TRACE) {
            String variantId = trace.getAttributes().get("case:variant").toString();
            for(String v : value) {
                if(variantId.equals(v)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean matchesCriterion(XEvent event) {
        return false;
    }
}
