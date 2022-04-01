/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2010 - University of Tartu
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.splitminer.dominatortree;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Luciano Garcia Banuelos
 *
 */
public class DominatorTree {
	Map<Integer, List<Integer>> adjList;
	HashMap<Integer, InfoNode> map = new HashMap<Integer, InfoNode>();
	LinkedList<InfoNode> vertex = new LinkedList<InfoNode>();

	public class InfoNode {
		int dfsnum;
		int semi;
		InfoNode parent;
		InfoNode ancestor;
		InfoNode label;
		LinkedList<InfoNode> pred = new LinkedList<InfoNode>();
		LinkedList<InfoNode> bucket = new LinkedList<InfoNode>();
		InfoNode dom;
		Integer node;
		
		public Integer getNode() { return node; }
		public InfoNode getDom() { return dom; }
	}

	public DominatorTree(Map<Integer, List<Integer>> adjList) {
		this.adjList = adjList;
	}
	
	public void DFS(Integer node, Integer parent) {
		InfoNode info = new InfoNode();
		info.node = node;
		info.parent = map.get(parent);
		info.dfsnum = vertex.size();
		info.semi = info.dfsnum;
		info.label = info;

		vertex.add(info);
		map.put(node, info);
		for (Integer succ : adjList.get(node)) {
			if (!map.containsKey(succ))
				DFS(succ, node);
			map.get(succ).pred.add(info);
		}
	}

	

	public void COMPRESS(InfoNode v) {
		if (v.ancestor.ancestor != null) {
			COMPRESS(v.ancestor);
			if (v.ancestor.label.semi < v.label.semi)
				v.label = v.ancestor.label;
			v.ancestor = v.ancestor.ancestor;
		}
	}

	public InfoNode EVAL(InfoNode v) {
		if (v.ancestor == null)
			return v;
		else {
			COMPRESS(v);
			return v.label;
		}
	}

	public void LINK(InfoNode v, InfoNode w) {
		w.ancestor = v;
	}

	public void analyse(Integer root) {
		// STEP 1
		DFS(root, null);
		for (int i = vertex.size() - 1; i > 0; i--) {
			InfoNode w = vertex.get(i);
			// STEP 2
			for (InfoNode v : w.pred) {
				InfoNode u = EVAL(v);
				if (u.semi < w.semi)
					w.semi = u.semi;
			}
			vertex.get(w.semi).bucket.add(w);
			LINK(w.parent, w);

			// STEP 3
			for (InfoNode v : w.parent.bucket) {
				InfoNode u = EVAL(v);
				v.dom = u.semi < v.semi ? u : w.parent;
			}
			w.parent.bucket.clear();
		}
		// STEP 4
		for (int i = 1; i < vertex.size(); i++) {
			InfoNode w = vertex.get(i);
			if (w.dom != vertex.get(w.semi))
				w.dom = w.dom.dom;
		}
		map.get(root).dom = null;
	}
	
	public InfoNode getInfo(Integer node) {
		return map.get(node);
	}
}
