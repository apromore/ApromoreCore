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
package org.apromore.logfilter.criteria.impl;

import org.apromore.logfilter.criteria.model.Action;
import org.apromore.logfilter.criteria.model.Containment;
import org.apromore.logfilter.criteria.model.Level;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Set;

/**
 * @author Chii Chang (20/09/2019)
 */
public class LogFilterCriterionEndRange extends AbstractLogFilterCriterion {

    private long start = 0;
    private long end = Long.MAX_VALUE;
    private long traceStart = 0;

    public LogFilterCriterionEndRange(Action action, Containment containment, Level level, String label, String attribute, Set<String> value) {
        super(action, containment, level, label, attribute, value);
    }

    @Override
    protected boolean matchesCriterion(XTrace trace) {
        XEvent event0 = trace.get(0);
        traceStart = ((XAttributeTimestamp) event0.getAttributes().get("time:timestamp")).getValueMillis();
        for(String v : value) {
            if(v.startsWith(">")) start = Long.parseLong(v.substring(1));
            if(v.startsWith("<")) end = Long.parseLong(v.substring(1));
        }
        XEvent lastEvent = trace.get(trace.size()-1);
        long t = ((XAttributeTimestamp) lastEvent.getAttributes().get("time:timestamp")).getValueMillis();
        return start <= t && t <= end;
    }

    @Override
    protected boolean matchesCriterion(XEvent event) {
        return false;
    }

    @Override
    public String getAttribute() {
        return "time:endrange";
    }

    @Override
    public String toString() {
        long from = 0, to = 0;
        for (String s : value) {
            if (s.contains(">")) from = Long.valueOf(s.substring(1));
            if (s.contains("<")) to = Long.valueOf(s.substring(1));
        }
        String fromString = new Date(from).toString();
        String toString = new Date(to).toString();
        String display = super.getAction().toString().substring(0,1).toUpperCase() +
                super.getAction().toString().substring(1).toLowerCase() +
                " cases that contain end event in the timestamp range between " +
                fromString + " and " + toString;
        return display;
    }
}
