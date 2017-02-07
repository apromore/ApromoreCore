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

package ee.ut.eventstr.folding;

import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

//import de.hpi.bpt.utils.IOUtils;

import com.google.common.collect.Multimap;
import com.google.gwt.dev.util.collect.HashSet;

import ee.ut.eventstr.FutureEquivalenceAnalyzer;
import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.PrimeEventStructure;

public class SuffixMerger {
	private PESSemantics<Integer> pessem;
	private PrimeEventStructure<Integer> pes;

	private Map<BitSet, BitSet> possibleExtensions;
	
	public SuffixMerger(PESSemantics<Integer> pessem) {
		this.pessem = pessem;
		this.pes = pessem.getPES();
		perform();
	}

	private void perform() {
		Multimap<Integer, Integer> feq = new FutureEquivalenceAnalyzer<Integer>(pessem).getFutureEquivalences();
		
//		possibleExtensions = new HashMap<>();
//		
//		BitSet emptyConf = new BitSet();
//		possibleExtensions.put(emptyConf, pessem.getPossibleExtensions(emptyConf));
		
		BitSet visitedEvents = new BitSet();
		BitSet pivots = new BitSet();
		Set<BitSet> visited = new HashSet<>();
		Map<Integer, Integer> event2pivots = new HashMap<>();
		Queue<BitSet> open = new LinkedList<>();
		
		open.offer(new BitSet());
		
		while (!open.isEmpty()) {
			BitSet curr = open.poll();
			visited.add(curr);
			System.out.println("Working with : " + curr);
			BitSet pe = pessem.getPossibleExtensions(curr);
			for (int e = pe.nextSetBit(0); e >= 0; e = pe.nextSetBit(e + 1)) {
				if (!visitedEvents.get(e)) {
					pivots.set(e);
					for (Integer ep: feq.get(e)) {
						visitedEvents.set(ep);
						event2pivots.put(ep, e);
					}
				}
				
				BitSet succ = (BitSet)curr.clone();
				succ.set(e);
				if (!(visited.contains(succ) || open.contains(succ)))
					open.add(succ);
				
//				IOUtils.toFile("punf.dot", pes.toDot(pivots, new BitSet()));
			}
		}		
	}
}
