package org.apromore.toolbox.similaritySearch.common.algos;


import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.apromore.toolbox.similaritySearch.common.Settings;
import org.apromore.toolbox.similaritySearch.common.similarity.AssingmentProblem;
import org.apromore.toolbox.similaritySearch.common.similarity.NodeSimilarity;
import org.apromore.toolbox.similaritySearch.graph.Graph;
import org.apromore.toolbox.similaritySearch.graph.Vertex;

/**
 * Class that implements the algorithm to compute the edit distance between two
 * SimpleGraph instances. Use the algorithm by calling the constructor with the two
 * SimpleGraph instances between which you want to compute the edit distance. Then call
 * compute(), which will return the edit distance.
 */
public class GraphEditDistanceGreedy extends DistanceAlgoAbstr implements DistanceAlgo {

	public int nrSubstitudedVertices = 0;
	
	private Set<TwoVertices> times(List<Vertex> a, List<Vertex> b, double labelTreshold){
		Set<TwoVertices> result = new HashSet<TwoVertices>();
		for (Vertex ea: a){
			for (Vertex eb: b){
//				System.out.println(">> " + ea+ " <> "+ eb);
				double similarity = NodeSimilarity.findNodeSimilarity(ea, eb, labelTreshold);
				if (ea.getType().equals(Vertex.Type.gateway) && eb.getType().equals(Vertex.Type.gateway) 
						&& similarity >= cedcutoff) {
//					System.out.println(">> " + ea+ " <> "+ eb + " -> "+ similarity);
					result.add(new TwoVertices(ea.getID(), eb.getID(), 1 - similarity));
				} else if ((ea.getType().equals(Vertex.Type.event) && eb.getType().equals(Vertex.Type.event) 
						|| ea.getType().equals(Vertex.Type.function) && eb.getType().equals(Vertex.Type.function)) &&
						AssingmentProblem.canMap(ea, eb) && similarity >= ledcutoff){
//					System.out.println(">> " + ea+ " <> "+ eb + " -> "+ similarity);
					result.add(new TwoVertices(ea.getID(), eb.getID(), 1 - similarity));
				}
			}
		}
		return result;
	}

	public Set<TwoVertices> compute(Graph sg1, Graph sg2){
		init(sg1,sg2);

		//INIT
		BestMapping mapping = new BestMapping();
		Set<TwoVertices> openCouples = times(sg1.getVertices(), sg2.getVertices(), ledcutoff);
		double shortestEditDistance = Double.MAX_VALUE;
		Random randomized = new Random();
		int stepn = 0;
		//STEP
		boolean doStep = true;
		while (doStep){
			doStep = false;
			stepn++;
			Vector<TwoVertices> bestCandidates = new Vector<TwoVertices>();
			double newShortestEditDistance = shortestEditDistance;
//			long s1 = System.currentTimeMillis();
//			System.out.println("step : "+stepn + " ; "+ openCouples.size());
			for (TwoVertices couple: openCouples){
//				System.out.println(">> PROCESSING COUPLE : " + sg1.getVertexMap().get(couple.v1)+ " <> "+ sg2.getVertexMap().get(couple.v2));

//				long t1 = System.currentTimeMillis();
				double newEditDistance = this.editDistance(mapping, couple); 
//				System.out.println("\t edit distance : " + newEditDistance + " ; shortest " + shortestEditDistance);

//				long t2 = System.currentTimeMillis();
//				System.out.println((t2-t1)+ " ms: openpair "+sg1.getLabel(couple.v1) + " "+ sg2.getLabel(couple.v2) + " "+newEditDistance+ " "+mapping.size());
				if (newEditDistance < newShortestEditDistance){
					bestCandidates = new Vector<TwoVertices>();
					bestCandidates.add(couple);
//					System.out.println("\t>> ADD COUPLE to bestcandidates(clean bestcand) : " + sg1.getVertexMap().get(couple.v1)+ " <> "+ sg2.getVertexMap().get(couple.v2));
					newShortestEditDistance = newEditDistance;
				}else if (newEditDistance == newShortestEditDistance){
					bestCandidates.add(couple);
//					System.out.println("\t>> ADD COUPLE to bestcandidates : " + sg1.getVertexMap().get(couple.v1)+ " <> "+ sg2.getVertexMap().get(couple.v2));

				}
			}

			if (bestCandidates.size() > 0){
				//Choose a random candidate
				TwoVertices couple = bestCandidates.get(randomized.nextInt(bestCandidates.size()));

				Set<TwoVertices> newOpenCouples = new HashSet<TwoVertices>();
				for (TwoVertices p: openCouples){
//					System.out.println("\t\t\t<><>openCouples : " + sg1.getVertexMap().get(p.v1)+ " <> "+ sg2.getVertexMap().get(p.v2));
					if (!p.v1.equals(couple.v1) && !p.v2.equals(couple.v2)){
						newOpenCouples.add(p);
//						System.out.println("\t\t\t\tADDING TO NEW MAPPP!!!");
					}
				}
				openCouples = newOpenCouples;

				mapping.addPair(couple);
//				System.out.println("<><>MAPPING ADD COUPLE : " + sg1.getVertexMap().get(couple.v1)+ " <> "+ sg2.getVertexMap().get(couple.v2));
				shortestEditDistance = newShortestEditDistance;
				doStep = true;
			}
//			long s2 = System.currentTimeMillis();
//			System.out.println("step took time : "+(s2-s1));
		}
		
//		for (TwoVertices pair : mapping.getMapping()) {
//			Vertex v1 = sg1.getVertexMap().get(pair.v1);
//			Vertex v2 = sg2.getVertexMap().get(pair.v2);
//			System.out.println(v1 + " <> "+ v2 + " " + (1-pair.weight));
//		}
//		
//		System.out.println("substitutedV "+ mapping.getMapping().size() + " ; substituedEdges "+ mapping.nrMappedEdges + " similarity "+ (1-shortestEditDistance));

//		System.out.println("shortest ed : "+shortestEditDistance);
		//Return the smallest edit distance
		return mapping.mapping;
	}
	
	public double computeGED(Graph sg1, Graph sg2){
		return computeGED(sg1, sg2, false);
	}
	
	public double computeGED(Graph sg1, Graph sg2, boolean print) {
		init(sg1,sg2);

		//INIT
		BestMapping mapping = new BestMapping();
		Set<TwoVertices> openCouples = times(sg1.getVertices(), sg2.getVertices(), ledcutoff);
		double shortestEditDistance = Double.MAX_VALUE;
		Random randomized = new Random();
		int stepn = 0;
		//STEP
		boolean doStep = true;
		while (doStep){
			doStep = false;
			stepn++;
			Vector<TwoVertices> bestCandidates = new Vector<TwoVertices>();
			double newShortestEditDistance = shortestEditDistance;
//			long s1 = System.currentTimeMillis();
//			System.out.println("step : "+stepn + " ; "+ openCouples.size());
			for (TwoVertices couple: openCouples){
//				System.out.println(">> PROCESSING COUPLE : " + sg1.getVertexMap().get(couple.v1)+ " <> "+ sg2.getVertexMap().get(couple.v2));

//				long t1 = System.currentTimeMillis();
				double newEditDistance = this.editDistance(mapping, couple); 
				
//				System.out.println("\t edit distance : " + newEditDistance + " ; shortest " + shortestEditDistance);

//				long t2 = System.currentTimeMillis();
//				System.out.println((t2-t1)+ " ms: openpair "+sg1.getLabel(couple.v1) + " "+ sg2.getLabel(couple.v2) + " "+newEditDistance+ " "+mapping.size());
				if (newEditDistance < newShortestEditDistance){
					bestCandidates = new Vector<TwoVertices>();
					bestCandidates.add(couple);
//					System.out.println("\t>> ADD COUPLE to bestcandidates(clean bestcand) : " + sg1.getVertexMap().get(couple.v1)+ " <> "+ sg2.getVertexMap().get(couple.v2));
					newShortestEditDistance = newEditDistance;
				}else if (newEditDistance == newShortestEditDistance){
					bestCandidates.add(couple);
//					System.out.println("\t>> ADD COUPLE to bestcandidates : " + sg1.getVertexMap().get(couple.v1)+ " <> "+ sg2.getVertexMap().get(couple.v2));

				}
			}

			if (bestCandidates.size() > 0){
				//Choose a random candidate
				TwoVertices couple = bestCandidates.get(randomized.nextInt(bestCandidates.size()));

				Set<TwoVertices> newOpenCouples = new HashSet<TwoVertices>();
				for (TwoVertices p: openCouples){
//					System.out.println("\t\t\t<><>openCouples : " + sg1.getVertexMap().get(p.v1)+ " <> "+ sg2.getVertexMap().get(p.v2));
					if (!p.v1.equals(couple.v1) && !p.v2.equals(couple.v2)){
						newOpenCouples.add(p);
//						System.out.println("\t\t\t\tADDING TO NEW MAPPP!!!");
					}
				}
				openCouples = newOpenCouples;

				mapping.addPair(couple);
//				System.out.println("<><>MAPPING ADD COUPLE : " + sg1.getVertexMap().get(couple.v1)+ " <> "+ sg2.getVertexMap().get(couple.v2));
				shortestEditDistance = newShortestEditDistance;
				doStep = true;
			}
//			long s2 = System.currentTimeMillis();
//			System.out.println("step took time : "+(s2-s1));
		}
		
		nrSubstitudedVertices = mapping.size();
		
		if (print) {
			for (TwoVertices pair : mapping.getMapping()) {
				Vertex v1 = sg1.getVertexMap().get(pair.v1);
				Vertex v2 = sg2.getVertexMap().get(pair.v2);
				
//				System.out.println(v1 + " <> "+ v2 + " " + NodeSimilarity.findNodeSimilarity(v1, v2, ledcutoff));
	
			}
		}
//		System.out.println("substitutedV "+ mapping.getMapping().size() + " ; substituedEdges "+ mapping.nrMappedEdges);
//		System.out.println("shortest ed : "+shortestEditDistance);
		//Return the smallest edit distance
		return shortestEditDistance;
	}

}
