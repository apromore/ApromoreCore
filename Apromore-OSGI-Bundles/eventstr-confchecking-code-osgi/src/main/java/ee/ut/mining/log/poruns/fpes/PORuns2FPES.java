package ee.ut.mining.log.poruns.fpes;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Multimap;

import ee.ut.eventstr.freq.FrequencyAwarePrimeEventStructure;
import ee.ut.graph.transitivity.BitsetDAGTransitivity;
import ee.ut.mining.log.poruns.PORuns;
import ee.ut.org.processmining.framework.util.Pair;

public class PORuns2FPES {
	public static FrequencyAwarePrimeEventStructure<Integer> getPrimeEventStructure(PORuns runs, String modelName) {
		Map<Integer, String> lmap = runs.getLabels();
		int size = lmap.size();
		List<String> labels = new ArrayList<>(size);
		for (int i = 0; i < size; i++)
			labels.add(lmap.get(i));

		return getPrimeEventStructure(runs.getSuccessors(), runs.getConcurrency(), runs.getSources(),
				runs.getSinks(), labels, runs.getEquivalenceClasses(), modelName);
	}
	public static FrequencyAwarePrimeEventStructure<Integer> getPrimeEventStructure(
			Multimap<Integer, Integer> adj, Multimap<Integer, Integer> conc,
			List<Integer> sources, List<Integer> sinks,
			List<String> labels, Multimap<Integer, Integer> equivalenceClasses, String modelName) {
		int size = labels.size();
		Pair<BitSet[], BitSet[]> pair = BitsetDAGTransitivity.transitivityDAG(adj, size, sources);
		BitSet[] causality = pair.getFirst();
		BitSet[] dcausality = pair.getSecond();
		BitSet[] invcausality = new BitSet[size];
		BitSet[] concurrency = new BitSet[size];
		BitSet[] conflict = new BitSet[size];
		
		for (int i = 0; i < size; i++) {
			invcausality[i] = new BitSet();
			concurrency[i] = new BitSet();
			conflict[i] = new BitSet();
		}
		
		for (Entry<Integer, Integer> entry: conc.entries())
			concurrency[entry.getKey()].set(entry.getValue());
		
		for (int i = 0; i < size; i++)
			for (int j = causality[i].nextSetBit(0); j >= 0; j = causality[i].nextSetBit(j + 1))
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
				if (causality[i].get(j))
					fmatrix[i][j] = (occurrences.get(j) + 0.0f) / occurrences.get(i);
			}
		}

		
		FrequencyAwarePrimeEventStructure<Integer> pes = 
				new FrequencyAwarePrimeEventStructure<Integer>(labels, causality, dcausality, invcausality,
						concurrency, conflict, sources, sinks, occurrences, fmatrix);		
		return pes;
	}	
}
