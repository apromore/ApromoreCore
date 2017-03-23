package org.processmining.stagemining.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.hypergraph.abs.Vertex;
import org.processmining.stagemining.models.graph.WeightedDirectedGraph;

public class ConnectedComponentSearch {
	private WeightedDirectedGraph g = null;
	private Map<IVertex,Boolean> visitedMap = new HashMap<IVertex,Boolean>(); 
	private Set<IVertex> vertexSetOneTraversal = new HashSet<IVertex>();
	private Set<Set<IVertex>> connectedComponents = new HashSet<Set<IVertex>>();
	
	public ConnectedComponentSearch(WeightedDirectedGraph g) {
		this.g = g;
	}
	
	/**
	 * Find connected components in a graph with DFS
	 * @param g
	 * @return
	 */
	public Set<Set<IVertex>> findConnectedComponents() {
		for (IVertex vertex : g.getVertices()) {
			visitedMap.put((Vertex)vertex, false);
		}
		for (IVertex v : g.getVertices()) {
			if (!visitedMap.get(v)) {
				vertexSetOneTraversal = new HashSet<IVertex>();
				dfsToFindCC(v);
				connectedComponents.add(vertexSetOneTraversal);
			}
		}
		
		return connectedComponents;
	}
	
	private void dfsToFindCC(IVertex v) {
		visitedMap.put(v, true);
		vertexSetOneTraversal.add(v);
		Set<Vertex> adjacents = new HashSet<Vertex>(g.getDirectSuccessors((Vertex)v));
		adjacents.addAll(new HashSet<Vertex>(g.getDirectPredecessors((Vertex)v)));
		adjacents.remove(v);
		for (IVertex adjacent : adjacents) {
			if (!visitedMap.get(adjacent)) {
				dfsToFindCC(adjacent);
			}
		}
	}
}
