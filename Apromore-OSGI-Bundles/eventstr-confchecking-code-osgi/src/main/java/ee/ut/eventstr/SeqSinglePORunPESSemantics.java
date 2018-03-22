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

package ee.ut.eventstr;

import java.io.PrintWriter;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SeqSinglePORunPESSemantics <T> {
	protected PrimeEventStructure<T> pes;	
	protected Set<BitSet> cuts;
	protected Set<BitSet> maximalConfigurations;
	protected BitSet maxConf;
	protected Map<BitSet, BitSet> possibleExtensions;
	protected static BitSet EMPTY = new BitSet();
	
	public SeqSinglePORunPESSemantics(PrimeEventStructure<T> pes, Integer sink) {
		this.pes = pes;
		this.possibleExtensions = new HashMap<>();
				
		// Let's initialize the set of possible extensions for configuration "empty set"
		BitSet pext = new BitSet();
		for (Integer src: pes.sources)
			pext.set(src);
		
		maxConf = getLocalConfiguration(sink);
		maximalConfigurations = new HashSet<>();
		maximalConfigurations.add(maxConf);
		
		this.possibleExtensions.put(EMPTY, pext);
	}
	
	public List<String> getLabels() {
		return pes.labels;
	}
	
	public String getLabel(int e1) {
		return pes.labels.get(e1);
	}

	public Set<BitSet> getMaxConf() {
		return maximalConfigurations;
	}

	public BitSet getPossibleExtensions(BitSet conf) {
		BitSet pext = possibleExtensions.get(conf);
		if (pext == null) {
			pext = new BitSet();
		
			for (int e = conf.nextSetBit(0); e >= 0; e = conf.nextSetBit(e+1))
				pext.or(pes.dcausality[e]);
	
			pext.and(maxConf);
			pext.andNot(conf);
			
			possibleExtensions.put(conf, pext);
		}
		
		if (!pext.isEmpty()) {
			BitSet restPosExt = new BitSet();
			restPosExt.set(pext.nextSetBit(0));
			return restPosExt;
		}
		
		return pext;
	}
	
	public BitSet getPossibleFuture(BitSet conf) {
		BitSet possibleFuture = (BitSet)maxConf.clone();
		possibleFuture.andNot(conf);
		
		return possibleFuture;
	}
	
	public Set<String> getPossibleFutureAsLabels(BitSet conf) {
		BitSet future = getPossibleFuture(conf);
		
		Set<String> flabels = new HashSet<>();
		for (int e = future.nextSetBit(0); e >= 0; e = future.nextSetBit(e+1))
			flabels.add(pes.labels.get(e));
		
		return flabels;
	}

	public BehaviorRelation getBRelation(int e1, int e2) {
		return pes.getBRelMatrix()[e1][e2];
	}

	public BitSet getDirectPredecessors(int e) {
		BitSet pred = new BitSet();
		for (int i = 0; i < pes.labels.size(); i++)
			if (pes.dcausality[i].get(e))
				pred.set(i);
		return pred;
	}	
	
	public BitSet getLocalConfiguration(int e) {
		BitSet conf = (BitSet)pes.invcausality[e].clone();
		conf.set(e);
		return conf;
	}

	public BitSet getStrictCausesOf(int e) {
		return pes.invcausality[e];
	}
	
	public BitSet getConflictSet(int e) {
		return pes.conflict[e];
	}

	public Set<Integer> getMaxima(BitSet conf) {
		Set<Integer> maxima = new HashSet<>();
		while (!conf.isEmpty()) {
			int max = conf.length() - 1;
			maxima.add(max);
			
			BitSet copy = (BitSet)conf.clone();
			copy.clear(max);
			copy.andNot(pes.invcausality[max]);
			
			conf = copy;
		}
		return maxima;
	}
	
	public void toDot(PrintWriter out, Set<Integer> set) {		
		out.println("\tnode[shape=box];");
		for (Integer i : set)
			out.printf("\tn%d [label=\"%s(%d)\"];\n", i, pes.labels.get(i), i);
		
		for (Integer src: set)
			for (Integer tgt: set) {
				if (pes.dcausality[src].get(tgt))
					out.printf("\tn%d -> n%d;\n", src, tgt);
			}		
	}

	
	// ==============================================================
	// ======= BitSet convenience set operations
	// ==============================================================
	public BitSet union(BitSet a, BitSet b) {
		BitSet set = (BitSet)a.clone();
		set.or(b);
		return set;
	}
	public BitSet intersection(BitSet a, BitSet b) {
		BitSet set = (BitSet)a.clone();
		set.and(b);
		return set;
	}
	public boolean isSubset(BitSet a, BitSet b) {
		BitSet set = (BitSet)a.clone();
		set.and(b);
		return set.cardinality() == a.cardinality();		
	}

	public PrimeEventStructure<T> getPES() {
		return pes;
	}
}
