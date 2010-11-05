package org.apromore.toolbox.similaritySearch.common.similarity;

import java.util.HashSet;
import java.util.LinkedList;

import org.apromore.toolbox.similaritySearch.common.Settings;
import org.apromore.toolbox.similaritySearch.common.StringPair;
import org.apromore.toolbox.similaritySearch.common.VertexPair;
import org.apromore.toolbox.similaritySearch.common.stemmer.SnowballStemmer;
import org.apromore.toolbox.similaritySearch.graph.Graph;
import org.apromore.toolbox.similaritySearch.graph.Vertex;
import org.apromore.toolbox.similaritySearch.graph.Vertex.GWType;
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
					if (g1Vertices_fe.get(i).getType().equals(g2Vertices_fe.get(j).getType())) {
						edScore = LabelEditDistance.edTokensWithStemming(g1Vertices_fe
//							LabelEditDistance.edTokensWithStemmingWordnet(g1Vertices_fe
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
//					if (result[i][0] < g1Vertices.size()
//							&& result[i][1] < g2Vertices.size()) {
//						System.out.println(g1Vertices.get(result[i][0]).getLabel() + 
//								" "+ g2Vertices.get(result[i][1]).getLabel() + " " + pairCost + " "+
//								AssingmentProblem.canMap(g1Vertices
//										.get(result[i][0]), g2Vertices
//										.get(result[i][1])));
//					}
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
	
	/**
	 * Find the mapping when some of the gateways can be added (parents/children of the gateway match some node 
	 * parents/children)
	 * @param g1 graph 1
	 * @param g2 graph 2
	 * @param g1Vertices graph g1 vertex parents/children that are not matched yet
	 * @param g2Vertices graph g2 vertex parents/children that are not matched yet
	 * @param g1Before the vertex in graph g1 that is whose parents/children are given
	 * @param g2Before the vertex in graph g2 that is whose parents/children are given
	 * @param threshold - if node similarity is >= than threshold then these nodes are considered to
	 * be matched.
	 * @param stemmer - stemmer for wrord stemming, if == null, then english stemmer is used
	 * @param lookParents - if == 0, then gateways are not matched, if == 1, then only parent are looked,
	 * if == 2, then only children are looked
	 * @return
	 */
	public static LinkedList<VertexPair> getMappingsWithAddingGateways(
			Graph g1, 
			Graph g2,
			LinkedList<Vertex> g1Vertices, 
			LinkedList<Vertex> g2Vertices,
			Vertex g1Before,
			Vertex g2Before,
			double threshold, 
			SnowballStemmer stemmer, 
			int lookParents,
			LinkedList<VertexPair> nodeMappingsPrev,
			LinkedList<VertexPair> process,
			LinkedList<VertexPair> processed) {
		
		LinkedList<VertexPair> possibleMatches = new LinkedList<VertexPair>();
		
		LinkedList<Vertex> g1Gateways = new LinkedList<Vertex>();
		LinkedList<Vertex> g2Gateways = new LinkedList<Vertex>();
		
		for (Vertex v : g1Vertices) {
			if (v.getType().equals(Vertex.Type.gateway) 
					&& !AssingmentProblem.mappingContainsVertexLeft(nodeMappingsPrev, v)
					&& !AssingmentProblem.mappingContainsVertexLeft(process, v)
					&& !AssingmentProblem.mappingContainsVertexLeft(processed, v)) {
				g1Gateways.add(v);
			}
		}

		for (Vertex gw : g1Gateways) {
			if (lookParents == 1) {
				LinkedList<Vertex> parentGWs = new LinkedList<Vertex>();
				LinkedList<Vertex> noparentGWs = new LinkedList<Vertex>();
				for (Vertex par : gw.getParents()) {
					if (par.getType().equals(Type.gateway)) {
						parentGWs.add(par);
					}
					else {
						noparentGWs.add(par);
					}
				}
				
				LinkedList<VertexPair> nodeMappings = AssingmentProblem.getMappingsVetrex(noparentGWs, g2Vertices, threshold, stemmer, lookParents);
				
				for (Vertex parentGW : parentGWs) {
					LinkedList<Vertex> toProcess = getParentGWs(parentGW);
					VertexPair bestMapping = null;
					for (Vertex pr : toProcess) {
						LinkedList<Vertex> toGW = new  LinkedList<Vertex>();
						toGW.add(pr);
						LinkedList<VertexPair> nodeMappingsGW = AssingmentProblem.getMappingsVetrex(toGW, g2Vertices, threshold, stemmer, lookParents);
						nodeMappingsGW.addAll(AssingmentProblem.getMappingsVetrex(pr.getParents(), g2Vertices, threshold, stemmer, lookParents));
						for (VertexPair vpp : nodeMappingsGW) {
							if (vpp.getWeight() > 0) {
								if (bestMapping  == null) {
									bestMapping = vpp;
								}
								else if (vpp.getWeight() > bestMapping.getWeight()) {
									bestMapping = vpp;
								}
							}
						}
					}
					if (bestMapping != null) {
						nodeMappings.add(bestMapping);
					}
				}
				
				// if some of the parents are matched
				if (nodeMappings.size() > 0) {
					// find the best one
					VertexPair bestMap = nodeMappings.getFirst();
					for (int i = 1; i < nodeMappings.size(); i++) {
						if (nodeMappings.get(i).getWeight() > bestMap.getWeight()) {
							bestMap = nodeMappings.get(i);
						}
					}
					
					Vertex newGW = new Vertex(GWType.xor, Graph.getNextId());
					newGW.setAddedGW(true);
					HashSet<String> labels = g2.removeEdge(bestMap.getRight().getID(), g2Before.getID());
					g2Before.removeParent(bestMap.getRight().getID());
					bestMap.getRight().removeChild(g2Before.getID());
					
					g2.addVertex(newGW);
					g2.connectVertices(newGW, g2Before, labels);
					g2.connectVertices(bestMap.getRight(), newGW, labels);
					
					possibleMatches.add(new VertexPair(gw, newGW));
				}
			} else  if (lookParents == 2) {
				LinkedList<Vertex> childGWs = new LinkedList<Vertex>();
				LinkedList<Vertex> nochildGWs = new LinkedList<Vertex>();
				for (Vertex ch : gw.getChildren()) {
					if (ch.getType().equals(Type.gateway)) {
						childGWs.add(ch);
					}
					else {
						nochildGWs.add(ch);
					}
				}
				
				LinkedList<VertexPair> nodeMappings = AssingmentProblem.getMappingsVetrex(nochildGWs, g2Vertices, threshold, stemmer, lookParents);
				
				for (Vertex childGW : childGWs) {
					LinkedList<Vertex> toProcess = getChildGWs(childGW);
					VertexPair bestMapping = null;
					for (Vertex pr : toProcess) {
						LinkedList<Vertex> toGW = new  LinkedList<Vertex>();
						toGW.add(pr);
						LinkedList<VertexPair> nodeMappingsGW = AssingmentProblem.getMappingsVetrex(toGW, g2Vertices, threshold, stemmer, lookParents);
						nodeMappingsGW.addAll(AssingmentProblem.getMappingsVetrex(pr.getChildren(), g2Vertices, threshold, stemmer, lookParents));
						for (VertexPair vpp : nodeMappingsGW) {
							if (vpp.getWeight() > 0) {
								if (bestMapping  == null) {
									bestMapping = vpp;
								}
								else if (vpp.getWeight() > bestMapping.getWeight()) {
									bestMapping = vpp;
								}
							}
						}
					}
					if (bestMapping != null) {
						nodeMappings.add(bestMapping);
					}
				}

				// if some of the parents are matched
				if (nodeMappings.size() > 0) {
					// find the best one
					VertexPair bestMap = nodeMappings.getFirst();
					for (int i = 1; i < nodeMappings.size(); i++) {
						if (nodeMappings.get(i).getWeight() > bestMap.getWeight()) {
							bestMap = nodeMappings.get(i);
						}
					}
					
					
					
					Vertex newGW = new Vertex(GWType.xor, Graph.getNextId());

					newGW.setAddedGW(true);
					HashSet<String> labels = g2.removeEdge(g2Before.getID(), bestMap.getRight().getID());
					g2Before.removeChild(bestMap.getRight().getID());
					bestMap.getRight().removeParent(g2Before.getID());
					
					g2.addVertex(newGW);
					g2.connectVertices(g2Before, newGW, labels);
					g2.connectVertices(newGW, bestMap.getRight(), labels);
					
					possibleMatches.add(new VertexPair(gw, newGW));
				}
			}
		}
		
		for (Vertex v : g2Vertices) {
			if (v.getType().equals(Vertex.Type.gateway)
					&& !AssingmentProblem.mappingContainsVertexRight(nodeMappingsPrev, v)
					&& !AssingmentProblem.mappingContainsVertexRight(process, v)
					&& !AssingmentProblem.mappingContainsVertexRight(processed, v)) {
				g2Gateways.add(v);
			}
		}

		for (Vertex gw : g2Gateways) {
			if (lookParents == 1) {
				LinkedList<Vertex> parentGWs = new LinkedList<Vertex>();
				LinkedList<Vertex> noparentGWs = new LinkedList<Vertex>();
				for (Vertex par : gw.getParents()) {
					if (par.getType().equals(Type.gateway)) {
						parentGWs.add(par);
					}
					else {
						noparentGWs.add(par);
					}
				}
				
				LinkedList<VertexPair> nodeMappings = AssingmentProblem.getMappingsVetrex(g1Vertices, noparentGWs, threshold, stemmer, lookParents);
				
				for (Vertex parentGW : parentGWs) {
					LinkedList<Vertex> toProcess = getParentGWs(parentGW);
					VertexPair bestMapping = null;
					for (Vertex pr : toProcess) {
						LinkedList<Vertex> toGW = new  LinkedList<Vertex>();
						toGW.add(pr);
						LinkedList<VertexPair> nodeMappingsGW = AssingmentProblem.getMappingsVetrex(g1Vertices, toGW, threshold, stemmer, lookParents);
						nodeMappingsGW.addAll(AssingmentProblem.getMappingsVetrex(g1Vertices, pr.getParents(), threshold, stemmer, lookParents));
						for (VertexPair vpp : nodeMappingsGW) {
							if (vpp.getWeight() > 0) {
								if (bestMapping  == null) {
									bestMapping = vpp;
								}
								else if (vpp.getWeight() > bestMapping.getWeight()) {
									bestMapping = vpp;
								}
							}
						}
					}
					if (bestMapping != null) {
						nodeMappings.add(bestMapping);
					}
				}
				
				// if some of the parents are matched
				if (nodeMappings.size() > 0) {
					// find the best one
					VertexPair bestMap = nodeMappings.getFirst();
					for (int i = 1; i < nodeMappings.size(); i++) {
						if (nodeMappings.get(i).getWeight() > bestMap.getWeight()) {
							bestMap = nodeMappings.get(i);
						}
					}
					
					Vertex newGW = new Vertex(GWType.xor, Graph.getNextId());
					newGW.setAddedGW(true);
					HashSet<String> labels = g1.removeEdge(bestMap.getLeft().getID(), g1Before.getID());
					g1Before.removeParent(bestMap.getLeft().getID());
					bestMap.getLeft().removeChild(g1Before.getID());
					
					g1.addVertex(newGW);
					g1.connectVertices(newGW, g1Before, labels);
					g1.connectVertices(bestMap.getLeft(), newGW, labels);
					
					possibleMatches.add(new VertexPair(newGW, gw));
				}
			} else  if (lookParents == 2) {
				LinkedList<Vertex> childGWs = new LinkedList<Vertex>();
				LinkedList<Vertex> nochildGWs = new LinkedList<Vertex>();
				for (Vertex ch : gw.getChildren()) {
					if (ch.getType().equals(Type.gateway)) {
						childGWs.add(ch);
					}
					else {
						nochildGWs.add(ch);
					}
				}
				
				LinkedList<VertexPair> nodeMappings = AssingmentProblem.getMappingsVetrex(g1Vertices, nochildGWs, threshold, stemmer, lookParents);
				
				for (Vertex childGW : childGWs) {
					LinkedList<Vertex> toProcess = getChildGWs(childGW);
					VertexPair bestMapping = null;
					for (Vertex pr : toProcess) {
						LinkedList<Vertex> toGW = new  LinkedList<Vertex>();
						toGW.add(pr);
						LinkedList<VertexPair> nodeMappingsGW = AssingmentProblem.getMappingsVetrex(g1Vertices, toGW, threshold, stemmer, lookParents);
						nodeMappingsGW.addAll(AssingmentProblem.getMappingsVetrex(g1Vertices, pr.getChildren(), threshold, stemmer, lookParents));
						for (VertexPair vpp : nodeMappingsGW) {
							if (vpp.getWeight() > 0) {
								if (bestMapping  == null) {
									bestMapping = vpp;
								}
								else if (vpp.getWeight() > bestMapping.getWeight()) {
									bestMapping = vpp;
								}
							}
						}
					}
					if (bestMapping != null) {
						nodeMappings.add(bestMapping);
					}
				}
				
				// if some of the parents are matched
				if (nodeMappings.size() > 0) {
					// find the best one
					VertexPair bestMap = nodeMappings.getFirst();
					for (int i = 1; i < nodeMappings.size(); i++) {
						if (nodeMappings.get(i).getWeight() > bestMap.getWeight()) {
							bestMap = nodeMappings.get(i);
						}
					}

					Vertex newGW = new Vertex(GWType.xor, Graph.getNextId());

					newGW.setAddedGW(true);
					HashSet<String> labels = g1.removeEdge(g1Before.getID(), bestMap.getLeft().getID());
					g1Before.removeChild(bestMap.getLeft().getID());
					bestMap.getLeft().removeParent(g1Before.getID());
					
					g1.addVertex(newGW);
					g1.connectVertices(g1Before, newGW, labels);
					g1.connectVertices(newGW, bestMap.getLeft(), labels);
					
					possibleMatches.add(new VertexPair(newGW, gw));
				}
			}
		}

		return possibleMatches;
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
