package org.apromore.logman.stats.collector;

import org.apromore.logman.log.activityaware.AXTrace;
import org.apromore.logman.log.event.LogFilteredEvent;
import org.deckfour.xes.model.XLog;

public class ActiveCasesOverTimeStats extends TimeAwareStatsCollector {
	private double avgCaseDuration; //seconds
	
	public ActiveCasesOverTimeStats(XLog xlog) {
		super(xlog);
	}

    @Override
    public void visitTrace(AXTrace trace) {
        super.visitTrace(trace); 
        avgCaseDuration += trace.getDuration()*1000/xlog.size();
    }
    
    @Override
    public void finishVisit() {
    	super.finishVisit();
    	
    }


    @Override
    public void onLogFiltered(LogFilteredEvent event) {
        // TODO Auto-generated method stub
        
    }
}
