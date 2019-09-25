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

public class LogFilterCriterionStartRange extends AbstractLogFilterCriterion {

    private long start = 0;
    private long end = Long.MAX_VALUE;
    private long traceStart = 0;

    public LogFilterCriterionStartRange(Action action, Containment containment, Level level, String label, String attribute, Set<String> value) {
        super(action, containment, level, label, attribute, value);
    }

    @Override
    protected boolean matchesCriterion(XEvent event) {
        return false;
    }

    @Override
    protected boolean matchesCriterion(XTrace trace) {

        for(String v : value) {
            if(v.startsWith(">")) start = Long.parseLong(v.substring(1));
            if(v.startsWith("<")) end = Long.parseLong(v.substring(1));
        }
        XEvent firstEvent = trace.get(0);
        traceStart = ((XAttributeTimestamp) firstEvent.getAttributes().get("time:timestamp")).getValueMillis();
        return start <= traceStart && traceStart <= end;
    }

    @Override
    public String toString() {
        return "Retain traces that contain start event in the timestamp range between " + new Date(start) + " and " + new Date(end);
    }

    @Override
    public String getAttribute() {
        return "time:startrange";
    }
}
