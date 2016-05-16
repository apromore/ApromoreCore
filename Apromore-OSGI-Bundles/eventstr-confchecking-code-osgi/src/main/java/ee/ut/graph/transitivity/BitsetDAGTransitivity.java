package ee.ut.graph.transitivity;

import java.util.BitSet;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.google.common.collect.Multimap;

import ee.ut.org.processmining.framework.util.Pair;

public class BitsetDAGTransitivity {
	
	public static LinkedHashMap<Integer, Integer> topologicalSorting(Multimap<Integer, Integer> adj, int size, Collection<Integer> sources) {
		LinkedHashMap<Integer, Integer> orderMap = new LinkedHashMap<Integer, Integer>();
		BitSet visited = new BitSet();
		for (Integer src: sources) {
			tsDFS(adj, orderMap, visited, src);
		}
		return orderMap;
	}
	
	private static void tsDFS(Multimap<Integer, Integer> adj, LinkedHashMap<Integer, Integer> order, BitSet visited, Integer curr) {
		visited.set(curr);
		for (Integer succ: adj.get(curr))
			if (!visited.get(succ))
				tsDFS(adj, order, visited, succ);
		order.put(curr,order.size());
	}
	public static BitSet[] transitiveClosureDAG(Multimap<Integer, Integer> adj, int size, Set<Integer> sources) {
		BitSet[] closure = new BitSet[size];
		LinkedHashMap<Integer, Integer> order = topologicalSorting(adj, size, sources);
		
		for (Integer v : order.keySet()) {
			closure[v] = new BitSet();
			
			// The following line is for reflexivity
			closure[v].set(v);
			
			TreeMap<Integer, Integer> succs = new TreeMap<>();
			for (Integer s: adj.get(v)) succs.put(order.get(s), s);
			for (Entry<Integer,Integer> entry: succs.entrySet()) {
				int w = entry.getValue();
				if (!closure[v].get(w)) {
					if (closure[w] == null)
						closure[w] = new BitSet();
					closure[v].or(closure[w]);
				}
			}
		}
		
		for (int i = 0; i < size; i++) {
			if (closure[i] == null)
				closure[i] = new BitSet();
			else
				closure[i].clear(i);
		}

		return closure;
	}
	
	public static Pair<BitSet[], BitSet[]> transitivityDAG(Multimap<Integer, Integer> adj, int size, Collection<Integer> sources) {
		BitSet[] reduction = new BitSet[size];
		BitSet[] closure = new BitSet[size];
		LinkedHashMap<Integer, Integer> order = topologicalSorting(adj, size, sources);
		
		for (Integer v : order.keySet()) {
			closure[v] = new BitSet();
			closure[v].set(v); // We are computing a reflexive transitive closure

			reduction[v] = new BitSet();
						
			TreeMap<Integer, Integer> succs = new TreeMap<>();
			for (Integer s: adj.get(v)) succs.put(order.get(s), s);
			for (Entry<Integer,Integer> entry: succs.entrySet()) {
				int w = entry.getValue();
				if (!closure[v].get(w)) {
					if (closure[w] == null)
						closure[w] = new BitSet();
					closure[v].or(closure[w]);
					reduction[v].set(w);
				}
			}
		}
		
		for (int i = 0; i < size; i++) {
			if (closure[i] == null) {
				closure[i] = new BitSet();
				reduction[i] = new BitSet();
			} else
				closure[i].clear(i);
		}

		return new Pair<>(closure, reduction);		
	}
	
	public static BitSet[] transitiveReductionDAG(Multimap<Integer, Integer> adj, int size, Set<Integer> sources) {
		return transitivityDAG(adj, size, sources).getSecond();
	}
}
