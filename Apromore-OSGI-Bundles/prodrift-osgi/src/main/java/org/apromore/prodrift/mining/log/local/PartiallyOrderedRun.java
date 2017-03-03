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
package org.apromore.prodrift.mining.log.local;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apromore.prodrift.config.BehaviorRelation;
import org.apromore.prodrift.graph.util.GraphUtils;
import org.apromore.prodrift.mining.log.local.AlphaRelations;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.util.Pair;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class PartiallyOrderedRun {
	
	private static final String GLOBAL_SOURCE_LABEL = "_0_";
	private static final String GLOBAL_SINK_LABEL = "_1_";
	private static final String GLOBAL_SINK_PRIME_LABEL = "_2_";

	private static int nextId = 0;
	private static int traceId = 0;
	private Map<Integer, String> labels;
	private Map<Integer, Set<Integer>> successors;
	private Map<Integer, Set<Integer>> predecessors;
	private Multimap<Integer, Integer> concurrency;
	
	private ArrayList<List<String>> existingTraces = new ArrayList<List<String>>();
	private String sampleTracesOutput = "";
	
	private ArrayList<Multimap<String, Set<String>>> DistinctRuns;
	private ArrayList<Map<Integer,Integer>> DRCount;
	
	
	private int [] tracesDistrib;
	private int [] runsDistrib;
	
	private Integer source;
	private AlphaRelations alphaRelations;
	private String modelName;
	private XLog log;
	
	public PartiallyOrderedRun(AlphaRelations alphaRelations, String modelName, XLog log) {
		this.log = log;
		this.alphaRelations = alphaRelations;
		this.modelName = modelName;
		this.successors = new TreeMap<Integer, Set<Integer>>();
		this.predecessors = new TreeMap<Integer, Set<Integer>>();
		this.concurrency = HashMultimap.create();
		this.labels = new TreeMap<Integer, String>();
		this.source = addEvent(null, GLOBAL_SOURCE_LABEL);
		
		this.DistinctRuns = new ArrayList<>();
		this.DRCount = new ArrayList<>();
				
	}
	
	
	//trace
	public void addTrace(XTrace trace) {
		List<String> activitiesList = new ArrayList<>(trace.size());
		TraceToEventList(activitiesList, trace);
		
		StringWriter str = new StringWriter();
		PrintWriter out = new PrintWriter(str);
		
		if (existingTraces.contains(activitiesList)){
			out.println(existingTraces.indexOf(activitiesList));
		}
		else {
			existingTraces.add(activitiesList);
			out.println(existingTraces.indexOf(activitiesList));
		}
		
		sampleTracesOutput += str.toString();
	}
	
	public int processSingleTrace(XTrace trace) {
		List<String> activitiesList = new ArrayList<>(trace.size());
		TraceToEventList(activitiesList, trace);
		
		int traceIndex = 0;
		
		if (existingTraces.contains(activitiesList)){
			traceIndex =existingTraces.indexOf(activitiesList);
		}
		else {
			existingTraces.add(activitiesList);
			traceIndex =existingTraces.indexOf(activitiesList);
		}
		
		return traceIndex;
	}
	
	private void TraceToEventList (List<String> events, XTrace tr){
		for (XEvent event: tr) {
			if (isCompleteEvent(event)) {
				String label = getEventName(event);
				events.add(label);
			}
		}
		
	}
	
	
	public double[] getTracesSampleValuesDouble(int logIndex, int winSize) {
		double [] sampleValues = new double[winSize];
		for (int i = 0; i < winSize; i++) {
			sampleValues [i] = (double) tracesDistrib[logIndex+i];
		}
		return sampleValues;
	}
	
	public int[] getTracesSampleValuesInt(int logIndex, int winSize) {
		int [] sampleValues = new int[winSize];
		for (int i = 0; i < winSize; i++) {
			sampleValues [i] = tracesDistrib[logIndex+i];
		}
		return sampleValues;
	}
	
	public void tracesToDisctrib() {
		tracesDistrib = new int [log.size()];
		for (int i = 0; i < log.size(); i++) {
			tracesDistrib [i] = processSingleTrace(log.get(i));
			sampleTracesOutput += tracesDistrib [i] + ",";
		}
//		System.out.println("Number of disctinct traces "+existingTraces.size());
	}
	
	//runs
//	public void addTrace(XTrace trace, int chunk_index) {
//		List<Integer> events = new ArrayList<>(trace.size());
//		Integer localSource = copyTrace(events, trace);
//		
//		// localSource is null when the trace is empty
//		if (localSource != null) {
//			introduceConcurrency(events, localSource);
//			reduceCausality(events, localSource);
//			// compare the created run with all the existing distinct runs
//			boolean runExist = false;
//			for (int i = 0; i < DistinctRuns.size(); i++) {
//				if (RunCompare(events, DistinctRuns.get(i))) {
//					//if DRCount.get(chunk_index).
//					if (DRCount.get(chunk_index).containsKey(i))
//						DRCount.get(chunk_index).put(i, DRCount.get(chunk_index).get(i)+1);
//					else DRCount.get(chunk_index).put(i, 1);
//					//DRCount.get(chunk_index).set(i, new Integer(DRCount.get(chunk_index).get(i).intValue()+1));
//					runExist = true;
//					break;
//					}
//			}
//			if (!runExist) {
//				//addNewDistinctRun(localSource);
//				addNewDR(events);
//				DRCount.get(chunk_index).put(DistinctRuns.size()-1, new Integer(1));
//			} 
//		}
//	}
	
	public int processSingleRun(XTrace trace) {
		List<Integer> events = new ArrayList<>(trace.size());
		Integer localSource = copyTrace(events, trace);
		
		int runIndex = 0;
		
		// localSource is null when the trace is empty
		if (localSource != null) {
			introduceConcurrency(events, localSource);
			reduceCausality(events, localSource);
			// compare the created run with all the existing distinct runs
//			boolean runExist = false;
//			for (int i = 0; i < DistinctRuns.size(); i++) {
//				if (RunCompare(events, DistinctRuns.get(i))) {
//					runIndex = i;
//					runExist = true;
//					break;
//					}
//			}
//			if (!runExist) {
//				//addNewDistinctRun(localSource);
//				runIndex = DistinctRuns.size();
//				addNewDR(events);
//			} 
			Multimap<String, Set<String>> run = createRun(events);
			runIndex = DistinctRuns.indexOf(run);
			if (runIndex==-1) {
				DistinctRuns.add(run);
				runIndex = DistinctRuns.size()-1;
			}
		}
		return runIndex;
	}
	
	
	public static double[] copyFromIntArray(int[] source) {
	    double[] dest = new double[source.length];
	    for(int i=0; i<source.length; i++) {
	        dest[i] = source[i];
	    }
	    return dest;
	}
	
	
	public int[] getRunsSampleValuesInt(int logIndex, int winSize) {
		int [] sampleValues = new int[winSize];
		for (int i = 0; i < winSize; i++) {
			sampleValues [i] = runsDistrib[logIndex+i];
		}
		return sampleValues;
	}
	
	public int[] getPastRunsSampleValuesInt(int logIndex, int winSize) {
		return getRunsSampleValuesInt(logIndex-winSize+1,winSize);
	}
	
	public void runsToDisctrib() {
		sampleTracesOutput +="\n";
		runsDistrib = new int [log.size()];
		for (int i = 0; i < log.size(); i++) {
			runsDistrib [i] = processSingleRun(log.get(i));
			sampleTracesOutput += runsDistrib [i] + ",";
		}
//		System.out.println("Total number of distinct runs "+DistinctRuns.size());
	}
	
	
//	private boolean RunCompare(List<Integer> events, Multimap<String, Set<String>> drToCompare ) {
//		boolean equal = true;
////		if (events.size() == drToCompare.size()){
//			
//			Set<String> activitiesSet1 = new TreeSet<>();
//			for (int i = 0; i < events.size(); i++) 
//				activitiesSet1.add(labels.get(events.get(i)));
//			
//			Set<String> activitiesSet2 = new TreeSet<>(drToCompare.keySet());
//			
//			if (!activitiesSet1.equals(activitiesSet2)) {
//				equal = false;
//			}
//			else {
//				for (int i = 0; i < events.size(); i++) {
//					Set<String> SubactivitiesSet1 = new TreeSet<>();
//					for(Integer succ : successors.get(events.get(i)))
//						SubactivitiesSet1.add(labels.get(succ));
//					if (!drToCompare.get(labels.get(events.get(i))).equals(SubactivitiesSet1)){
//						equal = false;
//						break;//break
//						}
//				}
//			}
////		}
////		else equal = false;
//		return equal;
//	}
//	
//	private void addNewDR(List<Integer> events) {
//		Multimap<String, Set<String>> runBuf = HashMultimap.create();
//		for (int i = 0; i < events.size(); i++) {
//			Set<String> succActivitiesSet = new TreeSet<>();
//			for (Integer succ:successors.get(events.get(i)))
//				succActivitiesSet.add(labels.get(succ));
//			runBuf.put(labels.get(events.get(i)), new TreeSet<String>(succActivitiesSet));//create first node
//		}
//		DistinctRuns.add(runBuf);
//		int test = DistinctRuns.indexOf(runBuf);
//		System.out.println("yes it works "+test);
//	}
	
	//create a run (based on events' names) out of the "events" list pointing to the Partially order runs.
	private Multimap<String, Set<String>>  createRun(List<Integer> events) {
		Multimap<String, Set<String>> runBuf = ArrayListMultimap.create();
		for (int i = 0; i < events.size(); i++) {
			Set<String> succActivitiesSet = new TreeSet<>();
			for (Integer succ:successors.get(events.get(i)))
				succActivitiesSet.add(labels.get(succ));
			runBuf.put(labels.get(events.get(i)), new TreeSet<String>(succActivitiesSet));//create first node
		}
		return runBuf;
	}
	
	public PrimeEventStructure<Integer> getPrimeEventStructure() {
		HashMap<Integer, Integer> pesMap = new HashMap<>();
		LinkedList<Integer> pesRIdx = new LinkedList<>();
		LinkedList<String> pesLabels = new LinkedList<>();
		
		for (Integer original: labels.keySet()) {
			int index = pesRIdx.size();
			String label = labels.get(original);
			
			pesMap.put(original, index);
			pesRIdx.add(original);
			pesLabels.add(label);			
		}
		
		int size = pesLabels.size();
		
		BehaviorRelation[][] matrix = new BehaviorRelation[size][size];
		boolean[][] causality = new boolean[size][size];
		for (int i = 0; i < size; i++) {
			Arrays.fill(matrix[i], BehaviorRelation.CONFLICT);
			matrix[i][i] = BehaviorRelation.CONCURRENCY;
		}

		for (Integer _src: successors.keySet()) {
			Integer src = pesMap.get(_src);
			for (Integer _tgt: successors.get(_src))
				causality[src][pesMap.get(_tgt)] = true;				
		}
		
		for (Entry<Integer, Integer> entry: concurrency.entries()) {
			Integer ev1 = pesMap.get(entry.getKey());
			Integer ev2 = pesMap.get(entry.getValue());
			if (ev1 != null && ev2 != null)
				matrix[ev1][ev2] = BehaviorRelation.CONCURRENCY;
		}
		
		GraphUtils.transitiveClosure(causality);
		
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				if (causality[i][j]) {
					matrix[i][j] = BehaviorRelation.FOLLOW;
					matrix[j][i] = BehaviorRelation.INV_FOLLOW;
				}		

		return new PrimeEventStructure<>(matrix, pesMap, pesRIdx, pesLabels, modelName);
	}
	
	public BehaviorRelation[][] getBehaviorRelationMatrix() {
		HashMap<Integer, Integer> pesMap = new HashMap<>();
		LinkedList<Integer> pesRIdx = new LinkedList<>();
		LinkedList<String> pesLabels = new LinkedList<>();
		
		for (Integer original: labels.keySet()) {
			int index = pesRIdx.size();
			String label = labels.get(original);
			
			pesMap.put(original, index);
			pesRIdx.add(original);
			pesLabels.add(label);			
		}
		
		int size = pesLabels.size();
		
		BehaviorRelation[][] matrix = new BehaviorRelation[size][size];
		boolean[][] causality = new boolean[size][size];
		for (int i = 0; i < size; i++) {
			Arrays.fill(matrix[i], BehaviorRelation.CONFLICT);
			matrix[i][i] = BehaviorRelation.CONCURRENCY;
		}

		for (Integer _src: successors.keySet()) {
			Integer src = pesMap.get(_src);
			for (Integer _tgt: successors.get(_src))
				causality[src][pesMap.get(_tgt)] = true;				
		}
		
		for (Entry<Integer, Integer> entry: concurrency.entries()) {
			Integer ev1 = pesMap.get(entry.getKey());
			Integer ev2 = pesMap.get(entry.getValue());
			if (ev1 != null && ev2 != null)
				matrix[ev1][ev2] = BehaviorRelation.CONCURRENCY;
		}
		
		GraphUtils.transitiveClosure(causality);
		
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				if (causality[i][j]) {
					matrix[i][j] = BehaviorRelation.FOLLOW;
					matrix[j][i] = BehaviorRelation.INV_FOLLOW;
				}		

		return matrix;
	}
	
	public BehaviorRelation[][] getBehaviorRelationMatrix(HashMap<Integer, String> index_label_Map) {
		HashMap<Integer, Integer> pesMap = new HashMap<>();
		LinkedList<Integer> pesRIdx = new LinkedList<>();
		LinkedList<String> pesLabels = new LinkedList<>();
		
		for (Integer original: labels.keySet()) {
			
			String label = labels.get(original);
			if(!label.startsWith("_") && !label.endsWith("_") /*&& !label.contains("START")*/)
			{
				
				int index = pesRIdx.size();
				
				pesMap.put(original, index);
				pesRIdx.add(original);
				pesLabels.add(label);		
				
				index_label_Map.put(index, label);
				
			}
			
		}
		
		int size = pesLabels.size();
		
		BehaviorRelation[][] matrix = new BehaviorRelation[size][size];
		boolean[][] causality = new boolean[size][size];
		for (int i = 0; i < size; i++) {
			Arrays.fill(matrix[i], BehaviorRelation.CONFLICT);
			matrix[i][i] = BehaviorRelation.CONCURRENCY;
		}

		for (Integer _src: successors.keySet()) {
			if(pesMap.containsKey(_src))
			{
				
				Integer src = pesMap.get(_src);
				for (Integer _tgt: successors.get(_src))
					causality[src][pesMap.get(_tgt)] = true;	
				
			}
						
		}
		
		for (Entry<Integer, Integer> entry: concurrency.entries()) {
			Integer ev1 = pesMap.get(entry.getKey());
			Integer ev2 = pesMap.get(entry.getValue());
			if (ev1 != null && ev2 != null)
				matrix[ev1][ev2] = BehaviorRelation.CONCURRENCY;
		}
		
		GraphUtils.transitiveClosure(causality);
		
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				if (causality[i][j]) {
					matrix[i][j] = BehaviorRelation.FOLLOW;
					matrix[j][i] = BehaviorRelation.INV_FOLLOW;
				}		

		return matrix;
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
			for (Integer cprime: equivalence.get(current))
				succs.addAll(successors.get(cprime));
						
			visited.add(current);
						
			Map<String, Set<Integer>> prePartitions = partitionEventSetByLabel(succs);
			for (Set<Integer> prePartition: prePartitions.values()) {
				Map<Set<Integer>, Set<Integer>> partitions = refinePartitionByImmediatePredecessorRelation(inverseEquivalence, prePartition);
				if (partitions == null) continue; // Not all the predecessors have been visited
				for (Set<Integer> preds : partitions.keySet())
					if (visited.containsAll(preds)) {
						Set<Integer> partition = partitions.get(preds);
						Integer pivot = partition.iterator().next();
						if (!inverseEquivalence.containsKey(pivot)) {
							Integer successor = (nextId++);
							equivalence.put(successor, partition);
							for (Integer event: partition)
								inverseEquivalence.put(event, successor);
							
							llabels.put(successor, labels.get(pivot));							
							lsuccessors.put(successor, new TreeSet<Integer>());
							lpredecessors.put(successor, preds);
							for (Integer pred: preds)
								lsuccessors.get(pred).add(successor);

							open.offer(successor);
						}
					}
			}
		}
		
		Multimap<Integer, Integer> lconcurrency = HashMultimap.create();
		
		for (Integer nevent: lsuccessors.keySet())
			for (Integer event: equivalence.get(nevent))
				for (Integer cevent: concurrency.get(event))
					lconcurrency.put(nevent, inverseEquivalence.get(cevent));
		
		this.successors = lsuccessors;
		this.predecessors = lpredecessors;
		this.concurrency = lconcurrency;
		this.labels = llabels;
	}

	private Map<Set<Integer>, Set<Integer>> refinePartitionByImmediatePredecessorRelation(
			Map<Integer, Integer> req, Set<Integer> partition) {
		Map<Set<Integer>, Set<Integer>> rpartitions = new HashMap<Set<Integer>, Set<Integer>>();
		
		for (Integer event: partition) {
			Set<Integer> translatedPredecessors = new TreeSet<Integer>();
			for (Integer pred: predecessors.get(event)) {
				if (!req.containsKey(pred))
					return null; // The event should wait for all the predecessors to be visited
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
		Map<String, Set<Integer>> partitions = new HashMap<String, Set<Integer>>();
		for (Integer local: events) {
			Set<Integer> partition = partitions.get(labels.get(local));
			if (partition == null)
				partitions.put(labels.get(local), partition = new TreeSet<Integer>());
			partition.add(local);
		}
		return partitions;
	}
	
	private Integer copyTrace(List<Integer> events, XTrace trace) {
		String labelBeingCounted = null;
		int labelCounter = 1;
		Integer predecessor = null;
		Integer localSource = null;

		for (XEvent event: trace) {
			if (isCompleteEvent(event)) {
				String label = getEventName(event);
				
//				// =========================================
//				// ===== Abstraction of length-one loops
//				// =========================================
//				if (label.equals(labelBeingCounted)) labelCounter++;
//				else {
//					labelCounter = 1;
//					labelBeingCounted = label;
//				}
//				if (labelCounter > 1) continue;
//				// ------------------------------------------
				
				Integer current = addEvent(events, label);
				if (predecessor == null)
					addEdge(source, localSource = current);
				else
					addEdge(predecessor, current);
				predecessor = current;
				
				if (alphaRelations.isEventInvolvedInLengthOneLoop(label)) {
					current = addEvent(events, label + AlphaRelations.LENGTH_ONE_SUFFIX);
					addEdge(predecessor, current);
					predecessor = current;
				}
			}
		}
		
		// The trace was empty !!
		if (predecessor == null)
//			return null;
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
				
				Set<Pair<Integer, Integer>> workset = new HashSet<Pair<Integer,Integer>>();
				for (Integer succ: successors.get(curr)) {
					if (!worklist.contains(succ) && !visited.contains(succ))
						worklist.offer(succ);
					
					if (alphaRelations.areConcurrent(labels.get(curr), labels.get(succ)) &&
							!successors.get(succ).contains(curr)) {
						workset.add(new Pair<Integer, Integer>(curr, succ));
						
						concurrency.put(curr, succ);
						concurrency.put(succ, curr);
					}
				}
				
				for (Pair<Integer, Integer> pair: workset) {
					Integer _src = pair.getFirst();
					Integer _tgt = pair.getSecond();
					
					for (Integer pred: predecessors.get(_src))
						addEdge(pred, _tgt);
					for (Integer succ: successors.get(_tgt))
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
		for (Integer src: events) {
			for (Integer tgt: successors.get(src))
				for (Integer inter: successors.get(src))
					if (!tgt.equals(inter) && successors.get(inter).contains(tgt))
						toRemove.add(new Pair<Integer, Integer>(src, tgt));
		}
		
		for (Pair<Integer,Integer> pair: toRemove) {
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
		if (events != null) events.add(eventId);
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
		return (amap.get(XLifecycleExtension.KEY_TRANSITION).toString().toLowerCase().equals("complete"));
	}
	
	public String toDot() {
		return toDot(labels, successors);
	}
	
	protected String toDot(Map<Integer, String> labels, Map<Integer, Set<Integer>> successors) {
		StringWriter str = new StringWriter();
		PrintWriter out = new PrintWriter(str);
		
		out.println("digraph G {");
		
		for (Integer vertex: labels.keySet())
			out.printf("\tn%d [label=\"%s\"];\n", vertex, labels.get(vertex));
//			out.printf("\tn%d [label=\"%s (%d)\"];\n", vertex, labels.get(vertex), vertex);

		for (Integer src: labels.keySet())
			for (Integer tgt: successors.get(src))
				out.printf("\tn%d -> n%d;\n", src, tgt);
		
		out.println("}");
		
		return str.toString();
	}
	
	public String DRtoDot() {
		StringWriter str = new StringWriter();
		PrintWriter out = new PrintWriter(str);
		
		out.println("digraph G {");
		
		System.out.println("------------------------------");
		System.out.println("Total number of Runs is " + DistinctRuns.size());

		for (int i = 0; i < DistinctRuns.size(); i++){
			
//			System.out.println("Run " + i + " = " + DistinctRuns.get(i).toString());
//			System.out.println("Run Freq" + DRCount.get(i));
			
			for (String entry:DistinctRuns.get(i).keys()){
				out.printf("\tNode_%s [label=\"%s\"];\n", entry.replaceAll("\\s","")+i, entry);
			}
			for (Entry<String, Set<String>> entry:DistinctRuns.get(i).entries()){
				String src = entry.getKey();
				for (String tgt:entry.getValue()) {
					if (!src.isEmpty() && !tgt.isEmpty())
						out.printf("\tNode_%s -> Node_%s;\n", src.replaceAll("\\s","")+i, tgt.replaceAll("\\s","")+i);
				}
			}
		}
					
		out.println("}");
		System.out.println("------------------------------");
		return str.toString();
	}
	
	
	public String DRtoMatrixFile() {
		StringWriter str = new StringWriter();
		PrintWriter out = new PrintWriter(str);
		
		String runsHeader = "R0";
		
		try {
			for (int i = 1; i < DistinctRuns.size(); i++)
			runsHeader +=  ", R" + i;
		

		
		
		out.println(runsHeader);
		
		for (int i_chunk = 0; i_chunk < DRCount.size(); i_chunk++) {//each shunk
			String shunk_values = "";
			double total = 0;
			for (Entry<Integer, Integer> entry : DRCount.get(i_chunk).entrySet()) {
				total += entry.getValue();
			}
			for (int i_chunk_run =0 ; i_chunk_run < DistinctRuns.size() ; i_chunk_run++) {
				double percentage = DRCount.get(i_chunk).get(i_chunk_run)/total;
//				System.out.println(percentage);
				if (i_chunk_run != 0) {
					if (DRCount.get(i_chunk).containsKey(i_chunk_run)) 
						shunk_values += ", " + percentage;
					else shunk_values += ", 0";
				}
				else { // for the first element no comma
					if (DRCount.get(i_chunk).containsKey(i_chunk_run)) 
						shunk_values += percentage;
					else shunk_values += "0";
				}	
			}
			out.println(shunk_values);
		}
		} catch (Exception e) {
			System.out.println(e.toString());
		}

		return str.toString();
	}


	public static int getNextId() {
		return nextId;
	}


	public static void setNextId(int nextId) {
		PartiallyOrderedRun.nextId = nextId;
	}


	public static int getTraceId() {
		return traceId;
	}


	public static void setTraceId(int traceId) {
		PartiallyOrderedRun.traceId = traceId;
	}


	public Map<Integer, String> getLabels() {
		return labels;
	}


	public void setLabels(Map<Integer, String> labels) {
		this.labels = labels;
	}


	public Map<Integer, Set<Integer>> getSuccessors() {
		return successors;
	}


	public void setSuccessors(Map<Integer, Set<Integer>> successors) {
		this.successors = successors;
	}


	public Map<Integer, Set<Integer>> getPredecessors() {
		return predecessors;
	}


	public void setPredecessors(Map<Integer, Set<Integer>> predecessors) {
		this.predecessors = predecessors;
	}


	public Multimap<Integer, Integer> getConcurrency() {
		return concurrency;
	}


	public void setConcurrency(Multimap<Integer, Integer> concurrency) {
		this.concurrency = concurrency;
	}


	public ArrayList<List<String>> getExistingTraces() {
		return existingTraces;
	}


	public void setExistingTraces(ArrayList<List<String>> existingTraces) {
		this.existingTraces = existingTraces;
	}


	public String getSampleTracesOutput() {
		return sampleTracesOutput;
	}


	public void setSampleTracesOutput(String sampleTracesOutput) {
		this.sampleTracesOutput = sampleTracesOutput;
	}


	public ArrayList<Multimap<String, Set<String>>> getDistinctRuns() {
		return DistinctRuns;
	}


	public void setDistinctRuns(
			ArrayList<Multimap<String, Set<String>>> distinctRuns) {
		DistinctRuns = distinctRuns;
	}


	public ArrayList<Map<Integer, Integer>> getDRCount() {
		return DRCount;
	}


	public void setDRCount(ArrayList<Map<Integer, Integer>> dRCount) {
		DRCount = dRCount;
	}


	public int[] getTracesDistrib() {
		return tracesDistrib;
	}


	public void setTracesDistrib(int[] tracesDistrib) {
		this.tracesDistrib = tracesDistrib;
	}


	public int[] getRunsDistrib() {
		return runsDistrib;
	}


	public void setRunsDistrib(int[] runsDistrib) {
		this.runsDistrib = runsDistrib;
	}


	public Integer getSource() {
		return source;
	}


	public void setSource(Integer source) {
		this.source = source;
	}


	public AlphaRelations getAlphaRelations() {
		return alphaRelations;
	}


	public void setAlphaRelations(AlphaRelations alphaRelations) {
		this.alphaRelations = alphaRelations;
	}


	public String getModelName() {
		return modelName;
	}


	public void setModelName(String modelName) {
		this.modelName = modelName;
	}


	public XLog getLog() {
		return log;
	}


	public void setLog(XLog log) {
		this.log = log;
	}


	public static String getGlobalSourceLabel() {
		return GLOBAL_SOURCE_LABEL;
	}


	public static String getGlobalSinkLabel() {
		return GLOBAL_SINK_LABEL;
	}


	public static String getGlobalSinkPrimeLabel() {
		return GLOBAL_SINK_PRIME_LABEL;
	}
	
	
	
}
