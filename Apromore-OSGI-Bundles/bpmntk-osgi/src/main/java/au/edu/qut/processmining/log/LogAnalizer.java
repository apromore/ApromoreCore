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

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Adriano on 24/10/2016.
 */
public class LogAnalizer {

    private XLog log;

    private HashSet<String> startEvents;
    private HashSet<String> endEvents;

    private HashMap<String, HashSet<String>> eventuallyFollow;

    private HashMap<String, Integer> minTime;
    private HashMap<String, Integer> maxTime;


    public LogAnalizer() { this.log = null; }
    public LogAnalizer(XLog log) {
        this.log = log;
    }

    public void setLog(XLog log) { this.log = log; }
    public XLog getLog() { return log; }

    public void runAnalysis() {
        HashSet<String> executed;

        startEvents = new HashSet<>();
        endEvents = new HashSet<>();

        eventuallyFollow = new HashMap<>();

        minTime = new HashMap<>();
        maxTime = new HashMap<>();

        int tIndex; //index to iterate on the log traces
        int eIndex; //index to iterate on the events of the trace

        XTrace trace;
        XEvent event;

        String eventLabel;

        int totalTraces = log.size();
        int traceSize;
//        System.out.println("DEBUG - total traces: " + totalTraces);

        for( tIndex = 0; tIndex < totalTraces; tIndex++ ) {
            trace = log.get(tIndex);
            traceSize = trace.size();
            //  System.out.println("DEBUG - analyzing trace: " + tIndex);

            executed = new HashSet<>();

            for (eIndex = 0; eIndex < traceSize; eIndex++) {
                event = trace.get(eIndex);
                eventLabel = event.getAttributes().get("concept:name").toString();

                if( eIndex == 0 ) startEvents.add(eventLabel);
                if( eIndex == (traceSize-1) ) endEvents.add(eventLabel);

                /* TIME ANALYSIS */
                if (!minTime.containsKey(eventLabel)) {
                    minTime.put(eventLabel, eIndex);
                    maxTime.put(eventLabel, eIndex);
                } else {
                    if (minTime.get(eventLabel) > (eIndex)) minTime.put(eventLabel, eIndex);
                    if (maxTime.get(eventLabel) < (eIndex)) maxTime.put(eventLabel, eIndex);
                }

                /* EVENTUALLY FOLLOW ANALYSIS */
                for( String e : executed ) {
                    if( !eventuallyFollow.containsKey(e) ) eventuallyFollow.put(e, new HashSet<String>());
                    eventuallyFollow.get(e).add(eventLabel);
                }
                if( !executed.contains(eventLabel) ) executed.add(eventLabel);

                /* DIRECTLY FOLLOW ANALYSIS */
            }
        }
    }

    public HashMap<String, HashSet<String>> getEventuallyFollowGraph() { return eventuallyFollow; }

    public void printTimeAnalysis() {
        minTime = new HashMap<>();
        maxTime = new HashMap<>();

        for( String event : minTime.keySet() )
            System.out.println("DEBUG - event time [min|max]: " + event + " [" + minTime.get(event) + " | " + maxTime.get(event) + "]" );
    }

    public int getMinTime(String name){ return minTime.get(name); }
    public int getMaxTime(String name){ return maxTime.get(name); }

    public void printEventuallyFollowRelations() {
        System.out.println("DEBUG - eventually follow relationships:");
        for( String srcEvent : eventuallyFollow.keySet() ) {
            System.out.print("DEBUG - eventually follow dependency holds in between: " + srcEvent + " > ");
            for( String tgtEvent : eventuallyFollow.get(srcEvent) ) System.out.print(tgtEvent + ", ");
            System.out.println();
        }
    }

    public boolean isEventuallyFollow(String node, String follower) {
        return (eventuallyFollow.containsKey(node) && eventuallyFollow.get(node).contains(follower));
    }

    public boolean isStart(String name) { return startEvents.contains(name); }
    public boolean isEnd(String name) { return endEvents.contains(name); }

}
