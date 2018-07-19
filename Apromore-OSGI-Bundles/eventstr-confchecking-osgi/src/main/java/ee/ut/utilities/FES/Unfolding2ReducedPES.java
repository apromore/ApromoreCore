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

package ee.ut.utilities.FES;

import hub.top.uma.DNode;
import hub.top.uma.DNodeBP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import de.hpi.bpt.utils.IOUtils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import ee.ut.eventstr.PrimeEventStructure;
import ee.ut.graph.cliques.CCliqueFinder;
import ee.ut.graph.transitivity.BitsetDAGTransitivity;
import ee.ut.graph.transitivity.MatrixBasedTransitivity;
import ee.ut.nets.unfolding.BPstructBP;
import ee.ut.nets.unfolding.BPstructBPSys;
import ee.ut.org.processmining.framework.util.Pair;

public class Unfolding2ReducedPES {
	private BPstructBPSys sys;
	private Set<String> visibleLabels;
	private List<String> labels;
	private Map<DNode, Integer> orderedVisibleEventMap;
	private List<DNode> orderedVisibleEvents;
	private PrimeEventStructure<Integer> pes;
	private ReducedPrimeEventStructure<Integer> rpes;
	private Set<Integer> cutoffEvents;
	private Map<Integer, Integer> cutoffCorrespondingMap;
	private Set<Integer> invisibleEvents;
	private Set<Integer> terminalEvents;
	

	public Unfolding2ReducedPES(BPstructBPSys sys, BPstructBP bp, Set<String> visibleLabels) {
		this.sys = sys;
		this.orderedVisibleEventMap = new HashMap<>();
		this.orderedVisibleEvents = new ArrayList<>();
		this.cutoffCorrespondingMap = new HashMap<>();
		this.cutoffEvents = new HashSet<>();
		this.invisibleEvents = new HashSet<>();
		this.terminalEvents = new HashSet<>();

		this.labels = new ArrayList<>();
		
		this.visibleLabels = new HashSet<>(visibleLabels);
		
		int numberOfEvents = bp.getBranchingProcess().allEvents.size();
		int numberOfConditions = bp.getBranchingProcess().allConditions.size();
		int fullSize = numberOfConditions + numberOfEvents + 3;
		boolean[][] matrix = new boolean[fullSize][fullSize];

		Map<DNode, Integer> localMap = new HashMap<>();
		Set<DNode> visibleEvents = new HashSet<>();
		Set<DNode> cutoffDNodes = new HashSet<>();
		
		int localId = 0;
		for (DNode node: bp.getBranchingProcess().allEvents) {
			localMap.put(node, localId++);
			if (visibleLabels.contains(sys.properNames[node.id]) || node.isCutOff) {
				visibleEvents.add(node);
				orderedVisibleEvents.add(node);
				orderedVisibleEventMap.put(node, orderedVisibleEventMap.size());
				labels.add(sys.properNames[node.id]);
			}
			if (node.isCutOff)
				cutoffDNodes.add(node);
		}
				
		for (DNode cutoff: cutoffDNodes) {
			DNode corresponding = bp.getCutOffEquivalentEvent().get(cutoff);
			if (!visibleEvents.contains(corresponding)) {
				visibleEvents.add(corresponding);
				orderedVisibleEvents.add(corresponding);
				orderedVisibleEventMap.put(corresponding, orderedVisibleEventMap.size());
				labels.add(sys.properNames[corresponding.id]);				
			}
			cutoffCorrespondingMap.put(orderedVisibleEventMap.get(cutoff), orderedVisibleEventMap.get(corresponding));
			//System.out.printf("Cutoff: %s, Corresponding: %s\n", sys.properNames[cutoff.id], sys.properNames[corresponding.id]);
		}
		
		localId = numberOfEvents;
		for (DNode node: bp.getBranchingProcess().allConditions)
			localMap.put(node, localId++);
		
		for (DNode node: bp.getBranchingProcess().allEvents) {
			for (DNode pred: node.pre)
				matrix[localMap.get(pred)][localMap.get(node)] = true;
			for (DNode succ: node.post)
				matrix[localMap.get(node)][localMap.get(succ)] = true;
		}

		MatrixBasedTransitivity.transitiveClosure(matrix);
		
		for (DNode node: bp.getBranchingProcess().allEvents) {
			if (visibleEvents.contains(node)) continue;
			Arrays.fill(matrix[localMap.get(node)], false);
		}
		MatrixBasedTransitivity.transitiveReduction(matrix, numberOfEvents);
		
		Multimap<Integer, Integer> adj = HashMultimap.create();
		Map<Integer, BitSet> preConcurrency = new HashMap<>();
		Multimap<Integer, Integer> preConcurrencyPrime = HashMultimap.create();
		for (DNode n1: visibleEvents) {
			int _n1 = orderedVisibleEventMap.get(n1);
			preConcurrency.put(_n1, new BitSet());
			for (DNode n2: visibleEvents)
				if (!n1.equals(n2)) {
					int _n2 = orderedVisibleEventMap.get(n2);					
					if (matrix[localMap.get(n1)][localMap.get(n2)]) {
						adj.put(_n1, _n2);
					} else if (//_n1 < _n2 && 
							bp.areConcurrent_struct(n1, n2) == DNodeBP.REL_CO) {
						if (!preConcurrency.containsKey(_n2))
							preConcurrency.put(_n2, new BitSet());
						preConcurrency.get(_n1).set(_n2);
						preConcurrency.get(_n2).set(_n1);
						preConcurrencyPrime.put(_n1, _n2);
						preConcurrencyPrime.put(_n2, _n1);
					}
				}
		}
		
		Set<Integer> sources = new HashSet<>(adj.keySet());
		sources.removeAll(adj.values());
		Set<Integer> sinks = new HashSet<>(adj.values());
		sinks.removeAll(adj.keySet());
		
		// =================
		// TODO: Partition the set of sources and the set of sinks into cosets
		// =================
		int finalSize = visibleEvents.size();
		int artificialStartEvent = finalSize;
		
		
		for (Integer source: sources) {
			adj.put(finalSize++, source);
			labels.add("_0_");
		}
		
		Set<Integer> visibleSinkEvents = new HashSet<>();
		for (Integer sink: sinks) {
			DNode node = orderedVisibleEvents.get(sink);
			for (DNode cond: node.post)
				if (sys.isTerminal(cond))
					visibleSinkEvents.add(sink);
		}

		Set<Set<Integer>> sinkCoSets = new CCliqueFinder(visibleSinkEvents, preConcurrencyPrime, HashMultimap.<Integer, Integer>create(), preConcurrencyPrime).getAllMaximalCliques();
		
		for (Set<Integer> coset: sinkCoSets) {
			terminalEvents.add(finalSize);
			for (Integer sink: coset)
				adj.put(sink, finalSize);
			finalSize++;
			labels.add("_1_");
		}

		Pair<BitSet[], BitSet[]> pair = BitsetDAGTransitivity.transitivityDAG(adj, labels.size(), Collections.singleton(artificialStartEvent));
		BitSet[] causality = pair.getFirst();
		BitSet[] dcausality = pair.getSecond();
		BitSet[] invcausality = new BitSet[finalSize];
		BitSet[] conflict = new BitSet[finalSize];
		BitSet[] concurrency = new BitSet[finalSize];

		for (int i = 0; i < finalSize; i++) {
			invcausality[i] = new BitSet();
			conflict[i] = new BitSet();
			if (preConcurrency.containsKey(i))
				concurrency[i] = preConcurrency.get(i);
			else
				concurrency[i] = new BitSet();
		}
		
		for (int i = 0; i < finalSize; i++)
			for (int j = causality[i].nextSetBit(0); j >= 0; j = causality[i].nextSetBit(j + 1))
				invcausality[j].set(i);

		for (int i = 0; i < finalSize; i++) {
			BitSet union = (BitSet) causality[i].clone();
			union.or(invcausality[i]);
			union.or(concurrency[i]);
			union.set(i); // Remove IDENTITY
			conflict[i].flip(0, finalSize);
			conflict[i].xor(union);
		}
		
		pes = new PrimeEventStructure<Integer>(labels, causality, dcausality, invcausality,
						concurrency, conflict, Arrays.asList(artificialStartEvent), new ArrayList<>(sinks));
		
		rpes = new ReducedPrimeEventStructure<Integer>(labels, causality, dcausality, invcausality,
				concurrency, conflict, Arrays.asList(artificialStartEvent), new ArrayList<>(sinks));

		for (DNode cutoff: cutoffDNodes)
			cutoffEvents.add(orderedVisibleEventMap.get(cutoff));
		
		for (int e = 0; e < labels.size(); e++)
			if (visibleLabels.contains(labels.get(e)))
				invisibleEvents.add(e);				
		
//		IOUtils.toFile("pes.dot", pes.toDot());
	}

	public Set<Integer> getInvisibleEvents() {
		return invisibleEvents;
	}

	public PrimeEventStructure<Integer> getPES() {
		return pes;
	}
	
	public ReducedPrimeEventStructure<Integer> getRPES() {
		return rpes;
	}

	public Set<Integer> getCutoffEvents() {
		return cutoffEvents;
	}
	
	public boolean isVisible(int event) {
		DNode dnode = orderedVisibleEvents.get(event);
		return visibleLabels.contains(sys.properNames[dnode.id]);
	}


	public int getCorrespondingEvent(int ev) {
		return cutoffCorrespondingMap.get(ev);
	}
	
	public Set<Integer> getTerminalEvents() {
		return terminalEvents;
	}
}
