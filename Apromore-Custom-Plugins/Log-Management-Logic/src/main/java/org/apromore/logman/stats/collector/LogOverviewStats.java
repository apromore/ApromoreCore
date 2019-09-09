package org.apromore.logman.stats.collector;

import org.apromore.logman.log.activityaware.AXLog;
import org.apromore.logman.log.activityaware.AXTrace;
import org.apromore.logman.log.activityaware.Activity;
import org.apromore.logman.log.event.LogFilteredEvent;
import org.apromore.logman.utils.LogUtils;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;

public class LogOverviewStats extends StatsCollector {
	private AXLog xlog;
    private long eventCount = 0;
    private long actCount = 0;
    private long caseCount = 0;
    private LongArrayList traceStartTimes = new LongArrayList(); // each element is for one trace in order
    private LongArrayList traceEndTimes = new LongArrayList(); // each element is for one trace in order
    
    public LogOverviewStats() {
        eventCount = 0;
        actCount = 0;
        caseCount = 0;       
        traceStartTimes.clear();
        traceEndTimes.clear();
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

    @Override
    public void reset() {
        eventCount = 0;
        actCount = 0;
        caseCount = 0;       
        traceStartTimes.clear();
        traceEndTimes.clear();
    }
    

    ///////////////////////// Collect statistics the first time //////////////////////////////
    
    @Override
    public void visitLog(AXLog log) {
    	xlog = log;
        caseCount = log.size();
    }

    @Override
    public void visitTrace(AXTrace trace) {
        if (!trace.isEmpty()) {
            traceStartTimes.add(LogUtils.getTimeMilliseconds(trace.get(0)));
            traceEndTimes.add(LogUtils.getTimeMilliseconds(trace.get(trace.size()-1)));
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
    	eventCount -= filterEvent.getAllDeletedEvents().size();
    	actCount -= filterEvent.getAllDeletedActs().size();
    	
    	for (XTrace trace : filterEvent.getDeletedTraces()) {
    		traceStartTimes.remove(xlog.indexOf(trace));
    		traceEndTimes.remove(xlog.indexOf(trace));
    	}
    	
    	for (XTrace trace : filterEvent.getDeletedEvents().keySet()) {
    		int traceIndex = xlog.indexOf(trace);
    		for (int i=0; i<trace.size(); i++) {
    			XEvent e = trace.get(i);
    			if (!filterEvent.getDeletedEvents().get(trace).contains(e)) {
    				traceStartTimes.set(traceIndex, LogUtils.getTimeMilliseconds(e));
    				break;
    			}
    		}
    		for (int i=trace.size()-1; i>=0; i--) {
    			XEvent e = trace.get(i);
    			if (!filterEvent.getDeletedEvents().get(trace).contains(e)) {
    				traceEndTimes.set(traceIndex, LogUtils.getTimeMilliseconds(e));
    				break;
    			}
    		}
    	}
    }

}
