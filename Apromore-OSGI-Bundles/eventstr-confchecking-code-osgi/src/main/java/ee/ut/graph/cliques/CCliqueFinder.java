package ee.ut.graph.cliques;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.common.collect.Multimap;


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
	Set<Set<Integer>> cliques = null;
	SortedMap<Integer, Set<Set<Integer>>> sortedCliques;
		
	Set<Integer> vertices;
	Multimap<Integer, Integer> d, c, n;
	
	public CCliqueFinder(Set<Integer> vertices, Multimap<Integer, Integer> edges, 
			Multimap<Integer, Integer> dedges, Multimap<Integer, Integer> cedges) {
		this.vertices = vertices;
		this.n = edges;
		this.d = dedges;
		this.c = cedges;
	}
	
	public Set<Set<Integer>> getAllMaximalCliques() {
		if (cliques == null) {
			cliques = new HashSet<Set<Integer>>();
			ccliqueInit();
		}
		return cliques;
	}
	
	public Set<Set<Integer>> getBiggestMaximalCliques() {
		getSortedCliques();
		return sortedCliques.get(sortedCliques.lastKey());
	}

	public SortedMap<Integer, Set<Set<Integer>>> getSortedCliques() {
		if (sortedCliques == null) {
			cliques = new HashSet<Set<Integer>>();
			ccliqueInit();
			
			sortedCliques = new TreeMap<Integer, Set<Set<Integer>>>();
			
			for (Set<Integer> clique: cliques) {
				int size = clique.size();
				Set<Set<Integer>> subset = sortedCliques.get(size);
				if (subset == null)
					sortedCliques.put(size, subset = new HashSet<Set<Integer>>());
				subset.add(clique);
			}
		}
		
		return sortedCliques;
	}
	
	private void ccliqueInit() {
		Set<Integer> T = new HashSet<Integer>();
		
		for (Integer ui: vertices) {
			Set<Integer> R = new HashSet<Integer>();
			Set<Integer> Q = new HashSet<Integer>();
			Set<Integer> P = new HashSet<Integer>();
			Set<Integer> Y = new HashSet<Integer>();
			Set<Integer> X = new HashSet<Integer>();
			
			R.add(ui);
			
			Q.addAll(vertices);
			Q.removeAll(T);
			Q.retainAll(d.get(ui));

			P.addAll(vertices);
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

	private void cclique(Set<Integer> r, Set<Integer> p, Set<Integer> q, Set<Integer> x, Set<Integer> y) {
		if (p.isEmpty() && x.isEmpty())
			cliques.add(new HashSet<Integer>(r));
		else {
			while (!p.isEmpty()) {
				Integer ui = p.iterator().next();
				p.remove(ui);
				Set<Integer> Rnew = new HashSet<Integer>();
				Set<Integer> Qnew = new HashSet<Integer>();
				Set<Integer> Pnew = new HashSet<Integer>();
				Set<Integer> Ynew = new HashSet<Integer>();
				Set<Integer> Xnew = new HashSet<Integer>();

				Rnew.addAll(r);
				Rnew.add(ui);
				
				Qnew.addAll(q);
				Qnew.retainAll(d.get(ui));
			
				Set<Integer> PClone = new HashSet<Integer>(p);
				PClone.retainAll(n.get(ui));
				Set<Integer> QClone = new HashSet<Integer>(q);
				QClone.retainAll(c.get(ui));
				Pnew.addAll(PClone);
				Pnew.addAll(QClone);
				
				Ynew.addAll(y);
				Ynew.retainAll(d.get(ui));
				
				Set<Integer> XClone = new HashSet<Integer>(x);
				XClone.retainAll(n.get(ui));
				Set<Integer> YClone = new HashSet<Integer>(y);
				YClone.retainAll(c.get(ui));
				Xnew.addAll(XClone);
				Xnew.addAll(YClone);
				
				cclique(Rnew, Pnew, Qnew, Xnew, Ynew);
				
				x.add(ui);
			}
		}
	}
}