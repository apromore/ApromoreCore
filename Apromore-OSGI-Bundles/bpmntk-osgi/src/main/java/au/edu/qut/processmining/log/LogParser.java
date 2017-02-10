/*
 * Copyright © 2009-2017 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package au.edu.qut.processmining.log;

import au.edu.qut.processmining.log.graph.fuzzy.FuzzyNet;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import java.util.HashMap;

/**
 * Created by Adriano on 14/06/2016.
 */
public class LogParser {

    private static final int STARTCODE = 0;
    private static final int ENDCODE = -1;

    public static FuzzyNet initFuzzyNet(XLog log) { return (new FuzzyNet(log)); }

    public static SimpleLog getSimpleLog(XLog log) {
        System.out.println("LOGP - starting ... ");
        System.out.println("LOGP Parser - input log size: " + log.size());

        SimpleLog sLog;

        HashMap<String, Integer> parsed = new HashMap<>();  //this maps the original name of an event to its code
        HashMap<Integer, String> events = new HashMap<>();  //this maps the code of the event to its original name
        HashMap<String, Integer> traces = new HashMap<>();  //this is the simple log, each trace is a string associated to its frequency

        int tIndex; //index to iterate on the log traces
        int eIndex; //index to iterate on the events of the trace

        XTrace trace;
        String sTrace;

        XEvent event;
        String label;

        int eventCounter;
        long totalEvents;
        long oldTotalEvents;

        long traceLength;
        long longestTrace = Integer.MIN_VALUE;
        long shortestTrace = Integer.MAX_VALUE;

        int totalTraces = log.size();
        long traceSize;

        events.put(STARTCODE, "autogen-start");
        events.put(ENDCODE, "autogen-end");

        totalEvents = 0;
        eventCounter = 1;

        for( tIndex = 0; tIndex < totalTraces; tIndex++ ) {
            /* we convert each trace in the log into a string
            *  each string will be a sequence of "::x" terminated with "::", where:
            *  '::' is a separator
            *  'x' is an integer encoding the name of the original event
            */
            trace = log.get(tIndex);
            traceSize = trace.size();

            oldTotalEvents = totalEvents;
            sTrace = "::" + Integer.toString(STARTCODE) + ":";
            for( eIndex = 0; eIndex < traceSize; eIndex++ ) {
                totalEvents++;
                event = trace.get(eIndex);
                label = event.getAttributes().get("concept:name").toString();

                if( !parsed.containsKey(label) ) {
                    parsed.put(label, eventCounter);
                    events.put(eventCounter, label);
                    eventCounter++;
                }

                sTrace += ":" + parsed.get(label).toString() + ":";
            }
            sTrace += ":" + Integer.toString(ENDCODE) + "::";
            traceLength = totalEvents - oldTotalEvents;

            if( longestTrace < traceLength ) longestTrace = traceLength;
            if( shortestTrace > traceLength ) shortestTrace = traceLength;

            if( !traces.containsKey(sTrace) ) traces.put(sTrace, 0);
            traces.put(sTrace, traces.get(sTrace)+1);
        }

        System.out.println("LOGP - total events parsed: " + totalEvents);
        System.out.println("LOGP - total distinct events: " + (events.size() - 2) );
        System.out.println("LOGP - total distinct traces: " + traces.size() );

//        for( String t : traces.keySet() ) System.out.println("DEBUG - ["+ traces.get(t) +"] trace: " + t);

//        System.out.println("DEBUG - final mapping:");
//        for( int code : events.keySet() ) System.out.println("DEBUG - " + code + " = " + events.get(code));

        sLog = new SimpleLog(traces, events);
        sLog.setStartcode(STARTCODE);
        sLog.setEndcode(ENDCODE);
        sLog.setTotalEvents(totalEvents);
        sLog.setShortestTrace(shortestTrace);
        sLog.setLongestTrace(longestTrace);

        return sLog;
    }
}
