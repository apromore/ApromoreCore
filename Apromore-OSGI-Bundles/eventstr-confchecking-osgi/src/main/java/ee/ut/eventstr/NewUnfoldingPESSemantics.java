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
import java.util.*;
import java.util.Map.Entry;

import com.google.common.collect.*;

import ee.ut.bpmn.replayer.Pomset;
import ee.ut.nets.unfolding.Unfolding2PES;
import ee.ut.org.processmining.framework.util.Pair;
import org.jbpt.graph.DirectedGraph;
import org.jbpt.hypergraph.abs.Vertex;

public class NewUnfoldingPESSemantics<T>{
	private Unfolding2PES unfMetadata;
	private PrimeEventStructure<T> pes;
	protected Set<BitSet> maximalConfigurations;

	protected Multimap<Integer, Integer> dpredecessors;
	protected Multimap<Integer, Integer> dsuccessors;
	protected Map<BitSet, Set<Integer>> possibleExtensions;
	protected Map<Multiset<Integer>, BitSet> mappings;
	
	public NewUnfoldingPESSemantics(PrimeEventStructure<T> pes, Unfolding2PES metadata) {
		this.pes = pes;
		this.unfMetadata = metadata;
		this.possibleExtensions = new HashMap<>();
		this.mappings = new HashMap<>();
		
		this.dpredecessors = HashMultimap.create();
		this.dsuccessors = HashMultimap.create();

		for (int src = 0; src < pes.labels.size(); src++) {
			BitSet dcausalityBS = pes.dcausality[src];
			for (int tgt = dcausalityBS.nextSetBit(0); tgt >= 0; tgt = dcausalityBS.nextSetBit(tgt + 1)) {
				dpredecessors.put(tgt, src);
				dsuccessors.put(src, tgt);
			}
		}

//		IOUtils.toFile("pesp.dot", toDot());
		
		BitSet emptyShiftedConf = new BitSet();
		this.possibleExtensions.put(emptyShiftedConf, new HashSet<>(pes.sources));
		this.mappings.put(HashMultiset.<Integer> create(), emptyShiftedConf);

		computePreFuture(new BitSet());
		findElementaryCycles(HashMultiset.<Integer> create());
		System.out.println("-----------------------------------------------");
		System.out.println("Elementary cycles");
		System.out.println(cycles);		
		System.out.println("-----------------------------------------------");
		System.out.println("Futures");
		for (Entry<BitSet, BitSet> entry: futures.entrySet())
			System.out.println(entry);
		
		Set<Integer> cyclicCutoffs = new HashSet<>();
		for (Integer cutoff: unfMetadata.getCutoffEvents()) {
			Integer corr = unfMetadata.getCorrespondingEvent(cutoff);
			if (getLocalConfiguration(cutoff).get(corr))
				cyclicCutoffs.add(cutoff);
		}
		
		for (Multiset<Integer> _cycle: cycles) {
			for (Integer ev: _cycle)
				if (cyclicCutoffs.contains(ev)) {
					cutoff2CyclicEvents.putAll(ev, _cycle);
					break;
				}			
		}
		System.out.println(":: " + cutoff2CyclicEvents);
	}

	// ========================================================================
	// ==== Data structures used while identifying the set of elementary cycles
	// ========================================================================
	private Set<Multiset<Integer>> cycles = new HashSet<>();
	public Multimap<Integer, Integer> cutoff2CyclicEvents = HashMultimap.create();

	private Map<BitSet, Multiset<Integer>> reverseMap = new HashMap<>();
	protected Map<BitSet, BitSet> futures = new LinkedHashMap<>();

	private void computePreFuture(BitSet conf) {
		if (!futures.containsKey(conf)) {
			BitSet future = new BitSet();

			for (int e = conf.nextSetBit(0); e >= 0; e = conf.nextSetBit(e+1))
				future.or(pes.conflict[e]);

			future.flip(0, pes.labels.size());
			future.andNot(conf);

			for (Integer e: unfMetadata.getInvisibleEvents())
				future.clear(e);

			futures.put(conf, future);
		}
	}

	private BitSet findElementaryCycles(Multiset<Integer> v) {
		BitSet shiftedV = getShifted(v);
		reverseMap.put(shiftedV, v);
		BitSet future = futures.get(shiftedV);

		for(Integer ext: getPossibleExtensions(v)) {
			Pair<Multiset<Integer>, Boolean> pair = extend(v, ext);

			Multiset<Integer> w = pair.getFirst();
			BitSet shiftedW = getShifted(w);

			computePreFuture(shiftedW);

			if (pair.getSecond() &&  reverseMap.containsKey(shiftedW)) {
				BitSet concset = (BitSet)getConcurrencySet(ext).clone();
				concset.and(shiftedW);
				if (concset.isEmpty()) {
					Multiset<Integer> copy = HashMultiset.create(w);
					copy.removeAll(reverseMap.get(shiftedW));
					cycles.add(copy);
				}
				future.or(futures.get(shiftedW));
			} else
				future.or(findElementaryCycles(w));
		}
		reverseMap.remove(shiftedV);
		return future;
	}
	
	public Map<Multiset<Integer>, Multiset<Integer>> getFootprints() {
		Map<Multiset<Integer>,Multiset<Integer>> result = new HashMap<>();
		Set<Integer> cutoffEvents = unfMetadata.getCutoffEvents();
		for (Multiset<Integer> cycle: cycles) {
			Multiset<Integer> footprint = HashMultiset.create(cycle);
			footprint.retainAll(cutoffEvents);
			result.put(footprint, cycle);
		}
		return result;
	}
	
	public Set<Integer> getPossibleExtensions(Multiset<Integer> conf) {

		BitSet shiftedConf = getActualConf(conf);

		Set<Integer> pe = possibleExtensions.get(shiftedConf);
		if (pe == null) {
			BitSet conflicting = new BitSet();
			BitSet concurrent = new BitSet();
			BitSet dcausal = new BitSet();
			BitSet _conf = new BitSet();
			
			for (int e = shiftedConf.nextSetBit(0); e >= 0; e = shiftedConf.nextSetBit(e + 1)) {
				conflicting.or(pes.conflict[e]);
				concurrent.or(pes.concurrency[e]);
				dcausal.or(pes.dcausality[e]);
				_conf.set(e);
			}
	
			dcausal.or(concurrent);
			dcausal.andNot(_conf);
			dcausal.andNot(conflicting);
			
			pe = new HashSet<>();
			for (int e = dcausal.nextSetBit(0); e >= 0; e = dcausal.nextSetBit(e+1)) {
				if (isSubset(pes.invcausality[e], _conf))
					pe.add(e);
			}
			
			possibleExtensions.put(shiftedConf, pe);
		}
		return pe;
	}

	private BitSet getActualConf(Multiset<Integer> conf) {
        if(conf == null){
            System.out.println("Null Conf");
            return null;
        }

		BitSet baseline = mappings.get(conf);

        while(true){
            Multiset<Integer> newConf = HashMultiset.<Integer>create(bS2int(baseline));

            if(mappings.containsKey(newConf)) {
                BitSet baseline2 = mappings.get(newConf);

                if (baseline.equals(baseline2))
                    return baseline;
                baseline = baseline2;
            }
            else{
                if(newConf!=null) {
                    BitSet bs2 = getBitSet(newConf);
                    if(possibleExtensions.containsKey(bs2))
                        return bs2;
                }
                return baseline;
            }
        }
	}

    private BitSet getBitSet(Multiset<Integer> newConf) {
        BitSet bs = new BitSet();

        for(Integer i : newConf)
            bs.set(i);

        return bs;
    }

    public Pair<Multiset<Integer>, Boolean> extend(Multiset<Integer> conf, Integer ext) {
		Multiset<Integer> confp = HashMultiset.create(conf);
        BitSet shiftedConfp = (BitSet)getActualConf(conf).clone();
//		BitSet shiftedConfp = (BitSet)mappings.get(conf).clone();
		confp.add(ext);
		boolean shift = false;

		if (unfMetadata.getCutoffEvents().contains(ext)) {
			Integer cutoff = ext;
			Integer corr = unfMetadata.getCorrespondingEvent(ext);
			shiftedConfp.andNot(getLocalConfiguration(cutoff));
			shiftedConfp.or(getLocalConfiguration(corr));
			BiMap<Integer, Integer> iso = unfMetadata.getIsomorphism().get(cutoff);
			for (Integer ev: iso.keySet())
				if (conf.contains(ev)) {
					shiftedConfp.clear(ev);
					shiftedConfp.set(iso.get(ev));
				}
			
			shift = true;
		} else
			shiftedConfp.set(ext);

//        boolean flag = false;
//        if(shiftedConfp.get(2) && shiftedConfp.get(3) ){
//            extend(conf, ext);
//        }
		
		mappings.put(confp, shiftedConfp);
		
		return new Pair<>(confp, shift);
	}
	
	public BitSet getShifted(Multiset<Integer> conf) {
		return mappings.get(conf);
	}
	
	public BitSet getLocalConfiguration(int e) {
		BitSet conf = (BitSet)pes.invcausality[e].clone();
		conf.set(e);
		return conf;
	}
	
	public Set<BitSet> getMaxConf() {
		if (maximalConfigurations == null) {
			maximalConfigurations = new HashSet<>();
			
			for (Integer terminalEvent: unfMetadata.getTerminalEvents())
				maximalConfigurations.add(getLocalConfiguration(terminalEvent));
		}
		return maximalConfigurations;
	}

	public Set<Integer> getInvisibleEvents() {
		return unfMetadata.getInvisibleEvents();
	}
	
	public String getLabel(Integer e) {
		return pes.labels.get(e);
	}
	
	public List<String> getLabels() {
		return pes.labels;
	}

	public List<String> getLabels(Set<Integer> next) {
		List<String> labels = new LinkedList<>();

		for(Integer evt : next)
			labels.add(getLabel(evt));

		return labels;
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

	public Pomset getPomset(BitSet bs, HashSet<String> obs) {
		DirectedGraph confgraph = new DirectedGraph();
		BiMap<Vertex, Integer> map = HashBiMap.<Vertex, Integer> create();
		HashMap<Integer, String> labels = new HashMap<>();

		for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1))
			if(!getLabel(i).equals("_0_") && !getLabel(i).equals("_1_") && obs.contains(getLabel(i))){
				Vertex v = new Vertex(getLabel(i));
				map.put(v, i);
				labels.put(i, getLabel(i));
				confgraph.addVertex(v);
			}

		HashSet<Integer> conf = bS2int(bs);
		for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1))
			if(!(getLabel(i).equals("_0_") && getDirectPredecessors(i).isEmpty()) && !(getLabel(i).equals("_1_") && getDirectSuccessors(i).isEmpty())
					&& obs.contains(getLabel(i))){
				BitSet pred = getOrderBS(conf, i);

				for (int j = pred.nextSetBit(0); j >= 0; j = pred.nextSetBit(j + 1))
					if(!getLabel(j).equals("_0_") && !getLabel(j).equals("_1_") && obs.contains(getLabel(j)))
						confgraph.addEdge(map.inverse().get(j), map.inverse().get(i));
			}

		return new Pomset(confgraph, map, labels);
	}

	public HashSet<Integer> bS2int(BitSet bs) {
        if(bs == null) {
            return null;
        }

		HashSet<Integer> indexes = new HashSet<>();

        if(bs.cardinality() > 0)
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

	public Set<Integer> getEvents(BitSet bs) {
		Set<Integer> events = new HashSet<>();

		for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1))
			events.add(i);

		return events;
	}

	public boolean isSubset(BitSet a, BitSet b) {
		BitSet set = (BitSet)a.clone();
		set.and(b);
		return set.cardinality() == a.cardinality();		
	}
	
	public String toDot() {
		StringWriter str = new StringWriter();
		PrintWriter out = new PrintWriter(str);
		
		out.println("digraph G {");
		
		out.println("\tnode[shape=box];");
		for (int i = 0; i < pes.labels.size(); i++)
			out.printf("\tn%d [label=\"%s(%d)\"];\n", i, pes.labels.get(i), i);
		
		for (int src = 0; src < pes.labels.size(); src++)
			for (int tgt = pes.dcausality[src].nextSetBit(0); tgt >= 0; tgt = pes.dcausality[src].nextSetBit(tgt+1))
				out.printf("\tn%d -> n%d;\n", src, tgt);
		
		for (Integer cutoff: unfMetadata.getCutoffEvents())
			out.printf("\tn%d -> n%d [color=red];\n", cutoff, unfMetadata.getCorrespondingEvent(cutoff));
		
		out.println("}");
		
		return str.toString();
	}

	public void toDot(PrintWriter out, Set<Integer> set) {		
		out.println("\tnode[shape=box];");
		for (Integer i : set)
			out.printf("\tnp%d [label=\"%s(%d)\"];\n", i, pes.labels.get(i), i);
		
		for (Integer src: set)
			for (Integer tgt: set) {
				if (pes.dcausality[src].get(tgt))
					out.printf("\tnp%d -> np%d;\n", src, tgt);
				Integer corr = unfMetadata.getCorrespondingEvent(src);
				if (tgt.equals(corr))
					out.printf("\tnp%d -> np%d [color=red];\n", src, corr);
			}		
	}

	
	public BitSet getConcurrencySet(Integer ev) {
		return pes.concurrency[ev];
	}

	public Set<Integer> getCutoffEvents() {
		return unfMetadata.getCutoffEvents();
	}

	public Collection<Integer> getDirectPredecessors(int e2) {
		return dpredecessors.get(e2);
	}
	
	public Collection<Integer> getDirectSuccessors(int e2) {
		return dsuccessors.get(e2);
	}

	public BehaviorRelation getBRelation(int e1, int e2) {
		return pes.getBRelMatrix()[e1][e2];
	}

	public Set<Integer> getEvents() {
		Set<Integer> set = new HashSet<>();
		for (int e = 0; e < pes.labels.size(); e++)
			set.add(e);
		return set;
	}

	public BitSet unshift(BitSet conf, Integer cutoff) {
		BitSet unshiftedConfp = (BitSet)conf.clone();
		Integer corr = unfMetadata.getCorrespondingEvent(cutoff);
		unshiftedConfp.andNot(getLocalConfiguration(corr));
		unshiftedConfp.or(getLocalConfiguration(cutoff));
		BiMap<Integer, Integer> iso = unfMetadata.getIsomorphism().get(cutoff).inverse();
		for (Integer ev: iso.keySet())
			if (conf.get(ev)) {
				unshiftedConfp.clear(ev);
				unshiftedConfp.set(iso.get(ev));
			}

		return unshiftedConfp;
	}

	public Integer unshift(Integer ev, Integer cutoff) {
		Integer result = unfMetadata.getIsomorphism().get(cutoff).inverse().get(ev);
		if (result != null) {
			return result;
		} else
			return ev;
	}

	public BitSet getCausesOf(Integer e2) {
		BitSet causes = (BitSet)getLocalConfiguration(e2).clone();
		causes.clear(e2);
		return causes;
	}

	public Set<String> getPossibleFutureAsLabels(Multiset<Integer> conf) {
        BitSet shiftedConf = getActualConf(conf);

//        if(!futures.containsKey(shiftedConf)) {
//            System.out.println(toDot());
//            System.out.println("(" + conf + ")enter shift " + shiftedConf);
//        }
		BitSet bitset = futures.get(shiftedConf);

		Set<String> set = new HashSet<>();
		for (int ev = bitset.nextSetBit(0); ev >= 0; ev = bitset.nextSetBit(ev+1))
			set.add(pes.labels.get(ev));
		return set;
	}

	public Integer getCorresponding(Integer e2) {
		return unfMetadata.getCorrespondingEvent(e2);
	}

	public BitSet getDirectConflictSet(Integer e2) {
		BitSet dconf = new BitSet();
		for (Integer pred: getDirectPredecessors(e2)) {
			for (Integer succ: getDirectSuccessors(pred))
				if (pes.conflict[e2].get(succ))
					dconf.set(succ);
		}
		return dconf;
	}

	public void setLabels(List<String> newLabels) {
		this.pes.setLabels(newLabels);
	}

	public Integer getLast(Multiset<Integer> c2, HashSet<String> observable) {
		Integer last = -1;
		int histSize = 0;

		for(Integer i : c2)
			if(getLocalConfiguration(i).cardinality() > histSize && observable.contains(getLabel(i))){
                last = i;
                histSize = getLocalConfiguration(i).cardinality();
		    }

        return last;
	}
}
