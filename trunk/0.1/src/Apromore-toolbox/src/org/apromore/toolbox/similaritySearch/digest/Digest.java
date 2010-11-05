package org.apromore.toolbox.similaritySearch.digest;


import java.math.BigInteger;
import java.util.HashSet;
import java.util.LinkedList;

import org.apromore.toolbox.similaritySearch.graph.Edge;
import org.apromore.toolbox.similaritySearch.graph.Graph;
import org.apromore.toolbox.similaritySearch.graph.Vertex;

public class Digest {
	
	public static Graph digest(Graph merged, int freq) {
		Graph digest = new Graph();
		digest.name = merged.name + "_digest";
		digest.ID = String.valueOf(Graph.getNextId());
		
		// add all the edges that occur in the merged
		// model more or equal as the frequency given
		for (Edge e : merged.getEdges()) {
			if (e.getLabels().size() >= freq) {
//				System.out.println("adding edge to digest "+e.getFromVertex() + " "+ e.getToVertex());
//				for (Entry<String, Vertex> entries : merged.getVertexMap().entrySet()) {
//					System.out.println(entries.getKey() + " : "+ entries.getValue());
//				}
//				
//				System.out.println("**********************");
//				
				digest.addEdge(e);
				Vertex fromVertex = digest.getVertexMap().get(e.getFromVertex());
				Vertex toVertex = digest.getVertexMap().get(e.getToVertex());
				
				// copy vertex definitions
				if (fromVertex == null) {
					fromVertex = merged.getVertexMap().get(e.getFromVertex()).copyVertex();
					digest.addVertex(fromVertex);
					
				}
				
				if (toVertex == null) {
					toVertex = merged.getVertexMap().get(e.getToVertex()).copyVertex();
					digest.addVertex(toVertex);
				}
				fromVertex.addChild(toVertex);
				toVertex.addParent(fromVertex);
			}
		}
		
//		for (Entry<String, Vertex> entries : digested.getVertexMap().entrySet()) {
//			System.out.println(entries.getKey() + " : "+ entries.getValue());
//		}
//		System.out.println("**********************");
		
		HashSet<Vertex> lessParents = new HashSet<Vertex>();
		HashSet<Vertex> lessChildren = new HashSet<Vertex>();
		
		for (Vertex v : digest.getVertices()) {
			Vertex originalV = merged.getVertexMap().get(v.getID());
			if (originalV.getParents().size() > v.getParents().size()) {
				lessParents.add(v);
			}
			if (originalV.getChildren().size() > v.getChildren().size()) {
				lessChildren.add(v);
			}
		}
		
		for (Vertex p : lessChildren) {
			Vertex mergedP = merged.getVertexMap().get(p.getID());
			for (Vertex ch : lessParents) {
				Vertex mergedCh = merged.getVertexMap().get(ch.getID());
				if (hasPath(mergedP, mergedCh, digest)) {
					// TODO add new node type .. and the presentation???
					Vertex placeholder = new Vertex(Vertex.Type.function, "#", Graph.getNextId());
					digest.addVertex(placeholder);
					digest.connectVertices(p, placeholder);
					digest.connectVertices(placeholder, ch);
				} else if (hasDirectPath(mergedP, mergedCh)) {
					digest.connectVertices(p, ch);
				} 
			}
		}
		
		// labels for all edges should be added to the model
		for (Edge e : digest.getEdges()) {
			e.removeLabelFromModel();
		}
		
		digest.cleanGraph();
		
		for (Vertex v : digest.getVertices()) {
			if (v.getType().equals(Vertex.Type.gateway)) {
				v.setConfigurable(false);
			}
		}
		
		return digest;
	}
	
	private static boolean hasPath(Vertex mergedP, Vertex mergedCh, Graph digested) {
		LinkedList<Vertex> toProcess = new LinkedList<Vertex>();
		HashSet<BigInteger> processed = new HashSet<BigInteger>();
		
		
		for (Vertex pCh : mergedP.getChildren()) {
			if (!digested.getVertexMap().containsKey(pCh.getID())) {
				toProcess.add(pCh);
			}
		}
		
		while (toProcess.size() > 0) {
			Vertex processing = toProcess.removeFirst();
			
			if (processing.getID().equals(mergedCh.getID())) {
				return true;
			}
			processed.add(processing.getID());
			
			for (Vertex pCh : processing.getChildren()) {
				if (pCh.getID().equals(mergedCh.getID())) {
					return true;
				}
				
				if (!processed.contains(pCh.getID()) && 
						!digested.getVertexMap().containsKey(pCh.getID())) {
					toProcess.add(pCh);
				}
			}
		}
		
		return false;
	}

	private static boolean hasDirectPath(Vertex p, Vertex ch) {
		
		for (Vertex pCh : p.getChildren()) {
			if (pCh.getID().equals(ch.getID())) {
				return true;
			}
		}
		return false;
	}
}
