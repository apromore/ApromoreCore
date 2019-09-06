package org.apromore.logman;

import java.util.ArrayList;
import java.util.List;
import org.apromore.logfilter.criteria.LogFilterCriterion;
import org.apromore.logman.log.Constants;
import org.apromore.logman.log.LogVisitor;
import org.apromore.logman.log.durationaware.AXTrace;
import org.apromore.logman.log.durationaware.Activity;
import org.apromore.logman.log.event.LogFilterListener;
import org.apromore.logman.log.event.LogFilteredEvent;
import org.apromore.logman.log.event.PerspectiveChangeListener;
import org.apromore.logman.log.event.PerspectiveChangedEvent;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;


public class LogManager {
    private XLog log;
    private List<LogFilterCriterion> filterCriteria;
    private String perspectiveAttribute;
    
    private List<LogVisitor> logVisitors;
    private List<LogFilterListener> logFilterListeners;
    private List<PerspectiveChangeListener> perspectiveChangeListeners;
    
    public LogManager(XLog log) {
        this(log, Constants.CONCEPT_NAME);
    }
    
    public LogManager(XLog log, String initialPerspective) {
        this.log = log;
        logVisitors = new ArrayList<>();
        logFilterListeners = new ArrayList<>();
        perspectiveAttribute = initialPerspective;
    }
    
    public void registerLogVisitor(LogVisitor visitor) {
        this.logVisitors.add(visitor);
    }
    
    public void registerLogFilterListener(LogFilterListener listener) {
        this.logFilterListeners.add(listener);
    }
    
    public void registerPerspectiveChangeListener(PerspectiveChangeListener listener) {
        this.perspectiveChangeListeners.add(listener);
    }
    
    public void scan() {
        this.prepareBeforeScanning();
        for (XTrace xtrace: log) {
            AXTrace trace = (AXTrace)xtrace;
            visit(trace);
            
            for (XEvent event : trace) {
                visit(event);
            }
            
            for (Activity act: trace.getActivities()) {
                visit(act);
            }
        }
    }
    
    public void filter(List<LogFilterCriterion> filterCriteria) {
        if (!this.filterCriteria.equals(filterCriteria)) {
            this.filterCriteria = filterCriteria;
            
            // Perform log filter
            
            // Update observers
            LogFilteredEvent event = new LogFilteredEvent();
            for (LogFilterListener listener : logFilterListeners) {
                listener.onLogFiltered(event);
            }
        }
    }
    
    public void setPerspective(String perspectiveAttribute) {
        if (!this.perspectiveAttribute.equals(perspectiveAttribute)) {
            this.perspectiveAttribute = perspectiveAttribute;
            PerspectiveChangedEvent event = new PerspectiveChangedEvent();
            for (PerspectiveChangeListener listener : perspectiveChangeListeners) {
                listener.onPerspectiveChanged(event);
            }            
        }
    }
    
    private void prepareBeforeScanning() {
        for (LogVisitor visitor : logVisitors) {
            visitor.beforeVisit();
        }
    }
    
    private void visit(AXTrace trace) {
        for (LogVisitor visitor : logVisitors) {
            visitor.visit(trace);
        }
    }
    
    private void visit(Activity act) {
        for (LogVisitor visitor : logVisitors) {
            visitor.visit(act);
        }
    }
    
    private void visit(XEvent event) {
        for (LogVisitor visitor : logVisitors) {
            visitor.visit(event);
        }
    }
}
