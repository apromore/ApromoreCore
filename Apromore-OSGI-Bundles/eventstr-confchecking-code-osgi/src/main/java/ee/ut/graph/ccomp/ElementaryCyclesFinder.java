package ee.ut.graph.ccomp;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gwt.dev.util.collect.HashSet;

public class ElementaryCyclesFinder<T> {
	private Set<T> mark;
	private Set<T> reach;
	private Stack<T> stack;
	private Map<T, Integer> position;
	private Multimap<T, T> succs;
	private Multimap<T, T> B;
	private Set<Set<T>> cycles;

	public ElementaryCyclesFinder(Set<T> vertices, Multimap<T, T> succs, Multimap<T, T> preds) {
		this.cycles = new HashSet<>();
		this.mark = new HashSet<>();
		this.reach = new HashSet<>();
		this.stack = new Stack<>();
		this.position = new HashMap<>();
		this.succs = succs;
		this.B = HashMultimap.create();
		
		StronglyConnectedComponents<T> processor = new StronglyConnectedComponents<>(vertices, succs);
		for (Set<T> scc: processor.getSCCs()) {
			if (scc.size() > 1) {
				int inDeg = Integer.MIN_VALUE;
				T s = null;
				for (T v: scc)
					if (preds.get(v).size() > inDeg) {
						inDeg = preds.get(v).size();
						s = v;
					}
				cycle(scc, s, Integer.MAX_VALUE);
			}
		}
	}

	private boolean cycle(Set<T> scc, T v, int q) {
		boolean f = false;
		mark.add(v);
		stack.push(v);
		int t = stack.size();
		position.put(v, t);
		if (!reach.contains(v)) q = t;
		System.out.println(stack);
		for (T w: succs.get(v))
			if (scc.contains(w) && !B.containsEntry(v, w)) {
				if (!mark.contains(w)) {
					boolean g = cycle(scc, w, q);
					if (g)
						f = true;
					else
						nocycle(v, w);
				} else if (position.get(w) <= q) {
					Set<T> cycle = new HashSet<>();
					for (int pos = stack.size(); pos >= 0; pos--) {
						T curr = stack.get(pos - 1);
						cycle.add(curr);
						if (w.equals(curr))
							break;
//						cycle.add(curr);
					}
//					System.out.println(">>> " + cycle);
					cycles.add(cycle);
					f = true;
				} else
					nocycle(v, w);
			}
		stack.pop();
		if (f) unmark(v);
		reach.add(v);
		position.put(v, Integer.MAX_VALUE);
		return f;
	}

	private void nocycle(T x, T y) {
		B.put(y, x);
	}

	private void unmark(T x) {
		mark.remove(x);
		for (T y: B.get(x))
			if (mark.contains(y))
				return;
		B.removeAll(x);
	}
	
	public Set<Set<T>> getElementaryCycles() {
		return cycles;
	}
}
