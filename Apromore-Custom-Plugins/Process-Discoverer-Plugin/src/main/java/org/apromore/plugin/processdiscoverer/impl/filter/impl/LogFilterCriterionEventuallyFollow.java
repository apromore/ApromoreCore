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

package org.apromore.plugin.processdiscoverer.impl.filter.impl;

import org.apromore.plugin.processdiscoverer.impl.filter.Action;
import org.apromore.plugin.processdiscoverer.impl.filter.Containment;
import org.apromore.plugin.processdiscoverer.impl.filter.Level;
import org.apromore.plugin.processdiscoverer.impl.filter.LogFilterCriterionImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

import java.util.Set;

public class LogFilterCriterionEventuallyFollow extends LogFilterCriterionImpl {

    public LogFilterCriterionEventuallyFollow(Action action, Containment containment, Level level, String label, String attribute, Set<String> value) {
        super(action, containment, level, label, attribute, value);
    }

    @Override
    public boolean matchesCriterion(XTrace trace) {
        if(level == Level.TRACE) {
            for (int i = -1; i < trace.size(); i++) {
                String event1 = (i == -1) ? "|>" : trace.get(i).getAttributes().get(label).toString();
                for (int j = i + 1; j < trace.size() + 1; j++) {
                    String event2 = (j == trace.size()) ? "[]" : trace.get(j).getAttributes().get(label).toString();
                    if (value.contains(event1 + " => " + event2)) return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean matchesCriterion(XEvent event) {
        return false;
    }

}
