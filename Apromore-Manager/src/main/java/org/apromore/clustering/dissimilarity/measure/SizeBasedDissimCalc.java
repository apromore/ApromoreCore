package org.apromore.clustering.dissimilarity.measure;

import nl.tue.tm.is.graph.SimpleGraph;
import org.apromore.clustering.dissimilarity.DissimilarityCalc;

public class SizeBasedDissimCalc implements DissimilarityCalc {
	private double threshold;
	
	double weightSkippedVertex = 1.0;
	double weightSubstitutedVertex = 1.0;
	double weightSkippedEdge = 1.0;
	
	public SizeBasedDissimCalc(double threshold) {
		this.threshold = threshold;
	}
	
	public double compute(SimpleGraph graph1, SimpleGraph graph2) {
		double totalNrVertices = graph1.getVertices().size() + graph2.getVertices().size();
		double totalNrEdges = graph1.getEdges().size() + graph2.getEdges().size();
		
		double vskip = Math.abs(graph1.getVertices().size() - graph2.getVertices().size()) / totalNrVertices;
		double eskip = Math.abs(graph1.getEdges().size() - graph2.getEdges().size()) / totalNrEdges;
		double vsubs = 0.0;
		
		return ((weightSkippedVertex * vskip) + (weightSubstitutedVertex * vsubs) + (weightSkippedEdge * eskip))/(weightSkippedVertex+weightSubstitutedVertex+weightSkippedEdge); 			
	}
	

	public boolean isAboveThreshold(double disim) {
		return disim > threshold;
	}
}
