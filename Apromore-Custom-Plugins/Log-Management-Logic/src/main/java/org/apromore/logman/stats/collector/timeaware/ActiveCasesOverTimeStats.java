package org.apromore.logman.stats.collector.timeaware;

import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Set;
import org.apromore.logman.log.activityaware.AXTrace;
import org.apromore.logman.log.event.LogFilteredEvent;
import org.apromore.logman.utils.LogUtils;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

public class ActiveCasesOverTimeStats extends TimeAwareStatsCollector {
	
	public ActiveCasesOverTimeStats(XLog xlog) {
		super(xlog);
	}

    @Override
    public void visitTrace(AXTrace trace) {
    	int[] overlapWindows = getOverlappingWindows(trace.getStartTimestamp(),trace.getEndTimestamp());
    	for (int i : overlapWindows) {
    		this.values[i] = this.values[i] + 1; 
    	}
    }
    
    @Override
    public void finishVisit() {
    }


    @Override
    public void onLogFiltered(LogFilteredEvent filterEvent) {
        for (XTrace trace: filterEvent.getDeletedTraces()) {
        	int[] overlapWindows = getOverlappingWindows(LogUtils.getTimestamp(trace.get(0)),
        						LogUtils.getTimestamp(trace.get(trace.size()-1)));
        	for (int i : overlapWindows) {
        		this.values[i] = this.values[i] - 1; 
        	}
        }
        
        for (Entry<XTrace,Set<XEvent>> deleted : filterEvent.getDeletedEvents().entrySet()) {
        	XTrace trace = deleted.getKey();
        	Set<XEvent> events = deleted.getValue();
        	long[] timestamps = new long[events.size()];
        	int i=0;
        	for (XEvent event : events) {
        		timestamps[i] = LogUtils.getTimestamp(event);
        		i++;
        	}
        	Arrays.sort(timestamps);
//        	if (timestamps[0] < LogUtils.getTimestamp(trace.get(0)))
        }
    }
}
