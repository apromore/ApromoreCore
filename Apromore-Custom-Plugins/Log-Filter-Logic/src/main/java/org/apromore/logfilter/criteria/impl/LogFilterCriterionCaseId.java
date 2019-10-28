package org.apromore.logfilter.criteria.impl;

import org.apromore.logfilter.criteria.model.Action;
import org.apromore.logfilter.criteria.model.Containment;
import org.apromore.logfilter.criteria.model.Level;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

import java.util.Set;

public class LogFilterCriterionCaseId extends AbstractLogFilterCriterion {

    public LogFilterCriterionCaseId(Action action, Containment containment, Level level, String label, String attribute, Set<String> value) {
        super(action, containment, level, label, attribute, value);
    }

    @Override
    protected boolean matchesCriterion(XEvent event) {
        return false;
    }

    @Override
    protected boolean matchesCriterion(XTrace trace) {
        if(trace.getAttributes().containsKey("concept:name")) {
            String caseId = trace.getAttributes().get("concept:name").toString();
            if(value.contains(caseId)) return true;
            else return false;
        }
        return false;
    }

    @Override
    public String toString() {
        return super.getAction().toString().substring(0,1).toUpperCase() +
                super.getAction().toString().substring(1).toLowerCase() +
                " traces such that the trace ID equals to " + value;
    }

    @Override
    public String getAttribute() {
        return "case:id";
    }
}
