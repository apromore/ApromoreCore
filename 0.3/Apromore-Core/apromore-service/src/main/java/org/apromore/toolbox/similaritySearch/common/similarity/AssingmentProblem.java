package org.apromore.toolbox.similaritySearch.common.similarity;

import java.util.LinkedList;

import org.apromore.toolbox.similaritySearch.common.Settings;
import org.apromore.toolbox.similaritySearch.common.StringPair;
import org.apromore.toolbox.similaritySearch.common.VertexPair;
import org.apromore.toolbox.similaritySearch.common.stemmer.SnowballStemmer;
import org.apromore.toolbox.similaritySearch.graph.Graph;
import org.apromore.toolbox.similaritySearch.graph.Vertex;
import org.apromore.toolbox.similaritySearch.graph.Vertex.Type;


public class AssingmentProblem {

	/**
	 * Finds the matching vertices between graphs g1 and g2
	 * @param g1
	 * @param g2
	 * @param threshold - if node similarity is >= than threshold then these nodes are considered to
	 * be matched.
	 * @param stemmer - stemmer for wrord stemming, if == null, then english stemmer is used
	 * @return matching vertex pairs
	 */
	public static LinkedList<VertexPair> getMappingsGraph(Graph g1, Graph g2,
			double threshold, SnowballStemmer stemmer) {

		LinkedList<Vertex> g1Vertices = g1.getFunctions();
		LinkedList<Vertex> g2Vertices = g2.getFunctions();


		if (Settings.considerEvents) {
			g1Vertices.addAll(g1.getEvents());
			g2Vertices.addAll(g2.getEvents());
		}

		return getMappingsVetrex(g1Vertices, g2Vertices, threshold, stemmer, 0);
	}

	/**
	 * Finds the vertex mapping
	 * @param g1Vertices - graph g1 vertices that need to be matched with graph g1 vertices
	 * @param g2Vertices - graph g2 vertices
	 * @param threshold - if node similarity is >= than threshold then these nodes are considered to
	 * be matched.
	 * @param stemmer - stemmer for wrord stemming, if == null, then english stemmer is used
	 * @return matching vertex pairs
	 */

	public static LinkedList<VertexPair> getMappingsVetrex(
			LinkedList<Vertex> g1Vertices, LinkedList<Vertex> g2Vertices,
			double threshold, SnowballStemmer stemmer) {
		return getMappingsVetrex(g1Vertices, g2Vertices, threshold, stemmer, 0);
	}
	
	public static boolean canMap(Vertex v1, Vertex v2) {
		if (v1.getParents().size() == 0 && v2.getParents().size() != 0 
				   || v1.getParents().size() != 0 && v2.getParents().size() == 0
				   || v1.getChildren().size() == 0 && v2.getChildren().size() != 0
				   || v1.getChildren().size() != 0 && v2.getChildren().size() == 0) {
			return false;
		}
		return true;	
	}
	
	/**
	 * Finds the vertex mapping
	 * @param g1Vertices - graph g1 vertices that need to be matched with graph g1 vertices
	 * @param g2Vertices - graph g2 vertices
	 * @param threshold - if node similarity is >= than threshold then these nodes are considered to
	 * be matched.
	 * @param stemmer - stemmer for wrord stemming, if == null, then english stemmer is used
	 * @param gateways - if == 0, then gateways are not matched, if == 1, then only parent are looked,
	 * if == 2, then only children are looked
	 * @return matching vertex pairs
	 */
	public static LinkedList<VertexPair> getMappingsVetrex(
									LinkedList<Vertex> g1Vertices, 
									LinkedList<Vertex> g2Vertices,
									double threshold, 
									SnowballStemmer stemmer, 
									int gateways) {

		LinkedList<VertexPair> solutionMappings = new LinkedList<VertexPair>();

		if (g1Vertices.size() == 0 || g2Vertices.size() == 0) {
			return solutionMappings;
		}

		if (stemmer == null) {
			stemmer = Settings.getEnglishStemmer();
		}
		
		LinkedList<Vertex> g1Vertices_fe = new LinkedList<Vertex>();
		LinkedList<Vertex> g2Vertices_fe = new LinkedList<Vertex>();
		
		for (Vertex v : g1Vertices) {
			if (!v.getType().equals(Vertex.Type.gateway)) {
				g1Vertices_fe.add(v);
			}
		}
		
		for (Vertex v : g2Vertices) {
			if (!v.getType().equals(Vertex.Type.gateway)) {
				g2Vertices_fe.add(v);
			}
		}

		if (g1Vertices_fe.size() > 0 && g2Vertices_fe.size() > 0) {
			int dimFunc = g1Vertices_fe.size() > g2Vertices_fe.size() ? g1Vertices_fe.size()
					: g2Vertices_fe.size();
			double costs[][] = new double[dimFunc][dimFunc];
			double costsCopy[][] = new double[dimFunc][dimFunc];
			int nrZeros = 0;
	
			// function mapping score
			for (int i = 0; i < g1Vertices_fe.size(); i++) {
				for (int j = 0; j < g2Vertices_fe.size(); j++) {
					double edScore = 0;
					if (g1Vertices_fe.get(i).getType().equals(g2Vertices_fe.get(j).getType())
							&& g1Vertices_fe.get(i).getLabel() != null 
							&& g2Vertices_fe.get(j).getLabel() != null) {
						edScore = LabelEditDistance.edTokensWithStemming(g1Vertices_fe
								.get(i).getLabel(), g2Vertices_fe.get(j).getLabel(),
								Settings.STRING_DELIMETER, stemmer, true);
					}
					
					if (edScore < threshold)
						edScore = 0;
	
					if (edScore == 0) {
						nrZeros++;
					}
					
					costs[i][j] = (-1) * edScore;
				}
			}
			
			if (nrZeros != g1Vertices_fe.size() * g2Vertices_fe.size()) {
				for (int i = 0; i < costs.length; i++) {
					for (int j = 0; j < costs[0].length; j++) {
						costsCopy[i][j] = costs[i][j];
					}
				}
		
				int[][] result = HungarianAlgorithm.computeAssignments(costsCopy);
		
				for (int i = 0; i < result.length; i++) {
					double pairCost = (-1) * costs[result[i][0]][result[i][1]];
					if (result[i][0] < g1Vertices_fe.size()
							&& result[i][1] < g2Vertices_fe.size()
							&& pairCost >= threshold 
							&& AssingmentProblem.canMap(g1Vertices_fe.get(result[i][0]), g2Vertices_fe.get(result[i][1]))
							) {
						solutionMappings.add(new VertexPair(g1Vertices_fe
								.get(result[i][0]), g2Vertices_fe.get(result[i][1]),
								pairCost));
					}
				}
			}
		}
		if (gateways > 0) {
			solutionMappings.addAll(getMappingsGateways(g1Vertices, g2Vertices, threshold, stemmer, gateways));
		}
		return solutionMappings;
	}

	public static LinkedList<VertexPair> getMappingsVetrexUsingNodeMapping(
			Graph g1, Graph g2, double threshold, double semanticThreshold) {

		LinkedList<Vertex> g1Vertices = (LinkedList<Vertex>) g1.getVertices();
		LinkedList<Vertex> g2Vertices = (LinkedList<Vertex>) g2.getVertices();
		
		LinkedList<VertexPair> solutionMappings = new LinkedList<VertexPair>();

		if (g1Vertices.size() == 0 || g2Vertices.size() == 0) {
			return solutionMappings;
		}

		if (g1Vertices.size() > 0 && g2Vertices.size() > 0) {
			int dimFunc = g1Vertices.size() > g2Vertices.size() ? g1Vertices
					.size()
					: g2Vertices.size();
			double costs[][] = new double[dimFunc][dimFunc];
			double costsCopy[][] = new double[dimFunc][dimFunc];
			int nrZeros = 0;

			// function mapping score
			for (int i = 0; i < g1Vertices.size(); i++) {
				for (int j = 0; j < g2Vertices.size(); j++) {
					double edScore = NodeSimilarity.findNodeSimilarity(g1Vertices.get(i), g2Vertices.get(j), threshold);
					if (g1Vertices.get(i).getType().equals(Type.gateway) 
							&& g2Vertices.get(j).getType().equals(Type.gateway) 
							&& edScore < semanticThreshold) {
						edScore = 0;
					}
					else if (!(g1Vertices.get(i).getType().equals(Type.gateway) 
							&& g2Vertices.get(j).getType().equals(Type.gateway)) && edScore < threshold)
						edScore = 0;

					if (edScore == 0) {
						nrZeros++;
					}
					costs[i][j] = (-1) * edScore;
				}
			}

			if (nrZeros != g1Vertices.size() * g2Vertices.size()) {
				for (int i = 0; i < costs.length; i++) {
					for (int j = 0; j < costs[0].length; j++) {
						costsCopy[i][j] = costs[i][j];
					}
				}

				int[][] result = HungarianAlgorithm
						.computeAssignments(costsCopy);

				for (int i = 0; i < result.length; i++) {
					double pairCost = (-1) * costs[result[i][0]][result[i][1]];
					if (result[i][0] < g1Vertices.size()
							&& result[i][1] < g2Vertices.size()
							&& pairCost > 0
							&& AssingmentProblem.canMap(g1Vertices
									.get(result[i][0]), g2Vertices
									.get(result[i][1]))) {
						solutionMappings.add(new VertexPair(g1Vertices
								.get(result[i][0]), g2Vertices
								.get(result[i][1]), pairCost));
					}
				}
			}
		}
		return solutionMappings;
	}
	
	
	public static boolean listContains(LinkedList<Vertex> list, Vertex v) {
		for (Vertex lv : list) {
			if (v.getID() == lv.getID()) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean mappingContainsVertexRight(LinkedList<VertexPair> mapping, Vertex v){
		
		for(VertexPair vp : mapping) {
			if(vp.getRight().getID().equals(v.getID())) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean mappingContainsVertexLeft(LinkedList<VertexPair> mapping, Vertex v){
		
		for(VertexPair vp : mapping) {
			if(vp.getLeft().getID().equals(v.getID())) {
				return true;
			}
		}
		return false;
	}
	/**
	 * Finds the match between gateways, the decision is made based on the match of gateway parents/children
	 * match, if the parent/child is also a gateway, then the decision is done recursively
	 * @param g1Vertices - graph g1 vertices that need to be matched with graph g1 vertices
	 * @param g2Vertices - graph g2 vertices
	 * @param threshold - if node similarity is >= than threshold then these nodes are considered to
	 * be matched.
	 * @param stemmer - stemmer for wrord stemming, if == null, then english stemmer is used
	 * @param lookParents - if == 0, then gateways are not matched, if == 1, then only parent are looked,
	 * if == 2, then only children are looked
	 * @return
	 */
	public static LinkedList<VertexPair> getMappingsGateways(
									LinkedList<Vertex> g1Vertices, 
									LinkedList<Vertex> g2Vertices, 
									double threshold, 
									SnowballStemmer stemmer, 
									int lookParents) {

		LinkedList<Vertex> g1Gateways = new LinkedList<Vertex>();
		LinkedList<Vertex> g2Gateways = new LinkedList<Vertex>();
		
		LinkedList<VertexPair> possibleMatches = new LinkedList<VertexPair>();
		
		for (Vertex v : g1Vertices) {
			if (v.getType().equals(Vertex.Type.gateway)) {
				g1Gateways.add(v);
			}
		}
		
		for (Vertex v : g2Vertices) {
			if (v.getType().equals(Vertex.Type.gateway)) {
				g2Gateways.add(v);
			}
		}

		if (g1Gateways.size() == 0 || g2Gateways.size() == 0) {
			return possibleMatches;
		}
		
		int dimFunc = g1Gateways.size() > g2Gateways.size() ? g1Gateways.size()
				: g2Gateways.size();

		double costs[][] = new double[dimFunc][dimFunc];
		double costsCopy[][] = new double[dimFunc][dimFunc];

		for (int i = 0; i < g1Gateways.size(); i++) {
			for (int j = 0; j < g2Gateways.size(); j++) {
				double edScore = 0;
				LinkedList<VertexPair> map;
				if (lookParents == 2) {
					map = getMappingsVetrex(g1Gateways.get(i).getChildren(), g2Gateways.get(j).getChildren(),
						threshold, stemmer, lookParents);
					for (VertexPair vp : map) {
						edScore += vp.getWeight();
					}
					
					edScore = map.size() == 0 ? 0 : edScore / map.size();
				}
				else if (lookParents == 1) {
					map = getMappingsVetrex(g1Gateways.get(i).getParents(), g2Gateways.get(j).getParents(),
						threshold, stemmer, lookParents);
					for (VertexPair vp : map) {
						edScore += vp.getWeight();
					}
					
					edScore = map.size() == 0 ? 0 :edScore / map.size();
				}

				if (edScore < threshold)
					edScore = 0;

				costs[i][j] = (-1) * edScore;
			}
		}

		for (int i = 0; i < costs.length; i++) {
			for (int j = 0; j < costs[0].length; j++) {
				costsCopy[i][j] = costs[i][j];
			}
		}

		int[][] result = HungarianAlgorithm.computeAssignments(costsCopy);

		for (int i = 0; i < result.length; i++) {
			double pairCost = (-1) * costs[result[i][0]][result[i][1]];
			if (result[i][0] < g1Gateways.size()
					&& result[i][1] < g2Gateways.size()
					&& pairCost > 0) {
				possibleMatches.add(new VertexPair(g1Gateways
						.get(result[i][0]), g2Gateways.get(result[i][1]),
						pairCost));
			}
		}
		return possibleMatches;
	}

	private static LinkedList<Vertex> getParentGWs(Vertex gw) {
		
		LinkedList<Vertex> parentGws = new LinkedList<Vertex>();
		parentGws.add(gw);
		LinkedList<Vertex> toProcess = new LinkedList<Vertex>();
		
		for (Vertex v : gw.getParents())  {
			if (v.getType().equals(Type.gateway)) {
				parentGws.add(v);
				toProcess.add(v);
			}
		}
		
		
		while (toProcess.size() > 0) {
			
			Vertex currGw = toProcess.removeFirst();
			
			for (Vertex v : currGw.getParents())  {
				if (v.getType().equals(Type.gateway)) {
					parentGws.add(v);
					toProcess.add(v);
				}
			}
		}
		
		return parentGws;
	}
	
	private static LinkedList<Vertex> getChildGWs(Vertex gw) {
		
		LinkedList<Vertex> childGws = new LinkedList<Vertex>();
		childGws.add(gw);
		
		LinkedList<Vertex> toProcess = new LinkedList<Vertex>();
		
		for (Vertex v : gw.getChildren())  {
			if (v.getType().equals(Type.gateway)) {
				childGws.add(v);
				toProcess.add(v);
			}
		}
		
		
		while (toProcess.size() > 0) {
			
			Vertex currGw = toProcess.removeFirst();
			
			for (Vertex v : currGw.getChildren())  {
				if (v.getType().equals(Type.gateway)) {
					childGws.add(v);
					toProcess.add(v);
				}
			}
		}
		
		return childGws;
	}
	
	static LinkedList<StringPair> getMappingsLabels(
			LinkedList<String> g1Labels, LinkedList<String> g2Labels,
			double threshold) {

		SnowballStemmer englishStemmer = Settings.getStemmer("english");
		int dimFunc = g1Labels.size() > g2Labels.size() ? g1Labels.size()
				: g2Labels.size();
		double costs[][] = new double[dimFunc][dimFunc];
		double costsCopy[][] = new double[dimFunc][dimFunc];
		LinkedList<StringPair> solutionMappings = new LinkedList<StringPair>();

		if (g1Labels.size() == 0 || g2Labels.size() == 0) {
			return solutionMappings;
		}

		// function mapping score
		for (int i = 0; i < g1Labels.size(); i++) {
			for (int j = 0; j < g2Labels.size(); j++) {
				double edScore;

				edScore = LabelEditDistance.edTokensWithStemming(g1Labels
						.get(i), g2Labels.get(j), Settings.STRING_DELIMETER,
						englishStemmer, true);

				if (edScore < threshold)
					edScore = 1;

				costs[i][j] = edScore;
			}
		}

		for (int i = 0; i < costs.length; i++) {
			for (int j = 0; j < costs[0].length; j++) {
				costsCopy[i][j] = costs[i][j];
			}
		}

		int[][] result = HungarianAlgorithm.computeAssignments(costsCopy);

		for (int i = 0; i < result.length; i++) {
			solutionMappings.add(new StringPair(g1Labels.get(result[i][0]),
					g2Labels.get(result[i][1])));
		}

		return solutionMappings;
	}
}
