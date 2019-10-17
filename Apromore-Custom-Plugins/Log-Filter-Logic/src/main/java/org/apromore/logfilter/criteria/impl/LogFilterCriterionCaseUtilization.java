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
        return super.getAction() + " all traces with a case utilization between " +
                minDisplay + " to " +
                maxDisplay;
    }

    @Override
    public String getAttribute() {
        return "case:utilization";
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

    private double getCaseUtilization(XTrace xTrace) {
        long st = epochMilliOf(zonedDateTimeOf(xTrace.get(0)));
        long et = epochMilliOf(zonedDateTimeOf(xTrace.get(xTrace.size()-1)));
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
                long waiting = startTime - lastEndTime;
                totalWaitTime += waiting;
            }
        }

        double cuDouble = (1 - ((double) totalWaitTime / duration));

        return cuDouble;
    }
}

