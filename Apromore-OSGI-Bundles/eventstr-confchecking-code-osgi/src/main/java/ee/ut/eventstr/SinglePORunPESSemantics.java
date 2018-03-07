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
import java.io.StringWriter;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SinglePORunPESSemantics <T>{
	protected PrimeEventStructure<T> pes;	
	protected Set<BitSet> cuts;
	protected Set<BitSet> maximalConfigurations;
	protected BitSet maxConf;
	protected Map<BitSet, BitSet> possibleExtensions;
	protected static BitSet EMPTY = new BitSet();
	
	public SinglePORunPESSemantics(PrimeEventStructure<T> pes, Integer sink) {
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

	public HashSet<String> getLabels(BitSet events) {
		HashSet<String> labels = new HashSet<>();
		for (int e = events.nextSetBit(0); e >= 0; e = events.nextSetBit(e+1))
			labels.add(pes.labels.get(e));

		return labels;
	}

	public String getLabel(int e1) {
		return pes.labels.get(e1);
	}

	public Set<BitSet> getMaxConf() {
		return maximalConfigurations;
	}

	public BitSet getPossibleExtensions(BitSet conf) {
		if (possibleExtensions.containsKey(conf))
			return possibleExtensions.get(conf);
		
		BitSet dcausal = new BitSet();
		
		for (int e = conf.nextSetBit(0); e >= 0; e = conf.nextSetBit(e+1))
			dcausal.or(pes.dcausality[e]);

		dcausal.and(maxConf);
		dcausal.andNot(conf);
		
		BitSet result = new BitSet();
		
		for (int e = dcausal.nextSetBit(0); e >= 0; e = dcausal.nextSetBit(e+1)) {
			BitSet causes = (BitSet)pes.invcausality[e].clone();
			causes.andNot(conf);
			if (causes.isEmpty())
				result.set(e);
		}
		
		possibleExtensions.put(conf, result);
		
		return result;
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
	
	public BitSet getDirectConflictSet(int e) {
		BitSet preds = getDirectPredecessors(e);
		BitSet dconf = new BitSet();
		for (int pred = preds.nextSetBit(0); pred >=0; pred = preds.nextSetBit(pred + 1)) {
			BitSet succs = (BitSet)pes.dcausality[pred].clone();
			succs.and(pes.conflict[e]);
			
			dconf.or(succs);
		}
		return dconf;
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

	public String toDot() {
		StringWriter str = new StringWriter();
		PrintWriter out = new PrintWriter(str);
		out.println("digraph G {");
		out.println("\tnode[shape=box];");
		for (int i = maxConf.nextSetBit(0); i >= 0; i = maxConf.nextSetBit(i + 1))
			out.printf("\tn%d [label=\"%s(%d)\"];\n", i, pes.labels.get(i), i);
		
		for (int src = maxConf.nextSetBit(0); src >= 0; src = maxConf.nextSetBit(src + 1))
			for (int tgt = maxConf.nextSetBit(0); tgt >= 0; tgt = maxConf.nextSetBit(tgt + 1))
				if (pes.dcausality[src].get(tgt))
					out.printf("\tn%d -> n%d;\n", src, tgt);
		out.println("}");
		return str.toString();
	}
}
