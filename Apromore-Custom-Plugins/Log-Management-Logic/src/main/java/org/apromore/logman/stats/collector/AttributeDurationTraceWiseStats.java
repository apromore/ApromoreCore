package org.apromore.logman.stats.collector;

import org.apromore.logman.log.activityaware.AXLog;
import org.apromore.logman.log.activityaware.AXTrace;
import org.apromore.logman.log.activityaware.Activity;
import org.apromore.logman.log.event.LogFilteredEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;

public class AttributeDurationTraceWiseStats extends StatsCollector {
	private XLog log;
	
	// 
//	private IntObjectHashMap<LongArrayList>
	
	///////////////////////// Collect statistics the first time //////////////////////////////
	
	@Override
    public void visitLog(XLog log) {
		this.log = log;
    }
	
    @Override
    public void visitTrace(XTrace trace) {
                
    }

    @Override
    public void visitActivity(Activity act) {
        // TODO Auto-generated method stub
        
    }
    
    ///////////////////////// Update statistics //////////////////////////////
    
    @Override
    public void onLogFiltered(LogFilteredEvent event) {
        // TODO Auto-generated method stub
        
    }
}
