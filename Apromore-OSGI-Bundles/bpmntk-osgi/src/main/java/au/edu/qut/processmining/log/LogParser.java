/*
 * Copyright © 2009-2018 The Apromore Initiative.
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

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by Adriano on 14/06/2016.
 */
public class LogParser {

//    !!WARNING - DO NOT CHANGE THESE VALUES!
    private static final int STARTCODE = 0;
    private static final int ENDCODE = -1;


    public static SimpleLog getSimpleLog(String path) {
        SimpleLog log;

        HashSet<String> labels = new HashSet<>();
        ArrayList<String> orderedLabels;
        HashMap<String, Integer> labelsToIDs = new HashMap<>();  //this maps the original name of an event to its code
        HashMap<Integer, String> events = new HashMap<>();  //this maps the code of the event to its original name
        HashMap<String, Integer> reverseMap = new HashMap<>();  //this maps the event name to its code
        HashMap<String, Integer> traces = new HashMap<>();  //this is the simple log, each trace is a string associated to its frequency

        int frequency;
        String trace;
        String strace;
        String event;
        StringTokenizer tokenizer;

        int LID;

        BufferedReader reader;

        events.put(STARTCODE, "autogen-start");
        events.put(ENDCODE, "autogen-end");

        try {
            reader = new BufferedReader(new FileReader(path));

            while( reader.ready() )
            {
                trace = reader.readLine();
                tokenizer = new StringTokenizer(trace, "::");
                tokenizer.nextToken();

                while( tokenizer.hasMoreTokens() ) {
                    event = tokenizer.nextToken();
                    labels.add(event);
                }
            }

            reader.close();

            orderedLabels = new ArrayList<>(labels);
            Collections.sort(orderedLabels);

            LID = 1;
            for (String l : orderedLabels) {
                labelsToIDs.put(l, LID);
                events.put(LID, l);
                reverseMap.put(l, LID);
                LID++;
            }

            reader = new BufferedReader(new FileReader(path));

            while( reader.ready() )
            {
                trace = reader.readLine();
                tokenizer = new StringTokenizer(trace, "::");
                frequency = Integer.valueOf(tokenizer.nextToken());

                strace = "::" + STARTCODE + "::";
                while( tokenizer.hasMoreTokens() ) {
                    event = tokenizer.nextToken();
                    strace += (labelsToIDs.get(event) + "::");
                }
                strace += ENDCODE + "::";

                if(!traces.containsKey(strace))  traces.put(strace, frequency);
                else traces.put(strace, traces.get(strace) + frequency);
            }

            reader.close();

            log = new SimpleLog(traces, events, null);
            log.setReverseMap(reverseMap);
            log.setStartcode(STARTCODE);
            log.setEndcode(ENDCODE);

        } catch ( IOException ioe ) {
            System.out.println("ERROR - something went wrong while reading the log file: " + path);
            return null;
        }

        return log;

    }

    public static SimpleLog getSimpleLog(XLog log, XEventClassifier xEventClassifier) {
//        System.out.println("LOGP - starting ... ");
//        System.out.println("LOGP - input log size: " + log.size());

        SimpleLog sLog;

        HashSet<String> labels = new HashSet<>();
        ArrayList<String> orderedLabels;
        HashMap<String, Integer> labelsToIDs = new HashMap<>();  //this maps the original name of an event to its code
        HashMap<Integer, String> events = new HashMap<>();  //this maps the code of the event to its original name
        HashMap<String, Integer> reverseMap = new HashMap<>();  //this maps the event name to its code
        HashMap<String, Integer> traces = new HashMap<>();  //this is the simple log, each trace is a string associated to its frequency

        int tIndex; //index to iterate on the log traces
        int eIndex; //index to iterate on the events of the trace

        XTrace trace;
        String sTrace;

        XEvent event;
        String label;

        int LID;
        long totalEvents;
        long oldTotalEvents;

        long traceLength;
        long longestTrace = Integer.MIN_VALUE;
        long shortestTrace = Integer.MAX_VALUE;

        int totalTraces = log.size();
        long traceSize;

        events.put(STARTCODE, "autogen-start");
        events.put(ENDCODE, "autogen-end");


        for( tIndex = 0; tIndex < totalTraces; tIndex++ ) {
            /*  we firstly get all the concept names
            *   and we map them into numbers for fast processing
            */

            trace = log.get(tIndex);
            traceSize = trace.size();

            for( eIndex = 0; eIndex < traceSize; eIndex++ ) {
                event = trace.get(eIndex);
                label = xEventClassifier.getClassIdentity(event);
                labels.add(label);
            }
        }

        orderedLabels = new ArrayList<>(labels);
        Collections.sort(orderedLabels);

        LID = 1;
        for( String l : orderedLabels ) {
            labelsToIDs.put(l, LID);
            events.put(LID, l);
            reverseMap.put(l, LID);
//            System.out.println("DEBUG - ID:label - " + LID + ":" + l);
            LID++;
        }

        totalEvents = 0;
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
                label = xEventClassifier.getClassIdentity(event);
                sTrace += ":" + labelsToIDs.get(label).toString() + ":";
            }
            sTrace += ":" + Integer.toString(ENDCODE) + "::";

            traceLength = totalEvents - oldTotalEvents;
            if( longestTrace < traceLength ) longestTrace = traceLength;
            if( shortestTrace > traceLength ) shortestTrace = traceLength;

            if( !traces.containsKey(sTrace) ) traces.put(sTrace, 0);
            traces.put(sTrace, traces.get(sTrace)+1);
        }

//        System.out.println("LOGP - total events parsed: " + totalEvents);
//        System.out.println("LOGP - total distinct events: " + (events.size() - 2) );
//        System.out.println("LOGP - total distinct traces: " + traces.size() );

//        for( String t : traces.keySet() ) System.out.println("DEBUG - ["+ traces.get(t) +"] trace: " + t);

//        System.out.println("DEBUG - final mapping:");
//        for( int code : events.keySet() ) System.out.println("DEBUG - " + code + " = " + events.get(code));

        sLog = new SimpleLog(traces, events, log);
        sLog.setReverseMap(reverseMap);
        sLog.setStartcode(STARTCODE);
        sLog.setEndcode(ENDCODE);
        sLog.setTotalEvents(totalEvents);
        sLog.setShortestTrace(shortestTrace);
        sLog.setLongestTrace(longestTrace);

        return sLog;
    }

    public SimpleLog getSimpleLog(XLog log, XEventClassifier xEventClassifier, double percentage) {
        SimpleLog sLog = getSimpleLog(log, xEventClassifier);
        Map<String, Integer> traces = sLog.getTraces();

        TracesComparator tracesComparator = new TracesComparator(traces);
        TreeMap<String, Integer> sortedTraces = new TreeMap(tracesComparator);
        sortedTraces.putAll(traces);

        int maxTraces = (int) (sLog.size() * percentage);
        int parsed = 0;
        int leastFrequent = 0;

        for( String trace : sortedTraces.keySet() ) {
            if( parsed < maxTraces ) {
//                System.out.println("DEBUG - trace, frequency: " + trace + "," + traces.get(trace) );
                parsed += traces.get(trace);
                leastFrequent = traces.get(trace);
            } else sLog.getTraces().remove(trace);
        }

//        System.out.println("DEBUG - log size: " + sLog.size());
        System.out.println("INFO - log parsed at " + percentage*100 + "%");
//        System.out.println("DEBUG - to parse: " + maxTraces);
//        System.out.println("DEBUG - parsed: " + parsed);
//        System.out.println("DEBUG - min frequency: " + leastFrequent);

        sLog.setSize(parsed);
        return sLog;
    }


    private class TracesComparator implements Comparator<String> {
        Map<String, Integer> base;

        public TracesComparator(Map<String, Integer> base) {
            this.base = base;
        }

        @Override
        public int compare(String a, String b) {
            if (base.get(a) >= base.get(b)) return -1;
            else return 1;
            // returning 0 would merge keys
        }
    }

}