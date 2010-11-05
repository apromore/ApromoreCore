package org.apromore.toolbox.similaritySearch.algorithms;


import java.util.HashSet;
import java.util.LinkedList;

import org.apromore.toolbox.similaritySearch.common.VertexPair;
import org.apromore.toolbox.similaritySearch.common.algos.GraphEditDistanceGreedy;
import org.apromore.toolbox.similaritySearch.common.algos.TwoVertices;
import org.apromore.toolbox.similaritySearch.common.similarity.AssingmentProblem;
import org.apromore.toolbox.similaritySearch.graph.Edge;
import org.apromore.toolbox.similaritySearch.graph.Graph;
import org.apromore.toolbox.similaritySearch.graph.Vertex;
import org.apromore.toolbox.similaritySearch.graph.Vertex.GWType;
import org.apromore.toolbox.similaritySearch.graph.Vertex.Type;
import org.apromore.toolbox.similaritySearch.planarGraphMathing.PlanarGraphMathing.MappingRegions;



public class MergeModels {

	public static Graph mergeModels (Graph g1, Graph g2, 
			boolean removeEnt, 
			String algortithm, 
			double ...param) {
		
		Graph merged = new Graph();
		long startTime = System.currentTimeMillis();

		merged.addVertices(g1.getVertices());
		merged.addEdges(g1.getEdges());
		merged.addVertices(g2.getVertices());
		merged.addEdges(g2.getEdges());
		
		// add resources and objects
		merged.getResources().putAll(g1.getResources());
		merged.getResources().putAll(g2.getResources());
		merged.getObjects().putAll(g1.getObjects());
		merged.getObjects().putAll(g2.getObjects());
		
		LinkedList<VertexPair> mapping = new LinkedList<VertexPair>();
		
		if (algortithm.equals("Greedy")) {
			GraphEditDistanceGreedy gedepc = new GraphEditDistanceGreedy();
			Object weights[] = {"ledcutoff", param[0],
								"cedcutoff", param[1],
								"vweight", param[2], 
								"sweight", param[3], 
								"eweight", param[4]};
			
			gedepc.setWeight(weights);
			
			for (TwoVertices pair : gedepc.compute(g1, g2)) {
				Vertex v1 = g1.getVertexMap().get(pair.v1);
				Vertex v2 = g2.getVertexMap().get(pair.v2);
				if (v1.getType().equals(v2.getType())) {
					mapping.add(new VertexPair(v1, v2, pair.weight));
				}
			}
		} else if (algortithm.equals("Hungarian")) {
			mapping = AssingmentProblem.getMappingsVetrexUsingNodeMapping(g1, g2, param[0], param[1]);
		}
		
//		for (VertexPair vp : mapping) {
//			System.out.println(vp.getLeft() + " <> "+ vp.getRight() + " -> "+vp.getWeight());
//		}
//		System.out.print("**********************************\n");
		
		// clean mappings from mappings that conflict
		// TODO uncomment
//		removeNonDominanceMappings(mapping);
		
		if (removeEnt) {
			g1.fillDominanceRelations();
			g2.fillDominanceRelations();
			removeNonDominanceMappings2(mapping);
		}
		
		MappingRegions mappingRegions = findMaximumCommonRegions(g1, g2, mapping);
		
//		for (LinkedList<VertexPair> mapping1 : mappingRegions.getRegions()) {
//			System.out.println("-------------------------------------");
//			for (VertexPair vp : mapping1) {
//				System.out.println(vp.getLeft().getLabel()+" ("+vp.getLeft().getID()+") "+ " <> "+ vp.getRight().getLabel()+" ("+vp.getRight().getID()+") " + " "+vp.getLeft().getChildren().size() + " "+vp.getRight().getChildren().size());
//			}
//			System.out.print(mapping1.size()+"\t");
//		}
//		System.out.print("\n");
		
		for (LinkedList<VertexPair> region : mappingRegions.getRegions()) {
			for (VertexPair vp : region) {
				LinkedList<Vertex> nodesToProcess = new LinkedList<Vertex>();
				for (Vertex c : vp.getRight().getChildren()) {
					// the child is also part of the mapping
					// remove the edge from the merged model
					if (containsVertex(region, c)) {
						nodesToProcess.add(c);
					}
				}
				for (Vertex c : nodesToProcess) {
					HashSet<String> labels = merged.removeEdge(vp.getRight().getID(), c.getID());
					
					vp.getRight().removeChild(c.getID());
					c.removeParent(vp.getRight().getID());
					
					Vertex cLeft = getMappingPair(mapping, c);
					Edge e = merged.containsEdge(vp.getLeft().getID(), cLeft.getID());
					if (e != null) {
						e.addLabels(labels);
					}
				}
			}
			// add annotations for the labels
			for (VertexPair vp : region) {
				Vertex mappingRight = vp.getRight();
				vp.getLeft().addAnnotations(mappingRight.getAnnotationMap());
				vp.getLeft().objectRefs.addAll(mappingRight.objectRefs);
				vp.getLeft().resourceRefs.addAll(mappingRight.resourceRefs);
				
//				// just in case the annotations in the xml file were incorrect
//				// or missing - in case the model was not configurable
//				// if you are messing with annotations, this is your own fault
//				// that things are not working
//				if (mappingRight.getType().equals(Vertex.Type.gateway)) {
//					if (Graph.isJoin(mappingRight)) {
//						HashSet<String> labels = merged.getEdgeLabels(mappingRight.getID(), mappingRight.getChildren().get(0).getID());
//						vp.getLeft().addAnnotations(labels, mappingRight.getLabel());
//					} 
//					else {
//						System.out.println("dfjhjsdfh "+ mappingRight.getID()+ " " + mappingRight.getGWType()+" " + mappingRight.getParents().size() 
//								+ " parents "+ mappingRight.getParents().size() + " children ? "+ mappingRight.getChildren().size() + "is splitt???" 
//								);
//						HashSet<String> labels = merged.getEdgeLabels(mappingRight.getID(), mappingRight.getParents().get(0).getID());
//						vp.getLeft().addAnnotations(labels, mappingRight.getLabel());
//					}
//				} else {
//					// not a leaf
//					if (mappingRight.getChildren().size() > 0) {
//						HashSet<String> labels = merged.getEdgeLabels(mappingRight.getID(), mappingRight.getChildren().get(0).getID());
//						vp.getLeft().addAnnotations(labels, mappingRight.getLabel());
//					} else if (mappingRight.getParents().size() > 0) {
//						HashSet<String> labels = merged.getEdgeLabels(mappingRight.getID(), mappingRight.getParents().get(0).getID());
//						vp.getLeft().addAnnotations(labels, mappingRight.getLabel());
//					}
//				}
			}
		}
		LinkedList<Vertex> toRemove = new LinkedList<Vertex>();
		// check if some vertices must be removed
		for (Vertex v : merged.getVertices()) {
			if (v.getParents().size() == 0 && v.getChildren().size() == 0) {
				toRemove.add(v);
			}
		}
		
		for (Vertex v : toRemove) {
			merged.removeVertex(v.getID());
		}
		
		for (LinkedList<VertexPair> region : mappingRegions.getRegions()) {
			for (VertexPair vp : region) {
				boolean addgw = true;
				boolean addgwr = true;
				
				for (Vertex p : vp.getLeft().getParents()) {
					if (containsVertex(region, p)) {
						addgw = false;
						break;
					}
				}
				// check parents from second model
				// maybe the nodes are concurrent in one model but not in the other
				for (Vertex p : vp.getRight().getParents()) {
					if (containsVertex(region, p)) {
						addgwr = false;
						break;
					}
				}
				if ((addgw || addgwr) && vp.getLeft().getParents().size() == 1 &&
						vp.getRight().getParents().size() == 1) {
					
					Vertex newGw = new Vertex(GWType.xor, Graph.getNextId());
					newGw.setConfigurable(true);
					merged.addVertex(newGw);
					
					Vertex v1 = vp.getLeft().getParents().get(0);
					HashSet<String> s1 = merged.removeEdge(v1.getID(), vp.getLeft().getID());
					v1.removeChild(vp.getLeft().getID());
					vp.getLeft().removeParent(v1.getID());
					merged.connectVertices(v1, newGw, s1);
					
					Vertex v2 = vp.getRight().getParents().get(0);
					HashSet<String> s2 = merged.removeEdge(v2.getID(), vp.getRight().getID());
					v2.removeChild(vp.getRight().getID());
					vp.getRight().removeParent(v2.getID());
					merged.connectVertices(v2, newGw, s2);
					
					HashSet<String> s3 = new HashSet<String>(s1);
					s3.addAll(s2);
					merged.connectVertices(newGw, vp.getLeft(), s3);
					newGw.addAnnotationsForGw(s3);
					
				}
			}
		}
		for (LinkedList<VertexPair> region : mappingRegions.getRegions()) {
			for (VertexPair vp : region) {
				boolean addgw = true;
				boolean addgwr = true;
				for (Vertex ch : vp.getLeft().getChildren()) {
					if (containsVertex(region, ch)) {
						addgw = false;
						break;
					}
				}
				
				// check parents from second model
				// maybe the nodes are concurrent in one model but not in the other
				for (Vertex ch : vp.getRight().getChildren()) {
					if (containsVertex(region, ch)) {
						addgwr = false;
						break;
					}
				}
				if ((addgw || addgwr) && vp.getLeft().getChildren().size() == 1 &&
						vp.getRight().getChildren().size() == 1) {
					
					Vertex newGw = new Vertex(GWType.xor, Graph.getNextId());
					newGw.setConfigurable(true);
					merged.addVertex(newGw);
					
					Vertex v1 = vp.getLeft().getChildren().get(0);
					HashSet<String> s1 = merged.removeEdge(vp.getLeft().getID(), v1.getID());
					vp.getLeft().removeChild(v1.getID());
					v1.removeParent(vp.getLeft().getID());
					merged.connectVertices(newGw, v1, s1);
					
					Vertex v2 = vp.getRight().getChildren().get(0);
					HashSet<String> s2 = merged.removeEdge(vp.getRight().getID(), v2.getID());
					vp.getRight().removeChild(v2.getID());
					v2.removeParent(vp.getRight().getID());
					merged.connectVertices(newGw, v2, s2);
					
					HashSet<String> s3 = new HashSet<String>(s1);
					s3.addAll(s2);
					merged.connectVertices(vp.getLeft(), newGw, s3);
					newGw.addAnnotationsForGw(s3);
				}
			}
		}
		
		mergeConnectors(mappingRegions, merged, mapping);
		
		toRemove = new LinkedList<Vertex>();
		// check if some vertices must be removed
		for (Vertex v : merged.getVertices()) {
			if (v.getParents().size() == 0 && v.getChildren().size() == 0) {
				toRemove.add(v);
			}
		}
		
		for (Vertex v : toRemove) {
			merged.removeVertex(v.getID());
		}

		int[] gwInf = merged.getNrOfConfigGWs();
//		System.out.println(gwInf[4]+"\t"+gwInf[0]);
		
		long mergeTime = System.currentTimeMillis();
		merged.cleanGraph();
		
		gwInf = merged.getNrOfConfigGWs();
//		System.out.println(gwInf[4]+"\t"+gwInf[0]);
		
		// labels for all edges should be added to the model
		for (Edge e : merged.getEdges()) {
			e.addLabelToModel();
		}
		
		long cleanTime = System.currentTimeMillis();
		
		merged.mergetime = mergeTime - startTime;
		merged.cleanTime = cleanTime - startTime;
		
		merged.name = "";
		for (String l : merged.getEdgeLabels()) {
			merged.name += l + ",";
		}
		merged.name = merged.name.substring(0, merged.name.length() - 1);
		merged.ID = String.valueOf(Graph.getNextId());
		
		return merged;
	}
	
	@SuppressWarnings("unused")
	private void removeNonDominanceMappings(LinkedList<VertexPair> mapping) {
		
		LinkedList<VertexPair> removeList = new LinkedList<VertexPair>();
		int i = 0;
		
		for (VertexPair vp : mapping) {
			i++;
			// the mapping is already in removed list
			if (removeList.contains(vp)) {
				continue;
			}
			
			for (int j = i; j < mapping.size(); j++) {
				VertexPair vp1 = mapping.get(j);
				if (vp.getLeft().getID() == vp1.getLeft().getID() || 
						vp.getRight().getID() == vp1.getRight().getID()) {
					continue;
				}
				boolean dominanceInG1 = containsInDownwardsPath(vp.getLeft(), vp1.getLeft());
				boolean dominanceInG2 = containsInDownwardsPath(vp.getRight(), vp1.getRight());
				
//				System.out.println(vp.getLeft().getID() +" <> "+ vp.getRight().getID() + " : "+dominanceInG1 + " "+vp1.getLeft().getID() +" <> "+ vp1.getRight().getID()+" : "+ dominanceInG2);
				// dominance rule is broken 
				if (dominanceInG1 && !dominanceInG2 || !dominanceInG1 && dominanceInG2) {
					// remove 2 pairs from the pairs list and start with the new pair
//					System.out.println(">>*REMOVING MAPPINGS");
//					System.out.println(vp.getLeft().getID() +" <> "+ vp.getRight().getID());
//					System.out.println(vp1.getLeft().getID() +" <> "+ vp1.getRight().getID());
					removeList.add(vp);
					removeList.add(vp1);
					break;
				}
			}
		}

		// remove conflicting mappings
		for (VertexPair vp : removeList) {
			mapping.remove(vp);
		}
	}
	
	@SuppressWarnings("unused")
	private void removeNonDominanceMappings1(LinkedList<VertexPair> mapping) {
		
		LinkedList<VertexPair> removeList = new LinkedList<VertexPair>();
		int i = 0;
		
		for (VertexPair vp : mapping) {
			i++;
			// the mapping is already in removed list
			if (removeList.contains(vp)) {
				continue;
			}
			
			// TODO - if there exists path where A dominances B, then this dominances B
			// even when this is a cycle
			for (int j = i; j < mapping.size(); j++) {
				VertexPair vp1 = mapping.get(j);
				if (vp.getLeft().getID() == vp1.getLeft().getID() || 
						vp.getRight().getID() == vp1.getRight().getID()) {
					continue;
				}
				
				// dominance rule is broken 
				if (vp.getLeft().dominance.contains(vp1.getLeft().getID()) 
						&& vp1.getRight().dominance.contains(vp.getRight().getID()) 
					|| vp1.getLeft().dominance.contains(vp.getLeft().getID()) 
						&& vp.getRight().dominance.contains(vp1.getRight().getID())) {
					// remove 2 pairs from the pairs list and start with the new pair
					System.out.println(">>*REMOVING MAPPINGS");
//					System.out.println(vp.getLeft() +" <> "+ vp.getRight());
//					System.out.println(vp1.getLeft() +" <> "+ vp1.getRight());
					removeList.add(vp);
					removeList.add(vp1);
					break;
				}
			}
		}

		// remove conflicting mappings
		for (VertexPair vp : removeList) {
			mapping.remove(vp);
		}
	}

	// implementation of Marlon new dominance mapping relation
	private static void removeNonDominanceMappings2(LinkedList<VertexPair> mapping) {
		
		LinkedList<VertexPair> removeList = new LinkedList<VertexPair>();
		int i = 0;
		
		for (VertexPair vp : mapping) {
			i++;
			// the mapping is already in removed list
			if (removeList.contains(vp)) {
				continue;
			}
			
			for (int j = i; j < mapping.size(); j++) {
				
				VertexPair vp1 = mapping.get(j);
				
				// the mapping is already in removed list
				if (removeList.contains(vp1)) {
					continue;
				}
				
				// same starting or ending point of models
				if (vp.getLeft().getID() == vp1.getLeft().getID() || 
						vp.getRight().getID() == vp1.getRight().getID()) {
					continue;
				}
				
				// dominance rule is broken 
				if ((vp.getLeft().dominance.contains(vp1.getLeft().getID()) 
						&& vp1.getRight().dominance.contains(vp.getRight().getID())
						&& !(vp1.getLeft().dominance.contains(vp.getLeft().getID()) 
								|| vp.getRight().dominance.contains(vp1.getRight().getID())))
					|| (vp1.getLeft().dominance.contains(vp.getLeft().getID()) 
						&& vp.getRight().dominance.contains(vp1.getRight().getID())
						&& !(vp.getLeft().dominance.contains(vp1.getLeft().getID()) 
								|| vp1.getRight().dominance.contains(vp.getRight().getID())))) {
					// remove 2 pairs from the pairs list and start with the new pair
//					System.out.println(">>*REMOVING MAPPINGS");
					System.out.println(">>*REMOVING MAPPINGS" + vp.getLeft() +" <> "+ vp.getRight());
					System.out.println(">>*REMOVING MAPPINGS" + vp1.getLeft() +" <> "+ vp1.getRight());
					removeList.add(vp);
					removeList.add(vp1);
					break;
				}
			}
		}

		// remove conflicting mappings
		for (VertexPair vp : removeList) {
			mapping.remove(vp);
		}
	}
	
	private boolean containsInDownwardsPath(Vertex v1, Vertex v2){
		
		LinkedList<Vertex> toProcess = new LinkedList<Vertex>();
		toProcess.addAll(v1.getChildren());

		while (toProcess.size() > 0) {
			Vertex process = toProcess.removeFirst();
			if (process.getID() == v2.getID()) {
				return true;
			}
			toProcess.addAll(process.getChildren());
		}
		return false;
	}
	
	private static void mergeConnectors(MappingRegions mappingRegions, Graph merged, LinkedList<VertexPair> mapping) {
		for (LinkedList<VertexPair> region : mappingRegions.getRegions()) {
			for (VertexPair vp : region) {
				if (vp.getLeft().getType().equals(Type.gateway)) {
					boolean makeConf = false;
					LinkedList<Vertex> toProcess = new LinkedList<Vertex>();
					for (Vertex p : vp.getRight().getParents()) {
						if (!containsVertex(region, p)) {
							toProcess.add(p);
						}
					}
					
					for (Vertex p : toProcess) {
						makeConf = true;
						HashSet<String> l = merged.removeEdge(p.getID(), vp.getRight().getID());
						p.removeChild(vp.getRight().getID());
						vp.getRight().removeParent(p.getID());
						merged.connectVertices(p, vp.getLeft(), l);
					}
					toProcess = new LinkedList<Vertex>();
					
					for (Vertex p : vp.getRight().getChildren()) {
						if (!containsVertex(region, p)) {
							toProcess.add(p);
						}
					}
					
					for (Vertex p : toProcess) {
						makeConf = true;
						HashSet<String> l = merged.removeEdge(vp.getRight().getID(), p.getID());
						p.removeParent(vp.getRight().getID());
						vp.getRight().removeChild(p.getID());
						merged.connectVertices(vp.getLeft(), p, l);
					}
					if (makeConf) {
						vp.getLeft().setConfigurable(true);
					}
					if (!vp.getLeft().getGWType().equals(vp.getRight().getGWType())) {
						vp.getLeft().setGWType(GWType.or);
					}
				}
			}
		}
	}
	
	
	private static VertexPair findNextVertexToProcess(LinkedList<VertexPair> mapping, LinkedList<VertexPair> visited) {
		for (VertexPair vp : mapping) {
			VertexPair process = containsMapping(visited, vp.getLeft(), vp.getRight());
			if (process == null) {
				return vp;
			}
		}
		return null;
	}
	
	private static VertexPair containsMapping(LinkedList<VertexPair> mapping, Vertex left, Vertex right) {
		for (VertexPair vp : mapping) {
			if (vp.getLeft().getID() == left.getID() &&
					vp.getRight().getID() == right.getID()) {
				return vp;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unused")
	private static boolean containsMapping(LinkedList<VertexPair> mapping, VertexPair v) {
		for (VertexPair vp : mapping) {
			if (vp.getLeft().getID() == v.getLeft().getID() &&
					vp.getRight().getID() == v.getRight().getID()) {
				return true;
			}
		}
		return false;
	}
	
	public static MappingRegions findMaximumCommonRegions(Graph g1, Graph g2, LinkedList<VertexPair> mapping) {
//		System.out.println("FINDING REGIONS");
		MappingRegions map = new MappingRegions();
		LinkedList<VertexPair> visited = new LinkedList<VertexPair>();
		
		while (true) {
			VertexPair c = findNextVertexToProcess(mapping, visited);
			if (c == null) {
				break;
			}
			LinkedList<VertexPair> toVisit = new LinkedList<VertexPair>();
			LinkedList<VertexPair> mapRegion = new LinkedList<VertexPair>();
			
			toVisit.add(c);
			while (toVisit.size() > 0) {
				c = toVisit.removeFirst();
//				System.out.println(">>> process: "+c.getLeft().getLabel()+" ("+c.getLeft().getID()+") "+ " <> "+ c.getRight().getLabel()+" ("+c.getRight().getID()+") ");
				mapRegion.add(c);
				
				visited.add(c);
				for (Vertex pLeft : c.getLeft().getParents()) {
					for (Vertex pRight : c.getRight().getParents()) {
						VertexPair pairMap = containsMapping(mapping, pLeft, pRight);
						VertexPair containsMap = containsMapping(visited, pLeft, pRight);
						VertexPair containsMap1 = containsMapping(toVisit, pLeft, pRight);
//						System.out.println("\tp: add to visit???: "+pLeft.getLabel()+" ("+pLeft.getID()+") "+ " <> "+ pRight.getLabel()+" ("+pRight.getID()+") ");
						if (pairMap != null && containsMap == null && containsMap1 == null) {
//							System.out.println("\t\t adding to visit: "+pairMap.getLeft().getLabel()+" ("+pairMap.getLeft().getID()+") "+ " <> "+ pairMap.getRight().getLabel()+" ("+pairMap.getRight().getID()+") ");
							toVisit.add(pairMap);
						}
					}
				}
				
				for (Vertex pLeft : c.getLeft().getChildren()) {
					for (Vertex pRight : c.getRight().getChildren()) {
						VertexPair pairMap = containsMapping(mapping, pLeft, pRight);
						VertexPair containsMap = containsMapping(visited, pLeft, pRight);
						VertexPair containsMap1 = containsMapping(toVisit, pLeft, pRight);
//						System.out.println("\tc: add to visit???: "+pLeft.getLabel()+" ("+pLeft.getID()+") "+ " <> "+ pRight.getLabel()+" ("+pRight.getID()+") ");
						if (pairMap != null && containsMap == null && containsMap1 == null) {
//							System.out.println("\t\t adding to visit: "+pairMap.getLeft().getLabel()+" ("+pairMap.getLeft().getID()+") "+ " <> "+ pairMap.getRight().getLabel()+" ("+pairMap.getRight().getID()+") ");

							toVisit.add(pairMap);
						}
					}
				}

			}
			if (mapRegion.size() > 0) {
				map.addRegion(mapRegion);
			}
		}
		
		return map;
	}
	
	public static boolean containsVertex(LinkedList<VertexPair> mapping, Vertex v){
		
		for (VertexPair vp : mapping) {
			if (vp.getLeft().getID() == v.getID() || vp.getRight().getID() == v.getID()) {
				
				return true;
			}
		}
		return false;
	}
	
	public static Vertex getMappingPair(LinkedList<VertexPair> mapping, Vertex v){
		
		for (VertexPair vp : mapping) {
			if (vp.getLeft().getID() == v.getID()) {
				return vp.getRight();
			}
			else if (vp.getRight().getID() == v.getID()) {
				return vp.getLeft();
			}
		}
		return null;
	}
}
