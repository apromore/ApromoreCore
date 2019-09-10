package org.apromore.logman.stats.collector.timeaware;

import org.apromore.logman.log.activityaware.AXTrace;
import org.apromore.logman.log.activityaware.Activity;
import org.apromore.logman.log.event.LogFilteredEvent;
import org.deckfour.xes.model.XLog;

public class ActivitiesOverTimeStats extends TimeAwareStatsCollector {
	
	public ActivitiesOverTimeStats(XLog xlog) {
		super(xlog);
	}

    @Override
    public void visitActivity(Activity act) {
    	int[] overlapWindows = getOverlappingWindows(act.getStartTimestamp(),act.getEndTimestamp());
    	for (int i : overlapWindows) {
    		this.values[i] = this.values[i] + 1; 
    	}
    }
    
    @Override
    public void finishVisit() {
    }


    @Override
    public void onLogFiltered(LogFilteredEvent event) {
        // TODO Auto-generated method stub
        
    }
}
