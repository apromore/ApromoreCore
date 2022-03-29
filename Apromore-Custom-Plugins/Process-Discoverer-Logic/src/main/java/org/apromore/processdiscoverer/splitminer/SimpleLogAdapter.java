/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.processdiscoverer.splitminer;

import java.util.HashMap;
import java.util.Map;

import org.apromore.logman.attribute.log.AttributeLog;
import org.apromore.logman.attribute.log.AttributeTrace;
import org.apromore.logman.utils.LogUtils;
import org.apromore.splitminer.log.SimpleLog;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.api.list.primitive.IntList;


/**
 * Adapter class from AttributeLog to SimpleLog used by Split Miner
 * 
 * @author Bruce Nguyen
 *
 */
public class SimpleLogAdapter {
	
	public static SimpleLog getSimpleLog (AttributeLog log) throws Exception {
		Map<Integer, String> eventToNameMap = extractEventToNameMap(log);  //this maps the code of the event to its original name
        Map<String, Integer> nameToEventMap = extractNameToEventMap(log);  //this maps the event name to its code
        
        long longestTrace = Integer.MIN_VALUE;
        long shortestTrace = Integer.MAX_VALUE;
        long totalEvents = 0;
        Map<String, Integer> traces = new HashMap<>();  //this is the simple log, each trace is a string associated to its frequency
        for (int tIndex = 0; tIndex < log.getTraces().size(); tIndex++) {
            /* we convert each trace in the log into a string
             *  each string will be a sequence of "::x" terminated with "::", where:
             *  '::' is a separator
             *  'x' is an integer encoding the name of the original event
             */
            IntList trace = log.getTraceFromIndex(tIndex).getValueTrace();
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

        SimpleLog sLog = new SimpleLog(traces, eventToNameMap,  getXLogFromAttriuteLog(log));
        sLog.setReverseMap(nameToEventMap);
        sLog.setStartcode(log.getStartEvent());
        sLog.setEndcode(log.getEndEvent());
        sLog.setTotalEvents(totalEvents);
        sLog.setShortestTrace(shortestTrace);
        sLog.setLongestTrace(longestTrace);

        return sLog;
	}
	
	private static Map<Integer, String> extractEventToNameMap(AttributeLog log) {
		Map<Integer, String> newMap = new HashMap<>();
		for (int value: log.getAttributeValues().toArray()) {
			newMap.put(value, log.getStringFromValue(value));
		}
		return newMap;
	}
	
	private static Map<String, Integer> extractNameToEventMap(AttributeLog log) {
		Map<String, Integer> newMap = new HashMap<>();
		for (int value: log.getAttributeValues().toArray()) {
			newMap.put(log.getStringFromValue(value), value);
		}
		return newMap;
	}
	
	private static XLog getXLogFromAttriuteLog(AttributeLog attLog) {
	    XFactory factory = new XFactoryNaiveImpl();
	    XLog xlog = factory.createLog();
	    for (AttributeTrace attTrace : attLog.getTraces()) {
	        XTrace xtrace = factory.createTrace();
	        for (int event : attTrace.getValueTrace().toArray()) {
	            XEvent xevent = factory.createEvent();
	            LogUtils.setConceptName(xevent, event+"");
	            xtrace.add(xevent);
	        }
	        xlog.add(xtrace);
	    }
	    return xlog;
	}

}
