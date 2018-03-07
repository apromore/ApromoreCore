package org.apromore.prodrift.graph.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jbpt.graph.Edge;
import org.jbpt.graph.Graph;
import org.jbpt.hypergraph.abs.Vertex;


/**
 * Computes all maximal c-cliques of an Undirected Graph. Used in conjunction
 * with the Modular Product of graphs, to solve the Maximum Commom Edge Subgraph
 * problem.
 * The implementation is an extension to well-known Bron-Kerbosch's Algorithm 457
 * as described in:
 * 
 *    F. Cazals and C. Karande, An Algorithm for reporting maximal c-cliques.
 *    Technical Computer Science 349 pp 484-490, 2005
 *
 * @author Luciano Garcia-Banuelos
 * 
 */
public class CCliqueFinder {
	Set<Set<Vertex>> cliques = null;
	SortedMap<Integer, Set<Set<Vertex>>> sortedCliques;
	
	private Graph graph;
	
	Map<Vertex, Set<Vertex>> d, c, n;
	
	public CCliqueFinder(Graph graph, Set<Edge> dedges, Set<Edge> cedges) {
		this.graph = graph;
		this.cliques = null;

		d = new HashMap<Vertex, Set<Vertex>>();
		c = new HashMap<Vertex, Set<Vertex>>();
		n = new HashMap<Vertex, Set<Vertex>>();
		
		for (Vertex v: graph.getVertices()) {
			c.put(v, new HashSet<Vertex>());
			d.put(v, new HashSet<Vertex>());
			n.put(v, new HashSet<Vertex>());			
		}
		
		for (Edge edge: graph.getEdges()) {
			Vertex u = edge.getV1(); // graph.getEdgeSource(edge);
			Vertex v = edge.getV2(); //graph.getEdgeTarget(edge);
			
			if (cedges.contains(edge)) {
				c.get(u).add(v);
				c.get(v).add(u);
			} else {
				d.get(u).add(v);
				d.get(v).add(u);				
			}
			
			n.get(u).add(v);
			n.get(v).add(u);
		}
	}
	
	public Set<Set<Vertex>> getAllMaximalCliques() {
		if (cliques == null) {
			cliques = new HashSet<Set<Vertex>>();
			ccliqueInit();
		}
		return cliques;
	}
	
	public Set<Set<Vertex>> getBiggestMaximalCliques() {
		getSortedCliques();
		return sortedCliques.get(sortedCliques.lastKey());
	}

	public SortedMap<Integer, Set<Set<Vertex>>> getSortedCliques() {
		if (sortedCliques == null) {
			cliques = new HashSet<Set<Vertex>>();
			ccliqueInit();
			
			sortedCliques = new TreeMap<Integer, Set<Set<Vertex>>>();
			
			for (Set<Vertex> clique: cliques) {
				int size = clique.size();
				Set<Set<Vertex>> subset = sortedCliques.get(size);
				if (subset == null)
					sortedCliques.put(size, subset = new HashSet<Set<Vertex>>());
				subset.add(clique);
			}
		}
		
		return sortedCliques;
	}
	
	private void ccliqueInit() {
		Set<Vertex> T = new HashSet<Vertex>();
		
		for (Vertex ui: graph.getVertices()) {
			Set<Vertex> R = new HashSet<Vertex>();
			Set<Vertex> Q = new HashSet<Vertex>();
			Set<Vertex> P = new HashSet<Vertex>();
			Set<Vertex> Y = new HashSet<Vertex>();
			Set<Vertex> X = new HashSet<Vertex>();
			
			R.add(ui);
			
			Q.addAll(graph.getVertices());
			Q.removeAll(T);
			Q.retainAll(d.get(ui));

			P.addAll(graph.getVertices());
			P.removeAll(T);
			P.retainAll(c.get(ui));

			Y.addAll(d.get(ui));
			Y.retainAll(T);

			X.addAll(c.get(ui));
			X.retainAll(T);

			cclique(R,P,Q,X,Y);
			T.add(ui);
		}
	}

	private void cclique(Set<Vertex> r, Set<Vertex> p, Set<Vertex> q, Set<Vertex> x, Set<Vertex> y) {
		if (p.isEmpty() && x.isEmpty())
			cliques.add(new HashSet<Vertex>(r));
		else {
			while (!p.isEmpty()) {
				Vertex ui = p.iterator().next();
				p.remove(ui);
				Set<Vertex> Rnew = new HashSet<Vertex>();
				Set<Vertex> Qnew = new HashSet<Vertex>();
				Set<Vertex> Pnew = new HashSet<Vertex>();
				Set<Vertex> Ynew = new HashSet<Vertex>();
				Set<Vertex> Xnew = new HashSet<Vertex>();

				Rnew.addAll(r);
				Rnew.add(ui);
				
				Qnew.addAll(q);
				Qnew.retainAll(d.get(ui));
			
				Set<Vertex> PClone = new HashSet<Vertex>(p);
				PClone.retainAll(n.get(ui));
				Set<Vertex> QClone = new HashSet<Vertex>(q);
				QClone.retainAll(c.get(ui));
				Pnew.addAll(PClone);
				Pnew.addAll(QClone);
				
				Ynew.addAll(y);
				Ynew.retainAll(d.get(ui));
				
				Set<Vertex> XClone = new HashSet<Vertex>(x);
				XClone.retainAll(n.get(ui));
				Set<Vertex> YClone = new HashSet<Vertex>(y);
				YClone.retainAll(c.get(ui));
				Xnew.addAll(XClone);
				Xnew.addAll(YClone);
				
				cclique(Rnew, Pnew, Qnew, Xnew, Ynew);
				
				x.add(ui);
			}
		}
	}
}