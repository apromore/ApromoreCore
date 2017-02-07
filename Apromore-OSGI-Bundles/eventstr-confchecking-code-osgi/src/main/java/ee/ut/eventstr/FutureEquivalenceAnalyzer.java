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

package ee.ut.eventstr;

import java.util.BitSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class FutureEquivalenceAnalyzer <T> {
	PESSemantics<T> pess;
	Multimap<Integer, Integer> dpredecessors;
	Multimap<Integer, Integer> dsuccessors;	
	Multimap<String, Integer> labelPartitions;
	Multimap<Integer, Integer> futureEquivalences;
	private List<String> labels;
	
	
	public FutureEquivalenceAnalyzer(PESSemantics<T> pess) {
		this.pess = pess;
		this.labels = pess.pes.labels;
		
		initializeDataStructures();
		perform();
	}

	private void initializeDataStructures() {
		dpredecessors = HashMultimap.create();
		dsuccessors = HashMultimap.create();
		labelPartitions = HashMultimap.create();
		
		for (int src = 0; src < labels.size(); src++) {
			BitSet dcausalityBS = pess.pes.dcausality[src];
			for (int tgt = dcausalityBS.nextSetBit(0); tgt >= 0; tgt = dcausalityBS.nextSetBit(tgt + 1)) {
				dpredecessors.put(tgt, src);
				dsuccessors.put(src, tgt);
			}
			labelPartitions.put(labels.get(src), src);
		}
	}
	
	public void perform() {
		futureEquivalences = HashMultimap.create();
		Set<Integer> open = new LinkedHashSet<>();
		Set<Integer> processed = new HashSet<>();

		for (int e = 0; e < labels.size(); e++)
			futureEquivalences.put(e, e);
		
		Set<Integer> sinks = new HashSet<>(pess.pes.getSinks());

		while (!sinks.isEmpty()) {
			Integer pivot = sinks.iterator().next();
			Set<Integer> candidates = new HashSet<>(labelPartitions.get(labels.get(pivot)));
			candidates.retainAll(sinks);
			for (Integer ev: candidates) {
				futureEquivalences.putAll(ev, candidates);
				open.addAll(dpredecessors.get(ev));
			}
			processed.addAll(candidates);
			sinks.removeAll(candidates);
		}
		
		while (!open.isEmpty()) {
			Integer pivot = open.iterator().next();
			Set<Integer> candidates = new HashSet<>(labelPartitions.get(labels.get(pivot)));
			Set<Integer> confirmed = new HashSet<>();
			
			System.out.printf("Working with: %s (%d)\n", labels.get(pivot), pivot);
			System.out.printf("\tCandidates: %s\n", candidates);

			for (Integer e1 : candidates) {
				if (processed.containsAll(dsuccessors.get(e1))) {
					Set<Integer> futureOfE1 = new HashSet<>(dsuccessors.get(e1));
					
					for (Integer e2 : candidates) {
						if (futureEquivalences.containsEntry(e1, e2)) continue;
						
						int futureEquivalenceCount = 0;
						for (Integer succE2 : dsuccessors.get(e2)) {
							if (processed.contains(succE2)) {
								Set<Integer> aux = new HashSet<Integer>(futureEquivalences.get(succE2));
								aux.retainAll(futureOfE1);
								if (!aux.isEmpty())
									futureEquivalenceCount++;
							}
						}
						if (futureEquivalenceCount > 0) {
							Set<Integer> set = new HashSet<>(futureEquivalences.get(e1));
							set.addAll(futureEquivalences.get(e2));

							for (Integer toUpdate : set)
								futureEquivalences.putAll(toUpdate, set);

							confirmed.add(e1);
							confirmed.add(e2);
						}
					}
				}
			}
			
			HashSet<Integer> copy = new HashSet<Integer>(open);
			processed.addAll(confirmed);

			System.out.println("\t" + confirmed);
			if (confirmed.isEmpty())
				open.remove(pivot);
			else {
				for (Integer vertex : confirmed)
					for (Integer pred : dpredecessors.get(vertex))
						open.add(pred);

				open.removeAll(processed);
			}

			copy.removeAll(open);
			if (copy.isEmpty())
				break;
		}
	}

	public Multimap<Integer, Integer> getFutureEquivalences() {
		return futureEquivalences;
	}
}
