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

package org.apromore.processdiscoverer.logfilter.impl;

import org.apromore.processdiscoverer.logfilter.Action;
import org.apromore.processdiscoverer.logfilter.Containment;
import org.apromore.processdiscoverer.logfilter.Level;
import org.apromore.processdiscoverer.logfilter.LogFilterCriterionImpl;
import org.apromore.processdiscoverer.logfilter.LogFilterTypeSelector;
import org.apromore.processdiscoverer.logfilter.Type;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

import java.util.Set;

public class LogFilterCriterionAttribute extends LogFilterCriterionImpl {

    public LogFilterCriterionAttribute(Action action, Containment containment, Level level, String label, String attribute, Set<String> value) {
        super(action, containment, level, label, attribute, value);
    }

    @Override
    public boolean matchesCriterion(XTrace trace) {
        if(level == Level.TRACE) {
            for (XEvent event : trace) {
                if (containment == Containment.CONTAIN_ANY) {
                    if (isMatching(event)) return true;
                } else if (containment == Containment.CONTAIN_ALL) {
                    if (!isMatching(event)) return false;
                }
            }
            if(containment == Containment.CONTAIN_ANY) return false;
            else return containment == Containment.CONTAIN_ALL;
        }
        return false;
    }

    @Override
    public boolean matchesCriterion(XEvent event) {
        if(level == Level.EVENT) {
            return isMatching(event);
        }else return false;
    }

    private boolean isMatching(XEvent event) {
        XAttribute xAttribute = event.getAttributes().get(attribute);
        //if(attribute.equals(timestamp_code)) {
        if (LogFilterTypeSelector.getType(attribute) == Type.TIME_TIMESTAMP) {
            long start = 0;
            long end = Long.MAX_VALUE;
            for(String v : value) {
                if(v.startsWith(">")) start = Long.parseLong(v.substring(1));
                if(v.startsWith("<")) end = Long.parseLong(v.substring(1));
            }
            long t = ((XAttributeTimestamp) xAttribute).getValueMillis();
            return start <= t && t <= end;
        }else {
            if (xAttribute != null) {
                String xAttributeValue = xAttribute.toString();
                return value.contains(xAttributeValue);
            }
        }
        return false;
    }
}
