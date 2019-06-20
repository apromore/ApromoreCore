package org.apromore.processdiscoverer.splitminer;

import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import org.apromore.processdiscoverer.logprocessors.LogUtils;
import org.apromore.processdiscoverer.logprocessors.SimplifiedLog;
import org.deckfour.xes.model.XLog;
import org.eclipse.collections.api.list.primitive.IntList;

import com.raffaeleconforti.splitminer.log.SimpleLog;



public class SimpleLogAdapter {
	
	public static SimpleLog getSimpleLog (SimplifiedLog log) throws Exception {
		//SimplifiedLog completeEventLog = log.containStartEvent() ? log.filterActivities(log.getCompleteEvents()) : log; 
		SimplifiedLog completeEventLog = log;
		Map<Integer, String> eventToNameMap = extractEventToNameMap(completeEventLog.getEventToNameMap());  //this maps the code of the event to its original name
        Map<String, Integer> nameToEventMap = extractNameToEventMap(completeEventLog.getNameToEventMap());  //this maps the event name to its code
        
        long longestTrace = Integer.MIN_VALUE;
        long shortestTrace = Integer.MAX_VALUE;
        long totalEvents = 0;
        Map<String, Integer> traces = new HashMap<>();  //this is the simple log, each trace is a string associated to its frequency
        for (int tIndex = 0; tIndex < completeEventLog.size(); tIndex++) {
            /* we convert each trace in the log into a string
             *  each string will be a sequence of "::x" terminated with "::", where:
             *  '::' is a separator
             *  'x' is an integer encoding the name of the original event
             */
            IntList trace = completeEventLog.get(tIndex);
            long oldTotalEvents = totalEvents;
            String sTrace = "::" + trace.get(0) + ":";
            for (int eIndex = 1; eIndex < trace.size()-1; eIndex++) { // the first and last events are start and end markers
                totalEvents++;
                sTrace += ":" + trace.get(eIndex) + ":";
            }
            sTrace += ":" + trace.get(trace.size()-1) + "::";

            long traceLength = totalEvents - oldTotalEvents;
            if (longestTrace < traceLength) longestTrace = traceLength;
            if (shortestTrace > traceLength) shortestTrace = traceLength;

            if (!traces.containsKey(sTrace)) traces.put(sTrace, 0);
            traces.put(sTrace, traces.get(sTrace) + 1);
        }

        SimpleLog sLog = new SimpleLog(traces, eventToNameMap, completeEventLog.getXLog());
        sLog.setReverseMap(nameToEventMap);
        sLog.setStartcode(completeEventLog.getStartEvent());
        sLog.setEndcode(completeEventLog.getEndEvent());
        sLog.setTotalEvents(totalEvents);
        sLog.setShortestTrace(shortestTrace);
        sLog.setLongestTrace(longestTrace);

        return sLog;
	}
	
	private static Map<Integer, String> extractEventToNameMap(Map<Integer, String> eventToNameMap) {
		Map<Integer, String> newMap = new HashMap<>();
		for (Integer event: eventToNameMap.keySet()) {
			newMap.put(event, LogUtils.getCollapsedEvent(eventToNameMap.get(event)));
		}
		return newMap;
	}
	
	// This is mapping from collapsed names to integer-based events. If the log
	// has start and complete events of the same activity, e.g. A.start and A.complete,
	// the collapsed name would be "A" but there are two integer-based events corresponding
	// to A.start and A.complete, e.g. A.start=3, A.complete=4.In this situation, 
	// this mapping only contains one of them, i.e. either A->3 or A->4.
	// Fortunately, SplitMiner does not use this mapping, but only use the mapping from
	// events to names, i.e. 3->A, and 4->A.
	private static Map<String, Integer> extractNameToEventMap(Map<String, Integer> nameToEventMap) {
		Map<String, Integer> newMap = new HashMap<>();
		for (String name: nameToEventMap.keySet()) {
			newMap.put(LogUtils.getCollapsedEvent(name), nameToEventMap.get(name));
		}
		return newMap;
	}

}
