package org.apromore.logman;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apromore.logfilter.criteria.LogFilterCriterion;
import org.apromore.logman.classifier.EventClassifier;
import org.apromore.logman.log.Constants;
import org.apromore.logman.log.LogVisitor;
import org.apromore.logman.log.activityaware.AXLog;
import org.apromore.logman.log.activityaware.AXTrace;
import org.apromore.logman.log.activityaware.Activity;
import org.apromore.logman.log.classifieraware.IntLog;
import org.apromore.logman.log.event.LogFilterListener;
import org.apromore.logman.log.event.LogFilteredEvent;
import org.apromore.logman.log.event.ClassifierChangeListener;
import org.apromore.logman.log.event.ClassifierChangedEvent;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

/**
 * LogManager is used to manage a log and the operations on the log
 * The operations on logs include:
 * 		- Filtering actions 
 * 		- Compute statistics
 * 		- Set a perspective attribute
 * 		- Compute case variants
 * 
 * @author Bruce Nguyen
 *
 */
public class LogManager {
    private AXLog log;
    private IntLog intLog;
    private EventClassifier classifier;
    
    private List<LogFilterCriterion> filterCriteria;
    private AXLog filteredLog;
    private IntLog filteredIntLog;
    
    private List<LogVisitor> logVisitors;
    private List<LogFilterListener> logFilterListeners;
    private List<ClassifierChangeListener> classifierChangeListeners;
    
    public LogManager(AXLog log) {
        this(log, new EventClassifier(Constants.CONCEPT_NAME));
    }
    
    public LogManager(AXLog log, EventClassifier classifier) {
        this.log = log;
        logVisitors = new ArrayList<>();
        logFilterListeners = new ArrayList<>();
        
        intLog = new IntLog(log, classifier);
        this.registerLogFilterListener(intLog);
    }
    
    //@todo: return an unmodifiable version
    public AXLog getLog() {
    	return this.log;
    }
    
    //@todo: return an unmodifiable version
    public IntLog getIntLog() {
    	return this.intLog;
    }
    
    
    // return: List of trace indexes belonging to a case variant => CountOfCaseVariant
    public Map<List<Integer>, Integer> getCaseVariantMap() {
    	return this.intLog.getCaseVariantMap();
    }
    
    public List<LogFilterCriterion> getLogFilterCriteria() {
    	return Collections.unmodifiableList(filterCriteria);
    }

    public void registerLogVisitor(LogVisitor visitor) {
        this.logVisitors.add(visitor);
    }
    
    public void registerLogFilterListener(LogFilterListener listener) {
        this.logFilterListeners.add(listener);
    }
    
    public void registerPerspectiveChangeListener(ClassifierChangeListener listener) {
        this.classifierChangeListeners.add(listener);
    }
    
    public void scan() {
        visit(log);
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
    
    public void setClassifier(EventClassifier newClassifier) {
        if (!this.classifier.equals(newClassifier)) {
            this.classifier = newClassifier;
            ClassifierChangedEvent event = new ClassifierChangedEvent();
            for (ClassifierChangeListener listener : classifierChangeListeners) {
                listener.onPerspectiveChanged(event);
            }            
        }
    }
    
    private void visit(AXLog log) {
        for (LogVisitor visitor : logVisitors) {
            visitor.visitLog(log);
        }
    }
    
    private void visit(AXTrace trace) {
        for (LogVisitor visitor : logVisitors) {
            visitor.visitTrace(trace);
        }
    }
    
    private void visit(Activity act) {
        for (LogVisitor visitor : logVisitors) {
            visitor.visitActivity(act);
        }
    }
    
    private void visit(XEvent event) {
        for (LogVisitor visitor : logVisitors) {
            visitor.visitEvent(event);
        }
    }
}
