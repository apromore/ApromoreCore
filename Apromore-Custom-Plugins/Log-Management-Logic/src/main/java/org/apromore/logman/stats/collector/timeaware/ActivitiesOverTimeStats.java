package org.apromore.logman.stats.collector.timeaware;

import java.util.stream.IntStream;

import org.apromore.logman.LogManager;
import org.apromore.logman.log.activityaware.Activity;
import org.apromore.logman.log.event.LogFilteredEvent;
import org.deckfour.xes.model.XLog;
import org.eclipse.collections.api.tuple.Pair;

public class ActivitiesOverTimeStats extends TimeAwareStatsCollector {
	
	///////////////////////// Collect statistics the first time //////////////////////////////
	@Override 
	public void startVisit(LogManager logManager) {
		super.startVisit(logManager);
	}
	
	@Override
	public void visitLog(XLog log) {
		super.visitLog(log);
	}
	

    @Override
    public void visitActivity(Activity act) {
    	int[] overlapWindows = getOverlappingWindows(act.getStartTimestamp(),act.getEndTimestamp());
    	for (int i : overlapWindows) {
    		this.values[i] = this.values[i] + 1; 
    	}
    }

	///////////////////////// Update statistics //////////////////////////////
    
    
    @Override
    public void onLogFiltered(LogFilteredEvent filterEvent) {
        for (Activity act: filterEvent.getAllDeletedActs()) {
        	int[] overlapWindows = getOverlappingWindows(act.getStartTimestamp(), act.getEndTimestamp());
        	for (int i : overlapWindows) {
        		this.values[i] = this.values[i] - 1; 
        	}
        }
        
        for (Pair<Activity,Activity> pair : filterEvent.getUpdatedActs()) {
        	Activity old = pair.getOne();
        	Activity ne = pair.getTwo();
        	int[] oldWindows = this.getOverlappingWindows(old.getStartTimestamp(), old.getEndTimestamp());
        	int[] newWindows = this.getOverlappingWindows(ne.getStartTimestamp(), ne.getEndTimestamp());
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
