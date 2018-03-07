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
