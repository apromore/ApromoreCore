package org.apromore.logman.stats.collector.timeaware;

import java.util.stream.IntStream;
import org.apromore.logman.log.activityaware.AXTrace;
import org.apromore.logman.log.event.LogFilteredEvent;
import org.apromore.logman.utils.LogUtils;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.api.tuple.Pair;

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
    public void onLogFiltered(LogFilteredEvent filterEvent) {
        for (XTrace trace: filterEvent.getDeletedTraces()) {
        	int[] overlapWindows = getOverlappingWindows(LogUtils.getTimestamp(trace.get(0)),
        						LogUtils.getTimestamp(trace.get(trace.size()-1)));
        	for (int i : overlapWindows) {
        		this.values[i] = this.values[i] - 1; 
        	}
        }
        
        for (Pair<XTrace,XTrace> pair : filterEvent.getUpdatedTraces()) {
        	XTrace old = pair.getOne();
        	XTrace ne = pair.getTwo();
        	int[] oldWindows = this.getOverlappingWindows(LogUtils.getTimestamp(old.get(0)), LogUtils.getTimestamp(old.get(old.size()-1)));
        	int[] newWindows = this.getOverlappingWindows(LogUtils.getTimestamp(ne.get(0)), LogUtils.getTimestamp(ne.get(ne.size()-1)));
        	IntStream is = IntStream.of(newWindows);
        	
        	// old windows not included in the new windows must be updated.
        	for (int i: oldWindows) {
        		if (!is.anyMatch(x -> x==i)) {
        			this.values[i] = this.values[i] - 1;
        		}
        	}
        }
    }
}
