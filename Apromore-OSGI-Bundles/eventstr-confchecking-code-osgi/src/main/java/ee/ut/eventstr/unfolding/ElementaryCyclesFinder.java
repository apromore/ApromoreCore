package ee.ut.eventstr.unfolding;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.gwt.dev.util.collect.HashSet;

import ee.ut.eventstr.NewUnfoldingPESSemantics;
import ee.ut.org.processmining.framework.util.Pair;

public class ElementaryCyclesFinder {
	private Set<Multiset<Integer>> cycles;
	private NewUnfoldingPESSemantics<Integer> pessem;
	private Map<BitSet, Multiset<Integer>> reverseMap;

	public ElementaryCyclesFinder(NewUnfoldingPESSemantics<Integer> pessem) {
		this.pessem = pessem;
		this.cycles = new HashSet<>();
		this.reverseMap = new HashMap<>();
		
		cycle(HashMultiset.<Integer> create());
		
		System.out.println(cycles);
		System.out.println(getFootprints());
	}
	
	public Set<Multiset<Integer>> getElementaryCycles() {
		return cycles;
	}
	
	public Map<Multiset<Integer>, Multiset<Integer>> getFootprints() {
		Map<Multiset<Integer>,Multiset<Integer>> result = new HashMap<>();
		Set<Integer> cutoffEvents = pessem.getCutoffEvents();
		for (Multiset<Integer> cycle: cycles) {
			Multiset<Integer> footprint = HashMultiset.create(cycle);
			footprint.retainAll(cutoffEvents);
			result.put(footprint, cycle);
		}
		return result;
	}
	
	private void cycle(Multiset<Integer> v) {
		BitSet shiftedV = pessem.getShifted(v);
		reverseMap.put(shiftedV, v);
		for(Integer ext: pessem.getPossibleExtensions(v)) {
			Pair<Multiset<Integer>, Boolean> pair = pessem.extend(v, ext);
			Multiset<Integer> w = pair.getFirst();
			BitSet shiftedW = pessem.getShifted(w);
			if (pair.getSecond() && isSubset(shiftedW, shiftedV)) {
				BitSet concset = (BitSet)pessem.getConcurrencySet(ext).clone();
				concset.and(shiftedW);
				if (concset.isEmpty()) {
					Multiset<Integer> copy = HashMultiset.create(w);
					copy.removeAll(reverseMap.get(shiftedW));
					cycles.add(copy);
				}
			} else
				cycle(w);
		}
	}
	
	public boolean isSubset(BitSet a, BitSet b) {
		BitSet set = (BitSet)a.clone();
		set.and(b);
		return set.cardinality() == a.cardinality();		
	}
}
