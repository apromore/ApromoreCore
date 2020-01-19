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
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Set;

/**
 * @author Chii Chang
 */
public class LogFilterCriterionEventuallyFollow extends AbstractLogFilterCriterion {

    String attributeOption = "";
    UnifiedSet<String> followSet = new UnifiedSet<>();
    String requiredAttributeString = "";
    String intervalString = "";

    public LogFilterCriterionEventuallyFollow(Action action, Containment containment, Level level, String label, String attribute, Set<String> value) {
        super(action, containment, level, label, attribute, value);

        String greaterString = "";
        String greaterEqualString = "";
        String lessString = "";
        String lessEqualString = "";

        for (String s : value) {
            if(s.contains("=>")) {
                followSet.put(s);
            }else if(s.contains("@&")) {
                requiredAttributeString = "have the same \"" + s.substring(2) + "\"";
            }else if(s.contains("@!")) {
                requiredAttributeString = "have the different \"" + s.substring(2) + "\"";
            }else if(s.contains("@$")) {
                attributeOption = s.substring(2);
            }

            if(s.contains("@>|")) {
                greaterString = s.substring(3);
            }
            if(s.contains("@>=")) {
                greaterEqualString = s.substring(3);
            }

            if(s.contains("@<|")) {
                lessString = s.substring(3);
            }
            if(s.contains("@<=")) {
                lessEqualString = s.substring(3);
            }
        }

        if(!greaterString.equals("") || !greaterEqualString.equals("") ||
                !lessString.equals("") || !lessEqualString.equals("")) {
            intervalString += " time interval";
            if(!greaterString.equals("")) intervalString += " is greater than " + greaterString;
            if(!greaterEqualString.equals("")) intervalString += " is at least " + greaterEqualString;

            if(!greaterString.equals("") || !greaterEqualString.equals("")) {
                if(!lessString.equals("") || !lessEqualString.equals("")) {
                    intervalString += " and";
                }
            }

            if(!lessString.equals("")) intervalString += " is less than " + lessString;
            if(!lessEqualString.equals("")) intervalString += " is up to " + lessEqualString;
        }
    }

    @Override
    public boolean matchesCriterion(XTrace trace) {
        if(level == Level.TRACE) {
            String attributeOption = "concept:name";
            String sameAttribute = "";
            String differentAttribute = "";
            String lessTimeString = "";
            String lessEqualTimeString = "";
            String moreTimeString = "";
            String moreEqualTimeString = "";
            for (String s : value) {
                if(s.contains("@&")) sameAttribute = s.substring(2);
                if(s.contains("@!")) differentAttribute = s.substring(2);
                if(s.contains("@$")) attributeOption = s.substring(2);
                if(s.contains("@<|")) lessTimeString = s.substring(3);
                if(s.contains("@<=")) lessEqualTimeString = s.substring(3);
                if(s.contains("@>|")) moreTimeString = s.substring(3);
                if(s.contains("@>=")) moreEqualTimeString = s.substring(3);
            }

            XEvent sEvent = trace.get(0);
            XEvent eEvent = trace.get(trace.size()-1);
            String s, e;
            s = sEvent.getAttributes().get(attributeOption).toString();
            e = eEvent.getAttributes().get(attributeOption).toString();

            for(int i=0; i<(trace.size() -1); i++) {
                XEvent event1 = trace.get(i);
                String event1V = event1.getAttributes().get(attributeOption).toString();

                for(int j = (i+1); j<trace.size(); j++) {
                    XEvent event2 = trace.get(j);
                    String event2V = event2.getAttributes().get(attributeOption).toString();
                    if (value.contains(event1V + " => " + event2V)) {
                        if(!sameAttribute.equals("")) {
                            if (!haveSameAttributeValue(event1, event2, sameAttribute)) return false;
                        }
                        if(!differentAttribute.equals("")) {
                            if (haveSameAttributeValue(event1, event2, differentAttribute)) return false;
                        }
                        if(!lessTimeString.equals("")) {
                            if (!haveIntervalShoterThan(event1, event2, lessTimeString)) return false;
                        }
                        if(!lessEqualTimeString.equals("")) {
                            if (!haveIntervalShoterEqualThan(event1, event2, lessEqualTimeString)) return false;
                        }
                        if(!moreTimeString.equals("")) {
                            if (!haveIntervalLongerThan(event1, event2, moreTimeString)) return false;
                        }
                        if(!moreEqualTimeString.equals("")) {
                            return haveIntervalLongerEqualThan(event1, event2, moreEqualTimeString);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean matchesCriterion(XEvent event) {
        return false;
    }

    @Override
    public String toString() {
        if (attributeOption.equals("")) attributeOption = label;
        String displayString = super.getAction().toString().substring(0,1).toUpperCase() +
                super.getAction().toString().substring(1).toLowerCase() +
                " all traces where their events contain the Eventually-follows relation of the \"" +
                attributeOption + "\" equal to " + followSet.toString();
        if(!requiredAttributeString.equals("")) displayString += " and " + requiredAttributeString;
        if(!intervalString.equals("")) displayString += " and " + intervalString;
        return displayString;
    }

    private boolean haveSameAttributeValue(XEvent event1, XEvent event2, String attributeKey) {
        if(event1.getAttributes().get(attributeKey) == null || event2.getAttributes().get(attributeKey) == null) return false;
        String value1 = event1.getAttributes().get(attributeKey).toString();
        String value2 = event2.getAttributes().get(attributeKey).toString();
        return value1.equals(value2);
    }

    private boolean haveIntervalShoterThan(XEvent event1, XEvent event2, String intervalString) {
        long intervalValue = millisecondsOfString(intervalString);
        long e1Time = epochMilliOf(event1);
        long e2Time = epochMilliOf(event2);
        long e1e2Interval = e2Time - e1Time;
        return e1e2Interval < intervalValue;
    }

    private boolean haveIntervalShoterEqualThan(XEvent event1, XEvent event2, String intervalString) {
        long intervalValue = millisecondsOfString(intervalString);
        long e1Time = epochMilliOf(event1);
        long e2Time = epochMilliOf(event2);
        long e1e2Interval = e2Time - e1Time;
        return e1e2Interval <= intervalValue;
    }

    private boolean haveIntervalLongerThan(XEvent event1, XEvent event2, String intervalString) {
        long intervalValue = millisecondsOfString(intervalString);
        long e1Time = epochMilliOf(event1);
        long e2Time = epochMilliOf(event2);
        long e1e2Interval = e2Time - e1Time;
        return e1e2Interval > intervalValue;
    }

    private boolean haveIntervalLongerEqualThan(XEvent event1, XEvent event2, String intervalString) {
        long intervalValue = millisecondsOfString(intervalString);
        long e1Time = epochMilliOf(event1);
        long e2Time = epochMilliOf(event2);
        long e1e2Interval = e2Time - e1Time;
        return e1e2Interval >= intervalValue;
    }

    private long millisecondsOfString(String intervalString) {
        String unitString = intervalString.substring(intervalString.indexOf(" ") + 1);
        BigDecimal unitValue = new BigDecimal(unitStringToLong(unitString));

        String inputString = intervalString.substring(0, intervalString.indexOf(" "));
        BigDecimal inputValue = decimalTimeStringToBigDecimal(inputString, unitString);

        BigDecimal intervalValue = inputValue.multiply(unitValue);
        return intervalValue.longValue();
    }

    private BigDecimal decimalTimeStringToBigDecimal(String inputString, String unit) {
        Long year = new Long(1000 * 60 * 60 * 24 * 365);
        Long month = new Long(1000 * 60 * 60 * 24 * 31);
        Long week = new Long(1000 * 60 * 60 * 24 * 7);
        Long day = new Long(1000 * 60 * 60 * 24);
        Long hour = new Long(1000 * 60 * 60);
        Long minute = new Long(1000 * 60);
        Long second = new Long(1000);
        BigDecimal bdYear = new BigDecimal(year);
        BigDecimal bdMonth = new BigDecimal(month);
        BigDecimal bdWeek = new BigDecimal(week);
        BigDecimal bdDay = new BigDecimal(day);
        BigDecimal bdHour = new BigDecimal(hour);
        BigDecimal bdMinute = new BigDecimal(minute);
        BigDecimal bdSecond = new BigDecimal(second);
        BigDecimal bdInputValue = new BigDecimal(inputString);
        if(unit.equals("years")) return bdInputValue.multiply(bdYear);
        else if(unit.equals("months")) return bdInputValue.multiply(bdMonth);
        else if(unit.equals("weeks")) return bdInputValue.multiply(bdWeek);
        else if(unit.equals("days")) return bdInputValue.multiply(bdDay);
        else if(unit.equals("hours")) return bdInputValue.multiply(bdHour);
        else if(unit.equals("minutes")) return bdInputValue.multiply(bdMinute);
        else if(unit.equals("seconds")) return bdInputValue.multiply(bdSecond);
        else return bdInputValue;

    }

    private long unitStringToLong(String s) { //2019-10-18
        if(s.toLowerCase().equals("years")) return new Long("31536000000");
        if(s.toLowerCase().equals("months")) return new Long("2678400000");
        if(s.toLowerCase().equals("weeks")) return new Long("604800000");
        if(s.toLowerCase().equals("days")) return new Long("86400000");
        if(s.toLowerCase().equals("hours")) return new Long("3600000");
        if(s.toLowerCase().equals("minutes")) return new Long("60000");
        if(s.toLowerCase().equals("seconds")) return new Long("1000");
        return new Long(0);
    }

    private static long epochMilliOf(XEvent xEvent) {
        String timestampString = xEvent.getAttributes().get(XTimeExtension.KEY_TIMESTAMP).toString();
        Calendar calendar = javax.xml.bind.DatatypeConverter.parseDateTime(timestampString);
        return calendar.getTimeInMillis();
    }

}
