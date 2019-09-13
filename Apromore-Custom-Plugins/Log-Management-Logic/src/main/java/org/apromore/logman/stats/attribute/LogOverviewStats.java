package org.apromore.logman.stats.attribute;

import org.apromore.logman.LogManager;
import org.apromore.logman.event.LogFilteredEvent;
import org.apromore.logman.log.activityaware.AXTrace;
import org.apromore.logman.log.activityaware.Activity;
import org.apromore.logman.stats.StatsCollector;
import org.apromore.logman.utils.LogUtils;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;

public class LogOverviewStats extends StatsCollector {
	private XLog xlog;
    private long eventCount = 0;
    private long actCount = 0;
    private long caseCount = 0;
    private LongArrayList traceStartTimes = new LongArrayList(); // each element is for one trace in order
    private LongArrayList traceEndTimes = new LongArrayList(); // each element is for one trace in order
    
    public LogOverviewStats() {

    }
    
    public long getTotalEventCount() {
        return eventCount;
    }
    
    public long getTotalActivityCount() {
        return actCount;
    }
    
    public long getTotalCaseCount() {
        return caseCount;
    }
    
    public long getLogMinTime() {
        return traceStartTimes.min();
    }
    
    public long getLogMaxTime() {
        return traceEndTimes.max();
    }    

    ///////////////////////// Collect statistics the first time //////////////////////////////
    
    @Override
    public void startVisit(LogManager logManager) {
        eventCount = 0;
        actCount = 0;
        caseCount = 0;       
        traceStartTimes.clear();
        traceEndTimes.clear();
    }
    
    @Override
    public void visitLog(XLog log) {
    	xlog = log;
        caseCount = log.size();
    }

    @Override
    public void visitTrace(XTrace trace) {
        if (!trace.isEmpty()) {
            traceStartTimes.add(LogUtils.getTimestamp(trace.get(0)));
            traceEndTimes.add(LogUtils.getTimestamp(trace.get(trace.size()-1)));
        }
    }

    @Override
    public void visitActivity(Activity act) {
        actCount++;
    }

    @Override
    public void visitEvent(XEvent event) {
        eventCount++;
    }

    
    ///////////////////////// Update statistics  //////////////////////////////
    
    @Override
    public void onLogFiltered(LogFilteredEvent filterEvent) {
    	caseCount -= filterEvent.getDeletedTraces().size();
    	eventCount -= filterEvent.getDeletedEvents().size();
    	actCount -= filterEvent.getDeletedActs().size();
    	for (XTrace trace : filterEvent.getDeletedTraces()) {
    		eventCount -= trace.size();
    		if (trace instanceof AXTrace) {
    			actCount -= ((AXTrace)trace).getActivities().size();
    		}
    		else {
    			actCount -= trace.size();
    		}
    	}
    	
    	for (Pair<XTrace,XTrace> pair : filterEvent.getUpdatedTraces()) {
    		int oldTraceIndex = xlog.indexOf(pair.getOne()); // old trace
    		XTrace newTrace = pair.getTwo();
			traceStartTimes.set(oldTraceIndex, LogUtils.getTimestamp(newTrace.get(0)));
			traceEndTimes.set(oldTraceIndex, LogUtils.getTimestamp(newTrace.get(newTrace.size()-1)));
    	}
    	
    	// Note: Delete must be after the update
    	for (XTrace trace : filterEvent.getDeletedTraces()) {
    		traceStartTimes.remove(xlog.indexOf(trace));
    		traceEndTimes.remove(xlog.indexOf(trace));
    	}
    }

}
