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
