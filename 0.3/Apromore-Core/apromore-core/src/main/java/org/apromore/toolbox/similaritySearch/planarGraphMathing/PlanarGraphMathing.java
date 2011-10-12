package org.apromore.toolbox.similaritySearch.planarGraphMathing;

import java.util.LinkedList;

import org.apromore.toolbox.similaritySearch.common.Settings;
import org.apromore.toolbox.similaritySearch.common.VertexPair;
import org.apromore.toolbox.similaritySearch.common.similarity.AssingmentProblem;
import org.apromore.toolbox.similaritySearch.common.stemmer.SnowballStemmer;
import org.apromore.toolbox.similaritySearch.graph.Graph;
import org.apromore.toolbox.similaritySearch.graph.Vertex;
import org.apromore.toolbox.similaritySearch.graph.Vertex.Type;

public class PlanarGraphMathing {

	LinkedList<VertexPair> nodesToVisit = new LinkedList<VertexPair>();
	static LinkedList<VertexPair> mappings = new LinkedList<VertexPair>();

	/**
	 * Finds matching regions. Also adds gateways, if in one modelass, the gateway exist and in other modelass does not
	 * exist. Also matches gateways of different types. 
	 * @param g1
	 * @param g2
	 * @param threshold
	 * @param stemmer
	 * @return
	 */
	public static MappingRegions findMatchWithGWAdding(Graph g1, Graph g2, double threshold, SnowballStemmer stemmer){
		
		LinkedList<VertexPair> process = new LinkedList<VertexPair>();
		LinkedList<VertexPair> processed = new LinkedList<VertexPair>();
		
		MappingRegions map = new MappingRegions();
		
		if (stemmer == null) {
			stemmer = Settings.getEnglishStemmer();
		}
		
		LinkedList<VertexPair> mappings = AssingmentProblem.getMappingsGraph(g1, g2, threshold, stemmer);
		
		if (mappings == null || mappings.size() == 0) {
			return map;
		}
		
		VertexPair v =  mappings.removeFirst();
		
		process.clear();
		processed.clear();
		
		process.add(v);
		
		while (true) {
			LinkedList<VertexPair> mapRegion = new LinkedList<VertexPair>();
			if (process.size() == 0) {
				break;
			}

			// map parents
			while(true) {
				if (process.size() == 0) {
					break;
				}
				
				VertexPair toProcess = process.getFirst();
				
				// match parents
				LinkedList<Vertex> leftParents = removeVertices((LinkedList<Vertex>) toProcess.getLeft().getParentsList());
				LinkedList<Vertex> rightParents = removeVertices((LinkedList<Vertex>) toProcess.getRight().getParentsList());
				
				LinkedList<VertexPair> nodeMappings = AssingmentProblem.getMappingsVetrex(leftParents, rightParents, threshold, stemmer, 1);
				for(VertexPair vp : nodeMappings) {
					if(!hasProcessed(processed, vp) && !hasProcessed(process, vp)) {
						process.add(vp);
					}
				}
				
				// match children
				LinkedList<Vertex> leftChildren = removeVertices((LinkedList<Vertex>) toProcess.getLeft().getChildrenList());
				LinkedList<Vertex> rightChildren = removeVertices((LinkedList<Vertex>) toProcess.getRight().getChildrenList());
				
				nodeMappings = AssingmentProblem.getMappingsVetrex(leftChildren, rightChildren, threshold, stemmer, 2);
				for(VertexPair vp : nodeMappings) {
					if(!hasProcessed(processed, vp) && !hasProcessed(process, vp)) {
						process.add(vp);
					}
				}
				process.remove(toProcess);
				processed.add(toProcess);
				mapRegion.add(toProcess);
			}
			
			if (mapRegion.size() > 0) {
				map.addRegion(mapRegion);
			}

			LinkedList<VertexPair> mappingsCopy = new LinkedList<VertexPair>(mappings);
			
			for (VertexPair v1 : mappingsCopy) {
				// this has already processed
				if (hasProcessed(processed, v1)) {
					mappings.remove(v1);
				}
				else {
					process.add(v1);
					break;
				}
			}
		}
		return map;
	}

	
	static LinkedList<Vertex> removeVertices(LinkedList<Vertex> vList) {
		LinkedList<Vertex> toReturn = new LinkedList<Vertex>();
		
		if (vList == null) {
			return toReturn;
		}
		
		for(Vertex v : vList) {
			if ((Settings.considerGateways || (!Settings.considerGateways && v.getType() != Type.gateway))
					&& (Settings.considerEvents || (!Settings.considerEvents && v.getType() != Type.event))) {
				toReturn.add(v);
			}
		}
		
		return toReturn;
	}
	
	static double calculateWeight(LinkedList<VertexPair> processedVertices){
		double result = 0;
		
		for(VertexPair vp : processedVertices) {
			result += vp.getWeight();
		}
		return result;
	}
	
	static boolean hasProcessed(LinkedList<VertexPair> processedVertices, VertexPair vp) {
		
		for (VertexPair processed : processedVertices) {
			if(processed.getLeft().getID() == vp.getLeft().getID() || processed.getRight().getID() == vp.getRight().getID()) {
				return true;
			}
		}
		return false;
	}
	
	public static class MappingRegions {
		
		private LinkedList<LinkedList<VertexPair>> regions = new LinkedList<LinkedList<VertexPair>>();
		
		public void addRegion(LinkedList<VertexPair> region) {
			regions.add(region);
		}
		
		public LinkedList<LinkedList<VertexPair>> getRegions () {
			return regions;
		}
	}
	
}
