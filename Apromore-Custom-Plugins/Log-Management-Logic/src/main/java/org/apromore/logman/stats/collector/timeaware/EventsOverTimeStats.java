package org.apromore.logman.stats.collector.timeaware;

import org.apromore.logman.LogManager;
import org.apromore.logman.log.event.LogFilteredEvent;
import org.apromore.logman.utils.LogUtils;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;

public class EventsOverTimeStats extends TimeAwareStatsCollector {
	
	@Override 
	public void startVisit(LogManager logManager) {
		super.startVisit(logManager);
	}
	
	@Override
	public void visitLog(XLog log) {
		super.visitLog(log);
	}
	

	@Override
    public void visitEvent(XEvent event) {
    	int containingWindow = this.getContainingWindow(LogUtils.getTimestamp(event));
    	if (containingWindow > 0) {
    		this.values[containingWindow] = this.values[containingWindow] + 1; 
    	}
    }
	
    @Override
    public void onLogFiltered(LogFilteredEvent filterEvent) {
        for (XEvent e: filterEvent.getAllDeletedEvents()) {
        	int window = getContainingWindow(LogUtils.getTimestamp(e));
        	this.values[window] = this.values[window] - 1; 
        }
    }
}
