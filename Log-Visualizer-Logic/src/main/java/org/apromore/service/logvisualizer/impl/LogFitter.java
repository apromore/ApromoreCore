package org.apromore.service.logvisualizer.impl;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.impl.bag.mutable.primitive.IntHashBag;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.set.mutable.primitive.IntHashSet;

import java.util.*;

public class LogFitter {

    private XFactory factory;

    private HashBiMap<String, Integer> simplified_names;

    public LogFitter(HashBiMap<String, Integer> simplified_names, XFactory factory) {
        this.simplified_names = simplified_names;
        this.factory = factory;
    }

    private boolean[] runAStar(IntList filtered_trace, Set<Arc> maintained_arcs) {
        Queue<State> queue = new PriorityQueue<>();
        IntHashSet retained_activities = new IntHashSet();
        retained_activities.addAll(filtered_trace);
        State start = new State(maintained_arcs, new boolean[filtered_trace.size()], filtered_trace, 0);
        queue.offer(start);

        while (!queue.isEmpty()) {
            State state = queue.poll();
            Set<State> next_states = generateNextState(maintained_arcs, filtered_trace, state);
            if (next_states.isEmpty()) {
                if(isValidTrace(state)) {
                    return state.getRetainedEvents();
                }
                return null;
            }
            for (State s : next_states) {
                queue.offer(s);
            }
        }
        return null;
    }

    private boolean isValidTrace(State state) {
        return state.getLastArc().getTarget() == 2;
    }

    public XTrace fitTrace(XTrace original_trace, IntList filtered_trace, HashSet<Arc> maintained_arcs) {
        boolean[] retained_events = runAStar(filtered_trace, maintained_arcs);
        if (retained_events == null) return null;

        XTrace filtered_xtrace = factory.createTrace(original_trace.getAttributes());

        for(int event = 1; event < retained_events.length - 1; event++) {
            if(retained_events[event]) {
                filtered_xtrace.add(original_trace.get(event - 1));
            }
        }
        if(filtered_xtrace.size() > 0) {
            return  filtered_xtrace;
        }
        return null;
    }

    private Set<State> generateNextState(Set<Arc> maintained_arcs, IntList filtered_trace, State state) {
        Set<State> states = new UnifiedSet<>();
        int unfiltered_event = state.getCurrent() + 1;
        if (unfiltered_event == filtered_trace.size()) return states;

        Arc lastArc = state.getLastArc();
        if(lastArc != null) {
            Arc arc = new Arc(lastArc.getTarget(), filtered_trace.get(unfiltered_event));
            if(maintained_arcs.contains(arc)) {
                State state1 = state.clone();
                state1.setCurrent(unfiltered_event, true);
                states.add(state1);
            }
            State state2 = state.clone();
            state2.setCurrent(unfiltered_event, false);
            states.add(state2);
        }else {
            Arc arc = new Arc(1, filtered_trace.get(unfiltered_event));
            if(maintained_arcs.contains(arc)) {
                State state1 = state.clone();
                state1.setCurrent(unfiltered_event, true);
                states.add(state1);
            }
            State state2 = state.clone();
            state2.setCurrent(unfiltered_event, false);
            states.add(state2);
        }
        return states;
    }

    private String getEventFullName(int event) {
        return simplified_names.inverse().get(event);
    }

    private class State implements Comparable<State>{

        private Set<Arc> maintained_arcs;
        private IntList trace;
        private boolean[] retained_events;
        private int current;
        private int cost;
        private Arc lastArc;

        public State(Set<Arc> maintained_arcs, boolean[] retained_events, IntList trace, int current) {
            this.maintained_arcs = maintained_arcs;
            this.trace = trace;
            this.retained_events = retained_events;
            this.current = current;
            computeCost();
        }

        public void setCurrent(int current, boolean retained) {
            this.retained_events[current] = retained;
            this.current = current;
            computeCost();
        }

        public int getCurrent() {
            return current;
        }

        public Arc getLastArc() {
            return lastArc;
        }

        public int computeCost() {
            return cost = g() + h();
        }

        private int g() {
            int cost = 0;
            int source;
            int target = 1;
            for(int i = 0; i <= current; i++) {
                if(!retained_events[i]) cost++;
                else {
                    source = target;
                    target = trace.get(i);
                    lastArc = new Arc(source, target);
                }
            }
            return cost;
        }

        private int h() {
            int cost = 0;
            for(int i = current; i < retained_events.length - 1; i++) {
                if(!maintained_arcs.contains(new Arc(trace.get(i), trace.get(i + 1)))) cost++;
            }
            return cost;
        }

        public State clone() {
            boolean[] clone_retained_events = Arrays.copyOf(retained_events, retained_events.length);
            return new State(maintained_arcs, clone_retained_events, trace, current);
        }

        @Override
        public int compareTo(State o) {
            return Integer.compare(cost, o.cost);
        }

        public boolean[] getRetainedEvents() {
            return retained_events;
        }
    }

}
