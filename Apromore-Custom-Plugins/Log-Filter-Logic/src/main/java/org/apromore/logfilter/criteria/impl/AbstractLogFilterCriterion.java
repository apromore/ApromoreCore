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

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apromore.logfilter.criteria.LogFilterCriterion;
import org.apromore.logfilter.criteria.impl.util.TimeUtil;
import org.apromore.logfilter.criteria.model.Action;
import org.apromore.logfilter.criteria.model.Containment;
import org.apromore.logfilter.criteria.model.Level;
import org.apromore.logfilter.criteria.model.Type;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import java.util.Set;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 15/07/2018.
 * Modified by Chii Chang on 24/01/2020
 */
public abstract class AbstractLogFilterCriterion implements LogFilterCriterion {

    protected final String timestamp_code = "time:timestamp";

    protected final String label; // the current event attribute key used to label task nodes in the process map
    protected final String attribute; //attribute name of trace or event
    protected final Set<String> value; // set of attribute values
    private final Action action;
    protected final Containment containment;
    protected final Level level;
    private final int hashCode;

    protected AbstractLogFilterCriterion(Action action, Containment containment, Level level, String label, String attribute, Set<String> value) {
        this.label = label;
        this.action = action;
        this.containment = containment;
        this.level = level;
        this.attribute = attribute;
        this.value = value;
        this.hashCode = new HashCodeBuilder().append(level).append(containment).append(action).append(attribute).append(value).hashCode();
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public Containment getContainment() {
        return containment;
    }

    @Override
    public Action getAction() {
        return action;
    }

    @Override
    public String getAttribute() {
        return attribute;
    }

    @Override
    public Set<String> getValue() {
        return value;
    }
    
    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public boolean isToRemove(XTrace trace) {
        boolean matches = matchesCriterion(trace);
        if (matches && action == Action.REMOVE) return true;
        else return !matches && action == Action.RETAIN;
    }

    @Override
    public boolean isToRemove(XEvent event) {
        boolean matches = matchesCriterion(event);
        if (matches && action == Action.REMOVE) return true;
        else return !matches && action == Action.RETAIN;
    }

    protected abstract boolean matchesCriterion(XTrace trace);

    protected abstract boolean matchesCriterion(XEvent event);

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof AbstractLogFilterCriterion) {
            AbstractLogFilterCriterion logFilterCriterion = (AbstractLogFilterCriterion) o;
            return this.level == logFilterCriterion.level &&
                    this.containment == logFilterCriterion.containment &&
                    this.action == logFilterCriterion.action &&
                    this.attribute.equals(logFilterCriterion.attribute) &&
                    this.value.equals(logFilterCriterion.value);
        }
        return false;
    }

    @Override
    public String toString() {
        String string = "";
        if(action == Action.RETAIN) {
            string += "Retain ";
        }else {
            string += "Remove ";
        }

        String values = "[";
        int count = value.size() - 1;
        for(String v : value) {
            values += v;
            if(count > 0) {
                if(containment == Containment.CONTAIN_ANY) { //2019-10-24
                    values += " OR ";
                }else{
                    values += " AND ";
                }
                count--;
            }
        }
        values += "]";

        if (attribute.equals("time:timestamp")) {
            if (level == Level.EVENT) {
                string += "all events where timestamp is " + getTimestampValueStrings(value);
            } else {
                string += "all cases where timestamp is " + getTimestampValueStrings(value);
            }
        } else {
            if(level == Level.EVENT) {
                string += "all events where attribute " + attribute + " is equal to " + values;
            }else {
                string += "all cases ";

                if(this.label.equals("case:attribute")) {
                    string += "where attribute " + this.attribute + " is equal to " + values;
                } else {
                    if (attribute.equals("case:variant")) {
                        string += "where case variant is equal to " + values;
                    } else {
                        if (containment == Containment.CONTAIN_ANY) {
                            string += "containing an event where attribute " + attribute + " is equal to " + values;
                        } else {
                            string += "where all events have attribute " + attribute + " equal to " + values + " for all events";
                        }
                    }
                }
            }
        }


        return string;
    }

    private String getTimestampValueStrings(Set<String> values) {
        long from = 0, to = 0;
        for (String s : values) {
            if (s.contains(">")) from = Long.valueOf(s.substring(1));

            if (s.contains("<")) to = Long.valueOf(s.substring(1));
        }
        String fromString = TimeUtil.convertTimestamp(from);
        String toString = TimeUtil.convertTimestamp(to);

        return "from " + fromString + " to " + toString;
    }
}
