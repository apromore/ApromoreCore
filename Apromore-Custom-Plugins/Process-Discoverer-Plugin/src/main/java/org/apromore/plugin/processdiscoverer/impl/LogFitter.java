/*
 * Copyright © 2019 The University of Melbourne.
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

package org.apromore.plugin.processdiscoverer.impl;

import com.raffaeleconforti.foreignkeydiscovery.Pair;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.map.mutable.primitive.IntIntHashMap;
import org.eclipse.collections.impl.multimap.set.UnifiedSetMultimap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.set.mutable.primitive.IntHashSet;

import java.util.*;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 */
public class LogFitter {

    private final XFactory factory;

    private final Map<IntList, State> reduced = new UnifiedMap<>();

    private Map<Pair<Arc, Pair<Integer, IntIntHashMap>>, List<IntArrayList>> ordered_labels_map;

    private Map<Pair<Arc, Pair<Integer, IntIntHashMap>>, Set<List<Arc>>> forward_paths;
    private UnifiedSetMultimap<Set<List<Arc>>, SolutionPointer> forward_paths_states;
    private Map<SolutionPointer, Set<List<Arc>>> forward_states_paths;
    private Map<SolutionPointer, Pair<Arc, Pair<Integer, IntIntHashMap>>> forward_states_paths_key;

    private Map<Pair<Arc, Pair<Integer, IntIntHashMap>>, Set<List<Arc>>> backward_paths;
    private UnifiedSetMultimap<Set<List<Arc>>, SolutionPointer> backward_paths_states;
    private Map<SolutionPointer, Set<List<Arc>>> backward_states_paths;
    private Map<SolutionPointer, Pair<Arc, Pair<Integer, IntIntHashMap>>> backward_states_paths_key;

    int last_cost = -1;
    int length = 0;
    int worse = Integer.MAX_VALUE;

    IntIntHashMap shortestDistance;

    public LogFitter(XFactory factory) {
        this.factory = factory;
    }

    private IntList extendIntList(IntList filtered_trace) {
        IntArrayList extended_filtered_trace = new IntArrayList();
        extended_filtered_trace.addAll(filtered_trace);
        return extended_filtered_trace;
    }

    private boolean[] reduceRetainedEvents(boolean[] retainedEvents) {
        return retainedEvents;
    }

    private State runAStar(IntList filtered_trace, Set<Arc> maintained_arcs, boolean log_only, int worse, SearchStrategy searchStrategy) {
        ordered_labels_map = new UnifiedMap<>();

        forward_paths = new UnifiedMap<>();
        forward_paths_states = UnifiedSetMultimap.newMultimap();
        forward_states_paths = new UnifiedMap<>();
        forward_states_paths_key = new UnifiedMap<>();

        backward_paths = new UnifiedMap<>();
        backward_paths_states = UnifiedSetMultimap.newMultimap();
        backward_states_paths = new UnifiedMap<>();
        backward_states_paths_key = new UnifiedMap<>();

        IntList extended_filtered_trace = extendIntList(filtered_trace);

        UnifiedSetMultimap<Integer, State> forward = new UnifiedSetMultimap<>();
        UnifiedSetMultimap<Integer, State> backward = new UnifiedSetMultimap<>();
        UnifiedSet<SolutionPointer> pointers = new UnifiedSet<>();

        last_cost = -1;
        State result = reduced.get(extended_filtered_trace);
        if (result == null) {
            Queue<State> queue = new PriorityQueue<>();
            boolean[] forward_retained_events = new boolean[extended_filtered_trace.size()];
            forward_retained_events[0] = true;
            boolean[] backward_retained_events = new boolean[extended_filtered_trace.size()];
            backward_retained_events[backward_retained_events.length - 1] = true;

            length = worse;
            if(extended_filtered_trace.size() > 2) {
                IntArrayList trace_contained_labels = new IntArrayList();
                for (int i = 0; i < forward_retained_events.length; i++) {
                    if (trace_contained_labels.isEmpty() ||
                            trace_contained_labels.getLast() != extended_filtered_trace.get(i) ||
                            !maintained_arcs.contains(new Arc(extended_filtered_trace.get(i), extended_filtered_trace.get(i)))) {
                        trace_contained_labels.add(extended_filtered_trace.get(i));
                    }
                }
                length += trace_contained_labels.size() + 1;
            }

            if(searchStrategy == SearchStrategy.FORWARD || searchStrategy == SearchStrategy.FORWARDANDBACKWARD)
                queue.offer(new ForwardState(null, maintained_arcs, new ArrayList<>(), new ArrayList<>(), forward_retained_events, extended_filtered_trace, 0, true));
            if(searchStrategy == SearchStrategy.BACKWARD || searchStrategy == SearchStrategy.FORWARDANDBACKWARD)
                queue.offer(new BackwardState(null, maintained_arcs, new ArrayList<>(), new ArrayList<>(), backward_retained_events, extended_filtered_trace, extended_filtered_trace.size() - 1, true));

            while (!queue.isEmpty()) {
                State state = queue.poll();
                if (last_cost != state.cost) {
                    removePointers(pointers, state.cost);
                    last_cost = state.cost;
                }
                if (state.current >= 0 && state.current < state.retained_events.length - 1) {
                    State merged = null;
                    if (state instanceof ForwardState) {
                        if (state.getRetainedEvents()[state.current]) forward.put(state.current, state);
                        State best = findBest(backward.get(state.current), state);
                        if (best != null) merged = mergeStates(state, best);
                    } else if (state instanceof BackwardState) {
                        if (state.getRetainedEvents()[state.current]) backward.put(state.current, state);
                        State best = findBest(forward.get(state.current), state);
                        if (best != null) merged = mergeStates(best, state);
                    }
                    if (merged != null && isValidTrace(merged)) {
                        reduced.put(extended_filtered_trace, merged);
                        return merged;
                    }
                }
                Set<State> next_states = generateNextState(maintained_arcs, extended_filtered_trace, state, log_only);
                if (next_states.isEmpty()) {
                    if (isValidTrace(state)) {
                        reduced.put(extended_filtered_trace, state);
                        return state;
                    }
                }
                for (State s : next_states) {
                    SolutionPointer sp = new SolutionPointer(s.g, s.current, s.getRetainedEvents(), s instanceof ForwardState, s.visited_arcs);
                    if (!pointers.contains(sp)) {
                        pointers.add(sp);
                        if (s.cost < length) queue.offer(s);
                    }
                }
            }
            return null;
        } else return result;
    }

    private void removePointers(UnifiedSet<SolutionPointer> pointers, int cost) {
        for (SolutionPointer p : pointers.toList()) {
            if (p.cost < cost) {
                pointers.remove(p);
                if(p.forward) {
                    Set<List<Arc>> path = forward_states_paths.remove(p);
                    Pair<Arc, Pair<Integer, IntIntHashMap>> key = forward_states_paths_key.remove(p);
                    forward_paths_states.remove(path, p);
                    if (!forward_paths_states.containsKey(path)) {
                        forward_paths.remove(key);
                    }
                }else {
                    Set<List<Arc>> path = backward_states_paths.remove(p);
                    Pair<Arc, Pair<Integer, IntIntHashMap>> key = backward_states_paths_key.remove(p);
                    backward_paths_states.remove(path, p);
                    if (!backward_paths_states.containsKey(path)) {
                        backward_paths.remove(key);
                    }
                }
            }
        }
    }

    private State mergeStates(State state, State best) {
        boolean[] merged = merge(state.getRetainedEvents(), best.retained_events, state.current);
        List<Arc> merged_visited_arcs = new ArrayList<>(state.visited_arcs);
        merged_visited_arcs.addAll(best.visited_arcs);
        List<Boolean> merged_matching_visited_arcs = new ArrayList<>(state.matching_visited_arcs);
        merged_matching_visited_arcs.addAll(best.matching_visited_arcs);
        return new ForwardState(null, state.maintained_arcs, merged_visited_arcs, merged_matching_visited_arcs, merged, state.trace, state.trace.size() - 1, false);
    }

    private State findBest(Set<State> candidates, State state) {
        Queue<State> list = new PriorityQueue<>(candidates);
        State best = null;
        boolean forward = state instanceof ForwardState;
        while (!list.isEmpty()) {
            State p = list.poll();
            if (best != null && best.cost < p.cost) return best;
            if (p.retained_events[state.current] == state.getRetainedEvents()[state.current] &&
                    (state.visited_arcs.size() > 0 && p.visited_arcs.size() > 0 &&
                            ((forward && state.lastArc.getTarget() == p.visited_arcs.get(0).getSource()) ||
                                    (!forward && state.lastArc.getSource() == p.visited_arcs.get(p.visited_arcs.size() - 1).getTarget())))) {
                if (best == null || best.cost > p.cost) best = p;
            }
        }
        return best;
    }

    private boolean[] merge(boolean[] beginning, boolean[] end, int current) {
        boolean[] merged = new boolean[beginning.length];
        for (int i = 0; i < merged.length; i++) {
            if (i < current) merged[i] = beginning[i];
            else merged[i] = end[i];
        }
        return merged;
    }

    private boolean isValidTrace(State state) {
        if (state instanceof ForwardState)
            return state.current == state.retained_events.length - 1 && state.getLastArc() != null && state.getLastArc().getTarget() == 2;
        else return state.current == 0 && state.getLastArc() != null && state.getLastArc().getSource() == 1;
    }

    public double measureFitness(List<IntList> log, Set<Arc> maintained_arcs, SearchStrategy searchStrategy) {
        List<IntArrayList> reduced_log = new ArrayList<>();
        worse = measureFitness(new IntArrayList(1, 2), maintained_arcs, false, Integer.MAX_VALUE, searchStrategy);

        length = 0;
        IntHashSet activities = new IntHashSet();
        for(IntList trace : log) {
            IntArrayList trace_contained_labels = new IntArrayList();
            for (int i = 0; i < trace.size(); i++) {
                if (trace_contained_labels.isEmpty() ||
                        trace_contained_labels.getLast() != trace.get(i) ||
                        !maintained_arcs.contains(new Arc(trace.get(i), trace.get(i)))
                        ) {
                    trace_contained_labels.add(trace.get(i));
                }
                if(trace.get(i) != 1 && trace.get(i) != 2) activities.add(trace.get(i));
            }
            if(trace_contained_labels.size() > length) length = trace_contained_labels.size();
            reduced_log.add(trace_contained_labels);
        }
        length += worse;

        List<IntIntHashMap> visitable_total = new ArrayList<>();
        List<IntIntHashMap> visitable_max = new ArrayList<>();
        List<Integer> pos = new ArrayList<>();
        for(IntArrayList trace : reduced_log) {
            pos.add(pos.size());
            IntIntHashMap map = new IntIntHashMap();
            IntIntHashMap map1 = new IntIntHashMap();
            Pair cycle = new Pair(1, 1);
            for (int i : trace.toArray()) {
                if((Integer) cycle.getFirstElement() == i) {
                    cycle = new Pair(cycle.getFirstElement(), (Integer) cycle.getSecondElement() + 1);
                }else {
                    cycle = new Pair(i, 1);
                }
                if(map1.get(i) < (Integer) cycle.getSecondElement()) map1.put(i, (Integer) cycle.getSecondElement());
                if(map1.get(i) == 0) map1.addToValue(i, 1);
                map.addToValue(i, 1);
            }
            for(int key : activities.toArray()) {
                map.addToValue(key, worse);
            }
            map.addToValue(-1, trace.size() + worse);
            visitable_total.add(map);
            visitable_max.add(map1);
        }

        pos.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return -Integer.compare(log.get(o1).size(), log.get(o2).size());
            }
        });

        shortestPath(maintained_arcs);
        UnifiedSetMultimap<Integer, Arc> retained_arcs = new UnifiedSetMultimap<>();
        for(Arc arc : maintained_arcs) {
            retained_arcs.put(arc.getSource(), arc);
        }

        Set<List<Arc>> arcs = discoverPathsForward(retained_arcs, new IntHashSet(), visitable_total, new Pair(1, 1), visitable_max, 1, 2, length);
        List<IntArrayList> paths = convertPathToListLabels(arcs, true);
        paths.sort(new Comparator<IntArrayList>() {
            @Override
            public int compare(IntArrayList o1, IntArrayList o2) {
                int compare = -Integer.compare(o1.size(), o2.size());
                if(compare == 0) return Arrays.toString(o1.toArray()).compareTo(Arrays.toString(o2.toArray()));
                return compare;
            }
        });
        DoubleArrayList raw_fitness = new DoubleArrayList();
        DoubleArrayList worse_fitness = new DoubleArrayList();
        for (Integer po : pos) {
            IntList trace = log.get(po);
            last_cost = -1;
            boolean[] forward_retained_events = new boolean[trace.size()];
            forward_retained_events[0] = true;
            int max = worse + trace.size() - 2;
            int cost = new ForwardState(null, maintained_arcs, new ArrayList<>(), new ArrayList<>(), forward_retained_events, trace, 0, false).computeCost(paths);

            if (cost > max) cost = max;
            raw_fitness.add(cost);
            worse_fitness.add(max);
        }

        double fitness = raw_fitness.sum() / raw_fitness.size();
        double max = worse_fitness.sum() / worse_fitness.size();

        return 1 - (fitness / max);
    }

    public int measureFitness(IntList filtered_trace, Set<Arc> maintained_arcs, boolean log_only, int worse, SearchStrategy searchStrategy) {
        State solution = runAStar(filtered_trace, maintained_arcs, log_only, worse, searchStrategy);
        if (solution == null) return Integer.MAX_VALUE;
        else return solution.cost;
    }

    public XTrace fitTrace(XTrace original_trace, IntList filtered_trace, Set<Arc> maintained_arcs, boolean log_only, SearchStrategy searchStrategy) {
        boolean[] retained_events = runAStar(filtered_trace, maintained_arcs, log_only, filtered_trace.size(), searchStrategy).retained_events;
        if (retained_events == null) return null;

        XTrace filtered_xtrace = factory.createTrace(original_trace.getAttributes());

        for (int event = 1; event < retained_events.length - 1; event++) {
            if (retained_events[event]) {
                filtered_xtrace.add(original_trace.get(event - 1));
            }
        }
        if (filtered_xtrace.size() > 0) {
            return filtered_xtrace;
        }
        return null;
    }

    private Set<State> generateNextState(Set<Arc> maintained_arcs, IntList filtered_trace, State state, boolean log_only) {
        Set<State> states = new UnifiedSet<>();
        int unfiltered_event = state.getNext();
        if ((unfiltered_event == -1) || (unfiltered_event == filtered_trace.size())) {
            return states;
        }

        Arc lastArc = state.getLastArc();
        if (lastArc == null) {
            lastArc = new Arc(2, 1);
        }

        Arc arc;
        if (log_only) {
            if (state instanceof ForwardState) arc = new Arc(lastArc.getTarget(), filtered_trace.get(unfiltered_event));
            else arc = new Arc(filtered_trace.get(unfiltered_event), lastArc.getSource());
            if (maintained_arcs.contains(arc)) {
                State state1 = state.clone();
                if (state1.setNext(arc)) {
                    state1.computeCost();
                    states.add(state1);
                }
            }
        } else {
            boolean repeat = (state.trace.get(state.current) == state.trace.get(state.getNext()));
            Arc possible = null;
            if (repeat && state.retained_events[state.current]) {
                for (Arc arc2 : maintained_arcs) {
                    if (arc2.getSource() == state.trace.get(state.current) && arc2.getSource() == arc2.getTarget()) {
                        possible = arc2;
                        break;
                    }
                }
            }
            if (possible != null) {
                State state1 = state.clone();
                state1.setNext(possible);
                state1.computeCost();
                states.add(state1);
            } else if (!repeat) {
                for (Arc arc1 : maintained_arcs) {
                    arc = null;
                    if (arc1.getSource() != arc1.getTarget()) {
                        if ((state instanceof ForwardState && arc1.getSource() == lastArc.getTarget()) ||
                                (state instanceof BackwardState && arc1.getTarget() == lastArc.getSource())) {
                            arc = arc1;
                        }
                    }
                    if (arc != null) {
                        boolean visited = false;
                        for (int i = state.visited_arcs.size() - 1; i >= 0 && !state.matching_visited_arcs.get(i); i--) {
                            if (state.visited_arcs.get(i).getTarget() == arc.getTarget() ||
                                    state.visited_arcs.get(i).getSource() == arc.getSource()) {
                                visited = true;
                                break;
                            }
                        }

                        State state1 = state.clone();
                        boolean match = state1.setNext(arc);
                        if (match || !visited) {
                            state1.computeCost();
                            states.add(state1);
                        }
                    }
                }
            }
        }

        State state2 = state.clone();
        state2.setNext(null);
        state2.computeCost();
        states.add(state2);
        return states;
    }

    private class ForwardState extends State {

        ForwardState(State parent, Set<Arc> maintained_arcs, List<Arc> visited_arcs, List<Boolean> matching_visited_arcs, boolean[] retained_events, IntList trace, int current, boolean check_cost) {
            super(parent, maintained_arcs, visited_arcs, matching_visited_arcs, retained_events, trace, current, check_cost);
        }

        @Override
        public int getNext() {
            return current + 1;
        }

        @Override
        public boolean setNext(Arc arc) {
            boolean ok = false;
            if (arc == null) {
                this.current++;
                this.retained_events[current] = false;
            } else {
                visited_arcs.add(arc);
                if (trace.get(current + 1) == arc.getTarget()) {
                    this.current++;
                    this.retained_events[current] = true;
                    ok = true;
                }
                matching_visited_arcs.add(ok);
            }
            return ok;
        }

        @Override
        protected int g() {
            event_cost = 0;
            arc_cost = 0;

            for (int i = 0; i <= current; i++) {
                if (!retained_events[i]) event_cost++;
            }

            for (Boolean matching_visited_arc : matching_visited_arcs) {
                if (!matching_visited_arc) {
                    arc_cost++;
                }
            }

            if (visited_arcs.size() > 0) lastArc = visited_arcs.get(visited_arcs.size() - 1);
            g = event_cost + arc_cost;
            return g;
        }

        @Override
        protected int h(boolean print) {
            h = 0;
            if (lastArc == null || (lastArc != null && lastArc.getTarget() != 2)) {
                IntArrayList trace_contained_labels = new IntArrayList();
                for (int i = current + 1; i < retained_events.length; i++) {
                    if(trace_contained_labels.isEmpty() &&
                            lastArc != null && matching_visited_arcs.get(matching_visited_arcs.size() - 1) &&
                            lastArc.getTarget() == trace.get(i) && retained_events[current] &&
                            maintained_arcs.contains(new Arc(trace.get(i), trace.get(i)))) {

                    }else if(trace_contained_labels.isEmpty() ||
                            trace_contained_labels.getLast() != trace.get(i) ||
                            !maintained_arcs.contains(new Arc(trace.get(i), trace.get(i)))) {
                        trace_contained_labels.add(trace.get(i));
                    }
                }

                IntArrayList pre_trace = new IntArrayList();
                for (int i = 1; i < current + 1; i++) {
                    if(retained_events[current] &&
                            (pre_trace.isEmpty() ||
                                    pre_trace.getLast() != trace.get(i) ||
                                    !maintained_arcs.contains(new Arc(trace.get(i), trace.get(i))))) {
                        pre_trace.add(trace.get(i));
                    }
                }

                IntIntHashMap visitable = new IntIntHashMap();
                for (int i : pre_trace.toArray()) {
                    if(visitable.get(i) == 0) visitable.addToValue(i, 1);
                    visitable.addToValue(i, 1);
                }
                for (int i : trace_contained_labels.toArray()) {
                    if(visitable.get(i) == 0) visitable.addToValue(i, 1);
                    visitable.addToValue(i, 1);
                }
                for(int i : visitable.keySet().toArray()) {
                    visitable.addToValue(i, -1);
                }

                int target = 2;
                int source = (lastArc != null) ? lastArc.getTarget() : 1;
                Pair<Arc, Pair<Integer, IntIntHashMap>> pair = new Pair(new Arc(source, target), new Pair(length, visitable));
                List<IntArrayList> ordered_labels = ordered_labels_map.get(pair);
                if(ordered_labels == null) {
                    Set<List<Arc>> paths = discoverPathsForward(maintained_arcs, new IntHashSet(), visitable, source, target, length);
                    ordered_labels_map.put(pair, ordered_labels);

                    SolutionPointer sp = new SolutionPointer(g, current, retained_events, true, visited_arcs);
                    forward_paths_states.put(paths, sp);
                    forward_states_paths.put(sp, paths);
                    forward_states_paths_key.put(sp, pair);
                    ordered_labels = convertPathToListLabels(paths, true);
                }
                h = Integer.MAX_VALUE - g;

                Iterator<IntArrayList> iterator = ordered_labels.iterator();
                while (iterator.hasNext()) {
                    IntArrayList list = iterator.next();

                    if(Math.abs(list.size() - trace_contained_labels.size()) > h) {
                        continue;
                    }

                    IntArrayList list_matching_labels1 = new IntArrayList(list.toArray());

                    IntArrayList move_model = new IntArrayList(list_matching_labels1.toArray());
                    move_model.removeAll(trace_contained_labels);

                    IntArrayList move_log = new IntArrayList(trace_contained_labels.toArray());
                    move_log.removeAll(list_matching_labels1);

                    if (move_log.size() + move_model.size() > h) {
                        iterator.remove();
                        continue;
                    }

                    list_matching_labels1.removeAll(move_model);

                    IntArrayList trace_contained_labels1 = new IntArrayList(trace_contained_labels.toArray());
                    trace_contained_labels1.removeAll(move_log);

                    int guess_move  = measureDistance(list_matching_labels1, trace_contained_labels1);
                    h = Math.min(h, (move_log.size() + move_model.size() + guess_move));

                }
            }else {
                int move_log = 0;
                for (int i = current + 1; i < retained_events.length; i++) {
                    if (2 != trace.get(i)) move_log++;
                }
                h = move_log;
            }
            if(print || last_cost > g + h) {
                if(last_cost > g + h) {
                    if (parent != null) {
                        parent.h(true);
                    } //else System.out.println("PARENT NULL");
                }
            }
            return h;
        }

        @Override
        protected int h(List<IntArrayList> ordered_labels) {
            h = worse + trace.size() - 2;

            IntArrayList trace_contained_labels1 = new IntArrayList(trace.toArray());
            trace_contained_labels1.removeAtIndex(0);
            IntHashSet trace_contained_labels1_set = new IntHashSet(trace_contained_labels1.toArray());

            for (IntArrayList list : ordered_labels) {
                if (Math.abs(list.size() - trace_contained_labels1.size()) > h) continue;

                IntArrayList list_contained_labels1 = new IntArrayList(list.toArray());
                IntHashSet list_contained_labels1_set = new IntHashSet(list_contained_labels1.toArray());

                int move_log = 0;
                for (int i : trace_contained_labels1.toArray()) {
                    if (!list_contained_labels1_set.contains(i)) move_log++;
                }
                if (move_log > h) continue;

                int move_model = 0;
                for (int i : list_contained_labels1.toArray()) {
                    if (!trace_contained_labels1_set.contains(i)) move_model++;
                }

                if (move_log + move_model > h) continue;

                int distance = measureDistance(list_contained_labels1, trace_contained_labels1);
                h = Math.min(h, distance);
            }
            return h;
        }

        @Override
        protected int r() {
            return retained_events.length - (current + 1);
        }

        @Override
        public State clone() {
            boolean[] clone_retained_events = Arrays.copyOf(retained_events, retained_events.length);
            List<Arc> clone_visited_arcs = new ArrayList<>(visited_arcs);
            List<Boolean> clone_matching_visited_arcs = new ArrayList<>(matching_visited_arcs);
            return new ForwardState(this, maintained_arcs, clone_visited_arcs, clone_matching_visited_arcs, clone_retained_events, trace, current, false);
        }

    }

    private int measureDistance(IntArrayList list_matching_labels1, IntArrayList trace_contained_labels1) {
        return editDistance(list_matching_labels1, trace_contained_labels1);
    }

    private class BackwardState extends State {

        BackwardState(State parent, Set<Arc> maintained_arcs, List<Arc> visited_arcs, List<Boolean> matching_visited_arcs, boolean[] retained_events, IntList trace, int current, boolean check_cost) {
            super(parent, maintained_arcs, visited_arcs, matching_visited_arcs, retained_events, trace, current, check_cost);
        }

        @Override
        public int getNext() {
            return current - 1;
        }

        @Override
        public boolean setNext(Arc arc) {
            boolean ok = false;
            if (arc == null) {
                this.current--;
                this.retained_events[current] = false;
            } else {
                visited_arcs.add(0, arc);
                if (trace.get(current - 1) == arc.getSource()) {
                    this.current--;
                    this.retained_events[current] = true;
                    ok = true;
                }
                matching_visited_arcs.add(0, ok);
            }
            return ok;
        }

        @Override
        protected int g() {
            event_cost = 0;
            arc_cost = 0;

            for (int i = retained_events.length - 1; i >= current; i--) {
                if (!retained_events[i]) event_cost++;
            }

            for (Boolean matching_visited_arc : matching_visited_arcs) {
                if (!matching_visited_arc) {
                    arc_cost++;
                }
            }

            if (visited_arcs.size() > 0) lastArc = visited_arcs.get(0);
            g = event_cost + arc_cost;
            return g;
        }

        @Override
        protected int h(boolean print) {
            h = 0;
            if (lastArc == null || (lastArc != null && lastArc.getSource() != 1)) {
                IntArrayList trace_contained_labels = new IntArrayList();
                for (int i = current - 1; i >= 0; i--) {
                    if(trace_contained_labels.isEmpty() &&
                            lastArc != null && matching_visited_arcs.get(0) &&
                            lastArc.getSource() == trace.get(i) && retained_events[current] &&
                            maintained_arcs.contains(new Arc(trace.get(i), trace.get(i)))) {

                    }else if(trace_contained_labels.isEmpty() ||
                            trace_contained_labels.getFirst() != trace.get(i) ||
                            !maintained_arcs.contains(new Arc(trace.get(i), trace.get(i)))) {
                        trace_contained_labels.addAtIndex(0, trace.get(i));
                    }
                }

                IntArrayList pre_trace = new IntArrayList();
                for (int i = retained_events.length - 1; i > current - 1; i--) {
                    if(retained_events[current] &&
                            (pre_trace.isEmpty() ||
                                    pre_trace.getFirst() != trace.get(i) ||
                                    !maintained_arcs.contains(new Arc(trace.get(i), trace.get(i))))) {
                        pre_trace.addAtIndex(0, trace.get(i));
                    }
                }

                IntIntHashMap visitable = new IntIntHashMap();
                for (int i : pre_trace.toArray()) {
                    if(visitable.get(i) == 0) visitable.addToValue(i, 1);
                    visitable.addToValue(i, 1);
                }
                for (int i : trace_contained_labels.toArray()) {
                    if(visitable.get(i) == 0) visitable.addToValue(i, 1);
                    visitable.addToValue(i, 1);
                }
                for(int i : visitable.keySet().toArray()) {
                    visitable.addToValue(i, -1);
                }

                int source = 1;
                int target = (lastArc != null) ? lastArc.getSource() : 2;
                Pair<Arc, Pair<Integer, IntIntHashMap>> pair = new Pair(new Arc(source, target), new Pair(length, visitable));
                List<IntArrayList> ordered_labels = ordered_labels_map.get(pair);
                if(ordered_labels == null) {
                    Set<List<Arc>> paths = discoverPathsBackward(maintained_arcs, new IntHashSet(), visitable, source, target, length);
                    ordered_labels_map.put(pair, ordered_labels);

                    SolutionPointer sp = new SolutionPointer(g, current, retained_events, false, visited_arcs);
                    backward_paths_states.put(paths, sp);
                    backward_states_paths.put(sp, paths);
                    backward_states_paths_key.put(sp, pair);
                    ordered_labels = convertPathToListLabels(paths, true);
                }
                h = Integer.MAX_VALUE - g;

                Iterator<IntArrayList> iterator = ordered_labels.iterator();
                while (iterator.hasNext()) {
                    IntArrayList list = iterator.next();

                    if(Math.abs(list.size() - trace_contained_labels.size()) > h) {
                        iterator.remove();
                        continue;
                    }

                    IntArrayList list_matching_labels1 = new IntArrayList(list.toArray());

                    IntArrayList move_model = new IntArrayList(list_matching_labels1.toArray());
                    move_model.removeAll(trace_contained_labels);

                    IntArrayList move_log = new IntArrayList(trace_contained_labels.toArray());
                    move_log.removeAll(list_matching_labels1);

                    if (move_log.size() + move_model.size() > h) {
                        iterator.remove();
                        continue;
                    }

                    list_matching_labels1.removeAll(move_model);

                    IntArrayList trace_contained_labels1 = new IntArrayList(trace_contained_labels.toArray());
                    trace_contained_labels1.removeAll(move_log);

                    int guess_move  = measureDistance(list_matching_labels1, trace_contained_labels1);
                    h = Math.min(h, (move_log.size() + move_model.size() + guess_move));
                }
            }else {
                int move_log = 0;
                for (int i = current - 1; i >= 0; i--) {
                    if (1 != trace.get(i)) move_log++;
                }
                h = move_log;
            }
            return h;
        }

        @Override
        protected int h(List<IntArrayList> ordered_labels) {
            return 0;
        }

        @Override
        protected int r() {
            return current;
        }

        @Override
        public State clone() {
            boolean[] clone_retained_events = Arrays.copyOf(retained_events, retained_events.length);
            List<Arc> clone_visited_arcs = new ArrayList<>(visited_arcs);
            List<Boolean> clone_matching_visited_arcs = new ArrayList<>(matching_visited_arcs);
            return new BackwardState(this, maintained_arcs, clone_visited_arcs, clone_matching_visited_arcs, clone_retained_events, trace, current, false);
        }

    }

    private Set<List<Arc>> discoverPathsBackward(Set<Arc> maintained_arcs, IntHashSet visited, IntIntHashMap visitable, int source, int target, int length) {
        Pair<Arc, Pair<Integer, IntIntHashMap>> pair = new Pair(new Arc(source, target), new Pair(length, visitable));
        Set<List<Arc>> paths = backward_paths.get(pair);
        if(paths == null && length > 0) {
            paths = new UnifiedSet<>();
            for (Arc arc : maintained_arcs) {
                if (arc.getTarget() == target && !visited.contains(arc.getTarget())) {
                    if (arc.getSource() != source) {
                        IntHashSet next_visited = new IntHashSet(visited);
                        IntIntHashMap next_visitable = new IntIntHashMap(visitable);
                        if (next_visitable.get(arc.getTarget()) > 0) next_visitable.addToValue(arc.getTarget(), -1);
                        else next_visited.add(arc.getTarget());
                        for (List<Arc> path : discoverPathsBackward(maintained_arcs, next_visited, next_visitable, source, arc.getSource(), length - 1)) {
                            List<Arc> path1 = new ArrayList<>();
                            path1.addAll(path);
                            path1.add(arc);
                            paths.add(path1);
                        }
                    } else {
                        List<Arc> path = new ArrayList<>();
                        path.add(arc);
                        paths.add(path);
                    }
                }
            }
            backward_paths.put(pair, paths);
        }
        if(paths == null) paths = new UnifiedSet<>();
        return paths;
    }


    private Set<List<Arc>> discoverPathsForward(UnifiedSetMultimap<Integer, Arc> maintained_arcs, IntHashSet visited, List<IntIntHashMap> visitable_total, Pair current_cycle, List<IntIntHashMap> visitable_max, int source, int target, int length) {
        Set<List<Arc>> paths = new UnifiedSet<>();
        UnifiedSetMultimap<Integer, Arc> next_maintained_arcs = new UnifiedSetMultimap<>(maintained_arcs);
        if(visited.size() > 0) {
            for (Arc arc : maintained_arcs.valuesView()) {
                if (visited.contains(arc.getTarget()) || shortestDistance.get(arc.getTarget()) > length) {
                    next_maintained_arcs.remove(arc.getSource(), arc);
                }
            }
        }
        for (Arc arc : next_maintained_arcs.get(source)) {
            if (arc.getSource() == source && !visited.contains(arc.getTarget())) {
                if (arc.getTarget() != target && shortestDistance.get(arc.getTarget()) <= length) {
                    IntHashSet next_visited = new IntHashSet(visited);
                    List<IntIntHashMap> next_visitable_total = new ArrayList<>();
                    for (IntIntHashMap map : visitable_total) next_visitable_total.add(new IntIntHashMap(map));
                    Pair next_current_cycle;
                    if((Integer) current_cycle.getFirstElement() == arc.getTarget()) {
                        next_current_cycle = new Pair(current_cycle.getFirstElement(), (Integer) current_cycle.getSecondElement() + 1);
                    }else {
                        next_current_cycle = new Pair(arc.getTarget(), 1);
                    }

                    boolean stop = true;
                    boolean skip = false;
                    boolean skip_all = true;
                    boolean skip_all_check = true;
                    for(int i = 0; i < next_visitable_total.size(); i++) {
                        if(next_visitable_total.get(i).get(-1) >= length) {
                            if((Integer) next_current_cycle.getFirstElement() == arc.getTarget() &&
                                    (Integer) next_current_cycle.getSecondElement() > visitable_max.get(i).get(arc.getTarget())) {
                                skip = true;
                            } else {
                                skip_all = false;
                            }
                            if (!skip && next_visitable_total.get(i).get(arc.getTarget()) > 0) {
                                stop = false;
                            }
                            next_visitable_total.get(i).addToValue(arc.getTarget(), -1);
                        }else {
                            skip_all_check = false;
                        }
                        next_visitable_total.get(i).addToValue(-1, -1);
                    }
                    if (skip_all && skip_all_check)
                        continue;
                    if (stop) {
                        next_visited.add(arc.getTarget());
                    }
                    for (List<Arc> path : discoverPathsForward(next_maintained_arcs, next_visited, next_visitable_total, next_current_cycle, visitable_max, arc.getTarget(), target, length - 1)) {
                        List<Arc> path1 = new ArrayList<>();
                        path1.add(arc);
                        path1.addAll(path);
                        paths.add(path1);
                    }
                } else {
                    List<Arc> path = new ArrayList<>();
                    path.add(arc);
                    paths.add(path);
                }
            }
        }
        return paths;
    }

    private Set<List<Arc>> discoverPathsForward(Set<Arc> maintained_arcs, IntHashSet visited, IntIntHashMap visitable, int source, int target, int length) {
        Pair<Arc, Pair<Integer, IntIntHashMap>> pair = new Pair(new Arc(source, target), new Pair(length, visitable));
        Set<List<Arc>> paths = forward_paths.get(pair);
        if(paths == null && length > 0) {
            paths = new UnifiedSet<>();
            for (Arc arc : maintained_arcs) {
                if (arc.getSource() == source && arc.getSource() != arc.getTarget() && !visited.contains(arc.getSource())) {
                    if (arc.getTarget() != target) {
                        IntHashSet next_visited = new IntHashSet(visited);
                        IntIntHashMap next_visitable = new IntIntHashMap(visitable);
                        if(next_visitable.get(arc.getSource()) > 0) next_visitable.addToValue(arc.getSource(), -1);
                        else next_visited.add(arc.getSource());
                        for (List<Arc> path : discoverPathsForward(maintained_arcs, next_visited, next_visitable, arc.getTarget(), target, length - 1)) {
                            List<Arc> path1 = new ArrayList<>();
                            path1.add(arc);
                            path1.addAll(path);
                            paths.add(path1);
                        }
                    } else {
                        List<Arc> path = new ArrayList<>();
                        path.add(arc);
                        paths.add(path);
                    }
                }
            }
            forward_paths.put(pair, paths);
        }
        if(paths == null) paths = new UnifiedSet<>();
        return paths;
    }

    private boolean hasLoop(List<IntArrayList> paths, int activity) {
        for(IntArrayList list : paths) {
            int last = -1;
            for(int i : list.toArray()) {
                if(i != last) last = i;
                else if(i == activity) return true;
            }
        }
        return false;
    }

    private List<IntArrayList> convertPathToListLabels(Set<List<Arc>> paths, boolean forward) {
        List<IntArrayList> possible = new ArrayList<>(paths.size());
        for (List<Arc> path : paths) {
            IntArrayList set = new IntArrayList();
            int arc_pos = (forward) ? 0 : path.size() - 1;
            while (arc_pos > -1 && arc_pos < path.size()) {
                Arc arc = path.get(arc_pos);
                if (forward) {
                    set.add(arc.getTarget());
                    arc_pos++;
                } else {
                    set.addAtIndex(0, arc.getSource());
                    arc_pos--;
                }
            }
            possible.add(set);
        }
        return possible;
    }

    private abstract class State implements Comparable<State> {

        final State parent;
        final Set<Arc> maintained_arcs;
        final List<Arc> visited_arcs;
        final List<Boolean> matching_visited_arcs;
        final IntList trace;
        final boolean[] retained_events;
        int current;

        int g;
        int event_cost;
        int arc_cost;

        int h;

        int cost;
        Arc lastArc;

        State(State parent, Set<Arc> maintained_arcs, List<Arc> visited_arcs, List<Boolean> matching_visited_arcs, boolean[] retained_events, IntList trace, int current, boolean check_cost) {
            this.parent = parent;
            this.maintained_arcs = maintained_arcs;
            this.trace = trace;
            this.retained_events = retained_events;
            this.current = current;
            this.visited_arcs = visited_arcs;
            this.matching_visited_arcs = matching_visited_arcs;
            if(check_cost) computeCost();
        }

        Arc getLastArc() {
            return lastArc;
        }

        int computeCost() {
            cost = g() + h(false);
            return cost;
        }

        int computeCost(List<IntArrayList> ordered_labels) {
            cost = g() + h(ordered_labels);
            return cost;
        }

        protected abstract int getNext();

        protected abstract boolean setNext(Arc arc);

        protected abstract int g();

        protected abstract int h(boolean print);

        protected abstract int h(List<IntArrayList> ordered_labels);

        protected abstract int r();

        public abstract State clone();

        @Override
        public int compareTo(State o) {
            int compare = Integer.compare(cost, o.cost);
            if (compare == 0) {
                compare = Integer.compare(r(), o.r());
                if (compare == 0) {
                    compare = -Integer.compare(g, o.g);
                    if (compare == 0) {
                        compare = -Integer.compare(event_cost, o.event_cost);
                        if (compare == 0) {
                            compare = Integer.compare(arc_cost, o.arc_cost);
                        }
                    }
                }
            }
            return compare;
        }

        boolean[] getRetainedEvents() {
            return retained_events;
        }
    }

    private class SolutionPointer {

        final int cost;
        final int current;
        final boolean[] selected;
        final boolean forward;
        final List<Arc> visited_arcs;
        final int hashCode;

        SolutionPointer(int cost, int current, boolean[] selected, boolean forward, List<Arc> visited_arcs) {
            this.cost = cost;
            this.current = current;
            this.selected = selected;
            this.forward = forward;
            this.visited_arcs = visited_arcs;
            hashCode = new HashCodeBuilder().append(cost).append(current).append(selected).append(forward).append(visited_arcs).toHashCode();
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof SolutionPointer) {
                SolutionPointer s = (SolutionPointer) o;
                return cost == s.cost && current == s.current && selected == s.selected && forward == s.forward && visited_arcs.equals(s.visited_arcs);
            }
            return false;
        }
    }

    private int editDistance(IntArrayList left, IntArrayList right) {
        int n = left.size(); // length of left
        int m = right.size(); // length of right

        if (n == 0) {
            return m;
        } else if (m == 0) {
            return n;
        }

        if (n > m) {
            // swap the input strings to consume less memory
            IntArrayList tmp = left;
            left = right;
            right = tmp;
            n = m;
            m = right.size();
        }

        final int[] p = new int[n + 1];

        // indexes into strings left and right
        int i; // iterates through left
        int j; // iterates through right
        int upperLeft;
        int upper;

        int rightJ; // jth character of right
        int cost; // cost

        for (i = 0; i <= n; i++) {
            p[i] = i;
        }

        for (j = 1; j <= m; j++) {
            upperLeft = p[0];
            rightJ = right.get(j - 1);
            p[0] = j;

            for (i = 1; i <= n; i++) {
                upper = p[i];
                cost = left.get(i - 1) == rightJ ? 0 : 2;
                // minimum of cell to the left+1, to the top+1, diagonally left and up +cost
                p[i] = Math.min(Math.min(p[i - 1] + 1, p[i] + 1), upperLeft + cost);
                upperLeft = upper;
            }
        }

        return p[n];
    }

    private IntIntHashMap shortestPath(Set<Arc> arcs) {
        shortestDistance = new IntIntHashMap();
        IntIntHashMap nodes = new IntIntHashMap();
        int v = 0;
        nodes.put(1, v++);
        for(Arc arc : arcs) {
            if(!nodes.containsKey(arc.getTarget())) nodes.put(arc.getTarget(), v++);
        }
        int[][] dist = new int[nodes.size()][nodes.size()];

        for(int i = 0; i < nodes.size(); i++) {
            for(int j = 0; j < nodes.size(); j++) {
                if(i == j) dist[i][j] = 0;
                else dist[i][j] = Integer.MAX_VALUE / 2;
            }
        }

        for(Arc arc : arcs) {
            dist[nodes.get(arc.getSource())][nodes.get(arc.getTarget())] = 1;
        }

        for(int k = 0; k < nodes.size(); k++) {
            for(int i = 0; i < nodes.size(); i++) {
                for(int j = 0; j < nodes.size(); j++) {
                    if (dist[i][j] > dist[i][k] + dist[k][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }
        int sink = nodes.get(2);
        for(int i = 0; i < nodes.size(); i++) {
            if(i != sink) shortestDistance.put(nodes.flipUniqueValues().get(i), dist[i][sink]);
        }
        return shortestDistance;
    }

}
