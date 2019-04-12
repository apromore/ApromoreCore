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
import org.apromore.plugin.processdiscoverer.impl.util.StringValues;
import org.apromore.plugin.processdiscoverer.impl.util.TimeConverter;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Set;

public class LogFilterCriterionDuration extends LogFilterCriterionImpl {

    public LogFilterCriterionDuration(Action action, Containment containment, Level level, String label, String attribute, Set<String> value) {
        super(action, containment, level, label, attribute, value);
    }

    @Override
    public boolean matchesCriterion(XTrace trace) {
        if(level == Level.TRACE) {
            long s = Long.MAX_VALUE;
            long e = 0;
            for (XEvent event : trace) {
                s = Math.min(s, ((XAttributeTimestamp) event.getAttributes().get(timestamp_code)).getValueMillis());
                e = Math.max(e, ((XAttributeTimestamp) event.getAttributes().get(timestamp_code)).getValueMillis());
            }
            long d = e - s;

            for(String v : value) {

                String[] h = TimeConverter.parseDuration(Double.parseDouble(v.substring(1)));

                double seconds = 1000.0;
                double minutes = seconds * 60.0;
                double hours = minutes * 60.0;
                double days = hours * 24.0;
                double weeks = days * 7.0;
                double months = days * 30.0;
                double years = days * 365.0;

                double x = 0;
                if(h[1].equals("0")) x = Double.parseDouble(h[0]) * years;
                else if(h[1].equals("1")) x = Double.parseDouble(h[0]) * months;
                else if(h[1].equals("2")) x = Double.parseDouble(h[0]) * weeks;
                else if(h[1].equals("3")) x = Double.parseDouble(h[0]) * days;
                else if(h[1].equals("4")) x = Double.parseDouble(h[0]) * hours;
                else if(h[1].equals("5")) x = Double.parseDouble(h[0]) * minutes;
                else if(h[1].equals("6")) x = Double.parseDouble(h[0]) * seconds;

                if(v.startsWith(">")) {
                    return d >= x;
                }
                if(v.startsWith("<")) {
                    return d <= x;
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
