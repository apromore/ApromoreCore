package org.apromore.logfilter.criteria.impl;

import org.apromore.logfilter.criteria.model.*;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Set;

public class LogFilterCriterionDurationRange extends AbstractLogFilterCriterion {
    public LogFilterCriterionDurationRange(Action action, Containment containment, Level level, String label, String attribute, Set<String> value) {
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
        for(String v : value) {
            if(v.startsWith(">")) {
                int spaceIndex = v.indexOf(" ");
                String numberString = v.substring(1, spaceIndex);
                long longValue = new Double(numberString).longValue();
                String unit = v.substring(spaceIndex + 1);
                long unitValue = stringToMilli(unit);
                greaterThan = longValue * unitValue;
            }
            if(v.startsWith("<")){
                int spaceIndex = v.indexOf(" ");
                String numberString = v.substring(1, spaceIndex);
                long longValue = new Double(numberString).longValue();
                String unit = v.substring(spaceIndex + 1);
                long unitValue = stringToMilli(unit);
                lesserThan = longValue * unitValue;
            }
        }
        long s = epochMilliOf(zonedDateTimeOf(trace.get(0)));
        long e = epochMilliOf(zonedDateTimeOf(trace.get(trace.size()-1)));
        long dur = e - s;
        if(dur >= greaterThan && dur <= lesserThan) return true;
        else return false;
    }



    public long epochMilliOf(ZonedDateTime zonedDateTime){

        long s = zonedDateTime.toInstant().toEpochMilli();
        return s;
    }

    public ZonedDateTime zonedDateTimeOf(XEvent xEvent) {
        XAttribute da =
                xEvent.getAttributes().get(XTimeExtension.KEY_TIMESTAMP);
        Date d = ((XAttributeTimestamp) da).getValue();
        ZonedDateTime z =
                ZonedDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault());
        return z;
    }

    private long stringToMilli(String s) {
        if(s.equals("Years")) return new Long("31556952000");
        if(s.equals("Months")) return new Long("2628000000");
        if(s.equals("Weeks")) return 1000 * 60 * 60 * 24 * 7;
        if(s.equals("Days")) return 1000 * 60 * 60 * 24;
        if(s.equals("Hours")) return 1000 * 60 * 60;
        if(s.equals("Minutes")) return 1000 * 60;
        if(s.equals("Seconds")) return 1000;
        return 0;
    }
}
