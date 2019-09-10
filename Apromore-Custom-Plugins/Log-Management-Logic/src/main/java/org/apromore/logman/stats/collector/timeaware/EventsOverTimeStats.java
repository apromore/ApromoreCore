package org.apromore.logman.stats.collector.timeaware;

import org.apromore.logman.utils.LogUtils;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;

public class EventsOverTimeStats extends TimeAwareStatsCollector {
	
    public EventsOverTimeStats(XLog xlog) {
		super(xlog);
	}

	@Override
    public void visitEvent(XEvent event) {
    	int containingWindow = this.getContainingWindow(LogUtils.getTimestamp(event));
    	if (containingWindow > 0) {
    		this.values[containingWindow] = this.values[containingWindow] + 1; 
    	}
    }
}
