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

package ee.ut.mining.log.poruns.pes;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Multimap;

import ee.ut.eventstr.PrimeEventStructure;
import ee.ut.graph.transitivity.BitsetDAGTransitivity;
import ee.ut.mining.log.poruns.PORuns;
import ee.ut.org.processmining.framework.util.Pair;

public class PORuns2PES {
	public static PrimeEventStructure<Integer> getPrimeEventStructure(PORuns runs, String modelName) {
		return getPrimeEventStructure(runs.getSuccessors(), runs.getConcurrency(), runs.getSources(),
				runs.getSinks(), runs.getLabels(), runs.getEquivalenceClasses());
	}


	public static PrimeEventStructure<Integer> getPrimeEventStructure(
			Multimap<Integer, Integer> adj, Multimap<Integer, Integer> conc,
			List<Integer> sources, List<Integer> sinks,
			Map<Integer,String> lmap, Multimap<Integer, Integer> equivalenceClasses) {
		int size = lmap.size();
		Pair<BitSet[], BitSet[]> pair
				= BitsetDAGTransitivity.transitivityDAG(adj, size, sources);
		BitSet[] causality = pair.getFirst();
		BitSet[] dcausality = pair.getSecond();

		BitSet[] invcausality = new BitSet[size];
		BitSet[] concurrency = new BitSet[size];
		BitSet[] conflict = new BitSet[size];
		List<String> labels = new ArrayList<>(size);
		
		for (int i = 0; i < size; i++) {
			invcausality[i] = new BitSet();
			concurrency[i] = new BitSet();
			conflict[i] = new BitSet();
			labels.add(lmap.get(i));
		}
		
		for (Entry<Integer, Integer> entry: conc.entries())
			concurrency[entry.getKey()].set(entry.getValue());
		
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				if (causality[i].get(j))
					invcausality[j].set(i);
		
		for (int i = 0; i < size; i++) {
			BitSet union = (BitSet) causality[i].clone();
			union.or(invcausality[i]);
			union.or(concurrency[i]);
			union.set(i); // Remove IDENTITY
			conflict[i].flip(0, size);
			conflict[i].xor(union);
		}
		
		Map<Integer, Integer> occurrences = new HashMap<Integer, Integer>();
		for (Integer event: equivalenceClasses.keySet())
			occurrences.put(event, equivalenceClasses.get(event).size());

		double[][] fmatrix = new double[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (dcausality[i].get(j))
					fmatrix[i][j] = (occurrences.get(j) + 0.0f) / occurrences.get(i);
			}
		}
		
		PrimeEventStructure<Integer> pes = 
				new PrimeEventStructure<Integer>(labels, causality, dcausality, invcausality,
						concurrency, conflict, sources, sinks, occurrences, fmatrix);
		
		return pes;
	}
	
}
