package ee.ut.graph.ccomp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.google.common.collect.Multimap;

public class StronglyConnectedComponents<T> {
	private Map<T, Integer> indices;
	private Map<T, Integer> lowlink;
	private Stack<T> stack;
	private Set<Set<T>> sccs;

	public StronglyConnectedComponents(Set<T> vertices, Multimap<T, T> edges) {
		indices = new HashMap<T, Integer>();
		lowlink = new HashMap<T, Integer>();
		stack = new Stack<T>();
		sccs = new HashSet<Set<T>>();
		for (T v: vertices)
			if (!indices.containsKey(v))
				tarjan(edges, v);
	}
	
	public Set<Set<T>> getSCCs() {
		return sccs;
	}
	
	private void tarjan(Multimap<T, T> adj, T v) {
		int index = indices.size();
		indices.put(v,index);
		lowlink.put(v,index);
		
		stack.push(v);
		
		for (T w : adj.get(v)) {
			if (!indices.containsKey(w)) {
				tarjan(adj, w);
				lowlink.put(v, Math.min(lowlink.get(v), lowlink.get(w)));
			}
			else if (stack.contains(w))
				lowlink.put(v, Math.min(lowlink.get(v), indices.get(w)));
		}
	
		if (lowlink.get(v).equals(indices.get(v))) {
			Set<T> scc = new HashSet<>(); 
			T w = null;
			do {
				w = stack.pop();	
				scc.add(w);
			} while (!v.equals(w));
			
			sccs.add(scc);
		}
	}

}
