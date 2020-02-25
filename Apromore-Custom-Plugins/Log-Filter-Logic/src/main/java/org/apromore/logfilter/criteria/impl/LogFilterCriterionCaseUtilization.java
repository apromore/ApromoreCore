/*
 * Copyright © 2019-2020 The University of Melbourne.
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

import org.apromore.logfilter.criteria.impl.util.TimeUtil;
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
 * @author Chii Chang (10/10/2019)
 * Modified: Chii Chang (24/01/2020)
 */
public class LogFilterCriterionCaseUtilization extends AbstractLogFilterCriterion {

    private DecimalFormat decimalFormat = new DecimalFormat("###############.##");

    public LogFilterCriterionCaseUtilization(Action action, Containment containment, Level level, String label, String attribute, Set<String> value) {
        super(action, containment, level, label, attribute, value);
    }

    @Override
    protected boolean matchesCriterion(XEvent event) {
        return false; // events have no duration
    }

    @Override
    public boolean matchesCriterion(XTrace trace) {

        double greaterThan = 0;
        double lesserThan = Double.MAX_VALUE;

        for(String v : value) {
            if(v.startsWith(">")) {
                String numberString = v.substring(1);
                greaterThan = new Double(numberString);
            }
            if(v.startsWith("<")) {
                String numberString = v.substring(1);
                lesserThan = new Double(numberString);
            }
        }

        double caseUtil = getCaseUtilization(trace);
        if(caseUtil == greaterThan || caseUtil == lesserThan) {
            return true;
        }
        if(caseUtil < greaterThan) {
            return false;
        }
        else if(caseUtil > lesserThan) {
            return false;
        }
        else return true;
    }

    @Override
    public String toString() {
        String minDisplay = "", maxDisplay = "";
        for(String v : value) {
            if(v.startsWith(">")){
                String minString = v.substring(v.indexOf(">") + 1);
                double minDouble = new Double(minString);
                minDisplay = decimalFormat.format(minDouble * 100) + "%";
            }
            if(v.startsWith("<")){
                String maxString = v.substring(v.indexOf("<") + 1);
                double maxDouble = new Double(maxString);
                maxDisplay = decimalFormat.format(maxDouble * 100) + "%";
            }
        }
        return super.getAction().toString().substring(0,1).toUpperCase() +
                super.getAction().toString().substring(1).toLowerCase() +
                " all cases with a case utilization between " +
                minDisplay + " to " +
                maxDisplay;
    }

    @Override
    public String getAttribute() {
        return "case:utilization";
    }





    private double getCaseUtilization(XTrace xTrace) {
        long st = TimeUtil.epochMilliOf(TimeUtil.zonedDateTimeOf(xTrace.get(0)));
        long et = TimeUtil.epochMilliOf(TimeUtil.zonedDateTimeOf(xTrace.get(xTrace.size()-1)));
        long duration = et - st;
        long totalWaitTime = 0;

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
                            startTime = TimeUtil.epochMilliOf(TimeUtil.zonedDateTimeOf(xEvent));
                        }
                        for(int k=(j-1); k >= 0; k--) {
                            XEvent kEvent = xTrace.get(k);
                            if(kEvent.getAttributes().containsKey("lifecycle:transition")) {
                                String kLife = kEvent.getAttributes().get("lifecycle:transition").toString();
                                if(kLife.toLowerCase().equals("complete")) {
                                    if(kEvent.getAttributes().containsKey("time:timestamp")) {
                                        lastEndTime = TimeUtil.epochMilliOf(TimeUtil.zonedDateTimeOf(kEvent));
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if(startTime > lastEndTime) {
                long waiting = startTime - lastEndTime;
                totalWaitTime += waiting;
            }
        }

        double cuDouble = (1 - ((double) totalWaitTime / duration));

        return cuDouble;
    }
}

