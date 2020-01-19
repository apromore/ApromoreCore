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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Set;

/**
 * @author Chii Chang
 */
public class LogFilterCriterionDurationAverageWaiting  extends AbstractLogFilterCriterion {

    public LogFilterCriterionDurationAverageWaiting(Action action, Containment containment, Level level, String label, String attribute, Set<String> value) {
        super(action, containment, level, label, attribute, value);
    }

    @Override
    protected boolean matchesCriterion(XEvent event) {
        return false; // events have no duration
    }

    @Override
    public boolean matchesCriterion(XTrace trace) {

        long greaterThan = 0;
        long lesserThan = Long.MAX_VALUE;
        String oGString = "";
        String oLString = "";
        for(String v : value) {
            if(v.startsWith(">")) {
                int spaceIndex = v.indexOf(" ");
                String numberString = v.substring(1, spaceIndex);
                BigDecimal doubleValue = new BigDecimal(numberString);
                String unit = v.substring(spaceIndex + 1);
                BigDecimal unitValue = unitStringToBigDecimal(unit);
                BigDecimal gValue = doubleValue.multiply(unitValue);
                greaterThan = gValue.longValue();
            }
            if(v.startsWith("<")){

                int spaceIndex = v.indexOf(" ");
                String numberString = v.substring(1, spaceIndex);
                BigDecimal doubleValue = new BigDecimal(numberString);
                String unit = v.substring(spaceIndex + 1);
                BigDecimal unitValue = unitStringToBigDecimal(unit);
                BigDecimal lValue = doubleValue.multiply(unitValue);
                lesserThan = lValue.longValue();
            }
        }

        long avgWaitingTime = getAverageWaitingTime(trace);
        if(avgWaitingTime == 0) return false;
        else if(avgWaitingTime < greaterThan) return false;
        else if(avgWaitingTime > lesserThan) return false;
        else return true;
    }

    @Override
    public String toString() {
        String minString = "", maxString = "";
        for(String v : value) {
            if(v.startsWith(">")) minString = v.substring(v.indexOf(">") + 1);
            if(v.startsWith("<"))maxString = v.substring(v.indexOf("<") + 1);
        }
        return super.getAction().toString().substring(0,1).toUpperCase() +
                super.getAction().toString().substring(1).toLowerCase() +
                " all traces with a average waiting time between " +
                minString + " to " +
                maxString;
    }

    @Override
    public String getAttribute() {
        return "duration:average_waiting";
    }

    private BigDecimal unitStringToBigDecimal(String s) {
        if(s.equals("Years")) return new BigDecimal("31536000000");
        if(s.equals("Months")) return new BigDecimal("2678400000");
        if(s.equals("Weeks")) return new BigDecimal("604800000");
        if(s.equals("Days")) return new BigDecimal("86400000");
        if(s.equals("Hours")) return new BigDecimal("3600000");
        if(s.equals("Minutes")) return new BigDecimal("60000");
        if(s.equals("Seconds")) return new BigDecimal("1000");
        return new BigDecimal(0);
    }
    public static String convertMilliseconds(long milliseconds) {
        DecimalFormat decimalFormat = new DecimalFormat("##############0.##");
        double seconds = milliseconds / 1000.0D;
        double minutes = seconds / 60.0D;
        double hours = minutes / 60.0D;
        double days = hours / 24.0D;
        double weeks = days / 7.0D;
        double months = days / 31.0D;
        double years = days / 365.0D;

        if (years > 1.0D) {
            return decimalFormat.format(years) + " yrs";
        }

        if (months > 1.0D) {
            return decimalFormat.format(months) + " mths";
        }

        if (weeks > 1.0D) {
            return decimalFormat.format(weeks) + " wks";
        }

        if (days > 1.0D) {
            return decimalFormat.format(days) + " d";
        }

        if (hours > 1.0D) {
            return decimalFormat.format(hours) + " hrs";
        }

        if (minutes > 1.0D) {
            return decimalFormat.format(minutes) + " mins";
        }

        if (seconds > 1.0D) {
            return decimalFormat.format(seconds) + " secs";
        }

        if (milliseconds > 1.0D) {
            return decimalFormat.format(milliseconds) + " millis";
        }

        return "instant";
    }
    public long epochMilliOf(ZonedDateTime zonedDateTime){
        long s = zonedDateTime.toInstant().toEpochMilli();
        return s;
    }
    public static ZonedDateTime millisecondToZonedDateTime(long millisecond){
        Instant i = Instant.ofEpochMilli(millisecond);
        ZonedDateTime z = ZonedDateTime.ofInstant(i, ZoneId.systemDefault());
        return z;
    }
    public static ZonedDateTime zonedDateTimeOf(XEvent xEvent) {
        String timestampString = xEvent.getAttributes().get(XTimeExtension.KEY_TIMESTAMP).toString();
        Calendar calendar = javax.xml.bind.DatatypeConverter.parseDateTime(timestampString);
        ZonedDateTime z = millisecondToZonedDateTime(calendar.getTimeInMillis());
        return z;
    }

    private long getAverageWaitingTime(XTrace xTrace) {
        long waitingTimeSum = 0;
        long count = 0;
        for(int j = 1; j < xTrace.size(); j++) {
            XEvent xEvent = xTrace.get(j);
            long startTime = 0;
            long lastEndTime = 0;
            if(xEvent.getAttributes().containsKey("lifecycle:transition")) {
                String lifecycle = xEvent.getAttributes().get("lifecycle:transition").toString();
                if(lifecycle.toLowerCase().equals("start")) {
                    if(xEvent.getAttributes().containsKey("concept:name")) {
                        String jName = xEvent.getAttributes().get("concept:name").toString();
                        if(xEvent.getAttributes().containsKey("time:timestamp")) {
                            startTime = epochMilliOf(zonedDateTimeOf(xEvent));
                        }
                        for(int k=(j-1); k >= 0; k--) {
                            XEvent kEvent = xTrace.get(k);
                            if(kEvent.getAttributes().containsKey("lifecycle:transition")) {
                                String kLife = kEvent.getAttributes().get("lifecycle:transition").toString();
                                if(kLife.toLowerCase().equals("complete")) {
                                    if(kEvent.getAttributes().containsKey("time:timestamp")) {
                                        lastEndTime = epochMilliOf(zonedDateTimeOf(kEvent));
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if(startTime > lastEndTime) {
                waitingTimeSum += startTime - lastEndTime;
                count += 1;
            }
        }
        long avgWaitTime = 0;
        if(waitingTimeSum > 0) avgWaitTime = waitingTimeSum / count;

        return avgWaitTime;
    }
}

