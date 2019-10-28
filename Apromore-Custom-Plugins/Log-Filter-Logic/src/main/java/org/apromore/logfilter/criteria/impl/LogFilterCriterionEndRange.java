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
    public String toString() {
        String display = super.getAction().toString().substring(0,1).toUpperCase() +
                super.getAction().toString().substring(1).toLowerCase() +
                " traces that contain end event in the timestamp range between " +
                new Date(start) + " and " + new Date(end);
        if(start==0) {
            display = super.getAction().toString().substring(0,1).toUpperCase() +
                    super.getAction().toString().substring(1).toLowerCase() +
                    " traces that contain end event in the timestamp range between " +
                    new Date(traceStart) + " and " + new Date(end);
        }
        return display;
    }

    @Override
    public String getAttribute() {
        return "time:endrange";
    }
}
