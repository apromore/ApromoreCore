/*
 * Copyright  2009-2017 The Apromore Initiative.
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
package org.apromore.prodrift.mining.log.local;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apromore.prodrift.mining.log.local.AlphaRelations;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.util.Pair;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class PariallyOrderedRun_Luc {

	public static final String GLOBAL_SOURCE_LABEL = "_0_";

	public static final String GLOBAL_SINK_LABEL = "_1_";

	public static final String GLOBAL_SINK_PRIME_LABEL = "_2_";

	static int nextId = 0;

	static int traceId = 0;

	private Map<Integer, String> labels;

	private Map<Integer, Set<Integer>> successors;

	private Map<Integer, Set<Integer>> predecessors;

	private Multimap<Integer, Integer> concurrency;

	private Integer source;

	private AlphaRelations alphaRelations;

	private String modelName;

	public PariallyOrderedRun_Luc(XLog log, AlphaRelations alphaRelations,
			String modelName) {

		this.alphaRelations = alphaRelations;

		this.modelName = modelName;

		this.successors = new TreeMap<Integer, Set<Integer>>();

		this.predecessors = new TreeMap<Integer, Set<Integer>>();

		this.concurrency = HashMultimap.create();

		this.labels = new TreeMap<Integer, String>();

		this.source = addEvent(null, GLOBAL_SOURCE_LABEL);
		
		for (int i = 0; i < log.size(); i++) 
		{
			
            XTrace trace = log.get(i);
            addTrace(trace);
           
		}

	}
	
	public void addTrace(XTrace trace) {

		List<Integer> events = new ArrayList<>(trace.size());

		Integer localSource = copyTrace(events, trace);

		// localSource is null when the trace is empty

		if (localSource != null) {

			introduceConcurrency(events, localSource);

			reduceCausality(events, localSource);

		}

	}

	public void mergePrefix() {

		Map<Integer, Set<Integer>> equivalence = new TreeMap<Integer, Set<Integer>>();

		Map<Integer, Integer> inverseEquivalence = new TreeMap<Integer, Integer>();

		Map<Integer, Set<Integer>> lsuccessors = new TreeMap<Integer, Set<Integer>>();

		Map<Integer, Set<Integer>> lpredecessors = new TreeMap<Integer, Set<Integer>>();

		Map<Integer, String> llabels = new TreeMap<Integer, String>();

		Set<Integer> singleton = new TreeSet<Integer>();

		singleton.add(source);

		equivalence.put(source, singleton);

		inverseEquivalence.put(source, source);

		lsuccessors.put(source, new TreeSet<Integer>());

		lpredecessors.put(source, new TreeSet<Integer>());

		llabels.put(source, GLOBAL_SOURCE_LABEL);

		Set<Integer> visited = new HashSet<Integer>();

		Queue<Integer> open = new LinkedList<Integer>();

		open.offer(source);

		while (!open.isEmpty()) {

			Integer current = open.poll();

			Set<Integer> succs = new TreeSet<Integer>();

			for (Integer cprime : equivalence.get(current))

				succs.addAll(successors.get(cprime));

			visited.add(current);

			Map<String, Set<Integer>> prePartitions = partitionEventSetByLabel(succs);

			for (Set<Integer> prePartition : prePartitions.values()) {

				Map<Set<Integer>, Set<Integer>> partitions = refinePartitionByImmediatePredecessorRelation(
						inverseEquivalence, prePartition);

				if (partitions == null)
					continue; // Not all the predecessors have been visited

				for (Set<Integer> preds : partitions.keySet())

					if (visited.containsAll(preds)) {

						Set<Integer> partition = partitions.get(preds);

						Integer pivot = partition.iterator().next();

						if (!inverseEquivalence.containsKey(pivot)) {

							Integer successor = (nextId++);

							equivalence.put(successor, partition);

							for (Integer event : partition)

								inverseEquivalence.put(event, successor);

							llabels.put(successor, labels.get(pivot));

							lsuccessors.put(successor, new TreeSet<Integer>());

							lpredecessors.put(successor, preds);

							for (Integer pred : preds)

								lsuccessors.get(pred).add(successor);

							open.offer(successor);

						}

					}

			}

		}

		Multimap<Integer, Integer> lconcurrency = HashMultimap.create();

		for (Integer nevent : lsuccessors.keySet())

			for (Integer event : equivalence.get(nevent))

				for (Integer cevent : concurrency.get(event))

					lconcurrency.put(nevent, inverseEquivalence.get(cevent));

		this.successors = lsuccessors;

		this.predecessors = lpredecessors;

		this.concurrency = lconcurrency;

		this.labels = llabels;

	}

	private Map<Set<Integer>, Set<Integer>> refinePartitionByImmediatePredecessorRelation(

	Map<Integer, Integer> req, Set<Integer> partition) {

		Map<Set<Integer>, Set<Integer>> rpartitions = new HashMap<Set<Integer>, Set<Integer>>();

		for (Integer event : partition) {

			Set<Integer> translatedPredecessors = new TreeSet<Integer>();

			for (Integer pred : predecessors.get(event)) {

				if (!req.containsKey(pred))

					return null; // The event should wait for all the
									// predecessors to be visited

				translatedPredecessors.add(req.get(pred));

			}

			Set<Integer> set = rpartitions.get(translatedPredecessors);

			if (set == null)

				rpartitions.put(translatedPredecessors,
						set = new TreeSet<Integer>());

			set.add(event);

		}

		return rpartitions;

	}

	private Map<String, Set<Integer>> partitionEventSetByLabel(
			Set<Integer> events) {

		Map<String, Set<Integer>> partitions = new HashMap<String, Set<Integer>>();

		for (Integer local : events) {

			Set<Integer> partition = partitions.get(labels.get(local));

			if (partition == null)

				partitions.put(labels.get(local),
						partition = new TreeSet<Integer>());

			partition.add(local);

		}

		return partitions;

	}

	private Integer copyTrace(List<Integer> events, XTrace trace) {

		String labelBeingCounted = null;

		int labelCounter = 1;

		Integer predecessor = null;

		Integer localSource = null;

		for (XEvent event : trace) {

			if (isCompleteEvent(event)) {

				String label = getEventName(event);

				// =========================================

				// ===== Abstraction of length-one loops

				// =========================================

				if (label.equals(labelBeingCounted))
					labelCounter++;

				else {

					labelCounter = 1;

					labelBeingCounted = label;

				}

				if (labelCounter > 1)
					continue;

				// ------------------------------------------

				Integer current = addEvent(events, label);

				if (predecessor == null)

					addEdge(source, localSource = current);

				else

					addEdge(predecessor, current);

				predecessor = current;

				if (alphaRelations.isEventInvolvedInLengthOneLoop(label)) {

					current = addEvent(events, label
							+ AlphaRelations.LENGTH_ONE_SUFFIX);

					addEdge(predecessor, current);

					predecessor = current;

				}

			}

		}

		// The trace was empty !!

		if (predecessor == null)

			// return null;

			predecessor = source;

		Integer current = addEvent(events, GLOBAL_SINK_LABEL);

		addEdge(predecessor, current);

		Integer additional = addEvent(events, GLOBAL_SINK_PRIME_LABEL);

		addEdge(current, additional);

		return localSource;

	}

	private void introduceConcurrency(List<Integer> events, Integer localSource) {

		boolean changed = false;

		do {

			changed = false;

			Set<Integer> visited = new HashSet<Integer>();

			Queue<Integer> worklist = new LinkedList<Integer>();

			worklist.offer(localSource);

			while (!worklist.isEmpty()) {

				Integer curr = worklist.poll();

				visited.add(curr);

				Set<Pair<Integer, Integer>> workset = new HashSet<Pair<Integer, Integer>>();

				for (Integer succ : successors.get(curr)) {

					if (!worklist.contains(succ) && !visited.contains(succ))

						worklist.offer(succ);

					if (alphaRelations.areConcurrent(labels.get(curr),
							labels.get(succ))
							&&

							!successors.get(succ).contains(curr)) {

						workset.add(new Pair<Integer, Integer>(curr, succ));

						concurrency.put(curr, succ);

						concurrency.put(succ, curr);

					}

				}

				for (Pair<Integer, Integer> pair : workset) {

					Integer _src = pair.getFirst();

					Integer _tgt = pair.getSecond();

					for (Integer pred : predecessors.get(_src))

						addEdge(pred, _tgt);

					for (Integer succ : successors.get(_tgt))

						addEdge(_src, succ);

					removeEdge(_src, _tgt);

					changed = true;

				}

			}

		} while (changed);

	}

	private void reduceCausality(List<Integer> events, Integer localSource) {

		Set<Pair<Integer, Integer>> toRemove = new HashSet<>();

		// Remove transitive causality edges

		for (Integer src : events) {

			for (Integer tgt : successors.get(src))

				for (Integer inter : successors.get(src))

					if (!tgt.equals(inter)
							&& successors.get(inter).contains(tgt))

						toRemove.add(new Pair<Integer, Integer>(src, tgt));

		}

		for (Pair<Integer, Integer> pair : toRemove) {

			successors.get(pair.getFirst()).remove(pair.getSecond());

			predecessors.get(pair.getSecond()).remove(pair.getFirst());

		}

	}

	private void addEdge(Integer src, Integer tgt) {

		successors.get(src).add(tgt);

		predecessors.get(tgt).add(src);

	}

	private void removeEdge(Integer src, Integer tgt) {

		successors.get(src).remove(tgt);

		predecessors.get(tgt).remove(src);

	}

	private Integer addEvent(List<Integer> events, String eventLabel) {

		Integer eventId = (nextId++);

		if (events != null)
			events.add(eventId);

		labels.put(eventId, eventLabel);

		successors.put(eventId, new TreeSet<Integer>());

		predecessors.put(eventId, new TreeSet<Integer>());

		return eventId;

	}

	private String getEventName(XEvent e) {

		return e.getAttributes().get(XConceptExtension.KEY_NAME).toString();

	}

	private boolean isCompleteEvent(XEvent e) {

		XAttributeMap amap = e.getAttributes();

		return (amap.get(XLifecycleExtension.KEY_TRANSITION).toString()
				.toLowerCase().equals("complete"));

	}

	public String toDot() {

		return toDot(labels, successors);

	}

	protected String toDot(Map<Integer, String> labels,
			Map<Integer, Set<Integer>> successors) {

		StringWriter str = new StringWriter();

		PrintWriter out = new PrintWriter(str);

		out.println("digraph G {");

		for (Integer vertex : labels.keySet())

			out.printf("\tn%d [label=\"%s\"];\n", vertex, labels.get(vertex));

		// out.printf("\tn%d [label=\"%s (%d)\"];\n", vertex,
		// labels.get(vertex), vertex);

		for (Integer src : labels.keySet())

			for (Integer tgt : successors.get(src))

				out.printf("\tn%d -> n%d;\n", src, tgt);

		out.println("}");

		return str.toString();

	}

}