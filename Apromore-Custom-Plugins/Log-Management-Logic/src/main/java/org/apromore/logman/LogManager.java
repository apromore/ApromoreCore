package org.apromore.logman;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apromore.logfilter.criteria.LogFilterCriterion;
import org.apromore.logman.classifier.SimpleEventClassifier;
import org.apromore.logman.event.ClassifierChangeListener;
import org.apromore.logman.event.ClassifierChangedEvent;
import org.apromore.logman.event.LogFilterListener;
import org.apromore.logman.event.LogFilteredEvent;
import org.apromore.logman.log.activityaware.AXLog;
import org.apromore.logman.log.activityaware.AXTrace;
import org.apromore.logman.log.activityaware.Activity;
import org.apromore.logman.log.classifieraware.SimpleLog;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

/**
 * LogManager is used to manage a log and the operations on the log
 * The log may change due to a number of actions:
 * 		- Filtering actions: remove events, update activities, traces 
 * 		- Change the classifier attribute
 * 		- ...
 * 
 * Other objects need to access to the log for calculations. For example,
 * these calculates can be various statistics derived from the log, or 
 * calculations of case variants. These calculations usually involve traversing
 * all events, activities and traces in the log.
 * 
 * For efficiency, LogManager expects these objects to do their calculation
 * all at once rather than that each object traverses the log separately multiple times.
 * To do that, each object must implement the LogVisitor interface and register themselves
 * with the LogManager. When the LogManager scans the log, it will notify these 
 * objects when it visits different structural elements in the log. 
 * 
 * To avoid recaculating every time the log has been filtered, these objects must implement 
 * LogFilterListener interface and register with LogManager to be notified of changes in the log. 
 * In that way, they can update their calculation incrementally rather than redoing it from scratch.
 * 
 * LogManager keeps a integer-based log of the original log. Each trace is a list of integers which
 * are mapped from the event values based on a chosen event classifier. This IntLog is efficient in 
 * calculating case variants and relations based on the event classifier. However, this IntLog is 
 * lazily created, i.e. it is created from the original log only when there are any required objects
 * to do related calculations (e.g. case variants).
 * 
 * It is advised to register dependent objects with LogManager right from the beginning rather than
 * somewhere in the log handling process. This is because if they are registered in the middle
 * of the process, the original log might have been filtered and it will create unexpected behavior
 * in the program.
 * 
 * @author Bruce Nguyen
 *
 */
public class LogManager {
    private AXLog log;
    private AttributeStore attributeStore;
    private SimpleLog intLog;
    private SimpleEventClassifier classifier;
    
    private List<LogFilterCriterion> filterCriteria;
    private AXLog filteredLog;
    private SimpleLog filteredIntLog;
    
    private List<LogVisitor> logVisitors;
    private List<LogFilterListener> logFilterListeners;
    private List<ClassifierChangeListener> classifierChangeListeners;
    
    public LogManager(AXLog log) {
        this(log, new SimpleEventClassifier(Constants.CONCEPT_NAME));
    }
    
    public LogManager(AXLog log, SimpleEventClassifier classifier) {
        this.log = log;
        logVisitors = new ArrayList<>();
        logFilterListeners = new ArrayList<>();
        attributeStore = new AttributeStore(log);
    }
    
    //@todo: return an unmodifiable version
    public AXLog getLog() {
    	return this.log;
    }
    
    public AttributeStore getAttributeStore() {
    	return this.attributeStore;
    }
    
    public SimpleLog createIntLog() {
        intLog = new SimpleLog(log, classifier);
        this.registerLogFilterListener(intLog); // register to keep IntLog in sync with the main log
        return intLog;
    }
    
    //@todo: return an unmodifiable version
    public SimpleLog getIntLog() {
    	return this.intLog;
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
    
    public void visitLog() {
    	startVisit();
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
        finishVisit();
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
    
    public void setClassifier(SimpleEventClassifier newClassifier) {
        if (!this.classifier.equals(newClassifier)) {
            this.classifier = newClassifier;
            ClassifierChangedEvent event = new ClassifierChangedEvent();
            for (ClassifierChangeListener listener : classifierChangeListeners) {
                listener.onPerspectiveChanged(event);
            }            
        }
    }
    
    private void startVisit() {
        for (LogVisitor visitor : logVisitors) {
            visitor.startVisit(this);
        }
    }    
    
    private void finishVisit() {
        for (LogVisitor visitor : logVisitors) {
            visitor.finishVisit();
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
