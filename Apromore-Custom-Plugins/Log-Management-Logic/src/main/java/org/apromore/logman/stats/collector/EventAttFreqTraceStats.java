package org.apromore.logman.stats.collector;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apromore.logman.log.activityaware.AXLog;
import org.apromore.logman.log.activityaware.AXTrace;
import org.apromore.logman.log.activityaware.Activity;
import org.apromore.logman.log.event.LogFilteredEvent;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;

public class EventAttFreqTraceStats extends StatsCollector {
	// Attribute name => (Attribute value => list of counts, each is attribute count in one trace 
	private Map<String, Map<String, LongArrayList>> attFreqMap;
	private int traceIndex;
	
	public EventAttFreqTraceStats() {
		attFreqMap = new HashMap<>();
		traceIndex = -1;
	}
	
	public Map<String, LongArrayList> getAttributeCountMap(String attribute) {
		return (attFreqMap.containsKey(attribute) ? Collections.unmodifiableMap(attFreqMap.get(attribute)) : null);
	}
	
	public LongArrayList getAttValueCountMap(String attribute, String attValue) {
		if (attFreqMap.containsKey(attribute)) {
			if (attFreqMap.get(attribute).containsKey(attValue)) {
				return attFreqMap.get(attribute).get(attValue);
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
	}
	
	
	///////////////////////// Collect statistics the first time //////////////////////////////
    @Override
    public void visitTrace(AXTrace trace) {
    	traceIndex++;
    }

    @Override
    public void visitEvent(XEvent event) {
    	for (XAttribute attribute : event.getAttributes().values()) {
            if (!(attribute instanceof XAttributeTimestamp)) {
                String key = attribute.getKey();
                if(attFreqMap.get(key) == null) attFreqMap.put(key, new HashMap<>());
                attFreqMap.get(key).get(attribute.toString()).addAtIndex(traceIndex, 1); 
            }
        }
    }
    
    ///////////////////////// Update statistics //////////////////////////////
    
    @Override
    public void onLogFiltered(LogFilteredEvent event) {
        // TODO Auto-generated method stub
        
    }
}
