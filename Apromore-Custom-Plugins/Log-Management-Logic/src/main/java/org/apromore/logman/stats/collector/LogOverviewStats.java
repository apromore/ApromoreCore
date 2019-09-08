package org.apromore.logman.stats.collector;

import org.apromore.logman.log.activityaware.AXLog;
import org.apromore.logman.log.activityaware.AXTrace;
import org.apromore.logman.log.activityaware.Activity;
import org.apromore.logman.log.event.LogFilteredEvent;
import org.apromore.logman.utils.LogUtils;
import org.deckfour.xes.model.XEvent;

public class LogOverviewStats extends StatsCollector {
    private long eventCount = 0;
    private long actCount = 0;
    private long caseCount = 0;
    private long minTime = Long.MAX_VALUE;
    private long maxTime = Long.MIN_VALUE;
    
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
        return minTime;
    }
    
    public long getLogMaxTime() {
        return maxTime;
    }    
    
    @Override
    public void initialize() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void reset() {
        eventCount = 0;
        actCount = 0;
        caseCount = 0;        
        minTime = Long.MAX_VALUE;
        maxTime = Long.MIN_VALUE;
    }
    
    @Override
    public void visitLog(AXLog log) {
        caseCount = log.size();
    }

    @Override
    public void visitTrace(AXTrace trace) {
        if (!trace.isEmpty()) {
            if (LogUtils.getTimeMilliseconds(trace.get(0)) < minTime) {
                minTime = LogUtils.getTimeMilliseconds(trace.get(0));
            }
            if (LogUtils.getTimeMilliseconds(trace.get(trace.size()-1)) > maxTime) {
                maxTime = LogUtils.getTimeMilliseconds(trace.get(trace.size()-1));
            }
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



}
