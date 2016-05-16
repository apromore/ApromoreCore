package ee.ut.eventstr;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

import ee.ut.nets.unfolding.Unfolding2PES;

public class UnfoldingPESSemantics <T> {
	private Unfolding2PES unfMetadata;
	private PrimeEventStructure<T> pes;
//	protected Set<BitSet> cuts;
	protected Set<BitSet> maximalConfigurations;
	protected BitSet residualEvents;

	protected Multimap<Integer, Integer> dpredecessors;
	protected Multimap<Integer, Integer> dsuccessors;
	protected Map<Multiset<Integer>, Set<Integer>> possibleExtensions;
	protected Map<Multiset<Integer>, Multiset<Integer>> mappings;

	public UnfoldingPESSemantics(PrimeEventStructure<T> pes, Unfolding2PES metadata) {
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
		
		Multiset<Integer> emptySet = HashMultiset.create();
		this.possibleExtensions.put(emptySet, new HashSet<>(pes.sources));
		Multiset<Integer> firstConf = HashMultiset.create(pes.sources);
		mappings.put(emptySet, emptySet);
		mappings.put(firstConf, firstConf);		
	}
	
	public Multimap<Integer,Integer> getDirectSuccessors() {
		return dsuccessors;
	}
		
	public Set<BitSet> getMaxConf() {
		if (maximalConfigurations == null) {
			maximalConfigurations = new HashSet<>();
			
			for (Integer terminalEvent: unfMetadata.getTerminalEvents())
				maximalConfigurations.add(getLocalConfiguration(terminalEvent));
		}
		return maximalConfigurations;
	}

	
	public Set<String> getPossibleFutureAsLabels(Multiset<Integer> conf) {
		Multiset<Integer> mapping = mappings.get(conf);
		BitSet confBitset = new BitSet();
		BitSet conflicting = new BitSet();
		
		for (int e: mapping.elementSet()) {
			conflicting.or(pes.conflict[e]);
			confBitset.set(e);
		}

		conflicting.flip(0, pes.labels.size());
		conflicting.andNot(confBitset);
		BitSet future = conflicting;
		
		Set<String> flabels = new HashSet<>();
		for (int e = future.nextSetBit(0); e >= 0; e = future.nextSetBit(e+1))
			if (!unfMetadata.getInvisibleEvents().contains(e))
				flabels.add(pes.labels.get(e));
		
		return flabels;
	}
	
	public Set<Integer> getCutoffEvents() {
		return unfMetadata.getCutoffEvents();
	}
		
	public Set<Integer> getPossibleExtensions(Multiset<Integer> conf) {
		Multiset<Integer> mapping = mappings.get(conf);

		Set<Integer> pe = possibleExtensions.get(mapping);

		if (pe == null) {
			BitSet conflicting = new BitSet();
			BitSet concurrent = new BitSet();
			BitSet dcausal = new BitSet();
			BitSet _conf = new BitSet();
			
			for (Integer e: mapping) {
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
		}
		
		for (Integer ev: pe) {
			Multiset<Integer> nconf = HashMultiset.create(conf);
			nconf.add(ev);
			Multiset<Integer> nmapping = HashMultiset.create();
			
			if (unfMetadata.getCutoffEvents().contains(ev)) {
				Integer cutoff = ev;
				Integer corresponding = unfMetadata.getCorrespondingEvent(cutoff);
				BitSet corrLC = getLocalConfiguration(corresponding);
				BitSet cutoffLC = getLocalConfiguration(cutoff);
				Set<Integer> flattened = new HashSet<>(mapping.elementSet());

				if (pes.getBRelMatrix()[corresponding][ev] == BehaviorRelation.CAUSALITY) {
					cutoffLC.andNot(corrLC);
					for (int e = cutoffLC.nextSetBit(0); e>=0; e = cutoffLC.nextSetBit(e+1))
						flattened.remove(e);
				} else {
					for (int e = cutoffLC.nextSetBit(0); e>=0; e = cutoffLC.nextSetBit(e+1))
						flattened.remove(e);
					for (int e = corrLC.nextSetBit(0); e>=0; e = corrLC.nextSetBit(e+1))
						flattened.add(e);
				}
				nmapping.addAll(flattened);
			} else {
				nmapping.addAll(mapping);
				nmapping.add(ev);
			}
			mappings.put(nconf, nmapping);
		}
		
		if (!possibleExtensions.containsKey(mapping))
			possibleExtensions.put(mapping, pe);
		
		return pe;
	}

	public BitSet getLocalConfiguration(int e) {
		BitSet conf = (BitSet)pes.invcausality[e].clone();
		conf.set(e);
		return conf;
	}

	public BehaviorRelation getBRelation(int e1, int e2) {
		return pes.getBRelMatrix()[e1][e2];
	}

	public List<String> getLabels() {
		return pes.labels;
	}
	public String getLabel(int e) {
		return pes.labels.get(e);
	}
	
	public boolean isSubset(BitSet a, BitSet b) {
		BitSet set = (BitSet)a.clone();
		set.and(b);
		return set.cardinality() == a.cardinality();		
	}
	
	public Set<Integer> getInvisibleEvents() {
		return unfMetadata.getInvisibleEvents();
	}

	public Collection<Integer> getDirectPredecessors(int e2) {
		return dpredecessors.get(e2);
	}

	public Integer getCorrespondingEvent(Integer cutoff) {
		return unfMetadata.getCorrespondingEvent(cutoff);
	}

	public Set<Integer> getCutoffPossibleExtensions(Integer cutoff) {
		BitSet strictCauses = (BitSet)pes.invcausality[cutoff].clone();
		BitSet conflicting = new BitSet();
		BitSet concurrent = new BitSet();
		BitSet dcausal = new BitSet();
		BitSet _conf = new BitSet();
		
		for (int e = strictCauses.nextSetBit(0); e >= 0; e = strictCauses.nextSetBit(e+1)) {
			conflicting.or(pes.conflict[e]);
			concurrent.or(pes.concurrency[e]);
			dcausal.or(pes.dcausality[e]);
			_conf.set(e);
		}

		dcausal.or(concurrent);
		dcausal.andNot(_conf);
		dcausal.andNot(conflicting);
		dcausal.clear(cutoff);
		
		Set<Integer> pe = new HashSet<>();
		for (int e = dcausal.nextSetBit(0); e >= 0; e = dcausal.nextSetBit(e+1)) {
			if (isSubset(pes.invcausality[e], _conf))
				pe.add(e);
		}
		return pe;
	}
	
	public BitSet getResidualEvents() {
		if (residualEvents == null) {
			residualEvents = new BitSet(pes.labels.size());
			residualEvents.flip(0, pes.labels.size());
			for (BitSet maxConf: getMaxConf())
				residualEvents.andNot(maxConf);
		}
		return residualEvents;
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
	
	
	Map<BitSet, Integer> beginning;
	Set<BitSet> acyclicConfs;
	Multimap<Integer, BitSet> intervals;
	Multimap<Integer, BitSet> beginning2confs;
	
	public void analyzeIntervals() {
		beginning = new HashMap<>();
		acyclicConfs = new HashSet<>();
		intervals = HashMultimap.create();
		beginning2confs = HashMultimap.create();
		
		for (BitSet conf: getMaxConf()) {
			Integer cand = null;
			BitSet candLConf = null;
			for (Integer cutoff: unfMetadata.getCutoffEvents()) {
				Integer corr = unfMetadata.getCorrespondingEvent(cutoff);
				if (conf.get(corr)) {
					BitSet lconf = getLocalConfiguration(corr);
					if (cand == null || lconf.cardinality() < candLConf.cardinality()) {
						cand = corr;
						candLConf = lconf;
					}
				}
			}
			if (cand != null) {
				beginning.put(conf, cand);
				beginning2confs.put(cand, conf);
			} else
				acyclicConfs.add(conf);
		}
		
		for (Integer corr: new HashSet<>(beginning.values())) {
			BitSet corrLConf = getLocalConfiguration(corr);
			for (Integer cutoff: unfMetadata.getCutoffEvents())
				if (corr.equals(unfMetadata.getCorrespondingEvent(cutoff))) {
					BitSet cutoffLConf = (BitSet)getLocalConfiguration(cutoff).clone();
					cutoffLConf.andNot(corrLConf);
					intervals.put(corr, cutoffLConf);
				}
					
		}
		
		System.out.println("Acyclic confs: " + acyclicConfs);
		System.out.println("Interval: " + intervals);
	}
	
	public boolean isAcyclicConf(BitSet conf) {
		return acyclicConfs.contains(conf);
	}

	public Integer getBeginningFor(BitSet conf) {
		return beginning.get(conf);
	}

	public Collection<BitSet> getIntervalsFor(Integer beginning2) {
		return intervals.get(beginning2);
	}
	
	public Multimap<Integer, BitSet> getBeginning2ConfsMap() {
		return beginning2confs;
	}

	public Multimap<Integer, Integer> getDirectPredecessors() {
		return dpredecessors;
	}
}
