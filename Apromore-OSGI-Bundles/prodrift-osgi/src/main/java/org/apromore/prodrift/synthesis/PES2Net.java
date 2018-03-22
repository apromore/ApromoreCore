package org.apromore.prodrift.synthesis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apromore.prodrift.config.BehaviorRelation;
import org.apromore.prodrift.graph.util.CCliqueFinder;
import org.apromore.prodrift.mining.log.local.PrimeEventStructure;
import org.jbpt.graph.Edge;
import org.jbpt.graph.Graph;
import org.jbpt.hypergraph.abs.Vertex;

public class PES2Net {
	
	
	public static int pid = 0;
	
	public static Set<Set<Integer>> getConflictClusters(PrimeEventStructure<?> aes) {
		Graph confgraph = new Graph();
		List<Vertex> vertices = new LinkedList<Vertex>();
		Map<Vertex, Integer> map = new HashMap<Vertex, Integer>();

		for (String label: aes.getLabels()) {
			Vertex v = new Vertex(label);
			map.put(v, vertices.size());
			vertices.add(v);
		}

		// Add edges
		for (int i = 0; i < vertices.size(); i++) 
			for (int j = 0; j < vertices.size(); j++)
				if (i < j && aes.getRelation(i, j) == BehaviorRelation.CONFLICT)
					confgraph.addEdge(vertices.get(i), vertices.get(j));
		
		// Remove transitive relations
		Set<Edge> toRemove = new HashSet<Edge>();
		for (Vertex src: vertices) {
			for (Vertex adj: confgraph.getAdjacent(src))
				for (Edge edge: confgraph.getEdges(adj)) {
					Vertex tgt = edge.getOtherVertex(adj);
					if (aes.getRelation(map.get(src), map.get(tgt)) == BehaviorRelation.FOLLOW)
						toRemove.add(edge);
				}
		}
		
		confgraph.removeEdges(toRemove);
				
		CCliqueFinder finder = new CCliqueFinder(confgraph, new HashSet<Edge>(), new HashSet<>(confgraph.getEdges()));
		
		Set<Set<Integer>> clusters = new HashSet<Set<Integer>>();
		
		for (Set<Vertex> clique: finder.getAllMaximalCliques()) {
			Set<Integer> set = new HashSet<Integer>();
			for (Vertex v: clique)
				set.add(map.get(v));
			clusters.add(set);
		}
		
		return clusters;
	}
}
