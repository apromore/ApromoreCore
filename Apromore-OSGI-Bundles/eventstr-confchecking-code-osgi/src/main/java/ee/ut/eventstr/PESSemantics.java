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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.model.XTrace;
import org.jbpt.graph.DirectedEdge;
import org.jbpt.graph.DirectedGraph;
import org.jbpt.graph.Edge;
import org.jbpt.graph.Graph;
import org.jbpt.hypergraph.abs.Vertex;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.Map.Entry;

import ee.ut.bpmn.replayer.Pomset;

public class PESSemantics <T> {
	protected PrimeEventStructure<T> pes;	
	protected Set<BitSet> cuts;
	protected Set<BitSet> maximalConfigurations;
	protected Map<BitSet, BitSet> possibleExtensions;
	protected static BitSet EMPTY = new BitSet();
	
	public PESSemantics(PrimeEventStructure<T> pes) {
		this.pes = pes;
		this.possibleExtensions = new HashMap<>();
		
		// Let's initialize the set of possible extensions for configuration "empty set"
		BitSet pext = new BitSet();
		for (Integer src: pes.sources)
			pext.set(src);
		
		BitSet allEvents = new BitSet();
		allEvents.set(0, pes.getLabels().size());
		
		this.possibleExtensions.put(EMPTY, pext);
	}
	
	public List<String> getLabels() {
		return pes.labels;
	}
	
	public String getLabel(int e1) {
		return pes.labels.get(e1);
	}

	public Set<BitSet> getCuts() {
		if (cuts == null) {
			cuts = new HashSet<>();
			BitSet _sinks = new BitSet();
			for (Integer s1: pes.sinks)
				_sinks.set(s1);
			
			while (!_sinks.isEmpty()) {
				// Select one event as the "pivot"
				int pivot = _sinks.nextSetBit(0);
				
				// Let's compute the CUT associated with event "pivot"
				// NOTE: CUT is a maximal co-set  (i.e. concurrency set)
				BitSet cut = (BitSet) pes.concurrency[pivot].clone();
				cut.set(pivot);
				cut.and(_sinks);
				
				cuts.add(cut); // Add the newly found cut
				_sinks.xor(cut);
			}
		}
		return cuts;
	}
	
	public Set<BitSet> getMaxConf() {
		if (maximalConfigurations == null) {
			maximalConfigurations = new HashSet<>();
			
			for (BitSet cut: getCuts()) {
				BitSet conf = new BitSet();
				for (int sink = cut.nextSetBit(0); sink >= 0; sink = cut.nextSetBit(sink+1)) {
					conf.or(pes.invcausality[sink]);
					conf.set(sink);
				}
				
				maximalConfigurations.add(conf);
			}
		}
		return maximalConfigurations;
	}

	public BitSet getPossibleExtensions(BitSet conf) {
		if (possibleExtensions.containsKey(conf))
			return possibleExtensions.get(conf);
		
		BitSet conflicting = new BitSet();
		BitSet concurrent = new BitSet();
		BitSet dcausal = new BitSet();
		
		for (int e = conf.nextSetBit(0); e >= 0; e = conf.nextSetBit(e+1)) {
			conflicting.or(pes.conflict[e]);
			concurrent.or(pes.concurrency[e]);
			dcausal.or(pes.dcausality[e]);
		}

		dcausal.or(concurrent);
		dcausal.andNot(conf);
		dcausal.andNot(conflicting);
		
		BitSet posExts = new BitSet();
		for (int e = dcausal.nextSetBit(0); e >= 0; e = dcausal.nextSetBit(e+1)) {
			if (isSubset(pes.invcausality[e], conf))
				posExts.set(e);
		}
		
		return posExts;
	}
	
	public BitSet getPossibleFuture(BitSet conf) {
		BitSet conflicting = new BitSet();
		
		for (int e = conf.nextSetBit(0); e >= 0; e = conf.nextSetBit(e+1))
			conflicting.or(pes.conflict[e]);

		conflicting.flip(0, pes.labels.size());
		conflicting.andNot(conf);
		
		return conflicting;
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
	
	public BitSet getDirectSuccessors(Integer e1) {
		// TODO Auto-generated method stub
		if (e1 >= pes.dcausality.length) System.out.println("Problem trace: " + e1);
		return pes.dcausality[e1];
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

	public HashSet<String> getConfigurationLabels(BitSet c1) {
		HashSet<String> labels = new HashSet<>();
		
		for (int i = c1.nextSetBit(0); i >= 0; i = c1.nextSetBit(i+1)) 
		     labels.add(getLabel(i));
		
		return labels;
	}

	public HashSet<String> getCyclicTasks() {
		return this.pes.getCyclicTasks();
	}

	public List<String> getLabels(Set<Integer> next) {
		List<String> labels = new LinkedList<>();
		
		for(Integer evt : next)
			labels.add(getLabel(evt));
		
		return labels;
	}

	public Set<Integer> getEvents(BitSet bs) {
		Set<Integer> events = new HashSet<>();

		for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1))
			events.add(i);

		return events;
	}

	public Pomset getPomset(BitSet bs) {
		DirectedGraph confgraph = new DirectedGraph();
		BiMap<Vertex, Integer> map = HashBiMap.<Vertex, Integer> create();
		HashMap<Integer, String> labels = new HashMap<>();
		
		for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) 
			if(!getLabel(i).equals("_0_") && !getLabel(i).equals("_1_")){
				Vertex v = new Vertex(getLabel(i));
				map.put(v, i);
				labels.put(i, getLabel(i));
			}

		HashSet<Integer> conf = bS2int(bs);
		for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) 
			if(!(getLabel(i).equals("_0_") && getDirectPredecessors(i).isEmpty()) && !(getLabel(i).equals("_1_") && getDirectSuccessors(i).isEmpty())){
				BitSet pred = getOrderBS(conf, i);
	
				for (int j = pred.nextSetBit(0); j >= 0; j = pred.nextSetBit(j + 1))
					if(!getLabel(j).equals("_0_") && !getLabel(j).equals("_1_")){
						confgraph.addEdge(map.inverse().get(j), map.inverse().get(i));
					}
			}

		return new Pomset(confgraph, map, labels);
	}
	
	public Pomset getPomset(BitSet conf1, BitSet conf1a) {
		Pomset po1 = getPomset(conf1);
		Pomset po2 = getPomset(conf1a);
		
		DirectedGraph confgraph = new DirectedGraph();
		BiMap<Vertex, Integer> map = HashBiMap.<Vertex, Integer> create();
		HashMap<Integer, String> labels = new HashMap<>();
		
		for(Entry<Vertex, Integer> entry: po1.getMap().entrySet()){
			Vertex v = new Vertex(getLabel(entry.getValue()));
			map.put(v, entry.getValue());
			labels.put(entry.getValue(), getLabel(entry.getValue()));
		}
		
		for(Entry<Vertex, Integer> entry: po2.getMap().entrySet())
			if(!map.inverse().containsKey(entry.getValue())){
				Vertex v = new Vertex(getLabel(entry.getValue()));
				map.put(v, entry.getValue());
				labels.put(entry.getValue(), getLabel(entry.getValue()));
			}
		
		for(DirectedEdge de : po1.getGraph().getEdges()){
			Vertex v1 = map.inverse().get(po1.getMap().get(de.getSource()));
			Vertex v2 = map.inverse().get(po1.getMap().get(de.getTarget()));
			confgraph.addEdge(v1, v2);
		}
		
		for(DirectedEdge de : po2.getGraph().getEdges()){
			Vertex v1 = map.inverse().get(po2.getMap().get(de.getSource()));
			Vertex v2 = map.inverse().get(po2.getMap().get(de.getTarget()));
			confgraph.addEdge(v1, v2);
		}
		
		return new Pomset(confgraph, map, labels);
	}
	
	public HashSet<Integer> bS2int(BitSet bs) {
		HashSet<Integer> indexes = new HashSet<>();

		for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1))
			indexes.add(i);

		return indexes;
	}
	
	public BitSet getOrderBS(Set<Integer> evts, int i) {
		BitSet causes = new BitSet(pes.labels.size());
		
		for(Integer p : evts)
			if(pes.causality[p].get(i))
				causes.set(p);
		
		return causes;
	}

	public boolean arePossibleExtensions(BitSet conf1, BitSet exts) {
		
		for (int i = conf1.nextSetBit(0); i >= 0; i = conf1.nextSetBit(i+1)) 
			if(!areConsistent(i, exts))
			return false;

	return true;
	}
	
	public boolean areConsistent(Integer evt, BitSet exts) {
		
		for (int i = exts.nextSetBit(0); i >= 0; i = exts.nextSetBit(i+1))
			if (this.pes.conflict[evt].get(i))
				return false;

		return true;
	}

    public Set<XTrace> getTracesOf(Integer target) {
        Set<XTrace> traces = new HashSet<>();

        for(String idTrace : this.pes.getTracesOf(target))
            traces.add(this.pes.getXTrace(idTrace));

		return traces;
    }
}
