package ee.ut.eventstr;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

import ee.ut.nets.unfolding.Unfolding2PES;
import ee.ut.org.processmining.framework.util.Pair;

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
		BitSet shiftedConf = mappings.get(conf);
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
	
	public Pair<Multiset<Integer>, Boolean> extend(Multiset<Integer> conf, Integer ext) {
		Multiset<Integer> confp = HashMultiset.create(conf);
		BitSet shiftedConfp = (BitSet)mappings.get(conf).clone();
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
					System.out.println("mapping Ie: " + ev);
					shiftedConfp.clear(ev);
					shiftedConfp.set(iso.get(ev));
				}
			
			shift = true;
		} else
			shiftedConfp.set(ext);
		
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
				System.out.println("mapping Ie: " + ev);
				unshiftedConfp.clear(ev);
				unshiftedConfp.set(iso.get(ev));
			}

		return unshiftedConfp;
	}

	public Integer unshift(Integer ev, Integer cutoff) {
		Integer result = unfMetadata.getIsomorphism().get(cutoff).inverse().get(ev);
		if (result != null) {
			System.out.println("mapping Ie: " + ev);
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
		BitSet shiftedConf = getShifted(conf);
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
}
