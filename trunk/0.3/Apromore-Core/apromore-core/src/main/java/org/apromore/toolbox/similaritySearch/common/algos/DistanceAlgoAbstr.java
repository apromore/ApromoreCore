package org.apromore.toolbox.similaritySearch.common.algos;


import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apromore.toolbox.similaritySearch.graph.Graph;
import org.apromore.toolbox.similaritySearch.graph.Vertex;

public abstract class DistanceAlgoAbstr implements DistanceAlgo {

	public final static int EPSILON = -1; //means: 'no mapping'
	public final static double VERTEX_INSERTION_COST = 0.1; //Only for reproducing Luciano's results
	public final static double VERTEX_DELETION_COST = 0.9; //Only for reproducing Luciano's results

	protected Graph sg1;
	protected Graph sg2;
	protected int totalNrVertices;
	protected int totalNrEdges;

	double weightGroupedVertex;
	double weightSkippedVertex;
	double weightSkippedEdge;
	double weightSubstitutedVertex;
	double ledcutoff;
	double cedcutoff;
	boolean usepuredistance;
	int prunewhen;
	int pruneto;
	boolean useepsilon;
	boolean dogrouping;

	/**
	 * Sets the weights for:
	 * - skipping vertices (vweight)
	 * - substituting vertices (sweight)
	 * - skipping edges (eweight)
	 * - string edit similarity cutoff (ledcutoff)
	 * - use pure edit distance/ use weighted average distance (usepuredistance) 
	 *     Ad usepuredistance: weight is a boolean. If 1.0: uses the pure edit distance, if 0.0: uses weighted average of the fractions of skipped vertices, skipped edges and substitution score.
	 * - prune when recursion reaches this depth, 0.0 means no pruning (prunewhen)
	 * - prune to recursion of this depth (pruneto)
	 *
	 * The argument is an array of objects, interchangably a String ("vweight", "sweight", or "eweight")
	 * and a 0.0 <= Double <= 1.0 that is the value that should be set for the given weight.
	 * All other weights are set to 0.0. 
	 * 
	 * @param weights 
	 * Pre: for i mod 2 = 0: weights[i] instanceof String /\ weights[i] \in {"vweight", "sweight", or "eweight"}
	 * 		for i mod 2 = 1: weights[i] instanceof Double /\ 0.0 <= weights[i] <= 1.0
	 * 		for i: if i < weights.length(), then i+1 < weights.length() 
	 * Post: weight identified by weights[i] is set to weights[i+1]
	 * 		 all other weights are set to 0.0 
	 */
	public void setWeight(Object weights[]){
		this.weightGroupedVertex = 0.0;
		this.weightSkippedVertex = 0.0;
		this.weightSubstitutedVertex = 0.0;
		this.weightSkippedEdge = 0.0;
		this.ledcutoff = 0.0;
		this.cedcutoff = 0.0;
		this.usepuredistance = false;
		this.prunewhen = 0;
		this.pruneto = 0;
		this.useepsilon = false;
		this.dogrouping = false;

		for (int i = 0; i < weights.length; i=i+2){
			String wname = (String) weights[i];
			Double wvalue = (Double) weights[i+1];
			if (wname.equals("vweight")){
				this.weightSkippedVertex = wvalue;
			}else if (wname.equals("sweight")){
				this.weightSubstitutedVertex = wvalue;
			}else if (wname.equals("gweight")){
				this.weightGroupedVertex = wvalue;
			}else if (wname.equals("eweight")){
				this.weightSkippedEdge = wvalue;
			}else if (wname.equals("ledcutoff")){
				this.ledcutoff = wvalue;
			}else if (wname.equals("cedcutoff")){
				this.cedcutoff = wvalue;
			}else if (wname.equals("usepuredistance")){
				if (wvalue == 0.0){
					this.usepuredistance = false;
				}else{
					this.usepuredistance = true;
				}
			}else if (wname.equals("useepsilon")){
				if (wvalue == 0.0){
					this.useepsilon = false;
				}else{
					this.useepsilon = true;
				}
			}else if (wname.equals("dogrouping")){
				if (wvalue == 0.0){
					this.dogrouping = false;
				}else{
					this.dogrouping = true;
				}
			}else if (wname.equals("prunewhen")){
				this.prunewhen = wvalue.intValue();
			}else if (wname.equals("pruneto")){
				this.pruneto = wvalue.intValue();
			}else{
				System.err.println("ERROR: Invalid weight identifier: " + wname);
			}
		}
	}

	protected void init(Graph sg1, Graph sg2){
		this.sg1 = sg1;
		this.sg2 = sg2;
		totalNrVertices = sg1.getVertices().size() + sg2.getVertices().size();
		totalNrEdges = sg1.getEdges().size() + sg2.getEdges().size();
	}
	
	protected double computeScore(double skippedEdges, double skippedVertices, double substitutedVertices, double insertedVertices, double deletedVertices){
		if (usepuredistance){
			if (useepsilon){
				return weightSkippedVertex*(VERTEX_DELETION_COST*deletedVertices + VERTEX_INSERTION_COST*insertedVertices) + weightSkippedEdge*skippedEdges + weightSubstitutedVertex*2.0*substitutedVertices;
			}else{
				return weightSkippedVertex*skippedVertices + weightSkippedEdge*skippedEdges + weightSubstitutedVertex*2.0*substitutedVertices;
			}
		}else{
			//Return the total edit distance. Multiply each element with its weight.
			double vskip = skippedVertices / (1.0 * totalNrVertices);
			double vsubs = (2.0*substitutedVertices) / (1.0 * totalNrVertices - skippedVertices);
			double editDistance;
			if (totalNrEdges==0){
				editDistance = ((weightSkippedVertex * vskip) + (weightSubstitutedVertex * vsubs))/(weightSkippedVertex+weightSubstitutedVertex); 			
			}else{
				double eskip = (skippedEdges / (1.0 * totalNrEdges)); 
				editDistance = ((weightSkippedVertex * vskip) + (weightSubstitutedVertex * vsubs) + (weightSkippedEdge * eskip))/(weightSkippedVertex+weightSubstitutedVertex+weightSkippedEdge); 			
//				System.out.println(">>>> "+ editDistance+ " ((" + weightSkippedVertex + " * " + vskip + ") + (" + weightSubstitutedVertex + " * " + vsubs + ") + (" + weightSkippedEdge + " * " + eskip+ ")) / (" +weightSkippedVertex+ " + " +weightSubstitutedVertex+ " + " +weightSkippedEdge+ " ))");
			}
			return editDistance;
		}		
	}
	
//	protected double editDistance(Set<TwoVertices> m){
//		Set<String> verticesFrom1Used = new HashSet<String>();
//		Set<String> verticesFrom2Used = new HashSet<String>();
//
//		double epsilonSkippedVertices = 0.0;
//		double epsilonInsertedVertices = 0.0;
//		double epsilonDeletedVertices = 0.0;
//		double epsilonSkippedEdges = 0.0;
//		
//		//vid1tovid2 = m, but it is a mapping, so we can more efficiently find the 
//		//counterpart of a node in Graph1.
//		Map<String, String> vid1tovid2 = new HashMap<String, String>();
//		Map<String, String> vid2tovid1 = new HashMap<String, String>();
//
//		//Substituted vertices are vertices that >are< mapped.
//		//Their distance is 1.0 - string-edit similarity of their labels.
//		double substitutedVertices = 0.0;
//		for (TwoVertices pair: m) {
//			if (((pair.v1) != null) && (pair.v2 != null)){
//				double substitutionDistance;
//				
//				verticesFrom1Used.add(pair.v1);
//				verticesFrom2Used.add(pair.v2);
//			
//				//Score the substitution
//				substitutionDistance = 1.0 - NodeSimilarity.findNodeSimilarity(sg1.getVertexMap().get(pair.v1), 
//						sg2.getVertexMap().get(pair.v2)); 
////				
////				System.out.println(sg1.getVertexMap().get(pair.v1)+ " <> " + sg2.getVertexMap().get(pair.v2)  + " " + substitutionDistance);
//				
//				if (sg1.getVertexMap().get(pair.v1).getType().equals(Type.gateway) 
//						&& sg2.getVertexMap().get(pair.v2).getType().equals(Type.gateway) 
//						&& substitutionDistance <= Settings.MERGE_CONTEXT_THRESHOLD) {
//				}
//				else if (substitutionDistance <= Settings.MERGE_THRESHOLD) {
//				}
////				System.out.println("*** ED: substitutionDistance: "+ 
////						substitutionDistance + " -> " + 
////						sg1.getVertexMap().get(pair.v1) + " <> "+ 
////						sg2.getVertexMap().get(pair.v2));
//				
//				substitutedVertices += substitutionDistance;
//			}else{
//				if (pair.v1 == null){
//					epsilonInsertedVertices += 1.0;
////					System.out.println("*** ED: epsilonInsertedVertices += 1.0");
//				}else{
//					epsilonDeletedVertices += 1.0;				
////					System.out.println("*** ED: epsilonDeletedVertices += 1.0");
//				}
//				epsilonSkippedVertices += 1.0;
////				System.out.println("*** ED: epsilonSkippedVertices += 1.0");
//			}
//			
//			//make each pair \in m also a pair \in vid1tovid2, 
//			//such that in the end vid1tovid2 = m.
//			vid1tovid2.put(pair.v1, pair.v2);
//			vid2tovid1.put(pair.v2, pair.v1);
//		}
//
//		//Substituted edges are edges that are not mapped.
//		//First, create the set of all edges in Graph 2.
//		List<Edge> edgesIn1 = sg1.getEdges();
//		List<Edge> edgesIn2 = sg2.getEdges();
//
//		//Second, create the set of all edges in Graph 1,
//		//but translate it into an edge on vertices from Graph 2.
//		//I.e.: if (v1,v2) \in <Edges from Graph 1> and
//		//v1 is mapped onto v1' and v2 is mapped onto v2', then
//		//(v1',v2') \in <Translated edges from Graph 1>.
//		Set<Edge> translatedEdgesIn1 = new HashSet<Edge>();
//		for (Vertex i: sg1.getVertices()){
//			for (Vertex j: i.getChildren()){
//				if (vid1tovid2.containsKey(i.getID()) 
//						&& vid1tovid2.containsKey(j.getID())){
//					String srcMap = vid1tovid2.get(i.getID());
//					String tgtMap = vid1tovid2.get(j.getID());
//					if ((srcMap != null) && (tgtMap != null)){
//						translatedEdgesIn1.add(new Edge(srcMap, tgtMap));
//					}else{
//						epsilonSkippedEdges += 1.0;
////						System.out.println("*** ED: epsilonSkippedEdges += 1.0");
//					}
//				}
//			}
//		}	
//		
//		if (translatedEdgesIn1.size() > 0)
//			edgesIn2.removeAll(translatedEdgesIn1); //Edges that are skipped remain
//		
//		Set<Edge> translatedEdgesIn2 = new HashSet<Edge>();
//		for (Vertex i: sg2.getVertices()){
//			for (Vertex j: i.getChildren()){
//				if (vid2tovid1.containsKey(i.getID()) 
//						&& vid2tovid1.containsKey(j.getID())){
//					String srcMap = vid2tovid1.get(i.getID());
//					String tgtMap = vid2tovid1.get(j.getID());
//					if ((srcMap != null) && (tgtMap != null)){					
//						translatedEdgesIn2.add(new Edge(srcMap, tgtMap));							
//					}else{
//						epsilonSkippedEdges += 1.0;
////						System.out.println("*** ED: epsilonSkippedEdges += 1.0");
//					}
//				}
//			}
//		}		
//		
////		System.out.println(edgesIn1 + " "+ translatedEdgesIn2);
//
//		if (translatedEdgesIn2.size() > 0) {
//			edgesIn1.removeAll(translatedEdgesIn2); //Edges that are skipped remain
//		}
//		
//		double skippedEdges = 1.0*edgesIn1.size() + 1.0*edgesIn2.size();
//		double skippedVertices = sg1.getVertices().size() + sg2.getVertices().size() - verticesFrom1Used.size() - verticesFrom2Used.size();
//		
//		if (useepsilon){
//			skippedEdges = epsilonSkippedEdges;
//			skippedVertices = epsilonSkippedVertices;
//		}
////		System.out.println("*** ED: skippedEdges: " + skippedEdges +"(" + edgesIn1.size() +", "+ edgesIn2.size()+ ") skippedVertices: " + skippedVertices +
////				" substitutedVertices: "+ substitutedVertices);
//		return computeScore(skippedEdges, skippedVertices, substitutedVertices, 0.0, 0.0);
//	}
	protected double editDistance(BestMapping bestMapping, TwoVertices addedPair){

		//Substituted vertices are vertices that >are< mapped.
		//Their distance is 1.0 - string-edit similarity of their labels.
		double substitutedVertices = bestMapping.substitutedVerticesCost + addedPair.weight;
		
		int addedbyMapping = bestMapping.nrMappedEdges + findNrVerticesByPair(bestMapping, addedPair);
	
		double skippedEdges = sg1.getEdges().size() + sg2.getEdges().size() - (2 * addedbyMapping);
		double skippedVertices = sg1.getVertices().size() + sg2.getVertices().size() - (2 * (bestMapping.size() + 1));
		
		// TODO find the logic if needed
//		if (useepsilon){
//			skippedEdges = epsilonSkippedEdges;
//			skippedVertices = epsilonSkippedVertices;
//		}
//		System.out.println("*** ED: skippedEdges: " + skippedEdges +"(" + edgesIn1.size() +", "+ edgesIn2.size()+ ") skippedVertices: " + skippedVertices +
//				" substitutedVertices: "+ substitutedVertices);
		return computeScore(skippedEdges, skippedVertices, substitutedVertices, 0.0, 0.0);
	}

	private int findNrVerticesByPair(BestMapping bestMapping,
			TwoVertices addedPair) {
		
		int addedbyMapping = 0;
		// find how many matched edges the new mapping will add
		Vertex left = sg1.getVertexMap().get(addedPair.v1);
		Vertex right = sg2.getVertexMap().get(addedPair.v2);
		if (bestMapping.size() > 0) { // best mapping contains some vertices already
			for (Vertex p : left.getParents()) {
				BigInteger mappingRight = bestMapping.mappingRight.get(p.getID());
				// the parent is also mapped and is parent of mapped node
				if (mappingRight != null 
						&& right.getParents().contains(sg2.getVertexMap().get(mappingRight))) {
					addedbyMapping++;
				}
			}
			for (Vertex ch : left.getChildren()) {
				BigInteger mappingRight = bestMapping.mappingRight.get(ch.getID());
				// the parent is also mapped and is parent of mapped node
				if (mappingRight != null 
						&& right.getChildren().contains(sg2.getVertexMap().get(mappingRight))) {
					addedbyMapping++;
				}
			}
		}
		return addedbyMapping;
	}

	public class BestMapping {
		public Set<TwoVertices> mapping = new HashSet<TwoVertices>();
		HashMap<BigInteger, BigInteger> mappingRight = new HashMap<BigInteger, BigInteger>();
		double substitutedVerticesCost = 0;
		int nrMappedEdges = 0;
		
		public void addPair(TwoVertices pair) {
			mappingRight.put(pair.v1, pair.v2);
			mapping.add(pair);
			substitutedVerticesCost += pair.weight;
			nrMappedEdges += findNrVerticesByPair(this, pair);
		}
		
		public Set<TwoVertices> getMapping() {
			return mapping;
		}
		
		public int size() {
			return mapping.size();
		}
	}
}
