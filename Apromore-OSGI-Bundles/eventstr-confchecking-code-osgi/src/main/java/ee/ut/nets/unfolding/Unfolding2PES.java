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

package ee.ut.nets.unfolding;

import hub.top.petrinet.Node;
import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Transition;
import hub.top.uma.DNode;
import hub.top.uma.DNodeBP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

//import de.hpi.bpt.utils.IOUtils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import ee.ut.eventstr.PrimeEventStructure;
import ee.ut.graph.cliques.CCliqueFinder;
import ee.ut.graph.transitivity.BitsetDAGTransitivity;
import ee.ut.graph.transitivity.MatrixBasedTransitivity;
import ee.ut.org.processmining.framework.util.Pair;
import hub.top.uma.DNodeSet;

public class Unfolding2PES {
	private BPstructBPSys sys;
	private BPstructBP bp;
	private Set<String> visibleLabels;
	private List<String> labels;
	private Map<DNode, Integer> orderedVisibleEventMap;
	private List<DNode> orderedVisibleEvents;
	private PrimeEventStructure<Integer> pes;
	private Set<Integer> cutoffEvents;
	private Map<Integer, Integer> cutoffCorrespondingMap;
	private Set<Integer> invisibleEvents;
	private Set<Integer> terminalEvents;
	private Map<Integer, BiMap<Integer, Integer>> isomorphism;
	private HashSet<String> cyclicTasks;
	private BiMap<DNode, Integer> mapEventsBP2ES;
    private HashMap<Integer, DNode> mapEventsPES2Unf;

	public Unfolding2PES(Unfolder_PetriNet unfolder, Set<String> originalVisibleLabels, HashMap<String, String> originalNames) {
		this.sys = unfolder.getSys();
		this.bp = unfolder.getBP();
		this.orderedVisibleEventMap = new HashMap<>();
		this.orderedVisibleEvents = new ArrayList<>();
		this.cutoffCorrespondingMap = new HashMap<>();
		this.cutoffEvents = new HashSet<>();
		this.invisibleEvents = new HashSet<>();
		this.terminalEvents = new HashSet<>();
		this.cyclicTasks = new HashSet<>();

		this.labels = new ArrayList<>();
		
		this.visibleLabels = new HashSet<>(originalVisibleLabels);
		this.mapEventsBP2ES = HashBiMap. <DNode, Integer>create();
        this.mapEventsPES2Unf = new HashMap<>();

		int numberOfEvents = bp.getBranchingProcess().allEvents.size();
		int numberOfConditions = bp.getBranchingProcess().allConditions.size();
		int fullSize = numberOfConditions + numberOfEvents + 3;
		boolean[][] matrix = new boolean[fullSize][fullSize];

		Map<DNode, Integer> localMap = new HashMap<>();
		Set<DNode> visibleEvents = new HashSet<>();
		Set<DNode> cutoffDNodes = new HashSet<>();
		Map<Integer, DNode> invisibleSinks = new HashMap<>();
		Set<DNode> visibleSinks = new HashSet<>();
		
		int localId = 0;
		for (DNode node: bp.getBranchingProcess().allEvents) {
			String originalName = unfolder.getOriginalLabel(originalNames.get(sys.properNames[node.id]));
			
			localMap.put(node, localId++);

			boolean atLeastOneTerminal = false;
			boolean atLeastOneNonTerminal = false;

			for (DNode cond: node.post) {
				if (sys.isTerminal(cond))
					atLeastOneTerminal = true;
				else
					atLeastOneNonTerminal = true;
			}
			
			boolean sinkEvent = atLeastOneTerminal & !atLeastOneNonTerminal;

			if (originalVisibleLabels.contains(originalName) || node.isCutOff || sinkEvent) {
				visibleEvents.add(node);
				orderedVisibleEvents.add(node);
				if (!originalVisibleLabels.contains(originalName) && sinkEvent)
					invisibleSinks.put(orderedVisibleEventMap.size(), node);

				orderedVisibleEventMap.put(node, orderedVisibleEventMap.size());
				this.mapEventsBP2ES.put(node, labels.size());

                if(originalVisibleLabels.contains(originalName))
                    this.mapEventsPES2Unf.put(labels.size(), node);

                labels.add(originalNames.get(sys.properNames[node.id]));
			}
			
			if (sinkEvent && node.isCutOff)
				visibleSinks.add(node);
			
			if (node.isCutOff)
				cutoffDNodes.add(node);
		}
				
		for (DNode cutoff: cutoffDNodes) {
			DNode corresponding = bp.getCutOffEquivalentEvent().get(cutoff);
			if (!visibleEvents.contains(corresponding)) {
				String originalName = unfolder.getOriginalLabel(sys.properNames[corresponding.id]);
				visibleEvents.add(corresponding);
				orderedVisibleEvents.add(corresponding);
				orderedVisibleEventMap.put(corresponding, orderedVisibleEventMap.size());
				labels.add(originalName);				
			}
			if (!visibleSinks.contains(cutoff)) {
				cutoffCorrespondingMap.put(orderedVisibleEventMap.get(cutoff), orderedVisibleEventMap.get(corresponding));
				//System.out.printf("Cutoff: %s, Corresponding: %s\n", unfolder.getOriginalLabel(sys.properNames[cutoff.id]), unfolder.getOriginalLabel(sys.properNames[corresponding.id]));
			}
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
			Set<Integer> intersection = new HashSet<>(invisibleSinks.keySet());
			intersection.retainAll(coset);
			if (intersection.isEmpty()) {
				terminalEvents.add(finalSize);
				for (Integer sink: coset)
					adj.put(sink, finalSize);
				finalSize++;
				labels.add("_1_");
			} else {
				if (coset.size() > 1)
					throw new RuntimeException("Something wrong with this model: ");
				terminalEvents.add(coset.iterator().next());
				labels.set(coset.iterator().next(), "_1_");
			}
		}
		sinks = terminalEvents;

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
		
		for(int i = 0; i < labels.size(); i++)
			for(int j = 0; j < labels.size(); j++)
				if(i != j && labels.get(i).equals(labels.get(j)) && (causality[i].get(j) || causality[j].get(i)))
					cyclicTasks.add(labels.get(i));
		
		for (DNode cutoff: cutoffDNodes) {
			if (!visibleSinks.contains(cutoff))
				cutoffEvents.add(orderedVisibleEventMap.get(cutoff));
		}
		
		visibleLabels.add("_1_");
		visibleLabels.add("_0_");

		for (int e = 0; e < labels.size(); e++)
			if (!visibleLabels.contains(labels.get(e)))
				invisibleEvents.add(e);				
		
//		IOUtils.toFile("pes.dot", pes.toDot());
				
		isomorphism = new HashMap<>();
		Map<DNode, BiMap<DNode, DNode>> _isomorphism = computeIsomorphism();
		for (DNode _cutoff: _isomorphism.keySet()) {
			Integer cutoff = orderedVisibleEventMap.get(_cutoff);
			BiMap<Integer, Integer> bimap = HashBiMap.create();
			for (Entry<DNode, DNode> entry: _isomorphism.get(_cutoff).entrySet()) {
				Integer e = orderedVisibleEventMap.get(entry.getKey());
				Integer ep = orderedVisibleEventMap.get(entry.getValue());
				if (e != null && ep != null && !e.equals(ep))
					bimap.put(e, ep);
			}
			isomorphism.put(cutoff, bimap);
		}
//		System.out.println(">> " + isomorphism+" ------ ");
	}

	public Map<Integer, BiMap<Integer, Integer>> getIsomorphism() {
		return isomorphism;
	}
	
	private Map<DNode, BiMap<DNode, DNode>> computeIsomorphism() {
		Queue<Pair<DNode, DNode>> q = new LinkedList<>();
		Map<DNode, BiMap<DNode, DNode>> _isomorphism = new HashMap<>();

		for (Entry<DNode,DNode> entry: bp.getElementary_ccPair().entrySet()) {
			DNode cutoff = entry.getKey();
			DNode corr = entry.getValue();
			Map<Short, DNode> mmap = new HashMap<>();
			BiMap<DNode, DNode> bimap = HashBiMap.create();
			
			DNode[] cutoffCUT = 
					bp.getBranchingProcess().getPrimeCut(cutoff, false, false);
			DNode[] corrCUT = 
					bp.getBranchingProcess().getPrimeCut(corr, false, false);
			
			for (DNode b: cutoffCUT)
				mmap.put(b.id, b);
			for (DNode bp: corrCUT) {
				DNode b = mmap.get(bp.id);
				q.offer(new Pair<>(b, bp));
				bimap.put(b, bp);
			}
			
			System.out.println("Current bimap: " + bimap);
						
			while (!q.isEmpty()) {
				Pair<DNode, DNode> pair = q.poll();
				
				System.out.println(pair);
				
				if (pair.getFirst().post == null || pair.getSecond().post == null)
					continue;
				
//				boolean processed = true;
				for (DNode yp: pair.getSecond().post) {
					boolean enabled = true;
					for (DNode zp: yp.pre)
						if (!bimap.containsValue(zp)) {
							enabled = false;
							break;
						}
					if (enabled) {
						for (DNode y: pair.getFirst().post) {
							if (y.id == yp.id) {
								enabled = true;
								for (DNode z: y.pre)
									if (!bimap.containsKey(z)) {
										enabled = false;
										break;
									}
								if (enabled) {
									q.offer(new Pair<>(y, yp));
									bimap.put(y, yp);
								}
							}
						}
					}
				}
//				if (!processed)
//					q.offer(pair);
			}
			_isomorphism.put(cutoff, bimap);
		}
		return _isomorphism;
	}

	public Set<Integer> getInvisibleEvents() {
		return invisibleEvents;
	}

	public PrimeEventStructure<Integer> getPES() {
		pes.setCyclicTasks(this.cyclicTasks);
		return pes;
	}

	public Set<Integer> getCutoffEvents() {
		return cutoffEvents;
	}
	
	public boolean isVisible(int event) {
		DNode dnode = orderedVisibleEvents.get(event);
		return visibleLabels.contains(sys.properNames[dnode.id]);
	}

	public Integer getCorrespondingEvent(int ev) {
		return cutoffCorrespondingMap.get(ev);
	}
	
	public Set<Integer> getTerminalEvents() {
		return terminalEvents;
	}

	public HashSet<String> getCyclicTasks() {
		return cyclicTasks;
	}

    public BiMap<DNode, Integer> getMapEventsBP2ES(){ return  mapEventsBP2ES; }

    public HashMap<Integer, DNode> getMapEventsPES2Unf() {
        return mapEventsPES2Unf;
    }
}
