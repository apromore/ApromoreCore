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

public class LogFilterCriterionDurationMaxWaiting  extends AbstractLogFilterCriterion {

    public LogFilterCriterionDurationMaxWaiting(Action action, Containment containment, Level level, String label, String attribute, Set<String> value) {
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

        long maxWaitingTime = getMaxWaitingTime(trace);
        if(maxWaitingTime == 0) return false;
        else if(maxWaitingTime < greaterThan) return false;
        else if(maxWaitingTime > lesserThan) return false;
        else return true;
    }

    @Override
    public String toString() {
        String minString = "", maxString = "";
        for(String v : value) {
            if(v.startsWith(">")) minString = v.substring(v.indexOf(">") + 1);
            if(v.startsWith("<"))maxString = v.substring(v.indexOf("<") + 1);
        }
        return super.getAction() + " all traces with a max waiting time between " +
                minString + " to " +
                maxString;
    }

    @Override
    public String getAttribute() {
        return "duration:max_waiting";
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

    private long getMaxWaitingTime(XTrace xTrace) {
        long maxWaiting = 0;

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
                if(waiting > maxWaiting) maxWaiting = waiting;
            }
        }

        return maxWaiting;
    }
}


