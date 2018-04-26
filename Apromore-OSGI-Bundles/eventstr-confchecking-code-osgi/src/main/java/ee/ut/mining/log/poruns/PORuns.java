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

package ee.ut.mining.log.poruns;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

//import de.hpi.bpt.utils.IOUtils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class PORuns implements PORunConst {
	private List<Integer> sources;
	private List<Integer> sinks;
	private Map<Integer, String> labels;
	
	private Multimap<Integer, Integer> successors;
	private Multimap<Integer, Integer> predecessors;
	private Multimap<Integer, Integer> concurrency;
	private Multimap<Integer, Integer> eq;
	private HashMultimap<Integer, String> evtsTraces;
    private HashMultimap<Integer, String> evtsTracesMerged;
	
	public PORuns() {
		this.sources = new ArrayList<>();
		this.sinks = new ArrayList<>();
		this.labels = new HashMap<>();
		this.successors = HashMultimap.create();
		this.predecessors = HashMultimap.create();
		this.concurrency = HashMultimap.create();
		this.evtsTraces = HashMultimap.create();
        this.evtsTracesMerged = HashMultimap.create();
	}
	
	public void add(PORun run) {
		sources.add(run.getSource());
		sinks.add(run.getSink());
		labels.putAll(run.getLabelMap());
		successors.putAll(run.asSuccessorsList());
		predecessors.putAll(run.asPredecessorsList());
		concurrency.putAll(run.getConcurrencyRelation());
		for(Integer key : run.vertices)
		    evtsTraces.put(key, run.getIdTrace());
	}
	
	public void mergePrefix() {
		Multimap<Integer, Integer> nsuccessors = HashMultimap.create();
		Map<Integer, String> nlabels = new HashMap<>();
		
		eq = HashMultimap.create();
		Map<Integer, Integer> inveq = new HashMap<>();
		
		Integer currentId = 0;
		eq.putAll(currentId, sources);
		evtsTracesMerged.putAll(currentId, getTraceIds(sources));
		for (Integer source: sources) inveq.put(source, currentId);
		nlabels.put(currentId, GLOBAL_SOURCE_LABEL);

		Set<Integer> visited = new HashSet<Integer>(); 
		Queue<Integer> open = new LinkedList<Integer>();
		
		open.offer(currentId++);
		
		while (!open.isEmpty()) {
			Integer current = open.poll();
			
			Set<Integer> succs = new TreeSet<Integer>();
			for (Integer cprime: eq.get(current)) succs.addAll(successors.get(cprime));
			
			visited.add(current);			
			
			for (Set<Integer> prePartition: partitionEventSetByLabel(succs).values()) {
				Map<Set<Integer>, Set<Integer>> partitions = refinePartitionByImmediatePredecessorRelation(inveq, prePartition);
				if (partitions == null) continue; // Not all the predecessors have been visited
				for (Set<Integer> preds : partitions.keySet())
					if (visited.containsAll(preds)) {
						Set<Integer> partition = partitions.get(preds);
						Integer pivot = partition.iterator().next();
						if (!inveq.containsKey(pivot)) {
							Integer successor = currentId++;
							eq.putAll(successor, partition);
                            evtsTracesMerged.putAll(successor, getTraceIds(partition));
							for (Integer event: partition) inveq.put(event, successor);
							
							nlabels.put(successor, labels.get(pivot));
							for (Integer pred: preds)
								nsuccessors.put(pred,successor);
							
							open.offer(successor);
						}
					}
			}
		}
		
		Multimap<Integer, Integer> lconcurrency = HashMultimap.create();
		
		for (Integer nevent: nsuccessors.keySet())
			for (Integer event: eq.get(nevent))
				for (Integer cevent: concurrency.get(event)) {
					Integer ncevent = inveq.get(cevent);
					if (ncevent != null)
						lconcurrency.put(nevent, ncevent);
				}
		
		successors = nsuccessors;
		predecessors = null;
		labels = nlabels;
		concurrency = lconcurrency;
		sources = Collections.singletonList(0);
		
		Set<Integer> tmp = new HashSet<Integer>(successors.values());
		tmp.removeAll(successors.keySet());
		
		sinks = new ArrayList<>(tmp);
	}

    private List<String> getTraceIds(Iterable<Integer> sources) {
	    List<String> traceIds = new ArrayList<>();
	    for(Integer i : sources)
	        traceIds.addAll(evtsTraces.get(i));

	    return traceIds;
    }

    private Map<Set<Integer>, Set<Integer>> refinePartitionByImmediatePredecessorRelation(Map<Integer, Integer> req, Set<Integer> partition) {
		Map<Set<Integer>, Set<Integer>> rpartitions = new HashMap<Set<Integer>, Set<Integer>>();
		
		outer:
		for (Integer event: partition) {
			Set<Integer> translatedPredecessors = new TreeSet<Integer>();
			for (Integer pred: predecessors.get(event)) {
				if (!req.containsKey(pred))
					continue outer; // The event must wait until all its predecessors are visited
				translatedPredecessors.add(req.get(pred));
			}
			
			Set<Integer> set = rpartitions.get(translatedPredecessors);
			if (set == null)
				rpartitions.put(translatedPredecessors, set = new TreeSet<Integer>());
			set.add(event);
		}
		
		return rpartitions;
	}

	
	private Map<String, Set<Integer>> partitionEventSetByLabel(Set<Integer> events) {
		Map<String, Set<Integer>> partitions = new HashMap<>();
		for (Integer event: events) {
			String label = labels.get(event);
			Set<Integer> set = partitions.get(label);
			if (set == null)
				partitions.put(label, set = new HashSet<>());
			set.add(event);
		}
		return partitions;
	}

	
	public String toDot() {
		return toDot(labels, successors);
	}
	
	protected String toDot(Map<Integer, String> labels, Multimap<Integer, Integer> successors) {
		StringWriter str = new StringWriter();
		PrintWriter out = new PrintWriter(str);
		
		out.println("digraph G {");
		
		for (Integer vertex: labels.keySet())
			out.printf("\tn%d [label=\"%s(%d)\"];\n", vertex, labels.get(vertex), vertex);

		for (Integer src: labels.keySet())
			for (Integer tgt: successors.get(src))
				out.printf("\tn%d -> n%d;\n", src, tgt);
		
		out.println("}");
		
		return str.toString();
	}

	public Map<Integer, String> getLabels() {
		return labels;
	}

	public Multimap<Integer, Integer> getSuccessors() {
		return successors;
	}

	public Multimap<Integer, Integer> getConcurrency() {
		return concurrency;
	}	

	public Multimap<Integer, Integer> getEquivalenceClasses() {
		return eq;
	}

	public List<Integer> getSources() {
		return sources;
	}

	public List<Integer> getSinks() {
		return sinks;
	}

    public HashMultimap<Integer, String> getEvtsTracesMerged() {
        return evtsTracesMerged;
    }
}
